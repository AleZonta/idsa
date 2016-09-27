package nl.tno.idsa.framework.test.potential_field.heatMap;

import nl.tno.idsa.framework.potential_field.POI;
import nl.tno.idsa.framework.potential_field.heatMap.Cell;
import nl.tno.idsa.framework.world.Point;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Created by alessandrozonta on 01/08/16.
 */
//Test the class that implements the single cell/tile of the matrix
public class CellTest {
    @Test
    //it tests if the cell is splittable
    //this means that should return true if the cell has some sub cell otherwise false
    //implicitly it test also addSubCells method
    public void isSplittable() throws Exception {
        for(int j = 0 ; j < 10; j++) { //test for 10 times
            Cell cell = new Cell();
            Cell emptyCell = new Cell();
            Random randomGenerator = new Random();
            int randomInt = randomGenerator.nextInt(10);
            if (randomInt == 0) randomInt++;
            for (int i = 0; i < randomInt; i++) {
                cell.addSubCells(new Cell());
            }
            assertEquals(Boolean.FALSE, emptyCell.isSplittable());
            assertEquals(Boolean.TRUE, cell.isSplittable());
        }
    }

    @Test
    //it tests if cell is able to compute the mean of all the charges present in his POI is correct
    //implicitly it test also addPOIs method
    public void computeAverageCharge() throws Exception {
        for(int q = 0 ; q < 100; q++) { //test for 10 times
            Cell cell = new Cell();
            List<Double> values = new ArrayList<>();
            Random randomGenerator = new Random();
            int randomInt = randomGenerator.nextInt(30);
            for (int i = 0; i < randomInt; i++) {
                values.add(randomGenerator.nextDouble());
            }
            Double expectedValue = 0.0;
            for (int z = 0; z < values.size(); z++) {
                expectedValue += values.get(z);
            }
            expectedValue /= values.size();
            if (expectedValue.isNaN()) expectedValue = 0.0;

            for (int j = 0; j < values.size(); j++) {
                POI poi = new POI(null, values.get(j), null);
                cell.addPOIs(poi);
            }

            cell.computeAverageCharge();
            assertEquals(expectedValue, cell.getAverageCharge());
        }
    }

    @Test
    //test if it is able to compute correctly the left border of the cell
    public void getLeftBorder() throws Exception {
        Cell cell = new Cell(10.0, new Point(50,50), null);
        assertEquals(new Double(45), cell.getLeftBorder());
    }

    @Test
    //test if it is able to compute correctly the right border of the cell
    public void getRightBorder() throws Exception {
        Cell cell = new Cell(10.0, new Point(50,50), null);
        assertEquals(new Double(55), cell.getRightBorder());
    }

    @Test
    //test if it is able to compute correctly the top border of the cell
    public void getTopBorder() throws Exception {
        Cell cell = new Cell(10.0, new Point(50,50), null);
        assertEquals(new Double(55), cell.getTopBorder());
    }

    @Test
    //test if it is able to compute correctly the bottom border of the cell
    public void getBottomBorder() throws Exception {
        Cell cell = new Cell(10.0, new Point(50,50), null);
        assertEquals(new Double(45), cell.getBottomBorder());
    }

    @Test
    //test if the point is inside the cell
    public void contains() throws Exception {
        Point point = new Point(35.0, 89.9);
        Cell cell = new Cell(100.0, new Point(50,50), null);
        assertEquals(Boolean.TRUE, cell.contains(point));
        cell = new Cell(5.0, new Point(50,50), null);
        assertEquals(Boolean.FALSE, cell.contains(point));
    }

    @Test
    //It tests if from the method is able from all the sub cell to copy all the POIs into the father and compute correctly their average
    public void populatePOIfromSubCells() throws Exception {
        for(int qq = 0 ; qq < 100; qq++) { //test for 10 times
            Integer numberOfSubPoi = 0;
            Random randomGenerator = new Random();
            Cell bigCell = new Cell();
            int randomInt = randomGenerator.nextInt(30);
            for (int q = 0; q < randomInt; q++) {
                Cell cell = new Cell(q);
                List<Double> values = new ArrayList<>();

                randomInt = randomGenerator.nextInt(30);
                for (int i = 0; i < randomInt; i++) {
                    values.add(randomGenerator.nextDouble());
                }

                for (int j = 0; j < values.size(); j++) {
                    POI poi = new POI(null, values.get(j), null);
                    cell.addPOIs(poi);
                }
                numberOfSubPoi += values.size();
                bigCell.addSubCells(cell);
            }
            bigCell.populatePOIfromSubCells();

            Double expectedValue = 0.0;
            for (int z = 0; z < bigCell.getPOIs().size(); z++) {
                expectedValue += bigCell.getPOIs().get(z).getCharge();
            }
            expectedValue /= bigCell.getPOIs().size();
            if(expectedValue.isNaN()) expectedValue = 0.0;

            assertEquals(expectedValue, bigCell.getAverageCharge());
            assertEquals(numberOfSubPoi.intValue(), bigCell.getPOIs().size());
        }
    }

    @Test
    //it tests if the method returns the correct id of the correct position
    public void getIdCorrectNeighbour() throws Exception {
        for(int qq = 0 ; qq < 10; qq++) { //test for 10 times
            Random randomGenerator = new Random();
            int randomInt = randomGenerator.nextInt(30);
            Cell bigCell = new Cell(null, null, randomInt);
            int secondRandomInt = randomGenerator.nextInt(1500);
            int thirdRandomInt = randomGenerator.nextInt(8);

            Cell cell = new Cell();
            try {
                cell.setNeighbour(bigCell, secondRandomInt);
            } catch (Exception e) {
                assertEquals("Wrong Index", e.getMessage());
            }
            cell.setNeighbour(bigCell, thirdRandomInt);
            try {
                cell.getIdCorrectNeighbour(secondRandomInt);
            } catch (Exception e) {
                assertEquals("Wrong Index", e.getMessage());
            }

            assertEquals(randomInt, cell.getIdCorrectNeighbour(thirdRandomInt).intValue());
        }
    }

    @Test
    //test if the method return a new cell without children
    public void retNewCellWithoutChildren() throws Exception {
        for(int qq = 0 ; qq < 10; qq++) { //test for 10 times
            Cell cell = new Cell();
            Random randomGenerator = new Random();
            int randomInt = randomGenerator.nextInt(10);
            if (randomInt == 0) randomInt++;
            for (int i = 0; i < randomInt; i++) {
                cell.addSubCells(new Cell());
            }
            Cell newCell = cell.deepCopy(Boolean.FALSE);
            assertEquals(0, newCell.getSubCells().size());
        }

    }

}