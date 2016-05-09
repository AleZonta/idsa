package nl.tno.idsa.tools.incident_tests;

import nl.tno.idsa.framework.behavior.incidents.Incident;
import nl.tno.idsa.framework.behavior.planners.IncidentPlanner;
import nl.tno.idsa.framework.behavior.plans.ActionPlan;
import nl.tno.idsa.framework.messaging.Messenger;
import nl.tno.idsa.framework.population.PopulationGenerator;
import nl.tno.idsa.framework.simulator.Sim;
import nl.tno.idsa.framework.utils.Tuple;
import nl.tno.idsa.framework.world.*;
import nl.tno.idsa.library.population.PopulationDataNL;
import nl.tno.idsa.library.world.WorldModelNL;

/**
 * Base class for tests of incidents. This way, incidents can be tested without the GUI.
 */
public abstract class IncidentTester {

    /**
     * Test an incident.
     */
    protected void testIncident() throws Exception {

        // STEP 0. CREATE WORLD.
        // TODO A lot of hardcoded stuff here.
        World world = WorldGenerator.generateWorld(new WorldModelNL(), "../../data/idsa_nav_network.shp", "../../data/idsa_pand_osm_a_utm31n.shp", "../../data/idsa_public_areas_a_utm31n.shp", "../../data/idsa_vbo_utm31n.shp", "../../data/idsa_pand_p_utm31n.shp");
        Environment env = new Environment(world, new Day(21, 9, 2015), new Time(12, 0, 0));
        env.setPopulation(new PopulationGenerator(env, new PopulationDataNL()).generatePopulation("../../data/nl/idsa_cbs_buurten_utm31n.shp"));
        env.initializePopulation(env.getSeason(), null, env.getDay(), env.getTime(), true);
        Messenger.setEnvironment(env);
        Messenger.enableMirrorToConsole(true);

        // STEP 1. CREATE INCIDENT.
        Incident incident = createIncident(env);

        // STEP 2. QUERY AND FILL PARAMETERS.
        System.out.println("\nCREATE INCIDENT PARAMETERS");
        long desiredTime = initializeIncidentParameters(env, incident);
        if (!incident.bindParameters()) {
            System.out.println("Incident parameters not valid.");
            return;
        }

        // STEP 3. CREATE A PLAN.
        System.out.println("\nCREATE INCIDENT PLAN");
        Tuple<ActionPlan, Boolean> planTuple = IncidentPlanner.plan(env, incident);
        if (!planTuple.getSecond()) {
            System.out.println("Plan will not take until " + new Time(desiredTime) +
                    " but until " + new Time(planTuple.getFirst().getGoalAction().getLocationVariable().getValue().getTimeNanos())); // TODO The reported value is possibly wrong.
        }
        ActionPlan plan = planTuple.getFirst();
        plan.startModels(env);      // Also spawns agents if needed.
        System.out.println("\n\nFINAL PLAN:\n" + plan.toString());

        // STEP 5. START THE SIM.
        Sim.getInstance().init(env);
        Sim.getInstance().setXRealTime(100);
        Sim.getInstance().start();
    }

    /**
     * Implementations create the incident to be tested here.
     */
    protected abstract Incident createIncident(Environment env) throws InstantiationException;

    /**
     * Implementations initialize the incident parameters here.
     */
    protected abstract long initializeIncidentParameters(Environment env, Incident incident);
}
