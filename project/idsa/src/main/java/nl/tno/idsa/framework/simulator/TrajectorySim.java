package nl.tno.idsa.framework.simulator;

import lgds.load_track.LoadTrack;
import lgds.simulator.SimulatorInterface;
import lgds.trajectories.Trajectories;
import lgds.trajectories.Trajectory;
import nl.tno.idsa.framework.agents.TrajectoryAgent;
import nl.tno.idsa.framework.config.ConfigFile;
import nl.tno.idsa.framework.population.Gender;
import nl.tno.idsa.framework.population.HouseholdRoles;
import nl.tno.idsa.framework.population.HouseholdTypes;
import nl.tno.idsa.framework.potential_field.PotentialField;
import nl.tno.idsa.framework.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Created by alessandrozonta on 27/09/16.
 * Trajectory Simulator that implements lgds simulator interface
 */
public class TrajectorySim implements SimulatorInterface {
    private LoadTrack storage; //class that loads the track from file
    private List<TrajectoryAgent> participant; //list of all the agents participating into the simulation
    private Trajectories tra; //keep track of all the trajectories
    private PotentialField pot; //This is the base instance of the pot

    /**
     * default constructor
     */
    public TrajectorySim(){
        this.storage = new LoadTrack();
        this.participant = new ArrayList<>();
        this.pot = null;
    }

    /**
     * getter for the agent participating at the simulation
     * @return list of agent that I am going to simulate
     */
    public List<TrajectoryAgent> getParticipant() {
        return participant;
    }

    /**
     * getter for all the trajectories
     * @return all the trajectories
     */
    @Override
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
        //retrieve all the tracks from file
        this.tra = this.storage.loadTrajectories();
        //shuffle it
        this.tra.shuffle();
        //now I am choosing only the first $number trajectories
        List<Trajectory> actualTrajectories = this.tra.getTrajectories().stream().limit(number).collect(Collectors.toList());
        //prepare the id of the agent
        List<Integer> id = new ArrayList<>();
        for(int i = 0; i < number; i++) id.add(i);
        //create the agents
        HouseholdTypes hhType = HouseholdTypes.SINGLE;
        Gender gender = Gender.FEMALE;
        double age = ThreadLocalRandom.current().nextDouble(0, 100);
        id.stream().forEach(integer -> this.participant.add(new TrajectoryAgent(actualTrajectories.get(integer), this.storage,age ,gender, hhType, HouseholdRoles.SINGLE,2016)));
        //what about poi? I should generate POI for them. Now I should generate some randomly than I should
        //find a way to find them from the poi
        this.tra.computePOIs(number);

        //now i should load all what I need for the potential field
        this.participant.stream().forEach(trajectoryAgent -> {

        });
    }


    /**
     * run the simulation. Move all the agents until all of them reach the end of their trajectory
     */
    @Override
    public void run(){


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
        this.pot = new PotentialField(world, conf, degree , s1, s2, w1 , w2, name, experiment);
    }

}
