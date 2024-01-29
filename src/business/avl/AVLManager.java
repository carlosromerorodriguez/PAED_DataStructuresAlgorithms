package business.avl;

import business.entities.avl_trees.AVLTree;
import business.entities.avl_trees.AVLTreePanel;
import business.entities.avl_trees.Citizen;
import business.entities.avl_trees.Tree;
import presentation.KeyBoardManager;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

public class AVLManager {
    private final Tree<Citizen> avlTree;
    private final Scanner scanner;
    public AVLManager(String pathFile) {
        this.avlTree = new AVLTree<>();
        this.scanner = new Scanner(System.in);
        readCitizensFromTxt(pathFile);
    }

    private void readCitizensFromTxt(String pathFile) {
        List<Citizen> citizens = new ArrayList<>();

        try (Stream<String> lines = Files.lines(Paths.get(pathFile))) {
            lines.skip(1).forEach(line -> {
                String[] fields = line.split(";");
                if (fields.length == 4) {
                    int id = Integer.parseInt(fields[0]);
                    String nom = fields[1];
                    float weight = Float.parseFloat(fields[2]);
                    String kingdom = fields[3];
                    citizens.add(new Citizen(id, nom, weight, kingdom));
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        citizens.forEach(avlTree::insert);
    }

    public void addHabitant() {
        int id = KeyBoardManager.askForPositiveInteger("Identificador de l’habitant: ", Integer.MAX_VALUE);
        if (this.avlTree.findCitizenID(id, false)) {
            System.out.println("Ja existeix un habitant amb aquest identificador.\n");
            return;
        }

        System.out.print("Nom de l’habitant: ");
        String nom = scanner.nextLine();
        float weight = KeyBoardManager.askForPositiveFloat("Pes de l’habitant: ", Float.MAX_VALUE);
        System.out.print("Regne de l’habitant: ");
        String kingdom = scanner.nextLine();

        Citizen citizen = new Citizen(id, nom, weight, kingdom);
        avlTree.insert(citizen);
        System.out.println(citizen.getNom() + " ens acompanyarà a partir d'ara.\n");
    }

    public void deleteHabitant() {
        int id = KeyBoardManager.askForPositiveInteger("Identificador de l’habitant: ", Integer.MAX_VALUE);
        if (!avlTree.findCitizenID(id, true)) {
            System.out.println("No existeix cap habitant amb aquest identificador.\n");
        }
    }

    public void showAVL() {
        JFrame frame = new JFrame("AVL Tree Visualization");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(new AVLTreePanel<>(this.avlTree));
        frame.pack();
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        System.out.println("¡Llest! Hem obert una finestra a la barra de tasques perquè puguis visualitzar l'arbre AVL. Gaudeix explorant-lo!\n");
    }

    public void searchWitches() {
        System.out.print("Introduce el nombre del objeto: ");
        scanner.nextLine();

        double weight = KeyBoardManager.askForPositiveFloat("Pes de l’habitant: ", Float.MAX_VALUE);

        System.out.print("Introduce el tipo de objeto (DUCK, WOOD, STONE): ");
        String objectType = scanner.next();

        switch (objectType.toUpperCase()) {
            case "DUCK" -> avlTree.searchByExactWeight(weight);
            case "WOOD" -> avlTree.searchFirstLighter(weight);
            case "STONE" -> avlTree.searchFirstHeavier(weight);
            default -> System.out.println("Tipo de objeto no válido. Debe ser DUCK, WOOD o STONE.\n");
        }
    }

    public void searchByRange() {
        float minWeight = KeyBoardManager.askForPositiveFloat("Pes mínim: ", Float.MAX_VALUE);
        float maxWeight = KeyBoardManager.askForPositiveFloat("Pes màxim: ", Float.MAX_VALUE);

        List<Citizen> citizens = avlTree.searchByRange(minWeight, maxWeight);
        if (citizens.isEmpty()) {
            System.out.println("No hi ha cap habitant en aquest rang de pesos.\n");
        } else {
            System.out.println("S'han capturat " + citizens.size() + " bruixes!");
            citizens.forEach(citizen -> System.out.println("\t* " + citizen.toString()));
            System.out.println();
        }
    }
}
