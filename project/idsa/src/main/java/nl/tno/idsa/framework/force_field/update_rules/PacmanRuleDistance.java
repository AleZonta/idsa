package nl.tno.idsa.framework.force_field.update_rules;

import nl.tno.idsa.framework.force_field.update_rules.PacmanRule;
import nl.tno.idsa.framework.world.Point;

/**
 * Created by alessandrozonta on 12/09/16.
 */
//Extend the pacman update rules adding the distance
public class PacmanRuleDistance extends PacmanRule {
    private Double constantSDistance; //constant for the update value formula
    private Double constantWTwo; //constant for the update value formula

    public PacmanRuleDistance(Double angle, Double constantS, Double constantWOne, Double constantSDistance, Double constantWTwo, Boolean usingPath){
        super(angle, constantS, constantWOne, usingPath);
        this.constantSDistance = constantSDistance;
        this.constantWTwo = constantWTwo;

    }

    //Method that compute the rule using also the distance. So it compute the normal value using the
    //normal pacman rule and then it adds the distance from the point
    public void computeUpdateRule(Point currentPosition, Point poi){
        super.computeUpdateRule(currentPosition,poi); //calculate value using only the angle
        //now I should add the distance at that value
        //for now I use simple euclidean distance between two points
        //The function is 1 / ( s * distance ^ w)
        Double distance;
        if(!super.getUsingPath()){
            //I am not using the path
            distance = poi.euclideanDistanceTo(super.getPreviousPoint());
        }else{
            //I am using the Path
            distance = this.world.getPathLengthInM(super.getPreviousPoint(), poi);
        }
        Double valueToAdd = 1 / (this.constantSDistance * Math.pow(distance , this.constantWTwo));
        if(super.doINeedToUpdate()){
            super.setHowMuchIncreaseTheCharge(super.getHowMuchIncreaseTheCharge() + valueToAdd);
        }else{
            super.setHowMuchDecreaseTheCharge(super.getHowMuchDecreaseTheCharge() + valueToAdd);
        }

    }
}
