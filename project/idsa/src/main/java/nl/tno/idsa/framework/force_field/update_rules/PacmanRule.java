package nl.tno.idsa.framework.force_field.update_rules;

import lgds.routing.Routing;
import nl.tno.idsa.framework.force_field.ForceField;
import nl.tno.idsa.framework.potential_field.POI;
import nl.tno.idsa.framework.world.Path;
import nl.tno.idsa.framework.world.Point;
import nl.tno.idsa.framework.world.World;

import java.util.List;

/**
 * Created by alessandrozonta on 07/09/16.
 */
//Implements the pacman update rules
public class PacmanRule implements UpdateRules {
    private final Double threshold; //threshold angle fixed by me
    private Double increaseValue; //value of how much I need to increase the charge
    private Double decreaseValue; //value of how much I need to decrease the charge
    private Double increaseInsidePOIValue; //value of how much I need to increase the charge if I am inside a POI
    private Double decreaseInsidePOIValue; //value of how much I need to decrease the charge if I am inside a POI
    private Boolean doINeedToUpdateTheCharge; //the name is self explaining
    private Point previousPoint; //store the previous point
    private Double constantS; //constant for the update value formula
    private Double constantWOne; //constant for the update value formula
    protected World world; //I need the world to compute the real distance and the path
    private final Boolean usingPath; //I am using the path or not?
    private final ForceField  pot; //need the potential field to compute the path planning
    private List<POI> POIs; //we need the list of all the poi to discover where the attraction comes from
    private Double angle; //angle of the attraction of the potential field
    private final Boolean PF; //Am i using the Potential Field Path Planning
    private Routing pathFinder; //set the object path finder to compute the path if loading the trajectories from file

    //normal constructor
    public PacmanRule(){
        this.threshold = 45.0;
        this.increaseValue = 0.1;
        this.decreaseValue = 0.1;
        this.increaseInsidePOIValue = 1.0;
        this.decreaseInsidePOIValue = 1.0;
        this.doINeedToUpdateTheCharge = null;
        this.previousPoint = null;
        this.constantS = 0.1;
        this.constantWOne = 0.005;
        this.usingPath = Boolean.FALSE;
        this.pot = null;
        this.POIs = null;
        this.PF = Boolean.FALSE;
        this.pathFinder = null;
    }

    //constructor with angle parameter
    public PacmanRule(Double angle, Double constantS, Double constantWOne, Boolean usingPath){
        this.threshold = angle / 2; //angle is the total angle. Threshold is only half if it because is the value that can subtract or added to my direction
        this.increaseValue = null;
        this.decreaseValue = null;
        this.increaseInsidePOIValue = (constantS * Math.exp(0 * constantWOne))*10;
        this.decreaseInsidePOIValue = (constantS * Math.exp(0 * constantWOne))*10;
        this.doINeedToUpdateTheCharge = null;
        this.previousPoint = null;
        this.constantWOne = constantWOne;
        this.constantS = constantS;
        this.usingPath = usingPath;
        this.pot = null;
        this.POIs = null;
        this.PF = Boolean.FALSE;
        this.pathFinder = null;
    }

    public PacmanRule(Double angle, Double constantS, Double constantWOne, Boolean usingPath, ForceField pot){
        this.threshold = angle / 2; //angle is the total angle. Threshold is only half if it because is the value that can subtract or added to my direction
        this.increaseValue = null;
        this.decreaseValue = null;
        this.increaseInsidePOIValue = constantS * Math.exp(0 * constantWOne);
        this.decreaseInsidePOIValue = constantS * Math.exp(0 * constantWOne);
        this.doINeedToUpdateTheCharge = null;
        this.previousPoint = null;
        this.constantWOne = constantWOne;
        this.constantS = constantS;
        this.usingPath = usingPath;
        this.pot = pot;
        this.POIs = null;
        this.PF = Boolean.TRUE;
        this.pathFinder = null;
    }

    public Double getHowMuchIncreaseTheCharge(){ return this.increaseValue; }

    protected void setHowMuchIncreaseTheCharge(Double charge){ this.increaseValue = charge; }

    public Double getHowMuchDecreaseTheCharge(){ return this.decreaseValue; }

    protected void setHowMuchDecreaseTheCharge(Double charge){ this.decreaseValue = charge; }

    public Double getHowMuchIncreaseTheChargeInsidePOI() { return this.increaseInsidePOIValue; }

    protected void setHowMuchIncreaseTheChargeInsidePOI(Double charge){ this.increaseInsidePOIValue = charge; }

    public Double getHowMuchDecreaseTheChargeInsidePOI() { return this.decreaseInsidePOIValue; }

