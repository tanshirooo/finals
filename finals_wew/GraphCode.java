package finals_wew;

import java.util.Queue;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Collections;

public class GraphCode {

    ArrayList<Node> nodes; // List of all people (Node objects).
    int[][] matrix; // Adjacency matrix representing friendships between people.

    public GraphCode(int size) {
        nodes = new ArrayList<>();
        matrix = new int[size][size];
    }

    public void addNode(char data, String name, String hobbies, int age) {
        if (nodes.size() >= matrix.length) {
            System.out.println("You can't add another person, the server is busy.");
            return;
        }
        Node node = new Node(data, name, hobbies, age);
        nodes.add(node);
    }

    public void addEdge(int src, int dst) {
        if (src < 0 || src >= nodes.size() || dst < 0 || dst >= nodes.size()) {
            System.out.println("Cannot create friendship. One or both persons do not exist.");
            return;
        }
        matrix[src][dst] = 1; // Set friendship on adjacency matrix
        matrix[dst][src] = 1; // Friendship is bidirectional (undirected).
        System.out.println("Friendship created between " + nodes.get(src).name + " and " + nodes.get(dst).name + ".");
    }

    /**
     * Breadth-First Search (BFS) algorithm to find the shortest path (degree of separation)
     * between two people in the network. Uses a queue to visit friends level by level.
     */
    public void findDegreeOfSeparation(int start, int end) {
        int n = nodes.size();
        boolean[] visited = new boolean[n];
        int[] degree = new int[n]; // Steps from start
        int[] parent = new int[n]; // For reconstructing the path
        Queue<Integer> queue = new LinkedList<>();

        for (int i = 0; i < n; i++) parent[i] = -1;

        queue.offer(start);
        visited[start] = true;
        degree[start] = 0;

        while (!queue.isEmpty()) {
            int curr = queue.poll();
            if (curr == end) { // Found the target, build path
                ArrayList<String> pathNames = new ArrayList<>();
                int trace = end;
                while (trace != -1) {
                    pathNames.add(nodes.get(trace).name);
                    trace = parent[trace];
                }
                Collections.reverse(pathNames);
                System.out.println("Degree of separation between "
                        + nodes.get(start).name + " and " + nodes.get(end).name + " is: " + degree[curr]);
                System.out.println("Path: " + String.join(" -> ", pathNames));
                return;
            }
            for (int i = 0; i < n; i++) {
                if (matrix[curr][i] == 1 && !visited[i]) {
                    visited[i] = true;
                    degree[i] = degree[curr] + 1;
                    parent[i] = curr;
                    queue.offer(i);
                }
            }
        }
        System.out.println("There is no connection between "
                + nodes.get(start).name + " and " + nodes.get(end).name + ".");
    }

    public void displayGraph() {
        if (nodes.isEmpty()) {
            System.out.println("No persons added yet. The graph is empty.");
            return;
        }
        // Print header row (names)
        System.out.print("           ");
        for (Node node : nodes) {
            System.out.printf("%-9s", node.name);
        }
        System.out.println();

        // Print each row (name and connections)
        for (int i = 0; i < nodes.size(); i++) {
            System.out.printf("%-11s", nodes.get(i).name);
            for (int j = 0; j < nodes.size(); j++) {
                System.out.printf("%-9d", matrix[i][j]);
            }
            System.out.println();
        }
    }

    public boolean nameExists(String name) {
        for (Node node : nodes) {
            if (node.name.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public void isolatedPeople() {
        for (int i = 0; i < nodes.size(); i++) {
            boolean hasFriends = false;
            for (int j = 0; j < nodes.size(); j++) {
                if (matrix[i][j] == 1) {
                    hasFriends = true;
                    break;
                }
            }
            if (!hasFriends) {
                System.out.println(nodes.get(i).name + " is isolated (has no friends).");
            }
        }
    }

    public void unfriend(int src, int dst) {
        if (src < 0 || src >= nodes.size() || dst < 0 || dst >= nodes.size()) {
            System.out.println("Cannot unfriend. One or both persons do not exist.");
            return;
        }
        if (matrix[src][dst] == 1) {
            matrix[src][dst] = 0;
            matrix[dst][src] = 0;
            System.out.println("Friendship between " + nodes.get(src).name + " and " + nodes.get(dst).name + " has been removed.");
        } else {
            System.out.println(nodes.get(src).name + " and " + nodes.get(dst).name + " are not friends.");
        }
    }

    public void printFriendships() {
        if (nodes.isEmpty()) {
            System.out.println("No persons added yet.");
            return;
        }
        for (int i = 0; i < nodes.size(); i++) {
            ArrayList<String> friends = new ArrayList<>();
            for (int j = 0; j < nodes.size(); j++) {
                if (matrix[i][j] == 1) {
                    friends.add(nodes.get(j).name);
                }
            }
            String nodeName = nodes.get(i).name;
            int friendCount = friends.size();

            if (friendCount == 0) {
                System.out.println(nodeName + " has no friends.");
            } else if (friendCount == 1) {
                System.out.println(nodeName + " and " + friends.get(0) + " are friends.");
            } else {
                StringBuilder sb = new StringBuilder();
                for (int k = 0; k < friendCount; k++) {
                    sb.append(friends.get(k));
                    if (k == friendCount - 2) {
                        sb.append(" and ");
                    } else if (k < friendCount - 2) {
                        sb.append(", ");
                    }
                }
                System.out.println(nodeName + " is friends with " + sb.toString() + ".");
            }
        }
    }
}
