package nl.tno.idsa.framework.potential_field.save_to_file;

import nl.tno.idsa.framework.agents.Agent;
import nl.tno.idsa.framework.world.Point;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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


    //constructor
    public SaveToFile(String name, String experiment){
        this.currentPath = Paths.get(".").toAbsolutePath().normalize().toString() + "/Output" + name;
        new File(this.currentPath).mkdirs();
        this.currentPath += "/" + experiment;
        new File(this.currentPath).mkdirs();
        this.fileCount = -1;
    }

    public SaveToFile(String name, String experiment, String path){
        this.currentPath = path + "/Output" + name;
        new File(this.currentPath).mkdirs();
        this.currentPath += "/" + experiment;
        new File(this.currentPath).mkdirs();
        this.fileCount = -1;
    }

    //method that set the info for the tracked agent
    public void setTrackedAgent(Agent agent){
        this.trackedAgent = agent;
        this.currentPath = this.currentPath + "/" + agent.getFirstName() + System.currentTimeMillis();
        new File(this.currentPath).mkdirs();
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

//        try (FileWriter file = new FileWriter(this.currentPath + "/trackedAgent.JSON")) {
//            file.write(obj.toJSONString());
//            System.out.println("Successfully Copied JSON Object to File...");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        try (FileOutputStream zipFile = new FileOutputStream(new File(this.currentPath + "/trackedAgent.zip"));
             ZipOutputStream zos = new ZipOutputStream(zipFile);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(zos, "UTF-8"));
        ){

            ZipEntry csvFile = new ZipEntry("/trackedAgent.JSON");
            zos.putNextEntry(csvFile);
            writer.append(obj.toJSONString());
            System.out.println("Successfully Saved trackedAgent.zip file...");
        }catch (Exception e){}
    }

    //method to save the heat map -> Only for the single level heat map
    //Input
    //Double worldWidth -> the width of the world
    //Double cellSide -> how big is a cell
    //List<Double> heatMapValue -> list of double with all the potential
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
    //Input
    //Double worldWidth -> the width of the world
    //Double cellSide -> how big is a cell
    //List<Double> heatMapValue -> list of double with all the potential
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
            System.out.println("Successfully Saved ZIP File number " + this.fileCount.toString() +"...");
        }catch (Exception e){}
    }



    //I need to save all the point of the person tracked
    public void savePathToFile(List<Point> pointsOfThePath){
        //I will save a Json File with the info of the path
        JSONObject obj = new JSONObject();
        JSONArray path = new JSONArray();
        pointsOfThePath.forEach(poi -> {
            path.add(Double.toString(poi.getX()) + "," + Double.toString(poi.getY()));
        });
        obj.put("Path",path);

//        try (FileWriter file = new FileWriter(this.currentPath + "/path.JSON")) {
//            file.write(obj.toJSONString());
//            System.out.println("Successfully Copied JSON Object to File...");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        try (FileOutputStream zipFile = new FileOutputStream(new File(this.currentPath + "/path.zip"));
             ZipOutputStream zos = new ZipOutputStream(zipFile);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(zos, "UTF-8"));
        ){

            ZipEntry csvFile = new ZipEntry("/path.JSON");
            zos.putNextEntry(csvFile);
            writer.append(obj.toJSONString());
            System.out.println("Successfully Saved path.zip file...");
        }catch (Exception e){}
    }

    //With this method I save all the charge of the POIs with the current position
    //Input
    //Point target -> target position
    //List<POI> listWithAllThePOIs -> list of all the POIs
    public void savePOIsCharge(Point target, List<Point> locations, List<List<Float>> charges){
        //I will save a Json File with the info of the agent
        JSONObject obj = new JSONObject();
        obj.put("target", Double.toString(target.getX()) + "," + Double.toString(target.getY()));

        JSONArray locationsPOIs = new JSONArray();
        locations.stream().forEach(location -> {
            locationsPOIs.add(Double.toString(location.getX()) + "," + Double.toString(location.getY()));
        });
        obj.put("POIsLocation",locationsPOIs);

        JSONArray chargePOIs = new JSONArray();
        charges.stream().forEach(chargeList -> {
            JSONArray subObj = new JSONArray();
            chargeList.stream().forEach(charge -> {
                subObj.add(charge);
            });
            chargePOIs.add(subObj);
        });
        obj.put("POIsCharge",chargePOIs);
//        try (FileWriter file = new FileWriter(this.currentPath + "/POIs.txt")) {
//            for(Map<Point,Double> map:listWithAllThePOIs){
//                List<Double> charges = new ArrayList<>();
//                map.forEach((point,charge) -> charges.add(charge));
//                file.write(charges.toString());
//                file.write("\n");
//            }
//            System.out.println("Successfully Copied JSON Object to File...");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        try (FileOutputStream zipFile = new FileOutputStream(new File(this.currentPath + "/POIs.zip"));
                                   ZipOutputStream zos = new ZipOutputStream(zipFile);
                                   BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(zos, "UTF-8"));
        ){

            ZipEntry csvFile = new ZipEntry("/POIs.JSON");
            zos.putNextEntry(csvFile);
            writer.append(obj.toJSONString());
            System.out.println("Successfully Saved POIs.zip file...");
        }catch (Exception e){}

    }

