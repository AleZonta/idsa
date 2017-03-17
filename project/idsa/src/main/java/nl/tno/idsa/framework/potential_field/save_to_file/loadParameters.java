package nl.tno.idsa.framework.potential_field.save_to_file;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by alessandrozonta on 16/09/16.
 */
public class LoadParameters {
    private String currentPath; //current path of the program
    private Double h; // angle used on the update rule
    private Double z1; // constant for the angle formula
    private Double z2; // constant for the angle formula
    private Double s2; // constant for the distance formula
    private Double w2;  // constant for the distance formula
    private String name; // experiment name
    private String experiment; //experiment number
    private Integer number; //number of the person tracked
    private Integer updateRule; //number of update rule

    //constructor
    public LoadParameters(String name, Boolean selectedPerson, Boolean selectRules){
        this.currentPath = Paths.get(".").toAbsolutePath().normalize().toString() + name;
        this.number = null;
        this.updateRule = null;
        try {
            this.readFile(selectedPerson, selectRules);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    //constructor with all the parameters
    public LoadParameters(String h, String z1, String z2, String s2, String w2, String name, String experiment, String number, String updateRule){
        //h -> h
        //z1 -> z1
        //z2 -> z2
        this.h = Double.parseDouble(h);
        this.z1 = Double.parseDouble(z1);
        this.z2 = Double.parseDouble(z2);
        this.s2 = Double.parseDouble(s2);
        this.w2 = Double.parseDouble(w2);
        this.name = name;
        this.experiment = experiment;
        try {
            this.number = Integer.parseInt(number);
        }catch (Exception ignored){
            this.number = null;
        }
        this.updateRule = Integer.parseInt(updateRule);
    }

    private void readFile(Boolean selectedPerson, Boolean selectRules) throws IOException, ParseException {
        FileReader reader = new FileReader(this.currentPath);
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(reader);

        this.h = (Double) jsonObject.get("h");
        this.z1 = (Double) jsonObject.get("z1");
        this.z2 = (Double) jsonObject.get("z2");
        this.s2 = (Double) jsonObject.get("s2");
        this.w2 = (Double) jsonObject.get("w2");
        this.name = (String) jsonObject.get("Name");
        this.experiment = ((Long) jsonObject.get("Exp")).toString();
        if(selectedPerson) this.number = ((Long) jsonObject.get("Number")).intValue();
        if(selectRules) this.updateRule = ((Long) jsonObject.get("UR")).intValue();
    }


    public Double getW2() { return this.w2; }

    public Double getS2() { return this.s2; }

    public Double getZ2() { return this.z2; }

    public Double getZ1() { return this.z1; }

    public Double getH() { return this.h; }

    public String getName() { return this.name; }

    public String getExperiment() { return this.experiment; }

    public Integer getNumber() { return this.number; }

    public Integer getUpdateRule() {
        return updateRule;
    }
}
