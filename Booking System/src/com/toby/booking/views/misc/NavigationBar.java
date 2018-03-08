package com.toby.booking.views.misc;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.toby.booking.common.Utils;

/**
 * The navigation panel for the system. Draws and formats buttons for the nav bar
 * 
 * @author Toby
 *
 */
public class NavigationBar extends JPanel {
	private static final long serialVersionUID = 1L;
	
	/** The buttons for the nav bar */
	public JButton btnCalendar;
	public JButton btnCustomers;
	public JButton btnPColorData;
	public JButton btnPTests;
	
	/** Font used for the nav bar */
	private Font navFont;
	
	/** Constructor for the class, used to just set up the font */
	public NavigationBar(){
		navFont = new Font("Sathu", Font.TRUETYPE_FONT, Utils.NAV_FONT_SIZE);
	}
	
	/** Creates and formats the buttons for the nav bar */
	public void createWidgets(){
		btnCalendar = new JButton("Calendar");
		btnCalendar.setOpaque(true);
		btnCalendar.setContentAreaFilled(false);
		btnCalendar.setBorderPainted(false);
		btnCalendar.setFont(navFont);
		btnCalendar.setForeground(Color.WHITE);
		btnCalendar.setFocusPainted(false);
		
		btnCustomers = new JButton("Customers");
		btnCustomers.setOpaque(true);
		btnCustomers.setContentAreaFilled(false);
		btnCustomers.setBorderPainted(false);
		btnCustomers.setFont(navFont);
		btnCustomers.setForeground(Color.WHITE);
		btnCustomers.setFocusPainted(false);
		
		btnPColorData = new JButton("Previous Color Data");
		btnPColorData.setOpaque(true);
		btnPColorData.setContentAreaFilled(false);
		btnPColorData.setBorderPainted(false);
		btnPColorData.setFont(navFont);
		btnPColorData.setForeground(Color.WHITE);
		btnPColorData.setFocusPainted(false);	
		
		btnPTests = new JButton("Patch Tests");
		btnPTests.setOpaque(true);
		btnPTests.setContentAreaFilled(false);
		btnPTests.setBorderPainted(false);
		btnPTests.setFont(navFont);
		btnPTests.setForeground(Color.WHITE);
		btnPTests.setFocusPainted(false);
	}
}

