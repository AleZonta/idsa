package nl.tno.idsa.framework.potential_field.performance_checker;

import nl.tno.idsa.framework.potential_field.save_to_file.SaveToFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alessandrozonta on 07/09/16.
 */
public class PersonalPerformance {
    private List<Double> numberOfPositivePOIs; //number of positive charged POIs

    public PersonalPerformance(){
        this.numberOfPositivePOIs = new ArrayList<>();
    }

    public List<Double> getNumberOfPositivePOIs() { return this.numberOfPositivePOIs; }

    //add the number to the list
    public void addValue(Long value){
        this.numberOfPositivePOIs.add(value.doubleValue());
    }

    //save to file the performance
    //input
    //SaveToFile storage -> reference to the class that saves to file
    public void saveInfoToFile(SaveToFile storage){
        storage.savePerformace(this.numberOfPositivePOIs);
    }
}
