package nl.tno.idsa.framework.kalman_filter;

import nl.tno.idsa.framework.world.Point;

/**
 * Created by alessandrozonta on 01/07/16.
 */
//State Vector for kalman filter. In our case it implements position (x,y) and velocity vector (Vx,Vy)
public class StateVector {

    private final Double x;
    private final Double y;
    private final Double Vx;
    private final Double Vy;

    //Normal constructor
    public StateVector(){
        this.x = null;
        this.y = null;
        this.Vx = null;
        this.Vy = null;
    }

    //constructor with two point. I have to compute Vx e Vy from the given points
    public StateVector(Point firstPoint, Point secondPoint){
        this.x = secondPoint.getX();
        this.y = secondPoint.getY();

        Double magnitude = Math.sqrt(Math.pow(secondPoint.getX() - firstPoint.getX(),2.0) + Math.pow(secondPoint.getY() - firstPoint.getY(),2.0)); // calculate magnitude of the vector
        Double angle = Math.atan2(secondPoint.getY() - firstPoint.getY(), secondPoint.getX() - firstPoint.getX()); // calculate the angle (direction) of the vector

        this.Vx = magnitude * Math.cos(angle);
        this.Vy = magnitude * Math.sin(angle);
    }

    //getter for the variable
    public Double getX() { return this.x; }

    public Double getY() { return this.y; }

    public Double getVx() { return this.Vx; }

    public Double getVy() { return this.Vy;}


}
