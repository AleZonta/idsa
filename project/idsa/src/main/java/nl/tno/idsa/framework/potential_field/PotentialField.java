package nl.tno.idsa.framework.potential_field;

import lgds.routing.PathFinderGraphHopper;
import lgds.routing.Routing;
import lgds.viewer.View;
import nl.tno.idsa.framework.agents.Agent;
import nl.tno.idsa.framework.agents.TrajectoryAgent;
import nl.tno.idsa.framework.behavior.activities.concrete.Activity;
import nl.tno.idsa.framework.behavior.activities.possible.PossibleActivity;
import nl.tno.idsa.framework.config.ConfigFile;
import nl.tno.idsa.framework.force_field.ElectricPotential;
import nl.tno.idsa.framework.force_field.ForceField;
import nl.tno.idsa.framework.force_field.update_rules.DoublePacmanRule;
import nl.tno.idsa.framework.force_field.update_rules.PacmanRule;
import nl.tno.idsa.framework.force_field.update_rules.PacmanRuleDistance;
import nl.tno.idsa.framework.force_field.update_rules.UpdateRules;
import nl.tno.idsa.framework.potential_field.heatMap.Matrix;
import nl.tno.idsa.framework.potential_field.performance_checker.PersonalPerformance;
import nl.tno.idsa.framework.potential_field.points_of_interest.POI;
import nl.tno.idsa.framework.potential_field.save_to_file.SaveToFile;
import nl.tno.idsa.framework.semantics_impl.locations.LocationFunction;
import nl.tno.idsa.framework.simulator.TrajectorySim;
import nl.tno.idsa.framework.world.Area;
import nl.tno.idsa.framework.world.Point;
import nl.tno.idsa.framework.world.World;
import nl.tno.idsa.library.activities.possible.*;
import nl.tno.idsa.viewer.ReplacementForMainFrame;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by alessandrozonta on 29/06/16.
 */
public class PotentialField extends Observable{

    private List<POI> pointsOfInterest; //list with all the POIs regarding the current tracked person
    private HashMap<String, List<Area>> differentAreaType; //list with all the different areas preload at the start of the program
    //private Integer initialPositiveCharge; //positive charge that would assign to the POIs
    //private Integer initialNegativeCharge; //negative charge that would assign to the POIs (for future implementations)
    private Agent trackedAgent; //agent that we are going to track

    private final Double worldHeight; //height of the world
    private final Double worldWidth; //width of the world
    private final Double cellSide = 10.0; //side of the cell. We are gonna divide the word into cells TODO bind cell size with the const inside force field (higher the cell size lower the constant)

    private final Matrix heatMapTilesOptimisation; //new version of the heat map. It use dynamic tile. Hope this is a faster way to compute everything

    private ForceField artificialPotentialField; //Declaration of the artificialPotentialField

    private Point previousPoint; //Store the previous point used for the tracking

    private TreeMap<Double, List<Double>> heatMapValues; //store all the charges (When I am using tile optimisation)
    private List<Double> heatMapValuesSingleLevel; //store all the charges (when I am using only one layer)
    private List<Point> centerPoint; //list with all the center

    private final Boolean confTypologyOfMatrix; //If it is true I am using the tile optimisation otherwise I am using the normal matrix
    private final Double confCommonInitialCharge; //common initial charge. Is easier store it here than inside the code
    private final Double confThresholdPotential; //Threshold used for computing the potential value
    private final Double confConfConstantPotential; //Constant use in the formula for computing the potential
    private final Integer confPath; //Do i want to save the path at the end?
    private final Integer confPerformance; //keep track of what i want to save (for deep copy)
    private final Integer confHeatMap; //keep track of what i want to save (for deep copy)
    private final Integer confPOIs; //keep track of what i want to save (for deep copy)
    private final Boolean confGUI; //If it is true I am using the GUI otherWise not (for deep copy)
    private final Integer confWayPoints; //Do i want to save the waypoints at the end?
    private final Boolean gdsi; //true if i am loading track from file
    private final Boolean confSmoother; //true if I am using the smoother -> Need to compute if I reach the end or not
    private List<Double> parameter; //it stores the parameter for the update rules


    private final ConfigFile conf; //config file with the field loaded from json


    private final SaveToFile storage; //save tracked person info to file

    private UpdateRules updateRule; //select the typology of update rule that i want now

    private PersonalPerformance performance; //keep track on my performance

    //private final World world; //save world object
    private Collection<Area> areaInTheWorld; //Save all the areas in the world (Need this to save memory)
    private Integer targetCounter; //Count time step after reached the target

    private TrajectorySim trajectorySimReference; //reference of the simulator if I simulating real GPS trajectories
    private ReplacementForMainFrame mainFrameReference; //reference of the class ReplacementForMainFrame
    private final String name; //remember the name of the experiment
    private final String experiment; //remember the number of the exp

    private Routing pathFinder; //object path finder for path planning when not using the simulator

    private View view; //object implementing the class that sows what is going on with a GUI -> usable with every dataset -> require internet connection for the maps

