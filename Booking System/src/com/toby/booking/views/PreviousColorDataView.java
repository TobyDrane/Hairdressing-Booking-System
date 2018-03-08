package com.toby.booking.views;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.toby.booking.common.Database;
import com.toby.booking.common.Utils;

/**
 * The previous color data view panel 
 * 
 * @author toby
 */
public class PreviousColorDataView extends JPanel implements ActionListener, MouseListener{
	private static final long serialVersionUID = 1L;

	/** List and scroll pane used to display all the customers */
	private JList<String> customerList;
	private DefaultListModel<String> customerListModel;
	private JScrollPane customerListScrollPane;
	
	/** The widgets used for the panel */
	private JTextArea colorTextArea;
	private JScrollPane colorScrollPane;
	private JButton saveButton;
	
	private Database database;
	
	/**
	 * Main constructor for the panel 
	 * 
	 * @param database the database object parsed from the main screen 
	 */
	public PreviousColorDataView(Database database){
		this.setLocation(0, 0);
		this.setSize(new Dimension(1018, 680));
		this.setOpaque(true);
		this.setLayout(null);
		this.database = database;
		
		initialize();
		createWidgets();
		addWidgets();
	}

	private void initialize() {
		customerListModel = new DefaultListModel<>();
		
		loadCustomers();
	}

	/**
	 * Loads all the customers from the database
	 */
	public void loadCustomers(){
		customerListModel.clear();
		try {
			PreparedStatement statement = database.connection.prepareStatement("select * from tableCustomers");
			ResultSet set = statement.executeQuery();
			while(set.next()){
				String firstName = set.getString("Customer_FirstName");
				String lastName = set.getString("Customer_LastName");
				
				customerListModel.addElement(firstName + " " + lastName);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void createWidgets() {
		customerList = new JList<>(customerListModel);
		customerList.setFont(Utils.FONT_LARGE);
		customerList.addMouseListener(this);
		customerList.setLocation(0, 0);
		customerList.setSize(100, 640);
		
		customerListScrollPane = new JScrollPane(customerList);
		customerListScrollPane.setLocation(10, 11);
		customerListScrollPane.setSize(290, 658);
		
		colorTextArea = new JTextArea();
		colorTextArea.setFont(Utils.FONT_MEDIUM);
		colorTextArea.setBounds(355, 17, 606, 180);
	
		colorScrollPane = new JScrollPane(colorTextArea);
		colorScrollPane.setSize(595, 255);
		colorScrollPane.setLocation(364, 35);
		
		saveButton = new JButton("Save");
		saveButton.addActionListener(this);
		saveButton.setLocation(364, 312);
		saveButton.setSize(96, 33);
	}

	private void addWidgets() {
		this.add(customerListScrollPane);
		this.add(colorScrollPane);
		this.add(saveButton);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == saveButton){
			updateDatabase();
		}
	}

	/** Updates the previous color data to the database */
	private void updateDatabase(){
		try{
			PreparedStatement statementUpdate = database.connection.prepareStatement("insert into tableColorData (Customer_ID, Previous_ColorData) values (?, ?) on duplicate key update Previous_ColorData = ?");
			statementUpdate.setInt(1, getSelectedCustomerID());
			statementUpdate.setString(2, colorTextArea.getText());
			statementUpdate.setString(3, colorTextArea.getText());
			statementUpdate.executeUpdate();
			statementUpdate.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Retrieves the customerID based on there name 
	 * 
	 * @return the customerID
	 */
	private int getSelectedCustomerID(){
		String selected = customerList.getSelectedValue();
		String lastName = "", firstName = "";
		int customerID = 0;
		if(selected.split("\\w+").length>1){
			lastName = selected.substring(selected.lastIndexOf(" ")+1);
			firstName = selected.substring(0, selected.lastIndexOf(" "));
		}
		
		try{
			PreparedStatement statementGetCustomerID = database.connection.prepareStatement("select * from tableCustomers where Customer_FirstName = ? and Customer_LastName = ?");
			statementGetCustomerID.setString(1, firstName);
			statementGetCustomerID.setString(2, lastName);
			ResultSet setGetCustomerID = statementGetCustomerID.executeQuery();
			while(setGetCustomerID.next()){
				customerID = setGetCustomerID.getInt("Customer_ID");
			}
			statementGetCustomerID.close();
		}catch(SQLException e){
			e.printStackTrace();
		}

		return customerID;
	}
	
	/** Handles if a customer has been selected in the pane */
	@Override
	public void mouseClicked(MouseEvent e) {
		try {
			colorTextArea.setText("");
			PreparedStatement statementGetData = database.connection.prepareStatement("select * from tableColorData where Customer_ID = ?");
			statementGetData.setInt(1, getSelectedCustomerID());
			ResultSet setGetData = statementGetData.executeQuery();
			while(setGetData.next()){
				String text = setGetData.getString("Previous_ColorData");
				colorTextArea.setText(setGetData.getString("Previous_ColorData"));
			}
			statementGetData.close();
		} catch(SQLException e1){
			e1.printStackTrace();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
}
