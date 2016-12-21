package nl.tno.idsa.framework.agents;

import lgds.load_track.LoadTrack;
import lgds.load_track.Traces;
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
    private Traces storage; //agent needs this to load his next position
    private Boolean dead; //true if it ends to move on the trajectory
    private Point previousPoint; //remember the previousPoint
    private Integer targetCounter; //Count time step after reached the target -> potential field count 20 time in the target before stopping tracking. I need to keep the track alive antil that point

    /**
     * Constructor with tho parameters. It builds the class with an Id and his trajectory
     * @param trajectory agent's trajectory that it will follow
     * @param storage class used to load the next position
     */
    public TrajectoryAgent(Trajectory trajectory, Traces storage, double age, Gender gender, HouseholdTypes householdType, HouseholdRoles householdRole, int year){
        super(age,gender,householdType,householdRole,year);
        this.trajectory = trajectory;
        this.storage = storage;
        this.dead = Boolean.FALSE;
        this.previousPoint = null;
        this.targetCounter = 0;
    }

    /**
     * move the agent to the next position.
     * The potential field stops track a person after 20 time steps in the same location
     * I need to keep alive the agent until the PF stops to track
     * So I am saying that the agent is dead after 21 time steps in the same location -> current position = null
     */
    public void doStep(){
        //retrieve next point
        Point currentPosition = this.trajectory.getNextPoint(this.storage);
        if(currentPosition == null){
            this.targetCounter++;
            //ended the trajectory
            //Hardcoded value -> 20
            if(this.targetCounter >= 21) {
                this.dead = Boolean.TRUE;
            }else{
                currentPosition = this.previousPoint;
            }
        }else{
            this.previousPoint = currentPosition;
        }
        if(!this.dead) {
            nl.tno.idsa.framework.world.Point position = new nl.tno.idsa.framework.world.Point(currentPosition.getLatitude(), currentPosition.getLongitude());
            super.setLocation(position); //set actual position and notify the observer
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
