package nl.tno.idsa.framework.potential_field.heatMap;

import nl.tno.idsa.framework.potential_field.POI;
import nl.tno.idsa.framework.world.Point;

import java.util.*;

/**
 * Created by alessandrozonta on 29/07/16.
 */
//class that implement the behaviour of all the cells of the map
public class Matrix {
    private final HashMap<Double, List<Cell>> mapLevel; //contains all the levels and all the cell of that level (static version containing all the level that I am using at the start)
    private HashMap<Double, List<Cell>> dynamicMapLevel; //dynamic map level containing the real cell that I am using
    private final Double worldHeight; //height of the map
    private final Double worldWidth; //width of the map
    private final HashMap<Double, Double> differentCellSize; //all the different cell size for the map

    //simple constructor
    public Matrix(Double height, Double width){
        this.worldHeight = height;
        this.worldWidth = width;
        this.mapLevel = new HashMap<>();
        this.dynamicMapLevel = new HashMap<>();
        this.differentCellSize = new HashMap<>();
        //adding hardcoded level
        this.differentCellSize.put(0.0,10.0);
        this.differentCellSize.put(1.0,50.0);
        this.differentCellSize.put(2.0,100.0);
        this.differentCellSize.put(3.0,500.0);
        this.differentCellSize.put(4.0,1000.0);
    }

    //get dynamicMapLevel
    public HashMap<Double, List<Cell>> getDynamicMapLevel(){ return this.dynamicMapLevel; }

    //building the different level
    //pointsOfInterest = list of all the points of interest
    public void initMap(List<POI> pointsOfInterest){
        //build all the level from the lowest to the higher
        this.differentCellSize.forEach((key,value) -> {
            List<Cell> level = new ArrayList<>();
            //divide the word into small cells
            Double column =  Math.ceil(this.worldWidth / value);
            Double row = Math.ceil(this.worldHeight / value);
            //now i have to find the center of the cell
            Double height = value/2;
            //creating the ID for every cell
            Integer id = 0;
            //I need to find the center of every cell. From the center we will compute the potential field
            for (int i = 0; i < row; i++){
                Double width = value/2;
                for(int j = 0; j < column;  j++){
                    level.add(new Cell(value,new Point(width, height), id));
                    width += value;
                    id++; //increasing the id
                }
                height += value;
            }
            //before adding the cell I need to compute all the friends
            for(int q = 0; q < row ; q++){
                for(int z = 0; z < column ; z++){
                    this.buildNeighbourhood(q, z, level, row.intValue(), column.intValue());
                }
            }

            this.mapLevel.put(key,level);
        });

        //I should add all the POIs to the respective cell. Only for the lowest level
        pointsOfInterest.stream().forEach(poi -> this.mapLevel.get(0.0).stream().filter(cell -> cell.contains(poi
                .getArea().getPolygon().getCenterPoint())).findFirst().ifPresent(cell -> cell.addPOIs(poi)));

        //calculate average charge for every cell
        this.mapLevel.get(0.0).stream().forEach(Cell::computeAverageCharge);

        //from the first level to the last level populate the POI
        for(Double i = 1.0; i < this.differentCellSize.size(); i++){
            this.mapLevel.get(i).stream().forEach(Cell::populatePOIfromSubCells);
        }

        //add every sub cell to its father
        for(Double i = 0.0; i < this.differentCellSize.size() - 1 ; i++){
            for(Cell cell : this.mapLevel.get(i)){
                this.putCellIntoFatherList(cell, this.mapLevel.get(i + 1.0));
            }
        }
    }

    //add this cell in the biggest cell
    private void putCellIntoFatherList(Cell cell, List<Cell> cellList){
        Integer position = 0;
        Boolean found = Boolean.FALSE;
        Point center = cell.getCenter();
        while(position < cellList.size() && !found){
            Cell bigCell = cellList.get(position);
            //if cell is inside the border of big cell
            if (center.getX() > bigCell.getLeftBorder() && center.getX() < bigCell.getRightBorder() && center.getY() > bigCell.getBottomBorder() && center.getY() < bigCell.getTopBorder()) {
                bigCell.addSubCells(cell); //add the cell to the father
                cell.setFatherId(bigCell.getId()); //add this cell the Id of the father
                found = Boolean.TRUE;
            }
            position++;
        }

    }

