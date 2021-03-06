package nl.tno.idsa.framework.force_field;

import nl.tno.idsa.framework.potential_field.POI;
import nl.tno.idsa.framework.world.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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
    //Return a new Point -> X position is the magnitude of the force, Y position is the direction
    protected Point attractiveForce(Point currentPosition, Double potentialAttractionPower, Point attractivePoint){
        //formula  = constant * (charge/distance) -> constant * (potentialAttractionPower / euclideanDistanceBetween(attractivePoint, currentPosition) ^ 2)
        Double constant = this.constantNeededForThePotentialCalculation;
        Double distance = currentPosition.euclideanDistanceTo(attractivePoint);
        return new Point(constant * (potentialAttractionPower/Math.pow(distance,2.0)) , Math.atan2(currentPosition.getY(),currentPosition.getX()));
    }

    @Override
    //implementation of the abstract class with the ElectricPotential formulation of the potential field
    //currentPosition -> current position of the tracked person where we want to know how strong is the repulsion
    //potentialRepulsivePower -> is the power of the repulsive point
    //obstacle -> charge position
    //influenceDistance -> not used for this formulation
    //Return a new Point -> X position is the magnitude of the force, Y position is the direction
    protected Point repulsiveForce(Point currentPosition, Double potentialRepulsivePower, Point obstacle, Double influenceDistance){
        //formula  = constant * (charge/distance) -> constant * (potentialAttractionPower / euclideanDistanceBetween(attractivePoint, currentPosition) ^ 2)
        Double constant = this.constantNeededForThePotentialCalculation;
        Double distance = currentPosition.euclideanDistanceTo(obstacle);
        //also for the repulsive point the charge is not store with the negative sign, we need to change this
        Double negativePotential = -potentialRepulsivePower;
        return new Point(constant * (negativePotential/Math.pow(distance,2.0)) , Math.atan2(currentPosition.getY(),currentPosition.getX()));
    }

    @Override
    //implementation of the abstract method that implement how retrieve the force in every points of the world
    //centerPoint -> list with all the center points of the heatMap cells
    //pointsOfInterest -> list of all the point of interest present in the map
    //return a list of double that are the value to show in the heatMap
    public List<Double> calculateForceInAllTheWorld(List<Point> centerPoint, List<POI> pointsOfInterest) {
        //list with all the magnitude.
        //List<Double> magnitude = new ArrayList<>();
        //Since I am using a parallel for the order of the result is not assured and I need to memorise also the point of that value
        ConcurrentHashMap<Point,Double> magnitude = new ConcurrentHashMap<>();
        //now I have to calculate the value of the PF in every point

        //Sequential version
        /*for (Point aCenterPoint : centerPoint) {
            //for every point I have to compute the potential for all the attraction/repulsive points and sum the result
            Point totalForceInThisPoint = new Point(0.0,0.0);
            for (POI aPointsOfInterest : pointsOfInterest) {
                //automatically sum every potential from every poi
                //this.force return the magnitude and the direction of the field in that position
                Point E = this.force(aCenterPoint, aPointsOfInterest);
                //I need to compute the vector component of the result -> Vx = v*sin(alpha) ; Vy = v*sin(alpha)
                Point vectorComponent = new Point(E.getX() * Math.cos(E.getY()),E.getX() * Math.sin(E.getY()));
                totalForceInThisPoint = totalForceInThisPoint.plus(vectorComponent);
            }
            //From the resultant vector get the magnitude
            Double resultantVectorMagnitude = Math.sqrt(Math.pow(totalForceInThisPoint.getX(), 2.0) + Math.pow(totalForceInThisPoint.getY(), 2.0));
            //If I am very close to the center of attraction the potential goes to a very very very high number.
            //Maybe I need a upper bound -> arbitrarily decided
            if (resultantVectorMagnitude > 400.0) resultantVectorMagnitude = 400.0;
            magnitude.add(resultantVectorMagnitude);
        }*/

        //parallel version
        centerPoint.parallelStream().forEach((aCenterPoint) -> {
            //for every point I have to compute the potential for all the attraction/repulsive points and sum the result
            Point totalForceInThisPoint = this.calculateForceFromPoint(aCenterPoint,pointsOfInterest);
            //From the resultant vector get the magnitude
            Double resultantVectorMagnitude = Math.sqrt(Math.pow(totalForceInThisPoint.getX(), 2.0) + Math.pow(totalForceInThisPoint.getY(), 2.0));
            //If I am very close to the center of attraction the potential goes to a very very very high number.
            //Maybe I need a upper bound -> arbitrarily decided
            if (resultantVectorMagnitude > this.maximumValuePermittedForThePotential) resultantVectorMagnitude = this.maximumValuePermittedForThePotential;
            if (resultantVectorMagnitude.isNaN()) resultantVectorMagnitude = 0.0;
            //adding the point and the magnitude of the pf
            magnitude.put(aCenterPoint,resultantVectorMagnitude);
        });

        //magnitude is not ordered. To print we need an ordered list so now is better order everything
        List<Double> orderedMagnitude = new ArrayList<>();
        centerPoint.stream().forEach(aPoint -> orderedMagnitude.add(magnitude.get(aPoint)));

        //return the ordered list
        return orderedMagnitude;
    }

    //implementation of the abstract method that compute the attraction force from one point
    //Point currentPosition -> position where I am now
    //List<POI> pointsOfInterest -> list of all the point of interest present in the map
    //return the resultant vector
    public Point calculateForceFromPoint(Point currentPosition, List<POI> pointsOfInterest) {
        Point totalForceInThisPoint = new Point(0.0,0.0);
        for (POI aPointsOfInterest : pointsOfInterest) {
            //automatically sum every potential from every poi
            //if the charge is 0 I can not perform next pass (maybe in this way the code is faster)
            if(aPointsOfInterest.getCharge() != 0.0) {
                //this.force return the magnitude and the direction of the field in that position
                Point E = this.force(currentPosition, aPointsOfInterest);
                //I need to compute the vector component of the result -> Vx = v*sin(alpha) ; Vy = v*sin(alpha)
                Point vectorComponent = new Point(E.getX() * Math.cos(E.getY()), E.getX() * Math.sin(E.getY()));
                totalForceInThisPoint = totalForceInThisPoint.plus(vectorComponent);
            }
        }
        return totalForceInThisPoint;
    }
}
