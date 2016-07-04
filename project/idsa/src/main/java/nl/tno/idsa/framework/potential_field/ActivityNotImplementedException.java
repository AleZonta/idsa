package nl.tno.idsa.framework.potential_field;

/**
 * Created by alessandrozonta on 29/06/16.
 */

//Exception thrown if there is a new activity not listed (after future upgrade of the code maybe you will need to implement new check or change method)
public class ActivityNotImplementedException extends Exception {
    //Parameterless Constructor
    public ActivityNotImplementedException() {}

    //Constructor that accepts a message
    public ActivityNotImplementedException(String message)
    {
        super(message);
    }
}
