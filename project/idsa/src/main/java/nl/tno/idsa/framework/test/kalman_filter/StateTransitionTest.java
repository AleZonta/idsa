package nl.tno.idsa.framework.test.kalman_filter;

import nl.tno.idsa.framework.kalman_filter.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

/**
 * Created by alessandrozonta on 20/07/16.
 */
public class StateTransitionTest {
    @Test
    //Testing the method for multiply a matrix by a state vector and return a State Vector
    public void multiplyFor() throws Exception {
        //test first exception. State transition and Matrix not compatible for multiplication
        StateTransition matrix = new StateTransition(1.0);
        MatrixDefinition wrongMatrix = new MatrixDefinition(8,8);
        try {
            matrix.multiplyFor(wrongMatrix);
        }catch (DifferentMatrixException e){
            assertEquals("Error with the matrix", e.getMessage());
        }
        //second exception. second matrix has good number of rows but wrong number of columns
        wrongMatrix = new MatrixDefinition(4,4);
        try {
            matrix.multiplyFor(wrongMatrix);
        }catch (DifferentMatrixException e){
            assertEquals("Error with the matrix. Is not a StateVector", e.getMessage());
        }
        //now test the algorithm to multiply the two matrix
        List<Double> res = new ArrayList<>();
        for(int i = 0; i < 4; i++) res.add(5.0);
        StateVector secondMatrix = new StateVector(res);

        for(int i = 0; i < matrix.getRow(); i++) for(int j = 0; j < matrix.getColumn(); j++) matrix.setElement(i,j,2.0);

        res = new ArrayList<>();
        for(int i = 0; i < 4; i++) res.add(40.0);
        StateVector expectedResult = new StateVector(res);

        StateVector computedResult = matrix.multiplyFor(secondMatrix);

        assertThat(computedResult, instanceOf(StateVector.class)); //Assert that result is really of that class

        for(int i = 0; i < 4; i++){
            assertEquals(expectedResult.get(i), computedResult.get(i));
        }
    }

    @Test
    //Testing the method for multiply a state transition matrix by a covariance matrix and it returns a covariance matrix
    public void multiplyFor1() throws Exception {
        Covariance matrix = new Covariance(1.0);
        for(int i = 0; i < matrix.getRow(); i++) for(int j = 0; j < matrix.getColumn(); j++) matrix.setElement(i,j,2.0);

        StateTransition secondMatrix = new StateTransition(1.0);
        for(int i = 0; i < matrix.getRow(); i++) for(int j = 0; j < matrix.getColumn(); j++) secondMatrix.setElement(i,j,5.0);

        Covariance expectedResult = new Covariance(4.0);
        for(int i = 0; i < matrix.getRow(); i++) for(int j = 0; j < matrix.getColumn(); j++) expectedResult.setElement(i,j,40.0);

        Covariance computedResult = secondMatrix.multiplyFor(matrix);

        assertThat(computedResult, instanceOf(Covariance.class)); //Assert that result is really of that class

        for(int i = 0; i < matrix.getRow(); i++) for(int j = 0; j < matrix.getColumn(); j++) assertEquals(expectedResult.getElement(i,j), computedResult.getElement(i,j));

    }

    @Test
    //Testing the method for transposing a stateTransition matrix and it returns a Covariance Matrix
    public void transposeMatrix() throws Exception {
        StateTransition matrix = new StateTransition(1.0);
        Double value = 0.0;
        for(int i = 0; i < matrix.getRow(); i++) {
            for (int j = 0; j < matrix.getColumn(); j++) {
                matrix.setElement(i,j,value);
                value++;
            }
        }

        Covariance matrixEnd = new Covariance(1.0);
        value = 0.0;
        for (int i = 0; i < 4; i++){
            for (int j = 0; j < 4; j++){
                matrixEnd.setElement(j,i,value);
                value++;
            }
        }

        Covariance computedResult = matrix.transposeMatrix();

        assertThat(computedResult, instanceOf(Covariance.class)); //Assert that result is really of that class

        for(int i = 0; i < matrix.getRow(); i++) for(int j = 0; j < matrix.getColumn(); j++) assertEquals(matrixEnd.getElement(i,j), computedResult.getElement(i,j));


    }

}