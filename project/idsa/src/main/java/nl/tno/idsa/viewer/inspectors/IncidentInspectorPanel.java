package nl.tno.idsa.viewer.inspectors;

import nl.tno.idsa.framework.agents.Agent;
import nl.tno.idsa.framework.behavior.incidents.Incident;
import nl.tno.idsa.framework.planners.ActionPlan;
import nl.tno.idsa.framework.semantics_impl.actions.Action;
import nl.tno.idsa.framework.world.Time;
import nl.tno.idsa.viewer.SelectionObserver;
import nl.tno.idsa.viewer.components.SimpleGridBagPanel;
import nl.tno.idsa.viewer.observers.RunningIncidents;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Inspect a running incident.
 */
public class IncidentInspectorPanel extends InspectorPanel implements Observer {

    private final SelectionObserver selectionObserver;

    private JList<Incident> incidentList;
    private JList<Agent> agentList;
    private JList<String> actionList;

    private List<Incident> incidents;
    private Vector<Agent> incidentAgents = new Vector<>();

    public IncidentInspectorPanel(RunningIncidents runningIncidentsObserver, SelectionObserver selectionObserver) {
        super(Side.LEFT);

        runningIncidentsObserver.addObserver(this);

        this.selectionObserver = selectionObserver;
        this.selectionObserver.addObserver(this);

        createSubComponents();
        setIncidents(null);
    }

    private void createSubComponents() {
        JPanel main = new SimpleGridBagPanel(SimpleGridBagPanel.Orientation.ROWS);
        incidentList = createClickableIncidentList();
        JComponent[] incidentListRow = createRow("Active incidents", incidentList);
        main.add(createRow(incidentListRow));
        agentList = createClickableAgentList(1);
        agentList.setListData(incidentAgents);
        JComponent[] agentListRow = createRow("Agents involved in selected incident", agentList);
        main.add(createRow(agentListRow));
        getMainPanel().add(main, BorderLayout.NORTH);
        actionList = new JList<>();
        actionList.setBorder(new LineBorder(SystemColor.controlDkShadow, 1));
        JComponent[] actionListRow = createRow("Actions of selected agent", actionList);
        main.add(createRow(actionListRow));
    }

    private void updateSubComponents() {
        if (incidents == null || incidents.size() == 0) {
            incidentList.setListData(new Incident[]{});
            notifyIncidentSelected(null);
            collapse();
        } else {
            incidentList.setListData(new Vector<>(incidents));
            notifyIncidentSelected(incidents.get(0));
            expand();
        }
    }

    @Override
    protected void notifyIncidentSelected(Incident incident) {
        incidentList.setSelectedValue(incident, true);
        selectionObserver.setIncident(incident);
        if (incident == null) {
            incidentAgents.clear();
            agentList.clearSelection();
            notifyAgentSelected(null);
        } else {
            Set<Agent> agentsInvolved = incident.getActionPlan().getAgentsInvolved();
            incidentAgents.clear();
            incidentAgents.addAll(agentsInvolved);
            agentList.setListData(incidentAgents);
            notifyAgentSelected(incidentAgents.get(0));
        }
    }

    @Override
    protected void notifyAgentSelected(Agent agent) {
        Agent oldSelectedValue = agentList.getSelectedIndex() != -1 ? agentList.getSelectedValue() : null;
        agentList.setSelectedValue(agent, true); // This works; if the agent is not currently shown, we deselect.
        if (agentList.getSelectedValue() == null) { // We don't know this agent. Restore old selection.
            agentList.setSelectedValue(oldSelectedValue, true);
        }
        selectionObserver.setAgent(agent);
        if (agent == null) {
            actionList.setListData(new String[]{});
        } else {
            Incident selectedIncident = incidentList.getSelectedValue();
            if (selectedIncident != null) {
                ActionPlan actionPlan = selectedIncident.getActionPlan();
                List<Action> actionSequence = actionPlan.getActionSequence(agent);
                if (actionSequence != null) {
                    String[] actionDescr = new String[actionSequence.size()];
                    for (int i = 0; i < actionSequence.size(); i++) {
                        Action action = actionSequence.get(i);
                        actionDescr[i] = String.format("[%s] %s", new Time(action.getLocationVariable().getValue().getTimeNanos()), action.getVerb());
                    }
                    actionList.setListData(actionDescr);
                } else {
                    actionList.setListData(new String[]{});
                }
            } else {
                actionList.setListData(new String[]{});
            }
        }
    }

    @Override
    protected void notifyAreaSelected(nl.tno.idsa.framework.world.Area area) {
        // DO NOTHING
    }

    private void setIncidents(List<Incident> incidents) {
        this.incidents = incidents;
        updateSubComponents();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void update(Observable o, Object arg) {
        if (o instanceof SelectionObserver) {
            if (arg instanceof Incident) {
                List<Incident> incidents = new ArrayList<>(1);
                incidents.add((Incident) arg);
                setIncidents(incidents);
            }
        } else if (o instanceof RunningIncidents) {
            if (arg instanceof List) {
                List list = (List) arg;
                if ((list.size() > 0 && list.get(0) instanceof Incident)) {
                    setIncidents((List<Incident>) list); // Unchecked cast due to the wonderful world of Java and generics.
                } else {
                    setIncidents(null);
                }
            }
        }
    }
}