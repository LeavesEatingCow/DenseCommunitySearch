import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.StringTokenizer;

public class Truss {


    public static int computeSupport(MyGraph g, Map<MyEdge, Integer> sp) {
        int s = 0;
        int maxs = 0;
        for (MyEdge e : g.edgelist) {
            int src = e.s;
            int dst = e.t;
            s = 0;
            if (g.numberOfEdges(src)> g.numberOfEdges(dst)) {
                src = e.t;
                dst = e.s;
            }
            for (int v : g.getAddjList(src)) {
                if (v != dst) {
                    if (g.containsEdge(v,dst))
                        s++;
                }
            }
            if (s > maxs)
                maxs = s;
            sp.put(e, s);
        }
        return maxs;
    }

    public int computeminTruss(MyGraph g, Map<MyEdge, Integer> sp, int k) {
        int s = 0;
        int min = 100;// ??
        for (MyEdge e : g.edgelist) {
            int src = e.s;
            int dst = e.t;
            s = 2;
            if (g.numberOfEdges(src) > g.numberOfEdges(dst)) {
                src = e.t;
                dst = e.s;
            }
            for (int v : g.getAddjList(src)) {
                if (v != dst) {
                    if (g.containsEdge(dst, v))
                        s++;
                }
                if (s >= k)
                    break;
            }
            if (s >= k)
                continue;
            if (s < min)
                min = s;
            sp.put(e, s);
        }
        return min;
    }
    public static int computeTruss( MyGraph g,Map<MyEdge, Integer> trussd, Map<Integer, LinkedHashSet<MyEdge>> klistdict,
                                    int ckl) {
        LinkedHashSet<MyEdge> kedgelist = new LinkedHashSet<MyEdge>();

        Map<MyEdge, Integer> sp = new HashMap<MyEdge, Integer>();
        int kmax = computeSupport(g,sp);

        int k = 2;
        MyEdge[] sorted_elbys = new MyEdge[sp.size()];

        Map<MyEdge, Integer> sorted_ep = new HashMap<MyEdge, Integer>();
        Map<Integer, Integer> svp = new HashMap<Integer, Integer>();
        g.bucketSortedgeList(kmax, sp, sorted_elbys, svp, sorted_ep);
        // System.out.println("sorted support");
        int min = sp.get(sorted_elbys[0]) + 2;
        for (int i = 0; i < sorted_elbys.length; i++) {
            MyEdge e = sorted_elbys[i];
            int val = sp.get(e);
            if (val > (k - 2)) {
                if (ckl == 1) {
                    klistdict.put(k, kedgelist);
                    kedgelist = new LinkedHashSet<MyEdge>();
                }
                k = val + 2;
            }
            Integer src = e.s;
            Integer dst = e.t;

            if (g.numberOfEdges(src) > g.numberOfEdges(dst)) {
                dst = e.s;
                src = e.t;
            }
            for (Integer v: g.edgesOf(src).keySet()) {
                // for (Integer v : nls) {
                if (g.containsEdge(v,dst)) {
                    MyEdge e1 = g.getEdge(src,dst);
                    MyEdge e2 = g.getEdge(v, dst);
                    if (!(trussd.containsKey(e1) || trussd.containsKey(e2))) {
                        if (sp.get(e1) > (k - 2))
                            g.reorderEL(sorted_elbys, sorted_ep, sp, svp, e1);
                        if (sp.get(e2) > (k - 2))
                            g.reorderEL(sorted_elbys, sorted_ep, sp, svp, e2);
                    }
                }
            }
            if (ckl == 1)
                kedgelist.add(e);
            trussd.put(e, k);
        }
        if (ckl == 1)
            klistdict.put(k, kedgelist);
        return min;
    }

    public static void write_sup_tr(String filename, Map<MyEdge, Integer> trussd) throws IOException {
        FileWriter fileWriter = new FileWriter(filename);
        BufferedWriter bw = new BufferedWriter(fileWriter);
        for (MyEdge e : trussd.keySet()) {
            bw.write(Integer.toString(e.s));
            bw.write(",");
            bw.write(Integer.toString(e.t));
            bw.write(",");
            bw.write(Integer.toString(trussd.get(e)));
            bw.write("\n");

        }
        bw.close();
    }

    public static int readTrussf(MyGraph g,String path, Map<MyEdge, Integer> trussd) throws IOException {
        StringTokenizer st;
        FileReader reader = new FileReader(path.concat("trussd.txt"));
        BufferedReader br = new BufferedReader(reader);
        String line = "";
        int max=0;
        int t;
        while ((line = br.readLine()) != null) {
            st = new StringTokenizer(line, ",");
            trussd.put(g.getEdge(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken())),
                    t=Integer.parseInt(st.nextToken()));
            if(t>max)
                max=t;
        }
        br.close();
        return max;
    }
}
