package Assignment4;

public class ProbabilityMap{
	public Square[][] map;
	private double towerAccuracy = .75;
	private int towerRange = 5;
	private static int SHOOTING_RANGE = 5;
	private static int TREE_RANGE = 2;
	
	public ProbabilityMap(int x, int y){
		map = new Square[x][y];
		for(int i = 0; i<y; i++){
			// Initialize whole map to 0.05 probability
			for(int j = 0; j<x; j++){
				map[j][i] = new Square();
			}
		}
	}
	
	public double getProbability(Location loc){
		return map[loc.x][loc.y].getProbability();
	}
	public Square getSquare(Location loc){
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
	
	public void towerSeen(Location loc){
		map[loc.x][loc.y].towerSeen();
	}
	
	public void treeSeen(Location loc){
		map[loc.x][loc.y].treeSeen();
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
				map[current.x][current.y].treeSeenInVicinity();
                        }
                }
		return;
	}
	
	public void emptySquare(Location loc){
		map[loc.x][loc.y].emptySquareSeen();
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
				map[i][j].peasantShotNearby();
                        }
                }
	}
	
	public void wasNotShot(Location loc){
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
                                map[i][j].peasantNotShotNearby();
                        }
                }
	}
	
	public void print(){
		for(int i = 0; i< map[0].length; i++){
			String line = "";
			for(int j = 0; j< map.length; j++){
				line = line + map[j][i].getProbability() + ", ";
			}
			System.out.println(line);
		}
	}
}
