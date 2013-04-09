package Assignment4;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Iterator;
import java.io.*;
import java.util.PriorityQueue;		

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.action.ActionType;
import edu.cwru.sepia.action.TargetedAction;
import edu.cwru.sepia.environment.model.history.History;
import edu.cwru.sepia.environment.model.state.ResourceType;
import edu.cwru.sepia.environment.model.state.ResourceNode.Type;
import edu.cwru.sepia.environment.model.state.State.StateView;
import edu.cwru.sepia.environment.model.state.Template.TemplateView;
import edu.cwru.sepia.environment.model.state.Unit.UnitView;
import edu.cwru.sepia.experiment.Configuration;
import edu.cwru.sepia.experiment.ConfigurationValues;
import edu.cwru.sepia.agent.Agent;
import edu.cwru.sepia.environment.model.state.ResourceNode.Type;
import edu.cwru.sepia.environment.model.state.ResourceNode.ResourceView;
import edu.cwru.sepia.util.Direction;

/**
 * This agent will first collect gold to produce a peasant,
 * then the two peasants will collect gold and wood separately until reach goal.
 * @author Feng
 *
 */
public class ProbabilityAgent extends Agent {
	private static final long serialVersionUID = -4047208702628325380L;
	private static final Logger logger = Logger.getLogger(ProbabilityAgent.class.getCanonicalName());

	private int goldRequired = 2000;
	private int step;
	private StateView currentState;
	private ProbabilityMap probMap;
	private Location goldMine;
	private Location townHall;
	private Map<Integer, Integer> healthMap;
	
	public ProbabilityAgent(int playernum, String[] arguments) {
		super(playernum);
	}

	@Override
	public Map<Integer, Action> initialStep(StateView newstate, History.HistoryView statehistory) {
		step = 0;
		currentState = newstate;
		goldMine = new Location(currentState.getXExtent(), currentState.getYExtent());
		townHall = new Location(0, currentState.getYExtent());
		probMap = new ProbabilityMap(currentState.getXExtent(), currentState.getYExtent());
		healthMap = new HashMap<Integer, Integer>();
		setHealthMap();
		return middleStep(newstate, statehistory);
	}

	@Override
	public Map<Integer,Action> middleStep(StateView newState, History.HistoryView statehistory) {
		step++;
		currentState = newState;
		updatePeasantEvents(); // Have they been shot
		updateLocations(); // What can they now see
		System.out.println("Map");
		probMap.print();
		System.out.println("\n");
		Map<Integer,Action> actions = getPeasantActions(getAllPeasants());
		return actions;
	}

	private Map<Integer, Action> getPeasantActions(List<UnitView> peasants){
		Map<Integer,Action> actions = new HashMap<Integer,Action>();
		// Find the action that moves peasant closer with least likelihood of getting shot
		for(UnitView peasant : peasants){
			Location goal = getGoal(peasant);
			System.out.println(goal);
			Location loc = getLocation(peasant);
			List<Location> neighbors = getNeighbors(loc);
			// If it is next to the goal node
			if(neighbors.contains(goal)){
				// Town Hall
				UnitView unit = getTownhall();
				Location loc1 = new Location(unit.getXPosition(), unit.getYPosition());
				if(goal.equals(loc1)){
					actions.put(peasant.getID(), Action.createPrimitiveDeposit(peasant.getID(), Direction.getDirection(0-loc.x, currentState.getYExtent() - loc.y)));
				}
				// Gold Mine
				else{
					actions.put(peasant.getID(), Action.createPrimitiveGather(peasant.getID(), Direction.getDirection(currentState.getXExtent()-loc.x, 0-loc.y)));
				}
			}
			else{
				Location bestLocation = AStar(loc, goal).get(0);
				// Create action
				actions.put(peasant.getID(), Action.createPrimitiveMove(peasant.getID(), Direction.getDirection(bestLocation.x-loc.x, bestLocation.y - loc.y)));
			}
			
			

		}
		return actions;
	}

	private List<Location> AStar(Location start, Location end){
		return AStar(new SearchNode(start, dist(start.x, start.y, end.x, end.y), 0, probMap), new SearchNode(end, 0, 0, probMap));
	}

	private List<Location> AStar(SearchNode start, SearchNode end){
		PriorityQueue<SearchNode> openList = new PriorityQueue<SearchNode>();
		List<SearchNode> closedList = new List<SearchNode>();
		List<Location> path = new List<Location>();
		openList.add(start);
		SearchNode bestNode = start;
		while(openList.size() > 0){
			SearchNode head = openList.peek();
			openList.remove(head);
			if(head.getHeuristic() == 0){
				return generateList(head);
			}
			if(head.getHeuristic() < bestNode.getHeuristic()){
				bestNode = head;
			}
			List<Location> neighbors = getNeighbors(head.getLocation());
			for(Location neighbor : neighbors){
				if(currentState.canSee(neighbor.x, neighbor.y)){
					SearchNode node = new SearchNode(neighbor, dist(neighbor.x, neighbor.y, end.x, end.y), neighbor.Cost + 1, probMap);
					if(openList.contains(node)){
						updateNode(node, openList);
					}
					else if(!closedList.contains(node)){
						openList.add(node);
					}
				}
			}
			closedList.add(head);
		}
		return generatePath(bestNode);
	}

