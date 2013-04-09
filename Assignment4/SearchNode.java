package Assignment4;
public class SearchNode implements Comparable<SearchNode>{
	
	public Location Loc;
	public ProbabilityMap Map;
	public int Distance;
	public int Cost;
	public SearchNode Parent;

	public SearchNode(Location start, int dist, int cost, ProbabilityMap map, SearchNode parent){
		Loc = start;
		Distance = dist;
		Cost = cost;
		Map = map;
		Parent = parent;
	}

	public Square getSquare(){
		return Map.map[Loc.x][Loc.y];
	}

	public double getHeuristic(){
		return Cost + Distance;
	}

	public boolean hasBeenSeen(){
		return getSquare().hasBeenSeen;
	}

	public int compareTo(SearchNode node){
		if(getHeuristic() < node.getHeuristic()){
			return 1;
		}
		else if(getHeuristic() > node.getHeuristic()){
			return -1;
		}
		else{
			return 0;
		}
	}

	public boolean equals(SearchNode node){
		return Loc.equals(node.Loc);
	}

	public void update(SearchNode node){
		if(Cost > node.Cost){
			Parent = node.Parent;
			Cost = node.Cost;
		}
	}
}