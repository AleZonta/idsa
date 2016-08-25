package nl.tno.idsa.framework.config;



import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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

    //constructor
    public ConfigFile(){
        this.currentPath = this.getClass().getClassLoader().getResource("").getPath() + this.getClass().getName().replace(".", File.separator).substring(0, this.getClass().getName().replace(".", File.separator).lastIndexOf(File.separator)) + "/config.json";
        this.differentCellSize = new TreeMap<>();
        this.constantPotential = null;
        this.thresholdPotential = null;
        this.commonInitialCharge = null;
        this.GUI = null;
        this.tileOptimisation = null;
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

}
