package com.toby.booking.views;

import java.awt.Color;
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
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.toby.booking.common.Database;
import com.toby.booking.common.Utils;

/**
 * The customers view panel 
 * 
 * @author toby
 */
public class CustomersView extends JPanel implements ActionListener, MouseListener{
	private static final long serialVersionUID = 1L;

	/** List and scroll pane used to display all the customers */
	private JList<String> customerList;
	private DefaultListModel<String> customerListModel;
	private JScrollPane customerListScrollPane;
	
	/** All the label widgets */
	private JLabel firstNameLabel;
	private JLabel lastNameLabel;
	private JLabel contactNumberLabel;
	private JLabel emailAddressLabel;
	
	/** All the text fields widgets */
	private JTextField firstNameField;
	private JTextField lastNameField;
	private JTextField contactNumberField;
	private JTextField emailAddressField;
	
	/** All the button widgets */
	private JButton saveButton;
	private JButton updateButton;
	private JButton deleteButton;
	
	private Database database;
	
	/**
	 * Main constructor for the panel
	 * 
	 * @param database the database object parsed from the main screen 
	 */
	public CustomersView(Database database){
		this.database = database;

		/** Sets up the panel information */
		this.setLocation(0, 0);
		this.setSize(new Dimension(1018, 680));
		this.setOpaque(true);
		this.setLayout(null);
		
		/** Runs basic init methods */
		initialize();
		createWidgets();
		addWidgets();
	}

	private void initialize() {
		customerListModel = new DefaultListModel<>();
		
		loadCustomers();
	}
	
