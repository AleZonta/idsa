package nl.tno.idsa.framework.potential_field;

import nl.tno.idsa.framework.config.ConfigFile;
import nl.tno.idsa.framework.agents.Agent;
import nl.tno.idsa.framework.behavior.activities.concrete.Activity;
import nl.tno.idsa.framework.behavior.activities.possible.PossibleActivity;
import nl.tno.idsa.framework.force_field.*;
import nl.tno.idsa.framework.force_field.update_rules.DoublePacmanRule;
import nl.tno.idsa.framework.force_field.update_rules.PacmanRule;
import nl.tno.idsa.framework.force_field.update_rules.PacmanRuleDistance;
import nl.tno.idsa.framework.force_field.update_rules.UpdateRules;
import nl.tno.idsa.framework.potential_field.heatMap.Matrix;
import nl.tno.idsa.framework.potential_field.performance_checker.PersonalPerformance;
import nl.tno.idsa.framework.potential_field.save_to_file.SaveToFile;
import nl.tno.idsa.framework.semantics_impl.locations.LocationFunction;
import nl.tno.idsa.framework.world.Area;
import nl.tno.idsa.framework.world.Point;
import nl.tno.idsa.framework.world.World;
import nl.tno.idsa.library.activities.possible.*;
import nl.tno.idsa.viewer.ReplacementForMainFrame;

import java.util.*;

/**
 * Created by alessandrozonta on 29/06/16.
 */
public class PotentialField extends Observable{

    private List<POI> pointsOfInterest; //list with all the POIs regarding the current tracked person
    private final HashMap<String, List<Area>> differentAreaType; //list with all the different areas preload at the start of the program
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
    private final Integer confPerformance; //keep track of what i want to save (for deepcopy)
    private final Integer confHeatMap; //keep track of what i want to save (for deepcopy)
    private final Integer confPOIs; //keep track of what i want to save (for deepcopy)


    private final ConfigFile conf; //config file with the field loaded from json


    private final SaveToFile storage; //save tracked person info to file

    private UpdateRules updateRule; //select the typology of update rule that i want now

    private PersonalPerformance performance; //keep track on my performance

    //private final World world; //save world object
    private Collection<Area> areaInTheWorld; //Save all the areas in the world (Need this to save memory)
    private Integer targetCounter; //Count time step after reached the target

    private ReplacementForMainFrame mainFrameReference; //reference of the class ReplacementForMainFrame

    //basic class constructor
    public PotentialField(World world, ConfigFile conf, Double degree, Double s1, Double s2, Double w1, Double w2){
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
        this.confPerformance = this.conf.getPerformance();
        this.confPOIs = this.conf.getPOIs();

        this.storage = new SaveToFile();

        if(this.confTypologyOfMatrix){
            this.heatMapTilesOptimisation = new Matrix(this.worldHeight, this.worldWidth, this.conf.getDifferentCellSize(),this.storage, this.conf);
        }else{
            this.heatMapTilesOptimisation = null;
        }

        this.heatMapValues = new TreeMap<>();
        this.heatMapValuesSingleLevel = new ArrayList<>();
        this.centerPoint = new ArrayList<>();


        this.initialiseHeatMap(); //initialise heat map
        this.artificialPotentialField = null; //initialise it later because now I don't know which type we need

        this.previousPoint = null;
        this.areaInTheWorld = world.getAreas();
        this.initDifferentAreaType(this.areaInTheWorld); //loading the lists with all the places

        Integer value = this.conf.getUpdateRules();
        switch (value){
            case 0:
                this.updateRule = new PacmanRule(90.0, 0.1 ,-0.0001, Boolean.FALSE); //select Pacman rule fixed value
                break;
            case 1:
                this.updateRule = new PacmanRule(degree, s1 , w1, Boolean.FALSE); //select Pacman rule without distance and path
                break;
            case 2:
                this.updateRule = new PacmanRuleDistance(degree, s1 , w1, s2, w2, Boolean.FALSE); //select Pacman rule with distance and no path
                break;
            case 3:
                this.updateRule = new PacmanRule(degree, s1 , w1, Boolean.TRUE); //select Pacman rule without distance but with path
                break;
            case 4:
                this.updateRule = new PacmanRuleDistance(degree, s1 , w1, s2, w2, Boolean.TRUE); //select Pacman rule with distance and path
                break;
            case 5:
                this.updateRule = new PacmanRule(degree, s1 , w1, Boolean.FALSE, this.artificialPotentialField, this.pointsOfInterest); //select Pacman rule without distance and path but with PF
                break;
            case 6:
                this.updateRule = new PacmanRuleDistance(degree, s1 , w1, s2, w2, Boolean.FALSE, this.artificialPotentialField, this.pointsOfInterest); //select Pacman rule with distance and no path but yes PF
                break;
            case 7:
                this.updateRule = new PacmanRule(degree, s1 , w1, Boolean.TRUE, this.artificialPotentialField, this.pointsOfInterest); //select Pacman rule without distance but with path and PF
                break;
            case 8:
                this.updateRule = new PacmanRuleDistance(degree, s1 , w1, s2, w2, Boolean.TRUE, this.artificialPotentialField, this.pointsOfInterest); //select Pacman rule with distance and path and PF
                break;
            case 9:
                this.updateRule = new DoublePacmanRule(degree, s1 , w1, Boolean.FALSE); //select DoublePacman rule without path
                break;
            case 10:
                this.updateRule = new DoublePacmanRule(degree, s1 , w1, Boolean.TRUE); //select Pacman rule wit path
                break;
        }

        this.targetCounter = 0;
    }

