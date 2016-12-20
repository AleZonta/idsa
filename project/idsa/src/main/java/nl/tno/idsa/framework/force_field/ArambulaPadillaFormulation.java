package nl.tno.idsa.framework.force_field;

import nl.tno.idsa.framework.potential_field.points_of_interest.POI;
import nl.tno.idsa.framework.world.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alessandrozonta on 30/06/16.
 */
//from Arambula Cosío, F., & Padilla Castañeda, M. a. A. (2004). Local Autonomous Robot Navigation using Potential Fields. Mathematical and Computer Modelling, 40(9–10), 1141–1156. http://doi.org/10.1016/j.mcm.2004.05.001
//TODO not sure the implementation is correct -> to check --> not used for now
public class ArambulaPadillaFormulation extends ForceField {

    @Override
    //implementation of the abstract class with the arambulaPadillaFormulation of the potential field
    //currentPosition -> current position of the tracked person where we want to know how strong is the attraction
    //potentialPower -> is the power of the attractive point
    //attractivePoint -> position of the attractive point
    protected Point attractiveForce(Point currentPosition, Double potentialAttractionPower, Point attractivePoint){
        //formula  = -potentialPower * (currentPosition - attractivePoint) * (1 / euclideanDistance(currentPosition,attractivePoint))
        //res = (currentPosition - attractivePoint)
        Double x = currentPosition.getX() - attractivePoint.getX();
        Double y = currentPosition.getY() - attractivePoint.getY();
        //res0 = potentialPower * res
        x *= -1 * potentialAttractionPower;
        y *= -1 * potentialAttractionPower;
        //res1 = (1 / euclideanDistance(currentPosition,attractivePoint)
        Double res1 = 1 / currentPosition.euclideanDistanceTo(attractivePoint);
        //res2 = res1 * res
        return new Point(x * res1,y * res1);
    }

    @Override
    //implementation of the abstract class with the arambulaPadillaFormulation of the potential field
    //currentPosition -> current position of the tracked person where we want to know how strong is the repulsion
    //potentialRepulsivePower -> is the power of the repulsive point
    //obstacle -> position of the obstacle
    //influenceDistance -> influence distance of the obstacle
    protected Point repulsiveForce(Point currentPosition, Double potentialRepulsivePower, Point obstacle, Double influenceDistance){
        //formula = if d <= influenceDistance then potentialRepulsivePower * (sqrt ((1/d)-(1/influenceDistance))) * ((currentPosition - obstacle)/d^3) else 0
        Double d = currentPosition.euclideanDistanceTo(obstacle);
        if (d <= influenceDistance){
            // res = (sqrt ((1/d)-(1/d0)))
            Double res = Math.sqrt((1/d)-(1/influenceDistance));
            //(x,y)T = ((currentPosition - obstacle)/d^3)
            Double x = currentPosition.getX() - obstacle.getX();
            Double y = currentPosition.getY() - obstacle.getY();
            x *= (1 / Math.pow(d,3.0));
            y *= (1 / Math.pow(d,3.0));
            //potentialRepulsivePower * res * (x,y)T
            return new Point(potentialRepulsivePower * res * x, potentialRepulsivePower * res * y);
        }else{
            return new Point(0,0);
        }
    }

    @Override
    //implementation of the abstract method that implement how retrieve the force in every points of the world
    //centerPoint -> list with all the center points of the heatMap cells
    //pointsOfInterest -> list of all the point of interest present in the map
    //return a list of double that are the value to show in the heatMap
    public List<Double> calculateForceInAllTheWorld(List<Point> centerPoint, List<POI> pointsOfInterest) {
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
            magnitude.add(Math.sqrt(Math.pow(totalForceInThisPoint.getX(), 2.0) + Math.pow(totalForceInThisPoint.getY(), 2.0)));
        }
        return magnitude;
    }

    //implementation of the abstract method that compute the attraction force from one point
    //Point currentPosition -> position where I am now
    //List<POI> pointsOfInterest -> list of all the point of interest present in the map
    //return the resultant vector
    public Point calculateForceFromPoint(Point currentPosition, List<POI> pointsOfInterest) {
        return null;
    }
}
