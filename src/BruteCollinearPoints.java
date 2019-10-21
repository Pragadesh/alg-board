import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class BruteCollinearPoints {

    private final List<LineSegment> lineSegments;

    // finds all line segments containing 4 points
    public BruteCollinearPoints(Point[] points) {
        lineSegments = new ArrayList<>();
        if (points == null) {
            throw new IllegalArgumentException();
        }
        preEvaluate(points);
        if (points.length < 4) {
            return;
        }
        findCollinearPoints(points);
    }

    private void findCollinearPoints(Point[] points) {
        for (int i = 0; i < points.length - 3; i++) {
            Point firstPoint = points[i];
            boolean matched = false;
            for (int j = i + 1; j < points.length - 2; j++) {
                double slope = firstPoint.slopeTo(points[j]);
                if (matched) {
                    matched = false;
                    continue;
                }
                if (slope == Double.NEGATIVE_INFINITY) {
                    continue;
                }
                for (int k = j + 1; k < points.length - 1; k++) {
                    if (matched) {
                        break;
                    }

                    if (Double.compare(slope, firstPoint.slopeTo(points[k])) != 0) {
                        continue;
                    }
                    for (int m = k + 1; m < points.length; m++) {
                        if (Double.compare(slope, firstPoint.slopeTo(points[m])) == 0) {
                            lineSegments.add(createLineSegment(firstPoint, points[j], points[k], points[m]));
                            matched = true;
                            break;
                        }
                    }
                }
            }
        }
    }

    private void preEvaluate(Point[] points) {
        Point[] toCheckDuplicates = new Point[points.length];
        for (int i = 0; i < points.length; i++) {
            if (points[i] == null)
                throw new IllegalArgumentException();
            toCheckDuplicates[i] = points[i];
        }
        Arrays.sort(toCheckDuplicates);
        if (toCheckDuplicates.length > 1) {
            Point prevPoint = toCheckDuplicates[0];
            for (int i = 1; i < toCheckDuplicates.length; i++) {
                if (prevPoint.compareTo(toCheckDuplicates[i]) == 0) {
                    throw new IllegalArgumentException();
                }
                prevPoint = toCheckDuplicates[i];
            }
        }
    }

    private LineSegment createLineSegment(Point firstPoint, Point secondPoint, Point thirdPoint, Point fourthPoint) {
        Point[] points = { firstPoint, secondPoint, thirdPoint, fourthPoint };
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
