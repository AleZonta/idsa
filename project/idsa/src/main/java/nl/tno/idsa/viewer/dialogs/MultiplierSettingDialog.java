package nl.tno.idsa.viewer.dialogs;

import nl.tno.idsa.framework.agents.Agent;
import nl.tno.idsa.framework.behavior.likelihoods.ActivityLikelihoodMap;
import nl.tno.idsa.framework.behavior.likelihoods.DayOfWeek;
import nl.tno.idsa.framework.behavior.multipliers.ISeason;
import nl.tno.idsa.framework.behavior.multipliers.ITimeOfYear;
import nl.tno.idsa.framework.config.ConfigFile;
import nl.tno.idsa.framework.semantics_base.enumerations.RuntimeEnum;
import nl.tno.idsa.framework.utils.TextUtils;
import nl.tno.idsa.framework.world.Day;
import nl.tno.idsa.framework.world.Environment;
import nl.tno.idsa.framework.world.Time;
import nl.tno.idsa.framework.world.World;
import nl.tno.idsa.viewer.components.TimeSetterPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.Month;
import java.util.Vector;

/**
 * Dialog allowing to set multipliers for agendas.
 */
// TODO This dialog should ask for all the multipliers plus the day. Currently the multipliers are hardcoded.
public class MultiplierSettingDialog extends JDialog {

    private static final String CAPTION = "Set multipliers";

    private DayOfWeek selectedDayOfWeek;
    private ISeason selectedSeason;
    private ITimeOfYear selectedTimeOfYear;
    private Time selectedTime;
    private boolean cancelled;

    /**
     * Environment may be null, in which case you need to call applySettingsTo(...) manually or call
     * createEnvironmentWithSettings() to create an environment.
     */
    public MultiplierSettingDialog(JFrame owner, final Environment environment) {
        super(owner, CAPTION, ModalityType.APPLICATION_MODAL);
        createDialog(environment);
    }

    /**
     * Environment may be null, in which case you need to call applySettingsTo(...) manually or call
     * createEnvironmentWithSettings() to create an environment.
     */
    public MultiplierSettingDialog(JDialog owner, final Environment environment) {
        super(owner, CAPTION, ModalityType.APPLICATION_MODAL);
        createDialog(environment);
    }

    //Constructor for the without GUI version
    public MultiplierSettingDialog(ConfigFile conf){
        cancelled = false;

        //select the day
        Vector<DayOfWeek> days = new Vector<>(RuntimeEnum.getInstance(DayOfWeek.class).listOptions());
        //zero is sunday, one is monday, two is friday and three is saturday
        selectedDayOfWeek = days.get(conf.getDayOfWeek());

        //select the season
        Vector<ISeason> seasons = new Vector<>(RuntimeEnum.getInstance(ISeason.class).listOptions());
        UnspecifiedSeason unspecifiedSeason = new UnspecifiedSeason();
        seasons.insertElementAt(unspecifiedSeason, 0);
        //zero is unspecified, one is winter and two is summer
        selectedSeason = seasons.get(conf.getSeason());

        //select Time of the year
        Vector<ITimeOfYear> timesOfYear = new Vector<>(RuntimeEnum.getInstance(ITimeOfYear.class).listOptions());
        UnspecifiedTimeOfYear unspecifiedTimeOfTheYear = new UnspecifiedTimeOfYear();
        timesOfYear.insertElementAt(unspecifiedTimeOfTheYear, 0);
        //zero is unspecified and one is pre christmas
        selectedTimeOfYear = timesOfYear.elementAt(conf.getTimeOfTheYear());


        selectedTime = conf.getTime();
    }

