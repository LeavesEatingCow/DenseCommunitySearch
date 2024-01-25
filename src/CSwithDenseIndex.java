import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.SimpleGraph;

import java.io.IOException;
import java.util.*;
//community search methods with index
// rooted tree, mst or original index graph
public class CSwithDenseIndex {

    //community search for given query
    public static LinkedList<MyEdge> findkCommunityForQuery(DenseIndex deni, int query, int k, int chose) throws IOException {
        MyGraph SG;
        if (chose == 0) {
            // System.out.println("index graph");
            SG = deni.indexGraph;
        } else {
            // System.out.println("spanning tree graph");
            SG = deni.mstg;
        }
        ArrayList<Integer> qIn = deni.vtoSN.get(query);
        Set<Integer> comSN = new HashSet<Integer>();
        Queue<Integer> ignidq;
        LinkedList<MyEdge> community = new LinkedList<>();
        for (int i = 0; i < qIn.size(); i++) {
            Integer qid = qIn.get(i);
            if (deni.idtoSN.get(qid).density >= k && !comSN.contains(qid)) {
                ignidq = new LinkedList<Integer>();

                ignidq.add(qid);
                comSN.add(qid);
                community.addAll(deni.idtoSN.get(qid).edgelist);

                while (!ignidq.isEmpty()) {
                    Integer ig = ignidq.poll();
                    for (Integer nid : SG.edgesOf(ig).keySet()) {
                        if (deni.idtoSN.get(nid).density >= k && !comSN.contains(nid)) {
                            ignidq.add(nid);
                            comSN.add(nid);
                            community.addAll(deni.idtoSN.get(nid).edgelist);
                        }
                    }
                }

                System.out.print("Number of edges in this community: ");
                System.out.println(community.size());
                break;
            }

        }
        return community;
    }

    //get super nodes of given query nodes
    public static Set<Integer> getQuerSN(DenseIndex deni, int[] query, int k) {
        Set<Integer> Sl = new HashSet<Integer>();
        boolean ad;
//		System.out.print("super nodes of qns:");
        ArrayList<Integer> clq = deni.vtoSN.get(query[0]);
        Sl.add(clq.get(clq.size() - 1));
//		System.out.print(clq.get(clq.size() - 1)+",");
        for (int j = 1; j < query.length; j++) {
            int q = query[j];
            ad = true;
            clq = deni.vtoSN.get(q);
            for (int i = clq.size() - 1; i >= 0; i--) {
                int sn = clq.get(i);
                if (Sl.contains(sn)) {
                    ad = false;
                    break;
                }
                if (deni.idtoSN.get(sn).density < k)
                    break;
            }
            if (ad)
            {
//				System.out.print(clq.get(clq.size() - 1)+",");
                Sl.add(clq.get(clq.size() - 1));
            }
        }
//		System.out.println();
        return Sl;
    }

    //find community with Rooted tree for given query set with max density
    public static int findCwithRT(DenseIndex deni, int[] query, MyGraph comofqq)
            throws NullPointerException {


        int kt, cid, k = 9999;

        for (int q : query) {
            //System.out.println("query is "+q);
            ArrayList<Integer> clq = deni.vtoSN.get(q);
            kt = deni.idtoSN.get(clq.get(clq.size() - 1)).density;
            if (kt < k)
                k = kt;
        }


        Set<Integer> sl = getQuerSN(deni,query, k);

        Set<Integer> Tq = new HashSet<>();
        int minkk = getDensityofQuery(deni, sl, Tq);// max k which connect the query nodes
        Queue<Integer> Sl = new LinkedList<Integer>(Tq);
        HashSet<Integer> qInl = new HashSet<Integer>(Tq);// visited super nodes

        int i, j, kmax;

        while (!Sl.isEmpty()) {
            cid = Sl.poll();
            MyNode vv = deni.indexGraph.sortedgg.get(cid);
            if (vv == null)
                continue;
            j = minkk;
            while (!vv.svp.containsKey(j)) {
                j++;
                if (j > vv.density)
                    break;
            }
            kmax = vv.sorted_nglbt.length;
            if (j > vv.density) {
                i = kmax;
            } else {
                // qInl.add(cid);
                i = vv.svp.get(j);// get position of the truss value j
            }
            // line 9
            int u;

            for (; i < kmax; i++) {
                u = vv.sorted_nglbt[i];
                if (!qInl.contains(u)) {
                    qInl.add(u);
                    Sl.add(u);
                }
                // com.addEdge(cid, u);
            }
        }
        // for (int in : com.vertexSet())

        for (int in : qInl) {
            for (MyEdge e : deni.idtoSN.get(in).edgelist) {
                comofqq.addEdge(e.s, e.t, 1);
            }
        }
        return minkk;
    }

