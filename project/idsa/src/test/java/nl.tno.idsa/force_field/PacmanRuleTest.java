package nl.tno.idsa.force_field;

import nl.tno.idsa.framework.force_field.update_rules.PacmanRule;
import nl.tno.idsa.framework.force_field.update_rules.UpdateRules;
import nl.tno.idsa.framework.potential_field.points_of_interest.POI;
import nl.tno.idsa.framework.world.Point;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by alessandrozonta on 12/09/16.
 */
public class PacmanRuleTest {
    @Test
    public void define_alpha() throws Exception {
        UpdateRules test0 = new PacmanRule(90.0,0.5,-0.001, Boolean.FALSE);
        Point a = new Point(0,0);
        Point b = new Point(1,1);
        Double direction = Math.toDegrees(Math.atan2(b.getY() - a.getY(), b.getX() - a.getX()));

        //Scenario 1
        System.out.println("First scenario");
        Point c = new Point(3,5);
        Double poi1 = Math.toDegrees(Math.atan2(c.getY() - b.getY(), c.getX() - b.getX()));
        Point d = new Point(5,4);
        Double poi2 = Math.toDegrees(Math.atan2(d.getY() - b.getY(), d.getX() - b.getX()));
        Point e = new Point(5,0);
        Double poi3 = Math.toDegrees(Math.atan2(e.getY() - b.getY(), e.getX() - b.getX()));
        Point f = new Point(-2,0);
        Double poi4 = Math.toDegrees(Math.atan2(f.getY() - b.getY(), f.getX() - b.getX()));
        Point g = new Point(-2,3);
        Double poi5 = Math.toDegrees(Math.atan2(g.getY() - b.getY(), g.getX() - b.getX()));
        Point h = new Point(7,1);
        Double poi6 = Math.toDegrees(Math.atan2(h.getY() - b.getY(), h.getX() - b.getX()));
        Point i = new Point(1,4);
        Double poi7 = Math.toDegrees(Math.atan2(i.getY() - b.getY(), i.getX() - b.getX()));
        Point l = new Point(-2,-2);
        Double poi8 = Math.toDegrees(Math.atan2(l.getY() - b.getY(), l.getX() - b.getX()));

        System.out.println("-----c");
        System.out.println(this.define_alpha(direction, poi1, 45.0));
        System.out.println(this.discriminate_increment_decrement(direction, poi1, 45.0));
        assertEquals(Boolean.TRUE, this.discriminate_increment_decrement(direction, poi1, 45.0));
        System.out.println("-----d");
        System.out.println(this.define_alpha(direction, poi2, 45.0));
        System.out.println(this.discriminate_increment_decrement(direction, poi2, 45.0));
        assertEquals(Boolean.TRUE, this.discriminate_increment_decrement(direction, poi2, 45.0));
        System.out.println("-----e");
        System.out.println(this.define_alpha(direction, poi3, 45.0));
        System.out.println(this.discriminate_increment_decrement(direction, poi3, 45.0));
        assertEquals(Boolean.FALSE, this.discriminate_increment_decrement(direction, poi3, 45.0));
        System.out.println("-----f");
        System.out.println(this.define_alpha(direction, poi4, 45.0));
        System.out.println(this.discriminate_increment_decrement(direction, poi4, 45.0));
        assertEquals(Boolean.FALSE, this.discriminate_increment_decrement(direction, poi4, 45.0));
        System.out.println("-----g");
        System.out.println(this.define_alpha(direction, poi5, 45.0));
        System.out.println(this.discriminate_increment_decrement(direction, poi5, 45.0));
        assertEquals(Boolean.FALSE, this.discriminate_increment_decrement(direction, poi5, 45.0));
        System.out.println("-----h");
        System.out.println(this.define_alpha(direction, poi6, 45.0));
        System.out.println(this.discriminate_increment_decrement(direction, poi6, 45.0));
        assertEquals(Boolean.FALSE, this.discriminate_increment_decrement(direction, poi6, 45.0));
        System.out.println("-----i");
        System.out.println(this.define_alpha(direction, poi7, 45.0));
        System.out.println(this.discriminate_increment_decrement(direction, poi7, 45.0));
        assertEquals(Boolean.TRUE, this.discriminate_increment_decrement(direction, poi7, 45.0));
        System.out.println("-----l");
        System.out.println(this.define_alpha(direction, poi8, 45.0));
        System.out.println(this.discriminate_increment_decrement(direction, poi8, 45.0));
        assertEquals(Boolean.FALSE, this.discriminate_increment_decrement(direction, poi8, 45.0));
        System.out.println("-----");
        System.out.println();
        System.out.println();
        System.out.println();

        //Scenario 2
        System.out.println("Second scenario");
        a = new Point(0,0);
        b = new Point(0,1);
        direction = Math.toDegrees(Math.atan2(b.getY() - a.getY(), b.getX() - a.getX()));
        c = new Point(-4,2);
        poi1 = Math.toDegrees(Math.atan2(c.getY() - b.getY(), c.getX() - b.getX()));
        d = new Point(-2,5);
        poi2 = Math.toDegrees(Math.atan2(d.getY() - b.getY(), d.getX() - b.getX()));
        e = new Point(2,5);
        poi3 = Math.toDegrees(Math.atan2(e.getY() - b.getY(), e.getX() - b.getX()));
        f = new Point(4,3);
        poi4 = Math.toDegrees(Math.atan2(f.getY() - b.getY(), f.getX() - b.getX()));
        g = new Point(3,-2);
        poi5 = Math.toDegrees(Math.atan2(g.getY() - b.getY(), g.getX() - b.getX()));
        h = new Point(-4,-2);
        poi6 = Math.toDegrees(Math.atan2(h.getY() - b.getY(), h.getX() - b.getX()));
        i = new Point(0,-3);
        poi7 = Math.toDegrees(Math.atan2(i.getY() - b.getY(), i.getX() - b.getX()));
        l = new Point(-5,6);
        poi8 = Math.toDegrees(Math.atan2(l.getY() - b.getY(), l.getX() - b.getX()));

        System.out.println("-----c");
        System.out.println(this.define_alpha(direction, poi1, 45.0));
        System.out.println(this.discriminate_increment_decrement(direction, poi1, 45.0));
        assertEquals(Boolean.FALSE, this.discriminate_increment_decrement(direction, poi1, 45.0));
        System.out.println("-----d");
        System.out.println(this.define_alpha(direction, poi2, 45.0));
        System.out.println(this.discriminate_increment_decrement(direction, poi2, 45.0));
        assertEquals(Boolean.TRUE, this.discriminate_increment_decrement(direction, poi2, 45.0));
        System.out.println("-----e");
        System.out.println(this.define_alpha(direction, poi3, 45.0));
        System.out.println(this.discriminate_increment_decrement(direction, poi3, 45.0));
        assertEquals(Boolean.TRUE, this.discriminate_increment_decrement(direction, poi3, 45.0));
        System.out.println("-----f");
        System.out.println(this.define_alpha(direction, poi4, 45.0));
        System.out.println(this.discriminate_increment_decrement(direction, poi4, 45.0));
        assertEquals(Boolean.FALSE, this.discriminate_increment_decrement(direction, poi4, 45.0));
        System.out.println("-----g");
        System.out.println(this.define_alpha(direction, poi5, 45.0));
        System.out.println(this.discriminate_increment_decrement(direction, poi5, 45.0));
        assertEquals(Boolean.FALSE, this.discriminate_increment_decrement(direction, poi5, 45.0));
        System.out.println("-----h");
        System.out.println(this.define_alpha(direction, poi6, 45.0));
        System.out.println(this.discriminate_increment_decrement(direction, poi6, 45.0));
        assertEquals(Boolean.FALSE, this.discriminate_increment_decrement(direction, poi6, 45.0));
        System.out.println("-----i");
        System.out.println(this.define_alpha(direction, poi7, 45.0));
        System.out.println(this.discriminate_increment_decrement(direction, poi7, 45.0));
        assertEquals(Boolean.FALSE, this.discriminate_increment_decrement(direction, poi7, 45.0));
        System.out.println("-----l");
        System.out.println(this.define_alpha(direction, poi8, 45.0));
        System.out.println(this.discriminate_increment_decrement(direction, poi8, 45.0));
        assertEquals(Boolean.TRUE, this.discriminate_increment_decrement(direction, poi8, 45.0));
        System.out.println("-----");
        System.out.println();
        System.out.println();
        System.out.println();

        //Scenario 3
        System.out.println("Third scenario");
        a = new Point(0,0);
        b = new Point(1,0);
        direction = Math.toDegrees(Math.atan2(b.getY() - a.getY(), b.getX() - a.getX()));
        c = new Point(-1,3);
        poi1 = Math.toDegrees(Math.atan2(c.getY() - b.getY(), c.getX() - b.getX()));
        d = new Point(5,2);
        poi2 = Math.toDegrees(Math.atan2(d.getY() - b.getY(), d.getX() - b.getX()));
        e = new Point(5,0);
        poi3 = Math.toDegrees(Math.atan2(e.getY() - b.getY(), e.getX() - b.getX()));
        f = new Point(5,-4);
        poi4 = Math.toDegrees(Math.atan2(f.getY() - b.getY(), f.getX() - b.getX()));
        g = new Point(6,-2);
        poi5 = Math.toDegrees(Math.atan2(g.getY() - b.getY(), g.getX() - b.getX()));
        h = new Point(1,-3);
        poi6 = Math.toDegrees(Math.atan2(h.getY() - b.getY(), h.getX() - b.getX()));
        i = new Point(-3,0);
        poi7 = Math.toDegrees(Math.atan2(i.getY() - b.getY(), i.getX() - b.getX()));
        l = new Point(0,0);
        poi8 = Math.toDegrees(Math.atan2(l.getY() - b.getY(), l.getX() - b.getX()));

        System.out.println("-----c");
        System.out.println(this.define_alpha(direction, poi1, 45.0));
        System.out.println(this.discriminate_increment_decrement(direction, poi1, 45.0));
        assertEquals(Boolean.FALSE, this.discriminate_increment_decrement(direction, poi1, 45.0));
        System.out.println("-----d");
        System.out.println(this.define_alpha(direction, poi2, 45.0));
        System.out.println(this.discriminate_increment_decrement(direction, poi2, 45.0));
        assertEquals(Boolean.TRUE, this.discriminate_increment_decrement(direction, poi2, 45.0));
        System.out.println("-----e");
        System.out.println(this.define_alpha(direction, poi3, 45.0));
        System.out.println(this.discriminate_increment_decrement(direction, poi3, 45.0));
        assertEquals(Boolean.TRUE, this.discriminate_increment_decrement(direction, poi3, 45.0));
        System.out.println("-----f");
        System.out.println(this.define_alpha(direction, poi4, 45.0));
        System.out.println(this.discriminate_increment_decrement(direction, poi4, 45.0));
        assertEquals(Boolean.TRUE, this.discriminate_increment_decrement(direction, poi4, 45.0));
        System.out.println("-----g");
        System.out.println(this.define_alpha(direction, poi5, 45.0));
        System.out.println(this.discriminate_increment_decrement(direction, poi5, 45.0));
        assertEquals(Boolean.TRUE, this.discriminate_increment_decrement(direction, poi5, 45.0));
        System.out.println("-----h");
        System.out.println(this.define_alpha(direction, poi6, 45.0));
        System.out.println(this.discriminate_increment_decrement(direction, poi6, 45.0));
        assertEquals(Boolean.FALSE, this.discriminate_increment_decrement(direction, poi6, 45.0));
        System.out.println("-----i");
        System.out.println(this.define_alpha(direction, poi7, 45.0));
        System.out.println(this.discriminate_increment_decrement(direction, poi7, 45.0));
        assertEquals(Boolean.FALSE, this.discriminate_increment_decrement(direction, poi7, 45.0));
        System.out.println("-----l");
        System.out.println(this.define_alpha(direction, poi8, 45.0));
        System.out.println(this.discriminate_increment_decrement(direction, poi8, 45.0));
        assertEquals(Boolean.FALSE, this.discriminate_increment_decrement(direction, poi8, 45.0));
        System.out.println("-----");
        System.out.println();
        System.out.println();
        System.out.println();

        //Scenario 4
        System.out.println("Fourth scenario");
        a = new Point(0,0);
        b = new Point(0,-1);
        direction = Math.toDegrees(Math.atan2(b.getY() - a.getY(), b.getX() - a.getX()));
        c = new Point(-4,-2);
        poi1 = Math.toDegrees(Math.atan2(c.getY() - b.getY(), c.getX() - b.getX()));
        d = new Point(-2,-5);
        poi2 = Math.toDegrees(Math.atan2(d.getY() - b.getY(), d.getX() - b.getX()));
        e = new Point(2,-5);
        poi3 = Math.toDegrees(Math.atan2(e.getY() - b.getY(), e.getX() - b.getX()));
        f = new Point(4,-3);
        poi4 = Math.toDegrees(Math.atan2(f.getY() - b.getY(), f.getX() - b.getX()));
        g = new Point(3,2);
        poi5 = Math.toDegrees(Math.atan2(g.getY() - b.getY(), g.getX() - b.getX()));
        h = new Point(-4,2);
        poi6 = Math.toDegrees(Math.atan2(h.getY() - b.getY(), h.getX() - b.getX()));
        i = new Point(0,3);
        poi7 = Math.toDegrees(Math.atan2(i.getY() - b.getY(), i.getX() - b.getX()));
        l = new Point(-5,-6);
        poi8 = Math.toDegrees(Math.atan2(l.getY() - b.getY(), l.getX() - b.getX()));

        System.out.println("-----c");
        System.out.println(this.define_alpha(direction, poi1, 45.0));
        System.out.println(this.discriminate_increment_decrement(direction, poi1, 45.0));
        assertEquals(Boolean.FALSE, this.discriminate_increment_decrement(direction, poi1, 45.0));
        System.out.println("-----d");
        System.out.println(this.define_alpha(direction, poi2, 45.0));
        System.out.println(this.discriminate_increment_decrement(direction, poi2, 45.0));
        assertEquals(Boolean.TRUE, this.discriminate_increment_decrement(direction, poi2, 45.0));
        System.out.println("-----e");
        System.out.println(this.define_alpha(direction, poi3, 45.0));
        System.out.println(this.discriminate_increment_decrement(direction, poi3, 45.0));
        assertEquals(Boolean.TRUE, this.discriminate_increment_decrement(direction, poi3, 45.0));
        System.out.println("-----f");
        System.out.println(this.define_alpha(direction, poi4, 45.0));
        System.out.println(this.discriminate_increment_decrement(direction, poi4, 45.0));
        assertEquals(Boolean.FALSE, this.discriminate_increment_decrement(direction, poi4, 45.0));
        System.out.println("-----g");
        System.out.println(this.define_alpha(direction, poi5, 45.0));
        System.out.println(this.discriminate_increment_decrement(direction, poi5, 45.0));
        assertEquals(Boolean.FALSE, this.discriminate_increment_decrement(direction, poi5, 45.0));
        System.out.println("-----h");
        System.out.println(this.define_alpha(direction, poi6, 45.0));
        System.out.println(this.discriminate_increment_decrement(direction, poi6, 45.0));
        assertEquals(Boolean.FALSE, this.discriminate_increment_decrement(direction, poi6, 45.0));
        System.out.println("-----i");
        System.out.println(this.define_alpha(direction, poi7, 45.0));
        System.out.println(this.discriminate_increment_decrement(direction, poi7, 45.0));
        assertEquals(Boolean.FALSE, this.discriminate_increment_decrement(direction, poi7, 45.0));
        System.out.println("-----l");
        System.out.println(this.define_alpha(direction, poi8, 45.0));
        System.out.println(this.discriminate_increment_decrement(direction, poi8, 45.0));
        assertEquals(Boolean.TRUE, this.discriminate_increment_decrement(direction, poi8, 45.0));
        System.out.println("-----");
        System.out.println();
        System.out.println();
        System.out.println();


        System.out.println("-----------------------------------");
        System.out.println("Data for graph");
        a = new Point(0,0);
        b = new Point(1,1);
        direction = Math.toDegrees(Math.atan2(b.getY() - a.getY(), b.getX() - a.getX()));
        c = new Point(5,5);
        poi1 = Math.toDegrees(Math.atan2(c.getY() - b.getY(), c.getX() - b.getX()));
        d = new Point(6,3);
        poi2 = Math.toDegrees(Math.atan2(d.getY() - b.getY(), d.getX() - b.getX()));
        e = new Point(8,1);
        poi3 = Math.toDegrees(Math.atan2(e.getY() - b.getY(), e.getX() - b.getX()));
        f = new Point(6,-2);
        poi4 = Math.toDegrees(Math.atan2(f.getY() - b.getY(), f.getX() - b.getX()));
        g = new Point(3,-5);
        poi5 = Math.toDegrees(Math.atan2(g.getY() - b.getY(), g.getX() - b.getX()));
        h = new Point(-2,-5);
        poi6 = Math.toDegrees(Math.atan2(h.getY() - b.getY(), h.getX() - b.getX()));
        i = new Point(-3,-3);
        poi7 = Math.toDegrees(Math.atan2(i.getY() - b.getY(), i.getX() - b.getX()));
        l = new Point(-5,-1);
        poi8 = Math.toDegrees(Math.atan2(l.getY() - b.getY(), l.getX() - b.getX()));
        Point m = new Point(-3,2);
        Double poi9 = Math.toDegrees(Math.atan2(m.getY() - b.getY(), m.getX() - b.getX()));
        Point n = new Point(-1,4);
        Double poi10 = Math.toDegrees(Math.atan2(n.getY() - b.getY(), n.getX() - b.getX()));
        Point o = new Point(1,6);
        Double poi11 = Math.toDegrees(Math.atan2(o.getY() - b.getY(), o.getX() - b.getX()));
        Point p = new Point(3,5);
        Double poi12 = Math.toDegrees(Math.atan2(p.getY() - b.getY(), p.getX() - b.getX()));
        Point q = new Point(6,-4);
        Double poi13 = Math.toDegrees(Math.atan2(q.getY() - b.getY(), q.getX() - b.getX()));
        Point r = new Point(2.5,1);
        Double poi14 = Math.toDegrees(Math.atan2(r.getY() - b.getY(), r.getX() - b.getX()));

        System.out.println("-----c");
        System.out.println(this.define_alpha(direction, poi1, 45.0));
        System.out.println(this.discriminate_increment_decrement(direction, poi1, 45.0));
        System.out.println("-----d");
        System.out.println(this.define_alpha(direction, poi2, 45.0));
        System.out.println(this.discriminate_increment_decrement(direction, poi2, 45.0));
        System.out.println("-----e");
        System.out.println(this.define_alpha(direction, poi3, 45.0));
        System.out.println(this.discriminate_increment_decrement(direction, poi3, 45.0));
        System.out.println("-----f");
        System.out.println(this.define_alpha(direction, poi4, 45.0));
        System.out.println(this.discriminate_increment_decrement(direction, poi4, 45.0));
        System.out.println("-----g");
        System.out.println(this.define_alpha(direction, poi5, 45.0));
        System.out.println(this.discriminate_increment_decrement(direction, poi5, 45.0));
        System.out.println("-----h");
        System.out.println(this.define_alpha(direction, poi6, 45.0));
        System.out.println(this.discriminate_increment_decrement(direction, poi6, 45.0));
        System.out.println("-----i");
        System.out.println(this.define_alpha(direction, poi7, 45.0));
        System.out.println(this.discriminate_increment_decrement(direction, poi7, 45.0));
        System.out.println("-----l");
        System.out.println(this.define_alpha(direction, poi8, 45.0));
        System.out.println(this.discriminate_increment_decrement(direction, poi8, 45.0));
        System.out.println("-----m");
        System.out.println(this.define_alpha(direction, poi9, 45.0));
        System.out.println(this.discriminate_increment_decrement(direction, poi9, 45.0));
        System.out.println("-----n");
        System.out.println(this.define_alpha(direction, poi10, 45.0));
        System.out.println(this.discriminate_increment_decrement(direction, poi10, 45.0));
        System.out.println("-----o");
        System.out.println(this.define_alpha(direction, poi11, 45.0));
        System.out.println(this.discriminate_increment_decrement(direction, poi11, 45.0));
        System.out.println("-----p");
        System.out.println(this.define_alpha(direction, poi12, 45.0));
        System.out.println(this.discriminate_increment_decrement(direction, poi12, 45.0));
        System.out.println("-----q");
        System.out.println(this.define_alpha(direction, poi13, 45.0));
        System.out.println(this.discriminate_increment_decrement(direction, poi13, 45.0));
        System.out.println("-----r");
        System.out.println(this.define_alpha(direction, poi14, 45.0));
        System.out.println(this.discriminate_increment_decrement(direction, poi14, 45.0));

        System.out.println("---------------");

        List<Point> points = new ArrayList<>();
        points.add(new Point(0,0));
        points.add(new Point(4,4));
        points.add(new Point(5,5));
        points.add(new Point(5,6));
        points.add(new Point(5,7));
        points.add(new Point(5,8));
        points.add(new Point(6,8));
        points.add(new Point(7,9));
        points.add(new Point(8,9));
        points.add(new Point(9,10));
        points.add(new Point(10,10));
        points.add(new Point(11,9));
        points.add(new Point(12,9));
        points.add(new Point(13,10));
        points.add(new Point(14,10));
        points.add(new Point(15,9));
        points.add(new Point(15,8));
        points.add(new Point(14,8));
        points.add(new Point(13,8));
        points.add(new Point(12,8));
        points.add(new Point(12,7));
        points.add(new Point(13,7));
        points.add(new Point(13,6));
        points.add(new Point(14,5));

        List<POI> POIs = new ArrayList<>();
        POIs.add(new POI(new Point(1,1)));
        POIs.add(new POI(new Point(2,10)));
        POIs.add(new POI(new Point(8,12)));
        POIs.add(new POI(new Point(18,10)));
        POIs.add(new POI(new Point(18,3)));
        POIs.add(new POI(new Point(9,4)));
        POIs.add(new POI(new Point(14,5)));




        for (int w=1; w<points.size(); w++){
            System.out.println(points.get(w));
            direction = Math.toDegrees(Math.atan2(points.get(w).getY() - points.get(w-1).getY(), points.get(w).getX() - points.get(w-1).getX()));
            System.out.println(w);
            if(w == 17){
                String s = "ds";
            }
            for (int ww=0; ww < POIs.size(); ww++){
                Double ppp = Math.toDegrees(Math.atan2(POIs.get(ww).getArea().getPolygon().getCenterPoint().getY() - points.get(w).getY(), POIs.get(ww).getArea().getPolygon().getCenterPoint().getX() - points.get(w).getX()));
                int www = ww + 1;
                System.out.println(www + " // " + this.discriminate_increment_decrement(direction, ppp, 45.0));
            }

        }

    }


