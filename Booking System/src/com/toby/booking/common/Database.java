package com.toby.booking.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Sets up a connection to the database, contains a few popular retrieve methods 
 * 
 * @author Toby
 *
 */
public class Database {
	
	public Connection connection;
	public Statement statement;
	public PreparedStatement preparedStatement;
	
	/**
	 * Sets up the connection to the database
	 */
	public Database(){
		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Booking_System", "root", "root");
			statement = connection.createStatement();
			System.out.println("MYSQL CONNECTION SUCCESSFUL");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets all the customers from the tableCustomers
	 * 
	 * @return a List of strings each element contains there full name (firstname + lastname)
	 */
	public List<String> getCustomers(){
		List<String> customers = new ArrayList<String>();
		try{
			ResultSet set = statement.executeQuery("select * from tableCustomers");
			
			while(set.next()){
				customers.add(set.getString("Customer_FirstName") + " " + set.getString("Customer_LastName"));
			}
			set.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
		return customers;
	}
	
	/**
	 * Gets all the different services from the tableServices
	 * 
	 * @return a List of strings each element containing a different service
	 */
	public List<String> getServices(){
		List<String> services = new ArrayList<String>();
		try {
			ResultSet set = statement.executeQuery("select * from tableServices");
			
			while(set.next()){
				services.add(set.getString("Cut_Type"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return services;
	}
	
	/**
	 * Method for creating a new appointment, saves it to the MySQL database
	 * 
	 * @param date The date of the appointment
	 * @param time How long the appointment will last
	 * @param customerFirstName First Name of the customer
	 * @param cutType The service that the customer is having 
	 */
	public void createNewAppointment(String date, String time, String customerFirstName, String cutType){
		int customerID=0;
		int cutID=0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			/** Combines the date and time together in one datetime */
			Date finalDate = sdf.parse(date+" "+time);
			java.sql.Timestamp sqlDate = new java.sql.Timestamp(finalDate.getTime());
			
			PreparedStatement stmt = connection.prepareStatement("insert into tableAppointments (Date_Time, Customer_ID, Cut_ID) values (?, (select Customer_ID from tableCustomers where Customer_FirstName = ?), (select Cut_ID from tableServices where Cut_Type = ?))");
			stmt.setTimestamp(1, sqlDate);
			stmt.setString(2, customerFirstName);
			stmt.setString(3, cutType);
			stmt.executeUpdate();
			stmt.close();
		} catch (SQLException | ParseException e) {
			e.printStackTrace();
		}
	}
}
