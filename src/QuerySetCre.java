import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class QuerySetCre {
    public static int readQueryFile(String qfile, List<int[]> lq, int qs, int d) throws IOException {
        FileReader reader = new FileReader(qfile.concat("qd_" + d + "-qs_" + qs + "-query.txt"));
        BufferedReader br = new BufferedReader(reader);
        String line = "";
        String[] l;
        int[] query;
        int q;
        Set<Integer> qset;
        while ((line = br.readLine()) != null) {
            qset = new HashSet<Integer>();
            l = line.split(",");
            query = new int[l.length];

            int i = 0;
            for (; i < l.length; i++) {
                q = Integer.parseInt(l[i]);
                if (qset.contains(q))
                    break;
                query[i] = q;
                qset.add(q);
            }
            if (i == l.length)
                lq.add(query);
        }
        br.close();
        return lq.size();
    }

    //Create query set based on distance and given density (trussness/kcore/kednge) value  and distance=2
    //qs_size=query set size (100)
    // q_size= how many nodes in a query set
    public static void createQuerySet(MyGraph g, Map<MyEdge, Integer> densed, int q_density, int q_size, int qs_size, List<int[]> lq) {
        System.out.println("creating queryset");
        Random r = new Random();
        int t;
        Set<Integer> qset;
        ArrayList<Integer> pos = new ArrayList<Integer>();
        for (int x = 0; x < qs_size; x++) {
            int[] query = new int[q_size];
            pos = new ArrayList<Integer>();
            int j = 0;
            while (true) {
                int qq = r.nextInt(g.numberOfVertices());// select a node randomly
                //qq=18089;
                qset = new HashSet<Integer>();
                qset.add(qq);
                for (Integer n : g.getAddjList(qq)) {//check whether selected node have an edge density value is larger than given trhreshold
                    if (densed.get(g.getEdge(qq, n)) > q_density) {
                        for (Integer nn : g.getAddjList(n)) {
                            if (densed.get(g.getEdge(n, nn)) > q_density & !qset.contains(nn)) {
                                pos.add(nn);
                                qset.add(nn);
                            }
                        }

                    }
                }

                if (pos.size() >= q_size) {
                    query[j] = qq;
                    j++;
                    while (true) {
                        qq = r.nextInt(pos.size());
                        query[j] = pos.get(qq);
                        j++;
                        if (j == q_size)
                            break;
                    }
                    break;
                }
            }

            lq.add(query);
        }
    }

    public static Set<Integer> chooseQueryVertices(MyGraph g, int numQueryVertices) {
        Set<Integer> queryVertices = new HashSet<>();

        // Check if the graph size is less than the desired number of query vertices
        if (g.getVertexSet().size() <= numQueryVertices) {
            queryVertices.addAll(g.getVertexSet());
        } else {
            Random random = new Random();
            List<Integer> vertexList = new ArrayList<>(g.getVertexSet());

            while (queryVertices.size() < numQueryVertices) {
                int randomIndex = random.nextInt(vertexList.size());
                int randomVertex = vertexList.get(randomIndex);
                queryVertices.add(randomVertex);
                vertexList.remove(randomIndex);  // Remove the selected vertex to avoid duplicates
            }
        }

        return queryVertices;
    }


// create query set for test based on degree of nodes
//qs query size,
//lq : created query set
//path file path to write query set

    public static void createQuerySet(MyGraph g, int qs, List<int[]> lq, int nq, String path, int md)
            throws NumberFormatException, IOException {
        //  ArrayList<Integer> vl=(ArrayList<Integer>) g.sortVetexBOdegree();
        // int s=vl.size()/5;
        // int e=vl.size();
        // int md=g.degreeOf(vl.get(s+1));// min degree of vertex with degree rank 0
        System.out.println("creating queryset" + "qd_" + md + "-qs_" + qs + "-query.txt");

        ArrayList<Integer> vl = new ArrayList<>();
        vl.addAll(g.g.keySet());
        md = 10;// can be given as parameter or use upper commentted lines
        Random r = new Random();
        int qq = 0;
        Set<Integer> qset = new HashSet<Integer>();
        for (int x = 0; x < nq; x++) {
            int[] query = new int[qs];
            qset = new HashSet<Integer>();
            int j = 0;

            do {
                qq = vl.get(r.nextInt(vl.size()));
            } while (g.degreeOf(qq) > md);
            query[j] = qq;
            qset.add(qq);
            j++;
            int l = 0;
            // nl=new ArrayList<>();
            if (qs > 1) {
                //get vertex list which has distance 2 to the first selected query qq
                //nl=g.getDdisneig(qq,d);
                //nl.addAll(g.g.keySet());
                while (true) {
                    qq = vl.get(r.nextInt(vl.size()));
                    if (g.degreeOf(qq) >= md & !qset.contains(qq))//if degree of this vertex is greater than min deg
                    {
                        query[j] = qq;
                        qset.add(qq);
                        j++;
                        if (j == qs)
                            break;
                    }
                    l++;
                    if (l > vl.size() * 10) {
                        //there no query within distance 2 and degree rank 80
                        System.out.println("query not found");
                        break;
                    }
                }
            }
            if (j == qs)
                lq.add(query);
            else
                x--;
        }
        writeQuerySet(lq, path, qs, md);
    }



    public static void writeQuerySet(List<int[]> lq, String path, int qs, int d) throws IOException {

        System.out.println("writing queryset" + "qd_" + d + "-qs_" + qs + "-query.txt");
        FileWriter fileWriter = new FileWriter(path.concat("qd_" + d + "-qs_" + qs + "-query.txt"));
        BufferedWriter bw = new BufferedWriter(fileWriter);
        for (int[] qset : lq) {
            for (int q : qset) {
                bw.write(q + ",");
            }
            bw.write("\n");
        }
        bw.close();
    }
}
