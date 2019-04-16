import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class Board {

    private static final int EMPTY = 0;

    private final int dimension;
    private final int[] blocks;
    private int hamming;
    private int manhattan;
    private List<Board> neighbors;
    private Board twin;

    // construct a board from an n-by-n array of blocks (where blocks[i][j] =
    // block in row i, column j)
    public Board(int[][] blocks) {
        if (blocks == null || blocks.length <= 1) {
            throw new IllegalArgumentException("blocks is empty");
        }
        this.dimension = blocks.length;
        if (dimension != blocks[0].length) {
            throw new IllegalArgumentException("blocks is not a (n * n) array");
        }
        this.blocks = new int[dimension * dimension];
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                this.blocks[i * dimension + j] = blocks[i][j];
            }
        }
        calcHamming();
        calcManhattan();
    }

    private Board(int[] blocks, int dimension) {
        this.blocks = blocks;
        this.dimension = dimension;
        calcHamming();
        calcManhattan();
    }

    // board dimension n
    public int dimension() {
        return dimension;
    }

    private void calcHamming() {
        for (int i = 0; i < blocks.length; i++) {
            if (blocks[i] != 0 && blocks[i] != (i + 1))
                hamming++;
        }
    }

    // number of blocks out of place
    public int hamming() {
        return hamming;
    }

    // sum of Manhattan distances between blocks and goal
    public int manhattan() {
        return manhattan;
    }

    private void calcManhattan() {
        for (int i = 0; i < blocks.length; i++) {
            int val = blocks[i];
            if (val != 0 && val != (i + 1)) {
                manhattan += Math.abs(((val - 1) % dimension) - (i % dimension));
                manhattan += Math.abs(((val - 1) / dimension) - (i / dimension));
            }
        }
    }

    // is this board the goal board?
    public boolean isGoal() {
        for (int i = 0; i < blocks.length - 2; i++) {
            if (i != blocks[i] - 1) {
                return false;
            }
        }
        return true;
    }

    // a board that is obtained by exchanging any pair of blocks
    public Board twin() {
        if (twin == null) {
            twin = findTwin();
        }
        return twin;
    }

    private Board findTwin() {
        int[] newBlocks = blocks.clone();
        int elem1 = StdRandom.uniform(newBlocks.length);
        while (newBlocks[elem1] == 0) {
            elem1 = StdRandom.uniform(newBlocks.length);
        }
        int elem2 = StdRandom.uniform(newBlocks.length);
        while (elem1 == elem2 || newBlocks[elem2] == 0) {
            elem2 = StdRandom.uniform(newBlocks.length);
        }
        exchangePosition(newBlocks, elem1, elem2);
        return new Board(newBlocks, dimension);
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (y == null || !y.getClass().equals(this.getClass())) {
            return false;
        }
        Board other = (Board) y;
        if (this.dimension != other.dimension) {
            return false;
        }
        if (this.blocks.length != other.blocks.length) {
            return false;
        }
        for (int i = 0; i < blocks.length; i++) {
            if (blocks[i] != other.blocks[i]) {
                return false;
            }
        }
        return true;
    }

    private void populateNeighbors() {
        this.neighbors = new ArrayList<>();
        int emptyPosition = findEmptyPosition();
        int row = emptyPosition / dimension;
        int column = emptyPosition % dimension;
        if (column > 0) {
            int[] leftBlock = this.blocks.clone();
            exchangePosition(leftBlock, emptyPosition, findPosition(row, (column - 1)));
            this.neighbors.add(new Board(leftBlock, dimension));
        }
        if (column < dimension - 1) {
            int[] rightBlock = this.blocks.clone();
            exchangePosition(rightBlock, emptyPosition, findPosition(row, (column + 1)));
            this.neighbors.add(new Board(rightBlock, dimension));
        }
        if (row > 0) {
            int[] upperBlock = this.blocks.clone();
            exchangePosition(upperBlock, emptyPosition, findPosition(row - 1, column));
            this.neighbors.add(new Board(upperBlock, dimension));
        }
        if (row < dimension - 1) {
            int[] lowerBlock = this.blocks.clone();
            exchangePosition(lowerBlock, emptyPosition, findPosition(row + 1, column));
            this.neighbors.add(new Board(lowerBlock, dimension));
        }
    }

    private int findPosition(int row, int column) {
        return (row * dimension) + column;
    }

    private void exchangePosition(int[] arr, int position1, int position2) {
        int temp = arr[position1];
        arr[position1] = arr[position2];
        arr[position2] = temp;
    }

    private int findEmptyPosition() {
        for (int i = 0; i < blocks.length; i++) {
            if (blocks[i] == EMPTY) {
                return i;
            }
        }
        throw new IllegalStateException("Missing empty pointer.");
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        if (neighbors == null) {
            populateNeighbors();
        }

        return new Iterable<Board>() {

            @Override
            public Iterator<Board> iterator() {
                return neighbors.iterator();
            }
        };
    }

    // string representation of this board (in the output format specified
    // below)
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.dimension).append("\n");
        for (int i = 0; i < blocks.length; i++) {
            sb.append(blocks[i]);
            if ((i + 1) % dimension == 0) {
                sb.append("\n");
            } else {
                sb.append("\t");
            }
        }
        return sb.toString();
    }

    // unit tests (not graded)
    public static void main(String[] args) {
        {
            int[][] blocks = new int[][] { { 8, 1, 3 }, { 4, 0, 2 }, { 7, 6, 5 } };
            Board board = new Board(blocks);
            StdOut.printf("Hamming: %d\n", board.hamming());
            StdOut.printf("Manhattan: %d\n", board.manhattan());
            StdOut.printf("Original Board:\n%s\n", board);
            StdOut.printf("Neighbors\n-------------------------------\n");
            Iterator<Board> iterator = board.neighbors().iterator();
            while (iterator.hasNext()) {
                Board neighbor = iterator.next();
                StdOut.printf("%s\n", neighbor);
            }
            StdOut.printf("-------------------------------\n");
        }
        {
            int[][] blocks = new int[][] { { 0, 1, 3 }, { 4, 2, 5 }, { 7, 8, 6 } };
            Board board = new Board(blocks);
            StdOut.printf("Hamming: %d\n", board.hamming());
            StdOut.printf("Manhattan: %d\n", board.manhattan());
            StdOut.printf("Original Board:\n%s\n", board);
            StdOut.printf("Neighbors\n-------------------------------\n");
            Iterator<Board> iterator = board.neighbors().iterator();
            while (iterator.hasNext()) {
                Board neighbor = iterator.next();
                StdOut.printf("%s\n", neighbor);
            }
            StdOut.printf("-------------------------------\n");
        }
        {
            int[][] blocks = new int[][] { { 1, 6, 4 }, { 7, 0, 8 }, { 2, 3, 5 } };
            Board board = new Board(blocks);
            for (int i = 0; i < 100; i++) {
                Board twin = board.twin();
                StdOut.printf("%s\n", twin);
                if (board.equals(twin)) {
                    StdOut.printf("Encountered same board\n");
                    break;
                }
            }
            StdOut.printf("End of loop\n");

        }
    }

}
