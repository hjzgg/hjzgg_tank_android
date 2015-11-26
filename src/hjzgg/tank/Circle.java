package hjzgg.tank;

public class Circle {
	public int x1,x2, y1,y2;
	public Circle(int x1, int y1, int x2, int y2){
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
	}
	
	public static boolean isOk(Circle r, int x0, int y0, int dist){
		 int x1 = (r.x1 + r.x2)/2, y1 = (r.y1 + r.y2)/2;
		 if(Math.sqrt((x1-x0)*(x1-x0) + (y1-y0)*(y1-y0)) > dist) return false;
		 
		 return true;
	}
}