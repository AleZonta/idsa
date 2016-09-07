package nl.tno.idsa.framework.potential_field.performance_checker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alessandrozonta on 07/09/16.
 */
public class PersonalPerformance {
    private List<Long> numberOfPositivePOIs; //number of positive charged POIs

    public PersonalPerformance(){
        this.numberOfPositivePOIs = new ArrayList<>();
    }

    public List<Long> getNumberOfPositivePOIs() { return this.numberOfPositivePOIs; }

    //add the number to the list
    public void addValue(Long value){
        this.numberOfPositivePOIs.add(value);
    }
}
