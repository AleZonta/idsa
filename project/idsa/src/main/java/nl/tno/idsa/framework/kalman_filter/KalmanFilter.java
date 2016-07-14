package nl.tno.idsa.framework.kalman_filter;

/**
 * Created by alessandrozonta on 01/07/16.
 */
//Kalman Filter implementation
public class KalmanFilter {

    private final StateVector x; //state vector for kalman filter. Everywhere is called x, so I am calling it in that way.
    private final StateTransiction F; // state transition function. Recall that the state transition function is implemented as a matrix FF that we multiply with the previous state of our system to get the next state
    private final Integer B = 0; //Control Function. We don't have any control input so we put it to zero
    private final Measurement z; //Measurements for (x,y)
    private final MeasurementFunction H; //H  changes from state to measurement
    private final MeasurementNoise P; //Noise present in measurement
    private final ProcessNoise Q; //process noise
    private final Double u; //control input
    private final Double TimeStep; //time step

    // Constructor
    public KalmanFilter(){
        this.x = null;
        this.F = null;
        this.z = null;
        this.H = null;
        this.P = null;
        this.Q = null;
        this.u = null;
        this.TimeStep = null;
    }

    //Set initial condition


}
