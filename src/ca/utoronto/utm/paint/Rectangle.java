package ca.utoronto.utm.paint;

public class Rectangle extends Shape {
	private Point p1,p2;
	public Rectangle(Point p1, Point p2){
		this.p1 = p1; this.p2=p2;
	}
	
	public Rectangle() {
		this(new Point(0,0), new Point(0,0));
	}

	public Point getP1() {
		return p1;
	}

	public void setP1(Point p1) {
		this.p1 = p1;
	}

	public Point getP2() {
		return p2;
	}

	public void setP2(Point p2) {
		this.p2 = p2;
	}

	public Point getTopLeft(){
		return new Point(Math.min(p1.x, p2.x), Math.min(p1.y, p2.y));
	}
	public Point getBottomRight(){
		return new Point(Math.max(p1.x, p2.x), Math.max(p1.y, p2.y));
	}
	public Point getDimensions(){
		Point tl = this.getTopLeft();
		Point br = this.getBottomRight();
		return(new Point(br.x-tl.x, br.y-tl.y));
	}
	
	public String toString() {
		int r = this.getColor().getRed();
		int g = this.getColor().getGreen();
		int b = this.getColor().getBlue();
		
		String s = "Rectangle\n";
		s+="\tcolor:"+r+","+g+","+b+"\n";
		s+="\tfilled:"+this.isFill()+"\n";
		s+="\tp1:("+this.getP1().x+","+this.getP1().y+")\n";
		s+="\tp2:("+this.getP2().x+","+this.getP2().y+")\n";
		s+="End Rectangle";
		
		return s;
	}
}