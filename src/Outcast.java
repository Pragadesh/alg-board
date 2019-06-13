import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {

    private WordNet wordnet;

    public Outcast(WordNet wordnet) {
        // constructor takes a WordNet object
        if (wordnet == null) {
            throw new IllegalArgumentException("Empty wordnet");
        }
        this.wordnet = wordnet;
    }

    public String outcast(String[] nouns) {
        // given an array of WordNet nouns, return an outcast
        if (nouns == null || nouns.length == 0) {
            throw new IllegalArgumentException("Empty nouns");
        }
        int[] distance = new int[nouns.length];
        for (int i = 0; i < nouns.length; i++) {
            for (int j = i + 1; j < nouns.length; j++) {
                int wordDistance = wordnet.distance(nouns[i], nouns[j]);
                distance[i] += wordDistance;
                distance[j] += wordDistance;
            }
        }
        return nouns[getOutcaseIndex(distance)];
    }

    private int getOutcaseIndex(int[] distance) {
        int maxDistance = distance[0];
        int maxDistanceIndex = 1;
        for (int i = 1; i < distance.length; i++) {
            if (distance[i] >= maxDistance) {
                maxDistance = distance[i];
                maxDistanceIndex = i;
            }
        }
        return maxDistanceIndex;
    }

    public static void main(String[] args) {
        // see test client below
        args = new String[] {
                "/Users/pgopalakrishnan/work/learn/workspace/alg/digraph/synsets.txt", 
                "/Users/pgopalakrishnan/work/learn/workspace/alg/digraph/hypernyms.txt", 
                "/Users/pgopalakrishnan/work/learn/workspace/alg/digraph/outcast5.txt",
                "/Users/pgopalakrishnan/work/learn/workspace/alg/digraph/outcast8.txt",
                "/Users/pgopalakrishnan/work/learn/workspace/alg/digraph/outcast11.txt"
                };
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
