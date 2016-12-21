package nl.tno.idsa.performance_checker;

import nl.tno.idsa.framework.potential_field.performance_checker.PerformanceChecker;
import nl.tno.idsa.framework.potential_field.performance_checker.PersonalPerformance;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by alessandrozonta on 08/09/16.
 */
public class PerformanceCheckerTest {
    @Test
    //test if the method is able to compute correctly the graph
    public void computeGraph() throws Exception {
        PerformanceChecker performance = new PerformanceChecker();

        List<Float> id = Arrays.asList(5f,4f,3f,2f,1f);

        id.stream().forEach(iD -> {
            Integer numberOfElement = ThreadLocalRandom.current().nextInt(20,40);
            List<Integer> listValue = new ArrayList<>();
            for(int i = 0; i < numberOfElement ; i++){
                listValue.add(ThreadLocalRandom.current().nextInt(0, 1000));
            }
            listValue.sort((b,a) -> a.compareTo(b));
            PersonalPerformance pp = new PersonalPerformance(0);
            listValue.stream().forEach(pp::addValue);
            performance.addPersonalPerformance(iD.longValue(),pp);
        });

        performance.computeGraph();


        List<Integer> one = Arrays.asList(7,6,5,2,1,1,1,1,1);
        List<Integer> two = Arrays.asList(10,9,8,7,2,2,2,2,2,2,2);
        List<Integer> three = Arrays.asList(15,7,3,1,1,1);
        PerformanceChecker performance1 = new PerformanceChecker();
        PersonalPerformance pp1 = new PersonalPerformance(0);
        one.stream().forEach(pp1::addValue);
        performance1.addPersonalPerformance(1L,pp1);
        PersonalPerformance pp2 = new PersonalPerformance(0);
        two.stream().forEach(pp2::addValue);
        performance1.addPersonalPerformance(2L,pp2);
        PersonalPerformance pp3 = new PersonalPerformance(0);
        three.stream().forEach(pp3::addValue);
        performance1.addPersonalPerformance(3L,pp3);

        performance1.computeGraph();

    }

}