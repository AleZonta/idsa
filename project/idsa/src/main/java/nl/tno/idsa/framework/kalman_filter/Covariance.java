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
            this.getMatrix()[i][i] = value;
        }
    }
}
