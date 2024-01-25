import java.util.*;

//import org.jgrapht.graph.SimpleGraph;
// create a rooted tree of a given maximum spanning tree
public class RootedTree {
    HashMap<Integer, MyTreeNode<Integer>> vtoTreenode;
    public RootedTree() {
        vtoTreenode = new HashMap<>();
    }
    public RootedTree(MyGraph mstg) {
        vtoTreenode = new HashMap<>();
        rootedTree(mstg);
    }

    public void rootedTree(MyGraph mstg) {
        Iterator<Integer> it = mstg.g.keySet().iterator();
        ArrayList<MyTreeNode<Integer>> rs = new ArrayList<>();
        // Set<Integer> visited = new HashSet<Integer>();
        Set<Integer> visitedall = new HashSet<Integer>();

        while (it.hasNext()) {
            Integer v = it.next();
            if (visitedall.contains(v))
                continue;
            MyTreeNode<Integer> root = new MyTreeNode<Integer>(v);
            visitedall.add(v);
            createTreeNode(root, visitedall, mstg);
            rs.add(root);
        }
    }

    private void createTreeNode(MyTreeNode<Integer> root, Set<Integer> visitedall, MyGraph mstg) {
        vtoTreenode.put(root.getData(), root);
        for (Integer n : mstg.getAddjList(root.getData())) {
            if (!visitedall.contains(n)) {
                visitedall.add(n);
                createTreeNode(root.addChild(n), visitedall, mstg);
            }
        }
    }




}
