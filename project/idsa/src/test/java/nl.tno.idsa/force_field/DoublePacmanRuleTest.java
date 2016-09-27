package nl.tno.idsa.framework.test.force_field;

import nl.tno.idsa.framework.force_field.update_rules.DoublePacmanRule;
import nl.tno.idsa.framework.force_field.update_rules.PacmanRuleDistance;
import nl.tno.idsa.framework.force_field.update_rules.UpdateRules;
import nl.tno.idsa.framework.world.Point;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by alessandrozonta on 12/09/16.
 */
public class DoublePacmanRuleTest {
    @Test
    public void computeUpdateRule() throws Exception {
        //test same direction
        UpdateRules test = new DoublePacmanRule(40.0,1.0,-0.02, Boolean.FALSE);
        test.setPreviousPoint(new Point(0.0,0.0));
        test.computeUpdateRule(new Point(5.0,5.0),new Point(6.0,6.0));
        assertEquals(new Double(1.4918246976412703), test.getHowMuchIncreaseTheCharge());

        //test 1
        test.computeUpdateRule(new Point(5.0,5.0),new Point(5.0,6.0));
        assertEquals(new Double(1.3446199685238756), test.getHowMuchIncreaseTheCharge());

        //test 2
        test.computeUpdateRule(new Point(5.0,5.0),new Point(5.0,100.0));
        assertEquals(null, test.getHowMuchDecreaseTheCharge());
        assertEquals(null, test.getHowMuchIncreaseTheCharge());

        //test 3
        test.computeUpdateRule(new Point(5.0,5.0),new Point(-5.0,5.0));
        assertEquals(null, test.getHowMuchDecreaseTheCharge());
        assertEquals(null, test.getHowMuchIncreaseTheCharge());

        //test 4 opposite direction
        test.computeUpdateRule(new Point(5.0,5.0),new Point(-5.0,-5.0));
        assertEquals(new Double(24.532530197109352), test.getHowMuchDecreaseTheCharge());
    }

}