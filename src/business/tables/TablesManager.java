package business.tables;

import business.entities.tables.Accused;
import business.entities.tables.HashTable;
import presentation.KeyBoardManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TablesManager {
    private HashTable hashTable;

    public TablesManager(String pathFile) {
        readTablesFile(pathFile);
    }

    private void readTablesFile(String pathFile) {
        List<String> input;
        try (Stream<String> lines = Files.lines(Paths.get(pathFile))) { input = lines.collect(Collectors.toList()); } catch (
                IOException e) { throw new RuntimeException(e); }

        this.hashTable = new HashTable(Integer.parseInt(input.get(0)) * 2);
        input.remove(0);
        for (String s : input) {
            String[] parts = s.split(";");
            hashTable.addAccused(new Accused(parts[0], Integer.parseInt(parts[1]), parts[2]));
        }
    }

    public void addAccusedToHashTable() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Nom de l'acusat: ");
        String name = scanner.nextLine().trim();
        int rabbits = KeyBoardManager.askForPositiveInteger("\nNombre de conills vistos: ", Integer.MAX_VALUE);
        System.out.print("Professió: ");
        String profession = scanner.nextLine().trim();
        hashTable.addAccused(new Accused(name, rabbits, profession));
    }

    public void deleteAccusedFromHashTable() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Nom de l'acusat: ");
        String name = scanner.nextLine().trim();
        if (hashTable.deleteAccused(name)) {
            System.out.println("Acusat eliminat correctament.");
        } else {
            System.out.println("(ERROR) Acusat no trobat!");
        }
    }

    public void markAsHeretic() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Nom de l'acusat: ");
        String name = scanner.nextLine().trim();
        char option = KeyBoardManager.askForCharacter("Marcar com a heretge (Y/N)? ", 'N', 'Y');
        hashTable.markAsHeretic(name, (option == 'Y'));
    }

    public void finalJudgementOneAccused() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Nom de l'acusat: ");
        String name = scanner.nextLine().trim();
        hashTable.finalJudgementOneAccused(name);
    }

    public void finalJudgementAllAccused() {
        int minRabbits = KeyBoardManager.askForPositiveInteger("Nombre mínim de conills: ", Integer.MAX_VALUE);
        int maxRabbits = KeyBoardManager.askForPositiveInteger("Nombre màxim de conills: ", Integer.MAX_VALUE);
        hashTable.finalJudgementAllAccused(minRabbits, maxRabbits);
    }

    public void histogramByProfession() {
        hashTable.histogramByProfession();
    }
}