    private void createDialog(final Environment environment) {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        JPanel contentPane = new JPanel(new BorderLayout(3, 3));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        JPanel top = new JPanel(new BorderLayout(3, 3));
        JPanel topLeft = new JPanel(new GridLayout(0, 1, 3, 3));
        top.add(topLeft, BorderLayout.WEST);
        JPanel topRight = new JPanel(new GridLayout(0, 1, 3, 3));
        top.add(topRight, BorderLayout.CENTER);
        contentPane.add(top, BorderLayout.CENTER);

        Vector<DayOfWeek> days = new Vector<>(RuntimeEnum.getInstance(DayOfWeek.class).listOptions());
        final JComboBox<DayOfWeek> daySelector = new JComboBox<>(days);
        daySelector.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                String representation = TextUtils.camelCaseToText(value.getClass().getSimpleName());
                return super.getListCellRendererComponent(list, representation, index, isSelected, cellHasFocus);
            }
        });
        if (days.size() > 1) {
            if (environment != null) {
                DayOfWeek dayOfWeek = DayOfWeek.getDayOfWeek(environment.getDay());
                daySelector.setSelectedItem(dayOfWeek);
            }
            topLeft.add(new JLabel("Day"));
            topRight.add(daySelector);
        }

        Vector<ISeason> seasons = new Vector<>(RuntimeEnum.getInstance(ISeason.class).listOptions());
        UnspecifiedSeason unspecifiedSeason = new UnspecifiedSeason();
        seasons.insertElementAt(unspecifiedSeason, 0);
        final JComboBox<ISeason> seasonSelector = new JComboBox<>(seasons);
        seasonSelector.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                String representation = TextUtils.camelCaseToText(value.getClass().getSimpleName());
                return super.getListCellRendererComponent(list, representation, index, isSelected, cellHasFocus);
            }
        });
        if (seasons.size() > 1) {
            if (environment != null) {
                if (environment.getSeason() != null) {
                    seasonSelector.setSelectedItem(environment.getSeason());
                } else {
                    seasonSelector.setSelectedItem(unspecifiedSeason);
                }
            }
            topLeft.add(new JLabel("Season"));
            topRight.add(seasonSelector);
        }

        Vector<ITimeOfYear> timesOfYear = new Vector<>(RuntimeEnum.getInstance(ITimeOfYear.class).listOptions());
        UnspecifiedTimeOfYear unspecifiedTimeOfTheYear = new UnspecifiedTimeOfYear();
        timesOfYear.insertElementAt(unspecifiedTimeOfTheYear, 0);
        final JComboBox<ITimeOfYear> timeOfTheYearSelector = new JComboBox<>(timesOfYear);
        timeOfTheYearSelector.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                String representation = TextUtils.camelCaseToText(value.getClass().getSimpleName());
                return super.getListCellRendererComponent(list, representation, index, isSelected, cellHasFocus);
            }
        });
        if (timesOfYear.size() > 1) {
            if (environment != null) {
                if (environment.getTimeOfTheYear() != null) {
                    timeOfTheYearSelector.setSelectedItem(environment.getTimeOfTheYear());
                } else {
                    timeOfTheYearSelector.setSelectedItem(unspecifiedTimeOfTheYear);
                }
            }
            topLeft.add(new JLabel("Time of the year"));
            topRight.add(timeOfTheYearSelector);
        }

        Time time = environment != null ? environment.getTime() : new Time(12, 0, 0);
        final TimeSetterPanel timeSetterPanel = new TimeSetterPanel(time);
        topLeft.add(new JLabel("Time of day"));
        topRight.add(timeSetterPanel);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 3));
        JButton okButton = new JButton(new AbstractAction("OK") {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelled = false;
                selectedDayOfWeek = (DayOfWeek) daySelector.getSelectedItem();
                selectedSeason = (ISeason) seasonSelector.getSelectedItem();
                if (selectedSeason.getClass() == UnspecifiedSeason.class) {
                    selectedSeason = null;
                }
                selectedTimeOfYear = (ITimeOfYear) timeOfTheYearSelector.getSelectedItem();
                if (selectedTimeOfYear.getClass() == UnspecifiedTimeOfYear.class) {
                    selectedTimeOfYear = null;
                }
                selectedTime = timeSetterPanel.getValue();
                applySettingsTo(environment);
                dispose();
            }
        });
        bottom.add(okButton);
        JButton cancelButton = new JButton(new AbstractAction("Cancel") {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelled = true;
                dispose();
            }
        });
        bottom.add(cancelButton);
        contentPane.add(bottom, BorderLayout.SOUTH);

        setLocationRelativeTo(getParent());
        pack();

        setVisible(true);
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public ISeason getSelectedSeason() {
        return selectedSeason;
    }

    public ITimeOfYear getSelectedTimeOfYear() {
        return selectedTimeOfYear;
    }

    public DayOfWeek getSelectedDayOfWeek() {
        return selectedDayOfWeek;
    }

    public Time getSelectedTime() {
        return selectedTime;
    }

    public Environment createEnvironmentWithSettings(World world) {
        return new Environment(world, selectedSeason, selectedTimeOfYear, selectedDayOfWeek.getPrototypeDay(), selectedTime);
    }

    public void applySettingsTo(Environment environment) {
        if (environment == null) {
            return;
        }
        Day selectedDay = (selectedDayOfWeek != null) ? selectedDayOfWeek.getPrototypeDay() : environment.getDay();
        environment.initializePopulation(selectedSeason, selectedTimeOfYear, selectedDay, selectedTime, true);
    }

    private class UnspecifiedSeason implements ISeason {
        @Override
        public void applyMultipliers(Agent agent, ActivityLikelihoodMap agentPossibilities) {

        }

        @Override
        public int getIndex() {
            return -1;
        }
    }

    private class UnspecifiedTimeOfYear implements ITimeOfYear {
        @Override
        public void applyMultipliers(Agent agent, ActivityLikelihoodMap agentPossibilities) {

        }

        @Override
        public int getIndex() {
            return -1;
        }
    }
}