//    public void saveTarget(Point target){
//        JSONObject obj = new JSONObject();
//        obj.put("target", target);
//        try (FileOutputStream zipFile = new FileOutputStream(new File(this.currentPath + "/Target.zip"));
//             ZipOutputStream zos = new ZipOutputStream(zipFile);
//             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(zos, "UTF-8"));
//        ){
//
//            ZipEntry csvFile = new ZipEntry("/Target.JSON");
//            zos.putNextEntry(csvFile);
//            writer.append(obj.toJSONString());
//            System.out.println("Successfully Saved Target.zip file...");
//        }catch (Exception e){}
//    }
//    public void savePOIs(List<Point> poisPosition){
//        JSONObject obj = new JSONObject();
//
//        JSONArray POIsPos = new JSONArray();
//        poisPosition.stream().forEach(POIsPos::add);
//
//        obj.put("charges",POIsPos);
//
//        try (FileOutputStream zipFile = new FileOutputStream(new File(this.currentPath + "/POIsPos.zip"));
//             ZipOutputStream zos = new ZipOutputStream(zipFile);
//             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(zos, "UTF-8"));
//        ){
//
//            ZipEntry csvFile = new ZipEntry("/POIsPos.JSON");
//            zos.putNextEntry(csvFile);
//            writer.append(obj.toJSONString());
//            System.out.println("Successfully Saved POIsPos.zip file...");
//        }catch (Exception e){}
//
//    }
//    public void saveLineChargePOI(List<Double> charges){
//        JSONObject obj = new JSONObject();
//
//        JSONArray chargePOIs = new JSONArray();
//        charges.stream().forEach(chargePOIs::add);
//
//        obj.put("charges",chargePOIs);
//        Writer output;
//        try {
//            output = new BufferedWriter(new FileWriter(this.currentPath + "/POIsCharges.json", true));
//            output.append(obj.toJSONString());
//            output.append("\n");
//            output.close();
////            System.out.println("Successfully added line to POIsCharges...");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }

    //Save Performance. This method is used for the personal performance and also for the general one
    //input
    //list<Long> value -> list with the performance value
    public void savePerformace(List<Integer> value){
        //I will save a Json File with the info of the agent
//        JSONObject obj = new JSONObject();
//        JSONArray perf = new JSONArray();
//        value.forEach(perf::add);
//        obj.put("Perf",perf);
//
//        try (FileWriter file = new FileWriter(this.currentPath + "/Performance.JSON")) {
//            file.write(obj.toJSONString());
//            System.out.println("Successfully Copied JSON Object to File...");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        try (FileOutputStream zipFile = new FileOutputStream(new File(this.currentPath + "/Performance.zip"));
             ZipOutputStream zos = new ZipOutputStream(zipFile);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(zos, "UTF-8"));
        ){

            ZipEntry csvFile = new ZipEntry("/Performance.JSON");
            zos.putNextEntry(csvFile);
            writer.append(value.toString());
            System.out.println("Successfully Saved Performance.zip file...");
        }catch (Exception e){}
    }



    //save waypoints
    //input
    //List<List<Point>> list - > all the way points used during computation
    public void saveWayPoints(List<List<Point>> list) {
        JSONObject obj = new JSONObject();
        JSONArray waypoints = new JSONArray();
        list.stream().forEach(poiList -> {
            JSONArray subObj = new JSONArray();
            poiList.stream().forEach(poi -> {
                if (poi != null){
                    subObj.add(Double.toString(poi.getX()) + "," + Double.toString(poi.getY()));
                }else {
                    subObj.add("null");
                }

            });
            waypoints.add(subObj);
        });
        obj.put("waypoints",waypoints);

        try (FileOutputStream zipFile = new FileOutputStream(new File(this.currentPath + "/waypoint.zip"));
             ZipOutputStream zos = new ZipOutputStream(zipFile);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(zos, "UTF-8"));
        ){

            ZipEntry csvFile = new ZipEntry("/waypoint.JSON");
            zos.putNextEntry(csvFile);
            writer.append(obj.toJSONString());
            System.out.println("Successfully Saved waypoint.zip file...");
        }catch (Exception e){}
    }

}
