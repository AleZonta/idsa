package nl.tno.idsa.library.relations.action_requires_role;

import nl.tno.idsa.framework.semantics_impl.relations.RoleEnablesAction;
import nl.tno.idsa.library.actions.Infect;
import nl.tno.idsa.library.roles.MedicalVictim;

/**
 * Created by jongsd on 7-8-15.
 */
// TODO Add ignore unused. Document the class.
public class InfectRequiresMedicalVictim extends RoleEnablesAction {
    public InfectRequiresMedicalVictim() {
        super(MedicalVictim.class, Infect.class, true);      // TODO replace "true/false" with a meaningful enum.
    }
}
