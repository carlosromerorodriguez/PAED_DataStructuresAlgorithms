package business.entities.tables;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;

public class HistogramWindow extends JFrame {
    private final List<String> professions;
    private final List<Integer> hereticCounts;

    public HistogramWindow(List<String> professions, List<Integer> hereticCounts, List<LinkedList<Accused>> accusedList) {
        this.professions = professions;
        this.hereticCounts = hereticCounts;

        setTitle("Histograma de Heréticos por Profesión");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel infoLabel = new JLabel("Para más información, pulsar sobre los gráficos");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        infoLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.add(infoLabel, BorderLayout.NORTH);

        JPanel histogramPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawHistogram(g);
            }
        };

        histogramPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int barIndex = getClickedBarIndex(e.getX());
                if (barIndex != -1) {
                    String profession = professions.get(barIndex);
                    int hereticCount = hereticCounts.get(barIndex);
                    StringBuilder message = new StringBuilder();
                    message.append("<html><body><h2 style='color:#34495E;'>Profesión: ").append(profession).append("</h2>");
                    message.append("<h3 style='color:#34495E;'>Heréticos por Profesión:</h3>");

                    for (LinkedList<Accused> accusedProfession : accusedList) {
                        for (Accused accused : accusedProfession) {
                            if (accused.isHeretic() && accused.getProfession().equalsIgnoreCase(profession)) {
                                message.append("<p><b>Nombre: </b>").append(accused.getName()).append("<br>");
                                message.append("<b>Profesión: </b>").append(accused.getProfession()).append("<br>");
                                message.append("<b>Herético: </b>").append(accused.isHeretic() ? "Sí" : "No").append("</p><br>");
                            }
                        }
                    }

                    JLabel messageLabel = new JLabel(message.toString());
                    messageLabel.setFont(new Font("Arial", Font.PLAIN, 16));
                    messageLabel.setBorder(new EmptyBorder(10, 10, 10, 10));

                    JScrollPane scrollPane = new JScrollPane(messageLabel);
                    scrollPane.setPreferredSize(new Dimension(400, 300));
                    JOptionPane.showMessageDialog(HistogramWindow.this, scrollPane);
                }
            }

            private int getClickedBarIndex(int x) {
                int width = getWidth() - 100;
                int barWidth = (int) ((0.4 * width) / professions.size());
                int barIndex = (x - 50) / (2 * barWidth);
                if (barIndex >= 0 && barIndex < professions.size()) {
                    return barIndex;
                }
                return -1;
            }
        });

        mainPanel.add(histogramPanel, BorderLayout.CENTER);
        add(histogramPanel, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                JOptionPane.showMessageDialog(HistogramWindow.this,
                        "Para más información, pulsar sobre las barras graficadas",
                        "Información",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    private void drawHistogram(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        int width = getWidth() - 100;
        int height = getHeight() - 150;
        int barWidth = (int) ((0.4 * width) / professions.size());

        int maxCount = hereticCounts.stream().max(Integer::compare).orElse(0);

        for (int i = 0; i < professions.size(); i++) {
            int barHeight = (int) ((double) hereticCounts.get(i) / maxCount * height);
            int x = i * (2 * barWidth) + 50;
            int y = (height - barHeight) + 50;

            Color myPersonalizedColor = new Color(0x34495E, true);
            Rectangle2D.Double bar = new Rectangle2D.Double(x, y + 5, barWidth, barHeight);
            g2d.setColor(myPersonalizedColor);
            g2d.fill(bar);
            g2d.setPaint(new GradientPaint(x, y, new Color(0x1ABC9D), x, y + barHeight, new Color(0x3498DB)));
            g2d.fill(bar);

            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 14));

            String profession = professions.get(i);
            int professionX = x + barWidth / 2 - g2d.getFontMetrics().stringWidth(profession) / 2;
            int professionY = height + 80;
            g2d.setColor(myPersonalizedColor);
            g2d.drawString(profession, professionX, professionY + 5);
            g2d.setColor(Color.BLACK);
            g2d.drawString(profession, professionX, professionY);

            String hereticCountText = String.valueOf(hereticCounts.get(i));
            int countX = x + barWidth / 2 - g2d.getFontMetrics().stringWidth(hereticCountText) / 2;
            int countY = y - 10;
            g2d.setColor(myPersonalizedColor);
            g2d.drawString(hereticCountText, countX, countY + 5);
            g2d.setColor(Color.BLACK);
            g2d.drawString(hereticCountText, countX, countY);
        }
    }
}