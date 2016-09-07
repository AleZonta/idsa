package nl.tno.idsa.framework.force_field;

import nl.tno.idsa.framework.world.Point;

/**
 * Created by alessandrozonta on 07/09/16.
 */
public class PacmanRule implements UpdateRules {

    private final Double threshold; //threshold angle fixed by me
    private final Double increaseValue; //value of how much I need to increase the charge
    private final Double decreaseValue; //value of how much I need to decrease the charge
    private final Double increaseInsidePOIValue; //value of how much I need to increase the charge if I am inside a POI
    private final Double decreaseInsidePOIValue; //value of how much I need to decrease the charge if I am inside a POI
    private Boolean doINeedToUpdateTheCharge; //the name is self explaining
    private Point previousPoint; //store the previous point

    //normal constructor
    public PacmanRule(){
        this.threshold = 45.0;
        this.increaseValue = 0.1;
        this.decreaseValue = 0.1;
        this.increaseInsidePOIValue = 1.0;
        this.decreaseInsidePOIValue = 1.0;
    }

    public Double getHowMuchIncreaseTheCharge(){ return this.increaseValue; }

    public Double getHowMuchDecreaseTheCharge(){ return this.decreaseValue; }

    public Double getHowMuchIncreaseTheChargeInsidePOI() { return this.increaseInsidePOIValue; }

    public Double getHowMuchDecreaseTheChargeInsidePOI() { return this.decreaseInsidePOIValue; }

    public Boolean doINeedToUpdate() { return this.doINeedToUpdateTheCharge; }

    public void setPreviousPoint(Point previousPoint) { this.previousPoint = previousPoint; }

    public void computeUpdateRule(Point currentPosition, Point poi){
        //this is the angle that the tracked person is using to move respect the x axis
        Double angle = Math.toDegrees(Math.atan2(currentPosition.getY() - this.previousPoint.getY(), currentPosition.getX() - this.previousPoint.getX()));
        Double currentAngle = Math.toDegrees(Math.atan2(poi.getY() - currentPosition.getY(), poi.getX() - currentPosition.getX()));
        if (currentAngle > angle - this.threshold && currentAngle < angle + this.threshold) {
            //increase the charge
            this.doINeedToUpdateTheCharge = Boolean.TRUE;
        }else {
            //decrease the charge
            this.doINeedToUpdateTheCharge = Boolean.FALSE;
        }
    }

}
