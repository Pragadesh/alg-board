import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class FastCollinearPoints {

    private List<LineSegment> lineSegments;

    // finds all line segments containing 4 or more points
    public FastCollinearPoints(Point[] points) {
        if (points == null || points.length < 4) {
            throw new IllegalArgumentException();
        }
        lineSegments = new ArrayList<>();
        findCollinearPoints(points);
    }

    private void findCollinearPoints(Point[] points) {
        int offsetLength = 1;
        for (int i = 0; i < points.length - 3; i += offsetLength) {
            Point pivot = points[i];
            PointScore[] scores = new PointScore[points.length - i - 1];
            for (int j = i + 1; j < points.length; j++) {
                scores[j - i - 1] = new PointScore(pivot.slopeTo(points[j]), j);
            }
            Arrays.sort(scores);
            offsetLength = findMaxCollinearPoints(points, scores, i + 1);
            if (offsetLength >= 3) {
                offsetLength++;
                Arrays.sort(points, i, i + offsetLength);
                lineSegments.add(new LineSegment(points[i], points[i + offsetLength - 1]));
            }
        }
    }

    private int findMaxCollinearPoints(Point[] points, PointScore[] sortedScores, int startIdx) {
        int maxStartIdx = 0;
        double currentScore = sortedScores[0].slope;
        int maxLength = 1;
        int currentStartIdx = 0;
        for (int i = 1; i < sortedScores.length; i++) {
            if (sortedScores[i].slope != currentScore) {
                if (i - currentStartIdx > maxLength) {
                    maxLength = (i - currentStartIdx);
                    maxStartIdx = currentStartIdx;
                }
                currentStartIdx = i;
                currentScore = sortedScores[i].slope;
            }
        }
        if (sortedScores.length - currentStartIdx > maxLength) {
            maxLength = (sortedScores.length - currentStartIdx);
            maxStartIdx = currentStartIdx;
        }
        if (maxLength >= 3) {
            rearrangePoints(points, sortedScores, startIdx, maxStartIdx, maxLength);
            return maxLength;
        }
        return 1;
    }

    private void rearrangePoints(Point[] points, PointScore[] sortedScores, int startIdx, int matchedStartIdx, int length) {
        int[] indexes = new int[length];
        for (int i = 0; i < length; i++) {
            indexes[i] = sortedScores[matchedStartIdx++].index;
        }
        Arrays.sort(indexes);
        for (int index : indexes) {
            exchangeIndex(points, startIdx++, index);
        }
    }

    private void exchangeIndex(Point[] points, int idx1, int idx2) {
        if (idx1 != idx2) {
            Point temp = points[idx1];
            points[idx1] = points[idx2];
            points[idx2] = temp;
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

    private static class PointScore implements Comparable<PointScore> {
        private double slope;
        private int index;
        private static final String FMT = "[%d, %f]";

        public PointScore(double slope, int index) {
            this.slope = slope;
            this.index = index;
        }

        @Override
        public int compareTo(PointScore that) {
            if (this.slope == that.slope) {
                return 0;
            } else if (this.slope > that.slope) {
                return 1;
            }
            return -1;
        }

        @Override
        public String toString() {
            return String.format(FMT, index, slope);
        }
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
