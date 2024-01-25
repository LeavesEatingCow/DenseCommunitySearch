import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.alg.util.UnionFind;

public class MaximumSpaningTree {
    private final MyGraph graph;
    double spanningTreeCost = 0;

    public MaximumSpaningTree(MyGraph graph) {
        this.graph = Objects.requireNonNull(graph, "Graph cannot be null");
    }

    public<T extends MyEdge> Set<T> getSpanningTree(int max) {

        UnionFind<Integer> forest = new UnionFind<>(graph.g.keySet());
        ArrayList<T> allEdges = (ArrayList<T>) new ArrayList<>(graph.getEdgeSet());
        if(max==1)//maximum spanning tree
            Collections.sort(allEdges,
                    (edge1, edge2) -> Integer.valueOf(edge2.w).compareTo(edge1.w));
        else//minimum spanning tree
            Collections.sort(allEdges,
                    (edge1, edge2) -> Integer.valueOf( edge1.w).compareTo(edge2.w));

        Set<T> edgeList = new HashSet<>();

        for (T edge : allEdges) {
            int source = edge.s;
            int target = edge.t;
            if (forest.find(source).equals(forest.find(target))) {
                continue;
            }
            forest.union(source, target);
            edgeList.add(edge);
            spanningTreeCost += edge.w;
        }
        return edgeList;
    }
}
