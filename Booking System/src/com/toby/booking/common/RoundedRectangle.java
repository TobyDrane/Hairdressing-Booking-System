package com.toby.booking.common;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.RoundRectangle2D;

/**
 * Custom rectangle class, draws rectangles with desired color and size based on what type
 * of service
 * 
 * @author Toby
 *
 */
public class RoundedRectangle {
	
	/** The x, y, width and height of the button that was pressed */
	private int buttonX;
	private int buttonY;
	private int buttonWidth;
	private int buttonHeight;
	
	/**
	 * The x, y, width and height of the rectangle to be drawn,
	 * NOTE: these values will be different to the buttons values
	 */
	private int rectX;
	private int rectY;
	private int rectWidth;
	private int rectHeight;
	
	/** Is the appointment a hour long*/
	private boolean hourAppointment;
	
	/** What service are they having */
	private String cutType;
	
	/** Customers names */
	private String customerName;
	
	/** The color of the rectangle to be drawn */
	private Color paintColor;
	
	public RoundRectangle2D roundedRectangle;
	public RoundRectangle2D closedRectangle;
	public Point rectPoint;
	
	/**
	 * The constructor for creating a appointment rectangle
	 * 
	 * @param buttonX the x position of the pressed button
	 * @param buttonY the y position of the pressed button
	 * @param buttonWidth the pressed button width
	 * @param buttonHeight the pressed button height
	 * @param hourAppointment is the appointment a hour long
	 * @param cutType what service are they having
	 * @param customerName what is the customers name
	 */
	public RoundedRectangle(int buttonX, int buttonY, int buttonWidth, int buttonHeight, boolean hourAppointment, String cutType, String customerName){
		this.buttonX = buttonX;
		this.buttonY = buttonY;
		this.buttonWidth = buttonWidth;
		this.buttonHeight = buttonHeight;
		this.hourAppointment = hourAppointment;
		this.cutType = cutType;
		this.customerName = customerName;
		
		/** Gets what service they are having and sets the rectangle to the corresponding color */
		if(cutType.equals("Cut and Blow Dry")) paintColor = Utils.CUT_BLOW_DRY_COLOR;
		if(cutType.equals("Blow Dry")) paintColor = Utils.BLOW_DRY_COLOR;
		if(cutType.equals("Wet Cut")) paintColor = Utils.WET_CUT_COLOR;
		if(cutType.equals("Highlight/Foiling")) paintColor = Utils.HIGHLIGHT_COLOR;
		if(cutType.equals("Root Touch Up")) paintColor = Utils.ROOT_TOUCH_UP_COLOR;
		
		/** Sets the rectangle x, y and width from the pressed button values */
		rectX = buttonX + 3;
		rectY = buttonY + 2;
		rectWidth = buttonWidth - 6;
		
		/** If the appointment is a hour long double the rectangle size */
		if(hourAppointment) rectHeight = (buttonHeight - 2) * 2;
		else rectHeight = buttonHeight - 4;
	
		roundedRectangle = new RoundRectangle2D.Float(rectX, rectY, rectWidth, rectHeight, 10, 10);
		rectPoint = new Point(rectX, rectY);
	}
	
	/**
	 * Alternative constructor for the class, used for creating the closed rectangle 
	 * 
	 * @param buttonX the x position of the button
	 * @param buttonY the y position of the button
	 * @param buttonWidth the button width
	 * @param buttonHeight the button height
	 */
	public RoundedRectangle(int buttonX, int buttonY, int buttonWidth, int buttonHeight){
		closedRectangle = new RoundRectangle2D.Float(buttonX + 3, buttonY + 2, buttonWidth - 6, ((buttonHeight - 2) * 24) + 19, 10, 10);
	}
	
	/**
	 * Called when the rectangle is going to be drawn onto the screen
	 */
	public void draw(Graphics g){
		Graphics2D g2d = (Graphics2D) g;
		
		/** Draws the appointment rectangles */
		if(roundedRectangle != null){
			g2d.setColor(paintColor);
			g2d.fill(roundedRectangle);
		
			g2d.setFont(new Font("Sathu", Font.TRUETYPE_FONT, 12));
			g2d.setColor(Color.BLACK);
			if(hourAppointment){
				g2d.drawString(customerName, rectX+12, rectY+26);
			}else{
				g2d.drawString(customerName, rectX+11, rectY+14);
			}
		}
		
		/** Draws the closed rectangles */
		if(closedRectangle != null){
			g2d.setColor(Utils.CLOSED_COLOR);
			g2d.fill(closedRectangle);
			g2d.setColor(Color.BLACK);
			g2d.setFont(new Font("Sathu", Font.BOLD, 20));
			g2d.drawString("Closed", (int) closedRectangle.getX()+26, 305);
		}
	}
	
	public Point getRectPoint() {
		return rectPoint;
	}
	
	public RoundRectangle2D getRoundedRectangle() {
		return roundedRectangle;
	}
	
	/**
	 * @return the x position of the desired rectangle
	 */
	public int getRectX() {
		return rectX;
	}
	
	/**
	 * @return the y position of the desired rectangle
	 */
	public int getRectY() {
		return rectY;
	}
	
	/**
	 * @return the width of the desired rectangle 
	 */
	public int getRectWidth() {
		return rectWidth;
	}
	
	/**
	 * @return the height of the desired rectangle 
	 */
	public int getRectHeight() {
		return rectHeight;
	}
}
