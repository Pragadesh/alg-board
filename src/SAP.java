
import java.util.ArrayList;
import java.util.List;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
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
        validateIndex(v);
        validateIndex(w);
        return new BreadthFirstSearch().search(v, w).length;
    }

    // a common ancestor of v and w that participates in a shortest ancestral
    // path; -1 if no such path
    public int ancestor(int v, int w) {
        validateIndex(v);
        validateIndex(w);
        return new BreadthFirstSearch().search(v, w).ancestor;
    }

    // length of shortest ancestral path between any vertex in v and any vertex
    // in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        validateIndex(v);
        validateIndex(w);
        return new BreadthFirstSearch().search(v, w).length;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no
    // such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        validateIndex(v);
        validateIndex(w);
        return new BreadthFirstSearch().search(v, w).ancestor;
    }
    
    private void validateIndex(int index) {
        if(index < 0 || index >= G.V()) {
            throw new IllegalArgumentException("Invalid index: "+index);
        }
    }
    
    private void validateIndex(Iterable<Integer> indexes) {
        for(Integer v : indexes) {
            if(v == null)
                throw new IllegalArgumentException("Empty index.");
            validateIndex(v);
        }
    }

    private class BreadthFirstSearch {

        private boolean[] forwardMarked;
        private boolean[] reverseMarked;
        private int[] forwardDistance;
        private int[] reverseDistance;

        private Integer ancestor = -1;
        private Integer length = -1;

        public BreadthFirstSearch() {
            forwardMarked = new boolean[G.V()];
            reverseMarked = new boolean[G.V()];
            forwardDistance = new int[G.V()];
            reverseDistance = new int[G.V()];
        }

        private void initializeQueue(Iterable<Integer> v, Queue<Integer> queue, boolean[] marked, int[] distance) {
            for (Integer v1 : v) {
                if (v1 >= G.V()) {
                    throw new IllegalArgumentException(String.format("Vertex %d larger than graph size %d", v1, G.V()));
                }
                queue.enqueue(v1);
                distance[v1] = 0;
                marked[v1] = true;
            }
        }

        public BreadthFirstSearch search(Iterable<Integer> v, Iterable<Integer> w) {
            long start = System.currentTimeMillis();
            Queue<Integer> forwardQueue = new Queue<>();
            Queue<Integer> reverseQueue = new Queue<>();
            initializeQueue(v, forwardQueue, forwardMarked, forwardDistance);
            initializeQueue(w, reverseQueue, reverseMarked, reverseDistance);
            int distance = 0;
            while (!forwardQueue.isEmpty() || !reverseQueue.isEmpty()) {
                findAncestor(forwardQueue, forwardMarked, reverseMarked, forwardDistance, distance);
                findAncestor(reverseQueue, reverseMarked, forwardMarked, reverseDistance, distance);
                distance++;
                if (this.length != -1 && distance >= this.length) {
                    break;
                }
            }
            long timeTaken = (System.currentTimeMillis() - start);
            if (timeTaken > 2000) {
                System.out.println(String.format("Search completed in %d ms", timeTaken));
            }
            return this;
        }

        private void findAncestor(Queue<Integer> queue, boolean[] currentMarked, boolean[] otherMarked, int[] distanceTo, int distance) {
            while (!queue.isEmpty() && distanceTo[queue.peek()] == distance) {
                Integer vertex = queue.dequeue();
                if (otherMarked[vertex]) {
                    markAncestor(vertex);
                }
                for (Integer adj : G.adj(vertex)) {
                    if (!currentMarked[adj]) {
                        currentMarked[adj] = true;
                        distanceTo[adj] = distanceTo[vertex] + 1;
                        queue.enqueue(adj);
                    }
                }
            }
        }

        private void markAncestor(int vertex) {
            int currentLength = calculateLength(vertex);
            if (this.length == -1 || this.length > currentLength) {
                ancestor = vertex;
                this.length = currentLength;
            }
        }

        private int calculateLength(Integer vertex) {
            return forwardDistance[vertex] + reverseDistance[vertex];
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
        In in = new In("/Users/pgopalakrishnan/work/learn/workspace/alg/digraph/digraph1.txt");
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        List<Integer> v = new ArrayList<>();
        v.add(0);
        v.add(7);
        List<Integer> w = new ArrayList<>();
        w.add(1);
        w.add(null);
        w.add(2);
        int length = sap.length(v, w);
        int ancestor = sap.ancestor(v, w);
        StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        // while (!StdIn.isEmpty()) {
        // int v = StdIn.readInt();
        // int w = StdIn.readInt();
        // int length = sap.length(v, w);
        // int ancestor = sap.ancestor(v, w);
        // StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        // }
    }
}
