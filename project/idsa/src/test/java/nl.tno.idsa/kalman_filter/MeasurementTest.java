package nl.tno.idsa.kalman_filter;

import nl.tno.idsa.framework.kalman_filter.Covariance;
import nl.tno.idsa.framework.kalman_filter.Measurement;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

/**
 * Created by alessandrozonta on 20/07/16.
 */
public class MeasurementTest {
    @Test
    //test if difference between two measurements is still a measurement
    public void differenceWith() throws Exception {
        Measurement minuend = new Measurement(10.0,10.0);
        Measurement subtrahend = new Measurement(5.0,7.0);

        Measurement expectedResult = new Measurement(5.0,3.0);

        Measurement computedResult = minuend.differenceWith(subtrahend);

        assertThat(computedResult, instanceOf(Measurement.class)); //Assert that result is really of that class

        for(int i = 0; i < computedResult.getRow(); i++) for(int j = 0; j < computedResult.getColumn(); j++) assertEquals(expectedResult.getElement(i,j), computedResult.getElement(i,j));
    }

}