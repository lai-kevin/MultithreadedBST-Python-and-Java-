import java.util.*;

public class BinarySearchTree<T extends Comparable<T>> implements Iterable<T>{
    private String name;
    private BinarySearchTreeNode<T> rootNode;
    private int treeSize = 0;

    public BinarySearchTree(String name){
        this.name = name;
        this.rootNode = null;
    }

    public void addAll(List<T> elements){
        for (int i = 0; i < elements.size(); i++){
            add(elements.get(i));
        }
    }

    private void add(T element){
        rootNode = addAux(rootNode, element, null);
    }

    private BinarySearchTreeNode<T> addAux(BinarySearchTreeNode<T> r, T element, BinarySearchTreeNode<T> parent){
        if (r == null){
            treeSize++;
            return new BinarySearchTreeNode<>(element, parent);
        }
        if (element.compareTo(r.element) < 0) {r.left = addAux(r.left, element , r);}
        else if (element.compareTo(r.element) > 0) {r.right = addAux(r.right, element, r);}

        return r;
    }
    @Override
    public Iterator<T> iterator() {
        Iterator<T> it = new Iterator<T>() {
            BinarySearchTreeNode<T> nextNode = getMinimum2(rootNode);
            @Override
            public boolean hasNext() {
                return nextNode != null;
            }

            @Override
            public T next() {
                BinarySearchTreeNode<T> returnNode = nextNode;
                nextNode = nextNode.getNextNode();
                return returnNode.element;
            }
        };
        return it;
    }

    public BinarySearchTreeNode<T> getMinimum2(BinarySearchTreeNode<T> root){
        if (root == null){
            return null;
        }
        if (root.left == null){
            return root;
        }
        return getMinimum2(root.left);
    }

    public String toString(){
        String result = "[" + name + "] " + printNode(rootNode);
        return result;
    }

    private String printNode(BinarySearchTreeNode<T> node){
        if(node == null){return "";}

        if(node.left == null && node.right == null){
            String result = "" + node.element;
            return result;
        }

        else if(node.left == null && node.right != null){
            String result = "" + node.element + " R:(" + printNode(node.right) + ")";
            return result;
        }

        else if(node.left != null && node.right == null){
            String result = "" + node.element + " L:(" + printNode(node.left) + ")";
            return result;
        }

        String result = "" + node.element + " L:(" + printNode(node.left) + ")" + " R:(" + printNode(node.right) + ")";
        return result;

    }

    public class BinarySearchTreeNode<T extends Comparable<T>> {
        T element;
        BinarySearchTreeNode<T> parent;
        BinarySearchTreeNode<T> left;
        BinarySearchTreeNode<T> right;

        public BinarySearchTreeNode(T element){
            this.element = element;
        }

        public BinarySearchTreeNode(T element, BinarySearchTreeNode<T> parent){
            this.element = element;
            this.parent = parent;
        }

        public BinarySearchTreeNode<T> getMinimum(BinarySearchTreeNode<T> root){
            if (root == null){
                return null;
            }
            if (root.left == null){
                return root;
            }
            return getMinimum(root.left);
        }

        public BinarySearchTreeNode<T> getNextNodeClimb(BinarySearchTreeNode<T> start){
            if (start.parent == null){
                return null;
            }
            BinarySearchTreeNode<T> parent = start.parent;
            if (parent.left != null && parent.left.equals(start)){
                return parent;
            }
            return getNextNodeClimb(parent);
        }


        public BinarySearchTreeNode<T> getNextNode(){
            if (right != null){
                return getMinimum(right);
            }
            return getNextNodeClimb(this);
        }


    }

    public static class MergedList<T extends Comparable<T>>{
        private ArrayList<T> mergedList;
        boolean newVal1 = false;
        boolean newVal2 = false;
        private T val1;
        private T val2;
        private Iterator<T> it1;
        private Iterator<T> it2;
        int doneMergedListSize;
        boolean done = false;



