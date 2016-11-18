package nl.tno.idsa.framework.kalman_filter;

/**
 * Created by alessandrozonta on 14/07/16.
 */
//Class implemementing matrix and how use it
public class MatrixDefinition {
    private final Double[][] matrix; // general matrix
    private final Integer row; // number of matrix rows
    private final Integer column; // number of matrix columns
    private final Double dt; //time step

    //normal constructor
    public MatrixDefinition(){
        this.matrix = null;
        this.row = null;
        this.column = null;
        this.dt = null;
    }

    //constructor with two parameters -> Dimension of the matrix
    public MatrixDefinition(Integer row, Integer column){
        this.row = row;
        this.column = column;
        this.matrix = new Double[this.row][this.column];
        this.dt = null;
        for(int i = 0; i < this.row; i++) for (int j = 0; j < this.column; j++) this.matrix[i][j] = 0.0; //initialize matrix with O.o in every cells
    }

    //constructor with three parameters -> Dimension of the matrix and the timestep
    public MatrixDefinition(Integer row, Integer column, Double timeStep){
        this.row = row;
        this.column = column;
        this.matrix = new Double[this.row][this.column];
        this.dt = timeStep;
        for(int i = 0; i < this.row; i++) for (int j = 0; j < this.column; j++) this.matrix[i][j] = 0.0; //initialize matrix with O.o in every cells
    }

    //constructor with four parameters -> Dimension of the matrix, timestep and initial value for the matrix
    public MatrixDefinition(Integer row, Integer column, Double timeStep, Double value){
        this.row = row;
        this.column = column;
        this.matrix = new Double[this.row][this.column];
        this.dt = timeStep;
        for(int i = 0; i < this.row; i++) for (int j = 0; j < this.column; j++) this.matrix[i][j] = value; //initialize matrix with O.o in every cells
    }

    //getter for the matrix
    //quite simple -> no test
    public Double[][] getMatrix(){
        return this.matrix;
    }

    //getter for element of the matrix
    public Double getElement(int x,int y) {
        return this.matrix[x][y];
    }

    //setter for element of the matrix
    public void setElement(int x,int y, Double value) {
        this.matrix[x][y] = value;
    }

    //getter for timestep
    //quite simple -> no test
    public Double getTimeStep(){
        return  this.dt;
    }

    //getter for row
    //quite simple -> no test
    public Integer getRow(){
        return  this.row;
    }

    //getter for column
    //quite simple -> no test
    public Integer getColumn(){
        return  this.column;
    }

    //multiply the matrix for a scalar
    //tested
    public void multiplyFor(Double var){
        for(int i = 0; i < this.row; i++){
            for(int j = 0; j < this.column; j++){
                this.matrix[i][j] *= var;
            }
        }
    }

    //multiply MatrixDefinition for MatrixDefinitio (same number of row and column) and return MatrixDefinition
    //tested
    public MatrixDefinition multiplyFor(MatrixDefinition matrix) throws DifferentMatrixException {
        if (!this.column.equals(matrix.getRow())) throw new DifferentMatrixException("Error with the matrix");
        MatrixDefinition newMatrix = new MatrixDefinition(this.row, matrix.getColumn());
        Double sum = 0.0;
        for(int i = 0; i < this.row; i++){
            for(int z = 0; z < matrix.getColumn(); z++){
                for(int j = 0; j < this.column; j++){
                    sum += this.matrix[i][j] * matrix.getMatrix()[j][z];
                }
                newMatrix.getMatrix()[i][z] = sum;
                sum = 0.0;
            }
        }
        return newMatrix;
    }

    //Return the transpose matrix
    //tested
    public MatrixDefinition transposeMatrix(){
        MatrixDefinition temp = new MatrixDefinition(this.column,this.row,this.dt);
        for (int i = 0; i < this.row; i++)
            for (int j = 0; j < this.column; j++)
                temp.getMatrix()[j][i] = this.matrix[i][j];
        return temp;
    }

    //sum two MatrixDefinition together
    //tested
    public MatrixDefinition sumWith(MatrixDefinition matrix) throws DifferentMatrixException{
        if (!this.row.equals(matrix.row)) throw new DifferentMatrixException("Error with the matrix");
        if (!this.column.equals(matrix.column)) throw new DifferentMatrixException("Error with the matrix");
        MatrixDefinition newMatrix = new MatrixDefinition(this.row, this.column);
        for(int i = 0; i < this.row; i++){
            for(int j = 0; j < this.column; j++){
                newMatrix.getMatrix()[i][j] += this.matrix[i][j] + matrix.getMatrix()[i][j];
            }
        }
        return newMatrix;
    }

