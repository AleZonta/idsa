package nl.tno.idsa.framework.potential_field;

import nl.tno.idsa.framework.agents.Agent;
import nl.tno.idsa.framework.behavior.activities.concrete.Activity;
import nl.tno.idsa.framework.behavior.activities.possible.PossibleActivity;
import nl.tno.idsa.framework.force_field.ArambulaPadillaFormulation;
import nl.tno.idsa.framework.force_field.ElectricPotential;
import nl.tno.idsa.framework.force_field.ForceField;
import nl.tno.idsa.framework.force_field.KathibFormulation;
import nl.tno.idsa.framework.semantics_impl.locations.LocationFunction;
import nl.tno.idsa.framework.world.Area;
import nl.tno.idsa.framework.world.Point;
import nl.tno.idsa.framework.world.World;
import nl.tno.idsa.library.activities.possible.*;

import java.util.*;

/**
 * Created by alessandrozonta on 29/06/16.
 */
public class PotentialField {

    private List<POI> pointsOfInterest; //list with all the POIs regarding the current tracked person
    private final HashMap<String, List<Area>> differentAreaType; //list with all the different areas preload at the start of the program
    //private Integer initialPositiveCharge; //positive charge that would assign to the POIs
    //private Integer initialNegativeCharge; //negative charge that would assign to the POIs (for future implementations)
    private Agent trackedAgent; //agent that we are going to track

    private Point worldRoot; //root point of the world
    private Double worldHeight; //height of the world
    private Double worldWidth; //width of the world
    private Double cellSide = 100.0; //side of the cell. We are gonna divide the word into cells

    private List<Double> heatMapValue; //List of the values of the cell for the heat map
    private List<Point> centerPoint; //list with all the center

    private ForceField artificialPotentialField; //Declaration of the artificialPotentialField

    //basic class constructor
    public PotentialField(World world){
        this.pointsOfInterest = new ArrayList<>();
        this.differentAreaType = new HashMap<>();
        //this.initialNegativeCharge = null;
        //this.initialPositiveCharge = null;
        this.trackedAgent = null;
        this.worldRoot = world.getUtmRoot();
        this.worldHeight = world.getGeoMisure().getY(); //Height is in the y position of the point
        this.worldWidth = world.getGeoMisure().getX(); //Width is in the x position of the point
        this.heatMapValue = new ArrayList<>();
        this.centerPoint = new ArrayList<>();

        this.initialiseHeatMap(); //initialise heat map
        this.artificialPotentialField = null; //initialise it later because now I don't know which type we need

        this.initDifferentAreaType(world); //loading the lists with all the places
    }

    //getter for pointsOfInterest
    public List<POI> getPointsOfInterest() { return this.pointsOfInterest; }

    //getter for differentAreaType
    public HashMap<String, List<Area>> getDifferentAreaType(){ return this.differentAreaType; }

    //setter for the track agent. This method throws two exceptions. If the agent has no activity we can not use it for our prediction. If the agent has unrecognized activity we raise another exception
    public void setTrackedAgent(Agent trackedAgent) throws EmptyActivityException, ActivityNotImplementedException {
        this.trackedAgent = trackedAgent;
        this.popolatePOIsfromAgent();
    }

    //getter for heatMapValue
    public List<Double> getHeatMapValue() { return this.heatMapValue; }

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
        String test = "";
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

    }

    //From a list of possible Area we build our list of POIs
    private void fromPoxPOIsToActualPOIs(List<Area> possiblePOIs){
        for (Area possiblePOI : possiblePOIs) { //iter among all the elemt of the list. All the saved area saved in the init of the program
            this.pointsOfInterest.add(new POI(possiblePOI)); //add the real POI using constructor with only one parameter. We don't know how many POI we will have so we set the charge later
        }
    }

    //initialise the heatmapvalue with zero after having calculate how many position we need and the center of every cell
    private void initialiseHeatMap(){
        //divide the word into small cells
        Double column =  Math.ceil(this.worldWidth / this.cellSide);
        Double row = Math.ceil(this.worldHeight / this.cellSide);
        //now i have to find the center of the cell
        Double height = this.worldRoot.getY();
        Double width = this.worldRoot.getX();
        //I need to find the center of every cell. From the center we will compute the potential field
        for (int i = 0; i < row; i++){
            height += this.cellSide/2;
            for(int j = 0; j < column;  j++){
                width += this.cellSide/2;
                this.centerPoint.add(new Point(width, height)); //add central point to the list
                this.heatMapValue.add(0.0); //initialise heatMapValue with a zero. Every center point has its own value
            }
        }
    }

    //Calculate potential in all the map for the GUI
    //parameter integer type -> 0 = ArambullaPadillaFormulation, 1 = KathibFormulation, 2 = ElectricPotential
    public void calculatePotentialFieldInAllTheWorld(Integer typology) throws ParameterNotDefinedException {
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
        this.heatMapValue = this.artificialPotentialField.calculateForceInAllTheWord(this.centerPoint,this.pointsOfInterest);
        this.normaliseHeatMapValue(); // normalise and scale the list

    }

    //normalise and scale heatMapValue for use the result like a rgb value
    private void normaliseHeatMapValue(){
        List<Double> newheatMapValue = new ArrayList<>();
        Double maxList = Collections.max(this.heatMapValue);
        Double minList = Collections.min(this.heatMapValue);
        Double max = 255.0;
        Double min = 0.0;
        for (int i = 0; i < this.heatMapValue.size(); i++){
            Double standard = (this.heatMapValue.get(i) - minList)/(maxList - minList);
            Double scaled = standard * (max - min) + min;
            newheatMapValue.add(scaled);
        }
        this.heatMapValue = newheatMapValue;
    }

    //function called after having select the person to track.
    //position is the real-time position
    public void trackAndUpdate(Point position){
        //from the position tracked
    }

}
