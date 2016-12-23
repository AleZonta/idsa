package nl.tno.idsa.framework.simulator;

import lgds.load_track.LoadIDSATrack;
import lgds.load_track.LoadTrack;
import lgds.load_track.Traces;
import lgds.people.Agent;
import lgds.simulator.SimulatorInterface;
import lgds.trajectories.Trajectories;
import lgds.trajectories.Trajectory;
import nl.tno.idsa.framework.agents.TrajectoryAgent;
import nl.tno.idsa.framework.config.ConfigFile;
import nl.tno.idsa.framework.population.Gender;
import nl.tno.idsa.framework.population.HouseholdRoles;
import nl.tno.idsa.framework.population.HouseholdTypes;
import nl.tno.idsa.framework.potential_field.*;
import nl.tno.idsa.framework.potential_field.points_of_interest.POI;
import nl.tno.idsa.framework.potential_field.performance_checker.PerformanceChecker;
import nl.tno.idsa.framework.potential_field.performance_checker.PersonalPerformance;
import nl.tno.idsa.framework.world.Point;
import nl.tno.idsa.framework.world.World;
import org.apache.commons.math3.ml.clustering.Cluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Created by alessandrozonta on 27/09/16.
 * Trajectory Simulator that implements lgds simulator interface
 */
public class TrajectorySim implements SimulatorInterface {
    private Traces storage; //class that loads the track from file
    private Map<TrajectoryAgent, PotentialField> partecipantPot; //list of all the agents participating into the simulation //Every tracked agent need its own potential field. I will deep copy the base instance for all the tracked agents and I will store them here. Save PotentialField with the id of the agent tracked
    private Trajectories tra; //keep track of all the trajectories
    private PotentialField pot; //This is the base instance of the pot
    private final PerformanceChecker performance; //keep track of the performance of the simulator
    private Boolean oldWorld; //using this variable only to remmebre if i am loading the simulator world for pathplanning
    private Boolean clusteredPOI; //true I ask for clustered POI, False not
    private Boolean smoother; //Am I using the smoother?
    private Integer lag; //Lag for the smoother

    /**
     * default constructor
     */
    public TrajectorySim(Integer selector, Boolean smoother, Integer lag){
        if (selector == 0){
            this.storage = new LoadIDSATrack();
        }else{
            this.storage = new LoadTrack();
        }
        this.pot = null;
        this.performance = new PerformanceChecker();
        //retrieve all the tracks from file
        this.tra = this.storage.loadTrajectories();
        this.oldWorld = Boolean.FALSE;
        this.clusteredPOI = null;

        this.partecipantPot = new ConcurrentHashMap<>();
        this.smoother = smoother;
        this.lag = lag;
    }

    /**
     * getter for the agent participating at the simulation
     * @return list of agent that I am going to simulate
     */
    public List<Agent> getParticipant() {
        return null;
    }

    /**
     * getter for the agent participating at the simulation
     * @return list of agent that I am going to simulate
     */
    public List<TrajectoryAgent> getLocalParticipant(){
        return new ArrayList<>(this.partecipantPot.keySet());
    }

    /**
     * getter for all the trajectories
     * @return all the trajectories
     */
    public Trajectories getTra() {
        return tra;
    }

    /**
     * Initialise the class loading all the tracks from file and building the list of Agent
     * Compute the point of interest
     * initialise the potential field and all it needs to work
     * @param number maximum number of agent to load
     */
    @Override
    public void init(Integer number){
        //shuffle it
        this.tra.shuffle();
        //analysing the trajectories
        this.tra.analiseAndCheckTrajectory();
        //now I am choosing only the first $number trajectories
        System.out.println("Selecting trajectories...");
        List<Trajectory> actualTrajectories = this.tra.getTrajectories().stream().limit(number).collect(Collectors.toList());
        //prepare the id of the agent
        List<Integer> id = new ArrayList<>();
        //check the number
        if(number > actualTrajectories.size()) number = actualTrajectories.size() - 1;
        for(int i = 0; i < number; i++) id.add(i);

        //what about poi? I should generate POI for them. Now I should generate some randomly than I should
        //find a way to find them from the poi
        //I can select how many POI use here. Lets add more
        System.out.println("Computing POIs...");
        this.tra.computePOIs(number);

        System.out.println("Creating agents...");
        //create the agents
        id.stream().forEach(integer -> {
            HouseholdTypes hhType = HouseholdTypes.SINGLE;
            Gender gender = Gender.FEMALE;
            double age = ThreadLocalRandom.current().nextDouble(0, 100);
            TrajectoryAgent agent = new TrajectoryAgent(actualTrajectories.get(integer), this.storage,age ,gender, hhType, HouseholdRoles.SINGLE,2016, this.smoother, this.lag);
            System.out.println("Connecting the potential field to the people tracked...");
            //now i should load all what I need for the potential field
            this.loadControllers(agent);
        });
    }


