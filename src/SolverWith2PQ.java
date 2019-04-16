import java.util.Iterator;
import java.util.LinkedList;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;

public class SolverWith2PQ {

    private SearchNode goalNode;
    private final Board initialBoard;

    private static class SearchNode implements Comparable<SearchNode> {
        private final Board board;
        private final SearchNode predecessor;
        private final int noOfMoves;
        private final int score;

        public SearchNode(Board board, SearchNode predecessor, int noOfMoves) {
            this.board = board;
            this.predecessor = predecessor;
            this.noOfMoves = noOfMoves;
            this.score = noOfMoves + board.manhattan();
        }

        private int getScore() {
            return score;
        }

        @Override
        public int compareTo(SearchNode other) {
            if (other == null || this.getScore() < other.getScore()) {
                return -1;
            }
            if (other == this || this.getScore() == other.getScore()) {
                return 0;
            }
            return 1;
        }

    }

    // find a solution to the initial board (using the A* algorithm)
    public SolverWith2PQ(Board initial) {
        if (initial == null) {
            throw new IllegalArgumentException();
        }
        this.initialBoard = initial;
        if (initial.isGoal()) {
            goalNode = new SearchNode(initial, null, 0);
        } else {
            MinPQ<SearchNode> problemNodeQueue = new MinPQ<>();
            SearchNode start = new SearchNode(initial, null, 0);
            problemNodeQueue.insert(start);
            MinPQ<SearchNode> twinNodeQueue = new MinPQ<>();
            Board twinBoard = initial.twin();
            SearchNode twinNode = new SearchNode(twinBoard, null, 0);
            twinNodeQueue.insert(twinNode);
            solve(problemNodeQueue, twinNodeQueue);
        }
    }

    private boolean solve(MinPQ<SearchNode> problemNodeQueue, MinPQ<SearchNode> twinNodeQueue) {
        boolean twinNode = false;
        MinPQ<SearchNode> searchNodeQueue = problemNodeQueue; 
        SearchNode deletedNode = problemNodeQueue.delMin();
        while (!deletedNode.board.isGoal()) {
            Board board = deletedNode.board;
            Iterator<Board> neighborItr = board.neighbors().iterator();
            while (neighborItr.hasNext()) {
                Board neighborBoard = neighborItr.next();
                if (deletedNode.predecessor == null || !deletedNode.predecessor.board.equals(neighborBoard)) {
                    searchNodeQueue.insert(new SearchNode(neighborBoard, deletedNode, deletedNode.noOfMoves + 1));
                }
            }
            twinNode = !twinNode;
            searchNodeQueue = twinNode? twinNodeQueue : problemNodeQueue;
            deletedNode = searchNodeQueue.delMin();

            if (deletedNode == null) {
                return false;
            }
            if (deletedNode.board.isGoal()) {
                goalNode = deletedNode;
                return true;
            }
        }
        return false;
    }

    // is the initial board solvable?
    public boolean isSolvable() {
        SearchNode searchNode = goalNode;
        while (searchNode != null) {
            SearchNode previousNode = searchNode.predecessor;
            if (previousNode == null) {
                return (initialBoard.equals(searchNode.board));
            }
            searchNode = previousNode;
        }
        return false;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        return isSolvable() ? goalNode.noOfMoves : -1;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        LinkedList<Board> path = new LinkedList<>();
        SearchNode currentNode = goalNode;
        while (currentNode != null) {
            path.addFirst(currentNode.board);
            currentNode = currentNode.predecessor;
        }
        if (!path.isEmpty() && !path.getFirst().equals(initialBoard)) {
            path.clear();
        }
        return path.isEmpty() ? null : path;
    }

    // solve a slider puzzle (given below)
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] blocks = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable()) {
            StdOut.println("No solution possible");
        } else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}
