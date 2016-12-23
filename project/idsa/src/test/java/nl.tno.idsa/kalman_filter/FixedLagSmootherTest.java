package nl.tno.idsa.kalman_filter;

import nl.tno.idsa.framework.kalman_filter.DifferentMatrixException;
import nl.tno.idsa.framework.kalman_filter.FixedLagSmoother;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by alessandrozonta on 23/12/2016.
 */
public class FixedLagSmootherTest {
    @Test
    public void getSmoothedPoint() throws Exception {
        FixedLagSmoother smoother = new FixedLagSmoother(8);
        smoother.smooth(1.0,1.0);
        try {
            smoother.getSmoothedPoint();
        }catch (Exception e){
            assertEquals("Not Smoothed", e.getMessage());
        }
        smoother.smooth(2.0,2.0);
        try {
            smoother.getSmoothedPoint();
        }catch (Exception e){
            assertEquals("Not Smoothed", e.getMessage());
        }
        smoother.smooth(3.0,3.0);
        try {
            smoother.getSmoothedPoint();
        }catch (Exception e){
            assertEquals("Not Smoothed", e.getMessage());
        }
        smoother.smooth(4.0,4.0);
        try {
            smoother.getSmoothedPoint();
        }catch (Exception e){
            assertEquals("Not Smoothed", e.getMessage());
        }
        smoother.smooth(5.0,5.0);
        try {
            smoother.getSmoothedPoint();
        }catch (Exception e){
            assertEquals("Not Smoothed", e.getMessage());
        }
        smoother.smooth(6.0,6.0);
        try {
            smoother.getSmoothedPoint();
        }catch (Exception e){
            assertEquals("Not Smoothed", e.getMessage());
        }
        smoother.smooth(7.0,7.0);
        try {
            smoother.getSmoothedPoint();
        }catch (Exception e){
            assertEquals("Not Smoothed", e.getMessage());
        }
        smoother.smooth(8.0,8.0);
        smoother.getSmoothedPoint();





    }

    @Test
    public void smooth() throws Exception {
        FixedLagSmoother smoother = new FixedLagSmoother(8);

    }

    @Test
    //Test if it loads correctly everything without errors
    public void FixedLagSmoother() throws Exception {
        FixedLagSmoother smoother = new FixedLagSmoother(8);
        smoother.smooth(1.0,1.0);
        smoother.smooth(2.0,2.0);
        smoother.smooth(3.0,3.0);
        smoother.smooth(4.0,4.0);
        smoother.smooth(5.0,5.0);
        smoother.smooth(6.0,6.0);
        smoother.smooth(7.0,7.0);
        smoother.smooth(8.0,8.0);
        smoother.smooth(9.0,9.0);
        smoother.smooth(10.0,10.0);




    }



}
