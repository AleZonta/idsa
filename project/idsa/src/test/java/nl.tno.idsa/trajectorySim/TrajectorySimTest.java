package nl.tno.idsa.trajectorySim;


import nl.tno.idsa.framework.config.ConfigFile;
import nl.tno.idsa.framework.simulator.TrajectorySim;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by alessandrozonta on 27/09/16.
 */
public class TrajectorySimTest {
    @Test
    /**
     * Test if the run method of the class simulator is working
     */
    public void run() throws Exception {
        TrajectorySim sim = new TrajectorySim();
        ConfigFile file = new ConfigFile();
        file.loadFile();
        sim.initPotentialField(file,90.0, 0.1 ,-0.0001, null, null, "test", "test");
        sim.init(5);
        sim.run();
    }

    @Test
    /**
     * test if I can initialise correctly the  PF
     */
    public void initPotentialField() throws Exception {
        TrajectorySim sim = new TrajectorySim();
        ConfigFile file = new ConfigFile();
        file.loadFile();
        sim.initPotentialField(file,90.0, 0.1 ,-0.0001, null, null, "test", "test");
    }

    /**
     * Test if it initialise correctly the class simulator
     * @throws Exception
     */
    @Test
    public void init() throws Exception {
        TrajectorySim sim = new TrajectorySim();
        ConfigFile file = new ConfigFile();
        file.loadFile();
        sim.initPotentialField(file,90.0, 0.1 ,-0.0001, null, null, "test", "test");
        sim.init(5);
        assertEquals(5, sim.getLocalParticipant().size());
        assertEquals(5, sim.getTra().getListOfPOIs().size());
    }



}