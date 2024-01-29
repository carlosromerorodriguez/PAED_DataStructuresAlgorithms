package business.r_trees;

import business.entities.r_trees.Bardiza;
import business.entities.r_trees.RTree;
import presentation.KeyBoardManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RTreeManager {
    private RTree rTree;

    public RTreeManager(String pathFile) {
        readRTreeFile(pathFile);
    }
    private void readRTreeFile(String pathFile) {
        List<String> input;
        this.rTree = new RTree();

        try (Stream<String> lines = Files.lines(Paths.get(pathFile))) { input = lines.collect(Collectors.toList()); } catch (IOException e) { throw new RuntimeException(e); }
        input.remove(0);
        long startTime = System.nanoTime();
        for (String s : input) {
            String[] parts = s.split(";");
            Bardiza b = new Bardiza(parts[0], Double.parseDouble(parts[1]), Double.parseDouble(parts[2]),Double.parseDouble(parts[3]), parts[4]);
            rTree.insertNode(b);
       }
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        System.out.println("Temps d'inserció: " + duration + " nanosegons");
    }


    public void addPoint() {
        String type = KeyBoardManager.askForType("Tipus de bardissa: ");
        double size = KeyBoardManager.askForPositiveFloat("Mida de la bardissa: ", Float.POSITIVE_INFINITY);
        double latitude = KeyBoardManager.askForFloat("Latitud de la bardissa: ");
        double length = KeyBoardManager.askForFloat("Longitud de la bardissa: ");
        String color = KeyBoardManager.askForColor("Color de la bardissa: ");

        System.out.println("\nUna nova bardissa aparegué a la Bretanya.");
        Bardiza newBardiza = new Bardiza(type, latitude, length, size, color);
        rTree.insertNode(newBardiza);
    }

    public void deletePoint() {
        double latitude = KeyBoardManager.askForFloat("Latitud de la bardissa: ");
        double length = KeyBoardManager.askForFloat("Longitud de la bardissa: ");
        if (rTree.deleteNode(latitude, length)) {
            System.out.println("\nLa bardissa s’ha eliminat, per ser integrada a una tanca.\n");
        } else {
            System.out.println("\nERROR: La bardissa no existeix.\n");
        }
    }

    public void showRTree() {
        System.out.println("Generant la visualització...\n");
        /*RTreeVisualizer visualizer = new RTreeVisualizer(rTree);
        visualizer.showRTree();
         */
        rTree.showRTreeInVertical();
    }

    public void searchByRange() {
        /*
          Todas las bardizas
              53.809420,-4.147590
              53.809510,-4.147510
          Bardiza 1 y 8:
              53.809450,-4.147540
              53.809480,-4.147510
         */
        String firstPoint = KeyBoardManager.askForPoint("Entra el primer punt de l’àrea (lat,long): ");
        String secondPoint = KeyBoardManager.askForPoint("Entra el segon punt de l’àrea (lat,long): ");
        String[] firstParts = firstPoint.split(",");
        String[] secondParts = secondPoint.split(",");
        double firstLatitude = Double.parseDouble(firstParts[0]);
        double firstLongitude = Double.parseDouble(firstParts[1]);
        double secondLatitude = Double.parseDouble(secondParts[0]);
        double secondLongitude = Double.parseDouble(secondParts[1]);

        List<Bardiza> bardizas = rTree.searchByRange(firstLatitude, firstLongitude, secondLatitude, secondLongitude);
        System.out.println("\nLes bardisses que es troben en aquesta àrea són:");
        for (Bardiza b : bardizas) {
            System.out.println(b.toString());
        }
    }
    public void estheticsOptimization() {
        /*
           Ejemplo:
           Latitude: 53.80946
           Longitud: -4.14753
           K: 3
         */
        String point = KeyBoardManager.askForPoint("Entra el punt de consultar (lat,long): ");
        int k = KeyBoardManager.askForPositiveInteger("Entra el nombre de bardisses a considerar (K): ", Integer.MAX_VALUE);
        String[] parts = point.split(",");
        double latitude = Double.parseDouble(parts[0]);
        double longitude = Double.parseDouble(parts[1]);

        System.out.println("Tipus majortari: " + rTree.getMajorityType(latitude, longitude, k));
        System.out.println("Color mitjà: " + rTree.getMediumColor(latitude, longitude, k));
    }
}
