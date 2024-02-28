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
        int KMax = 6;
        String dlim = "\t";
//        args= new String[2];
//        args[0]="l";
//        args[1]="4";
        if (args[0].equals("l"))
        {
            fileName = "Data/com-lj.ungraph.txt";
            query_file="Data/lj_q.txt";
            pathtec = "tecclljfn/";
            comfile = "Data/com-lj.top5000.cmty.txt";
            KMax = 8;
        }
        else if (args[0].equals("d"))
        {
            fileName = "Data/com-dblp.ungraph.txt";
            query_file="Data/dblp_";
            pathtec = "teccldblpsb/";
            comfile = "Data/com-dblp.top5000.cmty.txt";
            KMax = 7;
        }
        else if (args[0].equals("a"))
        {
            fileName = "Data/com-amazon.ungraph.txt";
            query_file="Data/amz_";
            pathtec = "tecclamzsbfn/";
            dlim = " ";
        }
        else if (args[0].equals("o"))
        {
            fileName = "Data/com-orkut.ungraph.txt";
            query_file="Data/orkut_";
            pathtec = "tecclorkfn/";
            KMax = 10;
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
            dlim = " ";
        }
        else if (args[0].equals("g"))
        {
            fileName = "Data/CA-GrQc.txt";
            query_file="Data/GrQc_";
            pathtec = "GrQc/";
            dlim = " ";
        }
        else if (args[0].equals("u"))
        {
            fileName = "Data/uk-2002.txt";
            query_file="Data/uk-2002_";
            pathtec = "teccluk/";
            dlim = " ";
            KMax = 10;
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


        int qt, chose, qs, nq, qty, md;
        double sf;


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
        if (args.length < 8)
            sf = 0.1;
        else
            sf = Double.parseDouble(args[8]);

        // Set parameters
//        nq = 100;
//        qs = 2;
//        qt = 2;
        md = 10;
//        type = "core";


        MyGraph g = new MyGraph();
        MyGraph g_subgraph = new MyGraph();;
        Map<MyEdge, Integer> densed = new HashMap<MyEdge, Integer>(g.numberOfEdge);
        DenseIndex dense;
        Util.createDir(pathtec);
        long startTime = System.nanoTime();
        g.read_GraphEdgelist(fileName, dlim);
        long endTime = System.nanoTime();
        int numOfVerticies = g.numberOfVertices();
        long totalTime = 0;
        int  numberOfIterations = 10;
        dense = new DenseIndex();
        String denseFN = "";
        Map<Integer, LinkedHashSet<MyEdge>> klistdict = new HashMap<Integer, LinkedHashSet<MyEdge>>();
        double averageTime = 0.0;
        int[] qsArray = {2, 3, 5, 8, 16};



        while(sf <= 1.0) {

            if (read == 'w') {
                if (sf > 0.95) {
                    g_subgraph = g;
                } else {
                    g_subgraph = g.createSubgraph(sf);
                }
                densed = new HashMap<MyEdge, Integer>(g_subgraph.numberOfEdge);
                String sfString = String.format("%.1f", sf);
                String pathtecType = pathtec.concat(sfString + "_" + fileName.substring("Data/".length(), fileName.length() - ".txt".length()));

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
                    dlim = ",";
                    g_subgraph.read_GraphEdgelist(denseFN, dlim);
                    System.out.println("This is densedFN: " + denseFN);
                }

                startTime = System.nanoTime();
                int maxk = Util.readDensef(g_subgraph, denseFN, densed);
                Util.createKlist(g_subgraph, densed, klistdict, maxk);
                endTime = System.nanoTime();


                //create index
                    g_subgraph.sortEdges(densed);
                    startTime = System.nanoTime();
                    dense.constructIndex(klistdict, densed, g_subgraph);
                    endTime = System.nanoTime();
//                    StringBuilder existingContent = new StringBuilder();
//                    try (BufferedReader reader = new BufferedReader(new FileReader(denseFN))) {
//                        String line;
//                        while ((line = reader.readLine()) != null) {
//                            existingContent.append(line).append(System.lineSeparator());
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }

// Write index creation time at the beginning of the file
                    try (PrintWriter writer = new PrintWriter(new FileWriter(pathtec + "/IndexTime/" + type + ".txt", true))) {
                        writer.println(sfString);
                        writer.println("index creation time:" + ((endTime - startTime) / (1000000000.0)));
                        // Append the existing content back to the file
//                        writer.print(existingContent);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    totalTime = 0;
                    System.out.println("index creation time:" + ((endTime - startTime) / (1000000000.0)));
                    dense.writeIndex(pathtec, sf, type);
                    System.out.println("Dense index was created");


            } else {//read index
                String sfString = String.format("%.1f", sf);
                String pathtecType = pathtec.concat(sfString + "_" + fileName.substring("Data/".length()));


                if (type.equals("truss")) {
                    denseFN = pathtecType.concat("_trussd.txt");
                } else if (type.equals("core")) {
                    denseFN = pathtecType.concat("_cored.txt");
                } else {
                    denseFN = pathtec.concat(sfString + "_K-ecc_" + fileName.substring("Data/".length()));
                }


                densed = new HashMap<MyEdge, Integer>(g_subgraph.numberOfEdge);
                klistdict = new HashMap<Integer, LinkedHashSet<MyEdge>>();
                dense = new DenseIndex();
                g_subgraph.read_GraphEdgelist(denseFN, ",");


                if (type.equals("truss")) {
                    Truss.computeTruss(g_subgraph, densed, klistdict, 1);
                } else if (type.equals("core")) {
                    densed = kcore.edgeCorecomputation(g_subgraph); //to calculate kcore values if not calculated before
                } else {
                    int maxk = Util.readDensef(g_subgraph, denseFN, densed);
                    Util.createKlist(g_subgraph, densed, klistdict, maxk);
                }


                System.out.println("This is the path " + denseFN);
                dense.readIndex(pathtec, g_subgraph, sf, type);
                g_subgraph.sortEdges(densed);
                System.out.println("Dense index was read");


                boolean cq = true;// create query set or read from file
                List<int[]> lq = new ArrayList<int[]>(2 * nq);//list of query sets
                if (cq) {
                    for (int i = 0; i < numberOfIterations; i++) {
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

                    QuerySetCre.writeQuerySet(lq, query_file, qs, qt);
                } else
                    QuerySetCre.readQueryFile(query_file, lq, qs, qt);

                if (sf > 0.95) {

                    while (md <= 100) {
                        for (int i = 0; i < numberOfIterations; i++) {
                            startTime = System.nanoTime();
                            QuerySetCre.createQuerySet(g_subgraph, qs, lq, nq, query_file, md);
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
                            writer.println("Degree: " + md + " query time:" + averageTime);
                            // Append the existing content back to the file
                            writer.print(existingContent);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        QuerySetCre.writeQuerySet(lq, query_file, qs, qt);
                        md += 10;
                    }

                    int distance = 2;
                    while (distance <= 5) {
                        for (int i = 0; i < numberOfIterations; i++) {
                            startTime = System.nanoTime();
                            QuerySetCre.createQuerySetDynamicDistance(g_subgraph, numOfVerticies, densed, qt, qs, nq,distance , lq);
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
                            writer.println("Distance: " + distance + " query time:" + averageTime);
                            // Append the existing content back to the file
                            writer.print(existingContent);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        QuerySetCre.writeQuerySet(lq, query_file, qs, qt);
                        distance++;
                    }

                    for (int x : qsArray) {
                        for (int i = 0; i < numberOfIterations; i++) {
                            startTime = System.nanoTime();
                            QuerySetCre.createQuerySet(g_subgraph, numOfVerticies, densed, qt, x, nq, lq);
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
                            writer.println("Query Size: " + x + " query time:" + averageTime);
                            // Append the existing content back to the file
                            writer.print(existingContent);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        QuerySetCre.writeQuerySet(lq, query_file, qs, qt);
                    }

                    while (qt <= KMax) {
                        for (int i = 0; i < numberOfIterations; i++) {
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
                            writer.println("k: " + qt + " query time:" + averageTime);
                            // Append the existing content back to the file
                            writer.print(existingContent);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        QuerySetCre.writeQuerySet(lq, query_file, qs, qt);
                        qt++;
                    }


                }


                int query[];
                double aig = 0;
                int k;
                for (int i = 0; i < nq; i++) {
                    query = lq.get(i);
                    startTime = System.nanoTime();
                    MyGraph com = new MyGraph();
                    k = CSwithDenseIndex.findCwithRT(dense, query, com);
                    endTime = System.nanoTime();
                    aig += (endTime - startTime) / 1000000000.0;
                    System.out.println(i + " Indexgraph_community  E " + com.numberOfEdge + " V " + com.g.size() + " "
                            + k + " query_time  " + (endTime - startTime) / 1000000000.0);
                }
                aig = aig / nq;
                System.out.println(aig);


            }
            sf += 0.1;
            System.out.println("This is the file name:" + fileName);
        }
    }

    private static void read_queryFile(MyGraph g) {
    }







}
