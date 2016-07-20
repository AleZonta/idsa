package nl.tno.idsa.framework.kalman_filter;

/**
 * Created by alessandrozonta on 14/07/16.
 */
//Implement the process noise
// matrix containing the process noise -> Q
public class ProcessNoise extends MatrixDefinition {

    //normal constructor
    public ProcessNoise(Double dt){
        //2 is the number of row present in measurement TODO make not hardcoded here
        super(4,4, dt);
        //TODO set noise
        //For simplicity I will assume the noise is a discrete time Wiener process - that it is constant for each time period. This assumption allows me to use a variance to specify how much I think the model changes between steps.
        //Here I assume the noise in x and y are independent, so the covariances between any x and y variable should be zero.
        this.q_discrete_white_noise();
    }

    //Returns the Q matrix for the Discrete Constant White Noise Model. It is using the implementation found at https://github.com/rlabbe/filterpy/blob/master/filterpy/common/discretization.py semplified for our used
    //TODO make not hardcoded in this way
    private void q_discrete_white_noise(){

        this.setElement(0,0,0.25 * Math.pow(this.getTimeStep(),4.0));
        this.setElement(0,1,0.5 * Math.pow(this.getTimeStep(),3.0));
        this.setElement(1,0,0.5 * Math.pow(this.getTimeStep(),3.0));
        this.setElement(1,1,Math.pow(this.getTimeStep(), 2.0));
        this.setElement(2,2,this.getElement(0,0));
        this.setElement(2,3,this.getElement(0,1));
        this.setElement(3,2,this.getElement(1,0));
        this.setElement(3,3,this.getElement(1,1));
        this.multiplyFor(0.001); //variance = 0.001 Hardcoded here
    }
}
