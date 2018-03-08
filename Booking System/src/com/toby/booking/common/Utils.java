package com.toby.booking.common;

import java.awt.Color;
import java.awt.Font;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
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

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

/**
 * A class that contains a few constants required throughout the system
 * 
 * @author Toby
 *
 */
public class Utils {
	
	/** The start and end date for the system calendar */
	public static final String START_DATE = "21/11/2016";
	public static final String END_DATE = "24/11/2018";
	
	/** The number of rows and columns of buttons for the calendar view */
	public static final int NUM_ROWS = 24;
	public static final int NUM_COLUMNS = 8;

	/** The nav bar font size */
	public static int NAV_FONT_SIZE = 16;
	
	/** Different colors for each of the appointment rectangles */
	public static final Color CUT_BLOW_DRY_COLOR = new Color(199, 199, 199);
	public static final Color BLOW_DRY_COLOR = new Color(200, 230, 201);
	public static final Color WET_CUT_COLOR = new Color(255, 249, 196);
	public static final Color HIGHLIGHT_COLOR = new Color(207, 216, 220);
	public static final Color ROOT_TOUCH_UP_COLOR = new Color(215, 204, 200);
	public static final Color CLOSED_COLOR = new Color(211, 211, 211);
	public static final Color IMAGE_PREVIEW_COLOR = new Color(214, 214, 214);
	
	/** Different time formatters used within the system, converts strings to date and vice versa */
	public static final SimpleDateFormat SIMPLE_DATE_FORMAT_TIME = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	public static final SimpleDateFormat SIMPLE_DATE_FORMAT_JUST_TIME = new SimpleDateFormat("HH:mm:ss");
	public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	public static final DateTimeFormatter DATE_TIME_FORMATTER_TIME = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
	
	/** The two main fonts used within the system */
	public static final Font FONT_LARGE = new Font("Sathu", Font.TRUETYPE_FONT, 19);
	public static final Font FONT_MEDIUM = new Font("Sathu", Font.TRUETYPE_FONT, 15);
	
	/** The directory that the patch tests images should be picked from */
	public static final String DIRECTORY_INITIAL = "C:/Users/Toby/OneDrive/Documents/College/Computer Science/Coursework/Final/Program/Booking System/resources/patch tests";

	/** Username and password for the gmail account */
	public static final String USERNAME = "tobydrane@gmail.com";
	public static final String PASSWORD = "wilburdom";
	
	/**
	 * Sends out email to the customer 
	 */
	public static void sendEmail(String customerFirstName, String customerEmail, String dateString, String timeString, String service){
		/** Sets up email properties, server, account details etc */
		Properties properties = new Properties();
		properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
		properties.put("mail.smtp.auth", true);
		properties.put("mail.smtp.starttls.enable", true);
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.port", "587");
		
		/** Checks the username and password to see if they are correct */
		Session session = Session.getInstance(properties, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication(){
				return new PasswordAuthentication(Utils.USERNAME, Utils.PASSWORD);
			}
		});
		
		try{
			/** Sets up the basic message, setting subject and parses who to send the email to */
			Message m = new MimeMessage(session);
			m.setFrom(new InternetAddress("no-reply@gmail.com"));
			m.setRecipients(Message.RecipientType.TO, InternetAddress.parse(customerEmail));
			m.setSubject("Tindalls Appointment Confirmation");
			
			/** Used to replace place holder values in the email template to customers values */
			Map params = new HashMap();
			params.put("firstName", customerFirstName);
			params.put("dateString", dateString);
			params.put("timeString", timeString);
			params.put("service", service);
			
			/** Apache Velocity Engine API: Loads the email template*/
			VelocityEngine velocityEngine = new VelocityEngine();
			velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
			velocityEngine.setProperty("classpath.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
			velocityEngine.init();
			
			/** Loads the email.vsl template*/
			Template template = velocityEngine.getTemplate("email.vsl");
			VelocityContext velocityContext = new VelocityContext(params);
			StringWriter writer = new StringWriter();
			
			/** Replaces the email.vsl place holders with the customer values*/
			template.merge(velocityContext, writer);
			
			/** Adds this email template to the body of the email*/
			m.setText(writer.toString());
			
			/** Sends out the email */
			Transport.send(m);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
