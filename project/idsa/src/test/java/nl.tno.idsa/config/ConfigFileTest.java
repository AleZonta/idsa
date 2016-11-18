package nl.tno.idsa.config;

import nl.tno.idsa.framework.config.ConfigFile;
import nl.tno.idsa.framework.world.Time;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by alessandrozonta on 24/08/16.
 */
public class ConfigFileTest {
    @Test
    public void loadFile() throws Exception {
        ConfigFile file = new ConfigFile();
        file.loadFile();

        assertEquals(Boolean.FALSE, file.getGUI());
        assertEquals(Boolean.FALSE, file.getTileOptimisation());
        assertEquals(new Double(1.0), file.getCommonInitialCharge());
        assertEquals(new Double(400.0), file.getThresholdPotential());
        assertEquals(new Double(100000.0), file.getConstantPotential());
        assertEquals(new Double(10.0), file.getDifferentCellSize().get(0.0));
        assertEquals(new Double(50.0), file.getDifferentCellSize().get(1.0));
        assertEquals(new Double(100.0), file.getDifferentCellSize().get(2.0));
        assertEquals(new Double(500.0), file.getDifferentCellSize().get(3.0));
        assertEquals(new Double(1000.0), file.getDifferentCellSize().get(4.0));
        assertEquals(new Integer(0), file.getDayOfWeek());
        assertEquals(new Integer(0), file.getSeason());
        assertEquals(new Integer(0), file.getTimeOfTheYear());
        assertEquals(new Time(12,0,0).getHour(), file.getTime().getHour());
        assertEquals(new Time(12,0,0).getMinute(), file.getTime().getMinute());
        assertEquals(new Time(12,0,0).getSecond(), file.getTime().getSecond());
        assertEquals(new Integer(5), file.getMaxNumberOfTrackedPeople());
        assertEquals(new Integer(0), file.getPerformance());
        assertEquals(new Integer(2), file.getHeatMap());
        assertEquals(new Integer(0), file.getPOIs());
        assertEquals(new Integer(0), file.getUpdateRules());
        assertEquals(Boolean.FALSE, file.getGdsi());
        assertEquals(Boolean.FALSE, file.getSelectPerson());
        assertEquals(Boolean.FALSE, file.getSelectUR());
        assertEquals(Boolean.FALSE, file.getFileFromThisLocation());
        assertEquals("../..", file.getSourceData());
//        assertEquals("/var/scratch/ama228", file.getDestinationData());

    }

}