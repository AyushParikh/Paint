package ca.utoronto.utm.paint;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Parse a file in Version 1.0 PaintSaveFile format. An instance of this class
 * understands the paint save file format, storing information about
 * its effort to parse a file. After a successful parse, an instance
 * will have an ArrayList of PaintCommand suitable for rendering.
 * If there is an error in the parse, the instance stores information
 * about the error. For more on the format of Version 1.0 of the paint 
 * save file format, see the associated documentation.
 * 
 * @author 
 *
 */
public class PaintSaveFileParser {
	private int lineNumber = 0; // the current line being parsed
	private String errorMessage =""; // error encountered during parse
	private ArrayList<PaintCommand> commands; // created as a result of the parse
	
	/**
	 * Below are Patterns used in parsing 
	 */
	private Pattern pFileStart=Pattern.compile("^PaintSaveFileVersion1.0$");
	private Pattern pFileEnd=Pattern.compile("^EndPaintSaveFile$");

	private Pattern pCircleStart=Pattern.compile("^Circle$");
	private Pattern pCircleEnd=Pattern.compile("^EndCircle$");
	
	private Pattern pSquiggleStart=Pattern.compile("^Squiggle$");
	private Pattern pSquiggleEnd=Pattern.compile("^EndSquiggle");
	
	private Pattern pRectangleStart=Pattern.compile("^Rectangle$");
	private Pattern pRectangleEnd=Pattern.compile("^EndRectangle$");
	
	private Pattern pColour=Pattern.compile("^color:(.*?)$");
	private Pattern pFill=Pattern.compile("^filled:(.*?)$");
	private Pattern pCenter=Pattern.compile("^center:[(](.*?)[)]$");
	private Pattern pRadius=Pattern.compile("^radius:(.*?)$");
	private Pattern pP1=Pattern.compile("^p1:[(](.*?)[)]$");
	private Pattern pP2=Pattern.compile("^p2:[(](.*?)[)]$");
	private Pattern pPoint=Pattern.compile("^points$");
	private Pattern pPointEnd=Pattern.compile("^endpoints$");
	
	String[] circlevalues = new String[4];
	String[] rectanglevalues = new String[4];
	Object[] squigglevalues = new Object[3];
	ArrayList<String> points = new ArrayList<String>();
	
	private ArrayList<String> shape = new ArrayList<String>();
	
	// ADD MORE!!
	
	/**
	 * Store an appropriate error message in this, including 
	 * lineNumber where the error occurred.
	 * @param mesg
	 */
	private void error(String mesg){
		this.commands.clear();
		this.errorMessage = "Error in line "+lineNumber+" "+mesg;
	}
	/**
	 * 
	 * @return the PaintCommands resulting from the parse
	 */
	public ArrayList<PaintCommand> getCommands(){
		return this.commands;
	}
	/**
	 * 
	 * @return the error message resulting from an unsuccessful parse
	 */
	public String getErrorMessage(){
		return this.errorMessage;
	}
	
