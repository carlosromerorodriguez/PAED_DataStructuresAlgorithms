package business.entities.avl_trees;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class AVLTreePanel<T extends Comparable<T>> extends JPanel {
    private final Tree<T> tree;
    private final int verticalSpacing = 50;
    private final Font nodeFont = new Font("Arial", Font.PLAIN, 14);
    private final List<VisualNode<T>> visualNodes = new ArrayList<>();

    public AVLTreePanel(Tree<T> tree) {
        this.tree = tree;
        setBackground(new Color(166, 221, 221));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for (VisualNode<T> node : visualNodes) {
                    if (node.contains(e.getPoint())) {
                        JOptionPane.showMessageDialog(null, node.getNode().getData().toString(), "Informació del ciudadà", JOptionPane.INFORMATION_MESSAGE);
                        break;
                    }
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        visualNodes.clear();

        if (tree != null && !tree.isEmpty()) {
            drawTree(g, tree.getRoot(), getWidth() / 2, verticalSpacing, getWidth() / 4);
        }
        drawInfoMessage(g);
    }

    private void drawInfoMessage(Graphics g) {
        String message = "Per obtenir més informació, premeu sobre els nodes.";
        Font font = new Font("Arial", Font.PLAIN, 14);
        g.setFont(font);
        g.setColor(Color.BLACK);
        FontMetrics metrics = g.getFontMetrics(font);
        int x = (getWidth() - metrics.stringWidth(message)) / 2;
        int y = getHeight() - 25;
        g.drawString(message, x, y);
    }

    private void drawTree(Graphics g, Node<T> node, int x, int y, int xOffset) {
        if (node != null) {
            g.setFont(nodeFont);
            FontMetrics fm = g.getFontMetrics();
            Citizen toDraw = (Citizen) node.getData();
            String nodeText = toDraw.getWeight() + " (" + toDraw.getId() + ")";
            int nodeWidth = fm.stringWidth(nodeText) + 20;
            int nodeHeight = fm.getHeight() + 10;

            g.setColor(new Color(71, 164, 233));
            g.fillRoundRect(x - nodeWidth / 2, y - nodeHeight / 2, nodeWidth, nodeHeight, 10, 10);
            g.setColor(Color.BLACK);
            g.drawRoundRect(x - nodeWidth / 2, y - nodeHeight / 2, nodeWidth, nodeHeight, 10, 10);
            g.drawString(nodeText, x - fm.stringWidth(nodeText) / 2, y + fm.getHeight() / 4);

            VisualNode<T> visualNode = new VisualNode<>(node, x - nodeWidth / 2, y - nodeHeight / 2, nodeWidth, nodeHeight);
            visualNodes.add(visualNode);

            if (node.getLeftChild() != null) {
                g.drawLine(x, y + nodeHeight / 2, x - xOffset, y + verticalSpacing - nodeHeight / 2);
                drawTree(g, node.getLeftChild(), x - xOffset, y + verticalSpacing, xOffset / 2);
            }
            if (node.getRightChild() != null) {
                g.drawLine(x, y + nodeHeight / 2, x + xOffset, y + verticalSpacing - nodeHeight / 2);
                drawTree(g, node.getRightChild(), x + xOffset, y + verticalSpacing, xOffset / 2);
            }
        }
    }
}
