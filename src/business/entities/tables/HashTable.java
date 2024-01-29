// HashTable.java
package business.entities.tables;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class HashTable {
    private final List<LinkedList<Accused>> table;
    private final int R;
    private int elements;

    public HashTable(int tableSize) {
        this.R = findNextPrime(tableSize);
        this.table = new ArrayList<>(R);
        this.elements = 0;

        for (int i = 0; i < R; i++) {
            table.add(new LinkedList<>());
        }
    }

    private int firstHashFunction(String key) {
        int hash = 0;

        for (int i = 0; i < key.length(); i++) {
            if (key.charAt(i) != ' ') {
                hash += (i + 1) * key.codePointAt(i);
            }
        }
        return (hash % R);
    }

    private int secondaryHashFunction(String key) {
        int hash = 0;

        for (int i = 0; i < key.length(); i++) {
            if (key.charAt(i) != ' ') {
                hash += key.codePointAt(i);
            }
        }
        return (2 - (hash % 2));
    }

    // Función de rehashing
    private int doubleHashing(int primaryHash, int secondaryHash, int attempt) {
        return ((primaryHash + attempt * secondaryHash) % R);
    }

    private int findNextPrime(int number) {
        if (number <= 1) {
            return 2;
        }
        int prime = number % 2 == 0 ? number + 1 : number;
        while (!isPrime(prime)) {
            prime += 2;
        }
        return prime;
    }

    private boolean isPrime(int number) {
        if (number <= 1) {
            return false;
        } if (number == 2) {
            return true;
        } if (number % 2 == 0) {
            return false;
        }

        for (int i = 3; i <= Math.sqrt(number); i += 2) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }

    public void addAccused(Accused accused) {
        int primaryHash = firstHashFunction(accused.getName());
        int secondaryHash = secondaryHashFunction(accused.getName());
        int newIndex = primaryHash;

        for (int i = 1; !table.get(newIndex).isEmpty(); i++) {
            newIndex = doubleHashing(primaryHash, secondaryHash, i);
        }
        table.get(newIndex).add(accused);
        elements++;

        if (elements >= (table.size() * 0.8)) {
            resizeHashTable();
        }
    }

    public boolean deleteAccused(String name) {
        int primaryHash = firstHashFunction(name);
        int secondaryHash = secondaryHashFunction(name);
        int newIndex = primaryHash;

        for (int i = 1; !table.get(newIndex).isEmpty(); i++) {
            LinkedList<Accused> accusedList = table.get(newIndex);
            if (accusedList.getFirst().getName().equals(name)) {
                System.out.println("L’execució pública de " + accusedList.getFirst().getName() + " ha estat un èxit.");
                accusedList.removeFirst();
                elements--;
                return true;
            }
            newIndex = doubleHashing(primaryHash, secondaryHash, i);
        }
        return false;
    }

    private void resizeHashTable() {
        int newSize = findNextPrime(table.size() * 2);
        List<LinkedList<Accused>> newTable = new ArrayList<>(newSize);

        for (int i = 0; i < newSize; i++) {
            newTable.add(new LinkedList<>());
        }

        for (LinkedList<Accused> accusedList : table) {
            for (Accused accused : accusedList) {
                int primaryHash = firstHashFunction(accused.getName());
                int secondaryHash = secondaryHashFunction(accused.getName());
                int newIndex = primaryHash;

                for (int i = 1; !newTable.get(newIndex).isEmpty(); i++) {
                    newIndex = doubleHashing(primaryHash, secondaryHash, i);
                }

                newTable.get(newIndex).add(accused);
            }
        }

        table.clear();
        table.addAll(newTable);
    }

    public void markAsHeretic(String name, boolean isHeretic) {
        int primaryHash = firstHashFunction(name);
        int secondaryHash = secondaryHashFunction(name);
        int newIndex = primaryHash;

        for (int i = 1; !table.get(newIndex).isEmpty(); i++) {
            Accused accused = table.get(newIndex).getFirst();
            if (accused.getName().equals(name)) {
                String profession = accused.getProfession();
                if (!profession.equals("KING") && !profession.equals("QUEEN") && !profession.equals("CLERGYMAN")) {
                    accused.setHeretic(isHeretic);
                    System.out.println(isHeretic ? "La Inquisició Espanyola ha conclòs que " + accused.getName() + " és un heretge." :
                            "La Inquisició Espanyola ha conclòs que " + accused.getName() + " no és un heretge.");
                    return;
                }
                System.out.println(isHeretic ? "(ERROR) No pots marcar com a herètic a un rei, una reina o un clergue." :
                        "La Inquisició Espanyola ha conclòs que " + accused.getName() + " no és un heretge.");
                return;
            }
            newIndex = doubleHashing(primaryHash, secondaryHash, i);
        }
        System.out.println("(ERROR) No s’ha trobat cap acusat amb aquest nom.");
    }

    public void histogramByProfession() {
        System.out.println("Generant histograma...\n");
        List<String> professions = new ArrayList<>();
        List<Integer> hereticCounts = new ArrayList<>();

        for (LinkedList<Accused> accusedList : table) {
            for (Accused accused : accusedList) {
                if (accused.isHeretic()) {
                    String profession = accused.getProfession();
                    int index = professions.indexOf(profession);
                    if (index == -1) {
                        professions.add(profession);
                        hereticCounts.add(1);
                    } else {
                        hereticCounts.set(index, hereticCounts.get(index) + 1);
                    }
                }
            }
        }
        HistogramWindow histogramWindow = new HistogramWindow(professions, hereticCounts, table);
        histogramWindow.setVisible(true);
    }

    public void finalJudgementOneAccused(String name) {
        int primaryHash = firstHashFunction(name);
        int secondaryHash = secondaryHashFunction(name);
        int newIndex = primaryHash;

        for (int i = 1; !table.get(newIndex).isEmpty(); i++) {
            Accused accused = table.get(newIndex).getFirst();
            if (accused.getName().equalsIgnoreCase(name)) {
                System.out.println("Registre per \"" + accused.getName() + "\":");
                System.out.println("\t* Nombre de conills vistos: " + accused.getRabbits());
                System.out.println("\t* Professió: " + accused.getProfession());
                System.out.println("\t* Heretge: " + (accused.isHeretic() ? "Sí" : "No") + "\n");
                return;
            }
            newIndex = doubleHashing(primaryHash, secondaryHash, i);
        }
        System.out.println("(ERROR) No s’ha trobat cap acusat amb aquest nom.\n");
    }

    public void finalJudgementAllAccused(int minRabbits, int maxRabbits) {
        List<Accused> accusedList = new ArrayList<>();

        for (LinkedList<Accused> accusedLinkedList : table) {
            for (Accused accused : accusedLinkedList) {
                if (accused.getRabbits() >= minRabbits && accused.getRabbits() <= maxRabbits) {
                    accusedList.add(accused);
                }
            }
        }

        if (accusedList.isEmpty()) {
            System.out.println("(ERROR) No s’ha trobat cap acusat amb aquest nombre de conills.");
        } else {
            System.out.println("S’han trobat els següents acusats:");
            for (Accused accused : accusedList) {
                System.out.println("\t" + accused.getName() + ":");
                System.out.println("\t\t* Nombre de conills vistos: " + accused.getRabbits());
                System.out.println("\t\t* Professió: " + accused.getProfession());
                System.out.println("\t\t* Heretge: " + (accused.isHeretic() ? "Sí" : "No"));
            }
            System.out.println();
        }
    }
}