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
	
	public ProbabilityAgent(int playernum, String[] arguments) {
		super(playernum);
	}

	@Override
	public Map<Integer, Action> initialStep(StateView newstate, History.HistoryView statehistory) {
		step = 0;
		currentState = newstate;
		probMap = new ProbabilityMap(currentState.getXExtent(), currentState.getYExtent());
		return middleStep(newstate, statehistory);
	}

	@Override
	public Map<Integer,Action> middleStep(StateView newState, History.HistoryView statehistory) {
		step++;
		Map<Integer,Action> builder = new HashMap<Integer,Action>();
		updateLocations();
		//probMap.print();
		currentState = newState;
		return builder;
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
					if(unit.getTemplateView().getName().equals("GuardTower")){
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
