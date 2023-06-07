import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Graph {
    int vertexCt;  // Number of vertices in the graph.
    int[][] capacity;  // Adjacency matrix
    int[][] residual; // Residual matrix
    boolean[] visited; // Array of visited network nodes
    int[] pred; // Array of parents
    int s, t; // Source & sink values
    ArrayList<String> pathList; // Path list for paths of network
    String graphName;  //The file from which the graph was created.

    /**
     * Graph Obj Constructor
     * @param filename file from which the graph was created
     */
    public Graph(String filename) {
        this.vertexCt = 0;
        this.graphName = filename;
        makeGraph();
    }

    /**
     * Creates a new edge-arc for the network flow
     * @param source Starting node of arc
     * @param destination Ending node of arc
     * @param cap Flow capacity of arc
     * @return Boolean for successful or unsuccessful edge arc creation
     */
    public boolean addEdge(int source, int destination, int cap) {
        if (source < 0 || source >= vertexCt) return false;
        if (destination < 0 || destination >= vertexCt) return false;
        capacity[source][destination] = cap;
        return true;
    }

    /**
     * Processes graph object for print readout
     * @return A processed string
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        // Prints the original graph
        sb.append("\nThe Graph " + graphName + " \n");
        for (int i = 0; i < vertexCt; i++) {
            for (int j = 0; j < vertexCt; j++) { sb.append(String.format("%5d", capacity[i][j])); }
            sb.append("\n");
        }
        // Prints the paths
        sb.append(printPaths());
        // Prints the edge flows
        sb.append(printFlows());
        return sb.toString();
    }

    /**
     * Creates arrays, reads file, and finishes initialization of Graph object
     */
    public void makeGraph() {
        try {
            Scanner reader = new Scanner(new File(graphName));
            vertexCt = reader.nextInt();
            capacity = new int[vertexCt][vertexCt];
            residual = new int[vertexCt][vertexCt];
            pathList = new ArrayList<>();
            pred = new int[vertexCt];

            this.s = 0;
            this.t = vertexCt - 1;

            for (int i = 0; i < vertexCt; i++) {
                for (int j = 0; j < vertexCt; j++) {
                    capacity[i][j] = 0;
                }
            }
            while (reader.hasNextInt()) {
                int v1 = reader.nextInt();
                int v2 = reader.nextInt();
                int cap = reader.nextInt();
                if (!addEdge(v1, v2, cap))
                    throw new Exception();
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Ford-Fulkerson Flow algorithm based on Edmonds-Karp augmented path algorithm
     */
    public void maxFlow(){
        int v, w; // Predecessor & successor

        // Syncs the residual graph w/ the original capacity graph
        for (w = 0; w < vertexCt; w++) {
            for (v = 0; v < vertexCt; v++) { residual[w][v] = capacity[w][v]; }
        }

        while (hasAugmentingPath()) {
            StringBuilder pathString = new StringBuilder();
            pathString.insert(0, t + " ");
            double availableFlow = 1000000; // Arbitrarily large number to represent infinity

            // Calculate the residual flow & capacity
            for (v = t; v != s; v = pred[v]) {
                w = pred[v];
                availableFlow = Math.min(availableFlow, residual[w][v]);
                pathString.insert(0, w + " ");
            }
            pathString.append("(flow " + (int)availableFlow + ")");
            pathString.insert(0, "Path ");
            pathList.add(pathString.toString());

            // Update the residual capacities & reverse the edges of the path
            for (v = t; v != s; v = pred[v]) {
                w = pred[v];
                residual[w][v] -= availableFlow;
                residual[v][w] += availableFlow;
            }
        }
    }

    /**
     * Edmonds-Karp algorithm for testing if a network has an augmenting path
     * @return Boolean of augmenting path availability
     */
    private boolean hasAugmentingPath(){
        Queue<Integer> q = new Queue<>();
        clearVisited();
        q.enqueue(s);
        visited[s] = true; // Mark the source as visited
        pred[s] = -1; // Set the predecessor of the sink to be an impossible number

        // Processes potential augmented paths according to Edmonds-Karp Algorithm
        while (!q.isEmpty() && !visited[t]) {
            int v = q.dequeue();
            for (int i = 0; i < vertexCt; i++) {
                if (!visited[i] && residual[v][i] > 0) {
                    if (i == t) {
                        pred[i] = v;
                        return true;
                    }
                    q.enqueue(i);
                    pred[i] = v;
                    visited[i] = true;
                }
            }
        }
        return false;
    }

    /**
     * Helper function to concatenate the path list for screen readout
     * @return Concatenated path list string
     */
    private String printPaths(){
        StringBuilder returnString = new StringBuilder();
        returnString.append("\nPaths found in order \n");
        for (String path : pathList) {
            returnString.append(path);
            returnString.append("\n");
        }
        return returnString.toString();
    }

    /**
     * Helper function to format the edge-flow list for screen readout
     * @return Formatted list of edge flows
     */
    private String printFlows(){
        StringBuilder sb = new StringBuilder();
        sb.append("\nFinal flow on each edge\n");
        for (int i = 0; i < vertexCt; i++) {
            for (int j = 0; j < vertexCt; j++) {
                if (residual[j][i] != 0 && i < j) { sb.append("Flow " + i + "->" + j + "(" + residual[j][i] +")\n"); }
            }
        }
        return sb.toString();
    }

    /**
     * Clears the visited array & sets all positions to false
     */
    private void clearVisited() {
        visited = new boolean[vertexCt];
        for (int i = 0; i < vertexCt; i++) { visited[i] = false; }
    }

    public static void main(String[] args) {
        Graph graph0 = new Graph("match0.txt");
        graph0.maxFlow();
        System.out.println(graph0.toString());
        Graph graph1 = new Graph("match1.txt");
        graph1.maxFlow();
        System.out.println(graph1.toString());
        Graph graph2 = new Graph("match2.txt");
        graph2.maxFlow();
        System.out.println(graph2.toString());
        Graph graph3 = new Graph("match3.txt");
        graph3.maxFlow();
        System.out.println(graph3.toString());

    }
}