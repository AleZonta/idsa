package nl.tno.idsa.framework.test.performance_checker;

import nl.tno.idsa.framework.potential_field.performance_checker.PerformanceChecker;
import nl.tno.idsa.framework.potential_field.performance_checker.PersonalPerformance;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.*;

/**
 * Created by alessandrozonta on 08/09/16.
 */
public class PerformanceCheckerTest {
    @Test
    //test if the method is able to compute correctly the graph
    public void computeGraph() throws Exception {
        PerformanceChecker performance = new PerformanceChecker();

        List<Double> id = Arrays.asList(5.0,4.0,3.0,2.0,1.0);

        id.stream().forEach(iD -> {
            Integer numberOfElement = ThreadLocalRandom.current().nextInt(20,40);
            List<Long> listValue = new ArrayList<>();
            for(int i = 0; i < numberOfElement ; i++){
                listValue.add(ThreadLocalRandom.current().nextLong(0, 1000));
            }
            listValue.sort((b,a) -> a.compareTo(b));
            PersonalPerformance pp = new PersonalPerformance();
            listValue.stream().forEach(pp::addValue);
            performance.addPersonalPerformance(iD.longValue(),pp);
        });

        performance.computeGraph();

    }

}