    //constructor used for the deep copy
    private PotentialField(Double worldHeight, Double worldWidth, Boolean typologyOfMatrix, Double commonInitialCharge, TreeMap<Double, Double> differentCellSize, Collection<Area> areaInTheWorld, Double thresholdPotential, Double constantPotential, UpdateRules updateRule, Integer confHeatMap, Integer confPerformance, Integer confPOIs){
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
        this.confPerformance = confPerformance;
        this.confPOIs = confPOIs;

        this.storage = new SaveToFile();

        if(this.confTypologyOfMatrix){
            this.heatMapTilesOptimisation = new Matrix(this.worldHeight, this.worldWidth, differentCellSize, this.storage, null); //TODO think about this null. For now I will never use the tile on multiple simulation, maybe in future
        }else{
            this.heatMapTilesOptimisation = null;
        }

        this.heatMapValues = new TreeMap<>();
        this.heatMapValuesSingleLevel = new ArrayList<>();
        this.centerPoint = new ArrayList<>();


        this.initialiseHeatMap(); //initialise heat map
        this.artificialPotentialField = null; //initialise it later because now I don't know which type we need

        this.previousPoint = null;
        this.areaInTheWorld = areaInTheWorld;
        this.initDifferentAreaType(this.areaInTheWorld); //loading the lists with all the places

        this.updateRule = updateRule; //select Pacman rule

        this.targetCounter = 0;
    }

    //getter for the matrix dynamic map level
    //public HashMap<Double, List<Cell>> getDynamicMapLevel(){ return this.heatMapTilesOptimisation.getDynamicMapLevel(); }

    //getter for the matrix map level
    //public HashMap<Double, List<Cell>> getMapLevel(){ return this.heatMapTilesOptimisation.getMapLevel(); }

    //getter for config
    public ConfigFile getConfig() { return this.conf; }

    //getter for storage
    public SaveToFile getStorage() { return this.storage; }

    //setter for mainframereference
    public void setMainFrameReference(ReplacementForMainFrame mainFrameReference) { this.mainFrameReference = mainFrameReference; }

    //setter for the performance
    public void setPerformance(PersonalPerformance performance) { this.performance = performance; }

    //getter for the typology of the matrix
    public Boolean getTypologyOfMatrix() { return this.confTypologyOfMatrix; }

    //getter for the different cell size
    public TreeMap<Double, Double> getDifferentCellSize(){ return this.heatMapTilesOptimisation.getDifferentCellSize(); }

    //getter for pointsOfInterest
    public List<POI> getPointsOfInterest() { return this.pointsOfInterest; }

    //getter for differentAreaType
    public HashMap<String, List<Area>> getDifferentAreaType(){ return this.differentAreaType; }