    private Double define_alpha(Double angle, Double currentAngle, Double threshold){
        //first thing first -> everything is positive
        Double alpha = 0.0;
        //if the direction is negative, just change sign in both angles and rules defined will work
        if (angle < 0){
            angle = -angle;
            currentAngle = -currentAngle;
            System.out.println("-angle && -currentAngle");
        }

        Double upper_leg = angle + threshold;
        Double lower_leg = angle - threshold;
        Double opposite_angle = angle - 180;

        Double real_upper_leg = -(360 - upper_leg); //useful only if the upper_leg is > 180
        //base option
        if (currentAngle.equals(angle)) { //poi == angle
            alpha = threshold;
            System.out.println("poi == angle");
        }else if (currentAngle.equals(lower_leg)){// poi == leg POV
            alpha = 0.0;
            System.out.println("poi == leg POV");
        }else if (upper_leg <= 180 && currentAngle.equals(upper_leg)) { // poi == leg POV
            alpha = 0.0;
            System.out.println("poi == leg POV");
        }else if (upper_leg > 180 && currentAngle.equals(real_upper_leg)) { // poi == leg POV
            alpha = 0.0;
            System.out.println("poi == leg POV");
        } else if (currentAngle > upper_leg){ //180 > poi > upper_leg
            alpha = currentAngle - upper_leg;
            System.out.println("180 > poi > upper_leg");
        } else if (upper_leg <= 180 && currentAngle > angle && currentAngle < upper_leg) { //upper_leg > poi > angle
            alpha = upper_leg - currentAngle;
            System.out.println("upper_leg > poi > angle");
        } else if (upper_leg > 180  && currentAngle > real_upper_leg){ //upper_leg > poi > angle
            alpha = Math.abs(currentAngle) - Math.abs(real_upper_leg);
            System.out.println("upper_leg > poi > angle");
        } else if (currentAngle < angle && currentAngle > lower_leg){ //lower_leg < poi < angle
            alpha = threshold - (angle - currentAngle);
            System.out.println("lower_leg < poi < angle");
        } else if (currentAngle < lower_leg && currentAngle >= 0){ //lower_leg > poi > 0
            alpha = lower_leg - currentAngle;
            System.out.println("lower_leg > poi > 0");
        } else if (currentAngle > opposite_angle){ //0 < poi < -angle
            alpha = Math.abs(currentAngle) + lower_leg;
            System.out.println("0 < poi < -angle");
        } else if (currentAngle.equals(opposite_angle)){  //poi == -angle
            alpha = Math.abs(opposite_angle) + lower_leg;
            System.out.println("poi == -angle");
        } else if (currentAngle < opposite_angle){ //-180 > poi > -angle
            alpha = 360 - Math.abs(currentAngle) - upper_leg;
            System.out.println("-180 > poi > -angle");
        }
        return alpha;
    }