    //basic class constructor
    public PotentialField(World world, ConfigFile conf, Double h, Double z1, Double z2, Double s2, Double w2, String name, String experiment){
        this.pointsOfInterest = new ArrayList<>();
        this.differentAreaType = new HashMap<>();
        this.trackedAgent = null;
        //this.world = world;
        this.worldHeight = world.getGeoMisure().getY(); //Height is in the y position of the point
        this.worldWidth = world.getGeoMisure().getX(); //Width is in the x position of the point

        this.conf = conf;
        this.confTypologyOfMatrix = this.conf.getTileOptimisation();
        this.confCommonInitialCharge = this.conf.getCommonInitialCharge();
        this.confThresholdPotential = this.conf.getThresholdPotential();
        this.confConfConstantPotential = this.conf.getConstantPotential();
        this.confHeatMap = this.conf.getHeatMap();
        this.confPath = this.conf.getPath();
        this.confPerformance = this.conf.getPerformance();
        this.confPOIs = this.conf.getPOIs();
        this.confGUI = this.conf.getGUI();
        this.gdsi = this.conf.getGdsi();
        this.confWayPoints = this.conf.getWayPoints();
        this.confSmoother = this.conf.getSmoother();

        if(this.conf.getFileFromThisLocation()) {
            this.storage = new SaveToFile(name, experiment);
        }else{
            this.storage = new SaveToFile(name, experiment, this.conf.getDestinationData());
        }

        if(this.confTypologyOfMatrix){
            this.heatMapTilesOptimisation = new Matrix(this.worldHeight, this.worldWidth, this.conf.getDifferentCellSize(),this.storage, this.conf);
        }else{
            this.heatMapTilesOptimisation = null;
        }

        this.heatMapValues = new TreeMap<>();
        this.heatMapValuesSingleLevel = new ArrayList<>();
        this.centerPoint = new ArrayList<>();


        this.initialiseHeatMap(); //initialise heat map
        this.artificialPotentialField = new ElectricPotential(); //initialise it later because now I don't know which type we need
        //set the variable
        this.artificialPotentialField.setConstant(this.confThresholdPotential,this.confConfConstantPotential);

        this.previousPoint = null;
        this.areaInTheWorld = world.getAreas();
        if(this.areaInTheWorld != null) this.initDifferentAreaType(this.areaInTheWorld); //loading the lists with all the places

        Integer value = this.conf.getUpdateRules();
        this.updateRule = this.returnUpdateRule(value, h, z1 , z2, s2, w2);
        if(this.conf.getSelectorSourceTracks() == 0){
            this.updateRule.setIdsaWorld(Boolean.TRUE);
        }else{
            this.updateRule.setIdsaWorld(Boolean.FALSE);
        }

        this.parameter = new ArrayList<>();
        this.parameter.add(new Double(value));
        this.parameter.add(h);
        this.parameter.add(z1);
        this.parameter.add(z2);
        this.parameter.add(s2);
        this.parameter.add(w2);
        this.updateRule.setWorld(world);
        this.targetCounter = 0;
        this.name = name;
        this.experiment = experiment;

        if (this.gdsi){
            if(this.conf.getSelectorSourceTracks() != 0) {
                this.pathFinder = new PathFinderGraphHopper(); //using graphHopper for path finding -> Need to load the .pbf file
                //load it
                this.pathFinder.load();
                this.updateRule.setPathFinder(this.pathFinder);
            }
        }else{
            this.pathFinder = null;
        }
    }

    //constructor used for the deep copy
    private PotentialField(Double worldHeight, Double worldWidth, Boolean typologyOfMatrix, Double commonInitialCharge,
                           TreeMap<Double, Double> differentCellSize, Collection<Area> areaInTheWorld, Double thresholdPotential,
                           Double constantPotential, Routing pathFinder, Integer confHeatMap, Integer confPerformance,
                           Integer confPOIs, Boolean GUI, String name, String experiment, Boolean gdsi, List<Double> parameter,
                            Boolean location, String destination, Integer confPath, Integer confWayPoints, Boolean idsaWorld,
                           Boolean smoother){
        this.pointsOfInterest = new ArrayList<>();
        this.differentAreaType = new HashMap<>();
        this.trackedAgent = null;
        //this.world = world;
        this.worldHeight = worldHeight; //Height is in the y position of the point
        this.worldWidth = worldWidth; //Width is in the x position of the point

        this.conf = null;
        this.confTypologyOfMatrix = typologyOfMatrix;
        this.confCommonInitialCharge = commonInitialCharge;
        this.confConfConstantPotential = constantPotential;
        this.confThresholdPotential = thresholdPotential;
        this.confHeatMap = confHeatMap;
        this.confPath = confPath;
        this.confPerformance = confPerformance;
        this.confPOIs = confPOIs;
        this.confGUI = GUI;
        this.gdsi = gdsi;
        this.confWayPoints = confWayPoints;
        this.confSmoother = smoother;

        if(location) {
            this.storage = new SaveToFile(name, experiment);
        }else{
            this.storage = new SaveToFile(name, experiment, destination);
        }

        if(this.confTypologyOfMatrix){
            this.heatMapTilesOptimisation = new Matrix(this.worldHeight, this.worldWidth, differentCellSize, this.storage, null); //TODO think about this null. For now I will never use the tile on multiple simulation, maybe in future
        }else{
            this.heatMapTilesOptimisation = null;
        }

        this.heatMapValues = new TreeMap<>();
        this.heatMapValuesSingleLevel = new ArrayList<>();
        this.centerPoint = new ArrayList<>();


        this.initialiseHeatMap(); //initialise heat map
        this.artificialPotentialField = new ElectricPotential(); //initialise it later because now I don't know which type we need
        //set the variable
        this.artificialPotentialField.setConstant(this.confThresholdPotential,this.confConfConstantPotential);

        this.previousPoint = null;
        this.areaInTheWorld = areaInTheWorld;
        if(this.areaInTheWorld != null) this.initDifferentAreaType(this.areaInTheWorld); //loading the lists with all the places

        this.updateRule = this.returnUpdateRule(parameter.get(0).intValue(), parameter.get(1), parameter.get(2) , parameter.get(3), parameter.get(4), parameter.get(5));
        this.updateRule.setIdsaWorld(idsaWorld);


        this.targetCounter = 0;
        this.name = name;
        this.experiment = experiment;

        if (this.gdsi){
            if(!idsaWorld) {
                this.pathFinder = pathFinder; //using graphHopper for path finding -> Need to load the .pbf file
                this.updateRule.setPathFinder(this.pathFinder);
            }
        }else{
            this.pathFinder = null;
        }
    }

