package nl.tno.idsa.framework.test.kalman_filter;

import nl.tno.idsa.framework.kalman_filter.DifferentMatrixException;
import nl.tno.idsa.framework.kalman_filter.MatrixDefinition;
import nl.tno.idsa.framework.kalman_filter.StateVector;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by alessandrozonta on 18/07/16.
 */
public class MatrixDefinitionTest {
    @Test
    //Testing the method for transposing a Matrix
    public void transposeMatrix() throws Exception {
        MatrixDefinition matrix = new MatrixDefinition(4,4,1.0);
        Double value = 0.0;
        for (int i = 0; i < 4; i++){
            for (int j = 0; j < 4; j++){
                matrix.getMatrix()[i][j] = value;
                value++;
            }
        }

        MatrixDefinition matrixEnd = new MatrixDefinition(4,4,1.0);
        value = 0.0;
        for (int i = 0; i < 4; i++){
            for (int j = 0; j < 4; j++){
                matrixEnd.getMatrix()[j][i] = value;
                value++;
            }
        }

        MatrixDefinition res = matrix.transposeMatrix();
        for (int i = 0; i < 4; i++){
            for (int j = 0; j < 4; j++){
                assertEquals(matrixEnd.getMatrix()[i][j], res.getMatrix()[i][j]);
            }
        }

        //now it works with square matrix. Does it work also with different matrix?
        matrix = new MatrixDefinition(2,4,1.0);
        value = 0.0;
        for (int i = 0; i < 2; i++){
            for (int j = 0; j < 4; j++){
                matrix.getMatrix()[i][j] = value;
                value++;
            }
        }
        matrixEnd = new MatrixDefinition(4,2,1.0);
        value = 0.0;
        for (int j = 0; j < 2; j++){
            for (int i = 0; i < 4; i++){
                matrixEnd.getMatrix()[i][j] = value;
                value++;
            }
        }
        res = matrix.transposeMatrix();
        assertEquals(matrixEnd.getMatrix()[0][0], res.getMatrix()[0][0]);
        assertEquals(matrixEnd.getMatrix()[0][1], res.getMatrix()[1][0]);
        assertEquals(matrixEnd.getMatrix()[0][2], res.getMatrix()[2][0]);
        assertEquals(matrixEnd.getMatrix()[0][3], res.getMatrix()[3][0]);
        assertEquals(matrixEnd.getMatrix()[1][0], res.getMatrix()[0][1]);
        assertEquals(matrixEnd.getMatrix()[1][1], res.getMatrix()[1][1]);
        assertEquals(matrixEnd.getMatrix()[1][2], res.getMatrix()[2][1]);
        assertEquals(matrixEnd.getMatrix()[1][3], res.getMatrix()[3][1]);
    }

    @Test
    //Testing the method for multiply a matrix by a scalar
    public void multiplyFor1() throws Exception {
        MatrixDefinition matrix = new MatrixDefinition(4,4,1.0,2.0);
        Double scalar = 2.0;
        matrix.multiplyFor(scalar);

        Double result = 4.0;
        for (int i = 0; i < 4; i++){
            for (int j = 0; j < 4; j++){
                assertEquals(result, matrix.getMatrix()[i][j]);
            }
        }
    }

    @org.junit.Test
    //Testing the method for multiply a matrix by a StateVector
    public void multiplyFor() throws Exception {
        List<Double> load = new ArrayList<>();
        for (int i = 0; i < 4; i++) load.add(2.0);
        StateVector vector = new StateVector(load);
        MatrixDefinition matrix = new MatrixDefinition(4,4,1.0,1.0);

        List<Double> end = new ArrayList<>();
        for (int i = 0; i < 4; i++) end.add(8.0);
        StateVector resultExpected = new StateVector(end);

        StateVector resultComputed = matrix.multiplyFor(vector);

        assertEquals(resultExpected.getX(),resultComputed.getX());
        assertEquals(resultExpected.getY(),resultComputed.getY());
        assertEquals(resultExpected.getVx(),resultComputed.getVx());
        assertEquals(resultExpected.getVy(),resultComputed.getVy());
    }

