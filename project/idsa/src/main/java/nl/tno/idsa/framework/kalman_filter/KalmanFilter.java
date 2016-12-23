package nl.tno.idsa.framework.kalman_filter;

import nl.tno.idsa.framework.world.Point;

/**
 * Created by alessandrozonta on 01/07/16.
 */
//Kalman Filter implementation
public class KalmanFilter {

    private StateVector x; // x -> state vector for kalman filter. Everywhere is called x, so I am calling it in that way.
    private final StateTransition F; // F -> state transition function. Recall that the state transition function is implemented as a matrix F that we multiply with the previous state of our system to get the next state
    private final Integer B = 0; // B  -> Control Function. We don't have any control input so we put it to zero
    private Measurement z; // z -> Measurements for (x,y)
    private final MeasurementFunction H; // H -> changes from state to measurement
    private final MeasurementNoise R; // R -> Noise present in measurement
    private final ProcessNoise Q; // Q -> process noise
    private final Double u; // u  -> control input
    private Covariance P; // P -> covariance matrix
    private MatrixDefinition S; // S -> system uncertainty
    private MatrixDefinition K; // K -> noise covariance
    private Measurement y; // y -> residual
    private final Double timeStep; //time step
    private Boolean setInitialX; //checker initial position
    private Boolean setInitialP; //checker initial position

    //getter
    public StateVector getX() {
        return this.x;
    }

    public Covariance getP() {
        return this.P;
    }

    public MatrixDefinition getS() { return  this.S; }

    public MatrixDefinition getK() { return  this.K; }

    public Measurement getY() { return this.y; }

    protected MeasurementFunction getH() { return this.H; }

    protected StateTransition getF() { return this.F; }

    //set the new measurement
    public void setMeasurement(Double x, Double y){
        this.z = new Measurement(x,y);
    }

    //set the initial position
    public void setInitialPosition(Point position, Point velocity){
        this.x = new StateVector(position,velocity);
        this.setInitialX = Boolean.TRUE;
    }

    //set initial covariance
    public void setInitialCovariance(Double value){
        this.P = new Covariance(value);
        this.setInitialP = Boolean.TRUE;
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
        this.setInitialP = Boolean.FALSE;
        this.setInitialX = Boolean.FALSE;
    }

    //constructor with one parameter (time step)
    //I am initialising all the matrix with predefined values
    public KalmanFilter(Double timeStep){
        this.timeStep = timeStep; //time step
        this.x = new StateVector(); //In this way I am setting the initial position at (0,0) with velocity (0,0)
        this.P = new Covariance(500.0); //In this way I am setting a very big covariance since our initial position is a pure guess
        this.F = new StateTransition(timeStep);
        this.H = new MeasurementFunction(); //I don't have transformation so i can use the value like they are.
        this.R = new MeasurementNoise(); //For now let's set the variance for xx and yy to be 5 meters22.
        this.z = new Measurement(); //Now the measurement is zero, we will update it later
        this.Q = new ProcessNoise(timeStep); //For simplicity I will assume the noise is a discrete time Wiener process
        this.u = null; //I don't have control input so I am not using this variable
        this.y = null; //I will update later this variable
        this.S = null; //I will update later this matrix
        this.K = null; //I will update later this matrix
        this.setInitialP = Boolean.TRUE;
        this.setInitialX = Boolean.TRUE;
    }



    //  Prediction phase of kalman filter
    //  Tested
    public void predictionPhase() throws Exception {
        if(!this.setInitialP && !this.setInitialX) throw new Exception("Missing Initial Condition");
        try {
            this.x = this.F.multiplyFor(this.x); // x = Fx -> throws ParameterNotDefinedException
            this.P = (this.F.multiplyFor(this.P).multiplyFor(this.F.transposeMatrix())).sumWith(this.Q); // P = FPF^ + Q -> throws DifferentMatrixException
        }catch (NullPointerException | DifferentMatrixException e){
            //We did not designed well our Kalman Filter.
            //TODO decide what to do with this exception
        }
    }

    // Update phase of kalman filter
    // Tested
    public void updatePhase(){
        try {
            this.S = (this.H.multiplyFor(this.P).multiplyFor(this.H.transposeMatrix())).sumWith(this.R); //  S = HPH^ + R -> throws DifferentMatrixException
            this.K = (this.P.multiplyFor(this.H.transposeMatrix())).multiplyFor(this.S.inverseMatrix()); // K = PH^(S^-1) -> throws DifferentMatrixException
            this.y = this.z.differenceWith(this.H.multiplyFor(this.x)); // y = z - Hx -> throws DifferentMatrixException
            this.x = this.x.sumWith(this.K.multiplyFor(this.y)); // x = x + Ky -> throws DifferentMatrixException
            Covariance I = new Covariance(1.0); //  Identity matrix
            this.P =  (I.differenceWith(this.K.multiplyFor(this.H))).multiplyFor(this.P); // P = (I - KH)P -> throws DifferentMatrixException
        }catch ( NullPointerException | DifferentMatrixException e){
            //We did not designed well our Kalman Filter.
            //TODO decide what to do with this exception
        }
    }


}
