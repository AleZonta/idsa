package nl.tno.idsa.framework.kalman_filter;

/**
 * Created by alessandrozonta on 18/07/16.
 */
//Exception thrown if the two matrix cannot multiplied between them because they column number of the first is not equal to the row number of the second
public class DifferentMatrixException extends Exception {
    //Parameterless Constructor
    public DifferentMatrixException() {}

    //Constructor that accepts a message
    public DifferentMatrixException(String message)
    {
        super(message);
    }
}