    @Test
    //Testing the method for multiply a matrix by a matrix
    public void multiplyFor2() throws Exception {
        MatrixDefinition matrix = new MatrixDefinition(4,4);
        MatrixDefinition matrixTwo = new MatrixDefinition(8,8);
        try {
            matrix.multiplyFor(matrixTwo);
        }catch (DifferentMatrixException e){
            assertEquals("Error with the matrix", e.getMessage());
        }
        matrixTwo = new MatrixDefinition(4,4);
        Double value = 0.0;
        for (int i = 0; i < 4; i++){
            for (int j = 0; j < 4; j++){
                matrixTwo.getMatrix()[i][j] = value;
                matrix.getMatrix()[i][j] = value;
                value++;
            }
        }

        MatrixDefinition result = matrix.multiplyFor(matrixTwo);

        assertEquals((Double)56.0,result.getMatrix()[0][0]);
        assertEquals((Double)62.0,result.getMatrix()[0][1]);
        assertEquals((Double)68.0,result.getMatrix()[0][2]);
        assertEquals((Double)74.0,result.getMatrix()[0][3]);
        assertEquals((Double)152.0,result.getMatrix()[1][0]);
        assertEquals((Double)174.0,result.getMatrix()[1][1]);
        assertEquals((Double)196.0,result.getMatrix()[1][2]);
        assertEquals((Double)218.0,result.getMatrix()[1][3]);
        assertEquals((Double)248.0,result.getMatrix()[2][0]);
        assertEquals((Double)286.0,result.getMatrix()[2][1]);
        assertEquals((Double)324.0,result.getMatrix()[2][2]);
        assertEquals((Double)362.0,result.getMatrix()[2][3]);
        assertEquals((Double)344.0,result.getMatrix()[3][0]);
        assertEquals((Double)398.0,result.getMatrix()[3][1]);
        assertEquals((Double)452.0,result.getMatrix()[3][2]);
        assertEquals((Double)506.0,result.getMatrix()[3][3]);

        //I tried with two 4x4 matrix
        //Is this working also with one 2x4 matrix and one 4x4 matrix?
        matrix = new MatrixDefinition(2,4);
        value = 0.0;
        for (int i = 0; i < 2; i++){
            for (int j = 0; j < 4; j++){
                matrix.getMatrix()[i][j] = value;
                value++;
            }
        }
        result = matrix.multiplyFor(matrixTwo);
        assertEquals((Double)56.0,result.getMatrix()[0][0]);
        assertEquals((Double)62.0,result.getMatrix()[0][1]);
        assertEquals((Double)68.0,result.getMatrix()[0][2]);
        assertEquals((Double)74.0,result.getMatrix()[0][3]);
        assertEquals((Double)152.0,result.getMatrix()[1][0]);
        assertEquals((Double)174.0,result.getMatrix()[1][1]);
        assertEquals((Double)196.0,result.getMatrix()[1][2]);
        assertEquals((Double)218.0,result.getMatrix()[1][3]);

        //Is this working also with one 2x4 matrix and one 4x2 matrix?
        matrixTwo = new MatrixDefinition(4,2);
        value = 0.0;
        for (int i = 0; i < 4; i++){
            for (int j = 0; j < 2; j++){
                matrixTwo.getMatrix()[i][j] = value;
                value++;
            }
        }

        result = matrix.multiplyFor(matrixTwo);
        assertEquals((Double)28.0,result.getMatrix()[0][0]);
        assertEquals((Double)34.0,result.getMatrix()[0][1]);
        assertEquals((Double)76.0,result.getMatrix()[1][0]);
        assertEquals((Double)98.0,result.getMatrix()[1][1]);
    }

    @Test
    //Testing the method for summing two matrices
    public void sumWith() throws Exception {
        MatrixDefinition matrix = new MatrixDefinition(4,4);
        MatrixDefinition matrixTwo = new MatrixDefinition(4,8);
        try {
            matrix.multiplyFor(matrixTwo);
        }catch (DifferentMatrixException e){
            assertEquals("Error with the matrix", e.getMessage());
        }
        matrixTwo = new MatrixDefinition(8,4);
        try {
            matrix.multiplyFor(matrixTwo);
        }catch (DifferentMatrixException e){
            assertEquals("Error with the matrix", e.getMessage());
        }
        matrixTwo = new MatrixDefinition(4,4);
        for (int i = 0; i < 4; i++){
            for (int j = 0; j < 4; j++){
                matrixTwo.getMatrix()[i][j] = 2.0;
                matrix.getMatrix()[i][j] = 2.0;
            }
        }

        MatrixDefinition expectedMatrix = new MatrixDefinition(4,4);
        for (int i = 0; i < 4; i++){
            for (int j = 0; j < 4; j++){
                expectedMatrix.getMatrix()[i][j] = 4.0;
            }
        }
        MatrixDefinition calculatedMatrix = matrix.sumWith(matrixTwo);

        for (int i = 0; i < 4; i++){
            for (int j = 0; j < 4; j++){
                assertEquals(expectedMatrix.getMatrix()[i][j], calculatedMatrix.getMatrix()[i][j]);
            }
        }

    }

}