	private List<Location> getNeighbors(Location loc){
		List<Location> neighbors = new ArrayList<Location>();
		for(int i = -1; i<=1; i++){
			for(int j = -1; j<=1; j++){
				if( !(i == 0 && j == 0)){
					int x = loc.x + j;
					int y = loc.y + i;
					if(currentState.inBounds(x,y){
						neighbors.add(new Location(x,y));
					}
				}
			}
		}
		return neighbors;
	}
	
	private boolean canMove(Location loc){
		return !(currentState.isUnitAt(loc.x, loc.y) || currentState.isResourceAt(loc.x,loc.y));
	}

	private Location getLocation(UnitView unit){
		return new Location(unit.getXPosition(), unit.getYPosition());
	}
	
	private Location getGoal(UnitView peasant){
		// Deposit at town hall
		if(peasant.getCargoAmount() > 0){
			UnitView unit = getTownhall();
			return new Location(unit.getXPosition(), unit.getYPosition());
		}
		// Go to gold mine
		else{
			ResourceView gold = getGoldMine();
			if(gold == null){
				return new Location(currentState.getXExtent(), 0);
			}
			else{
				return new Location(gold.getXPosition(), gold.getYPosition());
			}
		}
	}
	
	private void updatePeasantEvents(){
		List<UnitView> newViews = getAllPeasants();
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		for(UnitView newUnit : newViews){
			boolean isContained = false;
			for(Integer unitId : healthMap.keySet()){
				if(unitId == newUnit.getID()){	
					isContained = true;	
					if(healthMap.get(unitId) != newUnit.getHP()){
						probMap.wasShot(new Location(newUnit.getXPosition(), newUnit.getYPosition()));
					}
					else {
						probMap.wasNotShot(new Location(newUnit.getXPosition(), newUnit.getYPosition()));
					}
					if(newUnit.getHP() >0){
						map.put(unitId, newUnit.getHP());
					}
				}
			}
			if(!isContained){
				map.put(newUnit.getID(), newUnit.getHP());
			}
		}
		healthMap = map;
	}

	private void updateLocations(){
		List<Location> newViewableLocations = getNewViewableLocations();
		List<UnitView> units = currentState.getAllUnits();
		List<ResourceView> trees = currentState.getResourceNodes(Type.TREE);
		for(UnitView unit : units){
			Iterator<Location> itr = newViewableLocations.iterator();
			while(itr.hasNext()){
				Location loc = itr.next();
				if(loc.equals(unit.getXPosition(), unit.getYPosition())){
					if(unit.getTemplateView().getName().equals("GuardTower") || unit.getTemplateView().getName().equals("ScoutTower")){
						probMap.towerSeen(loc);
						itr.remove();
					}
				}
			}
		}
		
		for(ResourceView tree : trees){
			Iterator<Location> itr = newViewableLocations.iterator();
			while(itr.hasNext()){
				Location loc = itr.next();
				if(loc.equals(tree.getXPosition(), tree.getYPosition())){
					probMap.treeSeen(loc);
					itr.remove();
				}
			}
		}
		
		for(Location loc : newViewableLocations){
			probMap.emptySquare(loc);
		}
	}
	
	private List<Location> getNewViewableLocations(){
		List<Location> locations = new ArrayList<Location>();
		for(int i = 0; i < currentState.getYExtent(); i++){
			for(int j = 0; j < currentState.getXExtent(); j++){
				if(currentState.canSee(j, i)){
					Location loc = new Location(j, i);
					if(probMap.getProbability(loc) != 0 && probMap.getProbability(loc) != 1){
						locations.add(loc);
					}				
				}
			}
		}		
		return locations;
	}

	private void setHealthMap(){
		List<UnitView> peasants = getAllPeasants();
		for(UnitView peasant : peasants){
			healthMap.put(peasant.getID(), peasant.getHP());
		}
	}

	private List<UnitView> getAllPeasants(){
		List<UnitView> peasants = new ArrayList<UnitView>();
		List<UnitView> units = currentState.getUnits(playernum);
		for(UnitView unit : units){
			if(unit.getTemplateView().getName().equals("Peasant")){
				peasants.add(unit);
			}
		}
		return peasants;
	}
	
	private UnitView getTownhall(){
		for(UnitView unit : currentState.getUnits(playernum)){
			if(unit.getTemplateView().getName().equals("TownHall")){
				return unit;
			}
		}
		return null;
	}

	private ResourceView getGoldMine(){
		for(ResourceView resource : currentState.getResourceNodes(Type.GOLD_MINE)){
			return resource;
		}
		return null;
	}

	private int dist(int x1, int y1, int x2, int y2){
		return (Math.abs(x1 - x2) + Math.abs(y1 - y2));
	}
	
	@Override
	public void terminalStep(StateView newstate, History.HistoryView statehistory) {
		step++;
		if(logger.isLoggable(Level.FINE))
		{
			logger.fine("=> Step: " + step);
		}
		
		int currentGold = newstate.getResourceAmount(0, ResourceType.GOLD);
		
		if(logger.isLoggable(Level.FINE))
		{
			logger.fine("Current Gold: " + currentGold);
		}
		if(logger.isLoggable(Level.FINE))
		{
			logger.fine("Congratulations! You have finished the task!");
		}
		System.out.println("=> Step: " + step);
		System.out.println("Current Gold: " + currentGold);
		System.out.println("Congratulations! You have finished the task!");
	}

	public static String getUsage() {
		return "Gathers 2000 gold losing as few peasants as possible";
	}
	
	@Override
	public void savePlayerData(OutputStream os) {
		//this agent lacks learning and so has nothing to persist.
		
	}
	
	@Override
	public void loadPlayerData(InputStream is) {
		//this agent lacks learning and so has nothing to persist.
	}
}