    /**
     * run the simulation. Move all the agents until all of them reach the end of their trajectory
     */
    @Override
    public void run(){
        //run the simulator
        while(this.partecipantPot.size() != 0){
            this.partecipantPot.keySet().parallelStream().forEach(TrajectoryAgent::doStep);
        }
        System.out.println("End simulating procedure...");

    }

    /**
     * Wrap the init and the run to let run a fixed amount of people together
     * @param max_allowed maximun number allowed to run in parallel
     * @param number total number of people tracked
     */
    public void init_and_run(Integer max_allowed, Integer number){
        //shuffle it
        this.tra.shuffle();
        //analysing the trajectories
        this.tra.analiseAndCheckTrajectory();
        //now I am choosing only the first $number trajectories
        System.out.println("Selecting trajectories...");
        List<Trajectory> actualBigTrajectories = this.tra.getTrajectories().stream().limit(number).collect(Collectors.toList());

        //The number of people running
        Integer effectiveCounter = max_allowed;
        //max_allowed will be always present in the pool
        List<Trajectory> actualTrajectories = actualBigTrajectories.subList(0, max_allowed);
        //prepare the id of the agent
        List<Integer> id = new ArrayList<>();
        for(int i = 0; i < max_allowed; i++) id.add(i);

        //what about poi? I should generate POI for them. Now I should generate some randomly than I should
        //find a way to find them from the poi
        //I can select how many POI use here. Lets add more
        System.out.println("Computing POIs...");
        this.tra.computePOIs(actualBigTrajectories);

        System.out.println("Creating agents...");
        //create the agents
        List<Trajectory> finalActualTrajectories = actualTrajectories;
        id.stream().forEach(integer -> {
            HouseholdTypes hhType = HouseholdTypes.SINGLE;
            Gender gender = Gender.FEMALE;
            double age = ThreadLocalRandom.current().nextDouble(0, 100);
            TrajectoryAgent agent = new TrajectoryAgent(finalActualTrajectories.get(integer), this.storage, age ,gender, hhType, HouseholdRoles.SINGLE,2016, this.smoother, this.lag);
            System.out.println("Connecting the potential field to the initialised person...");
            //load controllers
            this.loadControllers(agent);
        });

        //run the simulator
        while(this.partecipantPot.size() != 0){
            this.partecipantPot.keySet().parallelStream().forEach(TrajectoryAgent::doStep);

            //check if the partecipant number is max_allowed or not.. if there are fewer partecipants I will add one
            if((this.partecipantPot.size() < max_allowed) && (effectiveCounter < number)){
                Integer numberThatINeed = max_allowed - this.partecipantPot.size();
                Integer sumOfTheTwo = effectiveCounter + numberThatINeed;
                if (sumOfTheTwo > number){
                    sumOfTheTwo = number;
                }
                actualTrajectories = actualBigTrajectories.subList(effectiveCounter, sumOfTheTwo);
                id = new ArrayList<>();
                for(int i = 0; i < numberThatINeed; i++) id.add(i);
                effectiveCounter = sumOfTheTwo;
                List<Trajectory> finalActualTrajectories1 = actualTrajectories;
                id.stream().forEach(integer -> {
                    HouseholdTypes hhType = HouseholdTypes.SINGLE;
                    Gender gender = Gender.FEMALE;
                    double age = ThreadLocalRandom.current().nextDouble(0, 100);
                    TrajectoryAgent agent = new TrajectoryAgent(finalActualTrajectories1.get(integer), this.storage, age ,gender, hhType, HouseholdRoles.SINGLE,2016, this.smoother, this.lag);
                    System.out.println("Connecting the potential field to the initialised person...");
                    //load controllers
                    this.loadControllers(agent);
                });
            }
        }
        System.out.println("End simulating procedure...");


    }

