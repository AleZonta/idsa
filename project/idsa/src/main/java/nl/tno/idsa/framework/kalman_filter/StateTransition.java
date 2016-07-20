package nl.tno.idsa.framework.kalman_filter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alessandrozonta on 01/07/16.
 */
//design the state transition function. Recall that the state transition function is implemented as a matrix FF that we multiply with the previous state of our system to get the next state, like so. x¯ = Fx
// matrix containing the state transition function -> F
public class StateTransition extends MatrixDefinition {

    //constructor with dt
    public StateTransition(Double dt){
        //4 rows and columns -> number of row and columns present in the state transition function
        super(4,4, dt); //calling father constructor with three parameters (dimension of the matrix and dt)

        //populate the matrix with values. The diagonal is all equal to 1
        for(int i = 0; i < 4; i++){
            this.setElement(i,i,1.0);
        }
        //Now depends the order we are using. I am defining x Vx y Vy so Dt will be in [0][1] and [2][3]
        this.setElement(0,1,this.getTimeStep());
        this.setElement(2,3,this.getTimeStep());
    }

    // Multiplication between StateTransition and StateVector that return a StateVector
    // tested
    public StateVector multiplyFor(StateVector vector) throws DifferentMatrixException{
        MatrixDefinition result = super.multiplyFor(vector);
        //Now the result should be a matrix with only one column
        if (result.getColumn() > 1) throw new DifferentMatrixException("Error with the matrix. Is not a StateVector");
        //otherwise I should transform the result into a StateVector
        List<Double> elements = new ArrayList<>();
        for(int i = 0; i < 4; i++) elements.add(result.getElement(i,0));
        StateVector realResult;
        try {
            realResult = new StateVector(elements);
        }catch (Exception e){
            //this exception can never happen
            throw new DifferentMatrixException("Error with the matrix vector");
        }
        return realResult;
    }

    // Multiplication between StateTransition and Covariance that return a Covariance
    // tested
    public Covariance multiplyFor(Covariance matrix) throws DifferentMatrixException{
        MatrixDefinition result = super.multiplyFor(matrix);
        if(result.getRow() != 4 && result.getColumn() != 4) throw new DifferentMatrixException("Error with the matrix");
        return new Covariance(result);
    }

    // Transpose Matrix return a Covariance Matrix
    // tested
    public Covariance transposeMatrix(){
        MatrixDefinition result = super.transposeMatrix();
        return new Covariance(result);
    }

}