    protected void setHowMuchDecreaseTheChargeInsidePOI(Double charge){ this.decreaseInsidePOIValue = charge; }

    public Boolean doINeedToUpdate() { return this.doINeedToUpdateTheCharge; }

    public void setPreviousPoint(Point previousPoint) { this.previousPoint = previousPoint; }

    protected Point getPreviousPoint() { return this.previousPoint; }

    public void setWorld(World world) { this.world = world; }

    public void setPathFinder(Routing pathFinder) {
        this.pathFinder = pathFinder;
    }

    protected Boolean getUsingPath() { return this.usingPath; }

    protected Double getThreshold() { return this.threshold; }

    protected Double getConstantS() { return this.constantS; }

    protected Double getConstantWOne() { return this.constantWOne; }

    protected void setDoINeedToUpdateTheCharge(Boolean doINeedToUpdateTheCharge) { this.doINeedToUpdateTheCharge = doINeedToUpdateTheCharge; }

    protected Boolean getPF() { return this.PF; }

    protected Double getAngle() { return this.angle; }

    public void setPOIs(List<POI> POIs) { this.POIs = POIs; }

    //Compute If i have to increase or decrease the charge of the POI and how much will be the amount
    //Input
    //Point currentPosition = current position where I am
    //Point poi = Position of the POI that i need to update
    //tested
    public void computeUpdateRule(Point currentPosition, Point poi){
        //this is the angle that the tracked person is using to move respect the x axis
        Double angle = Math.toDegrees(Math.atan2(currentPosition.getY() - this.previousPoint.getY(), currentPosition.getX() - this.previousPoint.getX()));

        this.increaseValue = null;
        this.decreaseValue = null;

        if(this.PF && this.angle.equals(angle)){
            //If I am using the PF and the angle where I am going is the same that the angle of attraction
            //then do nothing
            this.doINeedToUpdateTheCharge = null;
        }else {
            //work normally
            //this is the angle between the POI and me
            Double currentAngle;
            if (!this.usingPath) {
                //I am not using the path
                currentAngle = Math.toDegrees(Math.atan2(poi.getY() - currentPosition.getY(), poi.getX() - currentPosition.getX()));
            } else {
                //I am using the Path
                Point middlePath = null;
                if(this.pathFinder != null){
                    //Need to use lgds point to call the method that will find the path between them
                    lgds.trajectories.Point source = new lgds.trajectories.Point(currentPosition.getX(), currentPosition.getY());
                    lgds.trajectories.Point destination = new lgds.trajectories.Point(poi.getX(), poi.getY());
                    this.pathFinder.getDirection(source, destination);
                    lgds.trajectories.Point result = this.pathFinder.getCenterPointOfTrajectory();
                    middlePath = new Point(result.getLatitude(), result.getLongitude());
                }else {
                    Path fromMeToPOI = this.world.getPath(currentPosition, poi);
                    middlePath = fromMeToPOI.get(fromMeToPOI.size() / 2);
                }

                currentAngle = Math.toDegrees(Math.atan2(poi.getY() - middlePath.getY(), poi.getX() - middlePath.getX()));
            }

            //calculate how much increase/decrease the charge
            Double alpha;
            if (currentAngle.equals(angle)) {
                alpha = this.threshold;
            } else if (currentAngle > angle && currentAngle < angle + this.threshold) {
                alpha = angle + this.threshold - currentAngle;
            } else if (currentAngle > angle + this.threshold && currentAngle <= angle + 180) {
                alpha = currentAngle - (angle + this.threshold);
            } else if (currentAngle > angle - this.threshold && currentAngle < angle) {
                alpha = Math.abs(Math.abs(currentAngle) - Math.abs(this.threshold));
            } else {
                alpha = angle - this.threshold - currentAngle;
            }

            //The function that I am using is s * e ^ -alpha * -const


            if (currentAngle > angle - this.threshold && currentAngle < angle + this.threshold) {
                //increase the charge
                this.doINeedToUpdateTheCharge = Boolean.TRUE;
                this.increaseValue = this.constantS * Math.exp(-alpha * this.constantWOne);
            } else {
                //decrease the charge
                this.doINeedToUpdateTheCharge = Boolean.FALSE;
                this.decreaseValue = this.constantS * Math.exp(-alpha * this.constantWOne);
            }
        }
    }

    //compute the angle of the attraction of the Potential Field
    //Input
    //Point currentPosition = current position where I am
    public void PFPathPlanning(Point currentPosition){
        //I am computing it only if I am using the potential field
        if(this.PF) {
            Point vectorComponent = this.pot.calculateForceFromPoint(currentPosition, this.POIs);
            this.angle = Math.atan2(vectorComponent.getY(), vectorComponent.getX());
        }
    }

}
