package nl.tno.idsa.framework.force_field.update_rules;

import lgds.routing.Routing;
import nl.tno.idsa.framework.force_field.ForceField;
import nl.tno.idsa.framework.potential_field.points_of_interest.POI;
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
    private Point waypoint; //waypoint used on the computation (stored for graph)
    private Boolean idsaWorld; //If i am loading idsa

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
        this.waypoint = null;
        this.idsaWorld = null;
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
        this.waypoint = null;
        this.idsaWorld = null;
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
        this.waypoint = null;
        this.idsaWorld = null;
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

    @Override
    public Point getWaypoint() { return this.waypoint; }

    protected Routing getPathFinder() { return this.pathFinder; }

    protected Boolean getUsingPath() { return this.usingPath; }

    protected Double getThreshold() { return this.threshold; }

    protected Double getConstantS() { return this.constantS; }

    protected Double getConstantWOne() { return this.constantWOne; }

    protected void setDoINeedToUpdateTheCharge(Boolean doINeedToUpdateTheCharge) { this.doINeedToUpdateTheCharge = doINeedToUpdateTheCharge; }

    protected Boolean getPF() { return this.PF; }

    protected Double getAngle() { return this.angle; }

    protected Boolean getIdsaWorld() { return this.idsaWorld; }

    public void setIdsaWorld(Boolean idsaWorld) { this.idsaWorld = idsaWorld; }

    public void setPOIs(List<POI> POIs) { this.POIs = POIs; }

    @Override
    public World retIdsaWorld() { return this.world; }

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
                //I used the middlePath. No very correct.
                //First way point
                if(this.pathFinder != null) {
                    if (!this.idsaWorld){
                        //Need to use lgds point to call the method that will find the path between them
                        lgds.trajectories.Point source = new lgds.trajectories.Point(currentPosition.getX(), currentPosition.getY());
                        lgds.trajectories.Point destination = new lgds.trajectories.Point(poi.getX(), poi.getY());
                        this.pathFinder.getDirection(source, destination);
                        lgds.trajectories.Point result;
                        try {
                            result = this.pathFinder.getFirstWayPointOfTrajectory();
                        } catch (Exception e) {
                            //in case of errors better set waypoint as the location of the poi
                            result = destination;
                        }
                        this.waypoint = new Point(result.getLatitude(), result.getLongitude());
                    }else{
                        Path fromMeToPOI = null;
                        fromMeToPOI = this.world.getPath(currentPosition, poi);
                        try {
                            this.waypoint =  fromMeToPOI.get(1);
                        }catch (Exception e){
                            this.waypoint =  fromMeToPOI.get(0);
                        }
                    }
                }else {
                    Path fromMeToPOI = this.world.getPath(currentPosition, poi);
                    //if i do not find the waypoint lets use the poi position
                    try {
                        this.waypoint =  fromMeToPOI.get(1);
                    }catch (Exception e){
                        this.waypoint =  fromMeToPOI.get(0);
                    }
                }
                currentAngle = Math.toDegrees(Math.atan2(this.waypoint.getY() - currentPosition.getY(), this.waypoint.getX() - currentPosition.getX()));
            }

            //calculate how much increase/decrease the charge
            //More specific case
            //angle is the direction where I am going
            //current angle is the direction of the POI
            Double alpha = this.define_alpha(angle, currentAngle);

            //The function that I am using is s * e ^ -alpha * -const
            //I have more cases
            Boolean increment = this.discriminate_increment_decrement(angle, currentAngle);
            if (increment) {
                //increase the charge
                this.doINeedToUpdateTheCharge = Boolean.TRUE;
                this.increaseValue = this.constantS * Math.exp(alpha * this.constantWOne);
            } else {
                //decrease the charge
                this.doINeedToUpdateTheCharge = Boolean.FALSE;
                this.decreaseValue = this.constantS * Math.exp(alpha * this.constantWOne);
            }
        }
    }

    //find if I am incrementing or not
    private Boolean discriminate_increment_decrement(Double angle, Double currentAngle){
        Boolean inc = Boolean.FALSE;
        if (angle < 0){
            angle = -angle;
            currentAngle = -currentAngle;
        }
        if (currentAngle > 0 && angle + threshold <= 180 && currentAngle >= angle - threshold && currentAngle <= angle + threshold ){
            inc = Boolean.TRUE;
        } else if (currentAngle > 0 && angle + threshold > 180){
            Double real_upper_leg = -(360 - angle + threshold); //useful only if the upper_leg is > 180
            if(currentAngle > -180 && currentAngle < real_upper_leg) inc = Boolean.TRUE;
            if(currentAngle < 180 && currentAngle > angle - threshold) inc = Boolean.TRUE;
        } else if (currentAngle.equals(angle)){
            inc = Boolean.TRUE;
        } else if (angle - threshold < 0 && currentAngle <= angle + threshold && currentAngle >= angle - threshold){
            inc = Boolean.TRUE;
        } else if(currentAngle < 0 && angle + threshold > 180){
            //angle smaller than zero is missing
            Double tot = angle + threshold;
            Double realTot = tot - 360;
            if(currentAngle < realTot){
                inc = Boolean.TRUE;
            }
        }
        return inc;
    }

    //set the alpha for the computation
    //this method correspond to the first paper submitted to smart-ct2017
    private Double define_alpha(Double angle, Double currentAngle){
        //first thing first -> everything is positive
        Double alpha = 0.0;
        //if the direction is negative, just change sign in both angles and rules defined will work
        if (angle < 0){
            angle = -angle;
            currentAngle = -currentAngle;
            //System.out.println("-angle && -currentAngle");
        }
        Double upper_leg = angle + this.threshold;
        Double lower_leg = angle - this.threshold;
        Double opposite_angle = angle - 180;
        Double real_upper_leg = -(360 - upper_leg); //useful only if the upper_leg is > 180
        //base option
        if (currentAngle.equals(angle)) { //poi == angle
            alpha = this.threshold;
            //System.out.println("poi == angle");
        }else if (currentAngle.equals(lower_leg)){// poi == leg POV
            alpha = 0.0;
            //System.out.println("poi == leg POV");
        }else if (upper_leg <= 180 && currentAngle.equals(upper_leg)) { // poi == leg POV
            alpha = 0.0;
            //System.out.println("poi == leg POV");
        }else if (upper_leg > 180 && currentAngle.equals(real_upper_leg)) { // poi == leg POV
            alpha = 0.0;
            //System.out.println("poi == leg POV");
        } else if (currentAngle > upper_leg){ //180 > poi > upper_leg
            alpha = currentAngle - upper_leg;
            //System.out.println("180 > poi > upper_leg");
        } else if (upper_leg <= 180 && currentAngle > angle && currentAngle < upper_leg) { //upper_leg > poi > angle
            alpha = upper_leg - currentAngle;
            //System.out.println("upper_leg > poi > angle");
        } else if (upper_leg > 180  && currentAngle > real_upper_leg){ //upper_leg > poi > angle
            alpha = Math.abs(currentAngle) - Math.abs(real_upper_leg);
           // System.out.println("upper_leg > poi > angle");
        } else if (currentAngle < angle && currentAngle > lower_leg){ //lower_leg < poi < angle
            alpha = this.threshold - (angle - currentAngle);
            //System.out.println("lower_leg < poi < angle");
        } else if (currentAngle < lower_leg && currentAngle >= 0){ //lower_leg > poi > 0
            alpha = lower_leg - currentAngle;
            ///System.out.println("lower_leg > poi > 0");
        } else if (currentAngle > opposite_angle){ //0 < poi < -angle
            alpha = Math.abs(currentAngle) + lower_leg;
            //System.out.println("0 < poi < -angle");
        } else if (currentAngle.equals(opposite_angle)){  //poi == -angle
            alpha = Math.abs(opposite_angle) + lower_leg;
            //System.out.println("poi == -angle");
        } else if (currentAngle < opposite_angle){ //-180 > poi > -angle
            alpha = 360 - Math.abs(currentAngle) - upper_leg;
            //System.out.println("-180 > poi > -angle");
        }
        return alpha;
    }

    /**
     * Method that compute how much the charge is changed.
     * It is using a different system than define_alpha
     * The equation used to compute how much increment the charge is:
     * y = -0.8 + max_increment_possible * exp(-value_alpha*currentangle)
     * max_increment_possible is hardcoded here and equal to 10
     * The equation works like this:
     * domain from 0 to 180
     * 0 is the direction of the movement
     * 180 is opposite direction
     * if the result of the equation is >0 assign that as a increment
     * if the result is <0
     * check how is the result with 180-angle as a new angle
     * if is still <0 the result is 0
     * if it is greater the result is the decrement we have to apply
     * value_alpha is the variable that controls where the graph touches the axis x -> tuned outside
     * @param angle direction of the movement
     * @param currentAngle current angle with the POI
     * @param valueAlpha define the zero in the graph (tuned from outside)
     * @return Double value representing the increment or decrement. If it is positive is the increment, otherwise is a decrement
     */
    private Double define_change_in_charge(Double angle, Double currentAngle, Double valueAlpha){
        return 0d;
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
