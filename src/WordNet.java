import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;

public class WordNet {

    private Map<String, Set<Integer>> wordIdMapper;
    private Map<Integer, String> idWordMapper;
    private int V;
    private Digraph digraph;
    private SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        wordIdMapper = new HashMap<>();
        idWordMapper = new HashMap<>();
        V = 0;
        createSynsets(synsets);
        createHypernyms(hypernyms);
        sap = new SAP(digraph);
    }

    private void createSynsets(String synsets) {
        if (synsets == null) {
            throw new IllegalArgumentException("Empty synsets file name");
        }
        In in = null;
        try {
            in = new In(synsets);
            String line = in.readLine();
            while (line != null) {
                createSynsetEntry(line);
                line = in.readLine();
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not process file" + synsets);
        } finally {
            if (in != null)
                in.close();
        }
    }

    private void createHypernyms(String hypernyms) {
        if (hypernyms == null) {
            throw new IllegalArgumentException("Empty hypernyms file name");
        }
        In in = null;
        digraph = new Digraph(V+1);
        try {
            in = new In(hypernyms);
            String line = in.readLine();
            while (line != null) {
                createHypernymEntry(line);
                line = in.readLine();
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not process file" + hypernyms);
        } finally {
            if (in != null)
                in.close();
        }
    }

    private void createSynsetEntry(String line) {
        try {
            String[] params = line.split(",");
            Integer key = Integer.parseInt(params[0]);
            String[] words = params[1].split(" ");
            if (words == null || words.length == 0) {
                throw new IllegalArgumentException("Invalid entry in synsets: " + line);
            }
            for (String word : words) {
                Set<Integer> ids = wordIdMapper.get(word);
                if(ids == null) {
                    ids = new HashSet<>();
                    wordIdMapper.put(word, ids);
                }
                ids.add(key);
            }
            idWordMapper.put(key, params[1]);
            V = Math.max(V, key);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid entry in synsets: " + line);
        }
    }

    private void createHypernymEntry(String line) {
        try {
            String[] params = line.split(",");
            if (params == null || params.length < 1) {
                throw new IllegalArgumentException("Invalid entry in hypernym: " + line);
            }
            int start = Integer.parseInt(params[0]);
            for (int i = 1; i < params.length; i++) {
                digraph.addEdge(start, Integer.parseInt(params[i]));
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid entry in hypernym: " + line);
        }
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return wordIdMapper.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        return wordIdMapper.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (!wordIdMapper.containsKey(nounA)) {
            throw new IllegalArgumentException("Could not find noun: " + nounA);
        }
        if (!wordIdMapper.containsKey(nounB)) {
            throw new IllegalArgumentException("Could not find noun: " + nounB);
        }
        return sap.length(wordIdMapper.get(nounA), wordIdMapper.get(nounB));
    }

    // a synset (second field of synsets.txt) that is the common ancestor of
    // nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (!wordIdMapper.containsKey(nounA)) {
            throw new IllegalArgumentException("Could not find noun: " + nounA);
        }
        if (!wordIdMapper.containsKey(nounB)) {
            throw new IllegalArgumentException("Could not find noun: " + nounB);
        }
        int ancestor = sap.ancestor(wordIdMapper.get(nounA), wordIdMapper.get(nounB));
        return idWordMapper.get(ancestor);
    }

    // do unit testing of this class
    public static void main(String[] args) {

    }
}
