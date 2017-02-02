package nl.tno.idsa.viewer.view;


import nl.tno.idsa.framework.potential_field.points_of_interest.POI;
import nl.tno.idsa.framework.world.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alessandrozonta on 01/02/2017.
 * need this extension to translate the Point and POIs
 */
public class View extends lgds.viewer.View {

    public View(){
        super();
    }

    /**
     * Translate the point from idsa system to lgds system
     * @param mapFocus focus of the map in idsa Point
     */
    public void setMapFocus(Point mapFocus) {
        lgds.trajectories.Point point = new lgds.trajectories.Point(mapFocus.getX(), mapFocus.getY());
        super.setMapFocus(point);
    }

    /**
     * translate the list of POI into the lgds system
     * @param poiList idsa list of POIs
     */
    public void setListPoints(List<POI> poiList){
        List<lgds.POI.POI> list = new ArrayList<>();
        poiList.stream().forEach(poi -> list.add(new lgds.POI.POI(new lgds.trajectories.Point(poi.getArea().getPolygon().getCenterPoint().getX(), poi.getArea().getPolygon().getCenterPoint().getY()), poi.getCharge())));
        super.setListPoint(list);
    }

    /**
     * trasnlate the Point for the trajectory from idsa to lgds system
     * @param point idsa point to translate
     */
    public void addPointTrajectory(Point point){
        lgds.trajectories.Point realPoint = new lgds.trajectories.Point(point.getX(), point.getY());
        super.addPointTrajectory(realPoint);
    }

    /**
     * translate the first list of POI into the lgds system
     * @param poiList list to transalte
     */
    public void setNewPOIs(List<POI> poiList){
        List<lgds.POI.POI> finalPOIs = new ArrayList<>();
        poiList.stream().forEach(poi -> finalPOIs.add(new lgds.POI.POI(new lgds.trajectories.Point(((POI)poi).getArea().getPolygon().getCenterPoint().getX(),((POI)poi).getArea().getPolygon().getCenterPoint().getY()), poi.getCharge())));
        super.setNewPOI(finalPOIs);
    }


}
