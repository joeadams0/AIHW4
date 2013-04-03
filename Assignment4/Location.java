package Assignment4;
public class Location{
	public int x;
	public int y;
	public Location(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public String toString(){
		return "(" + x + ", " + y + ")";
	}
	
	public boolean equal(Location loc){
		return loc.x == x && loc.y == y;
	}
	
	public boolean equals(int x, int y){
		return x == this.x && y == this.y;
	}
}