    //getter for the matrix dynamic map level
    //public HashMap<Double, List<Cell>> getDynamicMapLevel(){ return this.heatMapTilesOptimisation.getDynamicMapLevel(); }

    //getter for the matrix map level
    //public HashMap<Double, List<Cell>> getMapLevel(){ return this.heatMapTilesOptimisation.getMapLevel(); }


    //setter for world if i need the old world
    public void setWorld(World world){
        this.updateRule.setWorld(world);
    }
    public UpdateRules getUpdateRule() { return this.updateRule; }

    //getter for config
    public ConfigFile getConfig() { return this.conf; }

    //getter for storage
    public SaveToFile getStorage() { return this.storage; }

    //setter for mainframereference
    public void setMainFrameReference(ReplacementForMainFrame mainFrameReference) { this.mainFrameReference = mainFrameReference; }

    //setter for trajectorySim
    public void setTrajectorySimReference(TrajectorySim trajectorySimReference) { this.trajectorySimReference = trajectorySimReference; }

    //setter for the performance
    public void setPerformance(PersonalPerformance performance) {
        this.performance = performance;

    }

    //getter for the typology of the matrix
    public Boolean getTypologyOfMatrix() { return this.confTypologyOfMatrix; }

    //getter for the different cell size
    public TreeMap<Double, Double> getDifferentCellSize(){ return this.heatMapTilesOptimisation.getDifferentCellSize(); }

    //getter for pointsOfInterest
    public List<POI> getPointsOfInterest() { return this.pointsOfInterest; }

    //setter for pointsOfInterest
    public void setPointsOfInterest(List<POI> pointsOfInterest) {
        this.pointsOfInterest = pointsOfInterest;
        List<Point> positions = new ArrayList<>();
        this.pointsOfInterest.stream().forEach(poi -> positions.add(poi.getArea().getPolygon().getCenterPoint()));
        this.performance.addLocations(positions);

        //set POIs to the update rules
        this.updateRule.setPOIs(this.pointsOfInterest);
    }

    //getter for differentAreaType
    public HashMap<String, List<Area>> getDifferentAreaType(){ return this.differentAreaType; }

    //getter for GraphHopper
    public Routing getPathFinder() {
        return this.pathFinder;
    }

    //setter for the track agent. This method throws two exceptions. If the agent has no activity we can not use it for our prediction. If the agent has unrecognized activity we raise another exception
    //trackedAgent is the agent that we have just selected how person to track
    public void setTrackedAgent(Agent trackedAgent) throws EmptyActivityException, ActivityNotImplementedException {
        this.trackedAgent = trackedAgent;
        //check if point of interest is empty (). This is needed if after one person I will select another one
        this.pointsOfInterest.clear();
        //If I am loading the trajectories from file I know the POIs and I do not need to find them in the simulator
        if((this.trajectorySimReference == null && this.mainFrameReference != null) || this.confGUI) this.popolatePOIsfromAgent();
        //set the starting point of the agent
        this.previousPoint = trackedAgent.getLocation();

        //populate the new version of the matrix with the POI
        if(this.confTypologyOfMatrix) this.heatMapTilesOptimisation.initPOI(this.pointsOfInterest);

        //save this agent info to file and crate the folder with the person name
        this.storage.setTrackedAgent(trackedAgent);
        this.storage.saveAgentInfo();
    }

    //getter for cellSide
    public Double getCellSize() { return this.cellSide; }

    //build the differentAreaType list from the world
    private void initDifferentAreaType(Collection<Area> listOfAreas){
        Iterator it = listOfAreas.iterator(); //iterate all the areas

        while(it.hasNext()){
            Area extractedArea = (Area)it.next();
            List<LocationFunction> functions = extractedArea.getFunctions(); //retrieve list with all the functions of that area

            // TODO instead iterating among all the elements check if there are elements different inside the vector and only for them iterate
            for (LocationFunction function : functions) {
                String nameOfTheFunction = function.toString(); //name of the function. We need it to build the map with all the functions

                if (differentAreaType.containsKey(nameOfTheFunction)) { //checking if exist a map called with nomeOfTheFunction
                    List<Area> listOfArea = differentAreaType.get(nameOfTheFunction); //retrieving all the areas in the selected function

                    if (!listOfArea.contains(extractedArea)) { //adding only if the object is not inside. I need this because a lot of them have the same function for all the places but a few have different so I need to check everyone
                        differentAreaType.get(nameOfTheFunction).add(extractedArea); //adding the area to the list of all the areas with "nameOfTheFunction" function
                    }

                } else { //if the map is not present in the list i have to create it and add the area to the list
                    differentAreaType.put(nameOfTheFunction, new ArrayList<>());
                    differentAreaType.get(nameOfTheFunction).add(extractedArea);
                }
            }
        }
    }

