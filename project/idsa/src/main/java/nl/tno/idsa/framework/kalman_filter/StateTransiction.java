package nl.tno.idsa.framework.kalman_filter;

/**
 * Created by alessandrozonta on 01/07/16.
 */
//design the state transition function. Recall that the state transition function is implemented as a matrix FF that we multiply with the previous state of our system to get the next state, like so. x¯ = Fx
// matrix containing the state transition function -> F
public class StateTransiction extends MatrixDefinition {

    //constructor with dt
    public StateTransiction(Double dt){
        //4 rows and columns -> number of row and columns present in the state transition function
        super(4,4, dt); //calling father constructor with two parameters (dimension of the matrix)

        //populate the matrix with values. The diagonal is all equal to 1
        for(int i = 0; i < 4; i++){
            this.getMatrix()[i][i] = 1.0;
        }
        //Now depends the order we are using. I am defining x Vx y Vy so Dt will be in [0][1] and [2][3]
        this.getMatrix()[0][1] = this.getMatrix()[2][3] = this.getTimeStep();
    }


}
