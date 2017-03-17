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
import nl.tno.idsa.framework.potential_field.ActivityNotImplementedException;
import nl.tno.idsa.framework.potential_field.EmptyActivityException;
import nl.tno.idsa.framework.potential_field.PotentialField;
import nl.tno.idsa.framework.potential_field.TrackingSystem;
import nl.tno.idsa.framework.potential_field.performance_checker.PerformanceChecker;
import nl.tno.idsa.framework.potential_field.performance_checker.PersonalPerformance;
import nl.tno.idsa.framework.potential_field.points_of_interest.POI;
import nl.tno.idsa.framework.world.Point;
import nl.tno.idsa.framework.world.World;
import nl.tno.idsa.viewer.view.UpdateGUI;
import nl.tno.idsa.viewer.view.View;
import org.apache.commons.math3.ml.clustering.Cluster;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
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
    private Integer morePOIs; //More than the normal number of POIs?
    private View view; //implementing the lgds.View
    private final Integer trackedPersonNumber; //number of person tracked
    private final Boolean selectPerson; //Am i selecting a person


    /**
     * default constructor
     */
    public TrajectorySim(Integer selector, Boolean smoother, Integer lag, Integer morePOIs, Boolean gui, Double alpha, Integer trackedPersonNumber, Boolean person){
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

        this.morePOIs = morePOIs;

        if(gui){
            this.view = new View();
            this.view.setAlpha(alpha);
        }else{
            this.view = null;
        }
        this.trackedPersonNumber = trackedPersonNumber;
        this.selectPerson = person;
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
        Boolean morePOIsInTotal = Boolean.FALSE;
        if(this.morePOIs > 0){
            morePOIsInTotal = Boolean.TRUE;
        }
        this.tra.computePOIs(number, morePOIsInTotal, this.morePOIs);

        System.out.println("Creating agents...");
        //create the agents
        id.stream().forEach(integer -> {
            HouseholdTypes hhType = HouseholdTypes.SINGLE;
            Gender gender = Gender.FEMALE;
            double age = ThreadLocalRandom.current().nextDouble(0, 100);
            TrajectoryAgent agent = new TrajectoryAgent(actualTrajectories.get(integer), this.storage,age ,gender, hhType, HouseholdRoles.SINGLE,2016, this.smoother, this.lag);
            System.out.println("Connecting the potential field to the people tracked...");
            //now i should load all what I need for the potential field
            this.loadControllers(agent, new Point(actualTrajectories.get(integer).getLastPoint().getLatitude(),actualTrajectories.get(integer).getLastPoint().getLongitude()));
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
     * @param max_allowed maximum number allowed to run in parallel
     * @param number total number of people tracked
     */
    public void init_and_run(Integer max_allowed, Integer number){
        //shuffle it
        this.tra.shuffle();
        //analysing the trajectories
        this.tra.analiseAndCheckTrajectory();
        //now I am choosing only the first $number trajectories
        System.out.println("Selecting trajectories...");
        List<Trajectory> actualBigTrajectories;
        if(this.selectPerson){
            actualBigTrajectories = new ArrayList<>();
            actualBigTrajectories.add(this.tra.getTrajectories().get(this.trackedPersonNumber));
        }else{
            actualBigTrajectories = this.tra.getTrajectories().stream().limit(number).collect(Collectors.toList());
        }


        //check If I have fewer trajectories than the maximum number
        if(actualBigTrajectories.size() < number) {
            number = actualBigTrajectories.size();
            System.out.println("Reducing maximum number due to limitation on trajectories number -> " + number.toString());
        }

        //check that max_allowed is not smaller than number
        if(number < max_allowed){
            max_allowed = number;
        }
        //The number of people running
        Integer effectiveCounter = max_allowed;
        //max_allowed will be always present in the pool
        Integer startPoint = 0;
        List<Trajectory> actualTrajectories = actualBigTrajectories.subList(0, max_allowed);
        //prepare the id of the agent
        List<Integer> id = new ArrayList<>();
        for(int i = 0; i < max_allowed; i++) id.add(i);

        //what about poi? I should generate POI for them. Now I should generate some randomly than I should
        //find a way to find them from the poi
        //I can select how many POI use here. Lets add more
        System.out.println("Computing POIs...");
        Boolean morePOIsInTotal = Boolean.FALSE;
        if(this.morePOIs != 0){
            //also negative numbers are okay, not too negative though
            morePOIsInTotal = Boolean.TRUE;
        }
        this.tra.computePOIs(actualBigTrajectories, morePOIsInTotal, this.morePOIs);

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
            this.loadControllers(agent, new Point(finalActualTrajectories.get(integer).getLastPoint().getLatitude(),finalActualTrajectories.get(integer).getLastPoint().getLongitude()));
        });

        //show the gui
        if (this.view != null) {
            System.out.println("Loading the Map on the GUI...");
            this.view.showMap();
        }

        //run the simulator
        while(this.partecipantPot.size() != 0){
            this.partecipantPot.keySet().parallelStream().forEach(TrajectoryAgent::doStep);

            if(this.view != null){
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //check if the partecipant number is max_allowed or not.. if there are fewer partecipants I will add one
            if((this.partecipantPot.size() < max_allowed) && (effectiveCounter < number)){
                //counting how many free spot do I have
                Integer numberThatINeed = max_allowed - this.partecipantPot.size();
                //summing the actual counter plus the number that i need to be sure I am not using more than the number that i need
                Integer sumOfTheTwo = effectiveCounter + numberThatINeed;
                if (sumOfTheTwo > number){
                    Integer difference = sumOfTheTwo - number;
                    sumOfTheTwo = number;
                    numberThatINeed -= difference;
                }
                //retrieving all the trajectory that remains to analise
                actualTrajectories = actualBigTrajectories.subList(effectiveCounter, sumOfTheTwo);
                id = new ArrayList<>();
                for(int i = 0; i < numberThatINeed; i++) id.add(i);
                effectiveCounter = sumOfTheTwo;
                List<Trajectory> finalActualTrajectoriez1 = actualTrajectories;
                id.stream().forEach(integer -> {
                    HouseholdTypes hhType = HouseholdTypes.SINGLE;
                    Gender gender = Gender.FEMALE;
                    double age = ThreadLocalRandom.current().nextDouble(0, 100);

                    TrajectoryAgent agent = new TrajectoryAgent(finalActualTrajectoriez1.get(integer), this.storage, age, gender, hhType, HouseholdRoles.SINGLE, 2016, this.smoother, this.lag);
                    System.out.println("Connecting the potential field to the initialised person...");
                    //load controllers
                    this.loadControllers(agent, new Point(finalActualTrajectories.get(integer).getLastPoint().getLatitude(),finalActualTrajectories.get(integer).getLastPoint().getLongitude()));


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
     * @param trajectoryAgent the agent to track
     * @param endpoint last point of the trajectory
     */
    private void loadControllers(TrajectoryAgent trajectoryAgent, Point endpoint){
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
                List<POI> pois = this.translatePOI(this.tra.getListOfPOIs());
                //since i deleted some poi i need to check if the target is there
                if (this.morePOIs < 0) {
                    Integer numberToRemove = Math.abs(this.morePOIs);
                    if(pois.size() < numberToRemove){
                        numberToRemove = numberToRemove - pois.size();
                    }

                    for(int i = 0; i < numberToRemove; i ++ ){
                        Random rn = new Random();
                        int numb = rn.nextInt(pois.size());
                        if(!pois.get(numb).contains(endpoint)){
                            pois.remove(numb);
                        }
                    }
                }
                fieldForTheTrackedAgent.setPointsOfInterest(pois); //set the POIs obtained from the GPS trajectories
            }else {
                List<POI> pois = this.translateClusterPOI(this.tra.getListOfPOIsClustered());
                //since i deleted some poi i need to check if the target is there
                if (this.morePOIs < 0) {
                    Integer numberToRemove = Math.abs(this.morePOIs);

                    if(pois.size() < numberToRemove){
                        numberToRemove = numberToRemove - pois.size();
                    }

                    for(int i = 0; i < numberToRemove; i ++ ){
                        Random rn = new Random();
                        int numb = rn.nextInt(pois.size());
                        if(!pois.get(numb).contains(endpoint)){
                            pois.remove(numb);
                        }
                    }
                }
                //In this case I am using clustered POI
                fieldForTheTrackedAgent.setPointsOfInterest(pois); //set the POIs obtained from the GPS trajectories
            }
            //set POIs to the map
            if (this.view != null) {
                this.view.setListPoints(fieldForTheTrackedAgent.getPointsOfInterest());
                //Point center = new Point(this.tra.getUtmRoot().getLatitude() + this.tra.getWhWorld().getLatitude() / 2 , this.tra.getUtmRoot().getLongitude() + this.tra.getWhWorld().getLongitude() / 2);
                Point center = new Point(39.905757, 116.392392);
                this.view.setMapFocus(center);
            }


            trajectoryAgent.deleteObservers(); //delete old observers
            trajectoryAgent.setTracked(trackingForTheTrackedAgent, null); //set the observer to this point
            //set the observer for the gui
            if (this.view != null) {
                UpdateGUI updateGUI = new UpdateGUI(this.view);
                fieldForTheTrackedAgent.addObserver(updateGUI);
            }

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
     * @param h parameter that the update rule needs -> angle h -> load by file
     * @param z1 parameter that the update rule needs -> load by file
     * @param s2 parameter that the update rule needs -> load by file
     * @param z2 parameter that the update rule needs -> load by file
     * @param w2 parameter that the update rule needs -> load by file
     * @param name parameter that the update rule needs -> name of the experiment
     * @param experiment  parameter that the update rule needs -> repetition of the experiment
     */
    public void initPotentialField(ConfigFile conf, Double h, Double z1, Double s2, Double z2, Double w2, String name, String experiment, World oldWorld){
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
        this.pot = new PotentialField(world, conf, h, z1, z2, s2, w2, name, experiment);

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


    /**
     * Return distance between two points using Distance measure used for clustering
     * @param source point
     * @param destination point
     * @return distance between two points in Double
     */
    public Double returnDistance(Point source, Point destination){
        double[] sourcePoints = new double[] { source.getX(), source.getY() };
        double[] destinationPoints = new double[] { destination.getX(), destination.getY() };
        return this.tra.retDistanceUsingDistanceClass(sourcePoints,destinationPoints);
    }

}
