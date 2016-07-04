package nl.tno.idsa.framework.kalman_filter;

/**
 * Created by alessandrozonta on 01/07/16.
 */
//design the state transition function. Recall that the state transition function is implemented as a matrix FF that we multiply with the previous state of our system to get the next state, like so. x¯ = Fx
public class StateTransiction {

    private final Double dt; //time step
    private final Double[][] matrix; // matrix containing the state transition function

    public StateTransiction(){
        this.dt = null;
        this.matrix = new Double[4][4];
    }
}