    private Boolean discriminate_increment_decrement(Double angle, Double currentAngle, Double threshold){
        Boolean inc = Boolean.FALSE;
        if (angle < 0){
            angle = -angle;
            currentAngle = -currentAngle;
        }
        if (currentAngle > 0 && angle + threshold <= 180 && currentAngle >= angle - threshold && currentAngle <= angle + threshold ){
            inc = Boolean.TRUE;
        } else if (currentAngle > 0 && angle + threshold > 180){
            Double real_upper_leg = -(360 - angle + threshold); //useful only if the upper_leg is > 180
            if(currentAngle > -180 && currentAngle < real_upper_leg) inc = Boolean.TRUE;
            if(currentAngle < 180 && currentAngle > angle - threshold) inc = Boolean.TRUE;
        } else if (currentAngle.equals(angle)){
            inc = Boolean.TRUE;
        } else if (angle - threshold < 0 && currentAngle <= angle + threshold && currentAngle >= angle - threshold){
            inc = Boolean.TRUE;
        } else if(currentAngle < 0 && angle + threshold > 180){
            //angle smaller than zero is missing
            Double tot = angle + threshold;
            Double realTot = tot - 360;
            if(currentAngle < realTot){
                inc = Boolean.TRUE;
            }
        }


        return inc;
    }



