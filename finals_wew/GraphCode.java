package finals_wew;

import java.util.ArrayList;

public class GraphCode {
    ArrayList<Node> nodes;
    int[][] matrix;

    public GraphCode(int size) {
        nodes = new ArrayList<>();
        matrix = new int[size][size];
    }

    public void addNode(char data, String name) {
        Node node = new Node(data, name);
        nodes.add(node);
    }

    public void addEdge(int src, int dst) {
        // Check if the indices are valid
        if (src < 0 || src >= nodes.size() || dst < 0 || dst >= nodes.size()) {
            System.out.println("Cannot create friendship. One or both persons do not exist.");
            return;
        }
        matrix[src][dst] = 1;
        matrix[dst][src] = 1;
        System.out.println("Friendship created between " + nodes.get(src).name + " and " + nodes.get(dst).name + ".");
    }

    public void displayGraph() {
        // Check if there are no nodes
        if (nodes.isEmpty()) {
            System.out.println("No persons added yet. The graph is empty.");
            return; // Return early if there are no nodes
        }

        System.out.print("  ");
        for (Node node : nodes) {
            System.out.print(node.name + " ");
        }
        System.out.println();

        // Only iterate up to the number of nodes
        for (int x = 0; x < nodes.size(); x++) {
            System.out.print(nodes.get(x).name + " ");
            for (int y = 0; y < nodes.size(); y++) {
                System.out.print(matrix[x][y] + " ");
            }
            System.out.println();
        }
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
                // For multiple friends print: A is friends with B, C and D
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