    //setter for the track agent. This method throws two exceptions. If the agent has no activity we can not use it for our prediction. If the agent has unrecognized activity we raise another exception
    //trackedAgent is the agent that we have just selected how person to track
    public void setTrackedAgent(Agent trackedAgent) throws EmptyActivityException, ActivityNotImplementedException {
        this.trackedAgent = trackedAgent;
        //check if point of interest is empty (). This is needed if after one person I will select another one
        this.pointsOfInterest.clear();
        this.popolatePOIsfromAgent();
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
        this.performance.addValue(this.pointsOfInterest.stream().filter(poi -> poi.getCharge() > 0.0).count());
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
    public  void calculateInitialPotentialFieldInAllTheWorld() throws ParameterNotDefinedException {
        this.calculatePotentialFieldInAllTheWorld(2, Boolean.TRUE);
    }

    //Calculate potential in all the map for the GUI
    //without parameter, force to use 2
    public  void calculatePotentialFieldInAllTheWorld() throws ParameterNotDefinedException {
        this.calculatePotentialFieldInAllTheWorld(2,Boolean.FALSE);
    }

    //Calculate potential in all the map for the GUI
    //parameter integer type -> 0 = ArambullaPadillaFormulation, 1 = KathibFormulation, 2 = ElectricPotential
    //Boolean disclaimer = True if is the first calculation of the bottom level, FALSE if is all the other computation
    private void calculatePotentialFieldInAllTheWorld(Integer typology, Boolean disclaimer) throws ParameterNotDefinedException {
        //declare the typology of potential field we are gonna use
        switch (typology){
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
        }
        //set the variable
        this.artificialPotentialField.setConstant(this.confThresholdPotential,this.confConfConstantPotential);
        //calculate the value of the potential field
        //calling the method of the  heat map system
        if(this.confTypologyOfMatrix){ //If it is true I am using the tile optimisation
            if(disclaimer){
                this.heatMapTilesOptimisation.computeInitialForceInAllOfThePoints(this.artificialPotentialField);
            }else {
                this.heatMapTilesOptimisation.computeForceInAllOfThePoints(this.artificialPotentialField);
            }
            //calculate all the charges
            this.heatMapValues = getAllTheCharges(disclaimer);
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
        System.out.println("Updating "+ this.trackedAgent.getFirstName() +"'s position and potential field...");
        //compute the actual map that I will use only If this.confTypologyOfMatrix is true I am using the tile optimisation
        if(this.confTypologyOfMatrix) this.heatMapTilesOptimisation.computeActualMatrix(currentPosition);

        //add the current position to the path to save on file
        this.storage.addPointToPath(currentPosition);

        //set previous point for the update rule computation
        this.updateRule.setPreviousPoint(this.previousPoint);

        if(this.confTypologyOfMatrix){ //If it is true I am using the tile optimisation
            //calling the method of the  heat map system
            this.heatMapTilesOptimisation.updatePOIcharge(currentPosition,this.updateRule);
        }else{
            this.updatePOIcharge(currentPosition,this.updateRule);
        }


        //after having modified all the poi we need to calculate again the POI
        try {
            this.calculatePotentialFieldInAllTheWorld();
        }catch (ParameterNotDefinedException e){
            //I'm fixing the parameter to 2 so I am not dealing with this exception
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
        //Am i in the target?
        POI amInsidePOI = this.arrivedIntoPOI(currentPosition);
        if(amInsidePOI == null) {
            //parallel version of the loop to check and update every point of interest
            this.pointsOfInterest.parallelStream().forEach(aPointsOfInterest -> {
                updateRule.computeUpdateRule(currentPosition, aPointsOfInterest.getArea().getPolygon().getCenterPoint());
                //check if I need to update
                //if it is null no action
                if(updateRule.doINeedToUpdate() != null) {
                    if (updateRule.doINeedToUpdate()) {
                        //in this case the path is inside our interest area so we should increase the attractiveness of this poi
                        aPointsOfInterest.increaseCharge(updateRule.getHowMuchIncreaseTheCharge());
                    } else {
                        //in this case the path is outside our interest area so we should decrease the attractiveness of this poi
                        aPointsOfInterest.decreaseCharge(updateRule.getHowMuchDecreaseTheCharge());
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
        }

        //save all the POIs and their charge
        if(this.confPOIs == 0) this.storage.savePOIsCharge(currentPosition,this.pointsOfInterest);
        //save performance
        this.performance.addValue(this.pointsOfInterest.stream().filter(poi -> poi.getCharge() > 0.0).count());
    }

    //Deep copy of all the fields of this object
    public PotentialField deepCopy(){
        return new PotentialField(this.worldHeight,this.worldWidth,this.confTypologyOfMatrix,this.confCommonInitialCharge,this.conf.getDifferentCellSize(),this.areaInTheWorld,this.confThresholdPotential, this.confConfConstantPotential, this.updateRule, this.confHeatMap, this.confPerformance, this.confPOIs);
    }

    //Am I at the target?
    //I need to test this method
    //Input Point currentPosition -> point where the tracked person is right now
    private POI arrivedIntoPOI(Point currentPosition){
        try {
            return this.pointsOfInterest.stream().filter(poi -> poi.getArea().getPolygon().contains(currentPosition)).findFirst().get();
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
            if(this.targetCounter == 20){
                //Stop the tracking and save all the information
                //remove observer from agent
                this.trackedAgent.deleteObservers();
                //save track
                this.storage.savePathToFile();
                if(this.confPerformance == 0 || this.confPerformance == 2) this.performance.saveInfoToFile(this.storage); //save personal performance
                //remove from main list of tracked people on replacementformainframe. Last thing to do, I need to save the info before eventually stop the simulation
                this.mainFrameReference.removeFromTheLists(this.trackedAgent.getId());
            }
        }
    }



}
