package nl.tno.idsa.framework.kalman_filter;

import nl.tno.idsa.framework.force_field.ForceField;
import nl.tno.idsa.framework.potential_field.ParameterNotDefinedException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alessandrozonta on 14/07/16.
 */
//Class implemementing matrix and how use it
public class MatrixDefinition {
    private final Double[][] matrix; // general matrix
    private final Integer row; // number of matrix rows
    private final Integer column; // number of matrix columns
    private final Double dt; //time step

    //normal constructor
    public MatrixDefinition(){
        this.matrix = null;
        this.row = null;
        this.column = null;
        this.dt = null;
    }

    //constructor with two parameters -> Dimension of the matrix
    public MatrixDefinition(Integer row, Integer column){
        this.row = row;
        this.column = column;
        this.matrix = new Double[this.row][this.column];
        this.dt = null;
        for(int i = 0; i < this.row; i++) for (int j = 0; j < this.column; j++) this.matrix[i][j] = 0.0; //initialize matrix with O.o in every cells
    }

    //constructor with three parameters -> Dimension of the matrix and the timestep
    public MatrixDefinition(Integer row, Integer column, Double timeStep){
        this.row = row;
        this.column = column;
        this.matrix = new Double[this.row][this.column];
        this.dt = timeStep;
        for(int i = 0; i < this.row; i++) for (int j = 0; j < this.column; j++) this.matrix[i][j] = 0.0; //initialize matrix with O.o in every cells
    }

    //constructor with four parameters -> Dimension of the matrix, timestep and initial value for the matrix
    public MatrixDefinition(Integer row, Integer column, Double timeStep, Double value){
        this.row = row;
        this.column = column;
        this.matrix = new Double[this.row][this.column];
        this.dt = timeStep;
        for(int i = 0; i < this.row; i++) for (int j = 0; j < this.column; j++) this.matrix[i][j] = value; //initialize matrix with O.o in every cells
    }

    //constructor with two parameters. The matrix and the timestep

    //getter for the matrix
    public Double[][] getMatrix(){
        return this.matrix;
    }

    //getter for timestep
    public Double getTimeStep(){
        return  this.dt;
    }

    //getter for row
    public Integer getRow(){
        return  this.row;
    }

    //getter for column
    public Integer getColumn(){
        return  this.column;
    }

    //multiply the matrix for a scalar
    public void multiplyFor(Double var){
        for(int i = 0; i < this.row; i++){
            for(int j = 0; j < this.column; j++){
                this.matrix[i][j] *= var;
            }
        }
    }

    //multiply the matrix by a statevector
    //I know state vector is [1xN] (parameter)
    //I know matrix is [NxN] (property of the class)
    //result should be [Nx1] (StateVector)
    //Throws Exception if the number of element into the matrix are not correct
    public StateVector multiplyFor(StateVector var) throws ParameterNotDefinedException{
        List<Double> result = new ArrayList<>();
        for(int i = 0; i < var.size(); i++){
            Double comp = 0.0;
            for(int j = 0; j < this.matrix.length; j++){
                comp += var.get(j) * this.matrix[i][j];
            }
            result.add(comp);
        }
        return new StateVector(result);
    }

    //multiply MatrixDefinition for MatrixDefinitio (same number of row and column)
    public MatrixDefinition multiplyFor(MatrixDefinition matrix) throws DifferentMatrixException {
        if (!this.column.equals(matrix.getRow())) throw new DifferentMatrixException("Error with the matrix");
        MatrixDefinition newMatrix = new MatrixDefinition(this.row, matrix.getColumn());
        Double sum = 0.0;
        for(int i = 0; i < this.row; i++){
            for(int z = 0; z < matrix.getColumn(); z++){
                for(int j = 0; j < this.column; j++){
                    sum += this.matrix[i][j] * matrix.getMatrix()[j][z];
                }
                newMatrix.getMatrix()[i][z] = sum;
                sum = 0.0;
            }
        }
        return newMatrix;
    }

    //Return the transpose matrix
    public MatrixDefinition transposeMatrix(){
        MatrixDefinition temp = new MatrixDefinition(this.column,this.row,this.dt);
        for (int i = 0; i < this.row; i++)
            for (int j = 0; j < this.column; j++)
                temp.getMatrix()[j][i] = this.matrix[i][j];
        return temp;
    }

    //sum two MatrixDefinition together
    public MatrixDefinition sumWith(MatrixDefinition matrix) throws DifferentMatrixException{
        if (!this.row.equals(matrix.row)) throw new DifferentMatrixException("Error with the matrix");
        if (!this.column.equals(matrix.column)) throw new DifferentMatrixException("Error with the matrix");
        MatrixDefinition newMatrix = new MatrixDefinition(this.row, this.column);
        for(int i = 0; i < this.row; i++){
            for(int j = 0; j < this.column; j++){
                newMatrix.getMatrix()[i][j] += this.matrix[i][j] * matrix.getMatrix()[i][j];
            }
        }
        return newMatrix;
    }

}
