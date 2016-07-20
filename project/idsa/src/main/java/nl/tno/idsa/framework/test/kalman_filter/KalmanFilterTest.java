package nl.tno.idsa.framework.test.kalman_filter;

import nl.tno.idsa.framework.kalman_filter.*;
import org.junit.Test;

import java.text.DecimalFormat;

import static org.junit.Assert.*;

/**
 * Created by alessandrozonta on 19/07/16.
 */
public class KalmanFilterTest {
    @Test
    //test if it calculates correctly the new matrices
    public void predictionPhase() throws Exception {
        //I'm testing only if the matrix loaded with the constructor with one parameter compute correctly the result
        KalmanFilter filter = new KalmanFilter(1.0);
        filter.predictionPhase();

        StateVector expectedResultX = new StateVector();
        StateVector x = filter.getX();

        for(int i = 0; i < x.size(); i++){
            assertEquals(x.get(i), expectedResultX.get(i));
        }

        Covariance P = filter.getP();
        assertEquals(P.getElement(0,0), new Double(1000.00025));
        assertEquals(P.getElement(0,1), new Double(500.0005));
        assertEquals(P.getElement(0,2), new Double(0));
        assertEquals(P.getElement(0,3), new Double(0));
        assertEquals(P.getElement(1,0), new Double(500.0005));
        assertEquals(P.getElement(1,1), new Double(500.001));
        assertEquals(P.getElement(1,2), new Double(0));
        assertEquals(P.getElement(1,3), new Double(0));
        assertEquals(P.getElement(2,0), new Double(0));
        assertEquals(P.getElement(2,1), new Double(0));
        assertEquals(P.getElement(2,2), new Double(1000.00025));
        assertEquals(P.getElement(2,3), new Double(500.0005));
        assertEquals(P.getElement(3,0), new Double(0));
        assertEquals(P.getElement(3,1), new Double(0));
        assertEquals(P.getElement(3,2), new Double(500.0005));
        assertEquals(P.getElement(3,3), new Double(500.001));

        //now I should check if going on with the computation everything ends okay (I chose to check after 4 steps)
        filter.predictionPhase();
        filter.predictionPhase();
        filter.predictionPhase();
        filter.predictionPhase();

        P = filter.getP();
        DecimalFormat newFormat = new DecimalFormat("#.###");

        assertEquals(Double.valueOf(newFormat.format(P.getElement(0,0))), new Double(13000.041));
        assertEquals(Double.valueOf(newFormat.format(P.getElement(0,1))), new Double(2500.012));
        assertEquals(P.getElement(0,2), new Double(0));
        assertEquals(P.getElement(0,3), new Double(0));
        assertEquals(Double.valueOf(newFormat.format(P.getElement(1,0))), new Double(2500.012));
        assertEquals(Double.valueOf(newFormat.format(P.getElement(1,1))), new Double(500.005));
        assertEquals(P.getElement(1,2), new Double(0));
        assertEquals(P.getElement(1,3), new Double(0));
        assertEquals(P.getElement(2,0), new Double(0));
        assertEquals(P.getElement(2,1), new Double(0));
        assertEquals(Double.valueOf(newFormat.format(P.getElement(2,2))), new Double(13000.041));
        assertEquals(Double.valueOf(newFormat.format(P.getElement(2,3))), new Double(2500.012));
        assertEquals(P.getElement(3,0), new Double(0));
        assertEquals(P.getElement(3,1), new Double(0));
        assertEquals(Double.valueOf(newFormat.format(P.getElement(3,2))), new Double(2500.012));
        assertEquals(Double.valueOf(newFormat.format(P.getElement(3,3))), new Double(500.005));

    }

    @Test
    public void updatePhase() throws Exception {
        //I'm testing only if the matrix loaded with the constructor with one parameter compute correctly the result
        //i need to compute also the prediction phase because I need matrix P
        KalmanFilter filter = new KalmanFilter(1.0);
        filter.predictionPhase();

        //set measurement before update phase
        filter.setMeasurement(7.0,9.0);
        //update phase after one prediction phase
        filter.updatePhase();

        //I'm checking if it computes correctly matrix S
        MatrixDefinition S = filter.getS();
        assertEquals(new Double(Math.round(S.getElement(0,0))), new Double(1005));
        assertEquals(S.getElement(0,1), new Double(0));
        assertEquals(S.getElement(1,0), new Double(0));
        assertEquals(new Double(Math.round(S.getElement(1,1))), new Double(1005));

        //I'm checking if it computes correctly matrix K
        MatrixDefinition K = filter.getK();
        DecimalFormat newFormat = new DecimalFormat("#.#######");

        assertEquals(Double.valueOf(newFormat.format(K.getElement(0,0))), new Double(0.9950249));
        assertEquals(K.getElement(0,1), new Double(0));
        assertEquals(Double.valueOf(newFormat.format(K.getElement(1,0))), new Double(0.4975128));
        assertEquals(K.getElement(1,1), new Double(0));
        assertEquals(K.getElement(2,0), new Double(0));
        assertEquals(Double.valueOf(newFormat.format(K.getElement(2,1))), new Double(0.9950249));
        assertEquals(K.getElement(3,0), new Double(0));
        assertEquals(Double.valueOf(newFormat.format(K.getElement(3,1))), new Double(0.4975128));


        //checking if it computes the residual correctly
        Measurement y = filter.getY();
        assertEquals(y.getElement(0,0), new Double(7));
        assertEquals(y.getElement(1,0), new Double(9));

        //checking if it computes x correctly
        StateVector x = filter.getX();
        newFormat = new DecimalFormat("#.######");
        assertEquals(Double.valueOf(newFormat.format(x.getElement(0,0))), new Double(6.965174));
        assertEquals(Double.valueOf(newFormat.format(x.getElement(1,0))), new Double(3.482590));
        assertEquals(Double.valueOf(newFormat.format(x.getElement(2,0))), new Double(8.955224));
        assertEquals(Double.valueOf(newFormat.format(x.getElement(3,0))), new Double(4.477615));

    }

}