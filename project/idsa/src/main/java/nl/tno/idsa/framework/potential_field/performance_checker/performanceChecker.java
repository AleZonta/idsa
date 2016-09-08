package nl.tno.idsa.framework.potential_field.performance_checker;

import nl.tno.idsa.framework.potential_field.save_to_file.SaveToFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by alessandrozonta on 07/09/16.
 */
public class PerformanceChecker {
    private HashMap<Long,PersonalPerformance> performance; //list with all the tracked person and their performance
    private List<Double> totalPerformanceNormalised; //List with all the value normalised at the end of the experiment

    public PerformanceChecker(){
        this.performance = new HashMap<>();
        this.totalPerformanceNormalised = new ArrayList<>();
    }

    //add the person Id and his performance
    public void addPersonalPerformance(Long id, PersonalPerformance performance){
        this.performance.put(id,performance);
    }

    //compute the final graph
    //tested
    public void computeGraph(){
        //list 100 positions for normalise all the result into a nice graph
        List<List<Double>> listOfValuesNormalisedFrom0to100 = new ArrayList<>();
        for(int i = 0; i < 100; i++) listOfValuesNormalisedFrom0to100.add(new ArrayList<>());
        this.performance.forEach((key,element) -> {
            Double division = 100.0 / element.getNumberOfPositivePOIs().size();
            final Double[] val = {0.0};
            element.getNumberOfPositivePOIs().stream().forEach(value -> {
                listOfValuesNormalisedFrom0to100.get(val[0].intValue()).add(value.doubleValue());
                val[0] = val[0] + division;
            });
        });
        //compute the average of all the element in every position in the vector and the save it into another list
        listOfValuesNormalisedFrom0to100.stream().forEach(el -> {
            if(el.isEmpty()){
                this.totalPerformanceNormalised.add(null);
            }else if(el.size() > 1) {
                this.totalPerformanceNormalised.add(el.stream().mapToDouble(a -> a).average().getAsDouble());
            }else{
                this.totalPerformanceNormalised.add(el.get(0));
            }
        });
    }

    //save to file the final graph
    //input
    //StorageFile storageFile -> class with the method to save everything
    public void saveTotalPerformance(SaveToFile storage){
        storage.savePerformace(this.totalPerformanceNormalised);
    }



}