    //from Agent agenda we populate future possible POIs. This method throws two exceptions. If the agent has no activity we can not use it for our prediction. If the agent has unrecognized activity we raise another exception
    private void popolatePOIsfromAgent() throws EmptyActivityException, ActivityNotImplementedException {
        List<Activity> trackedActivities = this.trackedAgent.getAgenda(); //retrieve all the activities of the tracked person
        if (trackedActivities.isEmpty()) { throw new EmptyActivityException("No planned activity. WTF"); } //no activity? let's throw an Exception

        List<PossibleActivity> activityAlreadyChecked = new ArrayList<>(); // some agent has more than one time the same activity. I am storing the activity so I am not adding more than once the same POI
        for (Activity trackedActivity : trackedActivities) { //iter among all the activities
            PossibleActivity poxActivity = trackedActivity.getPossibleActivity(); //return possible activity

            if (!activityAlreadyChecked.contains(poxActivity)) { // if already checked I don't do anything
                //check which activity is planning to do
                //TODO all this decision is hardcoded. This is not good for future upgrading
                if (poxActivity instanceof PossibleBeAtWork) { //if the possible activity is to be at work in the future we add all the workplaces (including police spawn point) to the POIs list
                    this.fromPoxPOIsToActualPOIs(this.differentAreaType.get("Workplace"));
                    this.fromPoxPOIsToActualPOIs(this.differentAreaType.get("PoliceSpawnPoint"));

                } else if (poxActivity instanceof PossibleHangAroundOnSquare) { //if the possible activity is to hang around on square in the future we add all the squares to the POIs list
                    this.fromPoxPOIsToActualPOIs(this.differentAreaType.get("Square"));

                } else if (poxActivity instanceof PossibleBeAtSchool) { //if the possible activity is to be at school in the future we add all the schools to the POIs list
                    this.fromPoxPOIsToActualPOIs(this.differentAreaType.get("School"));

                } else if (poxActivity instanceof PossibleHaveDinnerAtHome) { //if the possible activity is to have dinner at home in the future we add all the homes to the POIs list
                    this.fromPoxPOIsToActualPOIs(this.differentAreaType.get("House"));

                } else if (poxActivity instanceof PossibleBeShopping) { //if the possible activity is to go to shopping in the future we add all the shops to the POIs list
                    this.fromPoxPOIsToActualPOIs(this.differentAreaType.get("Shop"));

                } else if (poxActivity instanceof PossibleBeAtSportsField) { //if the possible activity is to be at the sport field in the future we add all the sport fields to the POIs list
                    this.fromPoxPOIsToActualPOIs(this.differentAreaType.get("SportsField"));

                } else if (poxActivity instanceof PossibleBeAtPlayground) { //if the possible activity is to be at the playground in the future we add all the playgrounds to the POIs list
                    this.fromPoxPOIsToActualPOIs(this.differentAreaType.get("Playground"));

                } else if (poxActivity instanceof PossibleBeAtPark) { //if the possible activity is to be at the park in the future we add all the parks(including the water) to the POIs list
                    this.fromPoxPOIsToActualPOIs(this.differentAreaType.get("Park"));
                    this.fromPoxPOIsToActualPOIs(this.differentAreaType.get("Water"));

                } else if (poxActivity instanceof PossibleBeAtMarket) { //if the possible activity is to be at the market in the future we add all the shops to the POIs list
                    this.fromPoxPOIsToActualPOIs(this.differentAreaType.get("Shops"));

                } else {
                    throw new ActivityNotImplementedException("Just found a new activity. Don't know how to react to it"); //just found new activity. Let's throw an Exception
                }
                activityAlreadyChecked.add(poxActivity); // adding current activity to the list of activity checked
            }

        }
/*
        //now we have all the POIs inside the list. We have to give them the correct initial charge
        //We decide to give them an initial charge equals to the number of attractive POIs
        //Counting how many attractive POIs we have
        Double attractivePoint = 0.0;
        for(int i = 0; i < this.pointsOfInterest.size(); i++){
            if (pointsOfInterest.get(i).getMeaning()) attractivePoint++;
        }
        //assign the charge to every POI
        for(int i = 0; i < this.pointsOfInterest.size(); i++){
            this.pointsOfInterest.get(i).setCharge(attractivePoint);
        }
*/
        //for now is better assign to every point the same charge
        this.pointsOfInterest.stream().forEach(p -> p.setCharge(this.confCommonInitialCharge));
        if(this.performance != null) {
            this.performance.addValue((int)this.pointsOfInterest.stream().filter(poi -> poi.getCharge() > 0.0).count());
            List<Point> positions = new ArrayList<>();
            this.pointsOfInterest.stream().forEach(poi -> positions.add(poi.getArea().getPolygon().getCenterPoint()));
            this.performance.addLocations(positions);
        }

    }

