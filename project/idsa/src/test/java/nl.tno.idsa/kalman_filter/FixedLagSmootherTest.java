package nl.tno.idsa.kalman_filter;

import lgds.load_track.LoadTrack;
import lgds.load_track.Traces;
import lgds.trajectories.Point;
import lgds.trajectories.Trajectories;
import lgds.trajectories.Trajectory;
import nl.tno.idsa.framework.kalman_filter.DifferentMatrixException;
import nl.tno.idsa.framework.kalman_filter.FixedLagSmoother;
import nl.tno.idsa.framework.kalman_filter.StateVector;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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


        //check real trajectory
        Traces storage = new LoadTrack();
        Trajectories tra = storage.loadTrajectories();
        //shuffle it
        tra.shuffle();
        //analysing the trajectories
        tra.analiseAndCheckTrajectory();
        //now I am choosing only the first $number trajectories
        List<Trajectory> actualTrajectories = tra.getTrajectories().stream().limit(50).collect(Collectors.toList());
        //select one of the trajectory
        Trajectory trajectory = actualTrajectories.get(40);
        Integer[] sizes = {1, 2, 5, 8, 10, 30, 50, 80, 100};
        for(int i = 0; i < 9; i ++){
            Integer targetCounter = 0;
            smoother = new FixedLagSmoother(sizes[i]);
            nl.tno.idsa.framework.world.Point firstPoint = new nl.tno.idsa.framework.world.Point(trajectory.getFirstPoint().getLatitude(), trajectory.getFirstPoint().getLongitude());
            smoother.setInitialPosition(firstPoint);

            List<Point> realPoint = new ArrayList<>();
            List<Point> smoothedPoints = new ArrayList<>();

            Integer value = sizes[i] + 20;
            while(targetCounter < value) {
                Point currentPosition = trajectory.getNextPoint(storage);
                realPoint.add(currentPosition);
                if(currentPosition == null) {
                    smoother.setEnd();
                }
                try {
                    smoother.smooth(currentPosition.getLatitude(), currentPosition.getLongitude()); //smooth the point
                } catch (Exception e) {
                    if(e.toString().equals("Error")) {
                        throw new Error("Error in data");
                    }
                }
                Point smoothedPoint = null;
                try {
                    StateVector x = smoother.getSmoothedPoint();
                    if(x == null){
                        smoothedPoint = null;
                    }else{
                        smoothedPoint = new Point(x.getX(), x.getY()); //return the point smoothed
                    }
                    smoothedPoints.add(smoothedPoint);
                } catch (Exception e) {
                    //Not smoothed, I am not doing anything here
                }
                currentPosition = smoothedPoint; //smoothed point, I am doing the same procedure as before
                if (currentPosition == null) {
                    targetCounter++;
                }
            }

            String test = "sdf";
            System.out.println("Size " + i + " output -> " + realPoint.size() + " Size Smoothed -> " + smoothedPoints.size());

            final Integer[] count = {0};
            BufferedWriter finalOutputWriter = new BufferedWriter(new FileWriter("/Users/alessandrozonta/Desktop/track/real" + Integer.toString(i) + ".csv"));
            finalOutputWriter.write("name, latitude, longitude");
            finalOutputWriter.newLine();
            realPoint.stream().forEach(point -> {
                try {
                    if(point!=null) {
                        finalOutputWriter.write(Integer.toString(count[0]) + ", " + Double.toString(point.getLatitude()) + ", " + point.getLongitude());
                        finalOutputWriter.newLine();
                        count[0]++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            finalOutputWriter.flush();
            finalOutputWriter.close();

            final Integer[] count1 = {0};
            BufferedWriter finalOutputWriter2 = new BufferedWriter(new FileWriter("/Users/alessandrozonta/Desktop/track/smoothed" + Integer.toString(i) + ".csv"));
            finalOutputWriter2.write("name, latitude, longitude");
            finalOutputWriter2.newLine();
            smoothedPoints.stream().forEach(point -> {
                try {
                    if(point!=null) {
                        finalOutputWriter2.write(Integer.toString(count1[0]) + ", " + Double.toString(point.getLatitude()) + ", " + point.getLongitude());
                        finalOutputWriter2.newLine();
                        count1[0]++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            finalOutputWriter2.flush();
            finalOutputWriter2.close();

            trajectory.resetReading();
        }




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
