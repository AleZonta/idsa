package nl.tno.idsa.framework.kalman_filter;

import nl.tno.idsa.framework.potential_field.ParameterNotDefinedException;

/**
 * Created by alessandrozonta on 01/07/16.
 */
//Kalman Filter implementation
public class KalmanFilter {

    private StateVector x; // x -> state vector for kalman filter. Everywhere is called x, so I am calling it in that way.
    private final StateTransiction F; // F -> state transition function. Recall that the state transition function is implemented as a matrix F that we multiply with the previous state of our system to get the next state
    private final Integer B = 0; // B  -> Control Function. We don't have any control input so we put it to zero
    private final Measurement z; // z -> Measurements for (x,y)
    private final MeasurementFunction H; // H -> changes from state to measurement
    private final MeasurementNoise R; // R -> Noise present in measurement
    private final ProcessNoise Q; // Q -> process noise
    private final Double u; // u  -> control input
    private Covariance P; // P -> covariance matrix
    private MatrixDefinition S; // S -> system uncertainty
    private MatrixDefinition K; // K -> noise covariance
    private Measurement y; // y -> residual
    private final Double timeStep; //time step

    //getter
    public StateVector getX() {
        return this.x;
    }

    public Covariance getP() {
        return this.P;
    }

    // Constructor
    public KalmanFilter(){
        this.timeStep = null;
        this.x = null;
        this.P = null;
        this.F = null;
        this.H = null;
        this.R = null;
        this.z = null;
        this.Q = null;

        this.u = null;
        this.y = null;
        this.S = null;
        this.K = null;
    }

    //constructor with one parameter (time step)
    //I am initialising all the matrix with predefined values
    public KalmanFilter(Double timeStep){
        this.timeStep = timeStep; //time step
        this.x = new StateVector(); //In this way I am setting the initial position at (0,0) with velocity (0,0)
        this.P = new Covariance(500.0); //In this way I am setting a very big covariance since our initial position is a pure guess
        this.F = new StateTransiction(timeStep);
        this.H = new MeasurementFunction(); //I don't have transformation so i can use the value like they are.
        this.R = new MeasurementNoise(); //For now let's set the variance for xx and yy to be 5 meters22.
        this.z = new Measurement(); //Now the measurement is zero, we will update it later
        this.Q = new ProcessNoise(timeStep); //For simplicity I will assume the noise is a discrete time Wiener process
        this.u = null; //I don't have control input so I am not using this variable
        this.y = null; //I will update later this variable
        this.S = null; //I will update later this matrix
        this.K = null; //I will update later this matrix
    }

    //Prediction phase of kalman filter
    public void predictionPhase(){
        try {
            this.x = (StateVector) this.F.multiplyFor(this.x); // x = Fx -> throws ParameterNotDefinedException
            MatrixDefinition test = this.F.multiplyFor(this.P).multiplyFor(this.F.transposeMatrix());
            MatrixDefinition test1 = test.sumWith(this.Q);

            this.P = (Covariance) (this.F.multiplyFor(this.P).multiplyFor(this.F.transposeMatrix())).sumWith(this.Q); // P = FPF^ + Q -> throws DifferentMatrixException
        }catch (NullPointerException | DifferentMatrixException e){
            //We did not designed well our Kalman Filter.
            //TODO decide what to do with this exception
        }
    }

    //Update phase of kalman filter
    public void updatePhase(){
        try {
            this.S = (this.H.multiplyFor(this.P).multiplyFor(this.H.transposeMatrix())).sumWith(this.R); //  S = HPH^ + R -> throws DifferentMatrixException
            this.K = (this.P.multiplyFor(this.H.transposeMatrix())).multiplyFor(this.S.inverseMatrix()); // K = PH^(S^-1) -> throws DifferentMatrixException
            this.y = (Measurement) this.z.differenceWith(this.H.multiplyFor(this.x)); // y = z - Hx -> throws DifferentMatrixException
            this.x = (StateVector) this.x.sumWith(this.K.multiplyFor(this.y)); // x = x + Ky -> throws DifferentMatrixException
            this.P = (Covariance) this.P.differenceWith(this.K.multiplyFor(this.H).multiplyFor(this.P)); // P = P - KHP -> throws DifferentMatrixException
        }catch ( NullPointerException | DifferentMatrixException e){
            //We did not designed well our Kalman Filter.
            //TODO decide what to do with this exception
        }
    }


}