    //From a list of possible Area we build our list of POIs
    private void fromPoxPOIsToActualPOIs(List<Area> possiblePOIs){
        //iter among all the elemt of the list. All the saved area saved in the init of the program
        //add the real POI using constructor with only one parameter. We don't know how many POI we will have so we set the charge later}
        possiblePOIs.stream().forEach( aPossiblePOI -> this.pointsOfInterest.add(new POI(aPossiblePOI)));
    }

    //initialise the heatmapvalue with zero after having calculate how many position we need and the center of every cell
    private void initialiseHeatMap(){
        if(this.confTypologyOfMatrix){ //If it is true I am using the tile optimisation
            //initialise the new version of the heat map -> calling Matrix.init
            this.heatMapTilesOptimisation.initMap();
        }else{
            //divide the word into small cells
            Double column =  Math.ceil(this.worldWidth / this.cellSide);
            Double row = Math.ceil(this.worldHeight / this.cellSide);
            //now i have to find the center of the cell
            Double height = this.cellSide/2;
            //I need to find the center of every cell. From the center we will compute the potential field
            for (int i = 0; i < row; i++){
                Double width = this.cellSide/2;
                for(int j = 0; j < column;  j++){
                    this.centerPoint.add(new Point(width, height)); //add central point to the list
                    this.heatMapValuesSingleLevel.add(0.0); //initialise heatMapValue with a zero. Every center point has its own value
                    width += this.cellSide;
                }
                height += this.cellSide;
            }
        }
    }

    //Calculate potential in all the map for the GUI, initial configuration
    public void calculateInitialPotentialFieldInAllTheWorld() throws ParameterNotDefinedException {
        this.calculatePotentialFieldInAllTheWorld(2, Boolean.TRUE);
    }

    //Calculate potential in all the map for the GUI
    //without parameter, force to use 2
    public void calculatePotentialFieldInAllTheWorld() throws ParameterNotDefinedException {
        this.calculatePotentialFieldInAllTheWorld(2,Boolean.FALSE);
    }

    //Calculate potential in all the map for the GUI
    //parameter integer type -> 0 = ArambullaPadillaFormulation, 1 = KathibFormulation, 2 = ElectricPotential
    //Boolean disclaimer = True if is the first calculation of the bottom level, FALSE if is all the other computation
    private void calculatePotentialFieldInAllTheWorld(Integer typology, Boolean disclaimer) throws ParameterNotDefinedException {
        //declare the typology of potential field we are gonna use
        /*switch (typology){
            case 0:
                this.artificialPotentialField = new ArambulaPadillaFormulation();
                break;
            case 1:
                this.artificialPotentialField = new KathibFormulation();
                break;
            case 2:
                this.artificialPotentialField = new ElectricPotential();
                break;
            default:
                throw new ParameterNotDefinedException("Typology of Potential Field not declared"); //Parameter is not correct
        }*/
        //calculate the value of the potential field
        //calling the method of the  heat map system
        if(this.confTypologyOfMatrix){ //If it is true I am using the tile optimisation
            if(disclaimer){
                this.heatMapTilesOptimisation.computeInitialForceInAllOfThePoints(this.artificialPotentialField);
            }else {
                this.heatMapTilesOptimisation.computeForceInAllOfThePoints(this.artificialPotentialField);
            }
            //calculate all the charges
            this.heatMapValues = this.getAllTheCharges(disclaimer);
            //notify that I have updated the charge
            setChanged();
            notifyObservers(this.heatMapValues);

            //TODO save also the different level???
        }else{
            this.heatMapValuesSingleLevel = this.artificialPotentialField.calculateForceInAllTheWorld(this.centerPoint,this.pointsOfInterest);
            this.normaliseHeatMapValue(); // normalise and scale the list
        }

    }

