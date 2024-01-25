import java.util.LinkedList;
import java.util.Map;
public class MyNode {
    int density;
    Integer idd;
    LinkedList<MyEdge> edgelist;

    public MyNode(int density, int tnid) {

        this.density = density;
        this.idd = tnid;
        edgelist = new LinkedList<MyEdge>();
    }
    Map<Integer,Integer> svp;
    Integer [] sorted_nglbt;
    public MyNode(Map<Integer,Integer> svpp,Integer [] sorted_nglbtt,int tr){
        svp=svpp;
        sorted_nglbt=sorted_nglbtt;
        density =tr;
    }
    public void addEdge(MyEdge e) {
        edgelist.add(e);
    }

}