    //inverse of the matrix
    //code from http://www.sanfoundry.com/java-program-find-inverse-matrix/
    //tested
    public MatrixDefinition inverseMatrix() throws DifferentMatrixException{
        if (!this.row.equals(this.column)) throw new DifferentMatrixException("Error with the matrix");
        int n = this.row;
        if (determinant(this.matrix,n) == 0) throw new DifferentMatrixException("Error with the matrix");
        MatrixDefinition res = new MatrixDefinition(n,n,this.dt);
        Double[][] b = new Double[n][n];
        for (int i = 0; i < n ; i++) {
            for (int j = 0; j < n; j++) {
                if(i == j) {
                    b[i][j] = 1.0;
                } else {
                    b[i][j] = 0.0;
                }

            }
        }

        // Transform the matrix into an upper triangle
        Integer[] index = gaussian(this.matrix, n);

        // Update the matrix b[i][j] with the ratios stored
        for (int i = 0; i < n - 1; ++i)
            for (int j = i+1; j < n; ++j)
                for (int k = 0; k < n; ++k)
                    b[index[j]][k] -= this.matrix[index[j]][i] * b[index[i]][k];

        // Perform backward substitutions
        for (int i = 0; i < n; ++i)
        {
            res.getMatrix()[n-1][i] = b[index[n-1]][i] / this.matrix[index[n-1]][n-1];
            for (int j = n-2; j >= 0; --j)
            {
                res.getMatrix()[j][i] = b[index[j]][i];
                for (int k = j+1; k < n; ++k)
                {
                    res.getMatrix()[j][i] -= this.matrix[index[j]][k] * res.getMatrix()[k][i];
                }
                res.getMatrix()[j][i] /= this.matrix[index[j]][j];
            }
        }
        return res;
    }

    // Method to carry out the partial-pivoting Gaussian elimination.  Return Integer[] -> stores pivoting order.
    private Integer[] gaussian(Double[][] a, int n){
        Integer[] index = new Integer[n];
        Double[] c = new Double[n];
        // Initialize the index
        for (int i = 0; i < n; ++i) index[i] = i;
        // Find the rescaling factors, one from each row
        for (int i = 0; i < n; ++i)
        {
            double c1 = 0;
            for (int j = 0; j < n; ++j)
            {
                double c0 = Math.abs(a[i][j]);
                if (c0 > c1) c1 = c0;
            }
            c[i] = c1;
        }
        // Search the pivoting element from each column
        int k = 0;
        for (int j = 0; j < n - 1; ++j)
        {
            double pi1 = 0;
            for (int i = j; i < n; ++i)
            {
                double pi0 = Math.abs(a[index[i]][j]);
                pi0 /= c[index[i]];
                if (pi0 > pi1)
                {
                    pi1 = pi0;
                    k = i;
                }
            }
            // Interchange rows according to the pivoting order
            int itmp = index[j];
            index[j] = index[k];
            index[k] = itmp;
            for (int i = j+1; i < n; ++i)
            {
                double pj = a[index[i]][j]/a[index[j]][j];
                // Record pivoting ratios below the diagonal
                a[index[i]][j] = pj;

                // Modify other elements accordingly
                for (int l = j+1; l < n; ++l)
                    a[index[i]][l] -= pj*a[index[j]][l];
            }
        }
        return  index;
    }

    // Method to calculate determinant of the matrix
    // see http://www.sanfoundry.com/java-program-compute-determinant-matrix/
    private Double determinant(Double A[][], int n){
        double det=0;
        if(n == 1)
        {
            det = A[0][0];
        }
        else if (n == 2)
        {
            det = A[0][0] * A[1][1] - A[1][0] * A[0][1];
        }
        else
        {
            det=0;
            for(int j1 = 0; j1 < n; j1++)
            {
                Double[][] m = new Double[n-1][];
                for(int k = 0; k < (n-1); k++)
                {
                    m[k] = new Double[n-1];
                }
                for(int i = 1; i < n; i++)
                {
                    int j2=0;
                    for(int j = 0; j < n; j++)
                    {
                        if(j == j1)
                            continue;
                        m[i-1][j2] = A[i][j];
                        j2++;
                    }
                }
                det += Math.pow(-1.0, 1.0 + j1 + 1.0)* A[0][j1] * determinant(m,n-1);
            }
        }
        return det;
    }

    //calculate difference between matrix (same number of row and column)
    //tested
    public MatrixDefinition differenceWith(MatrixDefinition matrix) throws DifferentMatrixException{
        //if both matrix are vectors I can compute this
        if  (!(this.column == matrix.column && matrix.column == 1)) {
            if (!this.row.equals(this.column)) throw new DifferentMatrixException("Error with the matrix");
            if (!matrix.getRow().equals(matrix.getColumn()))
                throw new DifferentMatrixException("Error with the matrix");
            if (!this.row.equals(matrix.getRow())) throw new DifferentMatrixException("Error with the matrix");
        }
        MatrixDefinition result = new MatrixDefinition(this.row,this.column);
        for(int i = 0; i < this.row; i++){
            for(int j = 0; j < this.column; j++){
                result.getMatrix()[i][j] = this.matrix[i][j] - matrix.getMatrix()[i][j];
            }
        }
        return result;
    }

}
