package nl.tno.idsa.framework.potential_field;

import nl.tno.idsa.framework.agents.Agent;
import nl.tno.idsa.framework.behavior.activities.concrete.Activity;
import nl.tno.idsa.framework.behavior.activities.possible.PossibleActivity;
import nl.tno.idsa.framework.force_field.ArambulaPadillaFormulation;
import nl.tno.idsa.framework.force_field.ElectricPotential;
import nl.tno.idsa.framework.force_field.ForceField;
import nl.tno.idsa.framework.force_field.KathibFormulation;
import nl.tno.idsa.framework.potential_field.heatMap.Cell;
import nl.tno.idsa.framework.potential_field.heatMap.Matrix;
import nl.tno.idsa.framework.semantics_impl.locations.LocationFunction;
import nl.tno.idsa.framework.world.Area;
import nl.tno.idsa.framework.world.Point;
import nl.tno.idsa.framework.world.World;
import nl.tno.idsa.library.activities.possible.*;
import nl.tno.idsa.viewer.MainFrame;

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

    private List<Double> heatMapValues; //store all the charge

    //basic class constructor
    public PotentialField(World world){
        this.pointsOfInterest = new ArrayList<>();
        this.differentAreaType = new HashMap<>();
        this.trackedAgent = null;
        this.worldHeight = world.getGeoMisure().getY(); //Height is in the y position of the point
        this.worldWidth = world.getGeoMisure().getX(); //Width is in the x position of the point

        this.heatMapTilesOptimisation = new Matrix(this.worldHeight, this.worldWidth);
        this.heatMapValues = new ArrayList<>();

        this.initialiseHeatMap(); //initialise heat map
        this.artificialPotentialField = null; //initialise it later because now I don't know which type we need

        this.previousPoint = null;
        this.initDifferentAreaType(world); //loading the lists with all the places
    }

    //getter for the matrix dynamic map level
    //public HashMap<Double, List<Cell>> getDynamicMapLevel(){ return this.heatMapTilesOptimisation.getDynamicMapLevel(); }

    //getter for the matrix map level
    //public HashMap<Double, List<Cell>> getMapLevel(){ return this.heatMapTilesOptimisation.getMapLevel(); }

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
        this.heatMapTilesOptimisation.initPOI(this.pointsOfInterest);
    }

    //getter for cellSide
    public Double getCellSize() { return this.cellSide; }

    //build the differentAreaType list from the world
    private void initDifferentAreaType(World world){
        Collection<Area> listOfAreas = world.getAreas(); //return all the areas from the world
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
                    differentAreaType.put(nameOfTheFunction, new ArrayList<Area>());
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
                    this.fromPoxPOIsToActualPOIs(this.differentAreaType.get("SportField"));

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
        this.pointsOfInterest.stream().forEach(p -> p.setCharge(1.0));
    }

    //From a list of possible Area we build our list of POIs
    //TODO this method sometimes generate a nullpointexception because possiblePOIs is empty. How is this possible? test method that creates that list
    private void fromPoxPOIsToActualPOIs(List<Area> possiblePOIs){
        try {
            //iter among all the elemt of the list. All the saved area saved in the init of the program
            //add the real POI using constructor with only one parameter. We don't know how many POI we will have so we set the charge later}
            possiblePOIs.stream().forEach( aPossiblePOI -> this.pointsOfInterest.add(new POI(aPossiblePOI)));
        }catch (NullPointerException e){
            String why = "why????";
             //do nothing
        }
    }

    //initialise the heatmapvalue with zero after having calculate how many position we need and the center of every cell
    private void initialiseHeatMap(){
        //initialise the new version of the heat map -> calling Matrix.init
        this.heatMapTilesOptimisation.initMap();
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
    public void calculatePotentialFieldInAllTheWorld(Integer typology, Boolean disclaimer) throws ParameterNotDefinedException {
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
        //calculate the value of the potential field
        //calling the method of the  heat map system
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
    }

    //function called after having select the person to track.
    //position is the real-time position
    public void trackAndUpdate(Point currentPosition){
        //compute the actual map that I will use
        this.heatMapTilesOptimisation.computeActualMatrix(currentPosition);

        //this is the angle that the tracked person is using to move respect the x axis
        Double angle = Math.toDegrees(Math.atan2(currentPosition.getY() - this.previousPoint.getY(), currentPosition.getX() - this.previousPoint.getX()));
        //threshold angle
        Double threshold = 45.0; // TODO find the best value for this threshold

        //calling the method of the  heat map system
        this.heatMapTilesOptimisation.updatePOIcharge(currentPosition,angle,threshold);

        //after having modified all the poi we need to calculate again the POI
        try {
            this.calculatePotentialFieldInAllTheWorld();
        }catch (ParameterNotDefinedException e){
            //I'm fixing the parameter to 2 so I am not dealing with this exception
        }

    }

    //get the charge of all the levels in one list
    //Boolean disclaimer = True if is the first calculation of the bottom level, FALSE if is all the other computation
    public List<Double> getAllTheCharges(Boolean disclaimer){
        List<Double> chargeValue = new ArrayList<>();
        if(disclaimer){
            this.heatMapTilesOptimisation.getChargeInSelectedLevel(0.0).forEach(chargeValue::add);
        }else {
            this.getDifferentCellSize().forEach((key,value) -> this.heatMapTilesOptimisation.getChargeInSelectedLevel(key).forEach(chargeValue::add));
        }
        return chargeValue;
    }

}
