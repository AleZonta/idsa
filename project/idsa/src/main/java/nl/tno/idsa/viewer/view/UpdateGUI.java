package nl.tno.idsa.viewer.view;



import nl.tno.idsa.framework.potential_field.points_of_interest.POI;
import nl.tno.idsa.framework.world.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by alessandrozonta on 01/02/2017.
 * Controller for the new GUI
 */
public class UpdateGUI implements Observer {
    private final View view; //instance of the new GUI

    /**
     * Constructor with one parameter
     * @param view the class implementing the new GUI
     */
    public UpdateGUI(View view){ this.view = view; }

    /**
     * Override update method of the Observer class
     * argument could be:
     * Point point if I am updating only the position of the person
     * List<POI> poiList if I am updating also the list of Points of interest
     * @param o Observable Object
     * @param arg argument that are changed
     */
    @Override
    public void update(Observable o, Object arg) {
        if (arg != null) {
            //check if the argument is really an area and that I am observing the right variable
            if (arg instanceof Point) {
                //I have just received an update from the model saying the person I am tracking is moving
                //I need to translate the current point in the lgds point
                Point point = (Point) arg;
                this.view.addPointTrajectory(point);
            } else if (arg instanceof List){
                    //I have received a list of something. I should check what is the list containing
                    List list = (List) arg;
                    if (list.get(0) instanceof POI) {
                        List<POI> actualList = (List<POI>) list;
                        //if the list I have just received contains some POI
                        //I need to translate the current list of POI in the list of lgds POI
                        this.view.setNewPOIs(actualList);
                    } else if (list.get(0) instanceof Point) {
                        //if the list contains some Point
                        List<Point> actualList = (List<Point>) list;
                        this.view.setWayPoints(actualList);
                    }
                }
            }
        }

}
