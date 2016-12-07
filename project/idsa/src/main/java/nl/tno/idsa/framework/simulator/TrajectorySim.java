package nl.tno.idsa.framework.simulator;

import lgds.POI.*;
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
import nl.tno.idsa.framework.potential_field.POI;
import nl.tno.idsa.framework.potential_field.performance_checker.PerformanceChecker;
import nl.tno.idsa.framework.potential_field.performance_checker.PersonalPerformance;
import nl.tno.idsa.framework.world.Point;
import nl.tno.idsa.framework.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Created by alessandrozonta on 27/09/16.
 * Trajectory Simulator that implements lgds simulator interface
 */
public class TrajectorySim implements SimulatorInterface {
    private Traces storage; //class that loads the track from file
    private List<TrajectoryAgent> participant; //list of all the agents participating into the simulation
    private Trajectories tra; //keep track of all the trajectories
    private PotentialField pot; //This is the base instance of the pot
    private final PerformanceChecker performance; //keep track of the performance of the simulator
    private HashMap<Long,PotentialField> listPot; //Every tracked agent need its own potential field. I will deep copy the base instance for all the tracked agents and I will store them here. Save PotentialField with the id of the agent tracked

    /**
     * default constructor
     */
    public TrajectorySim(Integer selector){
        if (selector == 0){
            this.storage = new LoadIDSATrack();
        }else{
            this.storage = new LoadTrack();
        }
        this.participant = new ArrayList<>();
        this.pot = null;
        this.performance = new PerformanceChecker();
        this.listPot = new HashMap<>();
        //retrieve all the tracks from file
        this.tra = this.storage.loadTrajectories();
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
        return participant;
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
        //now I am choosing only the first $number trajectories
        System.out.println("Selecting trajectories...");
        List<Trajectory> actualTrajectories = this.tra.getTrajectories().stream().limit(number).collect(Collectors.toList());
        //prepare the id of the agent
        List<Integer> id = new ArrayList<>();
        //check the number
        if(number > actualTrajectories.size()) number = actualTrajectories.size() - 1;
        for(int i = 0; i < number; i++) id.add(i);
        System.out.println("Creating agents...");
        //create the agents
        id.stream().forEach(integer -> {
            HouseholdTypes hhType = HouseholdTypes.SINGLE;
            Gender gender = Gender.FEMALE;
            double age = ThreadLocalRandom.current().nextDouble(0, 100);
            this.participant.add(new TrajectoryAgent(actualTrajectories.get(integer), this.storage,age ,gender, hhType, HouseholdRoles.SINGLE,2016));
        });
        //what about poi? I should generate POI for them. Now I should generate some randomly than I should
        //find a way to find them from the poi
        //I can select how many POI use here. Lets add more
        System.out.println("Computing POIs...");
        this.tra.computePOIs(number * 3);

        System.out.println("Connecting the potential field to the people tracked...");
        //now i should load all what I need for the potential field
        this.participant.stream().forEach(trajectoryAgent -> {
            try {
                //new agent tracked new potential field for him
                PotentialField fieldForTheTrackedAgent = this.pot.deepCopy();
                TrackingSystem trackingForTheTrackedAgent = new TrackingSystem(fieldForTheTrackedAgent);

                PersonalPerformance personalPerformance = new PersonalPerformance(); //prepare class for personal performance
                fieldForTheTrackedAgent.setPerformance(personalPerformance); //set personal performance on the field
                this.performance.addPersonalPerformance(trajectoryAgent.getId(),personalPerformance); //connect performance with id person and put them together in a list

                fieldForTheTrackedAgent.setTrajectorySimReference(this);
                fieldForTheTrackedAgent.setTrackedAgent(trajectoryAgent);
                fieldForTheTrackedAgent.setPointsOfInterest(this.translatePOI(this.tra.getListOfPOIs())); //set the POIs obtained from the GPS trajectories
                trajectoryAgent.deleteObservers(); //delete old observers
                trajectoryAgent.setTracked(trackingForTheTrackedAgent, null); //set the observer to this point

                //Add potential field and tracking system to their list
                this.listPot.put(trajectoryAgent.getId(),fieldForTheTrackedAgent);
                System.out.println("Loaded Potential Field for person number " + this.listPot.size() + "...");

            } catch (EmptyActivityException | ActivityNotImplementedException e) {
                //No planned activity. I do not need to do anything. The exception doesn't add the agent to the list
                e.printStackTrace();
            }
        });
    }


    /**
     * run the simulation. Move all the agents until all of them reach the end of their trajectory
     */
    @Override
    public void run(){
        //run the simulator
        while (this.participant.stream().filter(agent -> !agent.getDead()).toArray().length != 0) {
            this.participant.parallelStream().filter(agent -> !agent.getDead()).forEach(TrajectoryAgent::doStep);
        }
        System.out.println("End simulating procedure...");

    }

