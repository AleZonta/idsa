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
    private Double alpha; // angle used on the update rule
    private Double s1; // constant for the angle formula
    private Double w1; // constant for the angle formula
    private Double s2; // constant for the distance formula
    private Double w2;  // constant for the distance formula
    private String name; // experiment name
    private String experiment; //experiment number

    //constructor
    public LoadParameters(String name){
        this.currentPath = Paths.get(".").toAbsolutePath().normalize().toString() + name;
        try {
            this.readFile();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void readFile() throws IOException, ParseException {
        FileReader reader = new FileReader(this.currentPath);
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(reader);

        this.alpha = (Double) jsonObject.get("Alpha");
        this.s1 = (Double) jsonObject.get("s1");
        this.w1 = (Double) jsonObject.get("w1");
        this.s2 = (Double) jsonObject.get("s2");
        this.w2 = (Double) jsonObject.get("w2");
        this.name = (String) jsonObject.get("Name");
        this.experiment = ((Long) jsonObject.get("Exp")).toString();
    }


    public Double getW2() { return this.w2; }

    public Double getS2() { return this.s2; }

    public Double getW1() { return this.w1; }

    public Double getS1() { return this.s1; }

    public Double getAlpha() { return this.alpha; }

    public String getName() { return this.name; }

    public String getExperiment() { return this.experiment; }

}