	/**
	 * Parse the inputStream as a Paint Save File Format file.
	 * The result of the parse is stored as an ArrayList of Paint command.
	 * If the parse was not successful, this.errorMessage is appropriately
	 * set, with a useful error message.
	 * 
	 * @param inputStream the open file to parse
	 * @return whether the complete file was successfully parsed
	 */
	public boolean parse(BufferedReader inputStream) {
		this.commands = new ArrayList<PaintCommand>();
		this.errorMessage="";
		
		// During the parse, we will be building one of the 
		// following shapes. As we parse the file, we modify 
		// the appropriate shape.
		Circle circle = null; 
		Rectangle rectangle = null;
		Squiggle squiggle = null;
	
		try {	
			int state=0; Matcher m; String l;
			
			this.lineNumber=0;
			while ((l = inputStream.readLine()) != null) {
				this.lineNumber++;
				System.out.println(lineNumber+" "+l+" "+state);
				switch(state){
					case 0:
						m=pFileStart.matcher(l.replaceAll("\\s+",""));
						if(m.matches()){
							state=1;
							break;
						}
						error("Expected Start of Paint Save File");
						return false;
					case 1: // Looking for the start of a new object or end of the save file
						m=pCircleStart.matcher(l.replaceAll("\\s+",""));
						if(m.matches()){
							state = 5;
							break;
						}
						else {
							state=2;
						}
					case 2:
						m=pRectangleStart.matcher(l.replaceAll("\\s+",""));
						if(m.matches()){
							state = 6;
							break;
						}
						else {
							state=3;
						}
					case 3:
						m=pSquiggleStart.matcher(l.replaceAll("\\s+",""));
						if(m.matches()){
							state = 7;
							break;
						}
						else {
							state=4;
						}
					case 4:
						m=pFileEnd.matcher(l.replaceAll("\\s+",""));
						if (m.matches()) {
							state = 10;
						}
						break;
						
					case 5:
						m=pCircleEnd.matcher(l.replaceAll("\\s+",""));
						if (m.matches()) {
							this.addCircle(circlevalues);
							circlevalues = new String[4];
							state = 1;
							break;
						}
						m=pRectangleEnd.matcher(l.replaceAll("\\s+",""));
						if(m.matches()) {
							error("");
							state = 9;
							break;
							
						}
						m=pRectangleStart.matcher(l.replaceAll("\\s+",""));
						if(m.matches()) {
							error("");
							state = 9;
							break;
							
						}
						m=pSquiggleEnd.matcher(l.replaceAll("\\s+",""));
						if(m.matches()) {
							error("");
							state = 9;
							break;
							
						}
						m=pSquiggleStart.matcher(l.replaceAll("\\s+",""));
						if(m.matches()) {
							error("");
							state=9;
							break;
						}
						else {
							state = 5;
							m=pColour.matcher(l.replaceAll("\\s+",""));
							if (m.matches()) {
								if (circlevalues[1]==null && circlevalues[2]==null && circlevalues[3] ==null) {
									circlevalues[0]=l;
									String colors = ((String) circlevalues[0]).replaceAll("[^\\d,]", "");
									int r=0;
									int g=0;
									int b=0;
									Pattern p = Pattern.compile("(?:[^,]*\\,){0}([^,]*)");
									Matcher m1 = p.matcher(colors);
									if (m1.find()) {
										r = Integer.parseInt(m1.group(1));
									}
									
									p = Pattern.compile("(?:[^,]*\\,){1}([^,]*)");
									m1 = p.matcher(colors);
									if (m1.find()) {
										g = Integer.parseInt(m1.group(1));
									}
									
									p = Pattern.compile("(?:[^,]*\\,){2}([^,]*)");
									m1 = p.matcher(colors);
									if (m1.find()) {
										b = Integer.parseInt(m1.group(1));
									}
									
									if ((r<0 || r>255)||(g<0 || g>255)||(b<0 || b>255)) {
										state = 9;
										error("");
									}
									
								}
								else {
									state = 9;
									error("");
								}
								
							}
							
							m=pFill.matcher(l.replaceAll("\\s+",""));
							if (m.matches()) {
								if (circlevalues[0]!=null && circlevalues[2]==null && circlevalues[3] ==null) {
									circlevalues[1]=l;
								}
								
								else {
									state = 9;
									error("");
								}
							}
							
							m=pCenter.matcher(l.replaceAll("\\s+",""));
							if (m.matches()) {
								if (circlevalues[0]!=null && circlevalues[1]!=null && circlevalues[3] ==null) {
									circlevalues[2]=l;
								}
								else {
									state =9;
									error("");
								}
							}
							
							m=pRadius.matcher(l.replaceAll("\\s+",""));
							if (m.matches()) {
								if (circlevalues[0]!=null && circlevalues[1]!=null && circlevalues[2] !=null) {
									Pattern radPattern = Pattern.compile("^\tradius:(.*?)$");
									Matcher radMatch = radPattern.matcher(l);
									if (radMatch.find()) {
										int radius = Integer.parseInt(radMatch.group(1));
										if (radius<0) {
											error("");
											state = 9;
										}
									}
									circlevalues[3]=l;
								}
								else {
									state = 9;
									error("");
								}
							}
							break;
						}
					case 6:
						m=pRectangleEnd.matcher(l.replaceAll("\\s+",""));
						if (m.matches()) {
							this.addRectangle(rectanglevalues);
							rectanglevalues = new String[4];
							state = 1;
							break;
						}
						m=pCircleEnd.matcher(l.replaceAll("\\s+",""));
						if(m.matches()) {
							error("");
							state = 9;
							break;
							
						}
						m=pCircleStart.matcher(l.replaceAll("\\s+",""));
						if(m.matches()) {
							error("");
							state = 9;
							break;
							
						}
						m=pSquiggleEnd.matcher(l.replaceAll("\\s+",""));
						if(m.matches()) {
							error("");
							state = 9;
							break;
							
						}
						m=pSquiggleStart.matcher(l.replaceAll("\\s+",""));
						if(m.matches()) {
							error("");
							state=9;
							break;
						}
						else {
							state = 6;
							m=pColour.matcher(l.replaceAll("\\s+",""));
							if (m.matches()) {
								if (rectanglevalues[1]==null && rectanglevalues[2]==null && rectanglevalues[3] ==null) {
									rectanglevalues[0]=l;
									String colors = ((String) rectanglevalues[0]).replaceAll("[^\\d,]", "");
									int r=0;
									int g=0;
									int b=0;
									Pattern p = Pattern.compile("(?:[^,]*\\,){0}([^,]*)");
									Matcher m1 = p.matcher(colors);
									if (m1.find()) {
										r = Integer.parseInt(m1.group(1));
									}
									
									p = Pattern.compile("(?:[^,]*\\,){1}([^,]*)");
									m1 = p.matcher(colors);
									if (m1.find()) {
										g = Integer.parseInt(m1.group(1));
									}
									
									p = Pattern.compile("(?:[^,]*\\,){2}([^,]*)");
									m1 = p.matcher(colors);
									if (m1.find()) {
										b = Integer.parseInt(m1.group(1));
									}
									
									if ((r<0 || r>255)||(g<0 || g>255)||(b<0 || b>255)) {
										state = 9;
										error("");
									}
								}
								else {
									state = 9;
									error("");
								}
							}
							
							m=pFill.matcher(l.replaceAll("\\s+",""));
							if (m.matches()) {
								if (rectanglevalues[0]!=null && rectanglevalues[2]==null && rectanglevalues[3] ==null) {
									rectanglevalues[1]=l;
								}
								else {
									state = 9;
									error("");
								}
							}
							
							m=pP1.matcher(l.replaceAll("\\s+",""));
							if (m.matches()) {
								if (rectanglevalues[0]!=null && rectanglevalues[1]!=null && rectanglevalues[3] ==null) {
									rectanglevalues[2]=l;
								}
								else {
									state = 9;
									error("");
								}
								
							}
							
							m=pP2.matcher(l.replaceAll("\\s+",""));
							if (m.matches()) {
								if (rectanglevalues[0]!=null && rectanglevalues[1]!=null && rectanglevalues[2] !=null) {
									rectanglevalues[3]=l;
								}
								else {
									state = 9;
									error("");
								}
							}
							break;
						}
					case 7:
						m=pSquiggleEnd.matcher(l.replaceAll("\\s+",""));
						if (m.matches()) {
							this.addSquiggle(squigglevalues);
							points.clear();
							squigglevalues = new Object[3];
							state = 1;
							break;
						}
						m=pCircleEnd.matcher(l.replaceAll("\\s+",""));
						if(m.matches()) {
							error("");
							state = 9;
							break;
							
						}
						m=pCircleStart.matcher(l.replaceAll("\\s+",""));
						if(m.matches()) {
							error("");
							state = 9;
							break;
							
						}
						m=pRectangleEnd.matcher(l.replaceAll("\\s+",""));
						if(m.matches()) {
							error("");
							state = 9;
							break;
							
						}
						m=pRectangleStart.matcher(l.replaceAll("\\s+",""));
						if(m.matches()) {
							error("");
							state=9;
							break;
						}
						else {
							state = 7;
							m=pColour.matcher(l.replaceAll("\\s+",""));
							if (m.matches()) {
								if (squigglevalues[1]==null && squigglevalues[2]==null) {
									squigglevalues[0]=l;
									String colors = ((String) squigglevalues[0]).replaceAll("[^\\d,]", "");
									int r=0;
									int g=0;
									int b=0;
									Pattern p = Pattern.compile("(?:[^,]*\\,){0}([^,]*)");
									Matcher m1 = p.matcher(colors);
									if (m1.find()) {
										r = Integer.parseInt(m1.group(1));
									}
									
									p = Pattern.compile("(?:[^,]*\\,){1}([^,]*)");
									m1 = p.matcher(colors);
									if (m1.find()) {
										g = Integer.parseInt(m1.group(1));
									}
									
									p = Pattern.compile("(?:[^,]*\\,){2}([^,]*)");
									m1 = p.matcher(colors);
									if (m1.find()) {
										b = Integer.parseInt(m1.group(1));
									}
									
									if ((r<0 || r>255)||(g<0 || g>255)||(b<0 || b>255)) {
										state = 9;
										error("");
									}
								}
								else {
									error("");
									state = 9;
									break;
								}
							}
							
							m=pFill.matcher(l.replaceAll("\\s+",""));
							if (m.matches()) {
								if (squigglevalues[0]!=null && squigglevalues[2]==null) {
									squigglevalues[1]=l;
								}
								else {
									error("");
									state = 9;
									break;
								}
							}
							m=pPoint.matcher(l.replaceAll("\\s+",""));
							if (m.matches()) {
								if (squigglevalues[0]!=null && squigglevalues[1]!=null) {
									state = 8;
									break;
								}
								else {
									error("");
									state = 9;
									break;
								}
							}
							break;
						}
					case 8:
						m=pPointEnd.matcher(l.replaceAll("\\s+",""));
						if (m.matches()) {
							state = 7;
							break;
						}
						else {
							points.add(l);
						}
						break;
					case 9:
						return false;
					case 10:
						if (l!=null) {
							state = 9;
							error("");
						}
						break;
					
				}
			}
		}  catch (Exception e){
			
		}
		return true;
	}
	/**
	 * Add squiggle object values to the command list.
	 * @param squiggle
	 */
	public void addSquiggle(Object[] squiggle) {
		String colors = ((String) squiggle[0]).replaceAll("[^\\d,]", "");
		int r = 0;
		int g = 0;
		int b = 0;
		boolean fill = false;
		Squiggle s = new Squiggle();
		
		Pattern p = Pattern.compile("(?:[^,]*\\,){0}([^,]*)");
		Matcher m = p.matcher(colors);
		if (m.find()) {
			r = Integer.parseInt(m.group(1));
		}
		
		p = Pattern.compile("(?:[^,]*\\,){1}([^,]*)");
		m = p.matcher(colors);
		if (m.find()) {
			g = Integer.parseInt(m.group(1));
		}
		
		p = Pattern.compile("(?:[^,]*\\,){2}([^,]*)");
		m = p.matcher(colors);
		if (m.find()) {
			b = Integer.parseInt(m.group(1));
		}
		
		String filled = (String) squiggle[1];
		Pattern pattern = Pattern.compile("^\tfilled:(.*?)$");
		Matcher matcher = pattern.matcher(filled);
		if (matcher.find())
		{
		    fill = Boolean.parseBoolean(matcher.group(1));
		}
		
		for (int i = 3; i < points.size(); i++) {
			pattern = Pattern.compile("^\t\tpoint:[(](.*?)[)]$");
			matcher = pattern.matcher(points.get(i));
			if (matcher.find()){
				String[] parts = matcher.group(1).split(",");
				Point point = new Point(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
				s.add(point);
			}
		}

	
		s.setColor(new Color (r, g, b));
		s.setFill(fill);
		SquiggleCommand squigglec = new SquiggleCommand(s);
		
		commands.add(squigglec);
		
	}
	/**
	 * Add rectangle object values to the command list.
	 * @param rectangle
	 */
	public void addRectangle(String[] rectangle) {
		String colors = rectangle[0].replaceAll("[^\\d,]", "");
		int r = 0;
		int g = 0;
		int b = 0;
		boolean fill = false;
		int p1x = 0;
		int p1y = 0;
		int p2x = 0;
		int p2y = 0;
		
		Pattern p = Pattern.compile("(?:[^,]*\\,){0}([^,]*)");
		Matcher m = p.matcher(colors);
		if (m.find()) {
			r = Integer.parseInt(m.group(1));
		}
		
		p = Pattern.compile("(?:[^,]*\\,){1}([^,]*)");
		m = p.matcher(colors);
		if (m.find()) {
			g = Integer.parseInt(m.group(1));
		}
		
		p = Pattern.compile("(?:[^,]*\\,){2}([^,]*)");
		m = p.matcher(colors);
		if (m.find()) {
			b = Integer.parseInt(m.group(1));
		}
		
		String filled = rectangle[1];
		Pattern pattern = Pattern.compile("^\tfilled:(.*?)$");
		Matcher matcher = pattern.matcher(filled);
		if (matcher.find())
		{
		    fill = Boolean.parseBoolean(matcher.group(1));
		}
		
		String pat1 = rectangle[2];
		pattern = Pattern.compile("^\tp1:[(](.*?)[)]$");
		matcher = pattern.matcher(pat1);
		if (matcher.find()) {
			String[] parts = matcher.group(1).split(",");
			p1x = Integer.parseInt(parts[0]);
			p1y = Integer.parseInt(parts[1]);

		}
		
		String pat2 = rectangle[3];
		pattern = Pattern.compile("^\tp2:[(](.*?)[)]$");
		matcher = pattern.matcher(pat2);
		if (matcher.find()) {
			String[] parts = matcher.group(1).split(",");
			p2x = Integer.parseInt(parts[0]);
			p2y = Integer.parseInt(parts[1]);

		}
		Point p1 = new Point(p1x, p1y);
		Point p2 = new Point(p2x, p2y);
		Rectangle rect = new Rectangle(p1, p2);
		rect.setFill(fill);
		rect.setColor(new Color(r, g, b));
		RectangleCommand rectc = new RectangleCommand(rect);
		commands.add(rectc);
	}
	/**
	 * Add circle object values to the command list.
	 * @param circle
	 */
	public void addCircle(String[] circle) {
		String colors = circle[0].replaceAll("[^\\d,]", "");
		int r = 0;
		int g = 0;
		int b = 0;
		boolean fill = false;
		int x = 0;
		int y = 0;
		Point center = new Point(x, y);
		int radius = 0;
		
		Pattern p = Pattern.compile("(?:[^,]*\\,){0}([^,]*)");
		Matcher m = p.matcher(colors);
		if (m.find()) {
			r = Integer.parseInt(m.group(1));
		}
		
		p = Pattern.compile("(?:[^,]*\\,){1}([^,]*)");
		m = p.matcher(colors);
		if (m.find()) {
			g = Integer.parseInt(m.group(1));
		}
		
		p = Pattern.compile("(?:[^,]*\\,){2}([^,]*)");
		m = p.matcher(colors);
		if (m.find()) {
			b = Integer.parseInt(m.group(1));
		}
		
		String filled = circle[1];
		Pattern pattern = Pattern.compile("^\tfilled:(.*?)$");
		Matcher matcher = pattern.matcher(filled);
		if (matcher.find())
		{
		    fill = Boolean.parseBoolean(matcher.group(1));
		}
		
		String cent = circle[2];
		Pattern centpat = Pattern.compile("^\tcenter:[(](.*?)[)]$");
		Matcher centmatch = centpat.matcher(cent);
		if (centmatch.find()) {
			String[] parts = centmatch.group(1).split(",");
			x = Integer.parseInt(parts[0]);
			y = Integer.parseInt(parts[1]);
			center.x=x;
			center.y=y;
		}
		
		String rad = circle[3];
		Pattern radPattern = Pattern.compile("^\tradius:(.*?)$");
		Matcher radMatch = radPattern.matcher(rad);
		if (radMatch.find()) {
			radius = Integer.parseInt(radMatch.group(1));
		}
		
		Circle c = new Circle(center, radius);
		c.setColor(new Color(r, g, b));
		c.setFill(fill);
		CircleCommand cc = new CircleCommand(c);
		commands.add(cc);
	}
	
}
