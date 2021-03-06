package nl.tno.idsa.viewer;

import nl.tno.idsa.Constants;
import nl.tno.idsa.framework.config.ConfigFile;
import nl.tno.idsa.framework.agents.Agent;
import nl.tno.idsa.framework.messaging.Messenger;
import nl.tno.idsa.framework.messaging.ProgressNotifier;
import nl.tno.idsa.framework.population.PopulationGenerator;
import nl.tno.idsa.framework.potential_field.PotentialField;
import nl.tno.idsa.framework.potential_field.save_to_file.LoadParameters;
import nl.tno.idsa.framework.simulator.Sim;
import nl.tno.idsa.framework.utils.DataSourceFinder;
import nl.tno.idsa.framework.utils.RandomNumber;
import nl.tno.idsa.framework.world.Environment;
import nl.tno.idsa.framework.world.Vertex;
import nl.tno.idsa.framework.world.World;
import nl.tno.idsa.framework.world.WorldGenerator;
import nl.tno.idsa.library.locations.PoliceSpawnPoint;
import nl.tno.idsa.viewer.components.ProgressDialog;
import nl.tno.idsa.viewer.dialogs.*;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Main entry point for the visual simulator.
 * */
public class GUI {


    public static void main(String[] args) throws Exception {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        System.out.println(dateFormat.format(cal.getTime()) + " Starting simulation...");

        // load config file
        ConfigFile conf = new ConfigFile();
        conf.loadFile();

        //load the GUI
        GUI simulator = new GUI();

        //loading parameter from inline conf
        //if arg is empty load normal rules otherwise load the file with than name
        if(args.length == 0){
            simulator.loadAndStartSimulation(conf,null,null,null,null,null,"Normal","Normal");
        }else{
            LoadParameters par = new LoadParameters(args[0]);
            simulator.loadAndStartSimulation(conf,par.getAlpha(), par.getS1(), par.getW1(), par.getS2(), par.getW2(), par.getName(), par.getExperiment());
        }

        //if I am here the sim is ended
        //0 is okay
        System.exit(0);
    }

