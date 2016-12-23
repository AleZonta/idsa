package nl.tno.idsa.framework.kalman_filter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alessandrozonta on 23/12/2016.
 * References
 ----------
 Wikipedia http://en.wikipedia.org/wiki/Kalman_filter#Fixed-lag_smoother
 https://github.com/rlabbe/filterpy/blob/master/filterpy/kalman/fixed_lag_smoother.py
 Simon, Dan. "Optimal State Estimation," John Wiley & Sons pp 274-8 (2006).
 |
 |

 */
public class FixedLagSmoother extends KalmanFilter {
    private final Integer sizeLag; //size of the lag
    private Integer count;
    private final List<StateVector> xSmooth;
    private Boolean end;
    private Integer countDown;

    /**
     * Create a fixed lag Kalman filter smoother.
     * @param sizeLag the size of the lag.
     */
    public FixedLagSmoother(Integer sizeLag){
        super(1.0); //construct kalman filter with timestamp 1
        this.sizeLag = sizeLag;
        this.count = 0;
        this.xSmooth = new ArrayList<>();
        this.end = Boolean.FALSE;
        this.countDown = 0;
    }

    /**
     * Set end Of the file
     */
    public void setEnd() { this.end = Boolean.TRUE; }

    /**
     * Smooths the measurement using a fixed lag smoother.
     * On return, xSmooth is populated with the N previous smoothed
     * estimates,  where xSmooth[k] is the kth time step. x
     * merely contains the current Kalman filter output of the most recent
     * measurement, and is not smoothed at all (beyond the normal Kalman
     * filter processing).
     * @param x first element of measurement to be smoothed
     * @param y second element of measurement to be smoothed
     * @throws Exception error in deep copy
     */
    public void smooth(Double x, Double y) throws Exception {
        if(this.end) return;
        Integer k = this.count;
        //measurement to be smoothed
        super.setMeasurement(x,y);
        //prediction step of normal Kalman filter
        super.predictionPhase();
        //save X_pre
        StateVector x_pre;
        try {
            x_pre = super.getX().deepCopy();
        } catch (DifferentMatrixException e) {
            throw new Exception("Error");
        }
        //update step of normal Kalman filter
        super.updatePhase();

        this.xSmooth.add(x_pre);

        //compute invariants
        MatrixDefinition HTSI = super.getH().transposeMatrix().multiplyFor(super.getS().inverseMatrix());
        MatrixDefinition F_LH = (super.getF().differenceWith(super.getK().multiplyFor(super.getH()))).transposeMatrix();

        if (k >= this.sizeLag){
            MatrixDefinition PS = super.getP().deepCopy(); // smoothed P for step i
            for (int i = 0; i < this.sizeLag; i++){
                MatrixDefinition K = PS.multiplyFor(HTSI);
                PS = PS.multiplyFor(F_LH);

                Integer si = k - i;
                this.xSmooth.set(si ,this.xSmooth.get(si).sumWith(K.multiplyFor(super.getY())));
            }
        }else{
            // Some sources specify starting the fix lag smoother only
            // after N steps have passed, some don't. My source is getting
            // better results by starting only at step N. -> I am implementing his version
            this.xSmooth.set(k, super.getX());
        }
        this.count++;
    }

    /**
     * Get Smoothed point
     * @return Statevector with the ltest smoothed point
     * @throws Exception
     */
    public StateVector getSmoothedPoint() throws Exception {
        //I know the size of the leg, so I return the first element smoothed
        if(this.xSmooth.size() < this.sizeLag) throw new Exception("Not Smoothed");
        if(!this.end){
            return this.xSmooth.get(this.xSmooth.size() - this.sizeLag);
        }else{
            this.countDown++;
            Integer val = this.sizeLag - this.countDown;
            if (val < 0) return null;
            return this.xSmooth.get(this.xSmooth.size() - val);
        }

    }




}
