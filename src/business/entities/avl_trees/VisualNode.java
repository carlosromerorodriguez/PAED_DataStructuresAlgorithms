package business.entities.avl_trees;

import java.awt.*;

public class VisualNode <T extends Comparable<T>> {
    private final Node<T> node;
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    public VisualNode(Node<T> node, int x, int y, int width, int height) {
        this.node = node;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Node<T> getNode() {
        return node;
    }

    public boolean contains(Point p) {
        return ((p.x >= x && p.x <= x + width) && (p.y >= y && p.y <= y + height));
    }
}
