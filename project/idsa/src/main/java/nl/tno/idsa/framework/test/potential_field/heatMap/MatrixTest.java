package nl.tno.idsa.framework.test.potential_field.heatMap;

import nl.tno.idsa.framework.force_field.ElectricPotential;
import nl.tno.idsa.framework.potential_field.POI;
import nl.tno.idsa.framework.potential_field.heatMap.Matrix;
import nl.tno.idsa.framework.world.Area;
import nl.tno.idsa.framework.world.Point;
import nl.tno.idsa.framework.world.Polygon;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Created by alessandrozonta on 01/08/16.
 */
//it tests the class that implement the new version of the matrix
public class MatrixTest {
    @Test
    //test if it is able to compute the potential in the initial case
    public void computeInitialForceInAllOfThePoints() throws Exception {
        //generate random charge for a random number of POI
        List<POI> listOfPOI = new ArrayList<>();
        Random randomGenerator = new Random();

        List<Double> values = new ArrayList<>();
        values.add(randomGenerator.nextDouble());
        values.add(randomGenerator.nextDouble());

        Point[] array = {new Point(5.0,5.0), new Point(8.0,6.0), new Point(14.0,3.0), new Point(9.0,0.0)};
        POI poi = new POI(new Area(1, new Polygon(array), null), values.get(0), null);

        Point[] array2 = {new Point(50.0,50.0), new Point(80.0,60.0), new Point(140.0,30.0), new Point(90.0,30.0)};
        POI poi2 = new POI(new Area(2, new Polygon(array2), null), values.get(1), null);

        listOfPOI.add(poi);
        listOfPOI.add(poi2);

        //declare the matrix
        Matrix mat = new Matrix(4000.0,4000.0);

        mat.initMap();
        mat.initPOI(listOfPOI);

        mat.computeInitialForceInAllOfThePoints(new ElectricPotential());
    }

    @Test
    //test if it returns the right things
    public void getChargeInSelectedLevel() throws Exception {
        //generate random charge for a random number of POI
        List<POI> listOfPOI = new ArrayList<>();
        Random randomGenerator = new Random();

        List<Double> values = new ArrayList<>();
        values.add(randomGenerator.nextDouble());
        values.add(randomGenerator.nextDouble());

        Point[] array = {new Point(5.0,5.0), new Point(8.0,6.0), new Point(14.0,3.0), new Point(9.0,0.0)};
        POI poi = new POI(new Area(1, new Polygon(array), null), values.get(0), null);

        Point[] array2 = {new Point(50.0,50.0), new Point(80.0,60.0), new Point(140.0,30.0), new Point(90.0,30.0)};
        POI poi2 = new POI(new Area(2, new Polygon(array2), null), values.get(1), null);

        listOfPOI.add(poi);
        listOfPOI.add(poi2);

        //declare the matrix
        Matrix mat = new Matrix(4000.0,4000.0);

        mat.initMap();
        mat.initPOI(listOfPOI);

        mat.computeActualMatrix(new Point(1000.0,1000.0));

        mat.updatePOIcharge(new Point(0.0,0.0),90.0,45.0);

        mat.computeForceInAllOfThePoints(new ElectricPotential());

        List<Double> retList = mat.getChargeInSelectedLevel(4.0);
        List<Double> retList1 = mat.getChargeInSelectedLevel(3.0);
        List<Double> retList2 = mat.getChargeInSelectedLevel(2.0);

    }

    @Test
    //test if the UpdatePOI method it works. It should update the POI charge knowing current position
    public void updatePOIcharge() throws Exception {
        //generate random charge for a random number of POI
        List<POI> listOfPOI = new ArrayList<>();
        Random randomGenerator = new Random();

        List<Double> values = new ArrayList<>();
        values.add(randomGenerator.nextDouble());
        values.add(randomGenerator.nextDouble());

        Point[] array = {new Point(5.0,5.0), new Point(8.0,6.0), new Point(14.0,3.0), new Point(9.0,0.0)};
        POI poi = new POI(new Area(1, new Polygon(array), null), values.get(0), null);

        Point[] array2 = {new Point(50.0,50.0), new Point(80.0,60.0), new Point(140.0,30.0), new Point(90.0,30.0)};
        POI poi2 = new POI(new Area(2, new Polygon(array2), null), values.get(1), null);

        listOfPOI.add(poi);
        listOfPOI.add(poi2);

        //declare the matrix
        Matrix mat = new Matrix(4000.0,4000.0);

        mat.initMap();
        mat.initPOI(listOfPOI);

        mat.computeActualMatrix(new Point(1000.0,1000.0));

        mat.updatePOIcharge(new Point(0.0,0.0),90.0,45.0);
    }

