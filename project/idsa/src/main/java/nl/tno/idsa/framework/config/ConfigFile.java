package nl.tno.idsa.framework.config;


import nl.tno.idsa.framework.world.Time;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.TreeMap;

/**
 * Created by alessandrozonta on 24/08/16.
 */
public class ConfigFile {
    private final String currentPath; //current path of the program
    private Boolean tileOptimisation;
    private Boolean GUI;
    private Double commonInitialCharge;
    private Double thresholdPotential;
    private Double constantPotential;
    private final TreeMap<Double, Double> differentCellSize;
    private Double dayOfWeek;
    private Double season;
    private Double timeOfTheYear;
    private Time time;
    private Integer maxNumberOfTrackedPeople;
    private Integer performance;
    private Integer heatMap;
    private Integer POIs;
    private Integer updateRules;
    private Boolean gdsi; //I am loading trajectories from file?
    private Boolean selectPerson; //Am i selecting person from parameter files?
    private Boolean selectUR; //Am I selecting the update rules from file?
    private Boolean fileFromThisLocation; //Am I loading and saving file in this location?
    private String sourceData; //If file are not loaded from the location they will be loaded from here
    private String destinationData; //If not saved in the same location they will be saved here

    public static String realSourceData = null;

    //constructor
    public ConfigFile(){
        this.currentPath = Paths.get(".").toAbsolutePath().normalize().toString() + "/idsa_config.json";
        this.differentCellSize = new TreeMap<>();
        this.constantPotential = null;
        this.thresholdPotential = null;
        this.commonInitialCharge = null;
        this.GUI = null;
        this.tileOptimisation = null;
        this.dayOfWeek = null;
        this.season = null;
        this.timeOfTheYear = null;
        this.time = null;
        this.maxNumberOfTrackedPeople = null;
        this.performance = null;
        this.heatMap =  null;
        this.POIs = null;
        this.updateRules = null;
        this.gdsi = null;
        this.selectPerson = null;
        this.selectUR = null;
        this.fileFromThisLocation = null;
        this.sourceData = null;
        this.destinationData = null;
    }

    //method that reads the configfile
    //This method load the field from the JSON file.
    //Tested
    public void loadFile() throws IOException, ParseException {
        FileReader reader = new FileReader(this.currentPath);
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(reader);

        this.tileOptimisation = (Boolean) jsonObject.get("TileOptimisation");
        this.GUI = (Boolean) jsonObject.get("GUI");
        this.commonInitialCharge = (Double) jsonObject.get("CommonInitialCharge");
        this.thresholdPotential = (Double) jsonObject.get("ThresholdPotential");
        this.constantPotential = (Double) jsonObject.get("ConstantPotential");

        JSONArray levels= (JSONArray) jsonObject.get("Levels");
        for(Integer i = 0; i < levels.size(); i++){
            this.differentCellSize.put(i.doubleValue(), (Double) levels.get(i));
        }

        this.dayOfWeek = (Double) jsonObject.get("DayOfWeek");//zero is sunday, one is monday, two is friday and three is saturday
        this.season = (Double) jsonObject.get("Season");//zero is unspecified, one is winter and two is summer
        this.timeOfTheYear = (Double) jsonObject.get("TimeOfTheYear");//zero is unspecified and one is pre christmas
        JSONArray time= (JSONArray) jsonObject.get("Time");
        this.time = new Time(((Long) time.get(0)).intValue(),((Long) time.get(1)).intValue(),((Long) time.get(2)).intValue());

        this.maxNumberOfTrackedPeople = ((Long) jsonObject.get("MaxNumberOfTrackedPeople")).intValue();

        this.performance = ((Long) jsonObject.get("Performance")).intValue();//zero is only single performance, one is only total performance, two is both, three is no performance saved
        this.heatMap = ((Long) jsonObject.get("HeatMap")).intValue(); //zero is csv, one is zip, two is no file saved
        this.POIs = ((Long) jsonObject.get("POIs")).intValue(); //zero is yes, one is no
        this.updateRules = ((Long) jsonObject.get("UpdateRule")).intValue(); // zero is Pacman rule

        this.gdsi = (Boolean) jsonObject.get("GDSI"); //True I am using GDSI for the tracking / False I am using IDSA for the tracking
        this.selectPerson = (Boolean) jsonObject.get("SelectPerson"); //True I read from parameter file the person to track / False I simulate all the n person
        this.selectUR = (Boolean) jsonObject.get("SelectUR"); //True I read from parameter file the UR / False I use the config files

        this.fileFromThisLocation = (Boolean) jsonObject.get("FileFromThisLocation"); //True I am loading and saving from the location of the jar. False I am using the other two locaiton
        this.sourceData = (String) jsonObject.get("SourceData");
        this.destinationData = (String) jsonObject.get("DestinationData");

        if (!this.fileFromThisLocation){
            realSourceData = this.sourceData;
        }
    }

    //getter
    public Boolean getTileOptimisation() {
        return this.tileOptimisation;
    }

    public Boolean getGUI() {
        return this.GUI;
    }

    public Double getCommonInitialCharge() {
        return this.commonInitialCharge;
    }

    public Double getThresholdPotential() {
        return this.thresholdPotential;
    }

    public Double getConstantPotential() {
        return this.constantPotential;
    }

    public TreeMap<Double,Double> getDifferentCellSize() { return  this.differentCellSize; }

    public Integer getDayOfWeek() {
        return this.dayOfWeek.intValue();
    }

    public Integer getSeason() {
        return this.season.intValue();
    }

    public Integer getTimeOfTheYear() {
        return this.timeOfTheYear.intValue();
    }

    public Time getTime() {
        return this.time;
    }

    public Integer getMaxNumberOfTrackedPeople() {
        return this.maxNumberOfTrackedPeople;
    }

    public Integer getPerformance() { return this.performance; }

    public Integer getHeatMap() { return this.heatMap; }

    public Integer getPOIs() { return this.POIs; }

    public Integer getUpdateRules() { return this.updateRules; }

    public Boolean getGdsi() { return this.gdsi; }

    public Boolean getSelectPerson() { return this.selectPerson; }

    public void setUpdateRules(Integer updateRules) {
        this.updateRules = updateRules;
    }

    public Boolean getSelectUR() {
        return this.selectUR;
    }

    public Boolean getFileFromThisLocation() { return this.fileFromThisLocation; }

    public String getSourceData() {
        return this.sourceData;
    }

    public String getDestinationData() { return this.destinationData; }
}
