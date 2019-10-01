import java.awt.Color;

import edu.princeton.cs.algs4.Picture;

public class SeamCarver {

    private static final double DEFAULT_BORDER_ENERGY = 1000d;

    private Color[][] colors;

    private int height;
    private int width;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException("Empty picture");
        }
        this.height = picture.height();
        this.width = picture.width();
        this.colors = new Color[width][height];
        updateColors(picture);
    }

    private void updateColors(Picture picture) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                colors[i][j] = picture.get(i, j);
            }
        }
    }

    private double calculateEnergy(Color[][] sColors, int column, int row, int sWidth, int sHeight) {
        int leftColumn = column - 1;
        int rightColumn = findNext(column, sWidth);
        int aboveRow = row - 1;
        int belowRow = findNext(row, sHeight);
        if (isAllIndexPositive(leftColumn, rightColumn, aboveRow, belowRow)) {
            double dx = calculateEnergy(sColors[leftColumn][row], sColors[rightColumn][row]);
            double dy = calculateEnergy(sColors[column][aboveRow], sColors[column][belowRow]);
            return Math.sqrt(dx + dy);
        } else {
            return DEFAULT_BORDER_ENERGY;
        }
    }

    private double calculateEnergy(Color one, Color two) {
        double pixelEnergy = Math.pow((double) two.getRed() - one.getRed(), 2);
        pixelEnergy += Math.pow((double) two.getGreen() - one.getGreen(), 2);
        pixelEnergy += Math.pow((double) two.getBlue() - one.getBlue(), 2);
        return pixelEnergy;
    }

    private boolean isAllIndexPositive(int... indexes) {
        for (int index : indexes) {
            if (index < 0)
                return false;
        }
        return true;
    }

    private int findNext(int pos, int maxLength) {
        pos++;
        return pos < maxLength ? pos : -1;
    }

    // current picture
    public Picture picture() {
        Picture picture = new Picture(width, height);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                picture.set(i, j, colors[i][j]);
            }
        }
        return picture;
    }

    // width of current picture
    public int width() {
        return width;
    }

    // height of current picture
    public int height() {
        return height;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x < 0 || x >= width()) {
            throw new IllegalArgumentException("Invalid x: " + x);
        }
        if (y < 0 || y >= height()) {
            throw new IllegalArgumentException("Invalid y: " + y);
        }
        return calculateEnergy(colors, x, y, width(), height());
    }

    private int toOneDimension(int col, int row, int sWidth) {
        return (row * sWidth) + col;
    }

    private int getColumnFromOneDimension(int oneDimPos, int sWidth) {
        return (oneDimPos % sWidth);
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {

        Color[][] sColor = new Color[height][width];
        for (int col = 0; col < height; col++) {
            for (int row = 0; row < width; row++) {
                sColor[col][row] = this.colors[row][col];
            }
        }
        return findVerticalSeam(sColor, height, width);
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        return findVerticalSeam(colors, width, height);
    }

    private int[] findVerticalSeam(Color[][] sColors, int sWidth, int sHeight) {

        int size = sWidth * sHeight;
        double[] energy = new double[size];
        double[] distTo = new double[size];
        int[] edgeTo = new int[size];

        initalizeSeam(sColors, sWidth, sHeight, energy, distTo, edgeTo);
        for (int row = 0; row < sHeight - 1; row++) {
            for (int col = 0; col < sWidth; col++) {
                int pos = toOneDimension(col, row, sWidth);
                if (col > 0) {
                    relax(pos, toOneDimension(col - 1, row + 1, sWidth), energy, distTo, edgeTo);
                }
                relax(pos, toOneDimension(col, row + 1, sWidth), energy, distTo, edgeTo);
                if (col < sWidth - 1) {
                    relax(pos, toOneDimension(col + 1, row + 1, sWidth), energy, distTo, edgeTo);
                }
            }
        }
        return findVerticalSeam(sWidth, sHeight, distTo, edgeTo);
    }

    private void initalizeSeam(Color[][] sColors, int sWidth, int sHeight, double[] energy, double[] distTo, int[] edgeTo) {
        for (int col = 0; col < sWidth; col++) {
            for (int row = 0; row < sHeight; row++) {
                int pos = toOneDimension(col, row, sWidth);
                energy[pos] = calculateEnergy(sColors, col, row, sWidth, sHeight);
                edgeTo[pos] = -1;
                if (row == 0) {
                    distTo[pos] = 0;
                } else {
                    distTo[pos] = Double.POSITIVE_INFINITY;
                }
            }
        }
    }

    private int[] findVerticalSeam(int sWidth, int sHeight, double[] distTo, int[] edgeTo) {
        double minDist = Double.POSITIVE_INFINITY;
        int minPosIdx = -1;
        for (int col = 0; col < sWidth; col++) {
            int pos = toOneDimension(col, sHeight - 1, sWidth);
            if (distTo[pos] < minDist) {
                minDist = distTo[pos];
                minPosIdx = pos;
            }
        }
        int[] seam = new int[sHeight];
        int idx = sHeight - 1;
        while (minPosIdx != -1) {
            seam[idx--] = getColumnFromOneDimension(minPosIdx, sWidth);
            minPosIdx = edgeTo[minPosIdx];
        }
        return seam;
    }

    private void relax(int from, int to, double[] energy, double[] distTo, int[] edgeTo) {
    	
    	double newDistance = distTo[from] + energy[to];
    	double currDistance = distTo[to];
        if (newDistance < currDistance) {
            distTo[to] = newDistance;
            edgeTo[to] = from;
        }
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        validateSeam(seam);
        if (seam.length != width) {
            throw new IllegalArgumentException(String.format("Seam length %d does not match the width %d", seam.length, width));
        }
        for (int i = 0; i < seam.length; i++) {
            validateRow(seam[i]);
            if(i > 0 && Math.abs(seam[i] - seam[i-1]) > 1) {
                throw new IllegalArgumentException(String.format("Seam diff between index %d and %d is greater than one.", i-1, i));
            }
            for (int j = seam[i] + 1; j < height(); j++) {
                colors[i][j - 1] = colors[i][j];
            }
        }
        height--;
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        validateSeam(seam);
        if (seam.length != height) {
            throw new IllegalArgumentException(String.format("Seam length %d does not match the height %d", seam.length, height));
        }
        for (int i = 0; i < seam.length; i++) {
            validateColumn(seam[i]);
            if(i > 0 && Math.abs(seam[i] - seam[i-1]) > 1) {
                throw new IllegalArgumentException(String.format("Seam diff between index %d and %d is greater than one.", i-1, i));
            }
            for (int j = seam[i] + 1; j < width(); j++) {
                colors[j - 1][i] = colors[j][i];
            }
        }
        width--;
    }

    private void validateSeam(int[] seam) {
        if (seam == null) {
            throw new IllegalArgumentException("Empty seam");
        }
    }

    private void validateColumn(int column) {
        if (column < 0 || column >= width) {
            throw new IllegalArgumentException(String.format("Row %d does not match the width %d", column, width));
        }
    }

    private void validateRow(int row) {
        if (row < 0 || row >= height) {
            throw new IllegalArgumentException(String.format("Row %d does not match the width %d", row, height));
        }
    }

    public static void main(String[] args) {
        String file = "6x5.png";
        String dir = "/Users/pgopalakrishnan/work/learn/alg/seam/";

        Picture picture = new Picture(dir + file);
//        System.out.println(picture.toString());
        SeamCarver seamCraver = new SeamCarver(picture);
        int[] seam = seamCraver.findVerticalSeam();
        System.out.println("Seam: " + seamCraver.toString(seam));
    }

    private String toString(int[] seam) {
        StringBuilder builder = new StringBuilder();
        for (int i : seam) {
            builder.append(i).append(" ");
        }
        return builder.toString();
    }
    
    private String toString(double[] energyArr, int sWidth) {
        StringBuilder builder = new StringBuilder();
        builder.append("*** Energy Graph ***");
        for(int i=0; i< energyArr.length; i++) {
            if(i % sWidth == 0) {
                builder.append("\n");
            }
            builder.append(energyArr[i]).append(" ");
            
        }
        return builder.toString();
    }
    

}