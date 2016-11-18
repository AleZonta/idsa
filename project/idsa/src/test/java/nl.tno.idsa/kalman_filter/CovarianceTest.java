package nl.tno.idsa.kalman_filter;

import nl.tno.idsa.framework.kalman_filter.Covariance;
import nl.tno.idsa.framework.kalman_filter.DifferentMatrixException;
import nl.tno.idsa.framework.kalman_filter.MatrixDefinition;
import nl.tno.idsa.framework.kalman_filter.ProcessNoise;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Created by alessandrozonta on 20/07/16.
 */
public class CovarianceTest {
    @Test
    //Testing the method for multiply a covariance matrix by a covariance matrix and it returns a covariance matrix
    public void multiplyFor() throws Exception {
        Covariance matrix = new Covariance(1.0);
        for(int i = 0; i < matrix.getRow(); i++) for(int j = 0; j < matrix.getColumn(); j++) matrix.setElement(i,j,2.0);

        Covariance secondMatrix = new Covariance(1.0);
        for(int i = 0; i < matrix.getRow(); i++) for(int j = 0; j < matrix.getColumn(); j++) secondMatrix.setElement(i,j,5.0);

        Covariance expectedResult = new Covariance(4.0);
        for(int i = 0; i < matrix.getRow(); i++) for(int j = 0; j < matrix.getColumn(); j++) expectedResult.setElement(i,j,40.0);

        Covariance computedResult = secondMatrix.multiplyFor(matrix);

        assertThat(computedResult, instanceOf(Covariance.class)); //Assert that result is really of that class

        for(int i = 0; i < matrix.getRow(); i++) for(int j = 0; j < matrix.getColumn(); j++) assertEquals(expectedResult.getElement(i,j), computedResult.getElement(i,j));
    }


    @Test
    //Testing the method for sum a covariance matrix for a process noise matrix and it returns a covariance matrix
    public void sumWith() throws Exception{
        ProcessNoise matrix = new ProcessNoise(1.0);
        for(int i = 0; i < matrix.getRow(); i++) for(int j = 0; j < matrix.getColumn(); j++) matrix.setElement(i,j,2.0);

        Covariance secondMatrix = new Covariance(1.0);
        for(int i = 0; i < matrix.getRow(); i++) for(int j = 0; j < matrix.getColumn(); j++) secondMatrix.setElement(i,j,5.0);

        Covariance expectedResult = new Covariance(4.0);
        for(int i = 0; i < matrix.getRow(); i++) for(int j = 0; j < matrix.getColumn(); j++) expectedResult.setElement(i,j,7.0);

        Covariance computedResult = secondMatrix.sumWith(matrix);

        assertThat(computedResult, instanceOf(Covariance.class)); //Assert that result is really of that class

        for(int i = 0; i < matrix.getRow(); i++) for(int j = 0; j < matrix.getColumn(); j++) assertEquals(expectedResult.getElement(i,j), computedResult.getElement(i,j));
    }


    @Test
    //testing if difference between a covariance matrix and a matrixDefinition is a covariance matrix
    public void differenceWith() throws Exception{
        Covariance matrix = new Covariance(1.0);
        for(int i = 0; i < matrix.getRow(); i++) for(int j = 0; j < matrix.getColumn(); j++) matrix.setElement(i,j,12.0);

        MatrixDefinition secondMatrix = new MatrixDefinition(6,4);
        try {
            matrix.differenceWith(secondMatrix);
        }catch (DifferentMatrixException e){
            assertEquals("Error with the matrix", e.getMessage());
        }
        secondMatrix = new MatrixDefinition(4,4);
        for(int i = 0; i < matrix.getRow(); i++) for(int j = 0; j < matrix.getColumn(); j++) secondMatrix.setElement(i,j,5.0);

        Covariance expectedResult = new Covariance(1.0);
        for(int i = 0; i < expectedResult.getRow(); i++) for(int j = 0; j < expectedResult.getColumn(); j++) expectedResult.setElement(i,j,7.0);

        Covariance computedResult = matrix.differenceWith(secondMatrix);

        assertThat(computedResult, instanceOf(Covariance.class)); //Assert that result is really of that class

        for(int i = 0; i < computedResult.getRow(); i++) for(int j = 0; j < computedResult.getColumn(); j++) assertEquals(expectedResult.getElement(i,j), computedResult.getElement(i,j));

    }
}