    @Test
    //test if it computes correctly the increase value
    public void computeUpdateRule() throws Exception {
//        UpdateRules test0 = new PacmanRule(120.0,0.5,-0.001, Boolean.FALSE);
//        test0.setPreviousPoint(new Point(0.0,0.0));
//        test0.computeUpdateRule(new Point(5.0,5.0), new Point(5.0,6.0));
//
//        UpdateRules test2 = new PacmanRule(60.0,0.5,-0.02, Boolean.FALSE);
//        test2.setPreviousPoint(new Point(0.0,0.0));
//        test2.computeUpdateRule(new Point(5.0,5.0), new Point(5.0,6.0));
//
//        UpdateRules test3 = new PacmanRule(120.0,0.1,-0.02, Boolean.FALSE);
//        test3.setPreviousPoint(new Point(0.0,0.0));
//        test3.computeUpdateRule(new Point(5.0,5.0), new Point(5.0,6.0));
//
//        UpdateRules test4 = new PacmanRule(30.0,0.5,-0.001, Boolean.FALSE);
//        test4.setPreviousPoint(new Point(0.0,0.0));
//        test4.computeUpdateRule(new Point(5.0,5.0), new Point(5.0,6.0));
//
//
//
//        Double res = test0.getHowMuchIncreaseTheCharge();
//        Double res2 = test2.getHowMuchIncreaseTheCharge();
//        Double res3 = test3.getHowMuchIncreaseTheCharge();
//        Double res4 = test4.getHowMuchIncreaseTheCharge();
//        Double reso = test0.getHowMuchDecreaseTheCharge();
//        Double res2o = test2.getHowMuchDecreaseTheCharge();
//        Double res3o = test3.getHowMuchDecreaseTheCharge();
//        Double res4o = test4.getHowMuchDecreaseTheCharge();
//
//
//        assertNull(res2);
//        assertNull(res4);
//        assertNull(reso);
//        assertNull(res3o);
//
//        assertNotEquals(res,res3);
//        assertNotEquals(res,res3);
//        assertNotEquals(res,res4);
//        assertNotEquals(res2,res3);
//        assertNotEquals(res3,res4);
//
//
//        //test same direction
//        UpdateRules test = new PacmanRule(40.0,1.0,-0.02, Boolean.FALSE);
//        test.setPreviousPoint(new Point(0.0,0.0));
//        test.computeUpdateRule(new Point(5.0,5.0),new Point(6.0,6.0));
//
//        assertEquals(new Double(1.4918246976412703), test.getHowMuchIncreaseTheCharge());
//        assertNull(test.getHowMuchDecreaseTheCharge());
//
//
//        //test 1
//        test.computeUpdateRule(new Point(5.0,5.0),new Point(5.0,6.0));
//        assertEquals(new Double(1.6487212707001282), test.getHowMuchDecreaseTheCharge());
//        assertNull(test.getHowMuchIncreaseTheCharge());
//
//        //test 2
//        test.computeUpdateRule(new Point(5.0,5.0),new Point(5.0,100.0));
//        assertEquals(new Double(1.6487212707001282), test.getHowMuchDecreaseTheCharge());
//        assertNull(test.getHowMuchIncreaseTheCharge());
//
//        //test 3
//        test.computeUpdateRule(new Point(5.0,5.0),new Point(-5.0,5.0));
//        assertEquals(new Double(9.974182454814724), test.getHowMuchDecreaseTheCharge());
//        assertNull(test.getHowMuchIncreaseTheCharge());
//
//        //test 4 opposite direction
//        test.computeUpdateRule(new Point(5.0,5.0),new Point(-5.0,-5.0));
//        assertEquals(new Double(24.532530197109352), test.getHowMuchDecreaseTheCharge());
//        assertNull(test.getHowMuchIncreaseTheCharge());
//
//        //test 5
//        test.computeUpdateRule(new Point(5.0,5.0),new Point(-5.0,-6.0));
//        assertEquals(new Double(23.230679100404053), test.getHowMuchDecreaseTheCharge());
//        assertNull(test.getHowMuchIncreaseTheCharge());
//
//        //test 6
//        test.computeUpdateRule(new Point(5.0,5.0),new Point(6.0,5.0));
//        assertEquals(new Double(1.6487212707001282), test.getHowMuchDecreaseTheCharge());
//        assertNull(test.getHowMuchIncreaseTheCharge());
//
//        //test 7
//        test.computeUpdateRule(new Point(5.0,5.0),new Point(5.0,0.0));
//        assertEquals(new Double(9.974182454814724), test.getHowMuchDecreaseTheCharge());
//        assertNull(test.getHowMuchIncreaseTheCharge());
//
//        //test 8
//        test.computeUpdateRule(new Point(5.0,5.0),new Point(5.0,-1.0));
//        assertEquals(new Double(9.974182454814724), test.getHowMuchDecreaseTheCharge());
//        assertNull(test.getHowMuchIncreaseTheCharge());
//
//        //test 9
//        test.computeUpdateRule(new Point(5.0,0.0),new Point(5.0,-1.0));
//        assertEquals(new Double(4.055199966844675), test.getHowMuchDecreaseTheCharge());
//        assertNull(test.getHowMuchIncreaseTheCharge());
//
//        //test 10
//        test.computeUpdateRule(new Point(5.0,0.0),new Point(5.0,-5.0));
//        assertEquals(new Double(4.055199966844675), test.getHowMuchDecreaseTheCharge());
//        assertNull(test.getHowMuchIncreaseTheCharge());
//
//        //test 11
//        test.computeUpdateRule(new Point(5.0,0.0),new Point(5.0,6.0));
//        assertEquals(new Double(4.055199966844675), test.getHowMuchDecreaseTheCharge());
//        assertNull(test.getHowMuchIncreaseTheCharge());
//
//        //test 12
//        test.computeUpdateRule(new Point(5.0,0.0),new Point(-5.0,0.0));
//        assertEquals(new Double(24.532530197109352), test.getHowMuchDecreaseTheCharge());
//        assertNull(test.getHowMuchIncreaseTheCharge());
//
//        //test 13
//        test.computeUpdateRule(new Point(5.0,0.0),new Point(-5.0,5.0));
//        assertEquals(new Double(14.421194668640851), test.getHowMuchDecreaseTheCharge());
//        assertNull(test.getHowMuchIncreaseTheCharge());
//
//        //test 14
//        test.computeUpdateRule(new Point(5.0,-5.0),new Point(5.0,0.0));
//        assertEquals(new Double(9.974182454814724), test.getHowMuchDecreaseTheCharge());
//        assertNull(test.getHowMuchIncreaseTheCharge());
//
//        //test 15
//        test.computeUpdateRule(new Point(0.0,-5.0),new Point(5.0,0.0));
//        assertEquals(new Double(9.974182454814724), test.getHowMuchDecreaseTheCharge());
//        assertNull(test.getHowMuchIncreaseTheCharge());
        Point StartingPoint = new Point(52.372702, 4.893080); //start in dam
        Point SecondPoint = new Point(52.373102, 4.893272);

        Point POI1 = new Point(52.379122, 4.900228); //central station -> same direction
        Point POI2 = new Point(52.374530, 4.883914); //wester church Anna Frank house -> left
        Point POI3 = new Point(52.365959, 4.916610); //artis -> right
        Point POI4 = new Point(52.359995, 4.885216); //Rijksmuseum -> opposit direction


        Point a = new Point(0,0);
        Point b = new Point(1,-1);
        Double direction = Math.toDegrees(Math.atan2(b.getY() - a.getY(), b.getX() - a.getX()));

        Point c = new Point(2,0);
        Double poi = Math.toDegrees(Math.atan2(c.getY() - b.getY(), c.getX() - b.getX()));

        String te = "";
    }


