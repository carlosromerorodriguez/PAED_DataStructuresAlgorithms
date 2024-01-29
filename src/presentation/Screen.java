package presentation;

public class Screen {
    public void showWelcome() {
        System.out.println("'`^\\ The Hashy Grail /^´'");
    }
    public int showMenu() {
        System.out.println("""
                
                1. Sobre orenetes i cocos (Grafs)
                2. Caça de bruixes (Arbres binaris de cerca)
                3. Tanques de bardissa (Arbres R)
                4. D’heretges i blasfems (Taules)
                
                5. Exit
                """);
        return KeyBoardManager.askForPositiveInteger("Esculli una opció: ", 5);
    }

    public String askForFile(int menuOption) {
        System.out.println("""
                1- Size L
                2- Size M
                3- Size S
                4- Size XL
                5- Size XS
                6- Size XXL
                7- Size XXS
                """);
        int fileNumber = KeyBoardManager.askForPositiveInteger("Quin fitxer vol obrir? ", 7);
        switch (menuOption) {
            case 1 -> {
                String[] graphs = {"data/graphs/graphsL.paed", "data/graphs/graphsM.paed", "data/graphs/graphsS.paed", "data/graphs/graphsXL.paed", "data/graphs/graphsXS.paed", "data/graphs/graphsXXL.paed", "data/graphs/graphsXXS.paed"};
                return graphs[fileNumber - 1];
            }
            case 2 -> {
                String[] bst = {"data/trees/treeL.paed", "data/trees/treeM.paed", "data/trees/treeS.paed", "data/trees/treeXL.paed", "data/trees/treeXS.paed", "data/trees/treeXXL.paed", "data/trees/treeXXS.paed"};
                return bst[fileNumber - 1];
            }
            case 3 -> {
                String[] rTree = {"data/rtrees/rtreeL.paed", "data/rtrees/rtreeM.paed", "data/rtrees/rtreeS.paed", "data/rtrees/rtreeXL.paed", "data/rtrees/rtreeXS.paed", "data/rtrees/rtreeXXL.paed", "data/rtrees/rtreeXXS.paed"};
                return rTree[fileNumber - 1];
            }
            case 4 -> {
                String[] tables = {"data/tables/tablesL.paed", "data/tables/tablesM.paed", "data/tables/tablesS.paed", "data/tables/tablesXL.paed", "data/tables/tablesXS.paed", "data/tables/tablesXXL.paed", "data/tables/tablesXXS.paed"};
                return tables[fileNumber - 1];
            }
        }
        return "";
    }

    public char showGraphsMenu() {
        System.out.println("""
                A. Exploració del regne
                B. Detecció de trajectes habituals
                C. Missatgeria premium
                D. Mostrar matriu d'adjacència
                
                E. Tornar enrere
                """);
        return KeyBoardManager.askForCharacter("Quina funcionalitat vol executar? ", 'A', 'E');
    }

    public char showBSTMenu() {
        System.out.println("""
                A. Afegir habitant
                B. Eliminar habitant
                C. Representació visual
                D. Identificació de bruixes
                E. Batuda
                
                F. Tornar enrere
                """);
        return KeyBoardManager.askForCharacter("Quina funcionalitat vol executar? ", 'A', 'F');
    }

    public char showRTreeMenu() {
        System.out.println("""
                A. Afegir bardissa
                B. Eliminar bardissa
                C. Visualització
                D. Cerca per àrea
                E. Optimització estètica
                
                F. Tornar enrere
                """);
        return KeyBoardManager.askForCharacter("Quina funcionalitat vol executar? ", 'A', 'F');
    }

    public char showTablesMenu() {
        System.out.println("""
                A. Afegir acusat
                B. Eliminar acusat
                C. Edicte de gràcia
                D. Judici final (un acusat)
                E. Judici final (rang)
                F. Histograma per professions
                
                G. Tornar enrere
                """);
        return KeyBoardManager.askForCharacter("Quina funcionalitat vol executar? ", 'A', 'G');
    }

    public void showExitMenu() {
        System.out.println("Aturant The Hashy Grail...");
    }
}