    //Load and start the simulation
    private void loadAndStartSimulation(ConfigFile conf, Double degree, Double s1, Double s2, Double w1, Double w2, String name, String experiment){
        //check if I am showing the GUI
        Boolean GUI = conf.getGUI();
        //If I am using the GUI I will show it otherwise no
        DataSourceFinder.DataSource dataSource = null;
        ProgressDialog progressDialog = null;
        if(GUI) {
            // Create the progress dialog.
            progressDialog = new ProgressDialog(null);
            ProgressNotifier.addObserver(progressDialog);
            ProgressNotifier.notifyShowProgress(true);
            Messenger.enableMirrorToConsole(true); // TODO Catch console messages in a graphical element.

            // Ask the user which data must be loaded.
            ProgressNotifier.notifyProgressMessage("Loading world data...");
            ProgressNotifier.notifyUnknownProgress();
            DataSourceInterface dataSourceSelectionDialog = new DataSourceSelectionDialog(progressDialog);
            if (dataSourceSelectionDialog.isCancelled()) {
                System.exit(0);
            }
            if (!dataSourceSelectionDialog.areDataSourcesPresent()) {
                JOptionPane.showMessageDialog(null, "No data sources found", "No data sources found", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
            dataSource = dataSourceSelectionDialog.getSelectedDataSource();
        }else{
            //initialise the data source with the hardcoded verion
            DataSourceInterface dataSourceSelectionDialog = new DataSourceSelection();
            dataSource = dataSourceSelectionDialog.getSelectedDataSource();
            System.out.println("Data source selected is" + dataSource.getPath() + "...");
        }

        // Create the world object.
        String path = dataSource.getPath();
        World world = WorldGenerator.generateWorld(dataSource.getWorldModel(),
                path + "/idsa_nav_network_pedestrian.shp",
                path + "/idsa_pand_osm_a_utm31n.shp",   // TODO File names are partially Dutch and not fully informative.
                path + "/idsa_public_areas_a_utm31n.shp",
                path + "/idsa_vbo_utm31n.shp",          // TODO File names are partially Dutch and not fully informative.
                path + "/idsa_pand_p_utm31n.shp");      // TODO File names are partially Dutch and not fully informative.

        // Ask the user for season, time, day, et cetera.
        MultiplierSettingInterface ssd;
        if(GUI){
            ssd = new MultiplierSettingDialog(progressDialog, null);

        }else{
            System.out.println("Loading time information...");
            ssd = new MultiplierSetting(conf);
        }
        if (ssd.isCancelled()) {
            System.exit(0);
        }

        // Create the environment.
        if(GUI){
            ProgressNotifier.notifyProgressMessage("Creating environment...");
        }else{
            System.out.println("Creating environment...");
        }
        Environment env = ssd.createEnvironmentWithSettings(world);

        // Create a population.
        if(GUI){
            ProgressNotifier.notifyProgressMessage("Creating population...");
            ProgressNotifier.notifyUnknownProgress();
        }else{
            System.out.println("Creating population...");
        }
        PopulationGenerator populationGenerator = new PopulationGenerator(env, dataSource.getPopulationDataProvider());
        List<Agent> population = populationGenerator.generatePopulation(path + "/idsa_cbs_buurten_utm31n.shp"); // TODO This also needs data-specific parsing. File name is partially Dutch.
        env.setPopulation(population);

        // Create agendas.
        if(GUI){
            ProgressNotifier.notifyProgressMessage("Creating agendas if needed...");
        }else{
            System.out.println("Creating agendas if needed...");
        }
        env.initializePopulation(env.getSeason(), null, env.getDay(), env.getTime(), Constants.AGENDA_ENABLED);

        // Add some police stations, randomly, as they are not in the world yet.
        // TODO Create police stations in the world.
        if(GUI){
            ProgressNotifier.notifyProgressMessage("Enriching environment...");
        }else{
            System.out.println("Enriching environment...");
        }
        List<Vertex> vertices = world.getVertices();
        int changedVertices = 0;
        while (changedVertices < 50) {
            Vertex randomVertex = vertices.get(RandomNumber.nextInt(vertices.size()));
            if (randomVertex.getArea() != null) {
                randomVertex.getArea().addFunction(new PoliceSpawnPoint());
                changedVertices++;
            }
        }


        //Loading the potential field
        if(GUI){
            ProgressNotifier.notifyProgressMessage("Loading potential field...");
            ProgressNotifier.notifyUnknownProgress();
        }else{
            System.out.println("Loading potential field...");
        }
        //The problem here is if I want to track only one person or more. I need a potential field per person
        //From the config I need to check this and modify everything to support more than one potential field
        //But here I don't know how many pot I would need. I can create only one and then copy for the number of time that i need
        PotentialField pot = new PotentialField(world, conf, degree , s1, s2, w1 , w2, name, experiment);

        if(GUI) {
            ProgressNotifier.notifyProgress(100);

            // Hide progress notifier.
            ProgressNotifier.notifyShowProgress(false);
            ProgressNotifier.removeObserver(progressDialog);
        }

        // Create sim.
        Sim sim = Sim.getInstance();
        sim.init(env);
        sim.setPot(pot); // set instance of Potential Field into the simulation

        if(GUI) {
            // Open the viewer.
            System.out.println("Creating viewer...");
            MainFrame mf = new MainFrame(sim);
            mf.show();
        }else{//If I am not using the GUI i should select all the point that I need to track
            System.out.println("Connecting the potential field to the people tracked...");
            ReplacementForMainFrame mf = new ReplacementForMainFrame(sim,conf.getMaxNumberOfTrackedPeople());
            sim.setMain(mf);
            //track all the people outside a building
            mf.trackEveryone();
        }

        // Start the sim.
        System.out.println("Starting simulator...");
        sim.setMaxXRealTime(30);
        sim.start();
    }
}


