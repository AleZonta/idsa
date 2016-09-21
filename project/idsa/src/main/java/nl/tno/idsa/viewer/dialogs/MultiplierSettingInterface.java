package nl.tno.idsa.viewer.dialogs;

import nl.tno.idsa.framework.agents.Agent;
import nl.tno.idsa.framework.behavior.likelihoods.ActivityLikelihoodMap;
import nl.tno.idsa.framework.behavior.multipliers.ISeason;
import nl.tno.idsa.framework.behavior.multipliers.ITimeOfYear;
import nl.tno.idsa.framework.world.Environment;
import nl.tno.idsa.framework.world.Time;
import nl.tno.idsa.framework.world.World;

/**
 * Created by alessandrozonta on 14/09/16.
 */
public interface MultiplierSettingInterface {
    String CAPTION = "Set multipliers";

    Environment createEnvironmentWithSettings(World world);

    boolean isCancelled();

    Time getSelectedTime();

    class UnspecifiedSeason implements ISeason {
        @Override
        public void applyMultipliers(Agent agent, ActivityLikelihoodMap agentPossibilities) {

        }

        @Override
        public int getIndex() {
            return -1;
        }
    }

    class UnspecifiedTimeOfYear implements ITimeOfYear {
        @Override
        public void applyMultipliers(Agent agent, ActivityLikelihoodMap agentPossibilities) {

        }

        @Override
        public int getIndex() {
            return -1;
        }
    }
}
