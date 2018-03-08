package com.toby.booking.views;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

import com.toby.booking.common.Database;
import com.toby.booking.common.RoundedRectangle;
import com.toby.booking.common.Utils;
import com.toby.booking.views.misc.AddAppointmentDialog;
import com.toby.booking.views.misc.AppointmentDialog;
import com.toby.booking.views.misc.NavigationBar;

/**
 * The main class for the system. This acts as the home screen for the system
 * All other classes are called from here, acts as a handler for the system
 * 
 * @author Toby
 *
 */
public class CalendarView extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;
	
	/** All the JPanel objects */
	private JPanel panelNavBar;
	private JPanel panelMain;
	private JPanel panelCalendar;
	private JPanel panelDateSelectionBar;
	private JPanel panelCalendarView;
	private RectPanel rectPanel;
	
	/** The variables used in the date selection bar */
	private JLabel dateLabel;
	private JButton dateButtonPrev;
	private JButton dateButtonNext;
	private JButton refreshButton;
	
	/** Objects of classes made in the system */
	private NavigationBar navBar;
	private CustomersView customers;
	private PreviousColorDataView previousColorData;
	private PatchTestsView patchTests;
	public Database database;

	/** A array list containing all the dates in the system */
	public List<LocalDate> dates = new ArrayList<LocalDate>();
	/** A array list containing all the dates that the Monday of the week lie on */
	private List<LocalDate> weeksStart = new ArrayList<LocalDate>();
	
	private int currentWeek = 0;
	private Date time;
	
	/** 2D JButton array */
	private JButton[][] calendarViewButtons;

	/** Objects of all the dilogs used in the system */
	private AddAppointmentDialog addAppointmentDialog;
	private AppointmentDialog appointmentDialog;
	
	private String todaysDate;
	
	/**
	 * Main constructor for the class but as well as the system
	 */
	public CalendarView(){
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e){
			e.printStackTrace();
		}

		initialize(); 
		createWidgets();
		createNavBar();
		addWidgets();

		createCalendar();
		
		sendOutReminders();
		
		/** Code used to create the window */
		setTitle("Booking System");
		setSize(1024, 768);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		getContentPane().setLayout(null);
		
		getContentPane().add(panelNavBar);	
		getContentPane().add(panelMain);
		
		setVisible(true);
	}
	
	/**
	 * Initialises the main JPanels and objects in the system
	 */
	private void initialize(){
		database = new Database();
		navBar = new NavigationBar();
		customers = new CustomersView(database);
		previousColorData = new PreviousColorDataView(database);
		patchTests = new PatchTestsView(database);

		panelNavBar = new JPanel();
		panelNavBar.setBounds(0, 0, 1018, 48);
		panelNavBar.setLayout(new GridLayout(1, 0, 0, 0));

		panelMain = new JPanel();
		panelMain.setBounds(0, 48, 1018, 680);
		panelMain.setLayout(null);
		
		panelCalendar = new JPanel();
		panelCalendar.setBounds(0, 0, 1018, 680);
		panelMain.add(panelCalendar);
		panelCalendar.setLayout(null);
		
		panelDateSelectionBar = new JPanel();
		panelDateSelectionBar.setBounds(0, 0, 1018, 64);
		panelDateSelectionBar.setOpaque(true);
		panelDateSelectionBar.setBackground(new Color(128, 203, 196));
		panelDateSelectionBar.setLayout(null);
		panelCalendar.add(panelDateSelectionBar);
		
		panelCalendarView = new JPanel();
		panelCalendarView.setBounds(0, 64, 1018, 616);
		GridLayout g = new GridLayout(Utils.NUM_ROWS, 0, 0, 0);
		panelCalendarView.setLayout(g); 
		panelCalendarView.setOpaque(true);
		panelCalendar.add(panelCalendarView);
		
		rectPanel = new RectPanel();
		rectPanel.setBounds(0, 64+48, 1018, 616);
		rectPanel.setOpaque(false);
		rectPanel.setLayout(null);
		getContentPane().add(rectPanel);
		
		addAppointmentDialog = new AddAppointmentDialog(this, database);
		addAppointmentDialog.buttonConfirm.addActionListener(this);

		appointmentDialog = new AppointmentDialog(this, database);
		appointmentDialog.rebookButton.addActionListener(this);
		
		try {
			time = Utils.SIMPLE_DATE_FORMAT_JUST_TIME.parse("09:00:00");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		todaysDate = Utils.SIMPLE_DATE_FORMAT.format(new Date());
		
		createDates();
	}
	
	/**
	 * Sets up the two dates array lists 
	 */
	private void createDates(){
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		LocalDate startDate = LocalDate.parse(Utils.START_DATE, formatter);
		LocalDate endDate = LocalDate.parse(Utils.END_DATE, formatter);
		
		/** Gets every date from the start to end dates and saves them to the array */
		for(LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)){
			dates.add(date);
		}
		
		/** Gets every 7 days from the above array list and saves that as the start of week */
		for(int i = 0;i < dates.size();i+=7){
			LocalDate lDate = (LocalDate) dates.get(i);
			weeksStart.add(lDate);
		}
	}
	
	private void createWidgets(){
		navBar.createWidgets();
		
		dateLabel = new JLabel("Test");
		dateLabel.setFont(new Font("Sathu", Font.TRUETYPE_FONT, 18));
		dateLabel.setForeground(Color.WHITE);
		dateLabel.setBounds(430, 7, 150, 50);
		
		dateButtonPrev = new JButton();
		dateButtonPrev.setBounds(375, 9, 50, 50);
		dateButtonPrev.addActionListener(this);
		dateButtonPrev.setBorderPainted(false);
		dateButtonPrev.setBorder(null);
		dateButtonPrev.setMargin(new Insets(0, 0, 0, 0));
		dateButtonPrev.setContentAreaFilled(false);
		dateButtonPrev.setIcon(new ImageIcon("resources/icons/left.png"));
		
		dateButtonNext = new JButton();
		dateButtonNext.setBounds(570, 9, 50, 50);
		dateButtonNext.addActionListener(this);
		dateButtonNext.setBorderPainted(false);
		dateButtonNext.setBorder(null);
		dateButtonNext.setMargin(new Insets(0, 0, 0, 0));
		dateButtonNext.setContentAreaFilled(false);
		dateButtonNext.setIcon(new ImageIcon("resources/icons/right.png"));
		
		refreshButton = new JButton();
		refreshButton.setBounds(956, 18, 35, 35);
		refreshButton.setBorderPainted(false);
		refreshButton.setBorder(null);
		refreshButton.setMargin(new Insets(0, 0, 0, 0));
		refreshButton.setContentAreaFilled(false);
		refreshButton.addActionListener(this);
		refreshButton.setIcon(new ImageIcon("resources/icons/refresh.png"));
		
		calendarViewButtons = new JButton[Utils.NUM_ROWS][Utils.NUM_COLUMNS];
	}
	
	/**
	 * Creates the object of the nav bar
	 */
	private void createNavBar(){
		panelNavBar.setOpaque(true);
		panelNavBar.setBackground(new Color(77, 182, 172));
		
		/** Handles the nav bar buttons key presses */
		navBar.btnCalendar.addActionListener(this);
		navBar.btnCustomers.addActionListener(this);
		navBar.btnPColorData.addActionListener(this);
		navBar.btnPTests.addActionListener(this);
		
		/** Adds the widgets to the main panelNavBar JPanel */
		panelNavBar.add(navBar.btnCalendar);
		panelNavBar.add(navBar.btnCustomers);
		panelNavBar.add(navBar.btnPColorData);
		panelNavBar.add(navBar.btnPTests);
	}
	
	/**
	 * Adds the created system widgets to there desired JPanels 
	 */
	private void addWidgets(){
		panelDateSelectionBar.add(dateLabel);
		panelDateSelectionBar.add(dateButtonPrev);
		panelDateSelectionBar.add(dateButtonNext);
		panelDateSelectionBar.add(refreshButton);
	}
	
	/**
	 * Creates and formats the date label based on what current week are in 
	 */
	private void createDateLabel(){
		LocalDate dateWeekStart = weeksStart.get(currentWeek);
		
		int year = dateWeekStart.getYear();
		int monthWeekStart = dateWeekStart.getMonthValue();
		int dayWeekStart = dateWeekStart.getDayOfMonth();
		
		/** Converts the months name into a 3 letter recogniser e.g. November = Nov */
		String monthWeekStartString = new DateFormatSymbols().getMonths()[monthWeekStart-1];
		
		/** Gets the index in dates of the starting week date */
		int index = dates.indexOf(dateWeekStart);
		
		/** Adds six to this index so it gets the last date in the week */
		index += 6;
		
		LocalDate weekAheadDate = dates.get(index);
		int weekAheadDateDay = weekAheadDate.getDayOfMonth();
		
		/** Sets the date label text */
		dateLabel.setText(monthWeekStartString.substring(0, 3)+" "+dayWeekStart+" - "+weekAheadDateDay+" "+year);
	}
	
	/**
	 * Creates the main calendar view
	 */
	private void createCalendarView(){
		Calendar c = Calendar.getInstance();
		
		/** Loops through the 2D array of buttons for the desired size */
		for(int i = 0;i < Utils.NUM_ROWS;i++){
			for(int j = 0;j < Utils.NUM_COLUMNS;j++){

				/** Formats the buttons */
				JButton b = new JButton();
				b.setBorder(LineBorder.createBlackLineBorder());
				b.setRolloverEnabled(false);
				b.setBackground(new Color(252, 252, 252));
				b.setContentAreaFilled(false);
				b.setFocusPainted(false);
				b.setOpaque(true);
				b.addActionListener(this);
				
				/** Sets the top row of buttons to display the current dates of the week */
				if(i == 0 && j != 0){
					LocalDate start = weeksStart.get(currentWeek);
					
					/** Gets the index of the start of the week date from the main dates array */
					int index = dates.indexOf(start);
					LocalDate date = dates.get(index + (j - 1));
					String dayOWeek = date.getDayOfWeek().toString();
					String dateString = dayOWeek.substring(0, 3)+" "+date.toString().substring(8,10)+"/"+date.toString().substring(5, 7);
					b.setText(dateString);
				}
				
				/** Sets the first column to display the time every hour */
				if(i != 0 & j == 0) {
					
					/** As every hour is every 2 buttons it checks to see if the button isnt even */
					if(i % 2 != 0){
						b.setText(new SimpleDateFormat("HH:mm:ss").format(time));
						c.setTime(time);
						c.add(Calendar.HOUR, 1);
						time = c.getTime();
					}
					
				}

				/** Sets the array to the buttons, then adds the buttons to the panel */
				calendarViewButtons[i][j] = b;
				
				/** Adds the buttons to the JPanel */
				panelCalendarView.add(calendarViewButtons[i][j]);
			}
		}
	}
	
	private void createCalendar(){
		createDateLabel();
		createCalendarView();
	}
	
	private void sendOutReminders(){
		try {
			PreparedStatement statement = database.connection.prepareStatement("select * from tableAppointments");
			ResultSet set = statement.executeQuery();
			while(set.next()){
				Date appointmentDate = set.getTimestamp("Date_Time");
				String appointmentDateString = Utils.SIMPLE_DATE_FORMAT.format(appointmentDate);

				Date appointment = Utils.SIMPLE_DATE_FORMAT.parse(appointmentDateString);
				Calendar c = Calendar.getInstance();
				c.setTime(appointment);
				c.add(Calendar.DATE, -7);
				appointment.setTime(c.getTime().getTime());
				appointmentDateString = Utils.SIMPLE_DATE_FORMAT.format(appointment);
				
				if(appointmentDateString.equals(todaysDate)){
					String customerFirstName="";
					String customerEmail="";
					int customerID = set.getInt("Customer_ID");
					int cutID = set.getInt("Cut_ID");
					String service="";
					
					PreparedStatement stmtCustomer = database.connection.prepareStatement("select * from tableCustomers where Customer_ID = ?");
					stmtCustomer.setInt(1, customerID);
					ResultSet setCustomer = stmtCustomer.executeQuery();
					while(setCustomer.next()){
						customerFirstName = setCustomer.getString("Customer_FirstName");
						customerEmail = setCustomer.getString("Customer_Email");
					}
					stmtCustomer.close();
					
					PreparedStatement stmtService = database.connection.prepareStatement("select * from tableServices where Cut_ID = ?");
					stmtService.setInt(1, cutID);
					ResultSet setService = stmtService.executeQuery();
					while(setService.next()){
						service = setService.getString("Cut_Type");
					}
					stmtService.close();
					
					Utils.sendEmail(customerFirstName, customerEmail, appointmentDateString, Utils.SIMPLE_DATE_FORMAT_JUST_TIME.format(appointmentDate), service);
				}
			}
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Updates the entire calendar view
	 */
	private void updateCalendar(){
		
		/** Removes all the widgets from the view (So they can readded )*/
		panelCalendarView.removeAll();
		
		/** Sets the time back to zero so it can run the setup again */
		try {
			time = new SimpleDateFormat("HH:mm:ss").parse("09:00:00");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		/** Readds the widgets to the calendar view */
		createDateLabel();
		createCalendarView();
	}
	
	/**
	 * This handles all the key presses in the Calendar View class
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		
		/** Is the calendar button pressed on the nav bar */
		if(event.getSource() == navBar.btnCalendar){
			panelMain.removeAll();
			rectPanel.removeAll();
			panelMain.add(panelCalendar);
		}
		
		/** Is the customers button pressed on the nav bar */
		if(event.getSource() == navBar.btnCustomers) {
			panelMain.removeAll();
			rectPanel.removeAll();
			panelMain.add(customers);
		}

		/** Is the previous color data button pressed on the nav bar */
		if(event.getSource() == navBar.btnPColorData) {
			panelMain.removeAll();
			rectPanel.removeAll();
			panelMain.add(previousColorData);
		}

		/** Is the patch tests button pressed on the nav bar */
		if(event.getSource() == navBar.btnPTests){
			panelMain.removeAll();
			rectPanel.removeAll();
			panelMain.add(patchTests);
		}
		
		/** In the date label is the next week button pressed */
		if(event.getSource() == dateButtonNext){
			if(currentWeek != weeksStart.size()) {
				/** Increases the current week so it loads the dates at that week */
				currentWeek++;
				
				/** Updates the calendar as dates change */
				updateCalendar();
			}
		}
		
		/** In the date label is the previous week button pressed */
		if(event.getSource() == dateButtonPrev){
			if(currentWeek != 0) {
				/** Decreases the current week as we are going back a week */
				currentWeek--;

				/** Updates the calendar as dates change */
				updateCalendar();
			}
		}
	
		/**
		 * This next block of code checks to see if the calendar view buttons have been pressed
		 * If they have, do they contain a appointment and if they do display the 
		 * AppointmentDialog else display the add Appointment Dialog
		 */
		Date time = null;
		LocalDate date = null;
		
		/** Loops through all the buttons in the 2D array again */
		for(int i = 0;i < Utils.NUM_ROWS;i++){
			for(int j  = 0;j < Utils.NUM_COLUMNS;j++){
				if(event.getSource() == calendarViewButtons[i][j]) {
					
					/** Does allow the user to press the top row and first column, aswell as Thursday and Sunday*/
					if((i != 0) && (j != 0) && (j != 4) && (j != 7)){	
						SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
						try{
							/** Is the button pressed time a hour or half a hour */
							if(i % 2 == 0){
								/** If half a hour retrieve the previous array time and 30 mins */
								time = sdf.parse(calendarViewButtons[i-1][0].getText());
								Calendar c = Calendar.getInstance();
								c.setTime(time);
								
								/** Adds 30 mins */
								c.add(Calendar.MINUTE, 30);
								time = sdf.parse(sdf.format(c.getTime()));
							}else{
								/** Sets time as a whole hour */
								time = sdf.parse(calendarViewButtons[i][0].getText());
							}
						}catch(ParseException e){
							e.printStackTrace();
						}
						
						LocalDate start = weeksStart.get(currentWeek);
						int index = dates.indexOf(start);
						/** If the button pressed is the second column the date is the start of the week date */
						if(j == 1) date = start;
						/**  Gets the date from which index of the 2D arrays of buttons was pressed */
						else date = dates.get(index+(j-1));

						String finalDate = date.toString() + " " + Utils.SIMPLE_DATE_FORMAT_JUST_TIME.format(time).toString();
						PreparedStatement statement;
						try {
							/** Retrieves all the appointments that have dates and times for the selected slot */
							statement = database.connection.prepareStatement("select * from tableAppointments where Date_Time = ?");
							statement.setString(1, finalDate);
							ResultSet set = statement.executeQuery();
							
							/** If there is a appointment isnt any appointments */
							if(!set.next()){
								/** Then load the addAppointmentDialog */
								addAppointmentDialog.refreshComboBox();
								addAppointmentDialog.sendData(time, date);
								addAppointmentDialog.updateInfoTextArea();
								addAppointmentDialog.setVisible(true);
							}else{
								/** Otherwise this means that there must already be a appointment */
								int customerID=0;
								int cutID;
								String cutType="";
								int price=0;
								int appointmentID;
								String customerFirstName="";
								String customerLastName="";
								do{
									/** Retrieve the customerID, cutID and appointmentID from the already booked appointment */
									customerID = set.getInt("Customer_ID");
									cutID = set.getInt("Cut_ID");
									appointmentID = set.getInt("Appointment_ID");
									
									/** Retrieve which service they had from the cutID and gets its price */
									PreparedStatement preparedStatementService = database.connection.prepareStatement("select * from tableServices where Cut_ID = ?");
									preparedStatementService.setInt(1, cutID);
									ResultSet setServices = preparedStatementService.executeQuery();
									while(setServices.next()){
										cutType = setServices.getString("Cut_Type");
										price = setServices.getInt("Cut_Price");
									}
									
									/** Retrieve the customers first name and last name from the customerID */
									PreparedStatement preparedStatementCustomer = database.connection.prepareStatement("select * from tableCustomers where Customer_ID = ?");
									preparedStatementCustomer.setInt(1, customerID);
									ResultSet setCustomer = preparedStatementCustomer.executeQuery();
									while(setCustomer.next()){
										customerFirstName = setCustomer.getString("Customer_FirstName");
										customerLastName = setCustomer.getString("Customer_LastName");
									}
								}while(set.next());
								/** Then load the appointmentDialog and give in the required data */
								appointmentDialog.sendData(customerFirstName+" "+customerLastName, cutType, time, date, price, appointmentID);
								appointmentDialog.updateInfo();
								appointmentDialog.setVisible(true);
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		
		/** If the confirm button has been pressed within the addAppointmentDialog */
		if(event.getSource() == addAppointmentDialog.buttonConfirm){
			addAppointmentDialog.createAppointment();
			
			/** Load the appointments */
			loadAppointments();
		}
		
		/** Used to automatically rebook a appointment */
		if(event.getSource() == appointmentDialog.rebookButton){
			
		}
		
		/** If the refresh button has been pressed load the appointments */
		if(event.getSource() == refreshButton){
			loadAppointments();
		}

		revalidate();
		repaint();
	}

	/**
	 * Loads all the current stored programs, checks to see if it is a hour long
	 * or not and creates a rounded rectangle based on the results, aswell as
	 * rendering this rounded rectangle to the screen 
	 */
	public void loadAppointments(){
		rectPanel.removeAll();
		LocalDate start = weeksStart.get(currentWeek);
		int index = dates.indexOf(start);
		LocalDate end = dates.get(index+6);
		
		int customerID = 0;
		int cutID = 0;
		String customerName = "";
		Date timestampDate;
		String timestampString;	
		String dateString;
		String timeString;
		LocalDate date;
		Date timeDate;
		String cutType="";
		Date timeStampCutLength;
		String cutLengthString;
		Calendar c = Calendar.getInstance();
		
		int buttonX=0;
		int buttonY=0;
		int buttonWidth=0;
		int buttonHeight=0;
		boolean hourAppointment=false;
		
		/** First creates the closed rectangles */
		createClosedRectangles();
		
		try {
			/** Selects all the appointments for the current week */ 
			PreparedStatement preparedStatement = database.connection.prepareStatement("select * from tableAppointments where Date_Time >= ? and Date_Time <= ?");
			preparedStatement.setString(1, start.toString());
			preparedStatement.setString(2, end.toString());
			ResultSet set = preparedStatement.executeQuery();
			while(set.next()){
				timestampDate = set.getTimestamp("Date_Time");
				customerID = set.getInt("Customer_ID");
				cutID = set.getInt("Cut_ID");
				
				timestampString = Utils.SIMPLE_DATE_FORMAT_TIME.format(timestampDate);
				String parts[] = timestampString.split(" ");
				dateString = parts[0];
				timeString = parts[1];
				date = LocalDate.parse(dateString, Utils.DATE_TIME_FORMATTER);
				c.setTime(Utils.SIMPLE_DATE_FORMAT_TIME.parse(timestampString));
				
				String dayOWeek = date.getDayOfWeek().toString();
				String formattedDateString = dayOWeek.substring(0, 3)+" "+date.toString().substring(8,10)+"/"+date.toString().substring(5, 7);
				timeDate = new SimpleDateFormat("HH:mm:ss").parse(timeString);
								
				/** Gets which x position the button pressed is at based on the date */
				for(int j = 1;j < 8;j++){
				 	if(calendarViewButtons[0][j].getText().equals(formattedDateString)){
						buttonX = calendarViewButtons[0][j].getX();
						buttonWidth = calendarViewButtons[0][j].getWidth();
					}
				}
				
				/** Checks how many minutes the time of the appointment has */
				int minutes = c.get(Calendar.MINUTE);
				
				/** 0 minutes the appointment must be on the hour */
				if(minutes == 0){
					for(int i = 1;i < 24;i+=2){
						if(calendarViewButtons[i][0].getText().equals(timeString)){
							buttonY = calendarViewButtons[i][0].getY();
							buttonWidth = calendarViewButtons[i][0].getWidth();
							buttonHeight = calendarViewButtons[i][0].getHeight();
						}
					}
				}else if(minutes == 30){
					/** Else the appointment must be on the half of the hour */
					c.setTime(timeDate);
					c.add(Calendar.MINUTE, 30);
					timeDate = c.getTime();					
					for(int i = 1;i < 24;i+=2){
						if(calendarViewButtons[i][0].getText().equals(new SimpleDateFormat("HH:mm:ss").format(timeDate))){
							i-=1;
							buttonY = calendarViewButtons[i][0].getY();
							buttonWidth = calendarViewButtons[i][0].getWidth();
							buttonHeight = calendarViewButtons[i][0].getHeight();
						}
					}
				}
				
				/** Gets what service the appointment is having */
				PreparedStatement preparedStatementService = database.connection.prepareStatement("select * from tableServices where Cut_ID = ?");
				preparedStatementService.setInt(1, cutID);
				ResultSet setServices = preparedStatementService.executeQuery();
				while(setServices.next()){
					cutType = setServices.getString("Cut_Type");
					
					timeStampCutLength = setServices.getTimestamp("Cut_Length");
					cutLengthString = new SimpleDateFormat("HH:mm:ss").format(timeStampCutLength);
					c.setTime(new SimpleDateFormat("HH:mm:ss").parse(cutLengthString));
					minutes = c.get(Calendar.MINUTE);
					
					/** Checks whether the service lasts for a hour or not, if it contains no minutes then its a hour long */
					if(minutes == 0){
						hourAppointment = true;
					}else{
						hourAppointment = false;
					}
				}
				
				/** Retrieves the first and last name from the customer */
				PreparedStatement preparedStatementCustomer = database.connection.prepareStatement("select * from tableCustomers where Customer_ID = ?");
				preparedStatementCustomer.setInt(1, customerID);
				ResultSet setCustomer = preparedStatementCustomer.executeQuery();
				while(setCustomer.next()){
					String fn = setCustomer.getString("Customer_FirstName");
					String ln = setCustomer.getString("Customer_LastName");
					customerName = fn + " " + ln;
				}
				
				/** Creates the rounded rectangle for all the values */
				RoundedRectangle r = new RoundedRectangle(buttonX, buttonY, buttonWidth, buttonHeight, hourAppointment, cutType, customerName);
				
				/** Adds the rounded rectangle to the RectPanel */
				rectPanel.addRoundedRectangle(r);
			}
		} catch (SQLException | ParseException e) {
			e.printStackTrace();
		}
	}
	
	/** Creates the closed rectangles [4] = Thursday [7] = Sunday */
	private void createClosedRectangles(){
		RoundedRectangle closed1 = new RoundedRectangle(calendarViewButtons[1][4].getX(), calendarViewButtons[1][4].getY(), calendarViewButtons[1][4].getWidth(), calendarViewButtons[1][4].getHeight());
		RoundedRectangle closed2 = new RoundedRectangle(calendarViewButtons[1][7].getX(), calendarViewButtons[1][7].getY(), calendarViewButtons[1][7].getWidth(), calendarViewButtons[1][7].getHeight());
		rectPanel.addRoundedRectangle(closed1);
		rectPanel.addRoundedRectangle(closed2);
	}
	
	public static void main(String args[]){
		CalendarView c = new CalendarView();
	}
	
	/**
	 * A separate class for the Rect JPanel
	 */
	class RectPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		
		/** Creates a array list of RoundedRectangle objects */
		public List<RoundedRectangle> roundedRects = new ArrayList<RoundedRectangle>();
		
		/** The paint method of the JPanel */
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			/** Loops through all the rouned rects and then paints them */
			for(RoundedRectangle r : roundedRects){
				r.draw(g);
			}
		}
		
		/** Returns all the rounded rects Point objects */
		public Point getRoundedRectsPoints(){
			for(RoundedRectangle r : roundedRects){
				return r.getRectPoint();
			}
			
			return null;
		}
		
		/** Returns the array list of the rounded rects */
		public List<RoundedRectangle> getRoundedRects() {
			return roundedRects;
		}
		
		/** Removes all from the rounded rects list */
		public void removeAll(){
			roundedRects.clear();
		}
		
		/** Adds a rounded rect to the array list */
		public void addRoundedRectangle(RoundedRectangle r){
			roundedRects.add(r);
			repaint();
		}
	}
}