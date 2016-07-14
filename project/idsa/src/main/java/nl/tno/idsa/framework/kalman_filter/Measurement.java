package nl.tno.idsa.framework.kalman_filter;

import nl.tno.idsa.framework.world.Point;

/**
 * Created by alessandrozonta on 14/07/16.
 */
//Measurement vector for Kalman filter. In our case it implements position (x,y) -> z
public class Measurement {
    private final Double x;
    private final Double y;

    //Normal constructor
    public Measurement(){
        this.x = null;
        this.y = null;
    }

    //constructor with one point.
    public Measurement(Point point){
        this.x = point.getX();
        this.y = point.getY();
    }

    //getter for the variable
    public Double getX() { return this.x; }

    public Double getY() { return this.y; }
}
