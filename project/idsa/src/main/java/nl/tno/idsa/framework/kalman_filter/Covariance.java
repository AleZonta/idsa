package nl.tno.idsa.framework.kalman_filter;

/**
 * Created by alessandrozonta on 18/07/16.
 */
//Implement the covariance -> P
public class Covariance extends MatrixDefinition {

    //constructor with covariance value
    public Covariance(Double value){
        //4 rows and columns -> number of row and columns present in the state transition function
        super(4,4); //calling father constructor with two parameters (dimension of the matrix)

        //populate the matrix with values. The diagonal is all equal to value
        for(int i = 0; i < 4; i++){
            this.setElement(i,i,value);
        }
    }

    //constructor with matrix
    public Covariance(MatrixDefinition matrix){
        //4 rows and columns -> number of row and columns present in the state transition function
        super(4,4); //calling father constructor with two parameters (dimension of the matrix)

        //populate the matrix with values. The diagonal is all equal to value
        for(int i = 0; i < matrix.getRow(); i++){
            for(int j = 0; j < matrix.getColumn(); j++){
                this.setElement(i,j,matrix.getElement(i,j));
            }
        }
    }

    // Multiplication between Covariance and Covariance that it returns a Covariance matrix
    // tested
    public Covariance multiplyFor(Covariance matrix) throws DifferentMatrixException{
        MatrixDefinition result = super.multiplyFor(matrix);
        if(result.getRow() != 4 && result.getColumn() != 4) throw new DifferentMatrixException("Error with the matrix");
        return new Covariance(result);
    }

    //Sum between a Covariance matrix and a process noise matrix that it returns a Covariance matrix
    // tested
    public Covariance sumWith(ProcessNoise matrix) throws DifferentMatrixException{
        MatrixDefinition result = super.sumWith(matrix);
        return new Covariance(result);
    }

    // Difference between Covariance and MatrixDefinition that return a Covariance
    // Tested
    public Covariance differenceWith(MatrixDefinition matrix) throws DifferentMatrixException{
        MatrixDefinition result = super.differenceWith(matrix);
        if(result.getRow() != 4 && result.getColumn() != 4) throw new DifferentMatrixException("Error with the matrix");
        return new Covariance(result);
    }
}
