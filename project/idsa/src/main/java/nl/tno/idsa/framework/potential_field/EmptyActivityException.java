package nl.tno.idsa.framework.potential_field;

/**
 * Created by alessandrozonta on 29/06/16.
 */

//Exception thrown if the agent do not have activity in its agenda
public class EmptyActivityException extends Exception {
    //Parameterless Constructor
    public EmptyActivityException() {}

    //Constructor that accepts a message
    public EmptyActivityException(String message)
    {
        super(message);
    }
}

