package nl.tno.idsa.framework.force_field;

import nl.tno.idsa.framework.world.Point;

/**
 * Created by alessandrozonta on 30/06/16.
 */
//from Arambula Cosío, F., & Padilla Castañeda, M. a. A. (2004). Local Autonomous Robot Navigation using Potential Fields. Mathematical and Computer Modelling, 40(9–10), 1141–1156. http://doi.org/10.1016/j.mcm.2004.05.001
public class KathibFormulation extends ForceField {

    //implementation of the abstract class with the KathibFormulation of the potential field
    //currentPosition -> current position of the tracked person where we want to know how strong is the attraction
    //potentialPower -> is the power of the attractive point
    //attractivePoint -> position of the attractive point
    protected Point attractiveForce(Point currentPosition, Double potentialAttractionPower, Point attractivePoint){
        //formula  = -potentialPower * (currentPosition - attractivePoint)
        //res = (currentPosition - attractivePoint)
        Double x = currentPosition.getX() - attractivePoint.getX();
        Double y = currentPosition.getY() - attractivePoint.getY();
        //res = -potentialPower * res
        x *= -1 * potentialAttractionPower;
        y *= -1 * potentialAttractionPower;
        return new Point(x ,y );
    }

    //implementation of the abstract class with the KathibFormulation of the potential field
    //currentPosition -> current position of the tracked person where we want to know how strong is the repulsion
    //potentialRepulsivePower -> is the power of the repulsive point
    //obstacle -> position of the obstacle
    //influenceDistance -> influence distance of the obstacle
    protected Point repulsiveForce(Point currentPosition, Double potentialRepulsivePower, Point obstacle, Double influenceDistance){
        //formula = if d <= influenceDistance then potentialRepulsivePower * ((1/d)-(1/influenceDistance)) * ((currentPosition - obstacle)/d^3) else 0
        Double d = currentPosition.euclideanDistanceTo(obstacle);
        if (d <= influenceDistance){
            // res = ((1/d)-(1/d0))
            Double res = (1/d)-(1/influenceDistance);
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
}
