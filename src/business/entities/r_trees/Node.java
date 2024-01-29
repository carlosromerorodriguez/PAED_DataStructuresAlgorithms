package business.entities.r_trees;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class Node {
    private final List<Bardiza> bardizas;
    private final List<Node> children;
    private Rectangle2D bounds;
    private Node parent;

    public Node() {
        bardizas = new ArrayList<>();
        children = new ArrayList<>();
        bounds = new Rectangle2D.Double();
        parent = null;
    }

    public List<Bardiza> getBardizas() {
        return bardizas;
    }

    public List<Node> getChildren() {
        return children;
    }

    public Rectangle2D getBounds() {
        return bounds;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public void updateBounds(Rectangle2D newBounds) {
        if (bounds == null) {
            bounds = new Rectangle2D.Double(newBounds.getX(), newBounds.getY(), newBounds.getWidth(), newBounds.getHeight());
        } else {
            bounds.add(newBounds);
        }
    }

    public boolean isLeaf() {
        return (children.isEmpty());
    }

    public Rectangle2D.Double getMBR() {
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;

        if (isLeaf()) {
            for (Bardiza bardiza : getBardizas()) {
                minX = Math.min(minX, bardiza.getLatitude());
                minY = Math.min(minY, bardiza.getLength());
                maxX = Math.max(maxX, bardiza.getLatitude() + bardiza.getSize());
                maxY = Math.max(maxY, bardiza.getLength() + bardiza.getSize());
            }
        } else {
            for (Node childNode : getChildren()) {
                Rectangle2D.Double childMBR = childNode.getMBR();
                minX = Math.min(minX, childMBR.getMinX());
                minY = Math.min(minY, childMBR.getMinY());
                maxX = Math.max(maxX, childMBR.getMaxX());
                maxY = Math.max(maxY, childMBR.getMaxY());
            }
        }

        return new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("MBR: ")
                .append("X: ").append(getMBR().x)
                .append(", Y: ").append(getMBR().y)
                .append(", Width: ").append(getMBR().width)
                .append(", Height: ").append(getMBR().height)
                .append("\n");

        if (isLeaf()) {
            for (Bardiza bardiza : getBardizas()) {
                stringBuilder.append("    ").append(bardiza).append("\n");
            }
        } else {
            for (Node childNode : getChildren()) {
                stringBuilder.append(childNode.toString());
            }
        }

        return stringBuilder.toString();
    }
}
