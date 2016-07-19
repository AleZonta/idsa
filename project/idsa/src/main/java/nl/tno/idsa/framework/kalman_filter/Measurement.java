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
        this.getMatrix()[0][0] = a; //x
        this.getMatrix()[1][0] = a; //y
    }

    //constructor with one point.
    public Measurement(Point point){
        super(2,1);
        this.getMatrix()[0][0] = point.getX(); //x
        this.getMatrix()[1][0] = point.getY(); //y
    }

    //getter for the variable
    public Double getX() { return this.getMatrix()[0][0]; } //x

    public Double getY() { return this.getMatrix()[1][0]; } //y
}
