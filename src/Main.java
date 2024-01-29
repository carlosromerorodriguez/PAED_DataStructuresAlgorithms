import business.avl.AVLManager;
import business.graphs.GraphManager;
import business.r_trees.RTreeManager;
import business.tables.TablesManager;
import presentation.Screen;

public class Main {
    public static void main(String[] args) {
        Screen screen = new Screen();
        boolean end = false;
        String pathFile = "";

        screen.showWelcome();

        while (!end) {
            int menuOption = screen.showMenu();
            if (menuOption != 5) { pathFile = screen.askForFile(menuOption); }

            switch (menuOption) {
                case 1 -> executeGraphsOption(screen, pathFile);
                case 2 -> executeBSTOptions(screen, pathFile);
                case 3 -> executeRTreeOptions(screen, pathFile);
                case 4 -> executeTablesOptions(screen, pathFile);
                default -> {
                    screen.showExitMenu();
                    end = true;
                }
            }
        }
    }

    private static void executeGraphsOption(Screen screen, String pathFile) {
        GraphManager graphManager = new GraphManager(pathFile);

        while (true) {
            switch (screen.showGraphsMenu()) {
                case 'A' -> graphManager.exploration(); // BFS
                case 'B' -> graphManager.detectHabitualPaths(); // Algoritmo de Prim (MST)
                case 'C' -> graphManager.premiumMessaging(); // Dijkstra
                case 'D' -> graphManager.showAdjacencyMatrix();
                case 'E' -> {
                    return;
                }
            }
        }
    }
    private static void executeBSTOptions(Screen screen, String pathFile) {
        AVLManager avlManager = new AVLManager(pathFile);

        while (true) {
            switch (screen.showBSTMenu()) {
                case 'A' -> avlManager.addHabitant();
                case 'B' -> avlManager.deleteHabitant();
                case 'C' -> avlManager.showAVL();
                case 'D' -> avlManager.searchWitches();
                case 'E' -> avlManager.searchByRange();
                case 'F' -> {
                    return;
                }
            }
        }
    }
    private static void executeRTreeOptions(Screen screen, String pathFile) {
        RTreeManager rTreeManager = new RTreeManager(pathFile);

        while (true) {
            switch (screen.showRTreeMenu()) {
                case 'A' -> rTreeManager.addPoint();
                case 'B' -> rTreeManager.deletePoint();
                case 'C' -> rTreeManager.showRTree();
                case 'D' -> rTreeManager.searchByRange();
                case 'E' -> rTreeManager.estheticsOptimization();
                case 'F' -> {
                    return;
                }
            }
        }
    }
    private static void executeTablesOptions(Screen screen, String pathFile) {
        TablesManager tablesManager = new TablesManager(pathFile);

        while (true) {
            switch (screen.showTablesMenu()) {
                case 'A' -> tablesManager.addAccusedToHashTable();
                case 'B' -> tablesManager.deleteAccusedFromHashTable();
                case 'C' -> tablesManager.markAsHeretic();
                case 'D' -> tablesManager.finalJudgementOneAccused();
                case 'E' -> tablesManager.finalJudgementAllAccused();
                case 'F' -> tablesManager.histogramByProfession();
                case 'G' -> {
                    return;
                }
            }
        }
    }
}