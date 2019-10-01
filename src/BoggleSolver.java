import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class BoggleSolver {

    private DictionaryTree dictTree;

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        if (dictionary == null) {
            throw new IllegalArgumentException("Empty dictionary");
        }
        this.dictTree = new DictionaryTree();
        for (String word : dictionary) {
            this.dictTree.put(word);
        }

    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        Graph graph = createGraph(board);
        Set<String> words = new WordFinder(board, graph, dictTree).findWords();
        List<String> wordList = new ArrayList<>(words);
        Collections.sort(wordList);
        return wordList;
    }
    
    private Graph createGraph(BoggleBoard board) {
        int maxRow = board.rows();
        int maxCol = board.cols();
        int V = (maxRow * maxCol);
        Graph graph = new Graph(V);
        for (int row = 0; row < maxRow; row++) {
            for (int col = 0; col < maxCol; col++) {
                createEdges(board, graph, row, col, maxRow, maxCol);
            }
        }
        return graph;
    }
    
    private void createEdges(BoggleBoard board, Graph graph, int row, int col, int maxRow, int maxCol) {
        int current1D = to1D(row, col, maxCol);
        addIfValid(graph, current1D, row - 1, col - 1, maxRow, maxCol);
        addIfValid(graph, current1D, row, col - 1, maxRow, maxCol);
        addIfValid(graph, current1D, row + 1, col - 1, maxRow, maxCol);
        addIfValid(graph, current1D, row - 1, col, maxRow, maxCol);
        addIfValid(graph, current1D, row + 1, col, maxRow, maxCol);
        addIfValid(graph, current1D, row - 1, col + 1, maxRow, maxCol);
        addIfValid(graph, current1D, row, col + 1, maxRow, maxCol);
        addIfValid(graph, current1D, row + 1, col + 1, maxRow, maxCol);
    }
    
    private int to1D(int row, int col, int maxCol) {
        return (row * maxCol) + col;
    }

    private void addIfValid(Graph graph, int current1D, int row, int col, int maxRow, int maxCol) {
        if (row >= 0 && row < maxRow && col >= 0 && col < maxCol) {
            graph.addEdge(current1D, to1D(row, col, maxCol));
        }
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (word == null) {
            throw new IllegalArgumentException("Empty word");
        }
        int len = word.length();
        if(len <= 2) {
            return 0;
        }else if(len <= 4) {
            return 1;
        }else if(len <= 5) {
            return 2;
        }else if (len <= 6) {
            return 3;
        }else if (len <= 7) {
            return 5;
        }
        return 11;
    }
    
    private static class WordFinder{
        private Graph graph;
        private BoggleBoard board;
        private DictionaryTree dictTree;
        private int maxCol;
        private boolean[] visited;
        private Set<String> words;
        
        public WordFinder(BoggleBoard board, Graph graph, DictionaryTree dictTree) {
            this.board = board;
            this.graph = graph;
            this.maxCol = board.cols();
            this.dictTree = dictTree;
            words = new HashSet<>();
        }
        
        public Set<String> findWords() {
            visited = new boolean[graph.V];
            for(int i=0; i< graph.V; i++) {
                visited[i] = true;
                _findWords(i, dictTree.root, "", 0);
                visited[i] = false;
            }
            return words;
        }
        
        private void _findWords(int v, TrieNode root, String prefixWord, int prefix) {
            char c = board.getLetter(v / maxCol, v % maxCol);
            prefixWord = prefixWord + c;
            TrieNode matchNode = dictTree.getNode(prefixWord, prefix, root);
            TrieNode qMatchNode = null;
            if(matchNode != null && c == 'Q') {
                qMatchNode = dictTree.getNode(prefixWord + "U", prefix + 1, matchNode.mid);
            }
            addIfValid(matchNode);
            if(matchNode != null) {
                for(int w : graph.adj(v)) {
                    if(!visited[w]) {
                        visited[w] = true;
                        if(qMatchNode != null) {
                            _findWords(w, qMatchNode.mid, prefixWord + "U", prefix + 2);
                        }
                        _findWords(w, matchNode.mid, prefixWord, prefix + 1);
                        visited[w] = false;
                    }
                }
            }
        }
        
        private void addIfValid(TrieNode matchNode) {
            if(matchNode != null && matchNode.value != null && matchNode.value.length() > 2) {
                words.add(matchNode.value);
            }
        }
        
    }
    
    public static void main(String[] args) {
        String dict1 = "/Users/pgopalakrishnan/work/learn/alg/boggle/dictionary-algs4.txt";
        String dict2 = "/Users/pgopalakrishnan/work/learn/alg/boggle/dictionary-yawl.txt";
        String boggle1 = "/Users/pgopalakrishnan/work/learn/alg/boggle/board4x4.txt";
        String boggle2 = "/Users/pgopalakrishnan/work/learn/alg/boggle/board-q.txt";
        String boggle3 = "/Users/pgopalakrishnan/work/learn/alg/boggle/board-qwerty.txt";
        In in = new In(dict2);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(boggle3);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }

    private static class Graph{
        
        private int V;
        private List<Integer>[] adj;
        
        public Graph(int V) {
            this.V = V;
            this.adj = new List[V];
            for(int i=0; i<adj.length; i++) {
                adj[i] = new ArrayList<>();
            }
        }
        
        public void addEdge(int v, int w) {
            adj[v].add(w);
        }
        
        public List<Integer> adj(int v){
            return adj[v];
        }
    }
    
    private static class DictionaryTree {
        private TrieNode root;

        public void put(String word) {
            root = put(word, 0, root);
        }

        public boolean isValid(String word) {
            TrieNode node = getNode(word, 0, root);
            return (node != null && node.value != null && node.value.equals(word));
        }

        public TrieNode getNode(String word, int prefix, TrieNode node) {
            if (node == null || prefix >= word.length()) {
                return null;
            }
            int compare = Character.compare(node.key, word.charAt(prefix));
            if (compare == 0) {
                if (prefix == word.length() - 1) {
                    return node;
                }
                return getNode(word, prefix + 1, node.mid);
            } else if (compare > 0) {
                return getNode(word, prefix, node.left);
            } else {
                return getNode(word, prefix, node.right);
            }
        }

        private TrieNode put(String word, int prefix, TrieNode node) {
            if (prefix >= word.length()) {
                return null;
            }
            if (node == null) {
                node = new TrieNode(word.charAt(prefix));
            }
            
            int compare = Character.compare(node.key, word.charAt(prefix));
            if (compare == 0) {
                if (prefix == word.length() - 1) {
                    node.value = word;
                    return node;
                }
                node.mid = put(word, prefix + 1, node.mid);
            } else if (compare > 0) {
                node.left = put(word, prefix, node.left);
            } else {
                node.right = put(word, prefix, node.right);
            }
            return node;
        }

    }

    private static class TrieNode {
        public final char key;
        private TrieNode left;
        private TrieNode mid;
        private TrieNode right;
        private String value;

        public TrieNode(char key) {
            this.key = key;
        }
    }
}
