package nl.tno.idsa.force_field;

import nl.tno.idsa.framework.force_field.ElectricPotential;
import nl.tno.idsa.framework.potential_field.points_of_interest.POI;
import nl.tno.idsa.framework.world.Area;
import nl.tno.idsa.framework.world.Point;
import nl.tno.idsa.framework.world.Polygon;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * Created by alessandrozonta on 22/07/16.
 */
public class ElectricPotentialTest {
    //http://hyperphysics.phy-astr.gsu.edu/hbase/electric/e2p.html#c1
    //website where to obtain the correct result for this equation and computation
    //from the result be careful, need to subtract 180
    @Test
    public void calculateForceFromPoint() throws Exception {
        ElectricPotential el = new ElectricPotential();
        el.setConstant(400.0,1000.0);

        List<POI> POIs = new ArrayList<>();

        Point currentPosition = new Point(5d,5d);
        POIs.clear();
        POIs.add(new POI(new Point(10d,2d),5.0));
        POIs.add(new POI(new Point(-1d,3d),1.0));
        Point result = el.calculateForceFromPoint(currentPosition, POIs);
        Double degrees = Math.toDegrees(Math.atan2(result.getY(), result.getX()));
//        if (result.getX() < 0) degrees = degrees - 180;
        System.out.println(result.toString() + " / -> " + degrees.toString());

        currentPosition = new Point(5d,5d);
        POIs.clear();
        POIs.add(new POI(new Point(9d,9d),5.0));
        POIs.add(new POI(new Point(4d,10d),1.0));
        result = el.calculateForceFromPoint(currentPosition, POIs);
        degrees = Math.toDegrees(Math.atan2(result.getY(), result.getX()));
//        if (result.getX() < 0) degrees = degrees - 180;
        System.out.println(result.toString() + " / -> " + degrees.toString());

        currentPosition = new Point(5d,5d);
        POIs.clear();
        POIs.add(new POI(new Point(9d,8d),5.0));
        POIs.add(new POI(new Point(8d,3d),1.0));
        result = el.calculateForceFromPoint(currentPosition, POIs);
        degrees = Math.toDegrees(Math.atan2(result.getY(), result.getX()));
//        if (result.getX() < 0) degrees = degrees - 180;
        System.out.println(result.toString() + " / -> " + degrees.toString());
    }

    @Test
    //test if from a list with all the center point and a list with all the point of interest it returns a list of double with the result
    public void calculateForceInAllTheWorld() throws Exception {
        List<Point> centerPoint = new ArrayList<>();
        //lets make our arena 10x10 with a center point every 1


        //let's generate random points
        Random r = new Random();
        double x = 0.5;
        double y = 0.5;
        for (int i = 0; i < 100 ; i++){
            for (int j = 0; j < 100 ; j++) {
                centerPoint.add(new Point(x, y));
                x += 1;
            }
            y += 1;
            x = 0.5;
        }

        for(int t = 0; t < 10; t++){

            //let's generate some random point of interest
            double min = 0.1;
            double max = 10.0;
            Long id = 0l;
            List<POI> pointsOfInterest = new ArrayList<>();
            for (int i = 0; i < 10 ; i++){
                int length = r.nextInt(10);
                Point[] listPoints = new Point[length];
                for (int j = 0; j < length ; j++){
                    listPoints[j] = new Point(min + (max - min) * r.nextDouble(),min + (max - min) * r.nextDouble());
                }
                pointsOfInterest.add(new POI(new Area(id, new Polygon(listPoints),"")));
            }
            //set charge for every poi
            pointsOfInterest.stream().forEach(p -> p.setCharge(1.0));

            ElectricPotential pot = new ElectricPotential();
            pot.setConstant(400.0,10000.0);

            //test the potential formula
            List<Double> magnitude = new ArrayList<>();
            magnitude = pot.calculateForceInAllTheWorld(centerPoint,pointsOfInterest);

            Optional<Double> maxx = magnitude.stream().max(Comparator.naturalOrder());

            //check that i can access the list and the number of element are okay
            assertEquals(10000, magnitude.size());

        }



    }

}