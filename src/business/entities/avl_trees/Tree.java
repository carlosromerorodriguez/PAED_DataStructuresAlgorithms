package business.entities.avl_trees;

import java.util.List;

public interface Tree <T extends Comparable<T>> {
    void insert(T data);
    boolean isEmpty();
    boolean findCitizenID(int id, boolean delete);
    Node<T> getRoot();
    void searchByExactWeight(double objectWeight);
    void searchFirstLighter(double objectWeight);
    void searchFirstHeavier(double objectWeight);
    List<Citizen> searchByRange(float minWeight, float maxWeight);
}
