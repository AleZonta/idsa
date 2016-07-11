package nl.tno.idsa.framework.potential_field;

/**
 * Created by alessandrozonta on 11/07/16.
 */
//Exception thrown if the the parameter used is not defined
public class ParameterNotDefinedException extends Exception {
    //Parameterless Constructor
    public ParameterNotDefinedException() {}

    //Constructor that accepts a message
    public ParameterNotDefinedException(String message)
    {
        super(message);
    }
}