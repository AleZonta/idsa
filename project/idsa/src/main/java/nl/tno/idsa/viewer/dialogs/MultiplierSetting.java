package nl.tno.idsa.viewer.dialogs;

import nl.tno.idsa.framework.agents.Agent;
import nl.tno.idsa.framework.behavior.likelihoods.ActivityLikelihoodMap;
import nl.tno.idsa.framework.behavior.likelihoods.DayOfWeek;
import nl.tno.idsa.framework.behavior.multipliers.ISeason;
import nl.tno.idsa.framework.behavior.multipliers.ITimeOfYear;
import nl.tno.idsa.framework.config.ConfigFile;
import nl.tno.idsa.framework.semantics_base.enumerations.RuntimeEnum;
import nl.tno.idsa.framework.world.Environment;
import nl.tno.idsa.framework.world.Time;
import nl.tno.idsa.framework.world.World;

import java.util.Vector;

/**
 * Created by alessandrozonta on 14/09/16.
 */
public class MultiplierSetting implements MultiplierSettingInterface {

    private DayOfWeek selectedDayOfWeek;
    private ISeason selectedSeason;
    private ITimeOfYear selectedTimeOfYear;
    private Time selectedTime;
    private boolean cancelled;

    //Constructor for the without GUI version
    public MultiplierSetting(ConfigFile conf){
        cancelled = false;

        //select the day
        Vector<DayOfWeek> days = new Vector<>(RuntimeEnum.getInstance(DayOfWeek.class).listOptions());
        //zero is sunday, one is monday, two is friday and three is saturday
        selectedDayOfWeek = days.get(conf.getDayOfWeek());

        //select the season
        Vector<ISeason> seasons = new Vector<>(RuntimeEnum.getInstance(ISeason.class).listOptions());
        MultiplierSetting.UnspecifiedSeason unspecifiedSeason = new MultiplierSetting.UnspecifiedSeason();
        seasons.insertElementAt(unspecifiedSeason, 0);
        //zero is unspecified, one is winter and two is summer
        selectedSeason = seasons.get(conf.getSeason());

        //select Time of the year
        Vector<ITimeOfYear> timesOfYear = new Vector<>(RuntimeEnum.getInstance(ITimeOfYear.class).listOptions());
        MultiplierSetting.UnspecifiedTimeOfYear unspecifiedTimeOfTheYear = new MultiplierSetting.UnspecifiedTimeOfYear();
        timesOfYear.insertElementAt(unspecifiedTimeOfTheYear, 0);
        //zero is unspecified and one is pre christmas
        selectedTimeOfYear = timesOfYear.elementAt(conf.getTimeOfTheYear());


        selectedTime = conf.getTime();
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public Time getSelectedTime() {
        return selectedTime;
    }

    public Environment createEnvironmentWithSettings(World world) {
        return new Environment(world, selectedSeason, selectedTimeOfYear, selectedDayOfWeek.getPrototypeDay(), selectedTime);
    }
}
