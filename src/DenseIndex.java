import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class DenseIndex {
    MyGraph indexGraph;
    Map<Integer, MyNode> idtoSN;
    MyGraph mstg;
    RootedTree rt;
    Map<Integer, ArrayList<Integer>> vtoSN;// super node list of vertex: keeps a list for each vertex
    // and list keeps super nodes including that vertex

    Map<Integer, LinkedHashSet<MyEdge>> layersofIGE;

    public DenseIndex() {
        vtoSN = new HashMap<Integer, ArrayList<Integer>>();
        idtoSN = new HashMap<Integer, MyNode>();
        indexGraph = new MyGraph();
        layersofIGE = new HashMap<Integer, LinkedHashSet<MyEdge>>();
        mstg = new MyGraph();
        rt=new RootedTree();
    }

    public void constructIndex(Map<Integer, LinkedHashSet<MyEdge>> klistdict, Map<MyEdge, Integer> trussd, MyGraph mg) {
        int snID = 0;// #tree node id
        if (klistdict.containsKey(2)) {
            mg.removeAllEdges(klistdict.get(2));
            klistdict.remove(2);
        }

        MyEdge ek;
        HashSet<Integer> prosNodes;
        Queue<Integer> Sk;

        int kmx = Collections.max(klistdict.keySet());
        for (int t = 3; t <= kmx; t++) {
            if (klistdict.containsKey(t)) {
                LinkedHashSet<MyEdge> Kedgelist = klistdict.get(t);

                while (!Kedgelist.isEmpty()) {
                    prosNodes = new HashSet<Integer>(2 * Kedgelist.size());
                    ek = Kedgelist.iterator().next();
                    Kedgelist.remove(ek);
                    Sk = new LinkedList<Integer>();
                    Integer x = ek.s;
                    Integer y = ek.t;
                    Sk.add(x);
                    Sk.add(y);
                    prosNodes.add(x);
                    prosNodes.add(y);
                    MyNode Ck = new MyNode(t, snID);
                    idtoSN.put(snID, Ck);
                    Ck.addEdge(ek);
                    while (!Sk.isEmpty()) {
                        Integer v = Sk.poll();
                        addcomVertex(v, snID);
                        MyNode mv = mg.sortedgg.get(v);//mv included edges of v sorted with their truss values
                        int sp = mv.svp.get(t);//get the starting point of the neighbours with density value t
                        int ep = getEndPoint(mv, t);////get the ending point of the neighbours with density value t
                        for (int i = sp; i < ep; i++) {
                            int ne = mv.sorted_nglbt[i];
                            MyEdge e1 = mg.getEdge(v, ne);//here we are looking just connectivity
                            if (Kedgelist.contains(e1)) {
                                Kedgelist.remove(e1);
                                Ck.addEdge(e1);
                                if (!prosNodes.contains(ne)) {
                                    Sk.add(ne);
                                    prosNodes.add(ne);
                                }
                            }
                        }
                    }
                    snID++;
                }
            }
        }
        addSuperEdges();
        constructMSTGraph();
        rt.rootedTree(mstg);
        //indexGraph.sortEdgesBOWeight();
        //  mstg.sortEdgesBOWeight();
    }

    private void addSuperEdges() {
        ArrayList<Integer> Snl;
        for (Entry<Integer, ArrayList<Integer>> vrt : vtoSN.entrySet()) {
            Snl = vrt.getValue();
            for (int i = 0; i < Snl.size() - 1; i++) {
                for (int j = i + 1; j < Snl.size(); j++) {
                    if (!indexGraph.containsEdge(Snl.get(i), Snl.get(j))) {
                        ArrayList<Integer> au = new ArrayList<Integer>();
                        au.add(vrt.getKey());
                        int min = idtoSN.get(Snl.get(i)).density;
                        if (min > idtoSN.get(Snl.get(j)).density)
                            min = idtoSN.get(Snl.get(j)).density;
                        MyEdge ed = indexGraph.addEdge(Snl.get(i), Snl.get(j), min);
                        if (layersofIGE.containsKey(min)) {
                            layersofIGE.get(min).add(ed);
                        } else {
                            LinkedHashSet<MyEdge> kl = new LinkedHashSet<>();
                            kl.add(ed);
                            layersofIGE.put(min, kl);
                        }
                    }
                }
            }
        }

    }

    private int getEndPoint(MyNode v, int t) {
        while (t < v.density) {

            if (v.svp.containsKey(t + 1))
                break;
            t++;
        }
        if (t == v.density)
            return v.sorted_nglbt.length;
        return v.svp.get(t + 1);
    }

    // add super node tns to the super node list of the vertex x
    private void addcomVertex(int vr, int snn) {
        if (vtoSN.containsKey(vr))
            vtoSN.get(vr).add(snn);
        else {
            ArrayList<Integer> cl = new ArrayList<Integer>();
            cl.add(snn);
            vtoSN.put(vr, cl);
        }
    }

    //construcy maximum spanning tree of index graph
    public void constructMSTGraph() {
        MaximumSpaningTree mst = new MaximumSpaningTree(indexGraph);
        Set<MyEdge> mstes = mst.getSpanningTree(1);
        for (MyEdge e : mstes) {
            mstg.addEdge(e);
        }
    }


    /*public void constructLayers(){
        for (int sn : indexGraph.g.keySet()) {
            for (int ne : indexGraph.g.get(sn).keySet()) {
                MyEdge ed = indexGraph.getEdge(sn, ne);
                if (layersofIGE.containsKey(ed.w)) {
                    layersofIGE.get(ed.w).add(ed);
                } else {
                    LinkedHashSet<MyEdge> kl = new LinkedHashSet<>();
                    kl.add(ed);
                    layersofIGE.put(ed.w, kl);
                }
            }

        }
    }*/

    public void writeIndex(String path) throws IOException {
        FileWriter fileWriter = new FileWriter(path.concat("allcommunities.txt"));
        BufferedWriter bw = new BufferedWriter(fileWriter);
        for (Integer sid : idtoSN.keySet()) {
            MyNode sg = idtoSN.get(sid);
            bw.write("id,");
            bw.write(Integer.toString(sid));
            bw.write(",density,");
            bw.write(Integer.toString(sg.density));
            bw.write("\n");
            ListIterator<MyEdge> listIterator = sg.edgelist.listIterator();
            while (listIterator.hasNext()) {
                MyEdge e = listIterator.next();
                bw.write(Integer.toString(e.s));
                bw.write(",");
                bw.write(Integer.toString(e.t));
                bw.write("\n");
            }
        }
        bw.close();
        fileWriter = new FileWriter(path.concat("ogn_ign_dic.txt"));// original
        // graph
        // nodes to
        // index
        // graph
        // node

        bw = new BufferedWriter(fileWriter);
        bw.write("original_node_id index_graph_node_id\n");
        for (Integer k : vtoSN.keySet()) {
            bw.write(Integer.toString(k));
            bw.write(":");
            for (Integer ign : vtoSN.get(k)) {
                bw.write(Integer.toString(ign));
                bw.write(",");
            }
            bw.write("\n");
        }
        bw.close();
        fileWriter = new FileWriter(path.concat("DenseIndexGraph.txt"));// index
        // graph
        // edge
        // list

        bw = new BufferedWriter(fileWriter);
        for (int v : indexGraph.g.keySet()) {
            for (int n : indexGraph.getAddjList(v))
                bw.write(v + "," + n + "," + ( indexGraph.getEdge(v, n)).w + "\n");
        }
        bw.close();
        fileWriter = new FileWriter(path.concat("mstIndexGraph.txt"));// index
        // graph
        // edge
        // list

        bw = new BufferedWriter(fileWriter);
        for (int v : mstg.g.keySet()) {
            for (int n : mstg.getAddjList(v))
                bw.write(v + "," + n + "\n");
        }
        bw.close();
    }


    public void readIndex(String path, MyGraph mg) throws IOException {
//		tec.visitedVd = new HashMap<Integer, ArrayList<Integer>>();
//		tec.cciddict = new HashMap<Integer, Node>();
        // trussGraph=new HashMap<Integer, Set<Integer>>();
//		?indexGraph = new MyGraph();

        FileReader fileWriter = new FileReader(path.concat("allcommunities.txt"));
        BufferedReader br = new BufferedReader(fileWriter);

        String line = br.readLine();
        String[] sr = line.trim().split(",");
        int id = Integer.parseInt(sr[1]);
        int truss = Integer.parseInt(sr[3]);
        MyNode sg = new MyNode(truss, id);
        while ((line = br.readLine()) != null) {
            sr = line.trim().split(",");
            if (sr[0].equals("id")) {
                idtoSN.put(id, sg);
                id = Integer.parseInt(sr[1]);
                truss = Integer.parseInt(sr[3]);
                sg = new MyNode(truss, id);
            } else {
//				System.out.println(sr[0]+" "+sr[1]);
                if (mg.getEdge(Integer.parseInt(sr[0]), Integer.parseInt(sr[1])) != null) {
                    // MyEdge e = mg.getEdge(Integer.parseInt(sr[0]),
                    // Integer.parseInt(sr[1]));
                    sg.edgelist.add(mg.getEdge(Integer.parseInt(sr[0]), Integer.parseInt(sr[1])));
                }
            }
        }
        idtoSN.put(id, sg);
        br.close();
        System.out.println("com read");
        Set<Integer> sn;
        for (Integer ci : idtoSN.keySet()) {
            sn=new  HashSet<Integer>();
            for (MyEdge e : idtoSN.get(ci).edgelist) {
                if(!sn.contains(e.s))
                {	if (!vtoSN.containsKey(e.s))
                    vtoSN.put(e.s, new ArrayList<Integer>());
                    vtoSN.get(e.s).add(ci);
                    sn.add(e.s);
                }
                if(!sn.contains(e.t))
                {	if (!vtoSN.containsKey(e.t))
                    vtoSN.put(e.t, new ArrayList<Integer>());
                    vtoSN.get(e.t).add(ci);
                    sn.add(e.t);
                }
            }
        }

        System.out.println("dictionary read");
        br = new BufferedReader(new FileReader(path.concat("DenseIndexGraph.txt")));
        int v1, v2;
        int w;
        int kmax=1;
        while ((line = br.readLine()) != null) {
            if (line.equals("vertex"))
                break;
            sr = line.trim().split(",");
            v1 = Integer.parseInt(sr[0]);
            v2 = Integer.parseInt(sr[1]);
            w = Integer.parseInt(sr[2]);
            indexGraph.addEdge(v1, v2, w);
            if(w>kmax)
                kmax=w;
        }
        br.close();
        System.out.println("index graph read");
        br = new BufferedReader(new FileReader(path.concat("mstIndexGraph.txt")));

        while ((line = br.readLine()) != null) {
            if (line.equals("vertex"))
                break;
            sr = line.trim().split(",");
            v1 = Integer.parseInt(sr[0]);
            v2 = Integer.parseInt(sr[1]);

            mstg.addEdge(indexGraph.getEdge(v1, v2));
        }
        br.close();
        // indexGraph.sortEdgesBOWeight();
        //   mstg.sortEdgesBOWeight();
        // constructLayers();
        // constructMSTGraph();
    }


}
