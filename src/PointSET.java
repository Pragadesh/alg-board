import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

public class PointSET {

    private Set<Point2D> points;

    public PointSET() {
        // construct an empty set of points
        points = new TreeSet<>();
    }

    public boolean isEmpty() {
        // is the set empty?
        return points.isEmpty();
    }

    public int size() {
        // number of points in the set
        return points.size();
    }

    public void insert(Point2D p) {
        // add the point to the set (if it is not already in the set)
        if (p == null) {
            throw new IllegalArgumentException("Empty point to insert");
        }
        points.add(p);
    }

    public boolean contains(Point2D p) {
        // does the set contain point p?
        if (p == null) {
            throw new IllegalArgumentException("Empty point to find");
        }
        return points.contains(p);
    }

    public void draw() {
        // draw all points to standard draw
        StdDraw.setScale();
        for (Point2D point : points) {
            StdDraw.point(point.x(), point.y());
        }

    }

    public Iterable<Point2D> range(RectHV rect) {
        // all points that are inside the rectangle (or on the boundary)
        if (rect == null) {
            throw new IllegalArgumentException();
        }
        List<Point2D> pointsInRange = new ArrayList<>();
        for (Point2D point : points) {
            if (rect.contains(point)) {
                pointsInRange.add(point);
            }
        }
        return pointsInRange;
    }

    public Point2D nearest(Point2D p) {
        // a nearest neighbor in the set to point p; null if the set is empty
        if (p == null) {
            throw new IllegalArgumentException();
        }
        Point2D nearestPoint = null;
        for (Point2D point : points) {
            if (nearestPoint == null || p.distanceToOrder().compare(point, nearestPoint) < 0) {
                nearestPoint = point;
            }
        }
        return nearestPoint;
    }

    public static void main(String[] args) {

    }
}
