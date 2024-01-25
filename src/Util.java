import java.io.*;
import java.util.*;

public class Util {
    public static void createKlist(MyGraph g, Map<MyEdge, Integer> densed, Map<Integer, LinkedHashSet<MyEdge>> klistdict, int max) {
        LinkedHashSet<MyEdge> kedgelist;
        System.out.println("max is "+max);
        for (int i = 1; i <= max; i++) {
            kedgelist = new LinkedHashSet<MyEdge>();
            klistdict.put(i, kedgelist);
        }

        System.out.println(klistdict.size()+" after for loop "+densed.keySet().size());

        try
        {
            for (MyEdge e : densed.keySet()) {

                if(e!=null) {
                    //System.out.println(e.s+" "+e.t+" "+e.w);
                    //  System.out.println("key of densed is "+densed.get(e));
                    klistdict.get(densed.get(e)).add(e);

                }
            }
        }

        catch(NullPointerException nle)
        {
            System.out.println("exception occurred in createklist "+nle.getCause());
            System.out.println();
            System.out.println(nle.getLocalizedMessage());
            System.out.println(nle.getMessage());
//    	System.out.println(nle.getCause());


            System.exit(1);
        }
    }

    public static int readDensef(MyGraph g, String denseFN, Map<MyEdge, Integer> densed) throws IOException {
        StringTokenizer st;
        System.out.println("denseFn is "+denseFN);
        FileReader reader = new FileReader(denseFN);
        BufferedReader br = new BufferedReader(reader);
        String line = "";
        int max = 0;
        int t;
        while ((line = br.readLine()) != null) {
            st = new StringTokenizer(line, ",");
            densed.put(g.getEdge(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken())),
                    t = Integer.parseInt(st.nextToken()));
            if (t > max)
                max = t;
        }
        br.close();
        System.out.println("maximum density value: " + max);
        return max;
    }

    public static void createDir(String path) {
        File theDir = new File(path);

        // if the directory does not exist, create it
        if (!theDir.exists()) {
            System.out.println("creating directory: " + path);
            boolean result = false;

            try {
                theDir.mkdir();
                result = true;
            } catch (SecurityException se) {
                // handle it
            }
            if (result) {
                System.out.println("DIR created");
            }
        }
    }

}
