import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class BruteCollinearPoints {

    private List<LineSegment> lineSegments;

    // finds all line segments containing 4 points
    public BruteCollinearPoints(Point[] points) {
        if (points == null) {
            throw new IllegalArgumentException();
        }
        lineSegments = new ArrayList<>();
        findCollinearPoints(points);

    }

    private void findCollinearPoints(Point[] points) {
        preEvaluate(points);
        for (int i = 0; i < points.length - 3; i++) {
            Point first = points[i];
            boolean matched = false;
            for (int j = i + 1; j < points.length - 2; j++) {
                double slope = first.slopeTo(points[j]);
                if (matched) {
                    break;
                }
                if (slope == Double.NEGATIVE_INFINITY) {
                    continue;
                }
                for (int k = j + 1; k < points.length - 1; k++) {
                    if (matched) {
                        break;
                    }
                    if (slope != first.slopeTo(points[k])) {
                        continue;
                    }
                    for (int l = k + 1; l < points.length; l++) {
                        if (slope == first.slopeTo(points[l])) {
                            lineSegments.add(createLineSegment(first, points[j], points[k], points[l]));
                            matched = true;
                            break;
                        }
                    }
                }
            }
        }
    }

    private void preEvaluate(Point[] points) {
        if (points.length < 4) {
            throw new IllegalArgumentException();
        }
        for (Point point : points) {
            if (point == null)
                throw new IllegalArgumentException();
        }
    }

    private LineSegment createLineSegment(Point one, Point two, Point three, Point four) {
        Point[] points = { one, two, three, four };
        Arrays.sort(points);
        return new LineSegment(points[0], points[3]);
    }

    // the number of line segments
    public int numberOfSegments() {
        return lineSegments.size();
    }

    // the line segments
    public LineSegment[] segments() {
        LineSegment[] segments = new LineSegment[lineSegments.size()];
        return lineSegments.toArray(segments);
    }

    public static void main(String[] args) {

        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
