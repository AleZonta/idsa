package nl.tno.idsa.force_field;

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
        UpdateRules test0 = new PacmanRule(120.0,0.5,-0.001, Boolean.FALSE);
        test0.setPreviousPoint(new Point(0.0,0.0));
        test0.computeUpdateRule(new Point(5.0,5.0), new Point(5.0,6.0));

        UpdateRules test2 = new PacmanRule(60.0,0.5,-0.02, Boolean.FALSE);
        test2.setPreviousPoint(new Point(0.0,0.0));
        test2.computeUpdateRule(new Point(5.0,5.0), new Point(5.0,6.0));

        UpdateRules test3 = new PacmanRule(120.0,0.1,-0.02, Boolean.FALSE);
        test3.setPreviousPoint(new Point(0.0,0.0));
        test3.computeUpdateRule(new Point(5.0,5.0), new Point(5.0,6.0));

        UpdateRules test4 = new PacmanRule(30.0,0.5,-0.001, Boolean.FALSE);
        test4.setPreviousPoint(new Point(0.0,0.0));
        test4.computeUpdateRule(new Point(5.0,5.0), new Point(5.0,6.0));



        Double res = test0.getHowMuchIncreaseTheCharge();
        Double res2 = test2.getHowMuchIncreaseTheCharge();
        Double res3 = test3.getHowMuchIncreaseTheCharge();
        Double res4 = test4.getHowMuchIncreaseTheCharge();
        Double reso = test0.getHowMuchDecreaseTheCharge();
        Double res2o = test2.getHowMuchDecreaseTheCharge();
        Double res3o = test3.getHowMuchDecreaseTheCharge();
        Double res4o = test4.getHowMuchDecreaseTheCharge();


        assertNull(res2);
        assertNull(res4);
        assertNull(reso);
        assertNull(res3o);

        assertNotEquals(res,res3);
        assertNotEquals(res,res3);
        assertNotEquals(res,res4);
        assertNotEquals(res2,res3);
        assertNotEquals(res3,res4);


        //test same direction
        UpdateRules test = new PacmanRule(40.0,1.0,-0.02, Boolean.FALSE);
        test.setPreviousPoint(new Point(0.0,0.0));
        test.computeUpdateRule(new Point(5.0,5.0),new Point(6.0,6.0));

        assertEquals(new Double(1.4918246976412703), test.getHowMuchIncreaseTheCharge());
        assertNull(test.getHowMuchDecreaseTheCharge());


        //test 1
        test.computeUpdateRule(new Point(5.0,5.0),new Point(5.0,6.0));
        assertEquals(new Double(1.6487212707001282), test.getHowMuchDecreaseTheCharge());
        assertNull(test.getHowMuchIncreaseTheCharge());

        //test 2
        test.computeUpdateRule(new Point(5.0,5.0),new Point(5.0,100.0));
        assertEquals(new Double(1.6487212707001282), test.getHowMuchDecreaseTheCharge());
        assertNull(test.getHowMuchIncreaseTheCharge());

        //test 3
        test.computeUpdateRule(new Point(5.0,5.0),new Point(-5.0,5.0));
        assertEquals(new Double(9.974182454814724), test.getHowMuchDecreaseTheCharge());
        assertNull(test.getHowMuchIncreaseTheCharge());

        //test 4 opposite direction
        test.computeUpdateRule(new Point(5.0,5.0),new Point(-5.0,-5.0));
        assertEquals(new Double(24.532530197109352), test.getHowMuchDecreaseTheCharge());
        assertNull(test.getHowMuchIncreaseTheCharge());

        //test 5
        test.computeUpdateRule(new Point(5.0,5.0),new Point(-5.0,-6.0));
        assertEquals(new Double(23.230679100404053), test.getHowMuchDecreaseTheCharge());
        assertNull(test.getHowMuchIncreaseTheCharge());

        //test 6
        test.computeUpdateRule(new Point(5.0,5.0),new Point(6.0,5.0));
        assertEquals(new Double(1.6487212707001282), test.getHowMuchDecreaseTheCharge());
        assertNull(test.getHowMuchIncreaseTheCharge());

        //test 7
        test.computeUpdateRule(new Point(5.0,5.0),new Point(5.0,0.0));
        assertEquals(new Double(9.974182454814724), test.getHowMuchDecreaseTheCharge());
        assertNull(test.getHowMuchIncreaseTheCharge());

        //test 8
        test.computeUpdateRule(new Point(5.0,5.0),new Point(5.0,-1.0));
        assertEquals(new Double(9.974182454814724), test.getHowMuchDecreaseTheCharge());
        assertNull(test.getHowMuchIncreaseTheCharge());

        //test 9
        test.computeUpdateRule(new Point(5.0,0.0),new Point(5.0,-1.0));
        assertEquals(new Double(4.055199966844675), test.getHowMuchDecreaseTheCharge());
        assertNull(test.getHowMuchIncreaseTheCharge());

        //test 10
        test.computeUpdateRule(new Point(5.0,0.0),new Point(5.0,-5.0));
        assertEquals(new Double(4.055199966844675), test.getHowMuchDecreaseTheCharge());
        assertNull(test.getHowMuchIncreaseTheCharge());

        //test 11
        test.computeUpdateRule(new Point(5.0,0.0),new Point(5.0,6.0));
        assertEquals(new Double(4.055199966844675), test.getHowMuchDecreaseTheCharge());
        assertNull(test.getHowMuchIncreaseTheCharge());

        //test 12
        test.computeUpdateRule(new Point(5.0,0.0),new Point(-5.0,0.0));
        assertEquals(new Double(24.532530197109352), test.getHowMuchDecreaseTheCharge());
        assertNull(test.getHowMuchIncreaseTheCharge());

        //test 13
        test.computeUpdateRule(new Point(5.0,0.0),new Point(-5.0,5.0));
        assertEquals(new Double(14.421194668640851), test.getHowMuchDecreaseTheCharge());
        assertNull(test.getHowMuchIncreaseTheCharge());

        //test 14
        test.computeUpdateRule(new Point(5.0,-5.0),new Point(5.0,0.0));
        assertEquals(new Double(9.974182454814724), test.getHowMuchDecreaseTheCharge());
        assertNull(test.getHowMuchIncreaseTheCharge());

        //test 15
        test.computeUpdateRule(new Point(0.0,-5.0),new Point(5.0,0.0));
        assertEquals(new Double(9.974182454814724), test.getHowMuchDecreaseTheCharge());
        assertNull(test.getHowMuchIncreaseTheCharge());


    }

}