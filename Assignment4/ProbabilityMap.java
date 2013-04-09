package Assignment4;

public class ProbabilityMap{
	private double[][] map;
	private double towerAccuracy = .75;
	private int towerRange = 5;
	private static int SHOOTING_RANGE = 5;
	private static int TREE_RANGE = 2;
	
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
		int lowerx = loc.x-SHOOTING_RANGE;
		int lowery = loc.y-SHOOTING_RANGE;
		int upperx = loc.x+SHOOTING_RANGE;
		int uppery = loc.y+SHOOTING_RANGE;
		if(lowerx < 0)
			lowerx = 0;
		if(lowery < 0)
			lowery =0;
		if(upperx >= map.length)
			upperx = map.length - 1;
		if(uppery >= map[0].length)
                        uppery = map[0].length - 1;

		double tmp=1.0;
		for(int i=lowerx; i<=upperx; i++){
			for(int j=lowery; j<uppery; j++){
				tmp = tmp*(1.0- getProbability(new Location(i, j)));
			} 
		}
		tmp = 1.0-tmp;
		
		tmp = .75*tmp;
		return tmp;
	}
	
	public void setProbability(Location loc, double value){
		map[loc.x][loc.y] = value;
	}
	
	public void towerSeen(Location loc){
		map[loc.x][loc.y] = 1.0;
	}
	
	public void treeSeen(Location loc){
		map[loc.x][loc.y] = 0;
		int lowerx = loc.x-TREE_RANGE;
                int lowery = loc.y-TREE_RANGE;
                int upperx = loc.x+TREE_RANGE;
                int uppery = loc.y+TREE_RANGE;
                if(lowerx < 0)
                        lowerx = 0;
                if(lowery < 0)
                        lowery =0;
                if(upperx >= map.length)
                        upperx = map.length - 1;
                if(uppery >= map[0].length)
                        uppery = map[0].length - 1;
		Location current = null;
                for(int i=lowerx; i<=upperx; i++){
                        for(int j=lowery; j<uppery; j++){
				current = new Location(i, j);
				if(!(getProbability(current) == 0.0 || getProbability(current) >= .7 || getProbability(current) == .35)){
					setProbability(new Location(i, j), Math.min(map[loc.x][loc.y] + 0.1, 1.0));
				}
                        }
                }
		map[loc.x][loc.y] = 0;		return;
	}
	
	public void emptySquare(Location loc){
		map[loc.x][loc.y] = 0;
	}
	
	public void wasShot(Location loc){
		int lowerx = loc.x-SHOOTING_RANGE;
                int lowery = loc.y-SHOOTING_RANGE;
                int upperx = loc.x+SHOOTING_RANGE;
                int uppery = loc.y+SHOOTING_RANGE;
                if(lowerx < 0)
                        lowerx = 0;
                if(lowery < 0)
                        lowery =0;
                if(upperx >= map.length)
                        upperx = map.length - 1;
                if(uppery >= map[0].length)
                        uppery = map[0].length - 1;
		Location current=null;
		for(int i=lowerx; i<=upperx; i++){
            for(int j=lowery; j<uppery; j++){
                current = new Location(i, j);
                if(getProbability(current) == 0.0 || getProbability(current) >= 0.7){
                        continue;
                }
                setProbability(new Location(i, j), Math.min(getProbability(loc) + 0.2, 0.7));
            }
        }
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
