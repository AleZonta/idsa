package nl.tno.idsa.framework.potential_field;

import nl.tno.idsa.framework.world.Area;
import nl.tno.idsa.framework.world.Point;
import nl.tno.idsa.framework.world.Polygon;

/**
 * Created by alessandrozonta on 29/06/16.
 */
public class POI {

    private Area area; //Area of interest. It contains the coordinate of the poi, the function and other stuff useless for now
    private Double charge; //charge of the POI during the execution
    private Double initial_charge; //initial charge of the POI. Used to reset after the use
    private Boolean meaning; //Boolean value. 1 if attractive, 0 if repulsive
    private Double influenceDistance; //Influence distance of obstacles. Now we are not using it so it is set to zero
    private Boolean isReal; //Boolean value that means if the POI is real or it is created only for the calculation TRUE is real / FALSE id created

    //Class constructor without parameters
    public POI(){
        this.area = null;
        this.charge = null;
        this.initial_charge = null;
        this.meaning = true;
        this.influenceDistance = null;
        this.isReal = Boolean.TRUE;
    }

    //Class constructor with only one parameter. Assuming it is an attractor and we set the charge later
    public POI(Area coord){
        this.area = coord;
        this.charge = null;
        this.initial_charge = null;
        this.meaning = true;
        this.influenceDistance = null;
        this.isReal = Boolean.TRUE;
    }

    //Class constructor for all the four variable
    public POI(Area coord, Double charge, Boolean meaning){
        this.area = coord;
        this.charge = charge;
        this.initial_charge = charge;
        this.meaning = meaning;
        this.influenceDistance = null;
        this.isReal = Boolean.TRUE;
    }

    //special constructor for POI not real
    public POI(Point centralPoint, Double charge){
        this.area = new Area(9999, new Polygon(centralPoint),"");
        this.charge = charge;
        this.initial_charge = charge;
        this.meaning = Boolean.TRUE;
        this.influenceDistance = null;
        this.isReal = Boolean.FALSE;
    }

    //getter for coordinate variable
    public Area getArea() { return this.area; }

    //getter for charge variable
    public Double getCharge() {
        return this.charge;
    }

    //setter for charge variable
    public void setCharge(Double charge) {
        this.charge = charge;
        this.initial_charge = charge;
    }

    //getter for meaning variable
    public Boolean getMeaning() {
        return this.meaning;
    }

    //getter for influenceDistance
    public Double getInfluenceDistance() {
        if(this.influenceDistance == null){
            return 0.0;
        }else{
            return this.influenceDistance;
        }
    }

    //increase the charge of this POI. Increase by one
    public void increaseCharge(Double value){
        this.charge+=value;
    }

    //decrease the charge of this POI. Minimum value is 0 for now. Under the 0 the charge cannot decrease
    public void decreaseCharge(Double value){
        if(this.charge > 0 && this.charge > value) {
            this.charge-=value;
        }else{
            this.charge = 0.0;
        }
    }

    //reset the charge value to the initial one
    public void reset(){
        this.charge = this.initial_charge;
    }

}
