package nl.tno.idsa.framework.force_field;

import nl.tno.idsa.framework.world.Point;

/**
 * Created by alessandrozonta on 04/07/16.
 */
//implement Electric potential due to a point charge (Only one formula for both cases. Charge could be positive or negative and the result will be different)
public class ElectricPotential extends ForceField {

    //implementation of the abstract class with the ElectricPotential formulation of the potential field
    //currentPosition -> current position of the tracked person where we want to know how strong is the attraction
    //potentialPower -> is the charge of the attractive point
    //attractivePoint -> charge position
    //With this formulation we don't need to return a point but only a scalar value -> the result would be in the x position of the result
    protected Point attractiveForce(Point currentPosition, Double potentialAttractionPower, Point attractivePoint){
        //formula  = constant * (charge/distance) -> constant * (potentialAttractionPower / euclideanDistanceBetween(attractivePoint, currentPosition))
        Double constant = 1.0;
        Double distance = currentPosition.euclideanDistanceTo(attractivePoint);
        return new Point(constant * (potentialAttractionPower/distance),0.0);
    }

    //implementation of the abstract class with the ElectricPotential formulation of the potential field
    //currentPosition -> current position of the tracked person where we want to know how strong is the repulsion
    //potentialRepulsivePower -> is the power of the repulsive point
    //obstacle -> charge position
    //influenceDistance -> not used for this formulation
    //With this formulation we don't need to return a point but only a scalar value -> the result would be in the x position of the result
    protected Point repulsiveForce(Point currentPosition, Double potentialRepulsivePower, Point obstacle, Double influenceDistance){
        //formula  = constant * (charge/distance) -> constant * (potentialAttractionPower / euclideanDistanceBetween(attractivePoint, currentPosition))
        Double constant = 1.0;
        Double distance = currentPosition.euclideanDistanceTo(obstacle);
        //also for the repulsive point the charge is not store with the negative sign, we need to change this
        Double negativePotential = -potentialRepulsivePower;
        return new Point(constant * (negativePotential/distance),0.0);
    }
}
