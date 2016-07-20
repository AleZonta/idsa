package nl.tno.idsa.framework.kalman_filter;

import nl.tno.idsa.framework.world.Point;

/**
 * Created by alessandrozonta on 14/07/16.
 */
//Measurement vector for Kalman filter. In our case it implements position (x,y) -> z
//extend matrixDefinition. Measurement is a special case of Matrix
//It is matrix [2][1]
public class Measurement extends MatrixDefinition {

    //Normal constructor
    public Measurement(){
        super(2,1);
    }

    //constructor with two element
    public Measurement(Double a, Double b){
        super(2,1);
        this.setElement(0,0,a); //x
        this.setElement(1,0,b); //y
    }

    //constructor with one point.
    public Measurement(Point point){
        super(2,1);
        this.setElement(0,0,point.getX()); //x
        this.setElement(1,0,point.getY()); //y
    }

    //getter for the variable
    public Double getX() { return this.getElement(0,0); } //x

    public Double getY() { return this.getElement(1,0); } //y


    // Difference between Measurement and Measurement that return a Measurement
    // Tested
    public Measurement differenceWith(Measurement matrix) throws DifferentMatrixException{
        MatrixDefinition result = super.differenceWith(matrix);
        return new Measurement(result.getElement(0,0),result.getElement(1,0));
    }
}
