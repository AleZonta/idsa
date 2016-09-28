package nl.tno.idsa.kalman_filter;

import nl.tno.idsa.framework.kalman_filter.Measurement;
import nl.tno.idsa.framework.kalman_filter.MeasurementFunction;
import nl.tno.idsa.framework.kalman_filter.StateVector;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

/**
 * Created by alessandrozonta on 20/07/16.
 */
public class MeasurementFunctionTest {
    @Test
    //test to check if a measurement function multiplied for a state vector result in a measurement
    public void multiplyFor() throws Exception {
        MeasurementFunction matrix = new MeasurementFunction();
        for(int i = 0; i < matrix.getRow(); i++) for(int j = 0; j < matrix.getColumn(); j++) matrix.setElement(i,j,2.0);

        List<Double> elements = new ArrayList<>();
        for(int i = 0; i < 4; i++) elements.add(8.0);
        StateVector vector = new StateVector(elements);

        Measurement expectedResult = new Measurement(64.0,64.0);

        Measurement computedResult = matrix.multiplyFor(vector);

        assertThat(computedResult, instanceOf(Measurement.class)); //Assert that result is really of that class

        for(int i = 0; i < computedResult.getRow(); i++) for(int j = 0; j < computedResult.getColumn(); j++) assertEquals(expectedResult.getElement(i,j), computedResult.getElement(i,j));

    }

}