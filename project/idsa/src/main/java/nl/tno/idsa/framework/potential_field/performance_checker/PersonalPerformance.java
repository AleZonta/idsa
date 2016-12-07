package nl.tno.idsa.framework.potential_field.performance_checker;

import nl.tno.idsa.framework.potential_field.save_to_file.SaveToFile;
import nl.tno.idsa.framework.world.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by alessandrozonta on 07/09/16.
 */
public class PersonalPerformance {
    private List<Double> numberOfPositivePOIs; //number of positive charged POIs
    private List<Point> locations; //locations of the POIs
    private List<List<Double>> charges; //charges of every POIs per timestep
    private Point target; // target central point
    private List<List<Point>> waypoints; //store all the used waypoint for the computation of the distance

    public PersonalPerformance(){
        this.numberOfPositivePOIs = new ArrayList<>();
        this.locations = new ArrayList<>();
        this.charges = new ArrayList<>();
        this.target = null;
        this.waypoints = new ArrayList<>();
    }

    public List<Double> getNumberOfPositivePOIs() { return this.numberOfPositivePOIs; }

    //add the number to the list
    public void addValue(Long value){
        this.numberOfPositivePOIs.add(value.doubleValue());
    }

    //add all the location of the POIs
    public void addLocations(List<Point> list) { this.locations = list; }

    //add the list of the waypoints used
    public void addWayPointList(List<Point> list) { this.waypoints.add(list); }

    //add che POIs charge
    public void addCharges(List<Double> list) {
        this.charges.add(list);
    }

    //save to file the performance
    //input
    //SaveToFile storage -> reference to the class that saves to file
    public void saveInfoToFile(SaveToFile storage){
        storage.savePerformace(this.numberOfPositivePOIs);
    }

    //setter for target
    public void setTarget(Point target) {
        if(this.target == null){
            this.target = target;
        }
    }

    //save the POis info
    public void savePOIsInfo(SaveToFile storage){
        storage.savePOIsCharge(this.target, this.locations, this.charges);
    }

    //save waypoints
    public void saveWayPoints(SaveToFile storage){
        //check if there is something inside, otherwise I do not need to save
        if (!this.waypoints.isEmpty()){
            storage.saveWayPoints(this.waypoints);
        }
    }
}
