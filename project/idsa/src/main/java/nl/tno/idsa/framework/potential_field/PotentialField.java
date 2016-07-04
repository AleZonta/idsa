package nl.tno.idsa.framework.potential_field;

import nl.tno.idsa.framework.agents.Agent;
import nl.tno.idsa.framework.behavior.activities.concrete.Activity;
import nl.tno.idsa.framework.behavior.activities.possible.PossibleActivity;
import nl.tno.idsa.framework.force_field.ArambulaPadillaFormulation;
import nl.tno.idsa.framework.force_field.ForceField;
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
    private Double cellSide = 10.0; //side of the cell. We are gonna divide the word into cells

    //basic class constructor
    public PotentialField(){
        this.pointsOfInterest = new ArrayList<>();
        this.differentAreaType = new HashMap<>();
        //this.initialNegativeCharge = null;
        //this.initialPositiveCharge = null;
        this.trackedAgent = null;
        this.worldRoot = null;
        this.worldHeight = null;
        this.worldWidth = null;
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

    //build the differentAreaType list from the world
    public void initDifferentAreaType(World world){
        Collection<Area> listOfAreas = world.getAreas(); //return all the areas from the world
        Iterator it = listOfAreas.iterator(); //iterate all the areas

        while(it.hasNext()){
            Area extractedArea = (Area)it.next();
            List<LocationFunction> functions = extractedArea.getFunctions(); //retrieve list with all the functions of that area

            // TODO instead iterating among all the elements check if there are elements different inside the vector and only for them iterate
            for(int i = 0; i < functions.size(); i++){
                String nameOfTheFunction = functions.get(i).toString(); //name of the function. We need it to build the map with all the functions

                if (differentAreaType.containsKey(nameOfTheFunction)){ //checking if exist a map called with nomeOfTheFunction
                    List<Area> listOfArea = differentAreaType.get(nameOfTheFunction); //retrieving all the areas in the selected function

                    if(!listOfArea.contains(extractedArea)) { //adding only if the object is not inside. I need this because a lot of them have the same function for all the places but a few have different so I need to check everyone
                        differentAreaType.get(nameOfTheFunction).add(extractedArea); //adding the area to the list of all the areas with "nameOfTheFunction" function
                    }

                }else{ //if the map is not present in the list i have to create it and add the area to the list
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
        for(int i = 0; i < trackedActivities.size(); i++){ //iter among all the activities
            PossibleActivity poxActivity = trackedActivities.get(i).getPossibleActivity(); //return possible activity

            if(!activityAlreadyChecked.contains(poxActivity)){ // if already checked I don't do anything
                //check which activity is planning to do
                //TODO all this decision is hardcoded. This is not good for future upgrading
                if(poxActivity instanceof PossibleBeAtWork){ //if the possible activity is to be at work in the future we add all the workplaces (including police spawn point) to the POIs list
                    this.fromPoxPOIsToActualPOIs(this.differentAreaType.get("Workplace"));
                    this.fromPoxPOIsToActualPOIs(this.differentAreaType.get("PoliceSpawnPoint"));

                } else if (poxActivity instanceof PossibleHangAroundOnSquare){ //if the possible activity is to hang around on square in the future we add all the squares to the POIs list
                    this.fromPoxPOIsToActualPOIs(this.differentAreaType.get("Square"));

                } else if (poxActivity instanceof PossibleBeAtSchool){ //if the possible activity is to be at school in the future we add all the schools to the POIs list
                    this.fromPoxPOIsToActualPOIs(this.differentAreaType.get("School"));

                } else if (poxActivity instanceof PossibleHaveDinnerAtHome){ //if the possible activity is to have dinner at home in the future we add all the homes to the POIs list
                    this.fromPoxPOIsToActualPOIs(this.differentAreaType.get("House"));

                } else if (poxActivity instanceof PossibleBeShopping){ //if the possible activity is to go to shopping in the future we add all the shops to the POIs list
                    this.fromPoxPOIsToActualPOIs(this.differentAreaType.get("Shop"));

                } else if (poxActivity instanceof PossibleBeAtSportsField){ //if the possible activity is to be at the sport field in the future we add all the sport fields to the POIs list
                    this.fromPoxPOIsToActualPOIs(this.differentAreaType.get("SportField"));

                } else if (poxActivity instanceof PossibleBeAtPlayground){ //if the possible activity is to be at the playground in the future we add all the playgrounds to the POIs list
                    this.fromPoxPOIsToActualPOIs(this.differentAreaType.get("Playground"));

                } else if (poxActivity instanceof PossibleBeAtPark){ //if the possible activity is to be at the park in the future we add all the parks(including the water) to the POIs list
                    this.fromPoxPOIsToActualPOIs(this.differentAreaType.get("Park"));
                    this.fromPoxPOIsToActualPOIs(this.differentAreaType.get("Water"));

                } else if (poxActivity instanceof PossibleBeAtMarket){ //if the possible activity is to be at the market in the future we add all the shops to the POIs list
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
        for(int i = 0; i < possiblePOIs.size(); i++){ //iter among all the elemt of the list. All the saved area saved in the init of the program
            this.pointsOfInterest.add(new POI(possiblePOIs.get(i))); //add the real POI using constructor with only one parameter. We don't know how many POI we will have so we set the charge later
        }
    }

    //Calculate potential in all the map for the GUI
    //TODO show on GUI the result
    public void calculatePotentialFieldInAllTheWorld(){
        //divide the word into small cells
        Double column =  Math.ceil(this.worldWidth / this.cellSide);
        Double row = Math.ceil(this.worldHeight / this.cellSide);
        //now i have to find the center of the cell
        Double height = this.worldRoot.getY();
        Double width = this.worldRoot.getX();
        //list with all the center
        List<Point> centerPoint = new ArrayList<>();
        //I need to find the center of every cell. From the center we will compute the potential field
        for (int i = 0; i < row; i++){
            height += this.cellSide/2;
            for(int j = 0; j < column;  j++){
                width += this.cellSide/2;
                centerPoint.add(new Point(width, height));
            }
        }
        //declare the typology of potential field we are gonna use
        ForceField artificialPotentialField = new ArambulaPadillaFormulation();
        //list with all the magnitude. From the potential field we should have vector so I calculate the magnitude. I m not sure
        List<Double> magnitude = new ArrayList<>();
        //now I have to calculate the value of the PF in every point
        for (int i = 0; i < centerPoint.size(); i++){
            //for every point I have to compute the potential for all the attraction/repulsive points and sum the result
            Point totalForceInThisPoint = new Point(0.0,0.0);
            for (int j = 0; j < this.pointsOfInterest.size(); j++){
                //automatically sum every potential from every poi
                totalForceInThisPoint.plus(artificialPotentialField.force(centerPoint.get(i), this.pointsOfInterest.get(j)));
            }
            magnitude.add(Math.sqrt(Math.pow(totalForceInThisPoint.getX(),2.0) + Math.pow(totalForceInThisPoint.getY(),2.0)));
        }
        //now I should show the result on the screen
    }

    //function called after having select the person to track.
    //position is the real-time position
    public void trackAndUpdate(Point position){
        //from the position tracked
    }

}
