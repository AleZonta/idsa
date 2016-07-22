package nl.tno.idsa.framework.test.force_field;

import nl.tno.idsa.framework.force_field.ElectricPotential;
import nl.tno.idsa.framework.potential_field.POI;
import nl.tno.idsa.framework.world.Area;
import nl.tno.idsa.framework.world.Point;
import nl.tno.idsa.framework.world.Polygon;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by alessandrozonta on 22/07/16.
 */
public class ElectricPotentialTest {
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

            //test the potential formula
            List<Double> magnitude = new ArrayList<>();
            magnitude = pot.calculateForceInAllTheWorld(centerPoint,pointsOfInterest);

            Optional<Double> maxx = magnitude.stream().max(Comparator.naturalOrder());

            //check that i can access the list and the number of element are okay
            assertEquals(10000, magnitude.size());

        }



    }

}