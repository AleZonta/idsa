package nl.tno.idsa.framework.potential_field.heatMap;

import nl.tno.idsa.framework.config.ConfigFile;
import nl.tno.idsa.framework.force_field.ForceField;
import nl.tno.idsa.framework.force_field.update_rules.UpdateRules;
import nl.tno.idsa.framework.potential_field.points_of_interest.POI;
import nl.tno.idsa.framework.potential_field.performance_checker.PersonalPerformance;
import nl.tno.idsa.framework.potential_field.save_to_file.SaveToFile;
import nl.tno.idsa.framework.world.Point;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by alessandrozonta on 29/07/16.
 */
//class that implement the behaviour of all the cells of the map
public class Matrix{
    private final HashMap<Double, List<Cell>> mapLevel; //contains all the levels and all the cell of that level (static version containing all the level that I am using at the start)
    private HashMap<Double, List<Cell>> dynamicMapLevel; //dynamic map level containing the real cell that I am using
    private final Double worldHeight; //height of the map
    private final Double worldWidth; //width of the map
    private final TreeMap<Double, Double> differentCellSize; //all the different cell size for the map -> is a tree map because it preserves the order
    private SaveToFile storage; //save tracked person info to file
    private ConfigFile conf; //config file
    private Integer targetCounter; //Count time step after reached the target
    private PersonalPerformance performance; //keep track on my performance


    //simple constructor
    public Matrix(Double height, Double width, TreeMap<Double, Double> differentCellSize, SaveToFile storage, ConfigFile conf){
        this.worldHeight = height;
        this.worldWidth = width;
        this.mapLevel = new HashMap<>();
        this.dynamicMapLevel = new HashMap<>();
        this.differentCellSize = new TreeMap<>();
        this.storage = storage;
        this.conf = conf;
        this.targetCounter = 0;
        this.performance = new PersonalPerformance(0);
        //adding level from file
        differentCellSize.forEach(this.differentCellSize::put);
    }

    //getDifferentCellSize
    public TreeMap<Double, Double> getDifferentCellSize(){ return this.differentCellSize; }

    //building the different level
    //pointsOfInterest = list of all the points of interest
    //kind of tested
    public void initMap(){
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

        //add every sub cell to its father
        for(Double i = 0.0; i < this.differentCellSize.size() - 1 ; i++){
            for(Cell cell : this.mapLevel.get(i)){
                this.putCellIntoFatherList(cell, this.mapLevel.get(i + 1.0));
            }
        }
    }