    //return the number of the level
    private Integer getLevelsNumber(){
        return this.differentCellSize.size();
    }

    //given a point I should return at which cell it belongs. From the biggest one to the smallest one
    //point = actual point that I am computing
    //level = level of the maps where I am looking for where the cell is
    //Return a Cell -> the Cell that contains the point
    private Cell getCellWhereIBelong(Point point, Double level){
        return this.mapLevel.get(level).stream().filter(tile -> tile.contains(point)).findFirst().get();
    }

    //return in which sector is the point
    //every cell is subdivided into 9 sector
    //we need dis subdivision to find how far go with the tree
    //cell = cell in using
    //point = actual point, The point that I am looking for the position in the actual cell
    //return an integer value that correspond at the sector where the point is
    private Integer getSector(Cell cell, Point point){
        Double division = cell.getSize();
        division /= 3;
        Double secondDivision = division * 2;
        Double leftBorder = cell.getLeftBorder();
        Double topBorder = cell.getTopBorder();
        Double x = point.getX();
        Double y = point.getY();
        if(x > leftBorder && x < leftBorder + division && y < topBorder && y > topBorder - division){
            return 1;
        }else if(x > leftBorder + division && x < leftBorder + secondDivision && y < topBorder && y > topBorder - division){
            return 2;
        }else if(x > leftBorder + secondDivision && x < cell.getRightBorder() && y < topBorder && y > topBorder - division){
            return 3;
        }else if(x > leftBorder && x < leftBorder + division && y < topBorder - division && y > topBorder - secondDivision){
            return 4;
        }else if(x > leftBorder + division && x < leftBorder + secondDivision && y < topBorder - division && y > topBorder - secondDivision){
            return 5;
        }else if(x > leftBorder + secondDivision && x < cell.getRightBorder() && y < topBorder - division && y > topBorder - secondDivision){
            return 6;
        }else if(x > leftBorder && x < leftBorder + division && y < topBorder && y < topBorder - secondDivision && y > topBorder - division - secondDivision){
            return 7;
        }else if(x > leftBorder + division && x < leftBorder + secondDivision && y < secondDivision && y > topBorder - division - secondDivision){
            return 8;
        }else{
            return 9;
        }
    }


