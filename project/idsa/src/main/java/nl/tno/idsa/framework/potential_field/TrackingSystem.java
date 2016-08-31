package nl.tno.idsa.framework.potential_field;

import nl.tno.idsa.framework.world.Point;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by alessandrozonta on 26/07/16.
 */
public class TrackingSystem implements Observer  {
    private final PotentialField pot; //istance of potential field

    //constructor
    public TrackingSystem(PotentialField pot){
        this.pot = pot;
    }

    @Override
    //implement update method for observer
    public void update(Observable o, Object arg) {
        if (arg != null) {
            //check if the argument is really an area and that I am observing the right variable
            if (arg instanceof Point) {
                //update the pf with this position
                Point point = (Point) arg;
                //call the function of the potential field to update it
                this.update(point);
            }
        }
    }

    //update the potential field having the point where the tracked person is now
    private void update(Point point){
        //call the function of the potential field to update it
        this.pot.trackAndUpdate(point);
    }

}
