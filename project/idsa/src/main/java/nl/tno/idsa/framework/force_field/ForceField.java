package nl.tno.idsa.framework.force_field;

import nl.tno.idsa.framework.potential_field.POI;
import nl.tno.idsa.framework.world.Point;

import java.util.List;

/**
 * Created by alessandrozonta on 30/06/16.
 */
//abstract class for calculate the force field equations
public abstract class ForceField {

    protected abstract Point attractiveForce(Point currentPosition, Double potentialAttractionPower, Point attractivePoint); //abstract method that should implement equation for the attractive force
    protected abstract Point repulsiveForce(Point currentPosition, Double potentialRepulsivePower, Point obstacle, Double influenceDistance); //abstract method that should implement equation for the repulsive force
    public abstract List<Double> calculateForceInAllTheWorld(List<Point> centerPoint, List<POI> pointsOfInterest); //abstract method that should implement how retrieve the force in every points of the world
    public abstract Point calculateForceFromPoint(Point currentPosition, List<POI> pointsOfInterest); //abstract method that compute the attraction force from one point

    protected Double maximumValuePermittedForThePotential; //It's easier to store here than inside the code. The potential could go to infinitive, so it needs a upper bound
    protected Double constantNeededForThePotentialCalculation; //constant used in the formula for the computation of the attractiveness or repulsiveness

    //method that return the potential force in the current position
    //currentPosition -> current position of the tracked person where we want to know how strong is the acctraction
    //POI -> POI data with position and disclaimer if it is attractive or repulsive
    //return gradient in that point
    protected Point force(Point currentPosition, POI point){
        //Check if current POI is attractive or repulsive
        if (point.getMeaning()){
            return this.attractiveForce(currentPosition, point.getCharge(), point.getArea().getPolygon().getClosestPoint(currentPosition));
        }else{
            return this.repulsiveForce(currentPosition, point.getCharge(), point.getArea().getPolygon().getClosestPoint(currentPosition), point.getInfluenceDistance());
        }
    }

    //method that set the two constant
    public void setConstant(Double maxValuePermittedForThePotential, Double constNeededForThePotentialCalculation){
        maximumValuePermittedForThePotential = maxValuePermittedForThePotential;
        constantNeededForThePotentialCalculation = constNeededForThePotentialCalculation;
    }


}
