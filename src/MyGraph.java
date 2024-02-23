import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class MyGraph {
    Map<Integer, HashMap<Integer, MyEdge>> g;
    // HashMap<Integer, Set<Integer>> sortedg;

    HashMap<Integer, MyNode> sortedgg;
    int numberOfEdge = 0;
    Set<MyEdge> edgelist;

    public MyGraph() {
        g = new HashMap<Integer, HashMap<Integer, MyEdge>>();
        // sortedg = new HashMap<Integer, Set<Integer>>();
        sortedgg = new HashMap<Integer, MyNode>();
        edgelist = new HashSet<MyEdge>();
    }

    public void removeAllEdges(LinkedHashSet<MyEdge> linkedHashSet) {
        for (MyEdge e : linkedHashSet) {
            removeEdge(e);
        }
    }

    public ArrayList<MyEdge> getEdgeSet() {
        ArrayList<MyEdge> es = new ArrayList<>();
        for (int v : g.keySet()) {
            for (MyEdge e : g.get(v).values()) {
                es.add(e);
            }
        }
        return es;
    }


    private int bucketSortEdgeListofV(Integer v, Map<MyEdge, Integer> density, Map<Integer, Integer> svp,
                                      Integer[] sorted_nglbt) {

        Set<Integer> nx = getAddjList(v);
        Map<Integer, Integer> nt = new HashMap<>();
        int kmax = 0;
        int val;
        for (Integer x : nx) {
            val = density.get(getEdge(x, v));
            nt.put(x, val);
            if (val > kmax)
                kmax = val;
        }
        int[] bucket = new int[kmax + 1];
        for (int i = 0; i < bucket.length; i++)
            bucket[i] = 0;

        for (Integer ne : nt.keySet())
            bucket[nt.get(ne)]++;
        int tmp;
        int p = 0;
        for (int j = 0; j < kmax + 1; j++) {
            tmp = bucket[j];
            bucket[j] = p;
            p = p + tmp;
        }
        for (int i = 0; i < sorted_nglbt.length; i++)
            sorted_nglbt[i] = null;

        for (Entry<Integer, Integer> e : nt.entrySet()) {
            sorted_nglbt[bucket[e.getValue()]] = e.getKey();
            if (!svp.containsKey(e.getValue()))
                svp.put(e.getValue(), bucket[e.getValue()]);
            bucket[e.getValue()] = bucket[e.getValue()] + 1;
        }
        bucket = null;
        return kmax;
    }

    public MyEdge addEdge(int x, int y) {
        if (containsEdge(x, y))
            return g.get(x).get(y);
        MyEdge me = new MyEdge(x, y);
        adding(me);
        return me;
    }
    public MyEdge addEdge(int x, int y,int w) {
        if (containsEdge(x, y))
            return g.get(x).get(y);
        MyEdge me = new MyEdge(x, y,w);
        adding(me);
        return me;
    }
    public boolean addEdge(MyEdge e)
    {
        if (containsEdge(e))
            return false;
        else
            adding(e);
        return true;
    }

    private<T extends MyEdge> void adding(T e) {
        if (!g.containsKey(e.s)) {
            HashMap<Integer, MyEdge> nl = new HashMap<Integer, MyEdge>();
            nl.put(e.t, e);
            g.put(e.s, nl);
        } else {
            g.get(e.s).put(e.t, e);
        }
        if (!g.containsKey(e.t)) {
            HashMap<Integer, MyEdge> nl = new HashMap<Integer, MyEdge>();
            nl.put(e.s, e);
            g.put(e.t, nl);
        } else {
            g.get(e.t).put(e.s, e);
        }
        numberOfEdge++;
        edgelist.add(e);
    }
    public boolean containsEdge(int u, int v) {
        return g.containsKey(u) && u != v && g.get(u).containsKey(v);
    }

    public boolean containsEdge(MyEdge ed) {
        return g.containsKey(ed.s) && g.get(ed.s).containsKey(ed.t);
    }
    public boolean containsVertex(int u) {
        if(g.containsKey(u))
            return true;
        return false;
    }
    public int numberOfEdges(int v) {
        return g.get(v).size();
    }
    public int numberOfVertices() {
//        System.out.println("Size:" + g.size());
        return g.size();
    }
    public Set<Integer> getAddjList(int x) {
        if (!g.containsKey(x))
            return (new HashSet());
        return g.get(x).keySet();
    }
    public Set<Integer> getVertexSet(){
        return g.keySet();
    }

    public MyEdge getEdge(int u, int v) {
        return g.get(u).get(v);
    }

    public HashMap<Integer, MyEdge> edgesOf(Integer v) {
        return g.get(v);
    }
    public void sortEdges(Map<MyEdge, Integer> trussd) {
        LinkedHashMap<Integer, MyEdge> se;
        MyNode vv;
        for (int v : g.keySet()) {
            Map<Integer, Integer> svp = new HashMap<Integer, Integer>();
            Integer[] sorted_nglbt = new Integer[numberOfEdges(v)];
            int kmax = bucketSortEdgeListofV(v, trussd, svp, sorted_nglbt);
            vv = new MyNode(svp, sorted_nglbt, kmax);
            sortedgg.put(v, vv);
        }
    }

    public static void reorderEL(MyEdge[] sorted_elbys, Map<MyEdge, Integer> sorted_ep, Map<MyEdge, Integer> supd,
                                 Map<Integer, Integer> svp, MyEdge e1) {
        int val = supd.get(e1);
        int pos1 = sorted_ep.get(e1);
        int cp = svp.get(val);
        if (cp != pos1) {
            MyEdge tmp2 = sorted_elbys[cp];
            sorted_ep.put(e1, cp);
            sorted_ep.put(tmp2, pos1);
            sorted_elbys[pos1] = tmp2;
            svp.put(val, cp + 1);
            sorted_elbys[cp] = e1;
        } else {
            if (sorted_elbys.length > cp + 1 && supd.get(sorted_elbys[cp + 1]) == val)
                svp.put(val, cp + 1);
            else
                svp.put(val, -1);
        }
        if ((!svp.containsKey(val - 1)) || svp.get(val - 1) == -1)
            svp.put(val - 1, cp);
        supd.put(e1, val - 1);
    }

    public static void bucketSortedgeList(int kmax, Map<MyEdge, Integer> sp, MyEdge[] sorted_elbys,
                                          Map<Integer, Integer> svp, Map<MyEdge, Integer> sorted_ep) {
        int[] bucket = new int[kmax + 1];
        for (int i = 0; i < bucket.length; i++)
            bucket[i] = 0;
        for (int v : sp.values())
            bucket[v]++;
        int tmp;
        int p = 0;
        for (int j = 0; j < kmax + 1; j++) {
            tmp = bucket[j];
            bucket[j] = p;
            p = p + tmp;
        }

        for (Entry<MyEdge, Integer> e : sp.entrySet()) {
            sorted_elbys[bucket[e.getValue()]] = e.getKey();
            sorted_ep.put(e.getKey(), bucket[e.getValue()]);
            if (!svp.containsKey(e.getValue()))
                svp.put(e.getValue(), bucket[e.getValue()]);
            bucket[e.getValue()] = bucket[e.getValue()] + 1;
        }
    }


    public void read_GraphEdgelist(String fileName, String dlim) throws IOException {
        g = new HashMap<Integer, HashMap<Integer, MyEdge>>();
        FileReader reader = new FileReader(fileName);
        BufferedReader br = new BufferedReader(reader);
        String line;
//        if(!dlim.equals(",")) {
            br.readLine();
//        }
        while ((line = br.readLine()) != null)
            processLine(line, dlim);
        br.close();
        System.out.println("v" + g.size());
        System.out.println("e" + numberOfEdge);
        // int size = g.size() * 4 + numberOfEdge * 8;
    }


    protected int processLine(String aLine, String delim) {
        StringTokenizer st;
        int id1, id2;
        st = new StringTokenizer(aLine, delim);
        id1 = Integer.parseInt(st.nextToken().trim());
        id2 = Integer.parseInt(st.nextToken().trim());
        if (id1 == id2)
            return 0;
        addEdge(id1, id2);
        return 1;
    }

    public int removeVertex(int s) {
        if (g.containsKey(s)) {
            for (int t : g.get(s).keySet()) {
                edgelist.remove(g.get(t).remove(s));
                numberOfEdge--;
            }
            g.remove(s);
            return 1;
        }
        return 0;
    }

    public int removeEdge(MyEdge e) {
        if (g.get(e.s).containsKey(e.t)) {
            g.get(e.s).remove(e.t);
            g.get(e.t).remove(e.s);
            numberOfEdge--;
            edgelist.remove(e);
            return 1;
        }
        return 0;
    }

    public boolean isConnected() {
        int numVertices = g.size();
        int numEdges = numVertices;

        if (numEdges < (numVertices - 1)) {
            return false;
        }
        if ((numVertices < 2) || (numEdges > ((numVertices - 1) * (numVertices - 2) / 2))) {
            return true;
        }

        Set<Integer> known = new HashSet<>();
        LinkedList<Integer> queue = new LinkedList<Integer>();
        Integer v = g.keySet().iterator().next();

        queue.add(v); // start with node 1
        known.add(v);

        while (!queue.isEmpty()) {
            v = queue.removeFirst();
            for (int v1 : getAddjList(v)) {
                v = v1;
                if (!known.contains(v)) {
                    known.add(v);
                    queue.add(v);
                }
            }
        }
        return known.size() == numVertices;
    }

    public int degreeOf(int v) {
        if(g.containsKey(v))
            return g.get(v).size();
        else
            return 0;
    }
    public int readDensef(String path, Map<MyEdge, Integer> kdense) throws IOException {
        StringTokenizer st;
        FileReader reader = new FileReader(path.concat("dense.txt"));
        BufferedReader br = new BufferedReader(reader);
        String line = "";
        int max=0;
        int t;
        while ((line = br.readLine()) != null) {
            st = new StringTokenizer(line, ",");
            kdense.put(getEdge(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken())),
                    t=Integer.parseInt(st.nextToken()));
            if(t>max)
                max=t;
        }
        br.close();
        return max;
    }

    public MyGraph createSubgraph(double scalingFactor) {
        MyGraph subgraph = new MyGraph();

        // Randomly select s|V(G)| vertices
        Set<Integer> selectedVertices = getRandomVertices(scalingFactor);


        // Add selected vertices to the subgraph
        for (int v : selectedVertices) {
//            subgraph.addEdge(v, v); // add self-loop for the selected vertex
            int cnt = 1;
            for (int neighbor : getAddjList(v)) {
                if (selectedVertices.contains(neighbor)) {


                    subgraph.addEdge(v, neighbor);
                    cnt++;
                }
            }
        }
        return subgraph;
    }

    public Set<Integer> getRandomVertices(double scalingFactor) {
        Set<Integer> selectedVertices = new HashSet<>();
        Random random = new Random();
        int scaledVertices = (int) (scalingFactor * numberOfVertices());

        List<Integer> vertexList = new ArrayList<>(getVertexSet());

        while (selectedVertices.size() < scaledVertices) {
            int randomIndex = random.nextInt(vertexList.size());
            int randomVertex = vertexList.get(randomIndex);
            selectedVertices.add(randomVertex);
            vertexList.remove(randomIndex);  // Remove the selected vertex to avoid duplicates
        }

        return selectedVertices;
    }

}
