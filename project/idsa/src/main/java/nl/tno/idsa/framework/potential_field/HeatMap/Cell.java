package nl.tno.idsa.framework.potential_field.heatMap;

import nl.tno.idsa.framework.potential_field.POI;
import nl.tno.idsa.framework.world.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alessandrozonta on 29/07/16.
 */
//this class implement the behaviour of one single cell
public class Cell {
    private final Double size; //size of the cell
    private final Point center; //center coordinate of the cell
    private final List<Cell> subCells; //list of sub cells of this cell
    private final List<POI> POIs; //list of POIs in this sub cell (list could be also with only one)
    private Double averageCharge; //average charge poi of the cell located in the center of it
    private Double potential; //potential in this cell
    private Double normalisedPotential; //normalised Potential of the cell
    private final Integer id; //id of the cell
    private final List<Integer> listNeighbors; //list of all the neighbors of the cell

    //constructor without parameters
    public Cell(){
        this.size = null;
        this.center = null;
        this.subCells = new ArrayList<>();
        this.POIs = new ArrayList<>();
        this.averageCharge = null;
        this.potential = -900.0;
        this.normalisedPotential = -900.0;
        this.id = null;
        this.listNeighbors = new ArrayList<>(8); //i'm setting the list with 8 position since I can have only 8 neighbors
        for(int i = 0; i < 8; i++) this.listNeighbors.add(null);
    }

    //constructor with only id
    public Cell(Integer id){
        this.size = null;
        this.center = null;
        this.subCells = new ArrayList<>();
        this.POIs = new ArrayList<>();
        this.averageCharge = null;
        this.potential = -900.0;
        this.normalisedPotential = -900.0;
        this.id = id;
        this.listNeighbors = new ArrayList<>(8); //i'm setting the list with 8 position since I can have only 8 neighbors
        for(int i = 0; i < 8; i++) this.listNeighbors.add(null);
    }

    //constructor with size and center point and id
    public Cell(Double size, Point center, Integer id){
        this.size = size;
        this.center = center;
        this.subCells = new ArrayList<>();
        this.POIs = new ArrayList<>();
        this.averageCharge = null;
        this.potential = -900.0;
        this.normalisedPotential = -900.0;
        this.id = id;
        this.listNeighbors = new ArrayList<>(8); //i'm setting the list with 8 position since I can have only 8 neighbors
        for (int i = 0; i < 8; i++) this.listNeighbors.add(null);
    }

    //constructor with all the parameters
    public Cell(Double size, Point center, List<Cell> subCells, List<POI> poiList, Integer id, Double charge, List<Integer> listNeighbors ){
        this.size = size;
        this.center = center;
        this.subCells = subCells;
        this.POIs = poiList;
        this.averageCharge = charge;
        this.potential = -900.0;
        this.normalisedPotential = -900.0;
        this.id = id;
        this.listNeighbors = listNeighbors;
    }

    //getters
    public Double getSize() {
        return this.size;
    }

    public Point getCenter() {
        return this.center;
    }

    public List<Cell> getSubCells() {
        return this.subCells;
    }

    public List<POI> getPOIs() {
        return this.POIs;
    }

    public Double getAverageCharge() {
        return this.averageCharge;
    }

    public Double getPotential() {
        return this.potential;
    }

    public void setPotential(Double potential) { this.potential = potential; }

    public Double getNormalisedPotential() {
        return this.normalisedPotential;
    }

    public void setNormalisedPotential(Double potential) { this.normalisedPotential = potential; }

    public Integer getId(){ return  this.id; }

    //reset the value on the potential fields
    public void resetPotential(){
        this.potential = -900.0;
        this.normalisedPotential = -900.0;
    }

    //add one sub cell to the list
    //implicitly tested
    public void addSubCells(Cell cell){
        this.subCells.add(cell);
    }

    //return if this is a single cell or it has to be split
    //tested
    public Boolean isSplittable(){
        return this.subCells.size() != 0;
    }

    //delete all the subcells
    public void eraseSubCells() { this.subCells.clear(); }

    //add one POI to the POIs list
    //implicitly tested
    public void addPOIs(POI poi){
        this.POIs.add(poi);
    }

    //compute average POI
    //tested
    public void computeAverageCharge(){
        Double averageCharge = 0.0;
        for(POI aPOI : this.POIs){
            averageCharge += aPOI.getCharge();
        }
        averageCharge /= this.POIs.size();
        if(averageCharge.isNaN()) averageCharge = 0.0; //occurs when I don't have POIs in this cell
        this.averageCharge = averageCharge;
    }

    //return left border
    //tested
    public Double getLeftBorder(){
        return  this.center.getX() - (this.size / 2);
    }

    //return right border
    //tested
    public Double getRightBorder(){
        return  this.center.getX() + (this.size / 2);
    }

    //return top border
    //tested
    public Double getTopBorder(){
        return  this.center.getY() + (this.size / 2);
    }

    //return bottom border
    //tested
    public Double getBottomBorder(){
        return  this.center.getY() - (this.size / 2);
    }

    //check if it contains the point
    //tested
    public Boolean contains(Point point){
        //if cell is inside the border of big cell
        return point.getX() >= this.getLeftBorder() && point.getX() <= this.getRightBorder() && point.getY() >= this.getBottomBorder() && point.getY() <= this.getTopBorder();
    }

    //from all the subCells find out the POIs and add them to my list
    //tested
    public void populatePOIfromSubCells(){
        this.subCells.stream().forEach(cell -> this.POIs.addAll(cell.getPOIs()));
        this.computeAverageCharge(); //compute the average charge
    }

    //populate neighbour
    //implicitly tested
    public void setNeighbour(Cell cell, Integer pos) throws Exception{
        if(pos < 0 || pos > 9) throw new Exception("Wrong Index");
        if(cell != null) { //if cell is null that means I have no friends close to me
            //this.listNeighbors[pos] =
            this.listNeighbors.set(pos,cell.getId());
        }else {
            this.listNeighbors.set(pos,null);
        }
    }

    //get the correct neighbour id
    //tested
    public Integer getIdCorrectNeighbour(Integer position) throws Exception{
        if(position < 0 || position > 9) throw new Exception("Wrong Index");
        return this.listNeighbors.get(position);
    }

    //hard copy cell.
    //Boolean keepChild. If true I keep them otherwise no
    //tested
    public Cell deepCopy(Boolean keepChild){
        if(keepChild){
            Cell newCell = new Cell(this.size,this.center,new ArrayList<>(),this.POIs,this.id,this.averageCharge,this.listNeighbors);
            this.subCells.stream().forEach(cell -> newCell.addSubCells(cell.deepCopy(Boolean.TRUE)));
            return newCell;
        }else {
            return new Cell(this.size,this.center, new ArrayList<>(),this.POIs,this.id,this.averageCharge,this.listNeighbors);
        }
    }

}
