package nl.tno.idsa.framework.test.force_field;

import nl.tno.idsa.framework.force_field.update_rules.PacmanRule;
import nl.tno.idsa.framework.force_field.update_rules.UpdateRules;
import nl.tno.idsa.framework.world.Point;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by alessandrozonta on 12/09/16.
 */
public class PacmanRuleTest {
    @Test
    //test if it computes correctly the increase value
    public void computeUpdateRule() throws Exception {
        //test same direction
        UpdateRules test = new PacmanRule(40.0,1.0,-0.02, Boolean.FALSE);
        test.setPreviousPoint(new Point(0.0,0.0));
        test.computeUpdateRule(new Point(5.0,5.0),new Point(6.0,6.0));

        assertEquals(new Double(1.4918246976412703), test.getHowMuchIncreaseTheCharge());

        //test 1
        test.computeUpdateRule(new Point(5.0,5.0),new Point(5.0,6.0));
        assertEquals(new Double(1.3446199685238756), test.getHowMuchIncreaseTheCharge());

        //test 2
        test.computeUpdateRule(new Point(5.0,5.0),new Point(5.0,100.0));
        assertEquals(new Double(1.5569859911515538), test.getHowMuchDecreaseTheCharge());

        //test 3
        test.computeUpdateRule(new Point(5.0,5.0),new Point(-5.0,5.0));
        assertEquals(new Double(4.055199966844675), test.getHowMuchDecreaseTheCharge());

        //test 4 opposite direction
        test.computeUpdateRule(new Point(5.0,5.0),new Point(-5.0,-5.0));
        assertEquals(new Double(24.532530197109352), test.getHowMuchDecreaseTheCharge());

        //test 5
        test.computeUpdateRule(new Point(5.0,5.0),new Point(-5.0,-6.0));
        assertEquals(new Double(22.11180042373877), test.getHowMuchDecreaseTheCharge());

        //test 6
        test.computeUpdateRule(new Point(5.0,5.0),new Point(6.0,5.0));
        assertEquals(new Double(1.48603488507638), test.getHowMuchIncreaseTheCharge());

        //test 7
        test.computeUpdateRule(new Point(5.0,5.0),new Point(5.0,0.0));
        assertEquals(new Double(1.6487212707001282), test.getHowMuchDecreaseTheCharge());

        //test 8
        test.computeUpdateRule(new Point(5.0,5.0),new Point(5.0,-1.0));
        assertEquals(new Double(2.067207472681574), test.getHowMuchDecreaseTheCharge());

        //test 9
        test.computeUpdateRule(new Point(5.0,0.0),new Point(5.0,-1.0));
        assertEquals(new Double(1.1898191853797635), test.getHowMuchIncreaseTheCharge());

        //test 10
        test.computeUpdateRule(new Point(5.0,0.0),new Point(5.0,-5.0));
        assertEquals(new Double(1.6487212707001282), test.getHowMuchDecreaseTheCharge());

        //test 11
        test.computeUpdateRule(new Point(5.0,0.0),new Point(5.0,6.0));
        assertEquals(new Double(1.8292180457925993), test.getHowMuchDecreaseTheCharge());

        //test 12
        test.computeUpdateRule(new Point(5.0,0.0),new Point(-5.0,0.0));
        assertEquals(new Double(24.532530197109352), test.getHowMuchDecreaseTheCharge());

        //test 13
        test.computeUpdateRule(new Point(5.0,0.0),new Point(-5.0,5.0));
        assertEquals(new Double(9.974182454814724), test.getHowMuchDecreaseTheCharge());

        //test 14
        test.computeUpdateRule(new Point(5.0,-5.0),new Point(5.0,0.0));
        assertEquals(new Double(1.6487212707001282), test.getHowMuchDecreaseTheCharge());

        //test 15
        test.computeUpdateRule(new Point(0.0,-5.0),new Point(5.0,0.0));
        assertEquals(new Double(4.055199966844675), test.getHowMuchDecreaseTheCharge());


    }

}