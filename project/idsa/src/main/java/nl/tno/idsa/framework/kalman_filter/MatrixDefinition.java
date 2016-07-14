package nl.tno.idsa.framework.kalman_filter;

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
    }

    //constructor with three parameters -> Dimension of the matrix and the timestep
    public MatrixDefinition(Integer row, Integer column, Double timeStep){
        this.row = row;
        this.column = column;
        this.matrix = new Double[this.row][this.column];
        this.dt = timeStep;
    }

    //getter for the matrix
    public Double[][] getMatrix(){
        return this.matrix;
    }

    //getter for timestep
    public Double getTimeStep(){
        return  this.dt;
    }

    //multiply the matrix for a scalar
    public void multiplyFor(Double var){
        for(int i = 0; i < this.row; i++){
            for(int j = 0; j < this.column; j++){
                this.matrix[i][j] *= var;
            }
        }
    }

}
