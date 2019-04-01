import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class FastCollinearPoints {

    private final List<LineSegment> lineSegments;

    // finds all line segments containing 4 or more points
    public FastCollinearPoints(Point[] points) {
        lineSegments = new ArrayList<>();
        if (points == null) {
            throw new IllegalArgumentException();
        }
        Point[] pointsCopy = evaluateAndGetCopy(points);
        if (points.length < 4) {
            return;
        }
        findCollinearPoints(points, pointsCopy);
    }

    private Point[] evaluateAndGetCopy(Point[] points) {
        Point[] pointsCopy = new Point[points.length];
        for (int i = 0; i < points.length; i++) {
            if (points[i] == null)
                throw new IllegalArgumentException();
            pointsCopy[i] = points[i];
        }
        Arrays.sort(pointsCopy);
        if (pointsCopy.length > 1) {
            Point prevPoint = pointsCopy[0];
            for (int i = 1; i < pointsCopy.length; i++) {
                if (prevPoint.compareTo(pointsCopy[i]) == 0) {
                    throw new IllegalArgumentException();
                }
                prevPoint = pointsCopy[i];
            }
        }
        return pointsCopy;
    }

    private void findCollinearPoints(Point[] points, Point[] pointsCopy) {
        int offsetLength = 1;
        Arrays.sort(points);
        for (int i = 0; i < points.length - 3; i += offsetLength) {
            Point pivot = points[i];
            Arrays.sort(pointsCopy, pivot.slopeOrder());

            double currentSlope = pivot.slopeTo(pointsCopy[0]);
            int currentLength = 1;
            for (int j = 1; j < pointsCopy.length; j++) {
                double calculatedSlope = pivot.slopeTo(pointsCopy[j]);
                if (Double.compare(calculatedSlope, currentSlope) == 0) {
                    currentLength++;
                } else {
                    currentSlope = calculatedSlope;
                    if (currentLength >= 3) {
                        createLineSegment(pivot, pointsCopy, j - currentLength, j - 1);
                    }
                    currentLength = 1;
                }
            }
            if (currentLength >= 3) {
                createLineSegment(pivot, pointsCopy, pointsCopy.length - currentLength, pointsCopy.length - 1);
            }
        }
    }

    private void createLineSegment(Point pivot, Point[] points, int start, int end) {
        Point[] segment = new Point[end - start + 2];
        System.arraycopy(points, start, segment, 0, end - start + 1);
        segment[segment.length - 1] = pivot;
        Arrays.sort(segment);
        if (pivot.compareTo(segment[0]) == 0) {
            lineSegments.add(new LineSegment(segment[0], segment[segment.length - 1]));
        }
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
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
