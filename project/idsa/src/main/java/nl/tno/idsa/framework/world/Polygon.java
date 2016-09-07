package nl.tno.idsa.framework.world;

import java.awt.geom.Line2D;

public class Polygon implements IGeometry {

    private final Point[] points;
    private final double xMin;
    private final double yMin;
    private final double xMax;
    private final double yMax;
    private final Point centroid;

    public Polygon(Point[] points) {
        this.points = points;
        double xMin = Double.POSITIVE_INFINITY;
        double yMin = Double.POSITIVE_INFINITY;
        double xMax = Double.NEGATIVE_INFINITY;
        double yMax = Double.NEGATIVE_INFINITY;
        double sumX = 0, sumY = 0;
        for (int i = 0; i < points.length; ++i) {
            xMin = Math.min(xMin, points[i].getX());
            yMin = Math.min(yMin, points[i].getY());
            xMax = Math.max(xMax, points[i].getX());
            yMax = Math.max(yMax, points[i].getY());
            sumX += points[i].getX();
            sumY += points[i].getY();
        }
        this.centroid = new Point(sumX / points.length, sumY / points.length);
        this.xMin = xMin;
        this.yMin = yMin;
        this.xMax = xMax;
        this.yMax = yMax;
    }

    public Polygon(Point central){
        this.centroid = central;
        this.xMin = Double.POSITIVE_INFINITY;
        this.yMin = Double.POSITIVE_INFINITY;
        this.xMax = Double.NEGATIVE_INFINITY;
        this.yMax = Double.NEGATIVE_INFINITY;
        this.points = new Point[0];
    }

    @Override
    public Point[] getPoints() {
        return points;
    }

    public int size() {
        return points.length;
    }

    public Point get(int i) {
        return points[i];
    }

    @Override
    public GeometryType getGeometryType() {
        return GeometryType.POLYGON;
    }

    @Override
    public Point getFirstPoint() {
        return getPoint();
    }

    @Override
    public Point getCenterPoint() {
        return getPoint();
    }

    @Override
    public Point getLastPoint() {
        return getPoint();
    }

    private Point getPoint() {
        return centroid;
    }

    @Override
    public boolean contains(Point point) {
        return contains(point.getX(), point.getY());
    }

    private boolean contains(double x, double y) {
        boolean result = false;
        // Bounding box check
        if (x >= this.xMin && x <= this.xMax && y >= this.yMin && y <= this.yMax) {
            int i, j = points.length - 1;
            for (i = 0; i < points.length; ++i) {
                if (points[i].getY() < y && points[j].getY() >= y || points[j].getY() < y && points[i].getY() >= y) {
                    if (points[i].getX() + (y - points[i].getY()) / (points[j].getY() - points[i].getY()) * (points[j].getX() - points[i].getX()) < x) {
                        result = !result;
                    }
                }
                j = i;
            }
        }
        return result;
    }

    public double getSurface() {
        double area = 0.0;
        for (int i = 0; i < points.length; ++i) {
            int j = (i + 1) % points.length;
            
            area += points[i].getX() * points[j].getY();
            area -= points[j].getX() * points[i].getY();
        }
        area *= 0.5;
        return Math.abs(area);
    }

    @Override
    public Polygon translate(Point pointRelativeToOrigin) {
        Point[] newPoints = new Point[points.length];
        for (int p = 0; p < points.length; p++) {
            newPoints[p] = (Point) points[p].translate(pointRelativeToOrigin);
        }
        return new Polygon(newPoints);
    }

    @Override
    public String toString() {
        if (points.length == 0) {
            return "()";
        }
        String ret = "(";
        for (Point p : points) {
            ret += p.toString() + ", ";
        }
        return ret + points[0] + ")";
    }

    //return the closest point to a selected point in the map
    public Point getClosestPoint(Point currentPosition){
        //How can i solve this?
        //One segment is the one from current position to the center of the area (this.centroid) -> external_segment
        //The other segments are the ones from the polygon.
        //first point to second point against external_segment
        //second point to third point agains external_segment
        //and so on
        //First returning true is the one that I need
        Boolean result = Boolean.FALSE;
        int i = 0;
        while(i < points.length - 1 && !result){
            //second vector is point[i-1] with points[i]
            i++;
            result = this.linesIntersect(currentPosition,this.centroid,points[i-1],points[i]);
        }
        //now two results are possible.
        //loops ends because result became true -> found two segment that intersect
        //loops ends because I check all the segments and no one intersect my segment. This means the segment is inside the polygon and I return the centroid
        if(!result){
            return this.centroid;
        }else{
            //now I need to find the point where the two segment intersect
            return this.lineIntersect(currentPosition,this.centroid,points[i-1],points[i]);
        }
    }

    //return if the two segments startPointAendPointA and startPointBendPointB intersect
    private Boolean linesIntersect(Point startPointA, Point endPointA, Point startPointB, Point endPointB){
        //I am using Java's 2D API to tests if the line segment from (x1,y1) to (x2,y2) intersects the line segment from (x3,y3) to (x4,y4).
        //see http://docs.oracle.com/javase/6/docs/api/java/awt/geom/Line2D.html#linesIntersect%28double,%20double,%20double,%20double,%20double,%20double,%20double,%20double%29
        Line2D line1 = new Line2D.Double(startPointA.getX(), startPointA.getY(), endPointA.getX(), endPointA.getY());
        Line2D line2 = new Line2D.Double(startPointB.getX(), startPointB.getY(), endPointB.getX(), endPointB.getY());
        return line2.intersectsLine(line1);
    }

    //return point where two line intersect
    private Point lineIntersect(Point startPointA, Point endPointA, Point startPointB, Point endPointB){
        Double denominator = (endPointB.getY() - startPointB.getY()) * (endPointA.getX() - startPointA.getX()) - (endPointB.getX() - startPointB.getX()) * (endPointA.getY() - startPointA.getY());
        Double Ua = ((endPointB.getX() - startPointB.getX()) * (startPointA.getY() - startPointB.getY()) - (endPointB.getY() - startPointB.getY()) * (startPointA.getX() - startPointB.getX())) / denominator;
        //Double Ub = ((endPointA.getX() - startPointA.getX()) * (startPointA.getY() - startPointB.getY()) - (endPointA.getY() - startPointA.getY()) * (startPointA.getX() - startPointB.getX())) / denominator;
        return new Point(startPointA.getX() + Ua * (endPointA.getX() - startPointA.getX()), startPointA.getY() + Ua * (endPointA.getY() - startPointA.getY()));
    }
}
