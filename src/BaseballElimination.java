import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

/*
 * https://coursera.cs.princeton.edu/algs4/assignments/baseball/specification.php
 */
public class BaseballElimination {

    private int N;
    private Map<String, Integer> teamIndexMap;
    private List<String> teams;

    private int[] wins;
    private int[] losses;
    private int[] remaining;

    private int[][] against;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String teamFile) {
        if (teamFile == null) {
            throw new IllegalArgumentException("Empty teamFile file name");
        }
        createTeamSchedules(teamFile);
    }

    private void createTeamSchedules(String teamFile) {
        In in = null;
        try {
            in = new In(teamFile);
            int noOfTeams = in.readInt();
            in.readLine();
            init(noOfTeams);
            int teamIndex = 0;
            while (teamIndex < noOfTeams) {
                String teamLine = in.readLine();
                createTeamEntry(teamLine, teamIndex);
                teamIndex++;
            }
        } finally {
            if (in != null)
                in.close();
        }
    }

    private void init(int noOfTeams) {
        this.N = noOfTeams;
        this.teamIndexMap = new HashMap<>();
        this.teams = new ArrayList<>();
        this.wins = new int[N];
        this.losses = new int[N];
        this.remaining = new int[N];
        this.against = new int[N][N];
    }

    private void createTeamEntry(String teamLine, int teamIndex) {
        try {
            String[] values = teamLine.trim().split("[ ]+");
            int expectedLength = N + 4;
            if (values.length == expectedLength) {
                this.teamIndexMap.put(values[0], teamIndex);
                this.teams.add(values[0]);
                this.wins[teamIndex] = Integer.parseInt(values[1]);
                this.losses[teamIndex] = Integer.parseInt(values[2]);
                this.remaining[teamIndex] = Integer.parseInt(values[3]);
                for (int i = 4; i < expectedLength; i++) {
                    this.against[teamIndex][i - 4] = Integer.parseInt(values[i]);
                }
                return;
            }

        } catch (NumberFormatException e) {
            System.err.println(e);
        }
        throw new IllegalArgumentException("Could not process: " + teamLine);
    }

    private int getTeamIndex(String team) {
        Integer teamIndex = this.teamIndexMap.get(team);
        if (teamIndex != null) {
            return teamIndex;
        }
        throw new IllegalArgumentException("Invalid team name: " + team);
    }

    // number of teams
    public int numberOfTeams() {
        return N;
    }

    // all teams
    public Iterable<String> teams() {
        return teams;
    }

    // number of wins for given team
    public int wins(String team) {
        return this.wins[getTeamIndex(team)];
    }

    // number of losses for given team
    public int losses(String team) {
        return this.losses[getTeamIndex(team)];
    }

    // number of remaining games for given team
    public int remaining(String team) {
        return this.remaining[getTeamIndex(team)];
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        return this.against[getTeamIndex(team1)][getTeamIndex(team2)];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        return (certificateOfElimination(team) != null);
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        List<String> teamsWithHigherWins = teamsWithHigherWinsThanScheduledMatches(team);
        if (teamsWithHigherWins != null && !teamsWithHigherWins.isEmpty()) {
            return teamsWithHigherWins;
        }
        teamsWithHigherWins = checkForWinDistribution(team);
        if (teamsWithHigherWins != null && !teamsWithHigherWins.isEmpty()) {
            return teamsWithHigherWins;
        }
        return null;
    }

    private List<String> checkForWinDistribution(String team) {
        int teamIndex = getTeamIndex(team);
        int s = 0;
        int noOfTeamCombinations = N * N;
        int teamStart = noOfTeamCombinations + 1;
        int t = teamStart + N;
        FlowNetwork flowNetwork = new FlowNetwork(t + 1);
        for (int i = 0; i < against.length; i++) {
            for (int j = 0; j < against[i].length; j++) {
                if (i == j || i == teamIndex || j == teamIndex || against[i][j] == 0 || i > j) {
                    continue;
                }
                int schedulePosition = (i * N) + j + 1;
                flowNetwork.addEdge(new FlowEdge(s, schedulePosition, against[i][j]));
                flowNetwork.addEdge(new FlowEdge(schedulePosition, teamStart + i, Integer.MAX_VALUE));
                flowNetwork.addEdge(new FlowEdge(schedulePosition, teamStart + j, Integer.MAX_VALUE));
            }
        }
        int maxPossibleWins = wins[teamIndex] + remaining[teamIndex];
        for (int i = 0; i < wins.length; i++) {
            if (i == teamIndex) {
                continue;
            }
            flowNetwork.addEdge(new FlowEdge(teamStart + i, t, maxPossibleWins - wins[i]));
        }
        FordFulkerson fordFulkerson = new FordFulkerson(flowNetwork, s, t);
        List<String> teamsAhead = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            if (i == teamIndex) {
                continue;
            }
            if (fordFulkerson.inCut(i + teamStart)) {
                teamsAhead.add(teams.get(i));
            }
        }
        return teamsAhead;
    }

    private List<String> teamsWithHigherWinsThanScheduledMatches(String team) {
        List<String> teamsAhead = new ArrayList<>();
        int teamIndex = getTeamIndex(team);
        int maxWins = wins[teamIndex] + remaining[teamIndex];
        for (int i = 0; i < N; i++) {
            if (i == teamIndex) {
                continue;
            }
            if (wins[i] > maxWins) {
                teamsAhead.add(teams.get(i));
            }
        }
        return teamsAhead;
    }

    private static class FordFulkerson {
        private boolean[] marked;
        private FlowEdge[] edgeTo;
        private int value;

        public FordFulkerson(FlowNetwork network, int s, int t) {

            while (hasAugmentingPath(network, s, t)) {
                int bottle = Integer.MAX_VALUE;
                for (int v = t; v != s; v = edgeTo[v].other(v)) {
                    bottle = Math.min(bottle, edgeTo[v].residualCapacityTo(v));
                }
                for (int v = t; v != s; v = edgeTo[v].other(v)) {
                    edgeTo[v].addResidualFlowTo(v, bottle);
                }
                value += bottle;
            }

        }

        private boolean hasAugmentingPath(FlowNetwork network, int s, int t) {
            marked = new boolean[network.V()];
            edgeTo = new FlowEdge[network.V()];
            Queue<Integer> q = new LinkedList<>();
            q.offer(s);
            marked[s] = true;
            while (!q.isEmpty()) {
                int v = q.poll();
                for (FlowEdge edge : network.adj[v]) {
                    int w = edge.other(v);
                    if (!marked[w] && edge.residualCapacityTo(w) > 0) {
                        marked[w] = true;
                        edgeTo[w] = edge;
                        q.offer(w);
                    }
                }
            }
            return marked[t];
        }

        public int getValue() {
            return value;
        }

        public boolean inCut(int v) {
            return marked[v];
        }
    }

    private static class FlowEdge {

        private int from;
        private int to;
        private int capacity;
        private int flow;

        public FlowEdge(int from, int to, int capacity) {
            this.from = from;
            this.to = to;
            this.capacity = capacity;
        }

        public int other(int v) {
            if (v == from) {
                return to;
            } else if (v == to) {
                return from;
            }
            throw new IllegalArgumentException("Invalid vertex: " + v);
        }

        public int residualCapacityTo(int v) {
            if (v == to) {
                return (capacity - flow); // forward edge
            } else if (v == from) {
                return flow;
            }
            throw new IllegalArgumentException("Invalid vertex: " + v);
        }

        public void addResidualFlowTo(int v, int delta) {
            if (v == to) {
                flow += delta;
            } else if (v == from) {
                flow -= delta;
            } else {
                throw new IllegalArgumentException("Invalid vertex: " + v);
            }
        }
    }

    private static class FlowNetwork {

        private int V;

        private List<FlowEdge>[] adj;

        public FlowNetwork(int V) {
            this.V = V;
            adj = (List<FlowEdge>[]) new List[V];
            for (int i = 0; i < adj.length; i++) {
                adj[i] = new ArrayList<>();
            }
        }

        public void addEdge(FlowEdge edge) {
            int from = edge.from;
            int to = edge.other(from);
            adj[from].add(edge);
            adj[to].add(edge);
        }

        public int V() {
            return V;
        }

        public Iterable<FlowEdge> adj(int v) {
            return adj[v];
        }

    }

    public static void main(String[] args) {

        String teamFile = "/Users/pgopalakrishnan/work/learn/workspace/alg/baseball/random/teams10.txt";
        BaseballElimination division = new BaseballElimination(teamFile);
        // System.out.println(division.certificateOfElimination("Atlanta"));
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            } else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
