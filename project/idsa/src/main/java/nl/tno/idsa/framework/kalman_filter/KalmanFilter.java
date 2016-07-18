package nl.tno.idsa.framework.kalman_filter;

import nl.tno.idsa.framework.potential_field.ParameterNotDefinedException;

/**
 * Created by alessandrozonta on 01/07/16.
 */
//Kalman Filter implementation
public class KalmanFilter {

    private StateVector x; // x -> state vector for kalman filter. Everywhere is called x, so I am calling it in that way.
    private final StateTransiction F; // F -> state transition function. Recall that the state transition function is implemented as a matrix FF that we multiply with the previous state of our system to get the next state
    private final Integer B = 0; // B  -> Control Function. We don't have any control input so we put it to zero
    private final Measurement z; // z -> Measurements for (x,y)
    private final MeasurementFunction H; // H -> changes from state to measurement
    private final MeasurementNoise R; // R -> Noise present in measurement
    private final ProcessNoise Q; // Q -> process noise
    private final Double u; // u  -> control input
    private Covariance P; // P -> covariance matrix
    private MatrixDefinition S; // S -> system uncertainty
    private final Double TimeStep; //time step

    // Constructor
    public KalmanFilter(){
        this.x = null;
        this.F = null;
        this.z = null;
        this.H = null;
        this.R = null;
        this.Q = null;
        this.u = null;
        this.P = null;
        this.S = null;
        this.TimeStep = null;
    }

    //Prediction phase of kalman filter
    public void predictionPhase(){
        try {
            this.x = this.F.multiplyFor(this.x); // x = Fx -> throws ParameterNotDefinedException
            this.P = (Covariance) (this.F.multiplyFor(this.P).multiplyFor(this.F.transposeMatrix())).sumWith(this.Q); // P = FPF^ + Q -> throws DifferentMatrixException
        }catch (ParameterNotDefinedException | NullPointerException | DifferentMatrixException e){
            //We did not designed well our Kalman Filter.
            //TODO decide what to do with this exception
        }
    }

    //Update phase of kalman filter
    public void updatePhase(){
        try {
            this.S = (this.H.multiplyFor(this.P).multiplyFor(this.H.transposeMatrix())).sumWith(this.R); //  S = HPH^ + R -> throws DifferentMatrixException
        }catch ( NullPointerException | DifferentMatrixException e){
            //We did not designed well our Kalman Filter.
            //TODO decide what to do with this exception
        }
    }


}
