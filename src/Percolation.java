import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {

    private boolean[][] grid;
    private final WeightedQuickUnionUF weightedQuickUnionUF;
    private final int size;
    private int openSites;

    // create n-by-n grid, with all sites blocked
    public Percolation(int n) {
        this.grid = new boolean[n][n];
        this.weightedQuickUnionUF = new WeightedQuickUnionUF(2 + (int) Math.pow(n, 2));
        this.size = n;
    }

    // open site (row, col) if it is not open already
    public void open(int row, int col) {
        if (!isValidSite(row, col)) {
            throw new IllegalArgumentException(String.format("Invalid index access [%d][%d]", row, col));
        }
        if (grid[row - 1][col - 1]) {
            return;
        }
        grid[row - 1][col - 1] = true;
        openSites++;
        int currIdx = mapTo1DArray(row, col);
        connect(currIdx, row + 1, col); // bottom site
        connect(currIdx, row - 1, col); // top site
        connect(currIdx, row, col + 1); // right site
        connect(currIdx, row, col - 1); // left site
        if (row == 1) {
            weightedQuickUnionUF.union(currIdx, 0);
        }
        if (row == size) {
            weightedQuickUnionUF.union(currIdx, (int) Math.pow(size, 2) + 1);
        }
    }

    // is site (row, col) open?
    public boolean isOpen(int row, int col) {
        if (!isValidSite(row, col)) {
            throw new IllegalArgumentException(String.format("Invalid index access [%d][%d]", row, col));
        }
        return (grid[row - 1][col - 1]);
    }

    // is site (row, col) full?
    public boolean isFull(int row, int col) {
        if (!isValidSite(row, col)) {
            throw new IllegalArgumentException(String.format("Invalid index access [%d][%d]", row, col));
        }
        return weightedQuickUnionUF.connected(0, mapTo1DArray(row, col));
    }

    // number of open sites
    public int numberOfOpenSites() {
        return openSites;
    }

    // does the system percolate?
    public boolean percolates() {
        return weightedQuickUnionUF.connected(0, (int) Math.pow(size, 2) + 1);
    }

    private int mapTo1DArray(int row, int col) {
        return (row - 1) * size + col;
    }

    private void connect(int currIdx, int adjRow, int adjCol) {
        if (isValidSite(adjRow, adjCol) && isOpen(adjRow, adjCol)) {
            weightedQuickUnionUF.union(currIdx, mapTo1DArray(adjRow, adjCol));
        }
    }

    private boolean isValidSite(int row, int col) {
        return (row > 0 && row <= size && col > 0 && col <= size);
    }

}
