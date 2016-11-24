package nl.tno.idsa.save_file;

import nl.tno.idsa.framework.agents.Agent;
import nl.tno.idsa.framework.population.Gender;
import nl.tno.idsa.framework.population.HouseholdRoles;
import nl.tno.idsa.framework.population.HouseholdTypes;
import nl.tno.idsa.framework.potential_field.POI;
import nl.tno.idsa.framework.potential_field.save_to_file.SaveToFile;
import nl.tno.idsa.framework.world.Area;
import nl.tno.idsa.framework.world.Point;
import nl.tno.idsa.framework.world.Polygon;
import org.junit.Test;

import java.util.*;

/**
 * Created by alessandrozonta on 29/08/16.
 */
public class SaveToFileTest {
    @Test
    //Test id save correctly the info into a zip file
    public void saveZipHeatMap() throws Exception {
        SaveToFile savingApp = new SaveToFile("test","test");
        Agent fakeAgent = new Agent(24.0, Gender.FEMALE, HouseholdTypes.PAIR, HouseholdRoles.FATHER, 1990);
        savingApp.setTrackedAgent(fakeAgent);

        Double wordWidth = 1000.0;
        Double cellSize = 100.0;

        Random rand = new Random();

        List<Double> list = new ArrayList<>();
        for(int i = 0; i < 100; i++){
            list.add(rand.nextDouble());
        }
        savingApp.saveZipHeatMap(wordWidth,cellSize,list);
    }

    @Test
    //test if save correctly the POIs info
    public void savePOIsCharge() throws Exception {
        //generate random charge for a random number of POI
        List<Map<Point,Double>> listOfPOI = new ArrayList<>();
        Random randomGenerator = new Random();

        List<Double> values = new ArrayList<>();
        values.add(randomGenerator.nextDouble());
        values.add(randomGenerator.nextDouble());

        Point[] array = {new Point(5.0,5.0), new Point(8.0,6.0), new Point(14.0,3.0), new Point(9.0,0.0)};
        POI poi = new POI(new Area(1, new Polygon(array), null), values.get(0), null);

        Point[] array2 = {new Point(50.0,50.0), new Point(80.0,60.0), new Point(140.0,30.0), new Point(90.0,30.0)};
        POI poi2 = new POI(new Area(2, new Polygon(array2), null), values.get(1), null);

        Map<Point,Double> map = new HashMap<>();
        map.put(poi.getArea().getPolygon().getCenterPoint(),poi.getCharge());
        map.put(poi2.getArea().getPolygon().getCenterPoint(),poi2.getCharge());
        listOfPOI.add(map);

        SaveToFile savingApp = new SaveToFile("test","test");

        //savingApp.savePOIsCharge(new Point(0.0,0.0),listOfPOI);
    }

    @Test
    //test if it adds correctly the agent and if it creates the right folder
    public void setTrackedAgent() throws Exception {
        SaveToFile savingApp = new SaveToFile("test","test");
        Agent fakeAgent = new Agent(24.0, Gender.FEMALE, HouseholdTypes.PAIR, HouseholdRoles.FATHER, 1990);
        savingApp.setTrackedAgent(fakeAgent);

        //checked in debug, it works
    }

    @Test
    //test if it saves all the agent info
    public void saveAgentInfo() throws Exception {
        SaveToFile savingApp = new SaveToFile("test","test");
        Agent fakeAgent = new Agent(24.0, Gender.FEMALE, HouseholdTypes.PAIR, HouseholdRoles.FATHER, 1990);
        savingApp.setTrackedAgent(fakeAgent);
        savingApp.saveAgentInfo();

        //check what happen with two person with the same name
        SaveToFile savingApp2 = new SaveToFile("test","test");
        savingApp2.setTrackedAgent(fakeAgent);
        savingApp2.saveAgentInfo();
        //checked in debug, it works
    }

    @Test
    //test if it saves all the heat map
    public void saveHeatMap() throws Exception {
        SaveToFile savingApp = new SaveToFile("test","test");
        Agent fakeAgent = new Agent(24.0, Gender.FEMALE, HouseholdTypes.PAIR, HouseholdRoles.FATHER, 1990);
        savingApp.setTrackedAgent(fakeAgent);

        Double wordWidth = 1000.0;
        Double cellSize = 100.0;

        Random rand = new Random();

        List<Double> list = new ArrayList<>();
        for(int i = 0; i < 100; i++){
            list.add(rand.nextDouble());
        }
        savingApp.saveHeatMap(wordWidth,cellSize,list);

        //checked in debug, it works

    }

    @Test
    //Test if it saves the file on the disk
    //It test indirectly also the method addPointToPath
    public void savePathToFile() throws Exception {
        Point po = new Point(0,0);
        Point po1 = new Point(10,10);
        Point po2 = new Point(20,20);
        Point po3 = new Point(30,30);
        Point po4 = new Point(40,40);
        Point po5 = new Point(50,50);
        Point po6 = new Point(60,60);
        SaveToFile savingApp = new SaveToFile("test","test");
        savingApp.addPointToPath(po);
        savingApp.addPointToPath(po1);
        savingApp.addPointToPath(po2);
        savingApp.addPointToPath(po3);
        savingApp.addPointToPath(po4);
        savingApp.addPointToPath(po5);
        savingApp.addPointToPath(po6);
        savingApp.savePathToFile();
        //checked in debug, it works


    }

}