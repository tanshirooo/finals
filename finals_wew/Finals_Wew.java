package finals_wew;

import java.util.Scanner;

public class Finals_Wew {

    public static void main(String[] args) {
        GraphCode graph = new GraphCode(10); // Initialize the graph with a size of 10
        Scanner input = new Scanner(System.in);

        while (true) {
            System.out.println();
            printDisplay();

            int choice = readInt(input, "Enter Your Choice: ");

            switch (choice) {
                case 1 -> {
                    // Add person
                    System.out.println("\n--- Add Person ---");
                    char data = readChar(input, "Enter character representation (e.g., A): ");
                    String name = readLine(input, "Enter name: ");
                    graph.addNode(data, name);
                    System.out.println("Person " + name + " added successfully!");
                }
                case 2 -> {
                    // Create friendship
                    if (graph.nodes.size() < 2) {
                        System.out.println("Must have at least 2 persons added to create a friendship.");
                        break;
                    }
                    System.out.println("\n--- Create Friendship ---");
                    System.out.println("Available persons:");
                    for (int i = 0; i < graph.nodes.size(); i++) {
                        System.out.println("[" + i + "] " + graph.nodes.get(i).name);
                    }
                    int src = readInt(input, "Enter index of your person (0 to " + (graph.nodes.size() - 1) + "): ");
                    int dst = readInt(input, "Enter index of the person you want to be friends with (0 to " + (graph.nodes.size() - 1) + "): ");
                    if (src < 0 || src >= graph.nodes.size() || dst < 0 || dst >= graph.nodes.size()) {
                        System.out.println("Invalid person index. Please enter valid indices.");
                    } else if (src == dst) {
                        System.out.println("Cannot create friendship with the same person.");
                    } else {
                        graph.addEdge(src, dst);
                    }
                }
                case 3 -> {
                    // Display Network
                    if (graph.nodes.isEmpty()) {
                        System.out.println("No persons added yet. Please add persons first.");
                    } else {
                        System.out.println("\n--- Mutual Friendships ---");
                        graph.printFriendships();
                    }
                }
                case 4 -> {
                    // Find degree of separation
                    if (graph.nodes.size() < 2) {
                        System.out.println("Must have at least 2 persons added to find degree of separation.");
                        break;
                    }
                    System.out.println("\n--- Find Degree of Separation ---");
                    int start = readInt(input, "Enter index of first person (0 to " + (graph.nodes.size() - 1) + "): ");
                    int end = readInt(input, "Enter index of second person (0 to " + (graph.nodes.size() - 1) + "): ");
                    if (start < 0 || start >= graph.nodes.size() || end < 0 || end >= graph.nodes.size()) {
                        System.out.println("Invalid person index. Please enter valid indices.");
                    } else {
                        findDegreeOfSeparation(graph, start, end);
                    }
                }
                case 5 -> {
                    // Find largest circle
                    if (graph.nodes.isEmpty()) {
                        System.out.println("No persons added yet to find the largest circle.");
                    } else {
                        findLargestCircle(graph);
                    }
                }
                case 6 -> {
                    // No friends
                    if (graph.nodes.isEmpty()) {
                        System.out.println("No persons added yet.");
                    } else {
                        System.out.println("\n--- Isolated People (No Friends) ---");
                        graph.isolatedPeople();
                    }
                }
                case 7 -> {
                    // Exit
                    System.out.println("Exiting the program. Goodbye!");
                    input.close();
                    return;
                }
                default -> System.out.println("Invalid choice. Please select a valid option (1-7).");
            }
        }
    }

    static void printDisplay() {
        System.out.println("---------- Social Circle Analyzer ----------");
        System.out.println("[1] Add Person");
        System.out.println("[2] Create a Friendship");
        System.out.println("[3] Display Network");
        System.out.println("[4] Find Degree of Separation");
        System.out.println("[5] Find Largest Circle");
        System.out.println("[6] No Friends");
        System.out.println("[7] Exit");
    }

    static void findDegreeOfSeparation(GraphCode graph, int start, int end) {
        // Placeholder for degree of separation logic
        System.out.println("Finding degree of separation between " + graph.nodes.get(start).name + " and " + graph.nodes.get(end).name + "...");
        // Implement BFS or DFS to find the degree of separation
    }

    static void findLargestCircle(GraphCode graph) {
        // Placeholder for largest circle logic
        System.out.println("Finding the largest circle...");
        // Implement logic to find the largest circle of friends
    }

    static int readInt(Scanner input, String prompt) {
        System.out.print(prompt);
        while (!input.hasNextInt()) {
            System.out.print("Invalid input. " + prompt);
            input.next(); // Clear invalid input
        }
        int value = input.nextInt();
        input.nextLine(); // Consume the newline character
        return value;
    }

    static char readChar(Scanner input, String prompt) {
        System.out.print(prompt);
        return input.next().charAt(0);
    }

    static String readLine(Scanner input, String prompt) {
        System.out.print(prompt);
        return input.nextLine();
    }
}
