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
    private Boolean lgds_GUI; //Am I using the separate gui?
    private Double commonInitialCharge;
    private Double thresholdPotential;
    private Double constantPotential;
    private final TreeMap<Double, Double> differentCellSize;
    private Double dayOfWeek;
    private Double season;
    private Double timeOfTheYear;
    private Time time;
    private Integer maxNumberOfTrackedPeople;
    private Integer path;
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
    private Integer selectorSourceTracks; //Select the source of the track -> Now or IDSA or chinese trajectories
    private Integer wayPoints; //waypoints for path planning
    private Boolean POIsAreClustered; //True the POI are clustered, false not
    private Integer Lag; //Lag for kalman filter smoother
    private Boolean Smoother; //Am I using the smoother?
    private Integer morePOIs; //More POIs than the number of trajectory selected?

    public static String realSourceData = null;

    //constructor
    public ConfigFile(){
        this.currentPath = Paths.get(".").toAbsolutePath().normalize().toString() + "/idsa_config.json";
        this.differentCellSize = new TreeMap<>();
        this.constantPotential = null;
        this.thresholdPotential = null;
        this.commonInitialCharge = null;
        this.GUI = null;
        this.lgds_GUI = null;
        this.tileOptimisation = null;
        this.dayOfWeek = null;
        this.season = null;
        this.timeOfTheYear = null;
        this.time = null;
        this.maxNumberOfTrackedPeople = null;
        this.path = null;
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
        this.selectorSourceTracks = null;
        this.wayPoints = null;
        this.POIsAreClustered = null;
        this.Lag = null;
        this.Smoother = null;
        this.morePOIs = null;
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
        this.lgds_GUI = (Boolean) jsonObject.get("lgds_GUI");
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

        this.path = ((Long) jsonObject.get("Path")).intValue();//zero is yes, one is no
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
        this.selectorSourceTracks = ((Long) jsonObject.get("SelectorSourceTracks")).intValue(); // zero is IDSA, one is China
        this.wayPoints = ((Long) jsonObject.get("WayPoints")).intValue(); //zero is yes, one is no
        this.POIsAreClustered = (Boolean) jsonObject.get("POIsAreClustered"); //True the POI are clustered, false not
        this.Lag = ((Long) jsonObject.get("Lag")).intValue();
        this.Smoother = (Boolean) jsonObject.get("Smoother"); //True I am using Smoother

        this.morePOIs = ((Long) jsonObject.get("MorePOIs")).intValue(); //More POIs than the number of trajectory selected?
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

    public void setMaxNumberOfTrackedPeople(Integer maxNumberOfTrackedPeople) { this.maxNumberOfTrackedPeople = maxNumberOfTrackedPeople; }

    public Integer getPath() { return this.path; }

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

    public Integer getSelectorSourceTracks() { return this.selectorSourceTracks; }

    public Integer getWayPoints() { return this.wayPoints; }

    public Boolean getPOIsAreClustered() { return this.POIsAreClustered; }

    public Integer getLag() { return this.Lag; }

    public Boolean getSmoother() { return this.Smoother; }

    public Integer getMorePOIs() { return this.morePOIs; }

    public Boolean getLgds_GUI() { return lgds_GUI; }

    @Override
    public String toString() {
        return "ConfigFile{" +
                "currentPath='" + currentPath + '\'' +
                ", tileOptimisation=" + tileOptimisation +
                ", GUI=" + GUI +
                ", lgds_GUI=" + lgds_GUI +
                ", commonInitialCharge=" + commonInitialCharge +
                ", thresholdPotential=" + thresholdPotential +
                ", constantPotential=" + constantPotential +
                ", differentCellSize=" + differentCellSize +
                ", dayOfWeek=" + dayOfWeek +
                ", season=" + season +
                ", timeOfTheYear=" + timeOfTheYear +
                ", time=" + time +
                ", maxNumberOfTrackedPeople=" + maxNumberOfTrackedPeople +
                ", path=" + path +
                ", performance=" + performance +
                ", heatMap=" + heatMap +
                ", POIs=" + POIs +
                ", updateRules=" + updateRules +
                ", gdsi=" + gdsi +
                ", selectPerson=" + selectPerson +
                ", selectUR=" + selectUR +
                ", fileFromThisLocation=" + fileFromThisLocation +
                ", sourceData='" + sourceData + '\'' +
                ", destinationData='" + destinationData + '\'' +
                ", selectorSourceTracks=" + selectorSourceTracks +
                ", wayPoints=" + wayPoints +
                ", POIsAreClustered=" + POIsAreClustered +
                ", Lag=" + Lag +
                ", Smoother=" + Smoother +
                ", morePOIs=" + morePOIs +
                '}';
    }
}
