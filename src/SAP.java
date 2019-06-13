
import java.util.ArrayList;
import java.util.List;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class SAP {

    private Digraph G;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) {
            throw new IllegalArgumentException("Empty digraph");
        }
        this.G = G;
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        return new BreadthFirstSearch().search(v, w).length;
    }

    // a common ancestor of v and w that participates in a shortest ancestral
    // path; -1 if no such path
    public int ancestor(int v, int w) {
        return new BreadthFirstSearch().search(v, w).ancestor;
    }

    // length of shortest ancestral path between any vertex in v and any vertex
    // in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        return new BreadthFirstSearch().search(v, w).length;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no
    // such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        return new BreadthFirstSearch().search(v, w).ancestor;
    }

    private class BreadthFirstSearch {

        private boolean[] forwardMarked;
        private boolean[] reverseMarked;
        private int[] forwardEdgeTo;
        private int[] reverseEdgeTo;

        private Integer ancestor = -1;
        private Integer length = -1;

        public BreadthFirstSearch() {
            forwardMarked = new boolean[G.V()];
            reverseMarked = new boolean[G.V()];
            forwardEdgeTo = new int[G.V()];
            reverseEdgeTo = new int[G.V()];
        }

        private void initializeQueue(Iterable<Integer> v, Queue<Integer> queue, int[] edgeTo) {
            for (Integer v1 : v) {
                if (v1 >= G.V()) {
                    throw new IllegalArgumentException(String.format("Vertex %d larger than graph size %d", v1, G.V()));
                }
                queue.enqueue(v1);
                edgeTo[v1] = 0;
            }
        }

        public BreadthFirstSearch search(Iterable<Integer> v, Iterable<Integer> w) {
            Queue<Integer> forwardQueue = new Queue<>();
            Queue<Integer> reverseQueue = new Queue<>();
            initializeQueue(v, forwardQueue, forwardEdgeTo);
            initializeQueue(w, reverseQueue, reverseEdgeTo);
            int distance = 0;
            while (!forwardQueue.isEmpty() || !reverseQueue.isEmpty()) {
                if (findAncestor(forwardQueue, forwardMarked, reverseMarked, forwardEdgeTo, distance)) {
                    break;
                }
                if (findAncestor(reverseQueue, reverseMarked, forwardMarked, reverseEdgeTo, distance)) {
                    break;
                }
                distance++;
            }
            return this;
        }

        private boolean findAncestor(Queue<Integer> queue, boolean[] currentMarked, boolean[] otherMarked, int[] edgeTo, int distance) {
            while (!queue.isEmpty() && edgeTo[queue.peek()] == distance) {
                Integer vertex = queue.dequeue();
                currentMarked[vertex] = true;
                if (otherMarked[vertex]) {
                    ancestor = vertex;
                    calculateLength(vertex);
                    return true;
                }
                for (Integer adj : G.adj(vertex)) {
                    if (!currentMarked[vertex]) {
                        queue.enqueue(adj);
                        edgeTo[adj] = distance + 1;
                    }
                }
            }
            return false;
        }

        private void calculateLength(Integer vertex) {
            this.length = forwardEdgeTo[vertex] + reverseEdgeTo[vertex];
        }

        public BreadthFirstSearch search(Integer v, Integer w) {
            List<Integer> vList = new ArrayList<>();
            List<Integer> wList = new ArrayList<>();
            vList.add(v);
            wList.add(w);
            search(vList, wList);
            return this;
        }

    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
