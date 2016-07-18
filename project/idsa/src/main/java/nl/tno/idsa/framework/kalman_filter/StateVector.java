package nl.tno.idsa.framework.kalman_filter;

import nl.tno.idsa.framework.potential_field.ParameterNotDefinedException;
import nl.tno.idsa.framework.world.Point;

import java.util.List;

/**
 * Created by alessandrozonta on 01/07/16.
 */
//State Vector for kalman filter. In our case it implements position (x,y) and velocity vector (Vx,Vy). StateVector is a subclass of measurement -> x
//Remember that it is ordered like [x Vx y Vy]
public class StateVector extends Measurement {
    private final Double Vx;
    private final Double Vy;

    //Normal constructor
    public StateVector(){
        super(); //call constructor of Measurement class
        this.Vx = null;
        this.Vy = null;
    }

    //Constructor for elemets
    public StateVector(List<Double> element){
        super(element.get(0),element.get(1));
        this.Vx = element.get(2);
        this.Vy = element.get(3);
    }

    //constructor with two points. I have to compute Vx e Vy from the given points
    public StateVector(Point firstPoint, Point secondPoint){
        super(secondPoint);  //call constructor of Measurement class

        Double magnitude = Math.sqrt(Math.pow(secondPoint.getX() - firstPoint.getX(),2.0) + Math.pow(secondPoint.getY() - firstPoint.getY(),2.0)); // calculate magnitude of the vector
        Double angle = Math.atan2(secondPoint.getY() - firstPoint.getY(), secondPoint.getX() - firstPoint.getX()); // calculate the angle (direction) of the vector

        this.Vx = magnitude * Math.cos(angle);
        this.Vy = magnitude * Math.sin(angle);
    }

    //getter for the variable
    public Double getVx() { return this.Vx; }

    public Double getVy() { return this.Vy;}

    //return number of element inside StateVector
    public Integer size(){
        return 4;
    }

    //return element state vector by index
    //Remember that it is ordered like [x Vx y Vy]
    //throws exception if I am trying to access to a member not defined
    public Double get(Integer index) throws ParameterNotDefinedException{
        switch (index){
            case 0:
                return this.getX();
            case 1:
                return this.Vx;
            case 2:
                return this.getY();
            case 3:
                return this.Vy;
            default:
                throw new ParameterNotDefinedException("Index not defined into StateVector");
        }
    }

}
