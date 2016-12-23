package nl.tno.idsa.framework.kalman_filter;

import nl.tno.idsa.framework.potential_field.ParameterNotDefinedException;
import nl.tno.idsa.framework.world.Point;

import java.util.List;

/**
 * Created by alessandrozonta on 01/07/16.
 */
//State Vector for kalman filter. In our case it implements position (x,y) and velocity vector (Vx,Vy).
//extend matrixDefinition. StateVector is a pecial case of Matrix
//It is matrix [4][1]
//Remember that it is ordered like [x Vx y Vy]
public class StateVector extends MatrixDefinition {

    //Normal constructor
    public StateVector(){
        super(4,1);
    }

    //Constructor for elemets
    public StateVector(List<Double> element) throws Exception{
        super(4,1);
        if (element.size() < 4 || element.size() > 4) throw new Exception("List with wrong size");
        this.setElement(0,0,element.get(0)); //x
        this.setElement(1,0,element.get(1)); //Vx
        this.setElement(2,0,element.get(2)); //y
        this.setElement(3,0,element.get(3)); //Yy
    }

    //constructor with one element. A matrix for per one
    public StateVector(MatrixDefinition matrix) throws Exception{
        super(4,1);
        if (matrix.getRow() != 4 || matrix.getColumn() != 1) throw new Exception("Matrix with wrong size");
        this.setElement(0,0,matrix.getElement(0,0)); //x
        this.setElement(1,0,matrix.getElement(1,0)); //Vx
        this.setElement(2,0,matrix.getElement(2,0)); //y
        this.setElement(3,0,matrix.getElement(3,0)); //Yy
    }

    //constructor with two points. I have to compute Vx e Vy from the given points
    //The points are indicating a vector
    public StateVector(Point firstPoint, Point secondPoint){
        super(4,1);
        this.setElement(0,0,firstPoint.getX()); //x
        this.setElement(2,0,firstPoint.getY()); //y


        Double magnitude = Math.sqrt(Math.pow(secondPoint.getX() - firstPoint.getX(),2.0) + Math.pow(secondPoint.getY() - firstPoint.getY(),2.0)); // calculate magnitude of the vector
        Double angle = Math.atan2(secondPoint.getY() - firstPoint.getY(), secondPoint.getX() - firstPoint.getX()); // calculate the angle (direction) of the vector

        this.setElement(1,0,magnitude * Math.cos(angle)); //Vx
        this.setElement(3,0,magnitude * Math.sin(angle)); //Vy

    }



    //getter for the variable
    public Double getX() { return this.getElement(0,0);} //x

    public Double getVx() { return this.getElement(1,0);} //Vx

    public Double getY() { return this.getElement(2,0);} //y

    public Double getVy() { return this.getElement(3,0);} //Vy

    //return number of element inside StateVector
    //tested
    public Integer size(){
        return this.getRow();
    }

    //return element state vector by index
    //Remember that it is ordered like [x Vx y Vy]
    //throws exception if I am trying to access to a member not defined
    //tested
    public Double get(Integer index) throws ParameterNotDefinedException{
        switch (index){
            case 0:
                return this.getElement(0,0);
            case 1:
                return this.getElement(1,0);
            case 2:
                return this.getElement(2,0);
            case 3:
                return this.getElement(3,0);
            default:
                throw new ParameterNotDefinedException("Index not defined into StateVector");
        }
    }


    // Sum between StateVector and StateVector that return a StateVector
    // tested
    @Override
    public StateVector sumWith(MatrixDefinition vector) throws DifferentMatrixException{
        MatrixDefinition result = super.sumWith(vector);
        try {
            return new StateVector(result);
        }catch (Exception e){
            throw new DifferentMatrixException("Matrix with wrong size");
        }
    }

    @Override
    public StateVector deepCopy() throws DifferentMatrixException {
        MatrixDefinition result = super.deepCopy();
        try {
            return new StateVector(result);
        } catch (Exception e) {
            throw new DifferentMatrixException("Matrix with wrong size");
        }
    }
}