    /**
     * Wrap the init and the run to let run a fixed amount of people together
     * @param max_allowed
     */
    public void init_and_run(Integer max_allowed, Integer number){
        //shuffle it
        this.tra.shuffle();
        //now I am choosing only the first $number trajectories
        System.out.println("Selecting trajectories...");
        List<Trajectory> actualBigTrajectories = this.tra.getTrajectories().stream().limit(number).collect(Collectors.toList());
        //how many division?
        Double division = Math.ceil(actualBigTrajectories.size() / max_allowed.doubleValue());
        for (Integer step = 1; step <= division.intValue(); step++){
            Integer start = max_allowed * ( step - 1 );
            Integer end = max_allowed * step;
            if(actualBigTrajectories.size() < end) end = actualBigTrajectories.size();
            //select subset from start to end
            List<Trajectory> actualTrajectories = actualBigTrajectories.subList(start, end);
            //prepare the id of the agent
            List<Integer> id = new ArrayList<>();
            //check the number
            if(number > actualTrajectories.size()) number = actualTrajectories.size();
            for(int i = 0; i < number; i++) id.add(i);
            System.out.println("Creating agents...");
            //delete everything in partecipant
            this.participant = new ArrayList<>();
            //create the agents
            id.stream().forEach(integer -> {
                HouseholdTypes hhType = HouseholdTypes.SINGLE;
                Gender gender = Gender.FEMALE;
                double age = ThreadLocalRandom.current().nextDouble(0, 100);
                this.participant.add(new TrajectoryAgent(actualTrajectories.get(integer), this.storage, age ,gender, hhType, HouseholdRoles.SINGLE,2016));
            });
            //what about poi? I should generate POI for them. Now I should generate some randomly than I should
            //find a way to find them from the poi
            //I can select how many POI use here. Lets add more
            System.out.println("Computing POIs...");
            this.tra.computePOIs(actualTrajectories);

            System.out.println("Connecting the potential field to the people tracked...");
            //now i should load all what I need for the potential field
            this.participant.stream().forEach(trajectoryAgent -> {
                try {
                    //new agent tracked new potential field for him
                    PotentialField fieldForTheTrackedAgent = this.pot.deepCopy();
                    TrackingSystem trackingForTheTrackedAgent = new TrackingSystem(fieldForTheTrackedAgent);

                    PersonalPerformance personalPerformance = new PersonalPerformance(); //prepare class for personal performance
                    fieldForTheTrackedAgent.setPerformance(personalPerformance); //set personal performance on the field
                    this.performance.addPersonalPerformance(trajectoryAgent.getId(),personalPerformance); //connect performance with id person and put them together in a list

                    fieldForTheTrackedAgent.setTrajectorySimReference(this);
                    fieldForTheTrackedAgent.setTrackedAgent(trajectoryAgent);
                    fieldForTheTrackedAgent.setPointsOfInterest(this.translatePOI(this.tra.getListOfPOIs())); //set the POIs obtained from the GPS trajectories
                    trajectoryAgent.deleteObservers(); //delete old observers
                    trajectoryAgent.setTracked(trackingForTheTrackedAgent, null); //set the observer to this point

                    //Add potential field and tracking system to their list
                    this.listPot.put(trajectoryAgent.getId(),fieldForTheTrackedAgent);
                    System.out.println("Loaded Potential Field for person number " + this.listPot.size() + "...");

                } catch (EmptyActivityException | ActivityNotImplementedException e) {
                    //No planned activity. I do not need to do anything. The exception doesn't add the agent to the list
                    e.printStackTrace();
                }
            });

            //run this subset of people
            this.run();
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
    public void initPotentialField(ConfigFile conf, Double degree, Double s1, Double s2, Double w1, Double w2, String name, String experiment){
        //set in word the dimension of the word and the area of the word set to null
        World world = new World();
        world.applyGeoRoot(this.tra.getUtmRoot().getLatitude(),this.tra.getUtmRoot().getLongitude(),this.tra.getWhWorld().getLatitude(),this.tra.getWhWorld().getLongitude());
        //I do not think I need something else inside the world for running the potential field
        this.pot = new PotentialField(world, conf, degree , s1, s2, w1 , w2, name, experiment);
    }


    /**
     * remove the person selected from the list of tracked people and saving its performance
     * If the list is empty stop the simulation and compute the final performance
     * @param agentId Id of the person to remove
     */
    public void removeFromTheLists(Long agentId){
        this.listPot.remove(agentId);
        System.out.println("Removing tracked agent from the list. Remaining agents -> " + this.listPot.size() + "...");
        //once I removed the agent i should check how many of them are still alive
        //If no one is alive stop the simulation
        if(this.listPot.isEmpty()){
            //now I should also save all the performance of all the person
            //I have to compute the total performance before saving it
            Integer value = this.pot.getConfig().getPerformance();
            if(value == 1 || value == 2) {
                this.performance.computeGraph();
                System.out.println("Saving performance...");
                //I am using the base instance of pot because it has still the correct path to save this fill
                this.performance.saveTotalPerformance(this.pot.getStorage());
            }
            System.out.println("End tracking procedure...");
        }
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
            if(this.pot.getPathFinder().isContained(poi.getLocation())){
                appoList.add(poi);
            }
        });
        List<POI> realList = new ArrayList<>();
        appoList.stream().forEach(poi -> realList.add(new POI(new Point(poi.getLocation().getLatitude(),poi.getLocation().getLongitude()))));
        return realList;
    }

}
