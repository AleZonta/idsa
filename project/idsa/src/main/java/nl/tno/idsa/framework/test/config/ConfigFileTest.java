package nl.tno.idsa.framework.test.config;

import nl.tno.idsa.framework.config.ConfigFile;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by alessandrozonta on 24/08/16.
 */
public class ConfigFileTest {
    @Test
    public void loadFile() throws Exception {
        ConfigFile file = new ConfigFile();
        file.loadFile();

        assertEquals(Boolean.TRUE, file.getGUI());
        assertEquals(Boolean.TRUE, file.getTileOptimisation());
        assertEquals(new Double(1.0), file.getCommonInitialCharge());
        assertEquals(new Double(400.0), file.getThresholdPotential());
        assertEquals(new Double(100000.0), file.getConstantPotential());
        assertEquals(new Double(10.0), file.getDifferentCellSize().get(0.0));
        assertEquals(new Double(50.0), file.getDifferentCellSize().get(1.0));
        assertEquals(new Double(100.0), file.getDifferentCellSize().get(2.0));
        assertEquals(new Double(500.0), file.getDifferentCellSize().get(3.0));
        assertEquals(new Double(1000.0), file.getDifferentCellSize().get(4.0));

    }

}