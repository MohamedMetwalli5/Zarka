import java.util.Date;

class Node {
    int  height;
    String key,value;
    Node left, right;

    Node(String key,String value) {
        this.key = key;
        this.value = value;
        this.height = 1;
    }
}

// Tree class
class AVLTree {
    Node root;
    int elemnts = 0;

    int height(Node N) {
        if (N == null)
            return 0;
        return N.height;
    }

    int max(int a, int b) {
        return Math.max(a, b);
    }

    Node rightRotate(Node y) {
        Node x = y.left;
        Node T2 = x.right;
        x.right = y;
        y.left = T2;
        y.height = max(height(y.left), height(y.right)) + 1;
        x.height = max(height(x.left), height(x.right)) + 1;
        return x;
    }

    Node leftRotate(Node x) {
        Node y = x.right;
        Node T2 = y.left;
        y.left = x;
        x.right = T2;
        x.height = max(height(x.left), height(x.right)) + 1;
        y.height = max(height(y.left), height(y.right)) + 1;
        return y;
    }

    // Get balance factor of a node
    int getBalanceFactor(Node N) {
        if (N == null)
            return 0;
        return height(N.left) - height(N.right);
    }

    // Insert a node
    Node insertNode(Node node, String key, String value) {
        // Find the position and insert the node
        if (node == null) {
            elemnts++;
            return (new Node(key, value));
        }
        if (key.compareTo(node.key) < 0)
            node.left = insertNode(node.left, key, value);
        else if (key.compareTo(node.key) > 0)
            node.right = insertNode(node.right, key, value);
        else {
            node.key = key;
            node.value=value;
            return node;
        }

        // Update the balance factor of each node
        // And, balance the tree
        node.height = 1 + max(height(node.left), height(node.right));
        int balanceFactor = getBalanceFactor(node);
        if (balanceFactor > 1) {
            if (key.compareTo(node.left.key) < 0) {
                return rightRotate(node);
            } else if (key.compareTo(node.left.key) > 0) {
                node.left = leftRotate(node.left);
                return rightRotate(node);
            }
        }
        if (balanceFactor < -1) {
            if (key.compareTo(node.right.key) > 0) {
                return leftRotate(node);
            } else if (key.compareTo(node.right.key) < 0) {
                node.right = rightRotate(node.right);
                return leftRotate(node);
            }
        }
        return node;
    }

    Node nodeWithMimumValue(Node node) {
        Node current = node;
        while (current.left != null)
            current = current.left;
        return current;
    }

    // Delete a node
    Node deleteNode(Node root, String key) {

        // Find the node to be deleted and remove it
        if (root == null)
            return root;
        if (key.compareTo(root.key) < 0)
            root.left = deleteNode(root.left, key);
        else if (key.compareTo(root.key) > 0)
            root.right = deleteNode(root.right, key);
        else {
            if ((root.left == null) || (root.right == null)) {
                Node temp = null;
                if (temp == root.left)
                    temp = root.right;
                else
                    temp = root.left;
                if (temp == null) {
                    temp = root;
                    root = null;
                } else
                    root = temp;
                elemnts--;
            } else {
                Node temp = nodeWithMimumValue(root.right);
                root.key = temp.key;
                root.right = deleteNode(root.right, temp.key);
            }
        }
        if (root == null)
            return root;

        // Update the balance factor of each node and balance the tree
        root.height = max(height(root.left), height(root.right)) + 1;
        int balanceFactor = getBalanceFactor(root);
        if (balanceFactor > 1) {
            if (getBalanceFactor(root.left) >= 0) {
                return rightRotate(root);
            } else {
                root.left = leftRotate(root.left);
                return rightRotate(root);
            }
        }
        if (balanceFactor < -1) {
            if (getBalanceFactor(root.right) <= 0) {
                return leftRotate(root);
            } else {
                root.right = rightRotate(root.right);
                return leftRotate(root);
            }
        }
        return root;
    }

    public Node find(String key){
        Node temp = root;
        while (temp != null && temp.key != key){
            if(temp.key.compareTo(key) > 0){
                temp = temp.left;
                continue;
            }
            else
                temp = temp.right;
        }
        return temp;
    }

    boolean insert(String key,String value){
        root = insertNode(root, key, value);
        return true;
    }
    static void print2DUtil(Node root, int space) {
        // Base case
        if (root == null)
            return;

        // Increase distance between levels
        space += 10;

        // Process right child first
        print2DUtil(root.right, space);

        // Print current node after space
        // count
        System.out.print("\n");
        for (int i = 10; i < space; i++)
            System.out.print(" ");
        System.out.print(root.key + "\n");

        // Process left child
        print2DUtil(root.left, space);
    }

    // Wrapper over print2DUtil()
    static void print2D(Node root) {
        // Pass initial space count as 0
        print2DUtil(root, 0);
    }

}