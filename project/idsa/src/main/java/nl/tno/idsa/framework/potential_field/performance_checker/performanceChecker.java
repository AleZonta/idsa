package nl.tno.idsa.framework.potential_field.performance_checker;

import java.util.HashMap;

/**
 * Created by alessandrozonta on 07/09/16.
 */
public class PerformanceChecker {
    private HashMap<Long,PersonalPerformance> performance; //list with all the tracked person and their performance

    public PerformanceChecker(){
        this.performance = new HashMap<>();
    }

    //add the person Id and his performance
    public void addPersonalPerformance(Long id, PersonalPerformance performance){
        this.performance.put(id,performance);
    }



}
