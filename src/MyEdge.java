import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

public class MyEdge {
    int s;
    int t;
    int w;
    // int sp;
    public MyEdge() {
        s = 0;
        t = 0;
    }

    public MyEdge(int ss, int tt) {
        s = ss;
        t = tt;
    }
    public MyEdge(int ss, int tt, int w) {
        s = ss;
        t = tt;
        w=w;
    }
}

class MyEdgeComparator implements Comparator<Entry<Integer, MyEdge>> {
    Map<MyEdge, Integer> trussd;

    public MyEdgeComparator(Map<MyEdge, Integer> trussdd) {
        trussd = trussdd;
    }

    public int compare(Entry<Integer, MyEdge> o1, Entry<Integer, MyEdge> o2) {
        int j1 = trussd.get(o1.getValue());
        int j2 = trussd.get(o2.getValue());
        return (j1 < j2 ? -1 : (j1 == j2 ? 0 : 1));
    }

}
