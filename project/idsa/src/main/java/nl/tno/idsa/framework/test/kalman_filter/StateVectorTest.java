package nl.tno.idsa.framework.test.kalman_filter;

import nl.tno.idsa.framework.kalman_filter.StateVector;
import nl.tno.idsa.framework.world.Point;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by alessandrozonta on 19/07/16.
 */
public class StateVectorTest {
    @Test
    //test if it returns correctly the size of the state vector
    public void size() throws Exception {
        //test the size with all the constructor
        //constructor no parameters
        StateVector vector = new StateVector();
        Integer supposeResult = 4;
        Integer generatedResult = vector.size();
        assertEquals(generatedResult, supposeResult);
        //constructor one parameter
        for(int i = 0; i < 10; i++){
            List<Double> randomElement = new ArrayList<>();
            for(int j = 0; j < i; j++){
                randomElement.add(5.0);
            }
            try{
                vector = new StateVector(randomElement);
                generatedResult = vector.size();
                assertEquals(generatedResult, supposeResult);
            }catch (Exception e){
                assertEquals("List with wrong size", e.getMessage());
            }
        }
        //constructor two parameters
        Point one = new Point(5.0,6.0);
        Point two = new Point(5.0,6.0);
        vector = new StateVector(one,two);
        generatedResult = vector.size();
        assertEquals(generatedResult, supposeResult);
    }

    @Test
    //test if it returns correctly  the element at the selected index
    public void get() throws Exception {
        //With zero parameter should throw an exception
        StateVector vector = new StateVector();
        for(int i = -100; i < 100; i++){
            try{
                Double result = vector.get(i);
                Double realValue = 0.0;
                assertEquals(realValue, result);
            }catch (Exception e){
                assertEquals("Index not defined into StateVector", e.getMessage());
            }
        }
        //with one parameter
        List<Double> randomElement = new ArrayList<>();
        for(double j = 0; j < 4; j++){
            randomElement.add(j);
        }
        vector = new StateVector(randomElement);
        for(int i = -100; i < 100; i++){
            try{
                Double result = vector.get(i);
                assertEquals(randomElement.get(i), result);
            }catch (Exception e){
                assertEquals("Index not defined into StateVector", e.getMessage());
            }
        }

    }

}