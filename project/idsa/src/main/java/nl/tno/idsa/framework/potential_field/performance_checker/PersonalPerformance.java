package nl.tno.idsa.framework.potential_field.performance_checker;

import nl.tno.idsa.framework.potential_field.save_to_file.SaveToFile;
import nl.tno.idsa.framework.world.Point;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;

/**
 * Created by alessandrozonta on 07/09/16.
 */
public class PersonalPerformance {
    private List<Double> numberOfPositivePOIs; //number of positive charged POIs
    private List<Map<Point, Double>> POIsInfo; //For every time step I am saving the position of the POIs and their charge
    private Point target; // target central point

    public PersonalPerformance(){
        this.numberOfPositivePOIs = new ArrayList<>();
        this.POIsInfo = new ArrayList<>();
        this.target = null;
    }

    public List<Double> getNumberOfPositivePOIs() { return this.numberOfPositivePOIs; }

    //add the number to the list
    public void addValue(Long value){
        this.numberOfPositivePOIs.add(value.doubleValue());
    }

    //add all the POI info to the List
    public void addValue(Map<Point,Double> dic) { this.POIsInfo.add(dic);}

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
        storage.savePOIsCharge(this.target,this.POIsInfo);
    }
}
