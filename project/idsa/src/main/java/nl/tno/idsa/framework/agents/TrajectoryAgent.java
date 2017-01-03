package nl.tno.idsa.framework.agents;

import lgds.load_track.LoadTrack;
import lgds.load_track.Traces;
import lgds.people.AgentInterface;
import lgds.trajectories.Point;
import lgds.trajectories.Trajectory;
import nl.tno.idsa.framework.kalman_filter.FixedLagSmoother;
import nl.tno.idsa.framework.kalman_filter.StateVector;
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
    private FixedLagSmoother smoother; //Smoother System

    /**
     * Constructor with some parameters. It builds the class with an Id and his trajectory
     * @param trajectory agent's trajectory that it will follow
     * @param storage class used to load the next position
     * @param age age agent
     * @param gender gender agent
     * @param householdType house type
     * @param householdRole house role
     * @param year current year
     * @param smo Am I using the smoother?
     */
    public TrajectoryAgent(Trajectory trajectory, Traces storage, double age, Gender gender, HouseholdTypes householdType, HouseholdRoles householdRole, int year, Boolean smo, Integer lag){
        super(age,gender,householdType,householdRole,year);
        this.trajectory = trajectory;
        this.storage = storage;
        this.dead = Boolean.FALSE;
        this.previousPoint = null;
        this.targetCounter = 0;
        if(smo){
            this.smoother = new FixedLagSmoother(lag);
            nl.tno.idsa.framework.world.Point firstPoint = new nl.tno.idsa.framework.world.Point(trajectory.getFirstPoint().getLatitude(), trajectory.getFirstPoint().getLongitude());
            //set initial position for the smoother
            this.smoother.setInitialPosition(firstPoint);
        }else{
            this.smoother = null;
        }

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
        //Am I using the smoother
        if(this.smoother != null){
            //If current position is null set end trajectory
            if(currentPosition == null) {
                this.smoother.setEnd();
            }
            try {
                this.smoother.smooth(currentPosition.getLatitude(), currentPosition.getLongitude()); //smooth the point
            } catch (Exception e) {
                if(e.toString().equals("Error")) {
                    throw new Error("Error in data");
                }
            }
            Point smoothedPoint;
            try {
                StateVector x = this.smoother.getSmoothedPoint();
                if(x == null){
                    smoothedPoint = null;
                }else{
                    smoothedPoint = new Point(x.getX(), x.getY()); //return the point smoothed
                }
            } catch (Exception e) {
                //Not smoothed, I am not doing anything here
                return;
            }
            currentPosition = smoothedPoint; //smoothed point, I am doing the same procedure as before
        }

        if(currentPosition == null){
            this.targetCounter++;
            //ended the trajectory
            //Hardcoded value -> 20
            if(this.targetCounter >= 21) {
                this.dead = Boolean.TRUE;
            }else{
                currentPosition = this.previousPoint;
            }
            //In this case something wrong happened and the potential field didn't reach the end
            //I need to put a safe end to the loop
            if(this.targetCounter > 30){
                //calling in anyway the pf field and updating the position
                //he will verify with "this.dead == true" and will know what to do
                currentPosition = this.previousPoint;
                nl.tno.idsa.framework.world.Point position = new nl.tno.idsa.framework.world.Point(currentPosition.getLatitude(), currentPosition.getLongitude());
                super.setLocation(position); //set actual position and notify the observer
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
