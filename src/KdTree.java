import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

public class KdTree {
    private Node root;
    private int maxLevel;

    public KdTree() {
        // construct an empty set of points
        maxLevel = 2;
    }

    public boolean isEmpty() {
        // is the set empty?
        return (size() == 0);
    }

    private int size(Node node) {
        if (node != null) {
            return node.size;
        }
        return 0;
    }

    public int size() {
        // number of points in the set
        return size(root);
    }

    public void insert(Point2D p) {
        // add the point to the set (if it is not already in the set)
        if (p == null) {
            throw new IllegalArgumentException("Empty point to insert");
        }
        root = insert(root, p, -1);
    }

    private Node insert(Node node, Point2D point, int level) {
        if (node == null) {
            int currLevel = getNextLevel(level);
            return new Node(point, currLevel);
        }
        int compareValue = getComparator(node.level).compare(point, node.key);
        if (compareValue < 0) {
            node.left = insert(node.left, point, node.level);
        } else if (compareValue > 0) {
            node.right = insert(node.right, point, node.level);
        } else {
            node.addPoint(point);
        }
        node.size = size(node.left) + size(node.right) + node.values.size();
        return node;
    }

    private Comparator<Point2D> getComparator(int level) {
        if (level == 0) {
            return Point2D.X_ORDER;
        }
        return Point2D.Y_ORDER;
    }

    private int getNextLevel(int level) {
        if (level < 0) {
            return 0;
        }
        return (++level) % maxLevel;
    }

    public boolean contains(Point2D p) {
        // does the set contain point p?
        if (p == null) {
            throw new IllegalArgumentException("Empty point to find");
        }
        return contains(root, p);
    }

    private boolean contains(Node node, Point2D point) {
        if (node == null) {
            return false;
        }
        int compareValue = getComparator(node.level).compare(point, node.key);
        if (compareValue < 0) {
            return contains(node.left, point);
        } else if (compareValue > 0) {
            return contains(node.right, point);
        } else {
            for (Point2D p : node.values) {
                if (p.equals(point)) {
                    return true;
                }
            }
            return false;
        }
    }

    public void draw() {
        // draw all points to standard draw
        List<Point2D> points = new ArrayList<>();
        StdDraw.setScale();
        inOrder(root, points);
        for (Point2D point : points) {
            StdDraw.point(point.x(), point.y());
        }

    }

    private void inOrder(Node node, List<Point2D> points) {
        if (node == null) {
            return;
        }
        inOrder(node.left, points);
        for (Point2D p : node.values) {
            points.add(p);
        }
        inOrder(node.right, points);
    }

    public Iterable<Point2D> range(RectHV rect) {
        // all points that are inside the rectangle (or on the boundary)
        if (rect == null) {
            throw new IllegalArgumentException();
        }
        List<Point2D> pointsInRange = new ArrayList<>();
        range(rect, root, pointsInRange);
        return pointsInRange;
    }

    private void range(RectHV rect, Node node, List<Point2D> pointsInRange) {
        if (node == null) {
            return;
        }
        double lower = rect.xmin();
        double upper = rect.xmax();
        double value = node.key.x();
        if (node.level == 1) {
            lower = rect.ymin();
            upper = rect.ymax();
            value = node.key.y();
        }
        if (Double.compare(value, lower) < 0) {
            range(rect, node.right, pointsInRange);
        } else if (Double.compare(value, upper) > 0) {
            range(rect, node.left, pointsInRange);
        } else {
            range(rect, node.left, pointsInRange);
            for (Point2D p : node.values) {
                if (rect.contains(p)) {
                    pointsInRange.add(p);
                }
            }
            range(rect, node.right, pointsInRange);
        }
    }

    public Point2D nearest(Point2D p) {
        // a nearest neighbor in the set to point p; null if the set is empty
        if (p == null) {
            throw new IllegalArgumentException();
        }
        return nearest(p, root, null);
    }