    //populate map with pointsOfInterest
    //pointsOfInterest = list of all the points of interest
    //kind of tested
    public void initPOI(List<POI> pointsOfInterest){
        //I should add all the POIs to the respective cell. Only for the lowest level
        pointsOfInterest.stream().forEach(poi -> this.mapLevel.get(0.0).stream().filter(cell -> cell.contains(poi
                .getArea().getPolygon().getCenterPoint())).forEach(cell -> cell.addPOIs(poi)));

        //calculate average charge for every cell
        this.mapLevel.get(0.0).stream().forEach(Cell::computeAverageCharge);

        //from the first level to the last level populate the POI
        for(Double i = 1.0; i < this.differentCellSize.size(); i++){
            this.mapLevel.get(i).stream().forEach(Cell::populatePOIfromSubCells);
        }

        //initialise also dynamic map level
        this.differentCellSize.forEach((key,size) -> this.dynamicMapLevel.put(key,this.copyEntireList(this.mapLevel.get(key))));
        if(this.performance != null) {
            this.performance.addValue(pointsOfInterest.stream().filter(poi -> poi.getCharge() > 0.0).count());
            List<Point> positions = new ArrayList<>();
            pointsOfInterest.stream().forEach(poi -> positions.add(poi.getArea().getPolygon().getCenterPoint()));
            this.performance.addLocations(positions);
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
                bigCell.addSubCells(cell.deepCopy(Boolean.TRUE)); //add the cell to the father
                found = Boolean.TRUE;
            }
            position++;
        }

    }

    //return the number of the level
    private Integer getLevelsNumber(){
        return this.differentCellSize.size();
    }

    //given a point I should return at which cell it belongs.
    //point = actual point that I am computing
    //level = level of the maps where I am looking for where the cell is
    //Return a Cell -> the Cell that contains the point
    private Cell getCellWhereIBelong(Point point, Double level){
        return this.mapLevel.get(level).stream().filter(tile -> tile.contains(point)).findFirst().get();
    }

    //given a point I should return at which cell it belongs.
    //point = actual point that I am computing
    //List<Cell> subCell = list of cells where I have to look where is the one that I am looking
    //Return a Cell -> the Cell that contains the point
    private Cell getCellWhereIBelong(Point point, List<Cell> subCell){
        return subCell.stream().filter(tile -> tile.contains(point)).findFirst().get();
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
        if(x >= leftBorder && x <= leftBorder + division && y <= topBorder && y >= topBorder - division){
            return 1;
        }else if(x >= leftBorder + division && x <= leftBorder + secondDivision && y <= topBorder && y >= topBorder - division){
            return 2;
        }else if(x >= leftBorder + secondDivision && x <= cell.getRightBorder() && y <= topBorder && y >= topBorder - division){
            return 3;
        }else if(x >= leftBorder && x <= leftBorder + division && y <= topBorder - division && y >= topBorder - secondDivision){
            return 4;
        }else if(x >= leftBorder + division && x <= leftBorder + secondDivision && y <= topBorder - division && y >= topBorder - secondDivision){
            return 5;
        }else if(x >= leftBorder + secondDivision && x <= cell.getRightBorder() && y <= topBorder - division && y >= topBorder - secondDivision){
            return 6;
        }else if(x >= leftBorder && x <= leftBorder + division && y <= topBorder - secondDivision && y >= topBorder - cell.getSize()){
            return 7;
        }else if(x >= leftBorder + division && x <= leftBorder + secondDivision && y <= topBorder -  secondDivision && y >= topBorder - cell.getSize()){
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
            Integer pos = (q * column) + z;
            //first row
            if (q == 0) {
                //first position
                if (z == 0) {
                    level.get(pos).setNeighbour(level.get(((q + 1) * column) + z), 1); // top cell
                    level.get(pos).setNeighbour(level.get(((q + 1) * column) + z + 1), 2); // top right cell
                    level.get(pos).setNeighbour(level.get(((q) * column) + z + 1), 4); // right cell
                } else if (z == column - 1) { //last position
                    level.get(pos).setNeighbour(level.get(((q + 1) * column) + z - 1), 0);// top left cell
                    level.get(pos).setNeighbour(level.get(((q + 1) * column) + z), 1); // top cell
                    level.get(pos).setNeighbour(level.get(((q) * column) + z - 1), 3); // left cell
                } else {//other position first row
                    level.get(pos).setNeighbour(level.get(((q + 1) * column) + z - 1), 0);// top left cell
                    level.get(pos).setNeighbour(level.get(((q + 1) * column) + z), 1); // top cell
                    level.get(pos).setNeighbour(level.get(((q + 1) * column) + z + 1), 2); // top right cell
                    level.get(pos).setNeighbour(level.get(((q) * column) + z - 1), 3); // left cell
                    level.get(pos).setNeighbour(level.get(((q) * column) + z + 1), 4); // top cell
                }
            } else if (q == row - 1) {//last row
                //first position
                if (z == 0) {
                    level.get(pos).setNeighbour(level.get(((q) * column) + z + 1), 4); // top cell
                    level.get(pos).setNeighbour(level.get(((q - 1) * column) + z), 6); // bottom cell
                    level.get(pos).setNeighbour(level.get(((q - 1) * column) + z + 1), 7); // bottom right cell
                } else if (z == column - 1) { //last position
                    level.get(pos).setNeighbour(level.get(((q) * column) + z - 1), 3); // left cell
                    level.get(pos).setNeighbour(level.get(((q - 1) * column) + z - 1), 5); // bottom left cell
                    level.get(pos).setNeighbour(level.get(((q - 1) * column) + z), 6); // bottom cell
                } else {//other position first row
                    level.get(pos).setNeighbour(level.get(((q) * column) + z - 1), 3); // left cell
                    level.get(pos).setNeighbour(level.get(((q) * column) + z + 1), 4); // right cell
                    level.get(pos).setNeighbour(level.get(((q - 1) * column) + z - 1), 5); // bottom left cell
                    level.get(pos).setNeighbour(level.get(((q - 1) * column) + z), 6); // bottom cell
                    level.get(pos).setNeighbour(level.get(((q - 1) * column) + z + 1), 7);// bottom right cell
                }
            } else if (z == 0) { //first column
                //i need only to fix the center cell since the first and the last are fixed
                level.get(pos).setNeighbour(level.get(((q + 1) * column) + z), 1); // top cell
                level.get(pos).setNeighbour(level.get(((q + 1) * column) + z + 1), 2); // top right cell
                level.get(pos).setNeighbour(level.get(((q) * column) + z + 1), 4); // right cell
                level.get(pos).setNeighbour(level.get(((q - 1) * column) + z), 6); // bottom cell
                level.get(pos).setNeighbour(level.get(((q - 1) * column) + z + 1), 7); // bottom right cell
            } else if (z == column - 1) { //last column
                //i need only to fix the center cell since the first and the last are fixed
                level.get(pos).setNeighbour(level.get(((q + 1) * column) + z - 1), 0);// top left cell
                level.get(pos).setNeighbour(level.get(((q + 1) * column) + z), 1); // top cell
                level.get(pos).setNeighbour(level.get(((q) * column) + z - 1), 3); // left cell
                level.get(pos).setNeighbour(level.get(((q - 1) * column) + z - 1), 5); // bottom left cell
                level.get(pos).setNeighbour(level.get(((q - 1) * column) + z), 6); // bottom cell
            } else {
                //these are the normal cases
                level.get(pos).setNeighbour(level.get(((q + 1) * column) + z - 1), 0);// top left cell
                level.get(pos).setNeighbour(level.get(((q + 1) * column) + z), 1); // top cell
                level.get(pos).setNeighbour(level.get(((q + 1) * column) + z + 1), 2); // top right cell
                level.get(pos).setNeighbour(level.get(((q) * column) + z - 1), 3); // left cell
                level.get(pos).setNeighbour(level.get(((q) * column) + z + 1), 4); // right cell
                level.get(pos).setNeighbour(level.get(((q - 1) * column) + z - 1), 5); // bottom left cell
                level.get(pos).setNeighbour(level.get(((q - 1) * column) + z), 6); // bottom cell
                level.get(pos).setNeighbour(level.get(((q - 1) * column) + z + 1), 7);// bottom right cell
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
        List<Cell> levelSelected = this.copyEntireList(this.mapLevel.get(currentLevel));
        for (Cell cell : levelSelected) {
            //neighbour could have only three different sizes. 3, 1, or is null
            if (neighbour == null) {
                if (cell.getId().equals(selectedCell.getId())) {
                    level.add(cell.deepCopy(Boolean.TRUE));
                } else { //otherwise I am copying the cell without children to save space
                    level.add(cell.deepCopy(Boolean.FALSE));
                }
            } else if (neighbour.size() == 1) {
                if (cell.getId().equals(selectedCell.getId()) || cell.getId().equals(selectedCell.getIdCorrectNeighbour(neighbour.get(0)))) {
                    level.add(cell.deepCopy(Boolean.TRUE));
                } else { //otherwise I am copying the cell without children to save space
                    level.add(cell.deepCopy(Boolean.FALSE));
                }
            } else {
                //if is one of the cells that i have to split I am copying the entire cell
                if (cell.getId().equals(selectedCell.getId()) || cell.getId().equals(selectedCell.getIdCorrectNeighbour(neighbour.get(0))) ||
                        cell.getId().equals(selectedCell.getIdCorrectNeighbour(neighbour.get(1))) ||
                        cell.getId().equals(selectedCell.getIdCorrectNeighbour(neighbour.get(2)))) {
                    level.add(cell.deepCopy(Boolean.TRUE));
                } else { //otherwise I am copying the cell without children to save space
                    level.add(cell.deepCopy(Boolean.FALSE));
                }
            }
        }
        this.dynamicMapLevel.put(currentLevel, level);
    }

    //Is computing the real list of the sub cell
    //if some of them are the ones that need to keep in mind I am saving them, otherwise I am deleting their children
    //The inputs are
    //neighbour = list of the positions of the neighbour that I am keeping entirely alive
    //selectedCell = the cell containing the point
    //currentLevel = the level on the mapLevel that I am currently using
    //List<Cell> subCells = sub cells to check
    //it throws an exception, if the index is not in the correct range
    private void computeRealList(List<Integer> neighbour, Cell selectedCell, Double currentLevel, List<Cell> subCells) throws Exception{
        for (Cell cell : subCells) {
            //neighbour could have only three different sizes. 3, 1, or is null
            if (neighbour == null) {
                if (!cell.getId().equals(selectedCell.getId())) {
                    cell.eraseSubCells();
                }
            } else if (neighbour.size() == 1) {
                if (!cell.getId().equals(selectedCell.getId()) && !cell.getId().equals(selectedCell.getIdCorrectNeighbour(neighbour.get(0)))) {
                    cell.eraseSubCells();
                }
            } else {
                //if is one of the cells that i have to split I am copying the entire cell
                if (!cell.getId().equals(selectedCell.getId()) && !cell.getId().equals(selectedCell.getIdCorrectNeighbour(neighbour.get(0))) &&
                        !cell.getId().equals(selectedCell.getIdCorrectNeighbour(neighbour.get(1))) &&
                        !cell.getId().equals(selectedCell.getIdCorrectNeighbour(neighbour.get(2)))) {
                    cell.eraseSubCells();
                }
            }
        }
        this.dynamicMapLevel.put(currentLevel, this.copyEntireList(this.mapLevel.get(currentLevel)));
        List<Cell> levelSelected = this.dynamicMapLevel.get(currentLevel);
        levelSelected.stream().forEach(cell -> {
            //if this cell is present in the list of the sub cells
            if(subCells.stream().filter(c -> c.getId().equals(cell.getId())).findFirst().isPresent()){
                //I need to check if it still has the children or not
                if(!subCells.stream().filter(c -> c.getId().equals(cell.getId())).findFirst().get().isSplittable()){
                    //is splittable mean it has children. So if it has not children we need to erase
                    cell.eraseSubCells();
                } //otherwise the children reamin
            }else{ // the cell is not present. I erase the subcell
                cell.eraseSubCells();
            }
        });

    }

    //sector could be from 1 to 9
    //different sector comport different cell
    //this method has a switch between the different sectors
    //the inputs are
    //sector =  number of the sector where we are now
    //levelCell = cell where we are now
    //List<Cell> subSplittableCells = sub cells to check
    //Double level = level where we are now
    private void discriminateSector(Integer sector, Cell levelCell, List<Cell> subSplittableCells, Double level){
        switch (sector){
            case 1:
                //if it is in the first sector I need to split also three other cell.s The neighbours number 0,1,3
                try {
                    if(subSplittableCells == null){
                        this.computeRealList(Arrays.asList(0, 1, 3), levelCell, level);
                    }else{
                        this.computeRealList(Arrays.asList(0, 1, 3), levelCell, level, subSplittableCells);
                    }
                }catch (Exception e) { }//do nothing. Since they are hardcoded I don't have exception}
                break;
            case 2:
                //if it is in the second sector I need to split another other cell. The neighbour number 1
                try {
                    if(subSplittableCells == null) {
                        this.computeRealList(Collections.singletonList(1), levelCell, level);
                    }else{
                        this.computeRealList(Collections.singletonList(1), levelCell, level, subSplittableCells);
                    }
                }catch (Exception e) { }//do nothing. Since they are hardcoded I don't have exception}
                break;
            case 3:
                //if it is in the third sector I need to split also three other cells. The neighbours number 1,2,4
                try {
                    if(subSplittableCells == null){
                        this.computeRealList(Arrays.asList(1,2,4), levelCell, level);
                    }else{
                        this.computeRealList(Arrays.asList(1,2,4), levelCell, level, subSplittableCells);
                    }
                }catch (Exception e) { }//do nothing. Since they are hardcoded I don't have exception}
                break;
            case 4:
                //if it is in the fourth sector I need to split another other cell. The neighbour number 3
                try {
                    if(subSplittableCells == null){
                        this.computeRealList(Collections.singletonList(3), levelCell, level);
                    }else{
                        this.computeRealList(Collections.singletonList(3), levelCell, level, subSplittableCells);
                    }
                }catch (Exception e) { }//do nothing. Since they are hardcoded I don't have exception}
                break;
            case 5:
                //if it is in the fifth sector I don't need to split other cells.
                try {
                    if(subSplittableCells == null){
                        this.computeRealList(null, levelCell, level);
                    }else{
                        this.computeRealList(null, levelCell, level, subSplittableCells);
                    }
                }catch (Exception e) { }//do nothing. Since they are hardcoded I don't have exception}
                break;
            case 6:
                //if it is in the sixth sector I need to split another other cell. The neighbour number 4
                try {
                    if(subSplittableCells == null){
                        this.computeRealList(Collections.singletonList(4), levelCell, level);
                    }else{
                        this.computeRealList(Collections.singletonList(4), levelCell, level, subSplittableCells);
                    }
                }catch (Exception e) { }//do nothing. Since they are hardcoded I don't have exception}
                break;
            case 7:
                //if it is in the seventh sector I need to split also three other cells. The neighbours number 3,5,6
                try {
                    if(subSplittableCells == null){
                        this.computeRealList(Arrays.asList(3,5,6), levelCell, level);
                    }else{
                        this.computeRealList(Arrays.asList(3,5,6), levelCell, level, subSplittableCells);
                    }
                }catch (Exception e) { }//do nothing. Since they are hardcoded I don't have exception}
                break;
            case 8:
                //if it is in the eighth sector I need to split another other cell. The neighbour number 6
                try {
                    if(subSplittableCells == null){
                        this.computeRealList(Collections.singletonList(6), levelCell, level);
                    }else{
                        this.computeRealList(Collections.singletonList(6), levelCell, level, subSplittableCells);
                    }
                }catch (Exception e) { }//do nothing. Since they are hardcoded I don't have exception}
                break;
            case 9:
                //if it is in the ninth sector I need to split also three other cells. The neighbours number 4,6,7
                try {
                    if(subSplittableCells == null){
                        this.computeRealList(Arrays.asList(4,6,7), levelCell, level);
                    }else{
                        this.computeRealList(Arrays.asList(4,6,7), levelCell, level, subSplittableCells);
                    }
                }catch (Exception e) { }//do nothing. Since they are hardcoded I don't have exception}
                break;
        }
    }

    //try to build all the level with only the actual cell that I am using
    //current position = the current position of the point that I am tracking
    //it return nothing but it is populating a private fields with new version of the levels
    //For every level if the currentPosition point is not in that area or close is keeping only the cell without children
    //if is in that area is keeping the children because I need more details
    //no children means highest level, children means lowest level of abstraction
    //kind of tested
    public void computeActualMatrix(Point currentPosition){
        //I have the current position, I need to build the final matrix
        this.dynamicMapLevel = new HashMap<>();
        //I don't need to check all the level. The biggest one is the only one that I am keeping totally
        //all the others I don't need since I can use the children of the cell
        //this is the first level cell where the point is
        Cell levelCell = this.getCellWhereIBelong(currentPosition , 4.0);
        //return the sector of this cell
        Integer sector = this.getSector(levelCell, currentPosition);
        //now sector could be from 1 to 9
        this.discriminateSector(sector,levelCell, null, 4.0);

        //I build first level. From that level I should check in the sub cell of all the cells that are splittable
        List<Cell> splittableCells = new ArrayList<>();
        this.dynamicMapLevel.get(4.0).stream().filter(Cell::isSplittable).forEach(splittableCells::add);

        Double level = 3.0;
        while (!splittableCells.isEmpty()){
            //having this list I should find all the sub cells of these cells
            List<Cell> subSplittableCells = new ArrayList<>();
            splittableCells.forEach(cell -> cell.getSubCells().forEach(subSplittableCells::add));

            //this is the first level cell where the point is
            levelCell = this.getCellWhereIBelong(currentPosition , subSplittableCells);
            //now I know at which sub cell I belong. I need to update the matrix
            //return the sector of this cell
            sector = this.getSector(levelCell, currentPosition);
            //now sector could be from 1 to 9
            this.discriminateSector(sector, levelCell, subSplittableCells, level);

            splittableCells.clear();
            subSplittableCells.stream().filter(Cell::isSplittable).forEach(splittableCells::add);
            level--;
        }

        /*
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
        });*/
    }

    //get all the point of interest from selected level. This means that I am using only the cell that are not going to be split
    //It is using the dynamic map because in that one I have the actual POIs
    //level = level that I am interest in finding all the POI
    //kind of tested
    private List<Cell> getPOIsInSelectedLevel(Double level){
        List<Cell> newPOIsList = new ArrayList<>();
        this.mapLevel.get(level).stream().filter(cell -> !cell.isSplittable()).forEach(newPOIsList::add);
        /*this.dynamicMapLevel.get(level).forEach(cell -> {
            if(!cell.isSplittable()) newPOIsList.add(new POI(cell.getCenter(), cell.getAverageCharge()));
        });*/
        return newPOIsList;
    }

    //same as before but instead have the level like a parameter has a list of cells
    //kind of tested
    private List<POI> getPOIsInSelectedLevel(List<Cell> cells){
        List<POI> newPOIsList = new ArrayList<>();
        //cells.stream().forEach(cell -> newPOIsList.add(new POI(cell.getPOIsCenterOfMass(), cell.getAverageCharge())));
        cells.stream().forEach(cell -> newPOIsList.add(new POI(cell.getPOIsCenterOfMass(), cell.getAverageCharge())));
        /*cells.forEach(cell -> {
            if(!cell.isSplittable()) newPOIsList.add(new POI(cell.getCenter(), cell.getAverageCharge()));
        });*/
        return newPOIsList;
    }

    //get all the center point of the cell. I am returning only the cells not splittable
    //It is using the dynamic map because in that one I have the actual centers
    //level = level that I am interest in finding all the POI
    //kind of tested
    private List<Point> getCenterPointsInSelectedLevel(Double level){
        List<Point> newPointsList = new ArrayList<>();
        this.dynamicMapLevel.get(level).stream().filter(cell -> !cell.isSplittable()).forEach(cell -> newPointsList.add(cell.getCenter()));
        /*this.dynamicMapLevel.get(level).forEach(cell -> {
            if(!cell.isSplittable()) newPointsList.add(cell.getCenter());
        });*/
        return newPointsList;
    }

    //same as before but instead have the level like a parameter has a list of cells
    //kind of tested
    private List<Point> getCenterPointsInSelectedLevel(List<Cell> cells){
        List<Point> newPointsList = new ArrayList<>();
        //cells.stream().forEach(cell -> newPointsList.add(cell.getCenter()));
        cells.stream().forEach(cell -> newPointsList.add(cell.getCenter()));
        /*cells.forEach(cell -> {
            if(!cell.isSplittable()) newPointsList.add(cell.getCenter());
        });*/
        return newPointsList;
    }

    //put the potential value calculated inside the actual level
    //I need this so when I am going to visualise it I can call directly the matrix instead a simpler vector
    //List<Double> potentialValue = list with all the calculated potential
    //List<Cell> cells = These are the cells that have to be updated
    //kind of tested
    private void setPotentialValueIntoSelectedLevel(List<Double> potentialValue, List<Cell> cells){

        final Integer[] i = {0};
        for(Cell singleCell : cells){
            Double level= 0.0;
            switch (singleCell.getSize().intValue()){
                case 10:
                    level = 0.0;
                    break;
                case 50:
                    level = 1.0;
                    break;
                case 100:
                    level = 2.0;
                    break;
                case 500:
                    level = 3.0;
                    break;
                case 1000:
                    level = 4.0;
                    break;
            }
            this.dynamicMapLevel.get(level).stream().filter(aCell -> aCell.getId().equals(singleCell.getId())).findFirst().ifPresent(bCell -> {
                bCell.setPotential(potentialValue.get(i[0]));
                i[0]++;
            });
        }



        /*Integer i = 0;
        for(Cell singleCell : cells){
            Cell c = listCells.stream().filter(cell -> cell.getId().equals(singleCell.getId())).findFirst().get();
            if( c != null ){
                c.setPotential(potentialValue.get(i));
                i++;
            }
        }
        */


        /*Integer j = 0;
        for(int i = 0; i < listNotSplittableCells.size(); i ++){
            if(cells == null){
                listNotSplittableCells.get(i).setPotential(potentialValue.get(i));
            }else {
                Cell cell = listNotSplittableCells.get(i);
                if(cells.stream().filter(c -> c.getId().equals(cell.getId())).findFirst().isPresent()){
                    listNotSplittableCells.get(i).setPotential(potentialValue.get(j));
                    j++;
                }
            }

        }*/

    }

    //get all the potential value in a list on the selected level
    //level = level that I am interest in finding all the POI
    //kind of tested
    private List<Double> getPotentialValueInSelectedLevel(Double level){
        List<Double> list = new ArrayList<>();
        this.dynamicMapLevel.get(level).stream().filter(cell -> cell.getPotential() != -900.0).forEach(cell -> list.add(cell.getPotential()));
        return list;
    }

    //return list of cell that are gonna be split in the level under the one where I am
    //this means that I am returning only the sub cell of the cell that in this level are splittable
    //level = level that I am interest in finding all the POI
    private List<Cell> getSubCellOfSplittableCells(Double level){
        List<Cell> cells = new ArrayList<>();
        this.dynamicMapLevel.get(level).forEach(cell -> {
            if(cell.isSplittable()){
                cell.getSubCells().forEach(cellID -> cells.add(this.dynamicMapLevel.get(level - 1).stream().filter(c -> c.getId().equals(cellID.getId())).findFirst().get()));
            }
        });
        return cells;
    }

    //wrapper for the calculation of the potential field in all of the points in the word
    //the input is
    //artPotField -> the force field selected for our program
    //kind of tested
    public void computeForceInAllOfThePoints(ForceField artPotField){
        //save the list of the potential value returned from the method
        //List<Double> numberOfLevels = Arrays.asList(3.0,2.0,1.0,0.0);
        /*HashMap<Double,Double> numbersOfLevels = new HashMap<>();
        numbersOfLevels.put(4.0,3.0);
        numbersOfLevels.put(3.0,2.0);
        numbersOfLevels.put(2.0,1.0);
        numbersOfLevels.put(1.0,0.0);
        

        //In first level I am calculating only that are not going to be split
        this.setPotentialValueIntoSelectedLevel(4.0, artPotField.calculateForceInAllTheWorld(this.getCenterPointsInSelectedLevel(4.0), this.getPOIsInSelectedLevel(4.0)), null);

        //In second level I am computing only the one that are not gonna be split but only in the cell that are split in the first one
        numbersOfLevels.entrySet().parallelStream().forEach(element -> {
            List<Cell> splittableCells = this.getSubCellOfSplittableCells(element.getKey());
            this.setPotentialValueIntoSelectedLevel(element.getValue(), artPotField.calculateForceInAllTheWorld(this.getCenterPointsInSelectedLevel(splittableCells),this.getPOIsInSelectedLevel(splittableCells)), splittableCells);
        });


        //now I have calculated the potential for all the cell, I should normalise it
        List<Double> numberOfLevels = Arrays.asList(4.0,3.0,2.0,1.0,0.0);
        List<Double> totalPotential = new ArrayList<>();
        numberOfLevels.stream().forEach(level -> this.getPotentialValueInSelectedLevel(level).stream().forEach(totalPotential::add));

        //now inside totalPotential I have a list of all the potential value. I can compute the values that I need to normalise them
        Double maxList = totalPotential.stream().max(Comparator.naturalOrder()).get();
        Double minList = totalPotential.stream().min(Comparator.naturalOrder()).get();
        Double max = 0.0;
        Double min = 255.0;

        //set the normalise potential to every cells
        //normalise and scale heatMapValue for use the result like a rgb value
        //Print the heat map value on a file
        //Instead from 0 to 255 I am scaling the value from 255 to 0 (inverted) so I can print only the attractive points
        numberOfLevels.stream().forEach(level -> this.dynamicMapLevel.get(level).stream().forEach(cell -> {
            Double standard = (cell.getPotential() - minList) / (maxList - minList);
            Double scaled = standard * (max - min) + min;
            cell.setNormalisedPotential(scaled);
        }));*/
        //reset both potential on the first level (I need this after the compute initial force in all the points )
        //this.mapLevel.get(0.0).stream().forEach(Cell::resetPotential);

        //In first level I am calculating only that are not going to be split

        /*
        List<Cell> splittableCells = new ArrayList<>();
        this.dynamicMapLevel.get(4.0).stream().filter(cell -> !cell.isSplittable()).forEach(splittableCells::add);

        this.setPotentialValueIntoSelectedLevel(4.0, artPotField.calculateForceInAllTheWorld(this.getCenterPointsInSelectedLevel(splittableCells), this.getPOIsInSelectedLevel(splittableCells)), splittableCells);

        //In second level nad so on I am computing only the sub cell of the cell that can be split in the first level
        List<Double> numberOfRealLevels = Arrays.asList(3.0,2.0,1.0,0.0);
        List<Cell> subSplittableCells = new ArrayList<>();

        //on splittable cell list now I have to put the cell that are splittable, not the others
        splittableCells.clear();
        this.dynamicMapLevel.get(4.0).stream().filter(Cell::isSplittable).forEach(splittableCells::add);

        numberOfRealLevels.forEach(level -> {
            subSplittableCells.clear();
            splittableCells.forEach(cell -> cell.getSubCells().forEach(subSplittableCells::add));

            //find the cell that I have to compute the potential
            List<Cell> subNotSplittableCells = new ArrayList<>();
            subSplittableCells.stream().filter(cell -> !cell.isSplittable()).forEach(subNotSplittableCells::add);

            //I need to populate second level of the matrix
            this.setPotentialValueIntoSelectedLevel(level, artPotField.calculateForceInAllTheWorld(this.getCenterPointsInSelectedLevel(subNotSplittableCells), this.getPOIsInSelectedLevel(subNotSplittableCells)), subNotSplittableCells);

            //adding for the next loop only the cells that are splittable
            splittableCells.clear();
            subSplittableCells.stream().filter(Cell::isSplittable).forEach(splittableCells::add);
        });

        //now I have calculated the potential for all the cell, I should normalise it
        List<Double> numberOfLevels = Arrays.asList(4.0,3.0,2.0,1.0,0.0);
        this.normalisePotentialValue(numberOfLevels);

        */

        //What I wrote since now it's bullshit
        List<Cell> listCellToUpdate = new ArrayList<>();
        List<POI> listOfAllThePOI = new ArrayList<>();
        List<Point> listOfAllThePoint = new ArrayList<>();

        List<Cell> splittableCells = new ArrayList<>();
        this.dynamicMapLevel.get(4.0).stream().filter(c -> !c.getPOIs().isEmpty()).filter(cell -> !cell.isSplittable()).forEach(splittableCells::add);
        splittableCells.forEach(listCellToUpdate::add); //keep track which cell I need to update

        this.getCenterPointsInSelectedLevel(splittableCells).forEach(listOfAllThePoint::add);
        this.getPOIsInSelectedLevel(splittableCells).forEach(listOfAllThePOI::add);

        //In second level nad so on I am computing only the sub cell of the cell that can be split in the first level
        List<Double> numberOfRealLevels = Arrays.asList(3.0,2.0,1.0,0.0);
        List<Cell> subSplittableCells = new ArrayList<>();

        //on splittable cell list now I have to put the cell that are splittable, not the others
        splittableCells.clear();
        this.dynamicMapLevel.get(4.0).stream().filter(Cell::isSplittable).forEach(splittableCells::add);

        numberOfRealLevels.forEach(level -> {
            subSplittableCells.clear();
            splittableCells.forEach(cell -> cell.getSubCells().forEach(subSplittableCells::add));

            //find the cell that I have to compute the potential
            List<Cell> subNotSplittableCells = new ArrayList<>();
            subSplittableCells.stream().filter(c -> !c.getPOIs().isEmpty()).filter(cell -> !cell.isSplittable()).forEach(subNotSplittableCells::add);
            subNotSplittableCells.forEach(listCellToUpdate::add); //keep track which cell I need to update

            this.getCenterPointsInSelectedLevel(subNotSplittableCells).forEach(listOfAllThePoint::add);
            this.getPOIsInSelectedLevel(subNotSplittableCells).forEach(listOfAllThePOI::add);


            //adding for the next loop only the cells that are splittable
            splittableCells.clear();
            subSplittableCells.stream().filter(Cell::isSplittable).forEach(splittableCells::add);
        });

        //I need to populate second level of the matrix
        this.setPotentialValueIntoSelectedLevel(artPotField.calculateForceInAllTheWorld(listOfAllThePoint, listOfAllThePOI), listCellToUpdate);

        //now I have calculated the potential for all the cell, I should normalise it
        List<Double> numberOfLevels = Arrays.asList(4.0,3.0,2.0,1.0,0.0);
        this.normalisePotentialValue(numberOfLevels);

    }

    //update all the POIs charge in the new map
    //I need to update the POI only in the cells not splittable of the first level. Then following the splittable cell I go down and repeat the update
    //the inputs are
    //currentPosition -> point where the tracked person is right now
    //angle -> this is the angle that the tracked person is using to move respect the x axis
    //threshold angle
    //kind of tested
    public void updatePOIcharge(Point currentPosition, UpdateRules updateRule){
        //list of all the POIs -> I need this list to save them on file
        List<POI> totalList = new ArrayList<>();
        //All the POI in level 4.0
        List<Cell> listOfPOIsToUpdate = new ArrayList<>();
        this.dynamicMapLevel.get(4.0).stream().filter(c -> !c.getPOIs().isEmpty()).filter(cell -> !cell.isSplittable()).forEach(listOfPOIsToUpdate::add);
        //List<Cell> listOfPOIsToUpdate = this.getPOIsInSelectedLevel(4.0);

        //I should check if I reach the POI(in the lowest level)
        Cell amInsidePOI = this.arrivedIntoPOI(currentPosition, this.dynamicMapLevel.get(0.0));

        //If I am not inside the POI I act normally
        if(amInsidePOI == null) {
            listOfPOIsToUpdate.stream().forEach(cell -> cell.getPOIs().stream().forEach(aPointsOfInterest -> {
                updateRule.PFPathPlanning(currentPosition); //compute the potential field
                updateRule.computeUpdateRule(currentPosition,aPointsOfInterest.getArea().getPolygon().getCenterPoint());
                //check if the current angle is inside or outside the angle plus or minus the threshold
                if(updateRule.doINeedToUpdate() != null) {
                    if (updateRule.doINeedToUpdate()) {
                        //in this case the path is inside our interest area so we should increase the attractiveness of this poi
                        aPointsOfInterest.increaseCharge(updateRule.getHowMuchIncreaseTheCharge());
                    } else {
                        //in this case the path is outside our interest area so we should decrease the attractiveness of this poi
                        aPointsOfInterest.decreaseCharge(updateRule.getHowMuchDecreaseTheCharge());
                    }
                }
            }));
            //I am not inside a POI so i do not need to increase the value. I
            //If i stayed less than n time step inside a POI I reset the value
            this.checkTimeStepAfterTarget(Boolean.FALSE);
        }else{
            //If I am inside the POI I All the other cells have to decrease their charge
            listOfPOIsToUpdate.stream().forEach(cell -> cell.getPOIs().stream().forEach(aPointsOfInterest -> aPointsOfInterest.decreaseCharge(updateRule.getHowMuchDecreaseTheChargeInsidePOI())));
        }
        //calculate new average for every cell used before
        listOfPOIsToUpdate.stream().forEach(Cell::computeAverageCharge);

        //remember POI
        listOfPOIsToUpdate.stream().forEach(cell -> cell.getPOIs().stream().forEach(totalList::add));



        //get the splittable cell in last level
        List<Cell> splittableCells = new ArrayList<>();
        this.dynamicMapLevel.get(4.0).stream().filter(Cell::isSplittable).forEach(splittableCells::add);
        //get sub cell of the splittable
        List<Cell> subSplittableCells = new ArrayList<>();

        while (!splittableCells.isEmpty()) {
            //check all the sub cells of the splittable cells
            subSplittableCells.clear();
            splittableCells.forEach(cell -> cell.getSubCells().forEach(subSplittableCells::add));

            //now I need to update the POI only in the cells that are not splittable
            List<Cell> notSplittableCell = new ArrayList<>();
            subSplittableCells.stream().filter(c -> !c.getPOIs().isEmpty()).filter(cell -> !cell.isSplittable()).forEach(notSplittableCell::add);

            if(amInsidePOI == null) {
                //listOfPOIsToUpdate = this.getPOIsInSelectedLevel(subSplittableCells);
                notSplittableCell.stream().forEach(cell -> cell.getPOIs().stream().forEach(aPointsOfInterest -> {
                    updateRule.PFPathPlanning(currentPosition); //compute the potential field
                    updateRule.computeUpdateRule(currentPosition,aPointsOfInterest.getArea().getPolygon().getCenterPoint());
                    //check if the current angle is inside or outside the angle plus or minus the threshold
                    if(updateRule.doINeedToUpdate() != null) {
                        if (updateRule.doINeedToUpdate()) {
                            //in this case the path is inside our interest area so we should increase the attractiveness of this poi
                            aPointsOfInterest.increaseCharge(updateRule.getHowMuchIncreaseTheCharge());
                        } else {
                            //in this case the path is outside our interest area so we should decrease the attractiveness of this poi
                            aPointsOfInterest.decreaseCharge(updateRule.getHowMuchDecreaseTheCharge());
                        }
                    }
                }));
                //I am not inside a POI so i do not need to increase the value. I
                //If i stayed less than n time step inside a POI I reset the value
                this.checkTimeStepAfterTarget(Boolean.FALSE);
            }else{
                //If I am inside the POI I All the other cells have to decrease their charge
                notSplittableCell.stream().forEach(cell -> cell.getPOIs().stream().forEach(aPointsOfInterest -> aPointsOfInterest.decreaseCharge(updateRule.getHowMuchDecreaseTheChargeInsidePOI())));
            }
            //calculate new average for every cell used before
            notSplittableCell.stream().forEach(Cell::computeAverageCharge);
            //remember POI
            notSplittableCell.stream().forEach(cell -> cell.getPOIs().stream().forEach(totalList::add));

            splittableCells.clear();
            subSplittableCells.stream().filter(Cell::isSplittable).forEach(splittableCells::add);
        }
        //If I am in the POI after having decreased all the other POI I have to increase this POI
        if(amInsidePOI != null) {
            amInsidePOI.getPOIs().stream().forEach(aPointOfInterest -> aPointOfInterest.increaseCharge(updateRule.getHowMuchIncreaseTheChargeInsidePOI()));
            //remember POI
            amInsidePOI.getPOIs().stream().forEach(totalList::add);
            //I am inside a POI, I should count how many time step before stop the tracking
            this.checkTimeStepAfterTarget(Boolean.TRUE);
        }

        //update performance
        this.performance.addValue(totalList.stream().filter(poi -> poi.getCharge() > 0.0).count());
        //update all the POI and the charge
        List<Double> charges = new ArrayList<>();
        totalList.stream().forEach(poi -> charges.add(poi.getCharge()));
        this.performance.addCharges(charges);


        /*List<Double> numberOfLevels = Arrays.asList(5.0,4.0,3.0,2.0,1.0);

        numberOfLevels.stream().forEach(level ->{
            List<POI> listOfPOIsToUpdate = new ArrayList<>();
            if(level == 5.0){//for level 4.0 I need to find the POIs in this level (it is coded like 5.0 in the if clause because 4.0 I will use later)
                listOfPOIsToUpdate = this.getPOIsInSelectedLevel(4.0);
            }else{//for all the others levels I find the POI from the Splittable Cell of the previous
                List<Cell> splittableCells = this.getSubCellOfSplittableCells(level);
                listOfPOIsToUpdate = this.getPOIsInSelectedLevel(splittableCells);
            }
            listOfPOIsToUpdate.stream().forEach(aPointsOfInterest ->{
                Double currentAngle = Math.toDegrees(Math.atan2(aPointsOfInterest.getArea().getPolygon().getCenterPoint().getY() - currentPosition.getY(), aPointsOfInterest.getArea().getPolygon().getCenterPoint().getX() - currentPosition.getX()));
                //check if the current angle is inside or outside the angle plus or minus the threshold
                if(currentAngle >= angle - threshold && currentAngle <= angle + threshold ){
                    //in this case the path is inside our interest area so we should increase the attractiveness of this poi
                    aPointsOfInterest.increaseCharge(0.1); //TODO is 0.1 the best value?
                }else{
                    //in this case the path is outside our interest area so we should decrease the attractiveness of this poi
                    aPointsOfInterest.decreaseCharge(0.1); //TODO is 0.1 the best value?
                }
            });
        });*/


    }

    //return the list of all the normalised charge in the current level.
    //if the level is the higher all the tiles has charge, for the other levels only the sub tile of the father has a value, all the other have to be null
    //I can not set null so I am setting -900.0 that is an impossible value for that value.
    //kind of tested
    public List<Double> getChargeInSelectedLevel(Double level){
        List<Double> chargeList = new ArrayList<>();
        this.dynamicMapLevel.get(level).forEach(cell -> chargeList.add(cell.getNormalisedPotential()));
        return chargeList;

       /* if(level == 4.0){//top level, all the tiles have charge
            this.dynamicMapLevel.get(4.0).forEach(cell -> {
                if(cell.isSplittable()){
                    chargeList.add(-900.0);
                }else{
                    chargeList.add(cell.getNormalisedPotential());
                }
            });
            return chargeList;
        }else{

            //I need to check in father level the cell that has to be colored
            List<Cell> listSubCell = this.getSubCellOfSplittableCells(level + 1);
            this.dynamicMapLevel.get(level).forEach(cell -> {
                if(listSubCell.stream().filter(subCell -> subCell.getId().equals(cell.getId())).findFirst().isPresent()){
                    if(cell.isSplittable()){
                        chargeList.add(-900.0);
                    }else{
                        chargeList.add(cell.getNormalisedPotential());
                    }
                }else{
                    chargeList.add(-900.0);
                }
            });
            return chargeList;
        }*/

    }

    //wrapper for the calculation of the potential field in all of the points in the word
    //this is calculating at the beginning of the simulation, so it is using the normal map and calculating only in the small level
    //the input is
    //artPotField -> the force field selected for our program
    public void computeInitialForceInAllOfThePoints(ForceField artPotField){
        //calculating bottom level
        List<POI> newPOIsList = new ArrayList<>();
        this.dynamicMapLevel.get(0.0).stream().forEach(cell -> newPOIsList.add(new POI(cell.getCenter(), cell.getAverageCharge())));
        List<Point> newPointsList = new ArrayList<>();
        this.dynamicMapLevel.get(0.0).stream().forEach(cell -> newPointsList.add(cell.getCenter()));
        List<Double> potentialValue = artPotField.calculateForceInAllTheWorld(newPointsList, newPOIsList);

        //put the value in it
        List<Cell> selectedLevel = this.dynamicMapLevel.get(0.0);
        for(int i = 0; i < potentialValue.size(); i++){
            selectedLevel.get(i).setPotential(potentialValue.get(i));
        }

        //now I have calculated the potential for all the cell, I should normalise it
        List<Double> numberOfLevels = Collections.singletonList(0.0);
        this.normalisePotentialValue(numberOfLevels);
    }


    //set the normalise potential to every cells
    //List<Double> numberOfLevels -> level where I need to normalise the potential
    private void normalisePotentialValue(List<Double> numberOfLevels){
        List<Double> totalPotential = new ArrayList<>();
        numberOfLevels.stream().forEach(level -> this.getPotentialValueInSelectedLevel(level).stream().forEach(totalPotential::add));

        //now inside totalPotential I have a list of all the potential value. I can compute the values that I need to normalise them
        Double maxList = totalPotential.stream().max(Comparator.naturalOrder()).get();
        Double minList = totalPotential.stream().min(Comparator.naturalOrder()).get();
        Double max = 0.0;
        Double min = 255.0;

        //set the normalise potential to every cells
        //normalise and scale heatMapValue for use the result like a rgb value
        //Print the heat map value on a file
        //Instead from 0 to 255 I am scaling the value from 255 to 0 (inverted) so I can print only the attractive points
        numberOfLevels.stream().forEach(level -> this.dynamicMapLevel.get(level).stream().filter(cell -> cell.getPotential() != -900.0).forEach(cell -> {
            Double standard = (cell.getPotential() - minList) / (maxList - minList);
            Double scaled = standard * (max - min) + min;
            if (scaled.isNaN()) scaled = 0.0;
            cell.setNormalisedPotential(scaled);
        }));

    }

    //When I am coping one list to another I need a deep copy otherwise everything screwed up
    private List<Cell> copyEntireList(List<Cell> list){
        return list.stream().map(cell -> cell.deepCopy(Boolean.TRUE)).collect(Collectors.toList());
    }

    //Check if the current position is inside a point of interest. Return the Cell where the POI that contains the actual point is. If Cell is null no POIs contain the currentposition
    private Cell arrivedIntoPOI(Point currentPosition, List<Cell> listCellWithPOItoUpdate){
        try{
            return listCellWithPOItoUpdate.stream().filter(cell -> cell.getPOIs().stream().filter(aPointsOfInterest -> aPointsOfInterest.getArea().getPolygon().contains(currentPosition)).findFirst().isPresent()).findFirst().get();
        }catch (Exception e){
            return null;
        }
    }

    //when I reach The first POI I wait other n time step and then I stop the simulation
    //Input Boolean Inside -> TRUE when I am inside the POI, FALSE I am not and if the counter is grater than 0 i need to decrease to zero
    private void checkTimeStepAfterTarget(Boolean inside){
        if(!inside){
            this.targetCounter = 0;
        }else{
            this.targetCounter++;
            //How many time step do we wait before stopping the tracking?
            //Hardcoded value -> 20
            if(this.targetCounter == 20){
                //Stop the tracking and save all the information
                if(this.conf.getPath() == 0) this.performance.savePath(this.storage);
                if(this.conf.getPOIs() == 0) this.performance.savePOIsInfo(this.storage); //save POIs info
                if(this.conf.getPerformance() == 0 || this.conf.getPerformance() == 2) this.performance.saveInfoToFile(this.storage); //save personal performance
                //Should stop the simulation
                //TODO stop the simulation
            }
        }
    }
}