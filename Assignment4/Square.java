package Assignment4;

public class Square{
	boolean hasBeenSeen;
	boolean isTower;
	boolean isTree;
	int timesShot;
	int opportunities;
	int treesNearby;
	double probability;
	public Square(){
		hasBeenSeen=false;
		isTower = false;
		isTree = false;
		probability = .05;
		treesNearby=0;
		
	}
	
	public double getProbability(){
		return probability;
	}
	
	public void towerSeen(){
		hasBeenSeen=true;
		isTower=true;
		updateProbability();
	}
	public void treeSeen(){
		hasBeenSeen=true;
		isTree=true;
		updateProbability();
	}
	public void emptySquareSeen(){
		hasBeenSeen=true;
		updateProbability();

	}
	public boolean isSeen(){
		return hasBeenSeen;
	}
	public boolean isTree(){
                return isTree;
        }
	public boolean isTower(){
                return isTower;
        }
	public void updateProbability(){
		if(hasBeenSeen){
			if(isTower){
				probability = 1.0;
			}
			else{
				probability = 0.0;
			}
			return;
		}	
		probability = 0.5;
		probability += Math.min(.15 * treesNearby, .4);
		probability += Math.min(.2 * timesShot, .3);
		probability -= Math.min((opportunities-timesShot)*.1, .5);
		probability = Math.max(0, probability);
		
	}
	public void treeSeenInVicinity(){
		treesNearby ++;
		updateProbability();
	}
	public void peasantNotShotNearby(){
		opportunities++;
		updateProbability();
	}
	public void peasantShotNearby(){
		timesShot++;
		opportunities++;
		updateProbability();
	}
}