    @Test
    //test if it computes correctly the increase value
    public void testUpdate() throws Exception {
        Double s = 1.0;
        Double alpha = 0.0;
        Double w = 0.005;
        System.out.println(s * Math.exp(alpha * w));
        alpha = 20.0;
        System.out.println(s * Math.exp(alpha * w));
        alpha = 45.0;
        System.out.println(s * Math.exp(alpha * w));
        System.out.println("--------------------");
        alpha = 0.0;
        w = 0.01;
        System.out.println(s * Math.exp(alpha * w));
        alpha = 20.0;
        System.out.println(s * Math.exp(alpha * w));
        alpha = 45.0;
        System.out.println(s * Math.exp(alpha * w));
        System.out.println("--------------------");
        alpha = 0.0;
        w = 0.02;
        System.out.println(s * Math.exp(alpha * w));
        alpha = 20.0;
        System.out.println(s * Math.exp(alpha * w));
        alpha = 45.0;
        System.out.println(s * Math.exp(alpha * w));


//        Double angle = Math.toDegrees(Math.atan2(5.0 - 0.0, 0.0 - 0.0));
//        System.out.println(angle);
//        Double currentAngle = Math.toDegrees(Math.atan2(7.0 - 5.0, 1.0 - 0.0)); //y2-y1 & x2-x1
//        System.out.println(currentAngle);
//        Double threshold = 120.0/2;
//        Double alpha;
//        if (currentAngle.equals(angle)) {
//            alpha = threshold;
//            System.out.println("--1");
//        } else if (currentAngle > angle && currentAngle < angle + threshold) {
//            alpha = angle + threshold - currentAngle;
//            System.out.println("--2");
//        } else if (currentAngle > angle + threshold && currentAngle <= angle + 180) {
//            alpha = currentAngle - (angle + threshold);
//            System.out.println("--3");
//        } else if (currentAngle > angle - threshold && currentAngle < angle) {
//            if(currentAngle > 0){
//                alpha = threshold - (angle - currentAngle);
//                System.out.println("--4.0");
//            }else {
//                //alpha = Math.abs(Math.abs(currentAngle) - Math.abs(threshold));
//                alpha = threshold - (angle + Math.abs(currentAngle));
//                System.out.println("--4.1");
//            }
//        } else {
//            alpha = angle - threshold - currentAngle;
//            System.out.println("--5");
//        }
//        if (currentAngle > angle - threshold && currentAngle < angle + threshold) {
//            System.out.println("increase");
//        }else {
//            System.out.println("decrease");
//        }
//
//        System.out.println(alpha);

//        UpdateRules test0 = new PacmanRule(120.0,1.0,0.005, Boolean.FALSE);
//        test0.setPreviousPoint(new Point(0.0,0.0));
//        test0.computeUpdateRule(new Point(5.0,5.0), new Point(6.0,6.0));
    }


    @Test
    public void define_change_in_charge(Double angle, Double currentAngle, Double valueAlpha) throws Exception{
        
    }

}