	/**
	 * Loads all the customers from the databases 
	 */
	private void loadCustomers(){
		customerListModel.clear();
		try {
			PreparedStatement statement = database.connection.prepareStatement("select * from tableCustomers");
			ResultSet set = statement.executeQuery();
			while(set.next()){
				String firstName = set.getString("Customer_FirstName");
				String lastName = set.getString("Customer_LastName");
				
				/** Adds the first name and last name as one element in the pane */
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
		
		firstNameLabel = new JLabel("First Name: ");
		firstNameLabel.setFont(Utils.FONT_LARGE);
		firstNameLabel.setSize(107, 42);
		firstNameLabel.setLocation(392, 65);

		firstNameField = new JTextField("First Name..");
		firstNameField.setFont(Utils.FONT_MEDIUM);
		firstNameField.setForeground(Color.GRAY);
		firstNameField.setSize(276, 38);
		firstNameField.setLocation(505, 70);
		
		lastNameLabel = new JLabel("Last Name: ");
		lastNameLabel.setFont(Utils.FONT_LARGE);
		lastNameLabel.setSize(101, 42);
		lastNameLabel.setLocation(392, 125);

		lastNameField = new JTextField("Last Name..");
		lastNameField.setFont(Utils.FONT_MEDIUM);
		lastNameField.setForeground(Color.GRAY);
		lastNameField.setSize(276, 38);
		lastNameField.setLocation(505, 130);
		
		contactNumberLabel = new JLabel("Contact Number: ");
		contactNumberLabel.setFont(Utils.FONT_LARGE);
		contactNumberLabel.setSize(147, 42);
		contactNumberLabel.setLocation(345, 185);
		
		contactNumberField = new JTextField("Contact Number..");
		contactNumberField.setFont(Utils.FONT_MEDIUM);
		contactNumberField.setForeground(Color.GRAY);
		contactNumberField.setSize(276, 38);
		contactNumberField.setLocation(505, 190);
		
		emailAddressLabel = new JLabel("Email Address: ");
		emailAddressLabel.setFont(Utils.FONT_LARGE);
		emailAddressLabel.setSize(134, 42);
		emailAddressLabel.setLocation(358, 245);
		
		emailAddressField = new JTextField("Email Address..");
		emailAddressField.setFont(Utils.FONT_MEDIUM);
		emailAddressField.setForeground(Color.GRAY);
		emailAddressField.setSize(276, 38);
		emailAddressField.setLocation(505, 250);
		
		saveButton = new JButton("Save");
		saveButton.setFont(Utils.FONT_LARGE);
		saveButton.addActionListener(this);
		saveButton.setBounds(355, 340, 116, 38);

		updateButton = new JButton("Update");
		updateButton.setFont(Utils.FONT_LARGE);
		updateButton.addActionListener(this);
		updateButton.setBounds(525, 340, 116, 38);
		
		deleteButton = new JButton("Delete");
		deleteButton.setFont(Utils.FONT_LARGE);
		deleteButton.addActionListener(this);
		deleteButton.setBounds(695, 340, 116, 38);
	}

	private void addWidgets() {
		this.add(customerListScrollPane);
		this.add(firstNameLabel);
		this.add(lastNameLabel);
		this.add(contactNumberLabel);
		this.add(emailAddressLabel);
		this.add(firstNameField);
		this.add(lastNameField);
		this.add(contactNumberField);
		this.add(emailAddressField);
		this.add(saveButton);
		this.add(updateButton);
		this.add(deleteButton);
	}

	/**
	 * Handles all the widgets actions 
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == saveButton){
			createCustomer();
		}
		if(e.getSource() == updateButton){
			updateCustomer();
		}
		if(e.getSource() == deleteButton){
			deleteCustomer();
		}
	}

	/**
	 * Creates a new customer to the database 
	 */
	private void createCustomer(){
		try {
			PreparedStatement statementInsert = database.connection.prepareStatement("insert into tableCustomers(Customer_FirstName, Customer_LastName, Customer_Email, Customer_ContactNumber ) values (?, ?, ?, ?)");
			statementInsert.setString(1, firstNameField.getText());
			statementInsert.setString(2, lastNameField.getText());
			statementInsert.setString(3, emailAddressField.getText());
			statementInsert.setString(4, contactNumberField.getText());
			statementInsert.executeUpdate();
			statementInsert.close();
			
			loadCustomers();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Updates a selected customers information 
	 */
	private void updateCustomer(){
		String selected = customerList.getSelectedValue();
		String oldLastName = "", oldFirstName = "";
		if(selected.split("\\w+").length>1){
			oldLastName = selected.substring(selected.lastIndexOf(" ")+1);
			oldFirstName = selected.substring(0, selected.lastIndexOf(" "));
		}
		try{
			PreparedStatement statementUpdate = database.connection.prepareStatement("update tableCustomers set Customer_FirstName = ?, Customer_LastName = ?, Customer_Email = ?, Customer_ContactNumber = ? where Customer_FirstName = ? and Customer_LastName = ?");
			statementUpdate.setString(1, firstNameField.getText());
			statementUpdate.setString(2, lastNameField.getText());
			statementUpdate.setString(3, emailAddressField.getText());
			statementUpdate.setString(4, contactNumberField.getText());
			statementUpdate.setString(5, oldFirstName);
			statementUpdate.setString(6, oldLastName);
			statementUpdate.executeUpdate();
			statementUpdate.close();
			
			loadCustomers();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Deletes a customer from the database
	 */
	private void deleteCustomer() {
		String selected = customerList.getSelectedValue();
		String lastName = "", firstName = "";
		if(selected.split("\\w+").length>1){
			lastName = selected.substring(selected.lastIndexOf(" ")+1);
			firstName = selected.substring(0, selected.lastIndexOf(" "));
		}
		try {
			PreparedStatement statementDeleteCustomers = database.connection.prepareStatement("delete from tableCustomers where Customer_FirstName = ? and Customer_LastName = ?");
			statementDeleteCustomers.setString(1, firstName);
			statementDeleteCustomers.setString(2, lastName);
			statementDeleteCustomers.executeUpdate();
			statementDeleteCustomers.close();

			loadCustomers();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}

	/**
	 * If a customers name has been pressed in the pane
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		if(customerListModel.isEmpty()){
			
		}else{
			String selected = customerList.getSelectedValue();
			String lastName = "", firstName = "", contact="", email = "";
			if(selected.split("\\w+").length>1){
				lastName = selected.substring(selected.lastIndexOf(" ")+1);
				firstName = selected.substring(0, selected.lastIndexOf(" "));
			}
			firstNameField.setText(firstName);
			lastNameField.setText(lastName);
			
			try {
				PreparedStatement statement = database.connection.prepareStatement("select * from tableCustomers where Customer_FirstName = ? and Customer_LastName = ?");
				statement.setString(1, firstName);
				statement.setString(2, lastName);
				ResultSet set = statement.executeQuery();
				while(set.next()){
					contact = set.getString("Customer_ContactNumber");
					email = set.getString("Customer_Email");
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			
			contactNumberField.setText(contact);
			emailAddressField.setText(email);
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
