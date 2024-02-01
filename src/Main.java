import com.sun.security.jgss.GSSUtil;

import java.io.*;
import java.util.*;

public class Main {
    //this main file will text community search with indexing
    // based on parameters, it will create index or read index  from file
    // and then create or read query set and get communities for the query sets using index
    public static void main(String[] args) throws Exception {
        String fileName, comfile ,query_file= "";
        String pathtec;
        args= new String[2];
        args[0]="c";
        args[1]="4";
        if (args[0].equals("l"))
        {
            fileName = "Data/com-lj.ungraph.txt";
            query_file="Data/lj_q.txt";
            pathtec = "tecclljfn/";
            comfile = "Data/com-lj.top5000.cmty.txt";
        }
        else if (args[0].equals("d"))
        {
            fileName = "Data/com-dblp.ungraph.txt";
            query_file="Data/dblp_";
            pathtec = "teccldblpsb/";
            comfile = "Data/com-dblp.top5000.cmty.txt";
        }
        else if (args[0].equals("a"))
        {
            fileName = "Data/com-amazon.ungraph.txt";
            query_file="Data/amz_";
            pathtec = "tecclamzsbfn/";
        }
        else if (args[0].equals("o"))
        {
            fileName = "Data/com-orkut.ungraph.txt";
            query_file="Data/orkut_";
            pathtec = "tecclorkfn/";
        }
        else if (args[0].equals("y"))
        {
            fileName = "Data/com-youtube.ungraph.txt";
            query_file="Data/youtube_";
            pathtec = "tecclyt/";
        }
        else if (args[0].equals("f"))
        {
            fileName = "Data/facebook_combined.txt";
            query_file="Data/facebook_";
            pathtec = "tecclfb/";
        }
        else if (args[0].equals("e"))
        {
            fileName = "Data/email-Eu-core.txt";
            query_file="Data/email_";
            pathtec = "tecclemeu/";
        }
        else if (args[0].equals("w"))
        {
            fileName = "Data/wiki-topcats.txt";
            query_file="Data/wiki_";
            pathtec = "tecclwiki/";
        }
        else if (args[0].equals("c"))
        {
            fileName = "Data/CA-CondMat.txt";
            query_file="Data/CondMat_";
            pathtec = "CondMat/";
        }
        else if (args[0].equals("g"))
        {
            fileName = "Data/CA-GrQc.txt";
            query_file="Data/GrQc_";
            pathtec = "GrQc/";
        }



        else {
            fileName = "Data/4area.txt";
            query_file="Data/4area_";
            pathtec = "tecc44/";

        }
        System.out.println("hereeee");
       // System.out.println(args[7]);

        int nu = 1000;// number of node in steriner tree ctc

        int gamma = 3;// paramter in steriner ctc

        boolean minus1 = false;


        int qt, chose, qs, nq, qty;


        String type;




        if (args.length < 2)
            qt = 6;
        else
            qt = Integer.parseInt(args[1]);
        if (args.length < 3)
            chose = 1;
        else
            chose = Integer.parseInt(args[2]);
        if (args.length < 4)
            qs = 5;
        else
            qs = Integer.parseInt(args[3]);
        char read;
        if (args.length < 5)
            read = 'w';
        else
            read = args[4].charAt(0);

        if (args.length < 6)
            nq = 100;
        else
            nq = Integer.parseInt(args[5]);
        if (args.length < 6)
            qty = 0;
        else
            qty = Integer.parseInt(args[6]);
        if (args.length < 7)
            type = "core";
        else
            type = args[7];

        // Set parameters
        nq = 100;
        qs = 2;
        qt = 2;
        type = "truss";


        MyGraph g = new MyGraph();
        MyGraph g_subgraph = new MyGraph();;
        Map<MyEdge, Integer> densed = new HashMap<MyEdge, Integer>(g.numberOfEdge);
        DenseIndex dense;
        Util.createDir(pathtec);
        long startTime = System.nanoTime();
        g.read_GraphEdgelist(fileName, " ");
        long endTime = System.nanoTime();
        int numOfVerticies = g.numberOfVertices();
        long totalTime = 0;
        int  numberOfIterations = 10;
        dense = new DenseIndex();
        String denseFN = "";
        Map<Integer, LinkedHashSet<MyEdge>> klistdict = new HashMap<Integer, LinkedHashSet<MyEdge>>();
        double averageTime = 0.0;


        double sf = 0.1;
        while(sf <= 1.0) {
            System.out.println("sf is " + sf);
            g_subgraph = g.createSubgraph(sf);
            densed = new HashMap<MyEdge, Integer>(g_subgraph.numberOfEdge);
            String sfString = String.format("%.1f", sf);
            String pathtecType = pathtec.concat(sfString + "_" + fileName.substring("Data/".length()));

            if (type.equals("truss")) {
                Truss.computeTruss(g_subgraph, densed, klistdict, 1);//calculate ktruss values
                System.out.println("truss computation time:" + ((endTime - startTime) / (1000000000.0)));
                Truss.write_sup_tr(pathtecType + "_trussd.txt", densed);//write calculated ktruss values to file
                denseFN = pathtecType.concat("_trussd.txt");


            } else if (type.equals("core")) {
                densed = kcore.edgeCorecomputation(g_subgraph); //to calculate kcore values if not calculated before
                kcore.write_core(pathtecType + "_cored.txt", densed);//write calculated kcore values to file
                denseFN = pathtecType.concat("_cored.txt");
            } else {
                denseFN = pathtec.concat(sfString + "_K-ecc_" + fileName.substring("Data/".length()));
                g_subgraph = new MyGraph();
                g_subgraph.read_GraphEdgelist( denseFN, ",");
                System.out.println("This is densedFN: " + denseFN);
            }

            System.out.println("The num of v is: " + numOfVerticies);

            startTime = System.nanoTime();
            int maxk = Util.readDensef(g_subgraph, denseFN, densed);
            Util.createKlist(g_subgraph, densed, klistdict, maxk);
            endTime = System.nanoTime();





            //create index
            if (read == 'w') {

                g_subgraph.sortEdges(densed);
                for(int i = 0; i < numberOfIterations; i++) {
                    startTime = System.nanoTime();
                    dense.constructIndex(klistdict, densed, g_subgraph);
                    endTime = System.nanoTime();
                    totalTime += (endTime - startTime);
                }
                averageTime = (double) totalTime / (numberOfIterations * (1000000000.0));
                StringBuilder existingContent = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new FileReader(denseFN))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        existingContent.append(line).append(System.lineSeparator());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

// Write index creation time at the beginning of the file
                try (PrintWriter writer = new PrintWriter(denseFN)) {
                    writer.println("index creation time:" + averageTime);
                    // Append the existing content back to the file
                    writer.print(existingContent);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                totalTime = 0;
                System.out.println("index creation time:" + ((endTime - startTime) / (1000000000.0)));
                dense.writeIndex(pathtec);
                System.out.println("Dense index was created");
            } else {//read index
                dense.readIndex(pathtec, g);
                g.sortEdges(densed);
                System.out.println("Dense index was read");
            }

            boolean cq=true;// create query set or read from file
            List<int[]> lq = new ArrayList<int[]>(2*nq);//list of query sets
            if (cq)
            {
                for(int i = 0; i < numberOfIterations; i++) {
                    startTime = System.nanoTime();
                    QuerySetCre.createQuerySet(g_subgraph, numOfVerticies, densed, qt, qs, nq, lq);
                    endTime = System.nanoTime();
                    totalTime += (endTime - startTime);
                }
                averageTime = (double) totalTime / (numberOfIterations * (1000000000.0));
                StringBuilder existingContent = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new FileReader(denseFN))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        existingContent.append(line).append(System.lineSeparator());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("This is DenseNF: " + denseFN);

    // Write index creation time at the beginning of the file
                try (PrintWriter writer = new PrintWriter(denseFN)) {
                    writer.println("query time:" + averageTime);
                    // Append the existing content back to the file
                    writer.print(existingContent);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                QuerySetCre.writeQuerySet(lq,  query_file, qs, qt);
            }
            else
                QuerySetCre.readQueryFile(query_file,lq,qs,qt);


            int query[];
            double aig=0;
            int k;
            for (int i = 0; i < nq; i++) {
                query = lq.get(i);
                startTime = System.nanoTime();
                MyGraph com = new MyGraph();
                k=CSwithDenseIndex.findCwithRT(dense, query, com);
                endTime = System.nanoTime();
                aig += (endTime - startTime) / 1000000000.0;
                System.out.println(i + " Indexgraph_community  E " + com.numberOfEdge + " V " + com.g.size() + " "
                        + k + " query_time  " + (endTime - startTime) / 1000000000.0);
            }
            aig=aig/nq;
            System.out.println(aig);

            sf += 0.1;
        }
    }

    private static void read_queryFile(MyGraph g) {
    }







}