    /**
     * This private method loads all the controller to the agent tracked
     * It loads the potential field, it sets the tracking system
     * Create the personal performance for the agent
     * It sets the POIs to the potential field
     * Sets the observer for the agent
     * Add the potential field to the global list
     * @param trajectoryAgent
     */
    private void loadControllers(TrajectoryAgent trajectoryAgent){
        try {
            //new agent tracked new potential field for him
            PotentialField fieldForTheTrackedAgent = this.pot.deepCopy();
            TrackingSystem trackingForTheTrackedAgent = new TrackingSystem(fieldForTheTrackedAgent);
            if (this.oldWorld){
                fieldForTheTrackedAgent.setWorld(this.pot.getUpdateRule().retIdsaWorld().deepCopy());
            }

            PersonalPerformance personalPerformance = new PersonalPerformance(this.pot.getConfig().getSelectorSourceTracks()); //prepare class for personal performance
            fieldForTheTrackedAgent.setPerformance(personalPerformance); //set personal performance on the field
            this.performance.addPersonalPerformance(trajectoryAgent.getId(),personalPerformance); //connect performance with id person and put them together in a list

            fieldForTheTrackedAgent.setTrajectorySimReference(this);
            fieldForTheTrackedAgent.setTrackedAgent(trajectoryAgent);
            //if it is false I am using normal POI
            if (!this.clusteredPOI) {
                fieldForTheTrackedAgent.setPointsOfInterest(this.translatePOI(this.tra.getListOfPOIs())); //set the POIs obtained from the GPS trajectories
            }else {
                //In this case I am using clustered POI
                fieldForTheTrackedAgent.setPointsOfInterest(this.translateClusterPOI(this.tra.getListOfPOIsClustered())); //set the POIs obtained from the GPS trajectories
            }
            trajectoryAgent.deleteObservers(); //delete old observers
            trajectoryAgent.setTracked(trackingForTheTrackedAgent, null); //set the observer to this point

            //Add potential field and tracking system to their list
            this.partecipantPot.put(trajectoryAgent, fieldForTheTrackedAgent);
            System.out.println("Loaded Potential Field for person number " + this.partecipantPot.size() + "...");

        } catch (EmptyActivityException | ActivityNotImplementedException e) {
            //No planned activity. I do not need to do anything. The exception doesn't add the agent to the list
            e.printStackTrace();
        }
    }

    /**
     * init the basic istanc eof the potential field
     * @param conf config file
     * @param degree parameter that the update rule needs -> angle degree -> load by file
     * @param s1 parameter that the update rule needs -> load by file
     * @param s2 parameter that the update rule needs -> load by file
     * @param w1 parameter that the update rule needs -> load by file
     * @param w2 parameter that the update rule needs -> load by file
     * @param name parameter that the update rule needs -> name of the experiment
     * @param experiment  parameter that the update rule needs -> repetition of the experiment
     */
    public void initPotentialField(ConfigFile conf, Double degree, Double s1, Double s2, Double w1, Double w2, String name, String experiment, World oldWorld){
        //set in word the dimension of the word and the area of the word set to null
        World world;
        if(oldWorld == null){
            world = new World();
            world.applyGeoRoot(this.tra.getUtmRoot().getLatitude(),this.tra.getUtmRoot().getLongitude(),this.tra.getWhWorld().getLatitude(),this.tra.getWhWorld().getLongitude());
        }else{
            world = oldWorld;
            this.oldWorld = Boolean.TRUE;
        }
        //I do not think I need something else inside the world for running the potential field
        this.pot = new PotentialField(world, conf, degree , s1, s2, w1 , w2, name, experiment);

        //set cluster
        this.clusteredPOI = conf.getPOIsAreClustered();
    }


    /**
     * remove the person selected from the list of tracked people and saving its performance
     * If the list is empty stop the simulation and compute the final performance
     * @param agentId Id of the person to remove
     */
    public void removeFromTheLists(Long agentId){
        TrajectoryAgent agent = this.partecipantPot.keySet().stream().filter(trajectoryAgent -> trajectoryAgent.getId() == agentId).findFirst().get();
        this.partecipantPot.remove(agent);
        System.out.println("Removing tracked agent from the list. Remaining agents -> " + this.partecipantPot.size() + "...");
    }

    /**
     * Convert the POI from the lgds library to the POI used in the simulator
     * @param oldList List of POI in lgds version
     * @return List of POI in idsa version
     */
    private List<POI> translatePOI(List<lgds.POI.POI> oldList){
        List<lgds.POI.POI> appoList = new ArrayList<>();
        //Check if the POIs are inside the boundaries
        oldList.stream().forEach(poi -> {
            if(this.pot.getPathFinder() != null) {
                if (this.pot.getPathFinder().isContained(poi.getLocation())) {
                    appoList.add(poi);
                }
            }else{
                appoList.add(poi);
            }
        });
        //deepcopy element
        List<POI> realList = new ArrayList<>();
        appoList.stream().forEach(poi -> realList.add(new POI(new Point(poi.getLocation().getLatitude(),poi.getLocation().getLongitude()))));
        return realList;
    }

    /**
     * Convert the clustered POI from the lgds library to the POI used in the simulator
     * @param list List of cluster
     * @return List of POI in idsa version
     */
    private List<POI> translateClusterPOI(List<? extends Cluster<lgds.POI.POI>> list){
        List<POI> oldList = new ArrayList<>();
        list.stream().forEach(cluster -> {
            oldList.add(new POI(cluster.getPoints()));
        });
        List<POI> realList = new ArrayList<>();
        //Check if the POIs are inside the boundaries
        oldList.stream().forEach(poi -> {
            if(this.pot.getPathFinder() != null) {
                if (this.pot.getPathFinder().isContained(new lgds.trajectories.Point(poi.getArea().getPolygon().getCenterPoint().getX(),poi.getArea().getPolygon().getCenterPoint().getY()))){
                    realList.add(poi);
                }
            }else{
                realList.add(poi);
            }
        });
        return realList;
    }

}
