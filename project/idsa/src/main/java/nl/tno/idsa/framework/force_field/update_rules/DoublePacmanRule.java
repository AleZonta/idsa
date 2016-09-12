package nl.tno.idsa.framework.force_field.update_rules;

import nl.tno.idsa.framework.world.Path;
import nl.tno.idsa.framework.world.Point;

/**
 * Created by alessandrozonta on 12/09/16.
 */
//special version of the pacman update rule -> Double pacman, with the mouth in both sides
public class DoublePacmanRule extends PacmanRule {
    private Double threshold; //threshold angle fixed by me

    public DoublePacmanRule(Double angle, Double constantS, Double constantWOne, Boolean usingPath){
        super(angle, constantS, constantWOne, usingPath);
        if(angle == 90.0) {
            this.threshold = 88.0 / 2;
        }else{
            this.threshold = angle / 2;
        }
    }

    //Compute If i have to increase or decrease the charge of the POI and how much will be the amount
    //Input
    //Point currentPosition = current position where I am
    //Point poi = Position of the POI that i need to update
    //tested
    public void computeUpdateRule(Point currentPosition, Point poi){
        //this is the angle that the tracked person is using to move respect the x axis
        Double angle = Math.toDegrees(Math.atan2(currentPosition.getY() - super.getPreviousPoint().getY(), currentPosition.getX() - super.getPreviousPoint().getX()));
        //this is the angle between the POI and me
        Double currentAngle;
        if(!super.getUsingPath()){
            //I am not using the path
            currentAngle = Math.toDegrees(Math.atan2(poi.getY() - super.getPreviousPoint().getY(), poi.getX() - super.getPreviousPoint().getX()));
        }else{
            //I am using the Path
            Path fromMeToPOI = this.world.getPath(super.getPreviousPoint(),poi);
            currentAngle = Math.toDegrees(Math.atan2(poi.getY() - fromMeToPOI.get(fromMeToPOI.size()/2).getY(), poi.getX() - fromMeToPOI.get(fromMeToPOI.size()/2).getX()));
        }

        //calculate how much increase/decrease the charge
        Double alpha;
        if(currentAngle.equals(angle)) {
            alpha = this.threshold;
        }else if(currentAngle > angle && currentAngle < angle + this.threshold ){
            alpha = angle + this.threshold - currentAngle;
        }else if(currentAngle > angle + this.threshold && currentAngle <= angle + 180){
            alpha = currentAngle - (angle + super.getThreshold());
        }else if(currentAngle > angle - this.threshold && currentAngle < angle){
            alpha = Math.abs(Math.abs(currentAngle) - Math.abs(this.threshold));
        }else{
            alpha = angle - this.threshold - currentAngle;
        }

        Double oppositeAngle = angle + 180;
        if(oppositeAngle > 180){
            oppositeAngle = oppositeAngle - 360;
        }
        //The function that I am using is s * e ^ -alpha * -const
        if (currentAngle > angle - this.threshold && currentAngle < angle + this.threshold) {
            //increase the charge
            super.setDoINeedToUpdateTheCharge(Boolean.TRUE);
            super.setHowMuchIncreaseTheCharge(super.getConstantS() * Math.exp(-alpha * super.getConstantWOne()));
        }else if(currentAngle > oppositeAngle - this.threshold && currentAngle < oppositeAngle + this.threshold){
            //decrease the charge
            super.setDoINeedToUpdateTheCharge(Boolean.FALSE);
            super.setHowMuchDecreaseTheCharge(super.getConstantS() * Math.exp(-alpha * super.getConstantWOne()));
        }else{
            //no action
            super.setDoINeedToUpdateTheCharge(null);
            super.setHowMuchDecreaseTheCharge(null);
            super.setHowMuchIncreaseTheCharge(null);
        }
        super.setHowMuchIncreaseTheChargeInsidePOI(super.getConstantS() * Math.exp(0 * super.getConstantWOne()));
        super.setHowMuchDecreaseTheChargeInsidePOI(super.getConstantS() * Math.exp(0 * super.getConstantWOne()));
    }
}