    private Point2D nearest(Point2D p, Node node, Point2D nearest) {
        if (node == null) {
            return nearest;
        }
        System.out.println("Searching: "+node.key);
        Comparator<Point2D> comparator = p.distanceToOrder();
        int compare = node.level == 0 ? Double.compare(p.x(), node.key.x()) : Double.compare(p.y(), node.key.y());
        if (compare < 0) {
            Point2D leftNearest = nearest(p, node.left, nearest);
            if (nearest == null || comparator.compare(leftNearest, nearest) < 0) {
                nearest = leftNearest;
            }
        } else {
            Point2D rightNearest = nearest(p, node.right, nearest);
            if (nearest == null || comparator.compare(rightNearest, nearest) < 0) {
                nearest = rightNearest;
            }
        }
        if (nearest == null) {
            nearest = node.key;
        }
        double axisDistance = node.level == 0 ? Math.abs(node.key.x() - p.x()) : Math.abs(node.key.y() - p.y());
        if(Double.compare(p.distanceTo(nearest), axisDistance) >= 0) {
            nearest = findNearestPoint(node, p, nearest);
        }
        if (Double.compare(p.distanceTo(nearest), axisDistance) > 0) {
            if (compare < 0) {
                nearest = nearest(p, node.right, nearest);
            } else {
                nearest = nearest(p, node.left, nearest);
            }
        }
        return nearest;
    }

    private Point2D findNearestPoint(Node node, Point2D p, Point2D nearest) {
        Comparator<Point2D> comparator = p.distanceToOrder();
        for (Point2D point : node.values) {
            if (nearest == null || comparator.compare(point, nearest) < 0) {
                nearest = point;
            }
        }
        return nearest;
    }

    private static class Node {
        private Point2D key;
        private List<Point2D> values;
        private int size;
        private int level;
        private Node left;
        private Node right;

        public Node(Point2D key, int level) {
            this.key = key;
            this.values = new ArrayList<>();
            this.values.add(key);
            this.level = level;
            this.size = 1;
        }

        public void addPoint(Point2D key) {
            for (Point2D p : this.values) {
                if (p.equals(key)) {
                    return;
                }
            }
            this.values.add(key);
        }

    }

    public static void main(String[] args) {
        KdTree tree = new KdTree();
        // tree.insert(new Point2D(0.25, 0.625));
        // tree.insert(new Point2D(0.875, 0.375));
        // tree.insert(new Point2D(0.5, 0.375));
        // tree.insert(new Point2D(0, 0));
        // tree.insert(new Point2D(0, 0));

        tree.insert(new Point2D(0.25, 0.0));
        tree.insert(new Point2D(1.0, 0.0));
        tree.insert(new Point2D(1.0, 0.25));
        tree.insert(new Point2D(0.25, 0.25));
        tree.insert(new Point2D(0.25, 0.5));
        tree.insert(new Point2D(0.25, 1.0));
        tree.insert(new Point2D(0.75, 0.75)); // should return
        tree.insert(new Point2D(1.0, 0.5));
        tree.insert(new Point2D(0.5, 0.0));
        tree.insert(new Point2D(1.0, 0.75));

        System.out.println("Size: " + tree.range(new RectHV(0.5, 0.25, 0.75, 1.0)));

        // PointSET pointSet = new PointSET();
        // addPoint(tree, pointSet, new Point2D(1, 1));
        // addPoint(tree, pointSet, new Point2D(2, 3));
        // addPoint(tree, pointSet, new Point2D(0, 1));
        // addPoint(tree, pointSet, new Point2D(0, 5));
        // addPoint(tree, pointSet, new Point2D(1, 3));
        // RectHV rect = new RectHV(0, 0, 2, 2);
        // System.out.println("KD Tree nearest: " + tree.nearest(new Point2D(0,
        // 0)));
        // System.out.println("pointSet nearest: " + pointSet.nearest(new
        // Point2D(0, 0)));
        //
        // System.out.println("KD Tree range: " + tree.range(rect));
        // System.out.println("pointSet range: " + pointSet.range(rect));
    }

    private static void addPoint(KdTree tree, PointSET pointSet, Point2D point) {
        tree.insert(point);
        pointSet.insert(point);
    }
}
