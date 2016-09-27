package nl.tno.idsa.framework.test.trajectorySim;

import lgds.simulator.Simulator;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by alessandrozonta on 27/09/16.
 */
public class TrajectorySimTest {
    /**
     * Test if it initialise correctly the class simulator
     * @throws Exception
     */
    @Test
    public void init() throws Exception {
        Simulator sim = new Simulator();
        sim.init(40);
        assertEquals(40, sim.getParticipant().size());
        assertEquals(40, sim.getTra().getListOfPOIs().size());
    }

}