    //function called after having select the person to track.
    //position is the real-time position
    public void trackAndUpdate(Point currentPosition){
//        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//        Calendar cal = Calendar.getInstance();

        //notify that I have just updated the last point -> for the lgds.GUI
        setChanged();
        notifyObservers(currentPosition);

        //check if current position is inside the border of the area loaded -> only if loading the trajectory
        Boolean point_inside = Boolean.TRUE;
        if (this.pathFinder != null) {
            lgds.trajectories.Point currentPositionTranslated = new lgds.trajectories.Point(currentPosition.getX(), currentPosition.getY());
            point_inside = this.pathFinder.isContained(currentPositionTranslated);
        }

        //If it is true do all the stuff otherwise throw away the trajectory
        if(point_inside) {
//            System.out.println(dateFormat.format(cal.getTime()) + " Updating " + this.trackedAgent.getFirstName() + "'s position and potential field...");
            //compute the actual map that I will use only If this.confTypologyOfMatrix is true I am using the tile optimisation
            if (this.confTypologyOfMatrix) this.heatMapTilesOptimisation.computeActualMatrix(currentPosition);

            //add the current position to the path to save on file
            this.performance.addPointToPath(currentPosition);

            //set previous point for the update rule computation
            this.updateRule.setPreviousPoint(this.previousPoint);

            if (this.confTypologyOfMatrix) { //If it is true I am using the tile optimisation
                //calling the method of the  heat map system
                this.heatMapTilesOptimisation.updatePOIcharge(currentPosition, this.updateRule);
            } else {
                this.updatePOIcharge(currentPosition, this.updateRule);
            }

            //I need to calculate the potential every time only if I am using the GUI
            if (this.confGUI) {
                //after having modified all the poi we need to calculate again the POI
                try {
                    this.calculatePotentialFieldInAllTheWorld();
                } catch (ParameterNotDefinedException e) {
                    //I'm fixing the parameter to 2 so I am not dealing with this exception
                }
            }
            //if I need the APF to print maybe I need also the potential values
            if (this.confHeatMap == 1 || this.confHeatMap == 2){
                try {
                    this.calculatePotentialFieldInAllTheWorld();
                } catch (ParameterNotDefinedException e) {
                    //I'm fixing the parameter to 2 so I am not dealing with this exception
                }
            }

            //update the previous point
            this.previousPoint = currentPosition;
        }else{
            System.out.println("Point outside border of the map...");
            //Stop the tracking and delete everything
            //remove observer from agent
            this.trackedAgent.deleteObservers();
            //remove from main list of tracked people on replacementformainframe.
            if(!this.confGUI) {
                if (this.mainFrameReference != null)
                    this.mainFrameReference.removeFromTheLists(this.trackedAgent.getId());
                if (this.trajectorySimReference != null)
                    this.trajectorySimReference.removeFromTheLists(this.trackedAgent.getId());
            }
        }
    }

    //get the charge of all the levels in one list
    //Boolean disclaimer = True if is the first calculation of the bottom level, FALSE if is all the other computation
    public TreeMap<Double, List<Double>> getAllTheCharges(Boolean disclaimer){
        TreeMap<Double, List<Double>> heatMapChargeValues = new TreeMap<>();
        if(disclaimer){
            List<Double> chargeValue = new ArrayList<>();
            this.heatMapTilesOptimisation.getChargeInSelectedLevel(0.0).forEach(chargeValue::add);
            heatMapChargeValues.put(0.0,chargeValue);
        }else {
            this.getDifferentCellSize().forEach((key,value) -> {
                List<Double> chargeValue = new ArrayList<>();
                this.heatMapTilesOptimisation.getChargeInSelectedLevel(key).forEach(chargeValue::add);
                heatMapChargeValues.put(key,chargeValue);
            });
        }
        return heatMapChargeValues;
    }

    //normalise and scale heatMapValue for use the result like a rgb value
    //Print the heat map value on a file
    //Instead from 0 to 255 I am scaling the value from 255 to 0 (inverted) so I can print only the attractive points
    private void normaliseHeatMapValue() {
        List<Double> newheatMapValue = new ArrayList<>();
        Optional<Double> maxList = this.heatMapValuesSingleLevel.stream().max(Comparator.naturalOrder());
        Optional<Double> minList = this.heatMapValuesSingleLevel.stream().min(Comparator.naturalOrder());
        //Double maxList = Collections.max(this.heatMapValue);
        //Double minList = Collections.min(this.heatMapValue);
        Double max = 0.0;
        Double min = 255.0;

        this.heatMapValuesSingleLevel.stream().forEach(aHeatMapValue -> {
            Double standard = (aHeatMapValue - minList.get()) / (maxList.get() - minList.get());
            Double scaled = standard * (max - min) + min;
            newheatMapValue.add(scaled);
        });

        this.heatMapValuesSingleLevel = newheatMapValue;
        //I need to indicate the state of the model has changed and then I need to update all of the registered observer
        //notify that I have updated the charge
        setChanged();
        notifyObservers(this.heatMapValuesSingleLevel);

        //print heat map to file
        if(this.confHeatMap  == 0){
            this.storage.saveHeatMap(this.worldWidth,this.cellSide,this.heatMapValuesSingleLevel);
        }else if(this.confHeatMap  == 1){
            this.storage.saveZipHeatMap(this.worldWidth,this.cellSide,this.heatMapValuesSingleLevel);
        }

    }

