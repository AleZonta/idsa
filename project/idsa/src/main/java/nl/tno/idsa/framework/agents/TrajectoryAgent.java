package nl.tno.idsa.framework.agents;

import lgds.load_track.LoadTrack;
import lgds.people.AgentInterface;

import lgds.trajectories.Point;
import lgds.trajectories.Trajectory;
import nl.tno.idsa.framework.population.Gender;
import nl.tno.idsa.framework.population.HouseholdRoles;
import nl.tno.idsa.framework.population.HouseholdTypes;

/**
 * Created by alessandrozonta on 27/09/16.
 */
public class TrajectoryAgent extends Agent implements AgentInterface {
    private final Trajectory trajectory; //Trajectory of the agent that has to follow
    private LoadTrack storage; //agent needs this to load his next position
    private Boolean dead; //true if it ends to move on the trajectory

    /**
     * Constructor with tho parameters. It builds the class with an Id and his trajectory
     * @param trajectory agent's trajectory that it will follow
     * @param storage class used to load the next position
     */
    public TrajectoryAgent(Trajectory trajectory, LoadTrack storage, double age, Gender gender, HouseholdTypes householdType, HouseholdRoles householdRole, int year){
        super(age,gender,householdType,householdRole,year);
        this.trajectory = trajectory;
        this.storage = storage;
        this.dead = Boolean.FALSE;
    }

    /**
     * move the agent to the next position
     */
    public void doStep(){
        //retrieve next point
        Point currentPosition = this.trajectory.getNextPoint(this.storage);
        nl.tno.idsa.framework.world.Point position = new nl.tno.idsa.framework.world.Point(currentPosition.getLatitude(),currentPosition.getLongitude());
        super.setLocation(position); //set actual position and notify the observer
        if(currentPosition == null){
            //ended the trajectory
            this.dead = Boolean.TRUE;
        }
    }

    /**
     * getter for the field that is indicating if the agent is still alive
     * @return false if it is still alive or true if it ends to follow the trajectory
     */
    public Boolean getDead(){
        return dead;
    }

}
