package nl.tno.idsa.framework.force_field.update_rules;

import nl.tno.idsa.framework.potential_field.PotentialField;
import nl.tno.idsa.framework.world.Point;

/**
 * Created by alessandrozonta on 12/09/16.
 */
//Implements the Potential Field PAth Planning update rules
//if I am following the attractor don't do anything
public class PFPathPlanningRule implements UpdateRules{
    private Double increaseValue; //value of how much I need to increase the charge
    private Double decreaseValue; //value of how much I need to decrease the charge
    private Double increaseInsidePOIValue; //value of how much I need to increase the charge if I am inside a POI
    private Double decreaseInsidePOIValue; //value of how much I need to decrease the charge if I am inside a POI
    private Boolean doINeedToUpdateTheCharge; //the name is self explaining
    private Point previousPoint; //store the previous point
    private final PotentialField pot; //need the potential field to compute the path planning

    //constructor with angle parameter
    public PFPathPlanningRule(PotentialField pot){
        this.increaseValue = null;
        this.decreaseValue = null;
        this.increaseInsidePOIValue = null;
        this.decreaseInsidePOIValue = null;
        this.doINeedToUpdateTheCharge = null;
        this.previousPoint = null;
        this.pot = pot;
    }

    public Double getHowMuchIncreaseTheCharge(){ return this.increaseValue; }

    public Double getHowMuchDecreaseTheCharge(){ return this.decreaseValue; }

    public Double getHowMuchIncreaseTheChargeInsidePOI() { return this.increaseInsidePOIValue; }

    public Double getHowMuchDecreaseTheChargeInsidePOI() { return this.decreaseInsidePOIValue; }

    public Boolean doINeedToUpdate() { return this.doINeedToUpdateTheCharge; }

    public void setPreviousPoint(Point previousPoint) { this.previousPoint = previousPoint; }

    //Compute If i have to increase or decrease the charge of the POI and how much will be the amount
    //Input
    //Point currentPosition = current position where I am
    //Point poi = Position of the POI that i need to update
    //tested
    public void computeUpdateRule(Point currentPosition, Point poi){

    }

}