    ////find community with index graph or mst for given query set with max density
    // use index graph if chose=0, use mst if 1
    public static int findCwithIGForQL(DenseIndex deni, int[] query, MyGraph comofqq, int chose) throws Exception {
        MyGraph gg;

        if (chose == 0) {
            // System.out.println("index graph");
            gg = deni.indexGraph;
        } else {
            // System.out.println("spanning tree graph");
            gg = deni.mstg;
        }
        Queue<Integer> Sk = new LinkedList<Integer>();// to visit super
        // nodes
        HashSet<Integer> qInl = new HashSet<Integer>();// visited super nodes
        // HashSet<Integer> proes = new HashSet<Integer>();// visited super
        // nodes
        SimpleGraph<Integer, MyEdge> com = new SimpleGraph<>(MyEdge.class);
        int kt, cid, k = 9999;

        for (int q : query) {
            if(!deni.vtoSN.containsKey(q))//if query is 2 truss, it does not have a suoer node.
            {
                System.out.println("2 truss query no community");
                return 0;
            }
            ArrayList<Integer> clq = deni.vtoSN.get(q);

            kt = deni.idtoSN.get(clq.get(clq.size() - 1)).density;
//			System.out.print(clq.get(clq.size() - 1)+","+cciddict.get(clq.get(clq.size() - 1)).truss +",");
            if (kt < k)
                k = kt;
        }
//		System.out.print("super nodes of qnodes:");
        Queue<Integer> Sl = new LinkedList<Integer>();
        HashMap<Integer, Queue<Integer>> Sll = new HashMap<Integer, Queue<Integer>>();
        for (int q : query) {
            ArrayList<Integer> clq = deni.vtoSN.get(q);
            for (int i = clq.size() - 1; i >= 0; i--) {
                int sn = clq.get(i);
                if (deni.idtoSN.get(sn).density >= k ) {
                    if( !com.containsVertex(sn))
                    {
                        Sl.add(sn);
                        com.addVertex(sn);
//					System.out.print(sn+","+cciddict.get(sn).truss +",");
                    }
                    // proes.add(sn);
                } else
                    break;
            }
        }
//		System.out.println();
        Sll.put(k, Sl);
        k++;

        ConnectivityInspector<Integer, MyEdge> ci;
        int j, kmax, l;
        int it = 0;
        do {
            do {
                k--;
                if (k == 2)
                    break;
            } while (!Sll.containsKey(k));

            if (k == 2)
                break;
            Sk = Sll.get(k);
            it++;
            while (!Sk.isEmpty()) {
                it++;
                cid = Sk.poll();
                MyNode vv = deni.indexGraph.sortedgg.get(cid);
                if (vv == null)
                    continue;
                if (qInl.contains(cid) && !vv.svp.containsKey(k))
                    continue;
                j = k;
                while (!vv.svp.containsKey(j)) {
                    j++;
                    if (j > vv.density)
                        break;
                }
                if (j > vv.density) {
                    if (!qInl.contains(cid))
                        qInl.add(cid);
                    l = vv.density;
                    if (Sll.containsKey(l))
                        Sll.get(l).add(cid);
                    else {
                        Sl = new LinkedList<Integer>();
                        Sl.add(cid);
                        Sll.put(l, Sl);// change
                    }
                    continue;
                }
                kmax = j;
                if (qInl.contains(cid)) {
                    kmax++;
                    while (!vv.svp.containsKey(kmax)) {
                        kmax++;
                        if (kmax > vv.density)
                            break;
                    }
                    if (kmax > vv.density)
                        kmax = vv.sorted_nglbt.length;
                    else
                        kmax = vv.svp.get(kmax);// position of the truss value
                    // kmax if it is in the vertex
                    // neighbor list
                } else {
                    kmax = vv.sorted_nglbt.length;
                    qInl.add(cid);
                }
                int i = vv.svp.get(j);// get position of the truss value j
                // line 9
                int u;
                // for creating Sl
                if (i > 0) {
                    l =  deni.idtoSN.get(vv.sorted_nglbt[i - 1]).density;//????? check again
                    if (Sll.containsKey(l))
                        Sll.get(l).add(cid);
                    else {
                        Sl = new LinkedList<Integer>();
                        Sl.add(cid);
                        Sll.put(l, Sl);// change
                    }
                }
                for (; i < kmax; i++) {
                    u = vv.sorted_nglbt[i];
                    if (!com.containsVertex(u)) {
                        com.addVertex(u);
                        Sk.add(u);
                    }
                    com.addEdge(cid, u);
                }
            }
            Sll.remove(k);
            ci = new ConnectivityInspector<Integer, MyEdge>(com);
        } while (!ci.isConnected());
        for (int in : com.vertexSet())
            for (MyEdge e : deni.idtoSN.get(in).edgelist) {
                comofqq.addEdge(e.s, e.t, 1);
            }
        return k;
    }

    // find the path to make the super nodes of query vertex connected. min edge
    // weight on this path is the max truss value of query nodes
    public  static int getDensityofQuery( DenseIndex deni, Set<Integer> Sl,
                                          Set<Integer> Tq) {
        HashMap<Integer, MyTreeNode<Integer>> vt=deni.rt.vtoTreenode;
        Iterator<Integer> si = Sl.iterator();
        int u = si.next();
        Tq.add(u);
        int lca = u;
        int min_k = deni.idtoSN.get(u).density;
        int v = 0;
        int t;
        try {
            while (si.hasNext()) {
                v = si.next();
                if (!Tq.contains(v)) {
                    Tq.add(v);
                    if (min_k > deni.idtoSN.get(v).density)
                        min_k = deni.idtoSN.get(v).density;
                    while (v != u) {
                        if (vt.get(u).level > 0 && vt.get(u).level >= vt.get(v).level) {
                            t = u;
                            u = vt.get(u).getParent().getData();
                            Tq.add(u);
                            if (min_k > deni.idtoSN.get(u).density)
                                min_k = deni.idtoSN.get(u).density;
                        } else {
                            t = v;
                            v = vt.get(v).getParent().getData();
                            if (Tq.contains(v))
                                break;
                            Tq.add(v);
                            if (min_k > deni.idtoSN.get(v).density)
                                min_k = deni.idtoSN.get(v).density;
                        }
                    }
                }
            }
        }
        catch(Exception e)
        {
//			 System.out.println("Warning: Some Other exception");
            min_k=2;
        }
        return min_k;
    }
}
