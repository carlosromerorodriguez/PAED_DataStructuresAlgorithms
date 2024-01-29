package business.graphs;

import business.entities.graphs.Connection;
import business.entities.graphs.InterestPoint;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import presentation.KeyBoardManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GraphManager {
    private float[][] adjMatrix;
    private List<InterestPoint> interestPoints;
    List<Connection> connections;
    public GraphManager(String path) {
        readGraphsFile(path);
    }

    private void readGraphsFile(String path) {
        List<String> input;
        try (Stream<String> lines = Files.lines(Paths.get(path))) { input = lines.collect(Collectors.toList()); } catch (IOException e) { throw new RuntimeException(e); }
        int numPlaces = Integer.parseInt(input.get(0));
        List<String> readPoints = new ArrayList<>(input.subList(1, numPlaces + 1));
        input.subList(0, numPlaces + 2).clear();
        interestPoints = createInterestPoints(readPoints);
        connections = createConnections(input);
        adjMatrix = createAdjacencyMatrix(interestPoints, connections);
    }

    private float[][] createAdjacencyMatrix(List<InterestPoint> interestPoints, List<Connection> connections) {
        float[][] matrizAdj = new float[interestPoints.size()][interestPoints.size()];
        for (int i = 0; i < interestPoints.size(); i++) {
            for (int j = 0; j < interestPoints.size(); j++) {
                if (i == j) {
                    matrizAdj[i][j] = 0; // La distancia de un punto a sí mismo es 0
                } else {
                    for (Connection connection : connections) {
                        if ((connection.idA() == interestPoints.get(i).id() && connection.idB() == interestPoints.get(j).id()) ||
                                (connection.idA() == interestPoints.get(j).id() && connection.idB() == interestPoints.get(i).id())) {
                            matrizAdj[i][j] = connection.distance(); // Guardar la distancia obtenida de dicha conexión
                            break;
                        }
                    }
                }
            }
        }
        return matrizAdj;
    }

    private List<Connection> createConnections(List<String> input) {
        List<Connection> graphs = new ArrayList<>();
        for (String s: input) {
            String[] parts = s.split(";");
            graphs.add(new Connection(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3]), Float.parseFloat(parts[4])));
        }
        return graphs;
    }

    private List<InterestPoint> createInterestPoints(List<String> interestPoints) {
        List<InterestPoint> interestPointList = new ArrayList<>();
        for (String s : interestPoints) {
            String[] parts = s.split(";");
            interestPointList.add(new InterestPoint(Integer.parseInt(parts[0]), parts[1], parts[2], parts[3]));
        }
        return interestPointList;
    }

    public void exploration() {
        // Exploració del regne
        int interestPlaceID = KeyBoardManager.askForPositiveInteger("Quin lloc vol explorar? ", Integer.MAX_VALUE);
        int matrixPosition = -1;
        for (int i = 0; i < interestPoints.size(); i++) {
            InterestPoint ip = interestPoints.get(i);
            if (ip.id() == interestPlaceID) {
                matrixPosition = i;
                break;
            }
        }
        if (matrixPosition == -1) {
            System.out.println("(ERROR) Lloc d'interés no existent en el sistema");
        } else {
            System.out.println(interestPlaceID+ " – " + interestPoints.get(matrixPosition).nom() + ", " + interestPoints.get(matrixPosition).regne() + " (Clima " + title(interestPoints.get(matrixPosition).clima()) + ")");
            System.out.println("\nEls llocs del Regne de "+ interestPoints.get(matrixPosition).regne() +" als que es pot arribar són:\n");

            assert adjMatrix != null;
            bfs(adjMatrix, matrixPosition, interestPoints);
        }
    }

    public static String title(String text) {
        String[] words = text.split(" ");
        StringBuilder title = new StringBuilder();
        for (String word : words) {
            title.append(word.substring(0, 1).toUpperCase()).append(word.substring(1).toLowerCase());
        }
        return title.toString();
    }


    private void bfs(float[][] adjMatrix, int inicio, List<InterestPoint> interestPoints) {
        // Crear una cola para el BFS
        Queue<Integer> queue = new LinkedList<>();

        // Marcar todos los vértices como no visitados (por defecto se marcan como false)
        boolean[] visited = new boolean[interestPoints.size()];

        // Marcar el vértice actual como visitado y añadirlo a la cola
        visited[inicio] = true;
        queue.add(inicio);

        while (queue.size() != 0) {
            // Quitar un vértice de la cola y imprimirlo
            int newPlace = queue.poll();
            if (interestPoints.get(newPlace).regne().equals(interestPoints.get(inicio).regne()) && newPlace != inicio) {
                System.out.println(interestPoints.get(newPlace).id() + " – " + interestPoints.get(newPlace).nom() + ", " + interestPoints.get(newPlace).regne() + " (Clima " + title(interestPoints.get(newPlace).clima()) + ")");
            }

            // Obtener todos los vértices adyacentes del vértice actual
            // Si un vértice adyacente no ha sido visitado, marcarlo como visitado y añadirlo a la cola
            for (int i = 0; i < interestPoints.size(); i++) {
                if (adjMatrix[newPlace][i] != 0 && !visited[i]) {
                    visited[i] = true;
                    queue.add(i);
                }
            }
        }
        System.out.println();
    }

    public void detectHabitualPaths() {
        boolean[] visited = new boolean[interestPoints.size()];
        int[] parent = new int[interestPoints.size()];
        float[] distance = new float[interestPoints.size()];

        Arrays.fill(distance, Float.POSITIVE_INFINITY);
        Arrays.fill(visited, false);

        distance[0] = 0;
        parent[0] = -1;

        for (int i = 0; i < (interestPoints.size() - 1); i++) { // menos 1 porque el ultimo nodo ya ha sido agregado al subconjunto de nodos visitados (evitar iteración extra)
            int minimumDistance = findMinimumDistance(distance, visited);
            visited[minimumDistance] = true;
            for (int vertex = 0; vertex < interestPoints.size(); vertex++) {
                if (adjMatrix[minimumDistance][vertex] != 0 && !visited[vertex] && adjMatrix[minimumDistance][vertex] < distance[vertex]) {
                    parent[vertex] = minimumDistance;
                    distance[vertex] = adjMatrix[minimumDistance][vertex];
                }
            }
        }

        // Mostrar el MST
        System.out.println("\nEls trajectes més habituals són els següents:\n");
        System.out.printf("%-20s  %-20s  %-10s\n", "Origen", "Destí", "Distància");
        System.out.printf("%-20s  %-20s  %-10s\n", "------", "-----", "---------");
        for (int i = 1; i < interestPoints.size(); i++) {
            String origen = interestPoints.get(parent[i]).nom();
            String dest = interestPoints.get(i).nom();
            float distancia = adjMatrix[parent[i]][i];
            System.out.printf("%-20s  %-20s  %.2f km\n", origen, dest, distancia);
        }
        System.out.println("\n");
    }

    @Contract(pure = true)
    private static int findMinimumDistance(float @NotNull [] distance, boolean[] visited) {
        float min = Float.POSITIVE_INFINITY;
        int minIndex = -1;

        for (int i = 0; i < distance.length; i++) {
            if (!visited[i] && distance[i] < min) {
                min = distance[i];
                minIndex = i;
            }
        }

        return minIndex;
    }

    private List<Integer> calculateShortestPath(int originMatrixPosition, int destinationMatrixPosition, boolean carryingCoconut, boolean europea) {
        int nVertices = interestPoints.size();
        float[] minDistances = new float[nVertices];
        boolean[] visited = new boolean[nVertices];
        int[] predecessors = new int[nVertices];

        Arrays.fill(minDistances, Float.POSITIVE_INFINITY); // Inicializar todas las distancias a "+infinito"
        Arrays.fill(predecessors, -1);                  // Inicializar todos los predecesores a -1
        minDistances[originMatrixPosition] = 0;             // La distancia del origen a si mismo es 0

        for (int i = 0; i < (nVertices - 1); i++) {
            int source = -1;
            for (int j = 0; j < nVertices; j++) {
                if (!visited[j] && (source == -1 || minDistances[j] < minDistances[source])) {
                    source = j;
                }
            }
            visited[source] = true;

            for (int destination = 0; destination < nVertices; destination++) {
                float edgeWeight = adjMatrix[source][destination];

                // Revisar las restricciones de clima
                String destinationClima = interestPoints.get(destination).clima();
                boolean climaRestriction = europea ? (!destinationClima.equalsIgnoreCase("POLAR") && !destinationClima.equalsIgnoreCase("CONTINENTAL"))
                        : (!destinationClima.equalsIgnoreCase("CONTINENTAL") && !destinationClima.equalsIgnoreCase("TROPICAL"));

                if (climaRestriction || (carryingCoconut && edgeWeight > 50)) {
                    continue; // Ignorar la arista si no cumple con las restricciones de clima o si la golondrina lleva un coco y la distancia es mayor a 50
                }

                if (edgeWeight > 0 && !visited[destination]) {
                    float newPathDistance = minDistances[source] + edgeWeight;
                    if (newPathDistance < minDistances[destination]) {
                        minDistances[destination] = newPathDistance;
                        predecessors[destination] = source;
                    }
                }
            }
        }

        List<Integer> path = new ArrayList<>();
        int currentNode = destinationMatrixPosition;
        while (currentNode != -1) {
            path.add(currentNode);
            currentNode = predecessors[currentNode];
        }
        Collections.reverse(path);

        return path;
    }

    public void premiumMessaging() {
        int originID = KeyBoardManager.askForPositiveInteger("Quin lloc vol explorar? ", Integer.MAX_VALUE);
        int destinationID = KeyBoardManager.askForPositiveInteger("A quin lloc vol arribar? ", Integer.MAX_VALUE);
        boolean carryingCoconut = KeyBoardManager.askForBoolean("L'oreneta portarà un coco (S/N)? ");

        int originMatrixPosition = -1;
        int destinationMatrixPosition = -1;
        for (int i = 0; i < interestPoints.size(); i++) {
            InterestPoint ip = interestPoints.get(i);
            if (ip.id() == originID) {
                originMatrixPosition = i;
            }
            if (ip.id() == destinationID) {
                destinationMatrixPosition = i;
            }
        }
        if (originMatrixPosition == -1 || destinationMatrixPosition == -1) {
            System.out.println("\n(ERROR) Lloc d'interés no existent en el sistema");
        } else {
            if (originID == destinationID) {
                System.out.println("\n(ERROR) El lloc d'origen i el lloc de destí no poden ser el mateix");
                return;
            }

            boolean onlyEuropeanCanDoIt = (interestPoints.get(destinationMatrixPosition).clima().equals("POLAR") || interestPoints.get(destinationMatrixPosition).clima().equals("CONTINENTAL"))
                    && (interestPoints.get(originMatrixPosition).clima().equals("POLAR") || interestPoints.get(originMatrixPosition).clima().equals("CONTINENTAL"));
            boolean onlyAfricanCanDoIt = (interestPoints.get(destinationMatrixPosition).clima().equals("TROPICAL") || interestPoints.get(destinationMatrixPosition).clima().equals("CONTINENTAL"))
                    && (interestPoints.get(originMatrixPosition).clima().equals("TROPICAL") || interestPoints.get(originMatrixPosition).clima().equals("CONTINENTAL"));

            List<Integer> pathEuropea, pathAfricana;
            if (onlyAfricanCanDoIt && onlyEuropeanCanDoIt) {
                pathEuropea = calculateShortestPath(originMatrixPosition, destinationMatrixPosition, carryingCoconut, true);
                pathAfricana = calculateShortestPath(originMatrixPosition, destinationMatrixPosition, carryingCoconut, false);

                String[] parts = calculateTimeAndDistance(pathAfricana, false).split(";");
                float tempsAfricana = Float.parseFloat(parts[0]);
                float distanciaAfricana = Float.parseFloat(parts[1]);

                parts = calculateTimeAndDistance(pathEuropea, true).split(";");
                float tempsEuropea = Float.parseFloat(parts[0]);
                float distanciaEuropea = Float.parseFloat(parts[1]);

                System.out.println("\nL’opció més eficient és enviar una oreneta " + (tempsEuropea < tempsAfricana ? "europea" : "africana") + ".");
                System.out.println("Temps: " + Math.min(tempsAfricana, tempsEuropea) + " minuts");
                System.out.println("Distància: " + Math.min(distanciaAfricana, distanciaEuropea) + " quilòmetres");

                showWinningPath(tempsEuropea < tempsAfricana ? pathEuropea : pathAfricana);
            } else if (onlyEuropeanCanDoIt) {
                pathEuropea = calculateShortestPath(originMatrixPosition, destinationMatrixPosition, carryingCoconut, true);
                String[] parts = calculateTimeAndDistance(pathEuropea, true).split(";");
                System.out.println("\nL’opció més eficient és enviar una oreneta europea.");
                System.out.println("Temps: " + parts[0] + " minuts");
                System.out.println("Distància: " + Float.parseFloat(parts[1]) + " quilòmetres");
                showWinningPath(pathEuropea);
            } else if (onlyAfricanCanDoIt) {
                pathAfricana = calculateShortestPath(originMatrixPosition, destinationMatrixPosition, carryingCoconut, false);
                String[] parts = calculateTimeAndDistance(pathAfricana, false).split(";");
                System.out.println("\nL’opció més eficient és enviar una oreneta africana.");
                System.out.println("Temps: " + parts[0] + " minuts");
                System.out.println("Distància: " + Float.parseFloat(parts[1]) + " quilòmetres");
                showWinningPath(pathAfricana);
            } else {
                System.out.println("\n(ERROR) No es pot arribar a aquest lloc des de l'origen");
            }
        }
        System.out.println("\n");
    }

    private void showWinningPath(List<Integer> pathToShow) {
        System.out.println("Camí: ");
        for (int i = 1; i < pathToShow.size(); i++) {
            if (i <= pathToShow.size() - 1) {
                System.out.print(" \t-> ");
            }
            System.out.print(interestPoints.get(pathToShow.get(i)).nom() + " (" + interestPoints.get(pathToShow.get(i)).id() + "), ");
            System.out.print("Distància: " + adjMatrix[pathToShow.get(i-1)][pathToShow.get(i)] + "km, ");
            System.out.println("[Clima " + title(interestPoints.get(pathToShow.get(i)).clima()) + "]");
        }
    }

    private String calculateTimeAndDistance(List<Integer> path , boolean isEuropean) {
        float time = 0;
        float dist = 0;
        for (int i = 1; i < path.size(); i++) {
            int idA = interestPoints.get(path.get(i - 1)).id();
            int idB = interestPoints.get(path.get(i)).id();
            dist += adjMatrix[path.get(i-1)][path.get(i)];
            for (Connection connection : connections) {
                if ((connection.idA() == idA && connection.idB() == idB) || (connection.idA() == idB && connection.idB() == idA)) {
                    time += isEuropean ? connection.tempsE() : connection.tempsA();
                }
            }
        }
        return (time + ";" + dist);
    }

    public void showAdjacencyMatrix() {
        // Encabezado con los ID's de los puntos de interés
        System.out.print("      ");
        for (InterestPoint point : interestPoints) {
            System.out.printf("%-6d", point.id());
        }
        System.out.println();

        // Distancias entre ID's (se obtiene una matriz simétrica, con la diagonal principal de 0)
        for (int i = 0; i < interestPoints.size(); i++) {
            System.out.printf("%-6d", interestPoints.get(i).id());
            for (int j = 0; j < interestPoints.size(); j++) {
                System.out.printf("%-6.2f", adjMatrix[i][j]);
            }
            System.out.println();
        }
        System.out.printf("%n");
    }
}
