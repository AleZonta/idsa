package nl.tno.idsa.viewer;

import nl.tno.idsa.framework.agents.Agent;
import nl.tno.idsa.framework.potential_field.ActivityNotImplementedException;
import nl.tno.idsa.framework.potential_field.EmptyActivityException;
import nl.tno.idsa.framework.potential_field.PotentialField;
import nl.tno.idsa.framework.potential_field.TrackingSystem;
import nl.tno.idsa.framework.simulator.Sim;
import nl.tno.idsa.framework.world.Environment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alessandrozonta on 30/08/16.
 */
public class ReplacementForMainFrame {

    private final Sim sim;
    private final PotentialField pot; //This is the base instance of the pot
    private List<PotentialField> listPot; //Every tracked agent need its own potential field. I will deep copy the base instance for all the tracked agents and I will store them here
    private List<TrackingSystem> listTrack; //Every tracked agent need its own tracking system. I will deep copy the base instance for all the tracked agents and I will store them here
    private final Integer maxNumberOfElementTrackable;

    public ReplacementForMainFrame(Sim sim, Integer number){
        this.sim = sim;
        this.pot = sim.getPot();
        this.maxNumberOfElementTrackable = number;
        this.listPot = new ArrayList<>();
        this.listTrack = new ArrayList<>();
    }

    //Method that is finding all the people outside a building and checking if the number found is under the limit set by config file
    //For every person tracked it instantiates the potential field and the tracking system and connect them to the agent
    public void trackEveryone(){
        Environment env = this.sim.getEnvironment();
        List<Agent> agent = env.getAgents();
        //I am tracking all the agents that are not inside at the starting point of the simulation
        agent.stream().filter(ag -> !ag.isInside()).limit(this.maxNumberOfElementTrackable).forEach(a -> {
            try {
                //new agent tracked new potential field for him
                PotentialField fieldForTheTrackedAgent = this.pot.deepCopy();
                TrackingSystem trackingForTheTrackedAgent = new TrackingSystem(fieldForTheTrackedAgent);

                fieldForTheTrackedAgent.setTrackedAgent(a);
                a.deleteObservers(); //delete old observers
                a.setTracked(trackingForTheTrackedAgent, null); //set the observer to this point

                //Add potential field and tracking system to their list
                this.listPot.add(fieldForTheTrackedAgent);
                this.listTrack.add(trackingForTheTrackedAgent);
                System.out.println("Loaded Potential Field for gent number " + this.listPot.size() + "...");


            } catch (EmptyActivityException | ActivityNotImplementedException e) {
                //No planned activity. I do not need to do anything. The exception doesn't add the agent to the list
                e.printStackTrace();
            }
        });


    }

}
