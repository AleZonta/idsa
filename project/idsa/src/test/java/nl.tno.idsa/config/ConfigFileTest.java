package config;

import nl.tno.idsa.framework.config.ConfigFile;
import nl.tno.idsa.framework.world.Time;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by alessandrozonta on 24/08/16.
 */
public class ConfigFileTest {
    @Test
    public void loadFile() throws Exception {
        ConfigFile file = new ConfigFile();
        file.loadFile();

        assertTrue(file.getTileOptimisation() == Boolean.FALSE || file.getTileOptimisation() == Boolean.TRUE);
        assertTrue(file.getGUI() == Boolean.FALSE || file.getGUI() == Boolean.TRUE);
        assertEquals(new Double(1.0), file.getCommonInitialCharge());
        assertEquals(new Double(400.0), file.getThresholdPotential());
        assertEquals(new Double(1000), file.getConstantPotential());
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
        assertTrue(0 <= file.getMaxNumberOfTrackedPeople() && file.getMaxNumberOfTrackedPeople() <= 800);
        assertTrue(0 <= file.getPerformance() && file.getPerformance() <= 2);
        assertTrue(0 <= file.getHeatMap() && file.getHeatMap() <= 2);
        assertTrue(0 <= file.getPOIs() && file.getPOIs() <= 2);
        assertTrue(0 <= file.getUpdateRules() && file.getUpdateRules() <= 8);
        assertTrue(file.getGdsi() == Boolean.FALSE || file.getGdsi() == Boolean.TRUE);
        assertTrue(file.getSelectPerson() == Boolean.FALSE || file.getSelectPerson() == Boolean.TRUE);
        assertTrue(file.getSelectUR() == Boolean.FALSE || file.getSelectUR() == Boolean.TRUE);
        assertTrue(file.getFileFromThisLocation() == Boolean.FALSE || file.getFileFromThisLocation() == Boolean.TRUE);
        assertTrue(!file.getSourceData().isEmpty());
        assertTrue(!file.getDestinationData().isEmpty());
        assertTrue(0 <= file.getSelectorSourceTracks() && file.getSelectorSourceTracks() <= 1);
        assertTrue(0 <= file.getWayPoints() && file.getWayPoints() <= 1);
        assertTrue(file.getPOIsAreClustered() == Boolean.FALSE || file.getPOIsAreClustered() == Boolean.TRUE);
        assertTrue(0 <= file.getLag() && file.getLag() <= 500);
        assertTrue(file.getSmoother() == Boolean.FALSE || file.getSmoother() == Boolean.TRUE);
        assertTrue(-500 <= file.getMorePOIs() && file.getMorePOIs() <= 500);

    }

}