    //building the neighbourhood of one cell
    //I need to keep in mind special case, the border. They may not have some neighbour
    //the inputs are
    //q = number of row that I am using
    //z = number of column that I am using
    //level = list of all the cells present in this level
    //row = number of rows present
    //column = number of columns present
    private void buildNeighbourhood(Integer q, Integer z, List<Cell> level, Integer row, Integer column){
        try {
            Integer pos = (q * row) + z;
            //first row
            if (q == 0) {
                //first position
                if (z == 0) {
                    level.get(pos).setNeighbour(level.get(((q) * column) + z + 1), 4); // top cell
                    level.get(pos).setNeighbour(level.get(((q + 1) * column) + z), 6); // bottom cell
                    level.get(pos).setNeighbour(level.get(((q + 1) * column) + z + 1), 7); // bottom right cell
                } else if (z == column - 1) { //last position
                    level.get(pos).setNeighbour(level.get(((q) * column) + z - 1), 3); // left cell
                    level.get(pos).setNeighbour(level.get(((q + 1) * column) + z - 1), 5); // bottom left cell
                    level.get(pos).setNeighbour(level.get(((q + 1) * column) + z), 6); // bottom cell
                } else {//other position first row
                    level.get(pos).setNeighbour(level.get(((q) * column) + z - 1), 3); // left cell
                    level.get(pos).setNeighbour(level.get(((q) * column) + z + 1), 4); // top cell
                    level.get(pos).setNeighbour(level.get(((q + 1) * column) + z - 1), 5); // bottom left cell
                    level.get(pos).setNeighbour(level.get(((q + 1) * column) + z), 6); // bottom cell
                    level.get(pos).setNeighbour(level.get(((q + 1) * column) + z + 1), 7); // bottom right cell
                }
            } else if (q == row - 1) {//last row
                //first position
                if (z == 0) {
                    level.get(pos).setNeighbour(level.get(((q - 1) * column) + z), 1); // top cell
                    level.get(pos).setNeighbour(level.get(((q - 1) * column) + z + 1), 2); // top right cell
                    level.get(pos).setNeighbour(level.get(((q) * column) + z + 1), 4); // right cell
                } else if (z == column - 1) { //last position
                    level.get(pos).setNeighbour(level.get(((q - 1) * column) + z - 1), 0);// top left cell
                    level.get(pos).setNeighbour(level.get(((q - 1) * column) + z), 1); // top cell
                    level.get(pos).setNeighbour(level.get(((q) * column) + z - 1), 3); // left cell
                } else {//other position first row
                    level.get(pos).setNeighbour(level.get(((q - 1) * column) + z - 1), 0);// top left cell
                    level.get(pos).setNeighbour(level.get(((q - 1) * column) + z), 1); // top cell
                    level.get(pos).setNeighbour(level.get(((q - 1) * column) + z + 1), 2); // top right cell
                    level.get(pos).setNeighbour(level.get(((q) * column) + z - 1), 3); // left cell
                    level.get(pos).setNeighbour(level.get(((q) * column) + z + 1), 4); // right cell
                }
            } else if (z == 0) { //first column
                //i need only to fix the center cell since the first and the last are fixed
                level.get(pos).setNeighbour(level.get(((q - 1) * column) + z), 1); // top cell
                level.get(pos).setNeighbour(level.get(((q - 1) * column) + z + 1), 2); // top right cell
                level.get(pos).setNeighbour(level.get(((q) * column) + z + 1), 4); // right cell
                level.get(pos).setNeighbour(level.get(((q + 1) * column) + z), 6); // bottom cell
                level.get(pos).setNeighbour(level.get(((q + 1) * column) + z + 1), 7); // bottom right cell
            } else if (z == column - 1) { //last column
                //i need only to fix the center cell since the first and the last are fixed
                level.get(pos).setNeighbour(level.get(((q - 1) * column) + z - 1), 0);// top left cell
                level.get(pos).setNeighbour(level.get(((q - 1) * column) + z), 1); // top cell
                level.get(pos).setNeighbour(level.get(((q) * column) + z - 1), 3); // left cell
                level.get(pos).setNeighbour(level.get(((q + 1) * column) + z - 1), 5); // bottom left cell
                level.get(pos).setNeighbour(level.get(((q + 1) * column) + z), 6); // bottom cell
            } else {
                //these are the normal cases
                level.get(pos).setNeighbour(level.get(((q - 1) * column) + z - 1), 0);// top left cell// top left cell// top left cell
                level.get(pos).setNeighbour(level.get(((q - 1) * column) + z), 1); // top cell
                level.get(pos).setNeighbour(level.get(((q - 1) * column) + z + 1), 2); // top right cell
                level.get(pos).setNeighbour(level.get(((q) * column) + z - 1), 3); // left cell
                level.get(pos).setNeighbour(level.get(((q) * column) + z + 1), 4); // right cell
                level.get(pos).setNeighbour(level.get(((q + 1) * column) + z - 1), 5); // bottom left cell
                level.get(pos).setNeighbour(level.get(((q + 1) * column) + z), 6); // bottom cell
                level.get(pos).setNeighbour(level.get(((q + 1) * column) + z + 1), 7);// bottom right cell
            }
        }catch (Exception e){} //I should not have exception because I am setting a hardcoded position
    }

    //copying the cells with and without children
    //The inputs are
    //neighbour = list of the positions of the neighbour that I am keeping entirely alive
    //selectedCell = the cell containing the point
    //currentLevel = the level on the mapLevel that I am currently using
    //it throws an exception, if the index is not in the correct range
    private void computeRealList(List<Integer> neighbour, Cell selectedCell, Double currentLevel) throws Exception{
        List<Cell> level = new ArrayList<>();
        List<Cell> levelSelected = this.mapLevel.get(currentLevel);
        for (Cell cell : levelSelected) {
            //neighbour could have only three different sizes. 3, 1, or is null
            if (neighbour == null) {
                if (cell.getId().equals(selectedCell.getId())) {
                    level.add(cell);
                } else { //otherwise I am copying the cell without children to save space
                    level.add(cell.retNewCellWithoutChildren());
                }
            } else if (neighbour.size() == 1) {
                if (cell.getId().equals(selectedCell.getId()) || cell.getId().equals(selectedCell.getIdCorrectNeighbour(neighbour.get(0)))) {
                    level.add(cell);
                } else { //otherwise I am copying the cell without children to save space
                    level.add(cell.retNewCellWithoutChildren());
                }
            } else {
                //if is one of the cells that i have to split I am copying the entire cell
                if (cell.getId().equals(selectedCell.getId()) || cell.getId().equals(selectedCell.getIdCorrectNeighbour(neighbour.get(0))) ||
                        cell.getId().equals(selectedCell.getIdCorrectNeighbour(neighbour.get(1))) ||
                        cell.getId().equals(selectedCell.getIdCorrectNeighbour(neighbour.get(3)))) {
                    level.add(cell);
                } else { //otherwise I am copying the cell without children to save space
                    level.add(cell.retNewCellWithoutChildren());
                }
            }
        }
        this.dynamicMapLevel.put(currentLevel, level);
    }

