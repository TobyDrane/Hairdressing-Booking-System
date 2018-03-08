package com.toby.booking.views.misc;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

import com.toby.booking.common.Database;
import com.toby.booking.common.Utils;

/**
 * The dialog that is displayed when the user wants to create a new appointment
 * 
 * @author Toby
 */
public class AddAppointmentDialog extends JDialog implements ActionListener{
	private static final long serialVersionUID = 1L;
	
	/** The main panel for the dialog */
	private JPanel panel;
	private Database database;
	
	/** Widgets for the dialog */
	private JLabel customerLabel;
	private JLabel serviceLabel;
	private JComboBox customerComboBox;
	private JComboBox serviceComboBox;
	private JRadioButton sendReminder;
	public JButton buttonConfirm;
	private JButton buttonQuit;
	private JTextArea infoTextArea;
	
	/** Basic random variables */
	private Date time;
	private LocalDate date;
	private String timeString;
	private String dateString;
	private String infoTextAreaString;
	
	/** Customer details normally retrived from databases */
	String customerFirstName = "";
	String customerLastName = "";
	String customerContactNumber = "";
	String customerEmail = "";
	String servicePrice="";
	
	/**
	 * The main constructor for dialog, takes in some key objects 
	 * 
	 * @param frame the JFrame of the main screen
	 * @param database the object of the database class
	 */
	public AddAppointmentDialog(JFrame frame, Database database) {
		super(frame, "Add Appointment", false);
		
		/** Sets the database object of this class to the one parsed */
		this.database = database;

		panel = new JPanel();

		/** Runs mandatory methods to set up dialog */
		initialize();
		addWidgets();

		panel.setLayout(null);

		getContentPane().add(panel);
		
		/** Customises the dialog */
		this.setSize(296, 318);
		this.setResizable(false);
		this.setLocationRelativeTo(frame);
	}

	/**
	 * Calendar View sends in key information about the button pressed 
	 * 
	 * @param time the time at which the button was pressed
	 * @param date the date at which the button was pressed
	 */
	public void sendData(Date time, LocalDate date) {
		this.time = time;
		this.date = date;
		timeString = Utils.SIMPLE_DATE_FORMAT_JUST_TIME.format(time).toString();
		dateString = date.toString();
	}

	/**
	 * Sets up and formats all the main widgets
	 */
	private void initialize() {
		customerLabel = new JLabel("Customer:");
		customerLabel.setFont(new Font("Sathu", Font.TRUETYPE_FONT, 16));
		customerLabel.setLocation(15, 15);
		customerLabel.setSize(75, 25);

		customerComboBox = new JComboBox(database.getCustomers().toArray());
		customerComboBox.setLocation(80 + 30, 15);
		customerComboBox.setSize(150, 25);
		customerComboBox.addActionListener(this);

		serviceLabel = new JLabel("Cut Type:");
		serviceLabel.setFont(new Font("Sathu", Font.TRUETYPE_FONT, 16));
		serviceLabel.setLocation(15, 50);
		serviceLabel.setSize(70, 25);

		serviceComboBox = new JComboBox(database.getServices().toArray());
		serviceComboBox.setLocation(80 + 30, 50);
		serviceComboBox.setSize(150, 25);
		serviceComboBox.addActionListener(this);

		sendReminder = new JRadioButton("Send out Reminder");
		sendReminder.setFont(new Font("Dialog", Font.PLAIN, 16));
		sendReminder.setLocation(15, 88);
		sendReminder.setSize(183, 30);

		buttonConfirm = new JButton("Confirm");
		buttonConfirm.setFont(new Font("Dialog", Font.PLAIN, 16));
		buttonConfirm.setBounds(10, 255, 87, 23);

		buttonQuit = new JButton("Quit");
		buttonQuit.setFont(new Font("Dialog", Font.PLAIN, 16));
		buttonQuit.setBounds(187, 255, 89, 23);
		buttonQuit.addActionListener(this);
				
		infoTextArea = new JTextArea();
		infoTextArea.setFont(Utils.FONT_MEDIUM);
		infoTextArea.setBounds(10, 121, 266, 123);
	}

	/** Adds the widgets to the panel */
	private void addWidgets() {
		panel.add(customerLabel);
		panel.add(customerComboBox);
		panel.add(serviceLabel);
		panel.add(serviceComboBox);
		panel.add(sendReminder);
		panel.add(buttonConfirm);
		panel.add(buttonQuit);
		panel.add(infoTextArea);
	}
	
	/** Removes all the elements from the combo box and re-adds them */
	public void refreshComboBox(){
		customerComboBox.removeAllItems();
		for(String s : database.getCustomers()){
			customerComboBox.addItem(s);
		}
	}
	
	/** If the user has changed the selected customer or service update the info displayed */
	public void updateInfoTextArea(){
		if(customerComboBox.getItemCount() > 0){
			String name = customerComboBox.getSelectedItem().toString();
			
			/** Splits the name into first and last name */
			if(name.split("\\w+").length>1){
				customerLastName = name.substring(name.lastIndexOf(" ")+1);
				customerFirstName = name.substring(0, name.lastIndexOf(" "));
			}else customerFirstName = name;
			
			try {
				/** Retrieve customer contact number and email based on there name */
				PreparedStatement statement = database.connection.prepareStatement("select Customer_ContactNumber, Customer_Email from tableCustomers where Customer_FirstName=?");
				statement.setString(1, customerFirstName);
				ResultSet set = statement.executeQuery();
				while(set.next()){
					customerContactNumber = set.getString("Customer_ContactNumber");
					customerEmail = set.getString("Customer_Email");
				}
				statement = database.connection.prepareStatement("select Cut_Price from tableServices where Cut_Type=?");
				statement.setString(1, serviceComboBox.getSelectedItem().toString());
				set = statement.executeQuery();
				while(set.next()) servicePrice = "£"+String.valueOf(set.getInt("Cut_Price"));
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			/** Update the information using the new values */
			infoTextAreaString = "Name: "+name+"\nContact Number: "+customerContactNumber+"\nEmail: "+customerEmail+"\nPrice: "+servicePrice+"\nDate: "+
			dateString+"\nTime: "+timeString;
			infoTextArea.setText(infoTextAreaString);
		}
	}
	
	/**
	 * This method is called when the confirm button
	 */
	public void createAppointment(){
		if(sendReminder.isSelected()){
			/** Sends out email if the user selected that */
			Utils.sendEmail(customerFirstName, customerEmail, dateString, timeString, serviceComboBox.getSelectedItem().toString());
		}
		
		/** Saves the appointment to the database */
		database.createNewAppointment(dateString, timeString, customerFirstName, serviceComboBox.getSelectedItem().toString());
		this.dispose();
	}
	
	/**
	 * Handles all the widgets interaction for the dialog 
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		/** If they press the quit button close the dialog */
		if(e.getSource() == buttonQuit){
			this.dispose();
		}
		
		/** If they change a customer or service update the info text area s */
		if(e.getSource() == customerComboBox){
			updateInfoTextArea();
		}
		if(e.getSource() == serviceComboBox){
			updateInfoTextArea();
		}
	}
}
