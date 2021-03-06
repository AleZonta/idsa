package nl.tno.idsa.tools.incident_tests;

import nl.tno.idsa.framework.behavior.incidents.Incident;
import nl.tno.idsa.framework.semantics_base.objects.ParameterId;
import nl.tno.idsa.framework.semantics_impl.locations.LocationAndTime;
import nl.tno.idsa.framework.semantics_impl.variables.LocationVariable;
import nl.tno.idsa.framework.semantics_impl.variables.Variable;
import nl.tno.idsa.framework.world.Environment;
import nl.tno.idsa.framework.world.Point;
import nl.tno.idsa.library.incidents.IncidentRoadBlock;

import java.util.Map;

public class TestIncidentRoadBlock extends IncidentTester {

    public static void main(String[] args) throws Exception {
        (new TestIncidentRoadBlock()).testIncident();
    }

    @Override
    protected Incident createIncident(Environment env) throws InstantiationException {
        return new IncidentRoadBlock(env.getWorld());
    }

    @Override
    protected long initializeIncidentParameters(Environment env, Incident incident) {
        Map<ParameterId, Variable> parameters = incident.getParameters();
        long desiredTime = env.getTime().getCopyWithDifference(0, 15, 0).getNanos();      // 15 minuten
        parameters.put(Incident.Parameters.LOCATION_VARIABLE, new LocationVariable(new LocationAndTime(new Point(1876, 2201), desiredTime)));
        return desiredTime;
    }
}