    //try to build all the level with only the actual cell that I am using
    //current position = the current position of the point that I am tracking
    //it return nothing but it is populating a private fields with new version of the levels
    //For every level if the currentPosition point is not in that area or close is keeping only the cell without children
    //if is in that area is keeping the children because I need more details
    //no children means highest level, children means lowest level of abstraction
    public void computeActualMatrix(Point currentPosition){
        //I have the current position, I need to build the final matrix
        //The key of all the levels ordered from the highest level to the lowest
        List<Double> numberOfLevels = Arrays.asList(4.0,3.0,2.0,1.0,0.0);
        numberOfLevels.stream().forEach(lev -> {
            //this is the first level cell where the point is
            Cell levelCell = this.getCellWhereIBelong(currentPosition , lev);
            //return the sector of this cell
            Integer sector = this.getSector(levelCell, currentPosition);
            //now sector could be from 1 to 9
            switch (sector){
                case 1:
                    //if it is in the first sector I need to split also three other cell.s The neighbours number 0,1,3
                    try {
                        this.computeRealList(Arrays.asList(0, 1, 3), levelCell, lev);
                    }catch (Exception e) { }//do nothing. Since they are hardcoded I don't have exception}
                    break;
                case 2:
                    //if it is in the second sector I need to split another other cell. The neighbour number 1
                    try {
                        this.computeRealList(Collections.singletonList(1), levelCell, lev);
                    }catch (Exception e) { }//do nothing. Since they are hardcoded I don't have exception}
                    break;
                case 3:
                    //if it is in the third sector I need to split also three other cells. The neighbours number 1,2,4
                    try {
                        this.computeRealList(Arrays.asList(1,2,4), levelCell, lev);
                    }catch (Exception e) { }//do nothing. Since they are hardcoded I don't have exception}
                    break;
                case 4:
                    //if it is in the fourth sector I need to split another other cell. The neighbour number 3
                    try {
                        this.computeRealList(Collections.singletonList(3), levelCell, lev);
                    }catch (Exception e) { }//do nothing. Since they are hardcoded I don't have exception}
                    break;
                case 5:
                    //if it is in the fifth sector I don't need to split other cells.
                    try {
                        this.computeRealList(null, levelCell, lev);
                    }catch (Exception e) { }//do nothing. Since they are hardcoded I don't have exception}
                    break;
                case 6:
                    //if it is in the sixth sector I need to split another other cell. The neighbour number 4
                    try {
                        this.computeRealList(Collections.singletonList(4), levelCell, lev);
                    }catch (Exception e) { }//do nothing. Since they are hardcoded I don't have exception}
                    break;
                case 7:
                    //if it is in the seventh sector I need to split also three other cells. The neighbours number 3,5,6
                    try {
                        this.computeRealList(Arrays.asList(3,5,6), levelCell, lev);
                    }catch (Exception e) { }//do nothing. Since they are hardcoded I don't have exception}
                    break;
                case 8:
                    //if it is in the eighth sector I need to split another other cell. The neighbour number 6
                    try {
                        this.computeRealList(Collections.singletonList(6), levelCell, lev);
                    }catch (Exception e) { }//do nothing. Since they are hardcoded I don't have exception}
                    break;
                case 9:
                    //if it is in the ninth sector I need to split also three other cells. The neighbours number 4,6,7
                    try {
                        this.computeRealList(Arrays.asList(4,6,7), levelCell, lev);
                    }catch (Exception e) { }//do nothing. Since they are hardcoded I don't have exception}
                    break;
            }
        });
    }

}