package nl.tno.idsa.framework.potential_field.save_to_file;

import nl.tno.idsa.framework.agents.Agent;
import nl.tno.idsa.framework.potential_field.POI;
import nl.tno.idsa.framework.world.Point;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        this.currentPath = this.getClass().getClassLoader().getResource("").getPath() + this.getClass().getName().replace(".", File.separator).substring(0, this.getClass().getName().replace(".", File.separator).lastIndexOf(File.separator));
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


}