    //update all the POIs charge in the new map
    //I need to update the POI only in the cells not splittable of the first level. Then following the splittable cell I go down and repeat the update
    //the inputs are
    //currentPosition -> point where the tracked person is right now
    //angle -> this is the angle that the tracked person is using to move respect the x axis
    //threshold angle
    private void updatePOIcharge(Point currentPosition, UpdateRules updateRule){
        //saving all the waypoints used to update the poi charge
        List<Point> waypoints = new ArrayList<>();
        //Am i in the target?
        POI amInsidePOI = this.arrivedIntoPOI(currentPosition);
        if(amInsidePOI == null) {
            //parallel version of the loop to check and update every point of interest
            this.pointsOfInterest.stream().forEach(aPointsOfInterest -> {
                updateRule.PFPathPlanning(currentPosition); //compute the potential field
                updateRule.computeUpdateRule(currentPosition, aPointsOfInterest.getArea().getPolygon().getCenterPoint());
                //check if I need to update
                //if it is null no action
                if(updateRule.doINeedToUpdate() != null) {
                    if (updateRule.doINeedToUpdate()) {
                        //in this case the path is inside our interest area so we should increase the attractiveness of this poi
                        aPointsOfInterest.increaseCharge(updateRule.getHowMuchIncreaseTheCharge());
                        //saving waypoint only if I am updating the poi and I am outside the poi
                        Point waypoint = updateRule.getWaypoint();
                        if (waypoint!=null){
                            waypoints.add(waypoint);
                        }
                    } else {
                        //in this case the path is outside our interest area so we should decrease the attractiveness of this poi
                        aPointsOfInterest.decreaseCharge(updateRule.getHowMuchDecreaseTheCharge());
                        waypoints.add(null);
                    }
                }
            });
            //I am not inside a POI so i do not need to increase the value. I
            //If i stayed less than n time step inside a POI I reset the value
            this.checkTimeStepAfterTarget(Boolean.FALSE);
        }else{
            //I am inside the POI
            this.pointsOfInterest.stream().filter(poi -> !poi.equals(amInsidePOI)).forEach(aPointOfInterest -> aPointOfInterest.decreaseCharge(updateRule.getHowMuchDecreaseTheChargeInsidePOI()));
            amInsidePOI.increaseCharge(updateRule.getHowMuchIncreaseTheChargeInsidePOI());
            //I am inside a POI, I should count how many time step before stop the tracking
            this.checkTimeStepAfterTarget(Boolean.TRUE);

            //set the target to the performance
            this.performance.setTarget(amInsidePOI.getArea().getPolygon().getCenterPoint());

            waypoints.add(null);
        }
        //adding the list of waypoints. Check if they are null or not. If they are null I do not need to save them
        if(!waypoints.isEmpty()){
            this.performance.addWayPointList(waypoints);
            //notify that I have just updated all the waypoints -> for the lgds.GUI
            setChanged();
            notifyObservers(waypoints);
        }
        //update performance
        this.performance.addValue((int) this.pointsOfInterest.stream().filter(poi -> poi.getCharge() > 0.0).count());
        //update all the POI and the charge
        List<Float> charges = new ArrayList<>();
        this.pointsOfInterest.stream().forEach(poi -> charges.add(poi.getCharge().floatValue()));
        this.performance.addCharges(charges);


        //notify that I have just updated all the POIs -> for the lgds.GUI
        setChanged();
        notifyObservers(this.pointsOfInterest);
    }

    //Deep copy of all the fields of this object
    public PotentialField deepCopy(){
        Boolean idsaWorld;
        if(this.conf.getSelectorSourceTracks() == 0){
            idsaWorld = Boolean.TRUE;
        }else{
            idsaWorld = Boolean.FALSE;
        }
        return new PotentialField(this.worldHeight,this.worldWidth,this.confTypologyOfMatrix,this.confCommonInitialCharge,
                this.conf.getDifferentCellSize(),this.areaInTheWorld, this.confThresholdPotential, this.confConfConstantPotential,
                this.pathFinder, this.confHeatMap, this.confPerformance, this.confPOIs, this.confGUI, this.name,
                this.experiment, this.gdsi, this.parameter, this.conf.getFileFromThisLocation(), this.conf.getDestinationData(),
                this.confPath, this.confWayPoints, idsaWorld, this.confSmoother);
    }

    //Am I at the target?
    //I need to test this method
    //If I am using the smoother I should change the system to understand if I am inside or not
    //Input Point currentPosition -> point where the tracked person is right now
    private POI arrivedIntoPOI(Point currentPosition){
        try {
            //If I am not smoothing
            if(!this.confSmoother) {
                if (this.gdsi) {
                    return this.pointsOfInterest.stream().filter(poi -> poi.contains(currentPosition)).findFirst().get();
                } else {
                    return this.pointsOfInterest.stream().filter(poi -> poi.getArea().getPolygon().contains(currentPosition)).findFirst().get();
                }
            } else {
                //If I am smoothing
                //I am checking if the trajectory is dead or not
                Boolean res = ((TrajectoryAgent)this.trackedAgent).getDead();

                //If it is dead I need to signal the PF that I am inside and to start the ending procedure
                if(res){
                    //If I am dead this means I am inside and I should return the POI that I am inside
                    //This point correspond to the destination of the trajectory
                    //Why not return it?
                    //I don't have it
                    //Maybe I should look for the closest one
                    final POI[] poiSelected = {null};
                    final Double[] distance = {Double.MAX_VALUE};
                    this.pointsOfInterest.stream().forEach(poi -> {
                        Double distanceLocal = this.trajectorySimReference.returnDistance(poi.getArea().getPolygon().getCenterPoint(), currentPosition);
                        if(distanceLocal < distance[0]){
                            distance[0] = distanceLocal;
                            poiSelected[0] = poi;
                        }
                    });
                    return poiSelected[0];
                }else{
                    //I am not inside so I will return null
                    return null;
                }
//
//
//
//
//                Boolean secRes = this.pointsOfInterest.stream().filter(poi -> poi.contains(currentPosition, this.trajectorySimReference)).findFirst().isPresent();
//                //If it is dead but the pf failed in verify if it reach the destination (possible error in trajectory) but i need to stop it in any case
//                if(res){
//                    if(!secRes){
//                        System.out.println("Error trajectory " + this.trackedAgent.getFirstName());
//                        //return the first with charge TODO fix this
//                        return this.pointsOfInterest.stream().filter(poi -> poi.getCharge() > 0).findFirst().get();
//                    }
//                }
//                return this.pointsOfInterest.stream().filter(poi -> poi.contains(currentPosition, this.trajectorySimReference)).findFirst().get();
            }


        }catch (Exception e){
            return null;
        }
    }