        public MergedList(BinarySearchTree<T> t1, BinarySearchTree<T> t2){
            mergedList = new ArrayList<>();
            it1 = t1.iterator();
            it2 = t2.iterator();
            this.doneMergedListSize = t1.treeSize + t2.treeSize;
        }

        public synchronized void insert(){
            if (val1 != null && val2 == null){
                mergedList.add(val1);
                newVal1 = false;
            }

            else if (val1 == null && val2 != null){
                mergedList.add(val2);
                newVal2 = false;
            }

            else if (val1.compareTo(val2) < 0){
                mergedList.add(val1);
                newVal1 = false;
            }
            else if (val1.compareTo(val2) > 0){
                mergedList.add(val2);
                newVal2 = false;
            }
            else {
                mergedList.add(val1);
                newVal1 = false;
            }

            if (mergedList.size() >= doneMergedListSize){
                done = true;
            }
            //notifyAll();
        }

        public synchronized void give1(){
            if (mergedList.size() >= doneMergedListSize){
                done = true;
            }
            while (newVal1 && !done) try{wait();} catch(InterruptedException ignore){}

            newVal1 = true;
            if (it1.hasNext()){
                val1 = it1.next();
            }
            else {
                val1 = null;
            }

            if ((newVal1 && newVal2)  && !(mergedList.size() >= doneMergedListSize)){
                insert();
            }
            notifyAll();

        }

        public synchronized void give2(){
            if (mergedList.size() >= doneMergedListSize){
                done = true;
            }
            while (newVal2 && !done) try{wait();} catch(InterruptedException ignore){}

            newVal2 = true;
            if (it2.hasNext()){
                val2 = it2.next();
            }
            else {
                val2 = null;
            }

            if ((newVal1 && newVal2) && !(mergedList.size() >= doneMergedListSize)){
                insert();
            }
            notifyAll();
        }

        public List<T> getMergedList(){
            return mergedList;
        }


    }

    public static <T extends Comparable<T>> List<T> merge(BinarySearchTree<T> t1, BinarySearchTree<T> t2){
        MergedList<T> resultList = new MergedList<>(t1,t2);
        Thread thread1 = new Thread(new Runnable(){
            @Override
            public void run() {
                while(!resultList.done){
                    resultList.give1();
                }
            }
        }
        );

        Thread thread2 = new Thread(new Runnable(){
            @Override
            public void run() {
                while(!resultList.done){
                    resultList.give2();
                }
            }
        }
        );
        thread1.start();
        thread2.start();

        try {
            thread1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return resultList.getMergedList();
    }


    public static void main(String... args) {
        // each tree has a name, provided to its constructor
        BinarySearchTree<Integer> t1 = new BinarySearchTree<>("Oak");
        // adds the elements to t1 in the order 5, 3, 0, and then 9
        t1.addAll(Arrays.asList(5, 3, 0, 9));
        BinarySearchTree<Integer> t2 = new BinarySearchTree<>("Maple");
        // adds the elements to t2 in the order 9, 5, and then 10

        t2.addAll(Arrays.asList(9, 5, 10));
        //t2.addAll(Arrays.asList(9, 5, 10));


        System.out.println(t1); // see the expected output for exact format

        t1.forEach(System.out::println); // iteration in increasing order

        System.out.println(t2); // see the expected output for exact format

        t2.forEach(System.out::println); // iteration in increasing order

        BinarySearchTree<String> t3 = new BinarySearchTree<>("Cornucopia");
        t3.addAll(Arrays.asList("coconut", "apple", "banana", "plum", "durian",
                "no durians on this tree!", "tamarind"));
        System.out.println(t3); // see the expected output for exact format

        t3.forEach(System.out::println); // iteration in increasing order

        List<Integer> merged = merge(t1, t2);
        merged.forEach(System.out::println);
        System.out.println(merged);
    }
}
