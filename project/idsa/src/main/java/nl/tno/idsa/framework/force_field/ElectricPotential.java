package nl.tno.idsa.framework.force_field;

import nl.tno.idsa.framework.potential_field.POI;
import nl.tno.idsa.framework.world.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alessandrozonta on 04/07/16.
 */
//implement Electric potential due to a point charge (Only one formula for both cases. Charge could be positive or negative and the result will be different)
public class ElectricPotential extends ForceField {

    @Override
    //implementation of the abstract class with the ElectricPotential formulation of the potential field
    //currentPosition -> current position of the tracked person where we want to know how strong is the attraction
    //potentialPower -> is the charge of the attractive point
    //attractivePoint -> charge position
    //With this formulation we don't need to return a point but only a scalar value -> the result would be in the x position of the result
    protected Point attractiveForce(Point currentPosition, Double potentialAttractionPower, Point attractivePoint){
        //formula  = constant * (charge/distance) -> constant * (potentialAttractionPower / euclideanDistanceBetween(attractivePoint, currentPosition))
        Double constant = 100000.0;
        Double distance = currentPosition.euclideanDistanceTo(attractivePoint);
        return new Point(constant * (potentialAttractionPower/distance) ,0.0);
    }

    @Override
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

    @Override
    //implementation of the abstract method that implement how retrieve the force in every points of the world
    //centerPoint -> list with all the center points of the heatMap cells
    //pointsOfInterest -> list of all the point of interest present in the map
    //return a list of double that are the value to show in the heatMap
    public List<Double> calculateForceInAllTheWord(List<Point> centerPoint, List<POI> pointsOfInterest) {
        //list with all the magnitude. From the potential field we should have vector so I calculate the magnitude. I m not sure
        List<Double> magnitude = new ArrayList<>();
        //now I have to calculate the value of the PF in every point
        for (Point aCenterPoint : centerPoint) {
            //for every point I have to compute the potential for all the attraction/repulsive points and sum the result
            Point totalForceInThisPoint = new Point(0.0, 0.0);
            for (POI aPointsOfInterest : pointsOfInterest) {
                //automatically sum every potential from every poi
                totalForceInThisPoint = totalForceInThisPoint.plus(this.force(aCenterPoint, aPointsOfInterest));
            }
            magnitude.add(totalForceInThisPoint.getX());
        }
        return magnitude;
    }
}
