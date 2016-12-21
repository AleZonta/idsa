package nl.tno.idsa.trajectorySim;


import nl.tno.idsa.framework.config.ConfigFile;
import nl.tno.idsa.framework.simulator.TrajectorySim;
import nl.tno.idsa.framework.utils.DataSourceFinder;
import nl.tno.idsa.framework.world.World;
import nl.tno.idsa.framework.world.WorldGenerator;
import nl.tno.idsa.viewer.dialogs.DataSourceInterface;
import nl.tno.idsa.viewer.dialogs.DataSourceSelection;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by alessandrozonta on 27/09/16.
 */
public class TrajectorySimTest {
    @Test
    /**
     * Test if the run method of the class simulator is working
     */
    public void run() throws Exception {
        ConfigFile file = new ConfigFile();
        file.loadFile();
        TrajectorySim sim = new TrajectorySim(file.getSelectorSourceTracks());

        World world = null;
        //load old world only if I am loading idsa
        if (file.getSelectorSourceTracks() == 0) {
            System.out.println("Loading idsa map simulator...");
            //initialise the data source with the hardcoded version
            DataSourceInterface dataSourceSelectionDialog = new DataSourceSelection();
            DataSourceFinder.DataSource dataSource = dataSourceSelectionDialog.getSelectedDataSource();
            System.out.println("Data source selected is" + dataSource.getPath() + "...");
            // Create the world object.
            String path = dataSource.getPath();
            world = WorldGenerator.generateWorld(dataSource.getWorldModel(),
                    path + "/idsa_nav_network_pedestrian.shp",
                    path + "/idsa_pand_osm_a_utm31n.shp",   // TODO File names are partially Dutch and not fully informative.
                    path + "/idsa_public_areas_a_utm31n.shp",
                    path + "/idsa_vbo_utm31n.shp",          // TODO File names are partially Dutch and not fully informative.
                    path + "/idsa_pand_p_utm31n.shp");      // TODO File names are partially Dutch and not fully informative.
            //DO i need info - > NO
            //DO i need environment - > NO
            //Do i need population in this case? ->  NO
            //Do i need agenda? ->  NO
            //Do i need police station? -> NO
            world.resetAreas();
        }

        //s1,s2,w1,w2
        //s1=0.25
        //w1=0.01
        //s2=0.5
        //w2=0.5
        sim.initPotentialField(file,180.0, 0.25 ,0.5, 0.01, 0.5, "testGH", "testGH", world);
//        sim.init(99999);
//        sim.run();
        sim.init_and_run(20, 40);
    }

    @Test
    /**
     * test if I can initialise correctly the  PF
     */
    public void initPotentialField() throws Exception {
        ConfigFile file = new ConfigFile();
        file.loadFile();
        TrajectorySim sim = new TrajectorySim(file.getSelectorSourceTracks());

        World world = null;
        //load old world only if I am loading idsa
        if (file.getSelectorSourceTracks() == 0) {
            System.out.println("Loading idsa map simulator...");
            //initialise the data source with the hardcoded version
            DataSourceInterface dataSourceSelectionDialog = new DataSourceSelection();
            DataSourceFinder.DataSource dataSource = dataSourceSelectionDialog.getSelectedDataSource();
            System.out.println("Data source selected is" + dataSource.getPath() + "...");
            // Create the world object.
            String path = dataSource.getPath();
            world = WorldGenerator.generateWorld(dataSource.getWorldModel(),
                    path + "/idsa_nav_network_pedestrian.shp",
                    path + "/idsa_pand_osm_a_utm31n.shp",   // TODO File names are partially Dutch and not fully informative.
                    path + "/idsa_public_areas_a_utm31n.shp",
                    path + "/idsa_vbo_utm31n.shp",          // TODO File names are partially Dutch and not fully informative.
                    path + "/idsa_pand_p_utm31n.shp");      // TODO File names are partially Dutch and not fully informative.
            //DO i need info - > NO
            //DO i need environment - > NO
            //Do i need population in this case? ->  NO
            //Do i need agenda? ->  NO
            //Do i need police station? -> NO
            world.resetAreas();
        }

        sim.initPotentialField(file,90.0, 0.1 ,-0.0001, null, null, "test", "test", world);
    }

    /**
     * Test if it initialise correctly the class simulator
     * @throws Exception
     */
    @Test
    public void init() throws Exception {
        ConfigFile file = new ConfigFile();
        file.loadFile();
        TrajectorySim sim = new TrajectorySim(file.getSelectorSourceTracks());

        World world = null;
        //load old world only if I am loading idsa
        if (file.getSelectorSourceTracks() == 0) {
            System.out.println("Loading idsa map simulator...");
            //initialise the data source with the hardcoded version
            DataSourceInterface dataSourceSelectionDialog = new DataSourceSelection();
            DataSourceFinder.DataSource dataSource = dataSourceSelectionDialog.getSelectedDataSource();
            System.out.println("Data source selected is" + dataSource.getPath() + "...");
            // Create the world object.
            String path = dataSource.getPath();
            world = WorldGenerator.generateWorld(dataSource.getWorldModel(),
                    path + "/idsa_nav_network_pedestrian.shp",
                    path + "/idsa_pand_osm_a_utm31n.shp",   // TODO File names are partially Dutch and not fully informative.
                    path + "/idsa_public_areas_a_utm31n.shp",
                    path + "/idsa_vbo_utm31n.shp",          // TODO File names are partially Dutch and not fully informative.
                    path + "/idsa_pand_p_utm31n.shp");      // TODO File names are partially Dutch and not fully informative.
            //DO i need info - > NO
            //DO i need environment - > NO
            //Do i need population in this case? ->  NO
            //Do i need agenda? ->  NO
            //Do i need police station? -> NO
            world.resetAreas();
        }

        sim.initPotentialField(file,90.0, 0.1 ,-0.0001, null, null, "test", "test", world);
        sim.init(5);
        assertEquals(5, sim.getLocalParticipant().size());
        assertEquals(5*3, sim.getTra().getListOfPOIs().size());
    }



}