package business.entities.r_trees;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class RTree {
    private static final int MAX_ENTRIES = 4;
    private Node root;
    private final Map<Bardiza, Node> chooseLeafMap;

    public RTree() {
        root = new Node();
        chooseLeafMap = new HashMap<>();
    }

    public void insertNode(Bardiza bardiza) {
        Node leafNode = chooseLeaf(root, bardiza);
        leafNode.getBardizas().add(bardiza);
        leafNode.updateBounds(new Rectangle2D.Double(bardiza.getLatitude(), bardiza.getLength(), bardiza.getSize(), bardiza.getSize()));

        if (leafNode.getBardizas().size() > MAX_ENTRIES) {
            Node[] splitNodes = splitNode(leafNode);
            reAdjustTreeMBRs(leafNode, splitNodes[0], splitNodes[1]);
        } else {
            reAdjustTreeMBRs(leafNode, null, null);
        }
        chooseLeafMap.clear();
    }

    private Node chooseLeaf(Node node, Bardiza bardiza) {
        if (chooseLeafMap.containsKey(bardiza)) {
            return chooseLeafMap.get(bardiza);
        }

        if (node.getChildren().isEmpty()) {
            return node;
        }

        double minAreaIncrease = Double.MAX_VALUE;
        Node chosenChild = null;
        Rectangle2D tempRect = new Rectangle2D.Double();

        for (Node child : node.getChildren()) {
            Rectangle2D bounds = child.getBounds();
            tempRect.setRect(bounds);
            tempRect.add(new Rectangle2D.Double(bardiza.getLatitude(), bardiza.getLength(), bardiza.getSize(), bardiza.getSize()));
            double areaIncrease = tempRect.getWidth() * tempRect.getHeight() - bounds.getWidth() * bounds.getHeight();

            if (areaIncrease < minAreaIncrease) {
                minAreaIncrease = areaIncrease;
                chosenChild = child;
            }
        }

        assert chosenChild != null;
        Node result = chooseLeaf(chosenChild, bardiza);
        chooseLeafMap.put(bardiza, result);
        return result;
    }

    private Node[] splitNode(Node node) {
        Node[] splitNodes = new Node[2];
        splitNodes[0] = new Node();
        splitNodes[1] = new Node();

        // Encuentra las dos entradas más distantes para iniciar los grupos (Método cuadrático)
        double maxD = Double.NEGATIVE_INFINITY;
        int seed1 = 0;
        int seed2 = 0;
        int nodeSize = node.getBardizas().size();

        for (int i = 0; i < nodeSize - 1; i++) {
            for (int j = i + 1; j < nodeSize; j++) {
                Rectangle2D.Double ri = new Rectangle2D.Double(node.getBardizas().get(i).getLatitude(), node.getBardizas().get(i).getLength(), node.getBardizas().get(i).getSize(), node.getBardizas().get(i).getSize());
                Rectangle2D.Double rj = new Rectangle2D.Double(node.getBardizas().get(j).getLatitude(), node.getBardizas().get(j).getLength(), node.getBardizas().get(j).getSize(), node.getBardizas().get(j).getSize());
                Rectangle2D.Double combined = (Rectangle2D.Double) ri.createUnion(rj);
                double d = combined.getWidth() * combined.getHeight() - ri.getWidth() * ri.getHeight() - rj.getWidth() * rj.getHeight();
                if (d > maxD) {
                    maxD = d;
                    seed1 = i;
                    seed2 = j;
                }
            }
        }

        // Asigna las dos entradas más distantes a los grupos
        if (node.getBardizas().size() > seed1 && node.getBardizas().size() > seed2) {
            Bardiza b1 = node.getBardizas().get(seed1);
            Bardiza b2 = node.getBardizas().get(seed2);
            splitNodes[0].getBardizas().add(b1);
            splitNodes[0].updateBounds(new Rectangle2D.Double(b1.getLatitude(), b1.getLength(), b1.getSize(), b1.getSize()));
            splitNodes[1].getBardizas().add(b2);
            splitNodes[1].updateBounds(new Rectangle2D.Double(b2.getLatitude(), b2.getLength(), b2.getSize(), b2.getSize()));
            node.getBardizas().remove(seed2);
            node.getBardizas().remove(seed1);
        }

        // Asigna las entradas restantes al grupo que tenga el menor aumento de área
        while (!node.getBardizas().isEmpty()) {
            maxD = Double.NEGATIVE_INFINITY;
            int selectedGroup = -1;
            int selectedIndex = -1;
            Rectangle2D.Double tempRect = new Rectangle2D.Double();

            for (int i = 0; i < node.getBardizas().size(); i++) {
                Bardiza b = node.getBardizas().get(i);
                for (int j = 0; j < 2; j++) {
                    tempRect.setRect(splitNodes[j].getBounds());
                    tempRect.add(new Rectangle2D.Double(b.getLatitude(), b.getLength(), b.getSize(), b.getSize()));
                    double d = tempRect.getWidth() * tempRect.getHeight() - splitNodes[j].getBounds().getWidth() * splitNodes[j].getBounds().getHeight();

                    if (d > maxD) {
                        maxD = d;
                        selectedGroup = j;
                        selectedIndex = i;
                    }
                }
            }

            Bardiza selectedBardiza = node.getBardizas().get(selectedIndex);
            splitNodes[selectedGroup].getBardizas().add(selectedBardiza);
            splitNodes[selectedGroup].updateBounds(new Rectangle2D.Double(selectedBardiza.getLatitude(), selectedBardiza.getLength(), selectedBardiza.getSize(), selectedBardiza.getSize()));
            node.getBardizas().remove(selectedIndex);
        }

        // Actualiza los nodos hijos de los nuevos nodos divididos
        if (!node.getChildren().isEmpty()) {
            List<Node> remainingChildren = new ArrayList<>(node.getChildren());
            node.getChildren().clear();

            for (Node child : remainingChildren) {
                int bestGroup = 0;
                double bestEnlargement = Double.POSITIVE_INFINITY;

                for (int j = 0; j < 2; j++) {
                    Rectangle2D.Double currentBounds = (Rectangle2D.Double) splitNodes[j].getBounds().clone();
                    currentBounds.add(child.getBounds());
                    double enlargement = currentBounds.getWidth() * currentBounds.getHeight() - splitNodes[j].getBounds().getWidth() * splitNodes[j].getBounds().getHeight();

                    if (enlargement < bestEnlargement) {
                        bestEnlargement = enlargement;
                        bestGroup = j;
                    }
                }

                splitNodes[bestGroup].getChildren().add(child);
                child.setParent(splitNodes[bestGroup]);
                splitNodes[bestGroup].updateBounds(child.getBounds());
            }
        }

        return splitNodes;
    }


    private void reAdjustTreeMBRs(Node node, Node newChild1, Node newChild2) {
        if (node == root) {
            if (newChild1 != null && newChild2 != null) {
                root = new Node();
                root.getChildren().add(newChild1);
                root.getChildren().add(newChild2);
                newChild1.setParent(root);
                newChild2.setParent(root);
                root.updateBounds(newChild1.getBounds());
                root.updateBounds(newChild2.getBounds());
            }
            return;
        }

        Node parent = node.getParent();
        if (newChild1 != null && newChild2 != null) {
            parent.getChildren().remove(node);
            parent.getChildren().add(newChild1);
            parent.getChildren().add(newChild2);
            newChild1.setParent(parent);
            newChild2.setParent(parent);

            parent.updateBounds(newChild1.getBounds());
            parent.updateBounds(newChild2.getBounds());
        } else {
            parent.updateBounds(node.getBounds());
        }

        if (parent.getChildren().size() > MAX_ENTRIES) {
            Node[] splitNodes = splitNode(parent);
            reAdjustTreeMBRs(parent, splitNodes[0], splitNodes[1]);
        } else {
            reAdjustTreeMBRs(parent, null, null);
        }
    }

    public boolean deleteNode(double latitude, double length) {
        Node leafNode = searchNode(root, latitude, length);
        Bardiza bardizaToDelete = null;

        for (Bardiza bardiza : leafNode.getBardizas()) {
            if (bardiza.getLatitude() == latitude && bardiza.getLength() == length) {
                bardizaToDelete = bardiza;
                break;
            }
        }

        if (bardizaToDelete != null) {
            leafNode.getBardizas().remove(bardizaToDelete);
            updateMBRsAfterDelete(leafNode, bardizaToDelete);
            return true;
        } else {
            return false;
        }
    }

    private Node searchNode(Node node, double latitude, double length) {
        if (node.isLeaf()) { return node; }

        for (Node childNode : node.getChildren()) {
            if (childNode.getBounds().contains(latitude, length)) {
                return searchNode(childNode, latitude, length);
            }
        }
        return node;
    }

    private void updateMBRsAfterDelete(Node node, Bardiza deletedBardiza) {
        while (node != null) {
            Rectangle2D oldBounds = node.getBounds();
            node.getBardizas().remove(deletedBardiza);
            Rectangle2D newBounds = node.getMBR();
            node.updateBounds(newBounds);

            //Optimización: si el MBR no ha cambiado, no es necesario seguir subiendo por el árbol
            if (node.getBounds().equals(oldBounds)) { break; }
            node = node.getParent();
        }
    }

    public List<Bardiza> searchByRange(double firstLatitude, double firstLongitude, double secondLatitude, double secondLongitude) {
        Rectangle2D range = new Rectangle2D.Double(firstLatitude, firstLongitude, secondLatitude - firstLatitude, secondLongitude - firstLongitude);
        List<Bardiza> bardizasInRange = new ArrayList<>();
        searchByRangeHelper(root, range, bardizasInRange);
        return bardizasInRange;
    }

    private void searchByRangeHelper(Node root, Rectangle2D range, List<Bardiza> bardizasInRange) {
        if (root.isLeaf()) {
            for (Bardiza bardiza : root.getBardizas()) {
                if (range.contains(bardiza.getLatitude(), bardiza.getLength())) {
                    bardizasInRange.add(bardiza);
                }
            }
        } else {
            for (Node childNode : root.getChildren()) {
                if (range.intersects(childNode.getBounds())) {
                    searchByRangeHelper(childNode, range, bardizasInRange);
                }
            }
        }
    }

    private List<Bardiza> findKNearestBardizas(double latitude, double longitude, int k) {
        PriorityQueue<BardizaDistance> nearestBardizas = new PriorityQueue<>(Comparator.comparingDouble(BardizaDistance::getDistance));

        findKNearestBardizasHelper(root, latitude, longitude, k, nearestBardizas);

        return nearestBardizas.stream()
                .map(BardizaDistance::getBardiza)
                .collect(Collectors.toList());
    }

    private void findKNearestBardizasHelper(Node node, double latitude, double longitude, int k, PriorityQueue<BardizaDistance> nearestBardizas) {
        if (node == null) {
            return;
        }

        if (node.isLeaf()) {
            for (Bardiza bardiza : node.getBardizas()) {
                double distance = distance(latitude, longitude, bardiza.getLatitude(), bardiza.getLength());
                BardizaDistance bardizaDistance = new BardizaDistance(bardiza, distance);
                nearestBardizas.add(bardizaDistance);

                if (nearestBardizas.size() > k) {
                    nearestBardizas.poll();
                }
            }
        } else {
            for (Node childNode : node.getChildren()) {
                if (childNode.getBounds().contains(latitude, longitude) || nearestBardizas.size() < k) {
                    findKNearestBardizasHelper(childNode, latitude, longitude, k, nearestBardizas);
                }
            }
        }
    }

    public String getMajorityType(double latitude, double longitude, int k) {
        List<Bardiza> kNearestBardizas = findKNearestBardizas(latitude, longitude, k);
        Map<String, Integer> typeCount = new HashMap<>();

        for (Bardiza bardiza : kNearestBardizas) {
            String type = bardiza.getType();
            typeCount.put(type, typeCount.getOrDefault(type, 0) + 1);
        }

        return Collections.max(typeCount.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    public String getMediumColor(double latitude, double longitude, int k) {
        List<Bardiza> kNearestBardizas = findKNearestBardizas(latitude, longitude, k);
        int red = 0, green = 0, blue = 0;

        for (Bardiza bardiza : kNearestBardizas) {
            Color color = Color.decode(bardiza.getColor());
            red += color.getRed();
            green += color.getGreen();
            blue += color.getBlue();
        }
        red /= k; green /= k; blue /= k;
        return String.format("#%02x%02x%02x", red, green, blue);
    }

    private double distance(double x1, double y1, double x2, double y2) {
        double distX = x2 - x1;
        double distY = y2 - y1;
        return Math.sqrt(distX * distX + distY * distY); // Distancia euclidiana
    }

    public void showRTreeInVertical() {
        StringBuilder sb = new StringBuilder();
        if (root == null) {
            sb.append("Empty tree");
        } else {
            sb.append(formatMBR(root.getMBR())).append("\n");
            List<Node> children = root.getChildren();
            for (int i = 0; i < children.size(); i++) {
                Node child = children.get(i);
                boolean isLastChild = i == children.size() - 1;
                if (child.getMBR().x < 1797693134 || child.getMBR().y < 1797693134) {
                    sb.append(isLastChild ? "'--- " : "|--- ").append("Level 1 Node ").append(formatMBR(child.getMBR())).append("\n");
                }
                String newPrefix = isLastChild ? "     " : "|   ";
                if (!child.isLeaf()) {
                    children = child.getChildren();
                    i = -1;
                } else {
                    for (Bardiza bardiza : child.getBardizas()) {
                        sb.append(newPrefix).append("|--- ").append("Bardiza: ").append(bardiza).append("\n");
                    }
                }
            }
        }
        System.out.println(sb);
    }

    private String formatMBR(Rectangle2D.Double mbr) {
        return String.format("MBR [X=%.12f, Y=%.12f, Width=%.12f, Height=%.12f]", mbr.x, mbr.y, mbr.width, mbr.height);
    }
}