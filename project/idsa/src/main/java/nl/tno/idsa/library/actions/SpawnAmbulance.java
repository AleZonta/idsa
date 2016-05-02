package nl.tno.idsa.library.actions;

import nl.tno.idsa.framework.semantics_impl.actions.Action;
import nl.tno.idsa.framework.semantics_impl.variables.GroupVariable;
import nl.tno.idsa.framework.world.GeometryType;
import nl.tno.idsa.library.models.ModelSpawn;
import nl.tno.idsa.library.roles.Ambulance;

/**
 * Created by jongsd on 7-8-15.
 */
@SuppressWarnings("unused")
public class SpawnAmbulance extends Action {
    public SpawnAmbulance() {
        super(null,
                // TODO What if there already are medics? Handle that in the model. ...
                // And make sure that all wandering medics match LocationFunction medic spawn point.
                // TODO Medic spawn points are pretty random.
                new GroupVariable(), null, GeometryType.asSet(GeometryType.POINT), ProvidesAgents.YES, AllowsInsertMoveActionBefore.NO);
        setModel(new ModelSpawn(this, Ambulance.class));
    }

    @Override
    @SuppressWarnings("unchecked") // Generic array.
    protected Class<? extends Action>[] createSemanticSuperclassArray() {
        return null;
    }
}