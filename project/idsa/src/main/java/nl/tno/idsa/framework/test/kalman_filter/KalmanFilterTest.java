package nl.tno.idsa.framework.test.kalman_filter;

import nl.tno.idsa.framework.kalman_filter.Covariance;
import nl.tno.idsa.framework.kalman_filter.KalmanFilter;
import nl.tno.idsa.framework.kalman_filter.StateVector;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by alessandrozonta on 19/07/16.
 */
public class KalmanFilterTest {
    @Test
    public void predictionPhase() throws Exception {

    }

    @Test
    //test if it calculates correctly the new matrices
    public void updatePhase() throws Exception {
        //I'm testing only if the matrix loaded with the constructor with one parameter compute correctly the result
        KalmanFilter filter = new KalmanFilter(1.0);
        filter.predictionPhase();

        StateVector expectedResultX = new StateVector();
        StateVector x = filter.getX();

        for(int i = 0; i < x.size(); i++){
            assertEquals(x.get(i), expectedResultX.get(i));
        }

        Covariance P = filter.getP();
        assertEquals(P.getMatrix()[0][0], new Double(1000.00025));
        assertEquals(P.getMatrix()[0][1], new Double(500.0005));
        assertEquals(P.getMatrix()[0][2], new Double(0));
        assertEquals(P.getMatrix()[0][3], new Double(0));
        assertEquals(P.getMatrix()[1][0], new Double(500.0005));
        assertEquals(P.getMatrix()[1][1], new Double(500.001));
        assertEquals(P.getMatrix()[1][2], new Double(0));
        assertEquals(P.getMatrix()[1][3], new Double(0));
        assertEquals(P.getMatrix()[2][0], new Double(0));
        assertEquals(P.getMatrix()[2][1], new Double(0));
        assertEquals(P.getMatrix()[2][2], new Double(1000.00025));
        assertEquals(P.getMatrix()[2][3], new Double(500.0005));
        assertEquals(P.getMatrix()[3][0], new Double(0));
        assertEquals(P.getMatrix()[3][1], new Double(0));
        assertEquals(P.getMatrix()[3][2], new Double(500.0005));
        assertEquals(P.getMatrix()[3][3], new Double(500.001));
    }

}