package ca.utoronto.utm.paint;

import java.util.ArrayList;

public class Squiggle extends Shape {
	private ArrayList<Point> points=new ArrayList<Point>();
	
	public Squiggle(){
		
	}
	public void add(Point p){ this.points.add(p); }
	public ArrayList<Point> getPoints(){ return this.points; }
	
	public String toString() {
		int r = this.getColor().getRed();
		int g = this.getColor().getGreen();
		int b = this.getColor().getBlue();
		
		String s = "Squiggle\n";
		s+="\tcolor:"+r+","+g+","+b+"\n";
		s+="\tfilled:"+this.isFill()+"\n";
		s+="\tpoints\n";
		for (int i=0; i<this.getPoints().size();i++) {
			s+="\t\tpoint:("+this.getPoints().get(i).x+","+this.getPoints().get(i).y+")\n";
		}
		s+="\tend points\n";
		s+="End Squiggle";
		return s;
	}
}