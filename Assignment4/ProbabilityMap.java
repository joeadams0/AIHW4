package Assignment4;

public class ProbabilityMap{
	private double[][] map;
	private double towerAccuracy = .75;
	private int towerRange = 5;
	
	
	public ProbabilityMap(int x, int y){
		map = new double[x][y];
		for(int i = 0; i<y; i++){
			// Initialize whole map to 0.1 probability
			for(int j = 0; j<x; j++){
				map[j][i] = 0.1;
			}
		}
	}
	
	public double getProbability(int x, int y){
		return map[x][y];
	}
	
	public double probOfBeingShot(int x, int y){
		return 0.0;
	}
	
	public void setProbability(int x, int y, double value){
		map[x][y] = value;
	}
	
	public void towerSeen(int x, int y){
		map[x][y] = 1.0;
	}
	
	public void forestSeen(int x, int y){
	}
	
	public void emptySquare(int x, int y){
		map[x][y] = 0;
	}
	
	public void wasShot(int x, int y){
	}
	
	public void wasNotShot(int x, int y){
	}
}