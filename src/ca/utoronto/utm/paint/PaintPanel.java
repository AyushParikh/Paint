package ca.utoronto.utm.paint;

import javax.swing.*;  
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class PaintPanel extends JPanel {
	private static final long serialVersionUID = 3277442988868869424L;
	private ArrayList<PaintCommand> commands = new ArrayList<PaintCommand>();
	
	public PaintPanel(){
		this.setBackground(Color.white);
		this.setPreferredSize(new Dimension(300,300));
	}
	
	/**
	 * Sets the command array
	 * @param commands Arraylist<PaintCommand> 
	 */
	public void setCommands(ArrayList<PaintCommand> commands){
		this.commands=commands;
	}
	/**
	 * Resets the Paint Program
	 */
	public void reset(){
		this.commands.clear();
		this.repaint();
	}
	/**
	 * Adds a command to the command arraylist
	 * @param command PaintCommand
	 */
	public void addCommand(PaintCommand command){
		this.commands.add(command);
	}
	/**
	 * Saves the paint file.
	 * @param writer Reads the file.
	 */
	public void save(PrintWriter writer){
		writer.println("Paint Save File Version 1.0");
		for (int i = 0; i<commands.size(); i++) {
			writer.println(commands.get(i).toString());
		}
		writer.print("End Paint Save File");
		writer.close();
	}
	
	
	public void paintComponent(Graphics g) {
        super.paintComponent(g); //paint background
        Graphics2D g2d = (Graphics2D) g;		
		for(PaintCommand c: this.commands){
			c.execute(g2d);
		}
		g2d.dispose();
	}
}
