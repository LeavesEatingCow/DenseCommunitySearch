import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import java.lang.reflect.*;
public class kcore {



    private static Map<Integer, Integer> vertexCorecomputation(MyGraph g)
    {
        int degeneracy;
        Map<Integer, Integer> vertexCore;
        vertexCore = new HashMap<>();
        degeneracy = 0;

        int n = g.numberOfVertices();
        int maxDegree = n - 1;
        Set<Integer>[] buckets = (Set<Integer>[]) Array.newInstance(Set.class, maxDegree + 1);
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = new HashSet<>();
        }

        int minDegree = n;
        Map<Integer, Integer> degrees = new HashMap<>();
        for (Integer v : g.getVertexSet()) {
            int d = g.degreeOf(v);
            buckets[d].add(v);
            degrees.put(v, d);
            minDegree = Math.min(minDegree, d);
        }
        while (minDegree < n) {
            Set<Integer> b = buckets[minDegree];
            if (b.isEmpty()) {
                minDegree++;
                continue;
            }

            Integer v = b.iterator().next();
            b.remove(v);
            vertexCore.put(v, minDegree);
            degeneracy = Math.max(degeneracy, minDegree);

            for (Integer u : g.edgesOf(v).keySet()) {
                int uDegree = degrees.get(u);
                if (uDegree > minDegree && !vertexCore.containsKey(u)) {
                    buckets[uDegree].remove(u);
                    uDegree--;
                    degrees.put(u, uDegree);
                    buckets[uDegree].add(u);
                    minDegree = Math.min(minDegree, uDegree);
                }
            }
        }
        return vertexCore;
    }
    public static Map<MyEdge, Integer> edgeCorecomputation(MyGraph g)
    {
        Map<Integer, Integer> vertexCore=vertexCorecomputation(g);
        Map<MyEdge, Integer> EdgeCore;
        EdgeCore=new HashMap<>();
        HashMap<Integer,MyEdge> esv;
        int score;
        for(Integer v:g.getVertexSet())
        {
            esv=g.edgesOf(v);
            for(Integer u:esv.keySet())
            {
                if(!EdgeCore.containsKey(esv.get(u))) {
                    score = vertexCore.get(v);
                    if (score < vertexCore.get(u))
                        score = vertexCore.get(u);
                    EdgeCore.put(esv.get(u),score);
                }
            }
        }
        return EdgeCore;
    }
    public static void write_core(String filename, Map<MyEdge, Integer> edgecore) throws IOException {
        FileWriter fileWriter = new FileWriter(filename);
        BufferedWriter bw = new BufferedWriter(fileWriter);
        for (MyEdge e : edgecore.keySet()) {
            bw.write(Integer.toString(e.s));
            bw.write(",");
            bw.write(Integer.toString(e.t));
            bw.write(",");
            bw.write(Integer.toString(edgecore.get(e)));
            bw.write("\n");

        }
        bw.close();
    }
}
