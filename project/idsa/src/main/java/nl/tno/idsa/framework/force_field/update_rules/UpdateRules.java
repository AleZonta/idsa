package nl.tno.idsa.framework.force_field.update_rules;

import nl.tno.idsa.framework.potential_field.POI;
import nl.tno.idsa.framework.world.Point;
import nl.tno.idsa.framework.world.World;

import java.util.List;

/**
 * Created by alessandrozonta on 07/09/16.
 */
public interface UpdateRules {

    //Get the value of how much I need to increase the charge
    Double getHowMuchIncreaseTheCharge();

    //Get the value of how much I need to decrease the charge
    Double getHowMuchDecreaseTheCharge();

    //Get the value of how much I need to increase the charge if I am currently inside the POI
    Double getHowMuchIncreaseTheChargeInsidePOI();

    //Get the value of how much I need to decrease the charge if I am currently inside the POI
    Double getHowMuchDecreaseTheChargeInsidePOI();

    //Set the previous point
    void setPreviousPoint(Point previousPoint);

    //Compute the value that I need
    //Input:
    //Point currentPosition = Where I am now
    //Point poi = Point of interest that I am analysing
    void computeUpdateRule(Point currentPosition, Point poi);

    //return true if following the rule I need to update the value
    Boolean doINeedToUpdate();

    //compute the angle of the attraction of the Potential Field
    void PFPathPlanning(Point currentPosition);

    //setThePOI
    void setPOIs(List<POI> POIs);

    //set the word
    void setWorld(World world);
}