    @Test
    //test if it is able to compute the potential in all over the world
    public void computeForceInAllOfThePoints() throws Exception {
        //generate random charge for a random number of POI
        List<POI> listOfPOI = new ArrayList<>();
        Random randomGenerator = new Random();

        List<Double> values = new ArrayList<>();
        values.add(randomGenerator.nextDouble());
        values.add(randomGenerator.nextDouble());

        Point[] array = {new Point(5.0,5.0), new Point(8.0,6.0), new Point(14.0,3.0), new Point(9.0,0.0)};
        POI poi = new POI(new Area(1, new Polygon(array), null), values.get(0), null);

        Point[] array2 = {new Point(50.0,50.0), new Point(80.0,60.0), new Point(140.0,30.0), new Point(90.0,30.0)};
        POI poi2 = new POI(new Area(2, new Polygon(array2), null), values.get(1), null);

        listOfPOI.add(poi);
        listOfPOI.add(poi2);

        //declare the matrix
        Matrix mat = new Matrix(4000.0,4000.0);

        mat.initMap();
        mat.initPOI(listOfPOI);
        mat.computeInitialForceInAllOfThePoints(new ElectricPotential());

        mat.computeActualMatrix(new Point(1000.0,1000.0));
        mat.updatePOIcharge(new Point(1000.0,1000.0),90.0,45.0);
        mat.computeForceInAllOfThePoints(new ElectricPotential());

        mat.computeActualMatrix(new Point(1005.0,1005.0));
        mat.updatePOIcharge(new Point(1005.0,1005.0),90.0,45.0);
        mat.computeForceInAllOfThePoints(new ElectricPotential());

        mat.computeActualMatrix(new Point(1010.0,1010.0));
        mat.updatePOIcharge(new Point(1010.0,1010.0),90.0,45.0);
        mat.computeForceInAllOfThePoints(new ElectricPotential());
    }

    @Test
    //test if it initialises correctly the POI
    public void initPOI() throws Exception {
        //generate random charge for a random number of POI
        List<POI> listOfPOI = new ArrayList<>();
        Random randomGenerator = new Random();

        List<Double> values = new ArrayList<>();
        values.add(randomGenerator.nextDouble());
        values.add(randomGenerator.nextDouble());

        Point[] array = {new Point(5.0,5.0), new Point(8.0,6.0), new Point(14.0,3.0), new Point(9.0,0.0)};
        POI poi = new POI(new Area(1, new Polygon(array), null), values.get(0), null);

        Point[] array2 = {new Point(50.0,50.0), new Point(80.0,60.0), new Point(140.0,30.0), new Point(90.0,30.0)};
        POI poi2 = new POI(new Area(2, new Polygon(array2), null), values.get(1), null);

        listOfPOI.add(poi);
        listOfPOI.add(poi2);

        //declare the matrix
        Matrix mat = new Matrix(4000.0,4000.0);
        mat.initMap();

        mat.initPOI(listOfPOI);
        //i don't have method that returns the private fields so I debugged and seems everything fine
    }

    @Test
    //it test the method to init the matrix at the start of the program
    public void initMap() throws Exception {

        //declare the matrix
        Matrix mat = new Matrix(4000.0,4000.0);
        mat.initMap();

        //i don't have method that returns the private fields so I debugged and seems everything fine
    }

    @Test
    public void computeActualMatrix() throws Exception {
        //generate random charge for a random number of POI
        List<POI> listOfPOI = new ArrayList<>();
        Random randomGenerator = new Random();

        List<Double> values = new ArrayList<>();
        values.add(randomGenerator.nextDouble());
        values.add(randomGenerator.nextDouble());

        Point[] array = {new Point(5.0,5.0), new Point(8.0,6.0), new Point(14.0,3.0), new Point(9.0,0.0)};
        POI poi = new POI(new Area(1, new Polygon(array), null), values.get(0), null);

        Point[] array2 = {new Point(50.0,50.0), new Point(80.0,60.0), new Point(140.0,30.0), new Point(90.0,30.0)};
        POI poi2 = new POI(new Area(2, new Polygon(array2), null), values.get(1), null);

        listOfPOI.add(poi);
        listOfPOI.add(poi2);

        //declare the matrix
        Matrix mat = new Matrix(4000.0,4000.0);

        mat.initMap();
        mat.initPOI(listOfPOI);

        mat.computeActualMatrix(new Point(1000.0,1000.0));
    }

}