    //when I reach The first POI I wait other n time step and then I stop the simulation
    //Input Boolean Inside -> TRUE when I am inside the POI, FALSE I am not and if the counter is grater than 0 i need to decrease to zero
    private void checkTimeStepAfterTarget(Boolean inside){
        if(!inside){
            this.targetCounter = 0;
        }else{
            this.targetCounter++;
            //How many time step do we wait before stopping the tracking?
            //Hardcoded value -> 20
            if(this.targetCounter >= 20){
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Calendar cal = Calendar.getInstance();
                System.out.println(dateFormat.format(cal.getTime()) + " End tracking for " + this.trackedAgent.getFirstName() + "...");
                //Stop the tracking and save all the information
                //remove observer from agent
                this.trackedAgent.deleteObservers();
                //save track
                if(this.confPath == 0) this.performance.savePath(this.storage);
                //save POIs info
                if(this.confPOIs == 0) this.performance.savePOIsInfo(this.storage);
                //save personal performance
                if(this.confPerformance == 0 || this.confPerformance == 2) this.performance.saveInfoToFile(this.storage);
                //save waypoints
                if(this.confWayPoints == 0) this.performance.saveWayPoints(this.storage);
                //remove from main list of tracked people on replacementformainframe. Last thing to do, I need to save the info before eventually stop the simulation
                if(!this.confGUI) {
                    if(this.mainFrameReference != null) this.mainFrameReference.removeFromTheLists(this.trackedAgent.getId());
                    if(this.trajectorySimReference != null) this.trajectorySimReference.removeFromTheLists(this.trackedAgent.getId());
                }else{
                    //TODO what to do in this case?
                }
            }
        }
    }


    //check if the agent is outside a POI
    //this method is called at the start using the god instance of this class
    //input
    //Agent agent - > agent that we have to check
    //output
    //Boolean -> TRUE if it is outside, FALSE if it is inside a place
    public Boolean isOutside(Agent agent) {
        this.trackedAgent = agent;
        //check if point of interest is empty (). This is needed if after one person I will select another one
        try {
            this.popolatePOIsfromAgent();
            //now I have calculate his POI. Is he inside the POI?
            POI amInsidePOI = this.arrivedIntoPOI(agent.getLocation());
            if(amInsidePOI==null){
                //not inside one of his POIs so return true
                this.trackedAgent = null;
                this.pointsOfInterest = new ArrayList<>();
                return Boolean.TRUE;
            }else{
                //Inside the POI so return False
                this.trackedAgent = null;
                this.pointsOfInterest = new ArrayList<>();
                return Boolean.FALSE;
            }
        } catch (EmptyActivityException | ActivityNotImplementedException e) {
            //no activity -> return false -> do not track him
            //before return I clear the two fields
            this.trackedAgent = null;
            this.pointsOfInterest = new ArrayList<>();
            return Boolean.FALSE;
        }
    }

    //given the value it returns the selected update rule
    //Here we can add how many update rules we want
    public UpdateRules returnUpdateRule(Integer value, Double h, Double z1, Double z2, Double s2, Double w2){
        switch (value){
            case 0:
                return new PacmanRule(90.0, 0.25 , 0.005, Boolean.FALSE); //select Pacman rule without distance and path
//                return new PacmanRuleDistance(90.0, 0.25 ,0.005, 0.5, 0.5, Boolean.TRUE); //select Pacman rule fixed value
            case 1:
                return new PacmanRule(h, z1 , z2, Boolean.FALSE); //select Pacman rule without distance and path
            case 2:
                return new PacmanRuleDistance(h, z1 , z2, s2, w2, Boolean.FALSE); //select Pacman rule with distance and no path
            case 3:
                return new PacmanRule(h, z1 , z2, Boolean.TRUE); //select Pacman rule without distance but with path
            case 4:
                return new PacmanRuleDistance(h, z1 , z2, s2, w2, Boolean.TRUE); //select Pacman rule with distance and path
            case 5:
                return new PacmanRule(h, z1 , z2, Boolean.FALSE, this.artificialPotentialField); //select Pacman rule without distance and path but with PF
            case 6:
                return new PacmanRuleDistance(h, z1 , z2, s2, w2, Boolean.FALSE, this.artificialPotentialField); //select Pacman rule with distance and no path but yes PF
            case 7:
                return new PacmanRule(h, z1 , z2, Boolean.TRUE, this.artificialPotentialField); //select Pacman rule without distance but with path and PF
            case 8:
                return new PacmanRuleDistance(h, z1 , z2, s2, w2, Boolean.TRUE, this.artificialPotentialField); //select Pacman rule with distance and path and PF
            case 9:
                return new DoublePacmanRule(h, z1 , z2, Boolean.FALSE); //select DoublePacman rule without path
            case 10:
                return new DoublePacmanRule(h, z1 , z2, Boolean.TRUE); //select Pacman rule wit path
        }
        return null;
    }

}
