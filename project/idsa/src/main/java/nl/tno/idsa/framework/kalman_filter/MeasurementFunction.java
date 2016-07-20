package nl.tno.idsa.framework.kalman_filter;

/**
 * Created by alessandrozonta on 14/07/16.
 */
//The measurement function HH defines how we go from the state variables to the measurements using the equation z=Hx -> H  changes from state to measurement
//Here the matrix contains the state transition function -> H
public class MeasurementFunction extends MatrixDefinition {

    //normal constructor
    public MeasurementFunction(){
        //2 rows -> number of row present in measurement TODO make not hardcoded here
        //4 columns -> number of column present in our state variable TODO make not hardcoded here
        //In this case we have measurements for (x,y), so we will design zz as [xy]T[xy]T which is dimension 2x1. Our state variable is size 4x1.
        //We can deduce the required size for HH by recalling that multiplying a matrix of size MxN by NxP yields a matrix of size MxP. Thus,
        //(2×1)=(a×b)(4×1)=(2×4)(4×1)
        //(2×1)=(a×b)(4×1)=(2×4)(4×1)
        //So, HH is 2x4.
        super(2,4); //calling father constructor with two parameters (dimension of the matrix)
        //I don't have transformation so i can use the value like they are. put one in [0][0] and [1][2]
        this.setElement(0,0,1.0);
        this.setElement(1,2,1.0);
    }


    // Multiplication between MeasurementFunction and StateVector that return a Measurement
    // Tested
    public Measurement multiplyFor(StateVector vector) throws DifferentMatrixException{
        MatrixDefinition result = super.multiplyFor(vector);
        return new Measurement(result.getElement(0,0),result.getElement(1,0));
    }

}
