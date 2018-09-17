package ca.utoronto.utm.paint;

public class Circle extends Shape {
	private Point centre;
	private int radius;
	
	public Circle(){
		this(new Point(0,0), 0);
	}
	public Circle(Point centre, int radius){
		this.centre = centre;
		this.radius = radius;
	}
	public Point getCentre() { return centre; }
	public void setCentre(Point centre) { this.centre = centre; }
	public int getRadius() { return radius; }
	public void setRadius(int radius) { this.radius = radius; }
	
	public String toString() {
		int r = this.getColor().getRed();
		int g = this.getColor().getGreen();
		int b = this.getColor().getBlue();
		
		String s="Circle\n";
		s+="\tcolor:"+r+","+g+","+b+"\n";
		s+="\tfilled:"+this.isFill()+"\n";
		s+="\tcenter:("+this.getCentre().x+","+this.getCentre().y+")\n";
		s+="\tradius:"+this.getRadius()+"\n";
		s+="End Circle";
		
		return s;
		
	}
	
}