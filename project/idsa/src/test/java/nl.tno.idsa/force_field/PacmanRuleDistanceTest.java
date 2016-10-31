package nl.tno.idsa.force_field;

import nl.tno.idsa.framework.force_field.update_rules.PacmanRuleDistance;
import nl.tno.idsa.framework.force_field.update_rules.UpdateRules;
import nl.tno.idsa.framework.world.Point;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by alessandrozonta on 12/09/16.
 */
public class PacmanRuleDistanceTest {
    @Test
    public void computeUpdateRule() throws Exception {
        //test same direction
        UpdateRules test = new PacmanRuleDistance(40.0,1.0,-0.02,0.05,0.6, Boolean.FALSE);
        test.setPreviousPoint(new Point(0.0,0.0));
        test.computeUpdateRule(new Point(5.0,5.0),new Point(6.0,6.0));
        assertEquals(new Double(7.035914377897558), test.getHowMuchIncreaseTheCharge());
        assertNull(test.getHowMuchDecreaseTheCharge());

        //test 1
        test.computeUpdateRule(new Point(5.0,5.0),new Point(5.0,6.0));
        assertEquals(new Double(7.4755337966399855), test.getHowMuchDecreaseTheCharge());
        assertNull(test.getHowMuchIncreaseTheCharge());

        //test 2
        test.computeUpdateRule(new Point(5.0,5.0),new Point(5.0,100.0));
        assertEquals(new Double(2.9096910586606346), test.getHowMuchDecreaseTheCharge());
        assertNull(test.getHowMuchIncreaseTheCharge());

        //test 3
        test.computeUpdateRule(new Point(5.0,5.0),new Point(-5.0,5.0));
        assertEquals(new Double(16.159172349034556), test.getHowMuchDecreaseTheCharge());
        assertNull(test.getHowMuchIncreaseTheCharge());

        //test 4 opposite direction
        test.computeUpdateRule(new Point(5.0,5.0),new Point(-5.0,-5.0));
        assertEquals(new Double(30.717520091329185), test.getHowMuchDecreaseTheCharge());
        assertNull(test.getHowMuchIncreaseTheCharge());


    }

}