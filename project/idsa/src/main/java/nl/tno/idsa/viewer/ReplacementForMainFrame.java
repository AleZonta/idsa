package nl.tno.idsa.viewer;

import nl.tno.idsa.framework.agents.Agent;
import nl.tno.idsa.framework.potential_field.ActivityNotImplementedException;
import nl.tno.idsa.framework.potential_field.EmptyActivityException;
import nl.tno.idsa.framework.potential_field.PotentialField;
import nl.tno.idsa.framework.potential_field.TrackingSystem;
import nl.tno.idsa.framework.potential_field.performance_checker.PerformanceChecker;
import nl.tno.idsa.framework.potential_field.performance_checker.PersonalPerformance;
import nl.tno.idsa.framework.simulator.Sim;
import nl.tno.idsa.framework.world.Environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by alessandrozonta on 30/08/16.
 */
public class ReplacementForMainFrame {

    private final Sim sim;
    private final PotentialField pot; //This is the base instance of the pot
    private HashMap<Long,PotentialField> listPot; //Every tracked agent need its own potential field. I will deep copy the base instance for all the tracked agents and I will store them here. Save PotentialField with the id of the agent tracked
    private HashMap<Long,TrackingSystem> listTrack; //Every tracked agent need its own tracking system. I will deep copy the base instance for all the tracked agents and I will store them here. Save PotentialField with the id of the agent tracked
    private final Integer maxNumberOfElementTrackable;
    private final PerformanceChecker performance; //keep track of the performance of the simulator
    private Integer checker; //if the number of element inside the list of updating is not changing for checker time step so I kill that person
    private Integer previousNumber; //previous number of tracked people
    private Integer waitingForTimestep;

    public ReplacementForMainFrame(Sim sim, Integer number){
        this.sim = sim;
        this.pot = sim.getPot();
        this.maxNumberOfElementTrackable = number;
        this.listPot = new HashMap<>();
        this.listTrack = new HashMap<>();
        this.performance = new PerformanceChecker();
        this.checker = 0;
        this.previousNumber = 0;
    }

    //Method that is finding all the people outside a building and checking if the number found is under the limit set by config file
    //For every person tracked it instantiates the potential field and the tracking system and connect them to the agent
    public void trackEveryone(){
        Environment env = this.sim.getEnvironment();
        List<Agent> agent = env.getAgents();

        //I am tracking all the agents that are not inside at the starting point of the simulation
        agent.stream().filter(this.pot::isOutside).limit(this.maxNumberOfElementTrackable).forEach(a -> {
            try {
                //new agent tracked new potential field for him
                PotentialField fieldForTheTrackedAgent = this.pot.deepCopy();
                TrackingSystem trackingForTheTrackedAgent = new TrackingSystem(fieldForTheTrackedAgent);

                PersonalPerformance personalPerformance = new PersonalPerformance(); //prepare class for personal performance
                fieldForTheTrackedAgent.setPerformance(personalPerformance); //set personal performance on the field
                this.performance.addPersonalPerformance(a.getId(),personalPerformance); //connect performance with id person and put them together in a list

                fieldForTheTrackedAgent.setMainFrameReference(this);
                fieldForTheTrackedAgent.setTrackedAgent(a);
                a.deleteObservers(); //delete old observers
                a.setTracked(trackingForTheTrackedAgent, null); //set the observer to this point

                //Add potential field and tracking system to their list
                this.listPot.put(a.getId(),fieldForTheTrackedAgent);
                this.listTrack.put(a.getId(),trackingForTheTrackedAgent);
                System.out.println("Loaded Potential Field for person number " + this.listPot.size() + "...");


            } catch (EmptyActivityException | ActivityNotImplementedException e) {
                //No planned activity. I do not need to do anything. The exception doesn't add the agent to the list
                e.printStackTrace();
            }
        });
        //set the initial number
        this.previousNumber = this.listPot.size();
    }


    //remove the tracked person from the list
    //Input
    //Long Id -> Person id that i have to remove from the list
    public void removeFromTheLists(Long agentId){
        this.listPot.remove(agentId);
        this.listTrack.remove(agentId);
        System.out.println("Removing tracked agent from the list. Remaining agents -> " + this.listPot.size() + "...");
        //once I removed the agent i should check how many of them are still alive
        //If no one is alive stop the simulation
        if(this.listPot.isEmpty()){
            //now I should also save all the performance of all the person
            //I have to compute the total performance before saving it
            Integer value = this.pot.getConfig().getPerformance();
            if(value == 1 || value == 2) {
                this.performance.computeGraph();
                System.out.println("Saving performance...");
                //I am using the base instance of pot because it has still the correct path to save this fill
                this.performance.saveTotalPerformance(this.pot.getStorage());
            }
            this.sim.stopEverything();
            System.out.println("Stopping simulation...");
        }
    }

    //check the number of element inside the list of tracked people. If for checker time step is not changing I will get rid of some of them
    public void checkNumberOfTrackedPeople(){
        if(this.listPot.size() == this.previousNumber){
            this.checker ++;
            if(this.checker == 35000){ //hardcoded value. If it reach this level something got wrong so maybe is better delete a user (35000 time step more or less are 7 minutes)
                this.checker = 0;
                //kill them all!
                System.out.println("Force deleting all the remain tracked agent for inactivity...");
                List<Long> key = new ArrayList<>();
                this.listPot.forEach((aLong, potentialField) -> key.add(aLong));
                key.forEach(this::removeFromTheLists);
            }
        }else{
            this.checker = 0;
            this.previousNumber = this.listPot.size();
        }
    }

}
