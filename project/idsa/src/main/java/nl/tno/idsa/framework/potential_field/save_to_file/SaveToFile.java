package nl.tno.idsa.framework.potential_field.save_to_file;

import nl.tno.idsa.framework.agents.Agent;
import nl.tno.idsa.framework.potential_field.POI;
import nl.tno.idsa.framework.world.Point;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by alessandrozonta on 25/08/16.
 */
//Class used to save to file all the data for the analysis
public class SaveToFile {
    private Agent trackedAgent;
    private String currentPath; //current path of the program
    private Integer fileCount; //Count the number of csv saved (to differentiate them)
    private List<Point> pointsOfThePath;

    //constructor
    public SaveToFile(){
        this.currentPath = Paths.get(".").toAbsolutePath().normalize().toString() + "/Output";
        //this.currentPath = "/var/scratch/ama228/Output";
        new File(this.currentPath).mkdirs();
        this.fileCount = -1;
        this.pointsOfThePath = new ArrayList<>();
    }

    //method that set the info for the tracked agent
    public void setTrackedAgent(Agent agent){
        this.trackedAgent = agent;
        this.currentPath = this.currentPath + "/" + agent.getFirstName() + System.currentTimeMillis();
        new File(this.currentPath).mkdirs();
        this.pointsOfThePath.add(agent.getLocation());
    }

    //method that save the agent info
    public void saveAgentInfo(){
        //I will save a Json File with the info of the agent
        JSONObject obj = new JSONObject();
        obj.put("Name",this.trackedAgent.getFirstName());
        obj.put("Age",this.trackedAgent.getAge());
        obj.put("Gender",this.trackedAgent.getGender());

        JSONArray agenda = new JSONArray();
        this.trackedAgent.getAgenda().forEach(agenda::add);
        obj.put("Agenda",agenda);

        obj.put("StartPosition",this.trackedAgent.getLocation());

        try (FileWriter file = new FileWriter(this.currentPath + "/trackedAgent.JSON")) {
            file.write(obj.toJSONString());
            System.out.println("Successfully Copied JSON Object to File...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //method to save the heat map -> Only for the single level heat map
    public void saveHeatMap(Double worldWidth, Double cellSide, List<Double> heatMapValue){
        this.fileCount++;
        Double localCount = 0.0;
        Double column =  Math.ceil(worldWidth / cellSide);
        BufferedWriter outputWriter = null;
        try {
            outputWriter = new BufferedWriter(new FileWriter(this.currentPath + "/heatMapValue" + this.fileCount + ".csv"));
            for (Double aHeatMapValue : heatMapValue) {
                outputWriter.write(Double.toString(aHeatMapValue) + ", ");
                localCount++;
                if(localCount.equals(column)){
                    localCount = 0.0;
                    outputWriter.newLine();
                }
            }
            System.out.println("Successfully Saved CSV File number " + this.fileCount.toString() +"...");
            outputWriter.flush();
            outputWriter.close();
        }catch (Exception e){}
    }

    //method to save the heat map into a zip file -> only for the single level heat map
    public void saveZipHeatMap(Double worldWidth, Double cellSize, List<Double> heatMapValue){
        this.fileCount++;
        Double localCount = 0.0;
        Double column =  Math.ceil(worldWidth / cellSize);

        try (FileOutputStream zipFile = new FileOutputStream(new File(this.currentPath + "/heatMapValue" + this.fileCount + ".zip"));
             ZipOutputStream zos = new ZipOutputStream(zipFile);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(zos, "UTF-8"));
        ){

            ZipEntry csvFile = new ZipEntry("/heatMapValue" + this.fileCount + ".csv");
            zos.putNextEntry(csvFile);

            List<String> rowContent = new LinkedList<>();
            for (Double aHeatMapValue : heatMapValue) {
                rowContent.add(Double.toString(aHeatMapValue));
                localCount++;
                if(localCount.equals(column)){
                    localCount = 0.0;
                    writer.append(String.join(",", rowContent)).append("\n");
                    rowContent.clear();
                }
            }
            System.out.println("Successfully Saved CSV File number " + this.fileCount.toString() +"...");
        }catch (Exception e){}
    }


    //I think I need to save also the path. So I need to store all the point of the person tracked
    public void addPointToPath(Point currentPosition){
        this.pointsOfThePath.add(currentPosition);
    }

    //I need to save all the point of the person tracked
    public void savePathToFile(){
        //I will save a Json File with the info of the path
        JSONObject obj = new JSONObject();
        JSONArray path = new JSONArray();
        this.pointsOfThePath.forEach(path::add);
        obj.put("Path",path);

        try (FileWriter file = new FileWriter(this.currentPath + "/path.JSON")) {
            file.write(obj.toJSONString());
            System.out.println("Successfully Copied JSON Object to File...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //With this method I save all the charge of the POIs with the current position
    public void savePOIsCharge(Point currentPosition, List<POI> listWithAllThePOIs){
        //I will save a Json File with the info of the agent
        JSONObject obj = new JSONObject();
        obj.put("Position",currentPosition);

        JSONArray POIs = new JSONArray();
        JSONObject subObj = new JSONObject();
        for(POI poi:listWithAllThePOIs)
        {
            subObj.put("Loc",poi.getArea().getPolygon().getCenterPoint());
            subObj.put("Charge",poi.getCharge());
            POIs.add(subObj);
        }
        obj.put("POIs",POIs);


        try (FileWriter file = new FileWriter(this.currentPath + "/POIs.JSON")) {
            file.write(obj.toJSONString());
            System.out.println("Successfully Copied JSON Object to File...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
