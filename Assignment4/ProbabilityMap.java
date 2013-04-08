package Assignment4;

public class ProbabilityMap{
	private double[][] map;
	private double towerAccuracy = .75;
	private int towerRange = 5;
	
	
	public ProbabilityMap(int x, int y){
		map = new double[x][y];
		for(int i = 0; i<y; i++){
			// Initialize whole map to 0.05 probability
			for(int j = 0; j<x; j++){
				map[j][i] = 0.05;
			}
		}
	}
	
	public double getProbability(Location loc){
		return map[loc.x][loc.y];
	}
	
	public double probOfBeingShot(Location loc){
		return 0.0;
	}
	
	public void setProbability(Location loc, double value){
		map[loc.x][loc.y] = value;
	}
	
	public void towerSeen(Location loc){
		map[loc.x][loc.y] = 1.0;
	}
	
	public void treeSeen(Location loc){
		map[loc.x][loc.y] = 0;
		//System.out.println("Tree seen");
	}
	
	public void emptySquare(Location loc){
		map[loc.x][loc.y] = 0;
	}
	
	public void wasShot(Location loc){
		//System.out.println(loc.toString() + " Was Shot");
	}
	
	public void wasNotShot(Location loc){
		//System.out.println(loc.toString() + " Was Not Shot");
	}
	
	public void print(){
		for(int i = 0; i< map[0].length; i++){
			String line = "";
			for(int j = 0; j< map.length; j++){
				line = line + map[j][i] + ", ";
			}
			System.out.println(line);
		}
	}
}