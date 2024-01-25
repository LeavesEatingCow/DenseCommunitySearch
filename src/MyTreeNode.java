import java.util.ArrayList;
import java.util.List;

public class MyTreeNode<T>{
    private T data = null;
    private List<MyTreeNode<T>> children = new ArrayList<>();
    private MyTreeNode<T> parent = null;
    int level;

    public MyTreeNode(T data) {
        this.data = data;
        level=0;
    }
    public MyTreeNode(T data, MyTreeNode<T> p ) {
        this.data = data;
        level=p.level+1;
        parent=p;
        p.children.add(this);
    }

    public void addChild(MyTreeNode<T> child) {
        child.setParent(this);
        this.children.add(child);
    }

    public MyTreeNode<T> addChild(T data) {
        MyTreeNode<T> newChild = new MyTreeNode<>(data);
        newChild.setParent(this);
        children.add(newChild);
        newChild.level=level+1;
        return newChild;
    }

    public void addChildren(List<MyTreeNode<T>> children) {
        for(MyTreeNode<T> t : children) {
            t.setParent(this);
        }
        this.children.addAll(children);
    }

    public List<MyTreeNode<T>> getChildren() {
        return children;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    private void setParent(MyTreeNode<T> parent) {
        this.parent = parent;
    }

    public MyTreeNode<T> getParent() {
        return parent;
    }
}