package com.toby.booking.views;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.toby.booking.common.Database;
import com.toby.booking.common.Utils;

/**
 * The patch tests view panel
 * 
 * @author toby
 */
public class PatchTestsView extends JPanel implements ActionListener, MouseListener {
	private static final long serialVersionUID = 1L;

	/** List and scroll pane used to display all the customers */
	private JList<String> customerList;
	private DefaultListModel<String> customerListModel;
	private JScrollPane customerListScrollPane;
	
	private Database database;
	
	/** All the button widgets */
	private JButton loadButton;
	private JButton saveButton;
	private JButton deleteButton;
	
	/** Other variables needed */
	private String imagePath;
	private BufferedImage image;
	
	/**
	 * Main constructor for the panel 
	 * 
	 * @param database the database object parsed from the main screen
	 */
	public PatchTestsView(Database database){
		
		/** Sets up the panel information */
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
		imagePath = "";
		
		loadCustomers();
	}
	
	/** 
	 * Loads all the customers from the database 
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
	
		loadButton = new JButton("Load");
		loadButton.setSize(100, 34);
		loadButton.setLocation(785, 42);
		loadButton.setFont(Utils.FONT_MEDIUM);
		loadButton.addActionListener(this);
				
		saveButton = new JButton("Save");
		saveButton.setSize(100, 40);
		saveButton.setLocation(372, 485);
		saveButton.setFont(Utils.FONT_MEDIUM);
		saveButton.addActionListener(this);
		
		deleteButton = new JButton("Delete");
		deleteButton.setSize(100, 40);
		deleteButton.setLocation(662, 485);
		deleteButton.setFont(Utils.FONT_MEDIUM);
		deleteButton.addActionListener(this);
	}

	private void addWidgets() {
		this.add(customerListScrollPane);
		this.add(loadButton);
		this.add(saveButton);
		this.add(deleteButton);
	}

	/** Reloads the patch tests image */
	private void reloadImage() {
		image = null;
		imagePath = "";
		try{
			/** Retrieve the image path from database */
			PreparedStatement stmt = database.connection.prepareStatement("select * from tablePatchTests where Customer_ID = ?");
			stmt.setInt(1, getSelectedCustomerID());
			ResultSet set = stmt.executeQuery();
			while(set.next()){
				imagePath = set.getString("Patch_Test");
				image = ImageIO.read(new File(imagePath));
			}
			
			repaint();
		}catch(IOException e){
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/** Saves a new patch test */
	private void saveImage(){
		if(!imagePath.equals("")){
			try {
				PreparedStatement saveStatement = database.connection.prepareStatement("insert into tablePatchTests (Customer_ID, Patch_Test) values(?, ?) on duplicate key update Patch_Test = ?");
				saveStatement.setInt(1, getSelectedCustomerID());
				saveStatement.setString(2, imagePath);
				saveStatement.setString(3, imagePath);
				saveStatement.executeUpdate();
				saveStatement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			reloadImage();
		}
	}
	
	/** Deletes a patch test */
	private void deleteImage(){
		if(!imagePath.equals("")){
			try {
				PreparedStatement deleteStatement = database.connection.prepareStatement("delete from tablePatchTests where Customer_ID = ?");
				deleteStatement.setInt(1, getSelectedCustomerID());
				deleteStatement.executeUpdate();
				deleteStatement.close();
				image = null;
				repaint();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Retrieves the customerID based on there name
	 * 
	 * @return the cusotmerID 
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
	
	/** Handles all the widgets actions */
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == loadButton){
			if(customerList.isSelectionEmpty()){
				JOptionPane.showMessageDialog(this, "You need to select a Customer!", "Warning", JOptionPane.INFORMATION_MESSAGE);
			}else{
				/** Loads the image picker */
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new java.io.File(Utils.DIRECTORY_INITIAL));
				fileChooser.setDialogTitle("Image Chooser");
				fileChooser.setFileSelectionMode(JFileChooser.APPROVE_OPTION);
				if(fileChooser.showOpenDialog(loadButton) == JFileChooser.APPROVE_OPTION){
					imagePath = fileChooser.getSelectedFile().getAbsolutePath(); 
				}
			}
		}
		
		if(e.getSource() == saveButton){
			saveImage();
		}
		if(e.getSource() == deleteButton){
			deleteImage();
		}
	}

	/** Draws the images to the screen */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if(image != null){
			g.drawImage(image, 372, 42, 390, 420, this);
		}else{
			g.setColor(Utils.IMAGE_PREVIEW_COLOR);
			g.fillRect(372, 42, 390, 420);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		reloadImage();
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
