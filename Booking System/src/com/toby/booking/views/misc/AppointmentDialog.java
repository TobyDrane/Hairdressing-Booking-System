package com.toby.booking.views.misc;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.toby.booking.common.Database;
import com.toby.booking.common.Utils;

import javax.swing.JButton;
import java.awt.BorderLayout;

/**
 * The dialog that is loaded when the client wants to view a appointment 
 * 
 * @author toby
 *
 */
public class AppointmentDialog extends JDialog implements ActionListener{
	private static final long serialVersionUID = 1L;
	
	/** The panel for the dialog */
	private JPanel panel;
	
	/** The database object used by the dialog*/
	private Database database;
	
	/** All the widgets used by the dialog */
	private JLabel customerLabel;
	private JLabel serviceLabel;
	private JTextArea infoTextArea;
	private JButton buttonDelete;
	
	public JButton rebookButton;
	
	/** Other random variales needed by the dialog */
	private String customerName;
	private String cutType;
	private Date time;
	private LocalDate date;
	private int price;
	private int appointmentID;
	private String infoTextAreaString;
	
	/** 
	 * The main constructor for the dialog 
	 * 
	 * @param frame the JFrame of the main screen
	 * @param database the object of the database
	 */
	public AppointmentDialog(JFrame frame, Database database){
		super(frame, "Appointment", false);
		
		/** Sets the database object of this class to the one paresed */
		this.database = database;
		
		panel = new JPanel();
		
		/** Runs mandatory methods to set up the dialog */
		initialize();
		addWidgets();
		
		panel.setLayout(null);
		
		getContentPane().add(panel);
		
		/** Customises the dialog */
		this.setSize(291, 239);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
	}
	
	/**
	 * Calendar view sends in key information about the apppointment pressed 
	 * 
	 * @param customerName the name of the customer
	 * @param cutType what service they are having
	 * @param time the time of the appointment 
	 * @param date the date of the appointment
	 * @param price how much the appointment is going to cost
	 * @param appointmentID what the appointmentID of it is 
	 */
	public void sendData(String customerName, String cutType, Date time, LocalDate date, int price, int appointmentID){
		this.customerName = customerName;
		this.cutType = cutType;
		this.time = time;
		this.date = date;
		this.price = price;
		this.appointmentID = appointmentID;
		
		/** Sets the info text area to these basic values */
		infoTextAreaString = "Date: "+date+"\nTime: "+Utils.SIMPLE_DATE_FORMAT_JUST_TIME.format(time)+"\nPrice: "+price;
	}
	
	/**
	 * Updates the appointment details 
	 */
	public void updateInfo(){
		customerLabel.setText("Customer: " + customerName);
		serviceLabel.setText("Service: " + cutType);
		infoTextArea.setText(infoTextAreaString);
	}
	
	/**
	 * Sets up and formats all the main widgets 
	 */
	private void initialize() {
		customerLabel = new JLabel();
		customerLabel.setFont(new Font("Sathu", Font.TRUETYPE_FONT, 16));
		customerLabel.setLocation(15, 15);
		customerLabel.setSize(221, 25);
		
		serviceLabel = new JLabel();
		serviceLabel.setFont(new Font("Sathu", Font.TRUETYPE_FONT, 16));
		serviceLabel.setLocation(15, 50);
		serviceLabel.setSize(221, 25);
		
		infoTextArea = new JTextArea();
		infoTextArea.setBounds(10, 86, 270, 63);
		
		buttonDelete = new JButton("Delete");
		buttonDelete.setFont(new Font("Dialog", Font.PLAIN, 16));
		buttonDelete.setBounds(20, 165, 89, 23);
		buttonDelete.addActionListener(this);
		
		rebookButton = new JButton("Rebook");
		rebookButton.setFont(new Font("Sathu", Font.TRUETYPE_FONT, 16));
		rebookButton.setLocation(180, 165);
		rebookButton.setSize(89, 23);
	}
	
	/** 
	 * Adds the widgets to the panel 
	 */
	private void addWidgets() {
		panel.add(customerLabel);
		panel.add(serviceLabel);
		panel.add(infoTextArea);
		panel.add(buttonDelete);
		panel.add(rebookButton);
	}

	/** 
	 * Handles all the widgets actions
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		/** If the delete button is pressed */
		if(e.getSource() == buttonDelete){
			try {
				/** Delete the appointment from the database by running a query */
				PreparedStatement statementDelete = database.connection.prepareStatement("delete from tableAppointments where Appointment_ID = ?");
				statementDelete.setInt(1, appointmentID);
				statementDelete.executeUpdate();
				this.dispose();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
}
