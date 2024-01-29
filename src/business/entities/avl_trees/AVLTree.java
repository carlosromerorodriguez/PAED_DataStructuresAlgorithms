package business.entities.avl_trees;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class AVLTree<T extends Comparable<T>> implements Tree<T> {
    private Node<T> root;

    @Override
    public void insert(T data) {
        root = insert(data, root);
    }

    private Node<T> insert(T data, Node<T> node) {
        if (node == null) { return new Node<>(data); }
        if (data.compareTo(node.getData()) <= 0) {
            node.setLeftChild(insert(data, node.getLeftChild()));
        } else {
            node.setRightChild(insert(data, node.getRightChild()));
        }
        updateHeight(node);
        return applyRotation(node);
    }

    private T getMax(Node<T> node) {
        if (node.getRightChild() != null) {
            return getMax(node.getRightChild());
        }
        return node.getData();
    }

    @Override
    public boolean isEmpty() {
        return root == null;
    }

    @Override
    public boolean findCitizenID(int id, boolean delete) {
        Queue<Node<T>> queue = new LinkedList<>();
        queue.add(root);
        Node<T> nodeToDelete = null;

        if (root == null) {
            return false;
        }

        while (!queue.isEmpty()) {
            Node<T> current = queue.remove();
            Citizen citizen = (Citizen) current.getData();
            if (citizen.getId() == id) {
                nodeToDelete = current;
                break;
            }
            if (current.getLeftChild() != null) {
                queue.add(current.getLeftChild());
            }
            if (current.getRightChild() != null) {
                queue.add(current.getRightChild());
            }
        }
        if (nodeToDelete != null) {
            if (delete) {
                Citizen citizen = (Citizen) nodeToDelete.getData();
                System.out.println(citizen.getNom() + " ha estat transformat en un grill.\n");
                root = delete(nodeToDelete.getData(), id, root);
            }
            return true;
        }
        return false;
    }


    private int compareCitizens(Citizen citizen1, Citizen citizen2) {
        int weightComparison = Integer.compare((int) citizen1.getWeight(), (int) citizen2.getWeight());
        if (weightComparison != 0) {
            return weightComparison;
        }
        return Integer.compare(citizen1.getId(), citizen2.getId());
    }

    public Node<T> delete(T data, int id, Node<T> node) {
        if (node == null) { return null; }

        Citizen citizenData = (Citizen) data;
        Citizen currentNodeCitizen = (Citizen) node.getData();
        int comparisonResult = compareCitizens(citizenData, currentNodeCitizen);

        if (comparisonResult < 0) {
            node.setLeftChild(delete(data, id, node.getLeftChild()));
        } else if (comparisonResult > 0) {
            node.setRightChild(delete(data, id, node.getRightChild()));
        } else {
            if (currentNodeCitizen.getId() == id) {
                if (node.getLeftChild() == null) {
                    return node.getRightChild();
                } else if (node.getRightChild() == null) {
                    return node.getLeftChild();
                }

                node.setData(getMax(node.getLeftChild()));
                node.setLeftChild(delete(node.getData(), ((Citizen) node.getData()).getId(), node.getLeftChild()));
            } else {
                node.setLeftChild(delete(data, id, node.getLeftChild()));
            }
        }
        updateHeight(node);
        return applyRotation(node);
    }

    private Node<T> applyRotation(Node<T> node) {
        int balance = balance(node);
        if (balance > 1) {
            if (balance(node.getLeftChild()) < 0) {
                node.setLeftChild(rotateLeft(node.getLeftChild()));
            }
            return rotateRight(node);
        }
        if (balance < -1) {
            if (balance(node.getRightChild()) > 0) {
                node.setRightChild(rotateRight(node.getRightChild()));
            }
            return rotateLeft(node);
        }
        return node;
    }

    private Node<T> rotateRight(Node<T> node) {
        Node<T> leftNode = node.getLeftChild();
        Node<T> centerNode = leftNode.getRightChild();
        leftNode.setRightChild(node);
        node.setLeftChild(centerNode);
        updateHeight(node);
        updateHeight(leftNode);
        return leftNode;
    }

    private Node<T> rotateLeft(Node<T> node) {
        Node<T> rightNode = node.getRightChild();
        Node<T> centerNode = rightNode.getLeftChild();
        rightNode.setLeftChild(node);
        node.setRightChild(centerNode);
        updateHeight(node);
        updateHeight(rightNode);

        return rightNode;
    }

    private void updateHeight(Node<T> node) {
        int maxHeight = Math.max( height(node.getLeftChild()), height(node.getRightChild()));
        node.setHeight(maxHeight + 1);
    }

    private int balance(Node<T> node) {
        return (node != null) ? (height(node.getLeftChild()) - height(node.getRightChild())) : 0;
    }

    private int height(Node<T> node) {
        return node != null ? node.getHeight() : 0;
    }

    public Node<T> getRoot() {
        return root;
    }
    public void searchByExactWeight(double weight) {
        searchByExactWeight(root, weight);
    }

    private void searchByExactWeight(Node<T> node, double weight) {
        if (node == null) {
            return;
        }
        Citizen citizen = (Citizen) node.getData();
        if (citizen.getWeight() == weight) { System.out.println(citizen); }
        searchByExactWeight((citizen.getWeight() > weight) ? node.getLeftChild() : node.getRightChild(), weight);
    }

    public void searchFirstLighter(double weight) {
        if (root == null) {
            System.out.println("El árbol AVL está vacío.\n");
            return;
        }
        Node<T> lighterNode = searchFirstLighter(root, weight, null);
        System.out.println((lighterNode != null) ? lighterNode.getData() : "(ERROR) No se encontró un habitante más ligero que el objeto.\n");
    }

    private Node<T> searchFirstLighter(Node<T> node, double weight, Node<T> lighterNode) {
        if (node == null) {
            return lighterNode;
        }
        Citizen citizen = (Citizen) node.getData();

        if (citizen.getWeight() < weight) {
            lighterNode = node;
            return searchFirstLighter(node.getRightChild(), weight, lighterNode);
        } else {
            return searchFirstLighter(node.getLeftChild(), weight, lighterNode);
        }
    }


    public void searchFirstHeavier(double weight) {
        if (root == null) {
            System.out.println("El árbol AVL está vacío.\n");
            return;
        }

        Node<T> heavierNode = searchFirstHeavier(root, weight, null);
        if (heavierNode != null) {
            System.out.println("\nS'ha descobert 1 bruixa!");
            System.out.println("\t* " + heavierNode.getData() + "\n");
        } else {
            System.out.println("\n(ERROR) No se encontró un habitante más pesado que el objeto.\n");
        }
    }

    @Override
    public List<Citizen> searchByRange(float minWeight, float maxWeight) {
        List<Citizen> citizens = new ArrayList<>();
        searchByRangeHelper(root, minWeight, maxWeight, citizens);
        return citizens;
    }

    private void searchByRangeHelper(Node<T> root, float minWeight, float maxWeight, List<Citizen> citizens) {
        if (root == null) {
            return;
        }
        Citizen citizen = (Citizen) root.getData();
        if (citizen.getWeight() >= minWeight && citizen.getWeight() <= maxWeight) {
            citizens.add(citizen);
        }
        searchByRangeHelper(root.getLeftChild(), minWeight, maxWeight, citizens);
        searchByRangeHelper(root.getRightChild(), minWeight, maxWeight, citizens);
    }


    private Node<T> searchFirstHeavier(Node<T> node, double weight, Node<T> heavierNode) {
        if (node == null) {
            return heavierNode;
        }
        Citizen citizen = (Citizen) node.getData();

        if (citizen.getWeight() > weight) {
            heavierNode = node;
            return searchFirstHeavier(node.getLeftChild(), weight, heavierNode);
        } else {
            return searchFirstHeavier(node.getRightChild(), weight, heavierNode);
        }
    }
}
