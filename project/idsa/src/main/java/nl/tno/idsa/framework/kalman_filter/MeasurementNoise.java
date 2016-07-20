package nl.tno.idsa.framework.kalman_filter;

/**
 * Created by alessandrozonta on 14/07/16.
 */
//Noise present in measurement
//matrix containing the measurement ->R
public class MeasurementNoise extends MatrixDefinition {

    //normal constructor
    public MeasurementNoise(){
        //2 is the number of row present in measurement TODO make not hardcoded here
        //It is a 2×2 matrix because we have 2 sensor inputs, and covariance matrices are always of size n×nn×n for nn variables.
        super(2,2);
        //TODO set noise
        //We assume that the xx and yy variables are independent white Gaussian processes.
        //That is, the noise in x is not in any way dependent on the noise in y, and the noise is normally distributed about the mean 0
        //For now let's set the variance for xx and yy to be 5 meters22. They are independent, so there is no covariance, and our off diagonals will be 0.
        this.setElement(0,0,5.0);
        this.setElement(1,1,5.0);
    }

}
