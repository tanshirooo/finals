package finals_wew;

import java.util.*;

public class Finals_Wew {

    // -- Helper classes for friend requests and blocks --
    static class FriendRequest {

        int fromIdx, toIdx;

        FriendRequest(int fromIdx, int toIdx) {
            this.fromIdx = fromIdx;
            this.toIdx = toIdx;
        }
    }

    static class BlockRelation {

        int blocker, blocked;

        BlockRelation(int blocker, int blocked) {
            this.blocker = blocker;
            this.blocked = blocked;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof BlockRelation)) {
                return false;
            }
            BlockRelation that = (BlockRelation) o;
            return blocker == that.blocker && blocked == that.blocked;
        }

        @Override
        public int hashCode() {
            return Objects.hash(blocker, blocked);
        }
    }

    public static void main(String[] args) {
        GraphCode graph = new GraphCode(10);
        Scanner input = new Scanner(System.in);
        List<FriendRequest> friendRequests = new ArrayList<>();
        Set<BlockRelation> blocks = new HashSet<>();

        while (true) {
            printMenu();
            int choice = readInt(input, "Enter Your Choice: ");
            System.out.println();

            switch (choice) {
                case 1 -> {   // Add person
                    System.out.println("------------------------------------");
                    System.out.println("------------- Add Person -------------");
                    if (graph.nodes.size() >= graph.matrix.length) {
                        System.out.println("You can't add another person, the server is Full.");
                        System.out.println("------------------------------------");
                        break;
                    }
                    String name;
                    while (true) {
                        name = readLine(input, "Enter name: ");
                        if (graph.nameExists(name)) {
                            System.out.println("Name already exists! Try again with a unique name.");
                        } else {
                            break;
                        }
                    }
                    String hobbies = readLine(input, "Enter hobbies: ");
                    int age = readInt(input, "Enter age: ");
                    graph.addNode(name.charAt(0), name, hobbies, age);
                    System.out.println("Person " + name + " added successfully!");
                    System.out.println("------------------------------------");
                }
                case 2 -> {   // Send friend request
                    if (graph.nodes.size() < 2) {
                        System.out.println("------------------------------------");
                        System.out.println("Must have at least 2 persons added to create a friendship.");
                        System.out.println("------------------------------------");
                        break;
                    }
                    System.out.println("------------------------------------");
                    System.out.println("------------- Send Friend Request -------------");
                    showPersons(graph);
                    int src = readInt(input, "Choose your index: ");
                    if (!isValidIndex(graph, src)) {
                        break;
                    }

                    String myHobby = graph.nodes.get(src).hobbies.trim().toLowerCase();
                    int myAge = graph.nodes.get(src).age;

                    List<Integer> sameHobby = new ArrayList<>();
                    List<Integer> otherCandidates = new ArrayList<>();
                    for (int i = 0; i < graph.nodes.size(); i++) {
                        if (i == src) {
                            continue;
                        }
                        String theirHobby = graph.nodes.get(i).hobbies.trim().toLowerCase();
                        if (theirHobby.equals(myHobby)) {
                            sameHobby.add(i);
                        } else {
                            otherCandidates.add(i);
                        }
                    }
                    sameHobby.sort(Comparator.comparingInt(i -> Math.abs(graph.nodes.get(i).age - myAge)));
                    otherCandidates.sort(Comparator.comparingInt(i -> Math.abs(graph.nodes.get(i).age - myAge)));

                    Map<Integer, Integer> mutuals = new HashMap<>();
                    for (int i = 0; i < graph.nodes.size(); i++) {
                        if (i == src) {
                            continue;
                        }
                        if (graph.matrix[src][i] == 0 && !hasPending(friendRequests, src, i)) {
                            int count = countMutualFriends(graph, src, i);
                            if (count > 0) {
                                mutuals.put(i, count);
                            }
                        }
                    }

                    System.out.println("------------------------------------");
                    System.out.println("Suggested Friends (Same Hobby, Closest Age):");
                    printSimpleList(graph, sameHobby);
                    System.out.println("Other Persons (Different Hobby, Closest Age):");
                    printSimpleList(graph, otherCandidates);
                    System.out.println("Friend Recommendations (Mutual Friends):");
                    if (mutuals.isEmpty()) {
                        System.out.println("  (None)");
                    } else {
                        mutuals.entrySet().stream()
                                .sorted((a, b) -> b.getValue() - a.getValue())
                                .forEach(e -> System.out.printf("[%d] %-15s Mutual Friends: %d%n", e.getKey(), graph.nodes.get(e.getKey()).name, e.getValue()));
                    }
                    System.out.println("------------------------------------");

                    String dstInput = readLine(input, "Enter index to send friend request or type 'cancel': ");
                    if (dstInput.trim().equalsIgnoreCase("cancel")) {
                        System.out.println("Cancelled.");
                        System.out.println("------------------------------------");
                        break;
                    }
                    int dst;
                    try {
                        dst = Integer.parseInt(dstInput.trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input.");
                        System.out.println("------------------------------------");
                        break;
                    }
                    if (!isValidIndex(graph, dst) || src == dst) {
                        System.out.println("------------------------------------");
                        System.out.println("Invalid index.");
                        System.out.println("------------------------------------");
                    } else if (graph.matrix[src][dst] == 1) {
                        System.out.println("------------------------------------");
                        System.out.println("You are already friends.");
                        System.out.println("------------------------------------");
                    } else if (hasPending(friendRequests, src, dst)) {
                        System.out.println("------------------------------------");
                        System.out.println("Friend request already sent or pending.");
                        System.out.println("------------------------------------");
                    } else if (isBlocked(blocks, src, dst) || isBlocked(blocks, dst, src)) {
                        System.out.println("------------------------------------");
                        System.out.println("Friend request cannot be sent because one of you has blocked the other.");
                        System.out.println("------------------------------------");
                    } else {
                        friendRequests.add(new FriendRequest(src, dst));
                        System.out.println("------------------------------------");
                        System.out.println("Friend request sent!");
                        System.out.println("------------------------------------");
                    }
                }
                case 3 -> {   // Display network
                    if (graph.nodes.isEmpty()) {
                        System.out.println("------------------------------------");
                        System.out.println("No persons added yet.");
                        System.out.println("------------------------------------");
                        break;
                    }
                    System.out.println("------------------------------------");
                    System.out.println("--- Mutual Friendships ---");
                    graph.printFriendships();
                    System.out.println("--- Adjacency Matrix ---");
                    graph.displayGraph();
                    System.out.println("------------------------------------");
                }
                case 4 -> {   // Show mutual friends
                    if (graph.nodes.size() < 2) {
                        System.out.println("------------------------------------");
                        System.out.println("Need at least 2 persons.");
                        System.out.println("------------------------------------");
                        break;
                    }
                    showPersons(graph);
                    int a = readInt(input, "Enter first person index: ");
                    int b = readInt(input, "Enter second person index: ");
                    if (!isValidIndex(graph, a) || !isValidIndex(graph, b) || a == b) {
                        System.out.println("------------------------------------");
                        System.out.println("Invalid indices.");
                        System.out.println("------------------------------------");
                        break;
                    }
                    List<String> mutuals = getMutualFriends(graph, a, b);
                    System.out.println("------------------------------------");
                    System.out.println("Mutual friends between " + graph.nodes.get(a).name + " and " + graph.nodes.get(b).name + ":");
                    if (mutuals.isEmpty()) {
                        System.out.println("  (None)");
                    } else {
                        mutuals.forEach(name -> System.out.println("  " + name));
                    }
                    System.out.println("------------------------------------");
                }
                case 5 -> {   // Find largest circle (DFS used)
                    int n = graph.nodes.size();
                    boolean[] visited = new boolean[n];
                    List<List<String>> circles = new ArrayList<>();
                    for (int i = 0; i < n; i++) {
                        if (!visited[i]) {
                            List<String> circle = new ArrayList<>();
                            dfs(graph, i, visited, circle);
                            if (!circle.isEmpty()) {
                                circles.add(circle);
                            }
                        }
                    }
                    System.out.println("------------------------------------");
                    if (circles.isEmpty()) {
                        System.out.println("No circles found.");
                    } else {
                        List<String> largest = Collections.max(circles, Comparator.comparingInt(List::size));
                        System.out.println("Largest circle (" + largest.size() + "): " + String.join(", ", largest));
                    }
                    System.out.println("------------------------------------");
                }
                case 6 -> {   // No friends
                    if (graph.nodes.isEmpty()) {
                        System.out.println("------------------------------------");
                        System.out.println("No persons added yet.");
                        System.out.println("------------------------------------");
                        break;
                    }
                    System.out.println("------------------------------------");
                    System.out.println("--- Isolated People (No Friends) ---");
                    graph.isolatedPeople();
                    System.out.println("------------------------------------");
                }
                case 7 -> {   // Unfriend
                    if (graph.nodes.size() < 2) {
                        System.out.println("------------------------------------");
                        System.out.println("Not enough persons to unfriend.");
                        System.out.println("------------------------------------");
                        break;
                    }
                    showPersons(graph);
                    int src = readInt(input, "Enter your index: ");
                    if (!isValidIndex(graph, src)) {
                        break;
                    }
                    List<Integer> friends = new ArrayList<>();
                    for (int i = 0; i < graph.nodes.size(); i++) {
                        if (graph.matrix[src][i] == 1) {
                            friends.add(i);
                        }
                    }
                    if (friends.isEmpty()) {
                        System.out.println("------------------------------------");
                        System.out.println(graph.nodes.get(src).name + " has no friends.");
                        System.out.println("------------------------------------");
                        break;
                    }
                    System.out.println("------------------------------------");
                    System.out.println("Current Friends:");
                    printSimpleList(graph, friends);
                    System.out.println("------------------------------------");
                    int dst = readInt(input, "Enter index to unfriend: ");
                    if (!friends.contains(dst)) {
                        System.out.println("------------------------------------");
                        System.out.println("Invalid friend index.");
                        System.out.println("------------------------------------");
                    } else {
                        graph.unfriend(src, dst);
                        System.out.println("------------------------------------");
                        System.out.println("Unfriended successfully.");
                        System.out.println("------------------------------------");
                    }
                }
                case 8 -> {   // Search friends
                    if (graph.nodes.isEmpty()) {
                        System.out.println("------------------------------------");
                        System.out.println("No persons added yet.");
                        System.out.println("------------------------------------");
                        break;
                    }
                    System.out.println("------------------------------------");
                    String query = readLine(input, "Enter name or hobby to search: ").trim().toLowerCase();
                    boolean found = false;
                    System.out.printf("%-7s %-15s %-15s %-5s%n", "Idx", "Name", "Hobbies", "Age");
                    for (int i = 0; i < graph.nodes.size(); i++) {
                        Node n = graph.nodes.get(i);
                        if (n.name.toLowerCase().contains(query) || n.hobbies.toLowerCase().contains(query)) {
                            System.out.printf("%-7s %-15s %-15s %-5d%n", "[" + i + "]", n.name, n.hobbies, n.age);
                            found = true;
                        }
                    }
                    if (!found) {
                        System.out.println("No matching friends found.");
                    }
                    System.out.println("------------------------------------");
                }
                case 9 -> {   // Display all persons
                    if (graph.nodes.isEmpty()) {
                        System.out.println("------------------------------------");
                        System.out.println("No persons added yet.");
                        System.out.println("------------------------------------");
                        break;
                    }
                    System.out.println("------------------------------------");
                    System.out.printf("%-7s %-15s %-15s %-5s%n", "Idx", "Name", "Hobbies", "Age");
                    for (int i = 0; i < graph.nodes.size(); i++) {
                        Node n = graph.nodes.get(i);
                        System.out.printf("%-7s %-15s %-15s %-5d%n", "[" + i + "]", n.name, n.hobbies, n.age);
                    }
                    System.out.println("------------------------------------");
                }
                case 10 -> {   // Display all circles (DFS used)
                    if (graph.nodes.isEmpty()) {
                        System.out.println("------------------------------------");
                        System.out.println("No persons added yet.");
                        System.out.println("------------------------------------");
                        break;
                    }
                    int n = graph.nodes.size();
                    boolean[] visited = new boolean[n];
                    List<List<String>> circles = new ArrayList<>();
                    for (int i = 0; i < n; i++) {
                        if (!visited[i]) {
                            List<String> circle = new ArrayList<>();
                            dfs(graph, i, visited, circle);
                            if (!circle.isEmpty()) {
                                circles.add(circle);
                            }
                        }
                    }
                    System.out.println("------------------------------------");
                    if (circles.isEmpty()) {
                        System.out.println("No circles found.");
                    } else {
                        System.out.println("All Circles:");
                        int num = 1;
                        for (List<String> circle : circles) {
                            System.out.println("Circle #" + num++ + " (" + circle.size() + "): " + String.join(", ", circle));
                        }
                    }
                    System.out.println("------------------------------------");
                }
                case 11 -> {   // View friend requests
                    if (friendRequests.isEmpty()) {
                        System.out.println("------------------------------------");
                        System.out.println("No pending friend requests.");
                        System.out.println("------------------------------------");
                        break;
                    }
                    for (int i = 0; i < graph.nodes.size(); i++) {
                        int userIdx = i;
                        List<FriendRequest> incoming = new ArrayList<>();
                        for (FriendRequest fr : friendRequests) {
                            if (fr.toIdx == userIdx) {
                                incoming.add(fr);
                            }
                        }
                        if (!incoming.isEmpty()) {
                            System.out.println("------------------------------------");
                            System.out.println("Requests for " + graph.nodes.get(userIdx).name + ":");
                            for (FriendRequest fr : incoming) {
                                Node fromNode = graph.nodes.get(fr.fromIdx);
                                System.out.printf("%-7s %-15s %-15s %-5d%n", "[" + fr.fromIdx + "]", fromNode.name, fromNode.hobbies, fromNode.age);
                            }
                            System.out.println("------------------------------------");
                        }
                    }
                    int approverIdx = readInt(input, "Enter your index to manage your requests: ");
                    if (!isValidIndex(graph, approverIdx)) {
                        break;
                    }
                    List<FriendRequest> incoming = new ArrayList<>();
                    for (FriendRequest fr : friendRequests) {
                        if (fr.toIdx == approverIdx) {
                            incoming.add(fr);
                        }
                    }
                    if (incoming.isEmpty()) {
                        System.out.println("------------------------------------");
                        System.out.println("No requests for you.");
                        System.out.println("------------------------------------");
                        break;
                    }
                    for (int i = 0; i < incoming.size(); i++) {
                        Node fromNode = graph.nodes.get(incoming.get(i).fromIdx);
                        System.out.printf("[%d] From: %s | Hobbies: %s | Age: %d\n", i, fromNode.name, fromNode.hobbies, fromNode.age);
                    }
                    System.out.println("------------------------------------");
                    String reqInput = readLine(input, "Enter request number to approve (or type 'cancel' to cancel): ");
                    if (reqInput.trim().equalsIgnoreCase("cancel")) {
                        System.out.println("------------------------------------");
                        System.out.println("Approval cancelled.");
                        System.out.println("------------------------------------");
                    } else {
                        try {
                            int reqIdx = Integer.parseInt(reqInput.trim());
                            if (reqIdx >= 0 && reqIdx < incoming.size()) {
                                FriendRequest approve = incoming.get(reqIdx);
                                graph.addEdge(approve.fromIdx, approve.toIdx);
                                friendRequests.remove(approve);
                                System.out.println("------------------------------------");
                                System.out.println("Friend request approved! You are now friends.");
                                System.out.println("------------------------------------");
                            } else {
                                System.out.println("------------------------------------");
                                System.out.println("Invalid request number.");
                                System.out.println("------------------------------------");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("------------------------------------");
                            System.out.println("Invalid input.");
                            System.out.println("------------------------------------");
                        }
                    }
                }
                case 12 -> {   // Smart Friend Suggestions
                    // If only one user exists, don't allow smart suggestion
                    if (graph.nodes.size() <= 1) {
                        System.out.println("------------------------------------");
                        System.out.println("There are no other users. Smart suggestions require at least two users.");
                        System.out.println("------------------------------------");
                        break;
                    }
                    System.out.println("------------------------------------");
                    System.out.println("----- Smart Friend Suggestions -----");
                    showPersons(graph);
                    int src = readInt(input, "Enter your index to view suggestions: ");
                    if (!isValidIndex(graph, src)) {
                        break;
                    }
                    Map<Integer, Integer> score = new HashMap<>();
                    for (int i = 0; i < graph.nodes.size(); i++) {
                        if (i == src) {
                            continue;
                        }
                        if (graph.matrix[src][i] == 1) {
                            continue;
                        }
                        if (hasPending(friendRequests, src, i)) {
                            continue;
                        }
                        if (isBlocked(blocks, src, i) || isBlocked(blocks, i, src)) {
                            continue;
                        }
                        int mutual = countMutualFriends(graph, src, i) * 5;
                        int hobby = graph.nodes.get(src).hobbies.trim().equalsIgnoreCase(graph.nodes.get(i).hobbies.trim()) ? 3 : 0;
                        int age = (Math.abs(graph.nodes.get(src).age - graph.nodes.get(i).age) <= 2) ? 1 : 0;
                        int total = mutual + hobby + age;
                        if (total > 0) {
                            score.put(i, total);
                        }
                    }
                    if (score.isEmpty()) {
                        System.out.println("No smart suggestions at this time.");
                        System.out.println("------------------------------------");
                    } else {
                        List<Map.Entry<Integer, Integer>> ranked = new ArrayList<>(score.entrySet());
                        ranked.sort((a, b) -> b.getValue() - a.getValue());
                        System.out.println("Recommended Connections:");
                        for (var rec : ranked) {
                            int idx = rec.getKey();
                            StringBuilder reasons = new StringBuilder();
                            int mutual = countMutualFriends(graph, src, idx);
                            if (mutual > 0) {
                                reasons.append(mutual).append(" mutual friends; ");
                            }
                            if (graph.nodes.get(src).hobbies.trim().equalsIgnoreCase(graph.nodes.get(idx).hobbies.trim())) {
                                reasons.append("same hobby; ");
                            }
                            if (Math.abs(graph.nodes.get(src).age - graph.nodes.get(idx).age) <= 2) {
                                reasons.append("similar age; ");
                            }
                            System.out.printf("[%d] %-15s Reason: %s\n", idx, graph.nodes.get(idx).name, reasons.toString());
                        }
                        System.out.println("------------------------------------");
                        String dstInput = readLine(input, "Enter index to send friend request or type 'cancel': ");
                        if (dstInput.trim().equalsIgnoreCase("cancel")) {
                            System.out.println("Cancelled.");
                            System.out.println("------------------------------------");
                            break;
                        }
                        int dst;
                        try {
                            dst = Integer.parseInt(dstInput.trim());
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input.");
                            System.out.println("------------------------------------");
                            break;
                        }
                        if (dst >= 0 && dst < graph.nodes.size() && score.containsKey(dst)) {
                            friendRequests.add(new FriendRequest(src, dst));
                            System.out.println("Friend request sent! Waiting for approval.");
                            System.out.println("------------------------------------");
                        } else {
                            System.out.println("Invalid choice.");
                            System.out.println("------------------------------------");
                        }
                    }
                }
                // ... rest of switch unchanged ...
                // cases 13-15, and default
                case 13 -> { // Block a person
                    if (graph.nodes.size() < 2) {
                        System.out.println("------------------------------------");
                        System.out.println("Not enough persons to block.");
                        System.out.println("------------------------------------");
                        break;
                    }
                    showPersons(graph);
                    int src = readInt(input, "Enter your index (blocker): ");
                    int dst = readInt(input, "Enter index to block: ");
                    if (!isValidIndex(graph, src) || !isValidIndex(graph, dst) || src == dst) {
                        System.out.println("------------------------------------");
                        System.out.println("Invalid indices.");
                        System.out.println("------------------------------------");
                        break;
                    }
                    if (blocks.contains(new BlockRelation(src, dst))) {
                        System.out.println("------------------------------------");
                        System.out.println("You already blocked " + graph.nodes.get(dst).name + ".");
                        System.out.println("------------------------------------");
                    } else {
                        blocks.add(new BlockRelation(src, dst));
                        graph.unfriend(src, dst); // <-- Add this line to unfriend upon blocking
                        System.out.println("------------------------------------");
                        System.out.println("You have blocked " + graph.nodes.get(dst).name + " and you are no longer friends.");
                        System.out.println("------------------------------------");
                    }
                }
                case 14 -> { // Unblock a person
                    if (graph.nodes.size() < 2) {
                        System.out.println("------------------------------------");
                        System.out.println("Not enough persons to unblock.");
                        System.out.println("------------------------------------");
                        break;
                    }
                    showPersons(graph);
                    int src = readInt(input, "Enter your index (blocker): ");
                    int dst = readInt(input, "Enter index to unblock: ");
                    if (!isValidIndex(graph, src) || !isValidIndex(graph, dst) || src == dst) {
                        System.out.println("------------------------------------");
                        System.out.println("Invalid indices.");
                        System.out.println("------------------------------------");
                        break;
                    }
                    if (blocks.remove(new BlockRelation(src, dst))) {
                        System.out.println("------------------------------------");
                        System.out.println("You have unblocked " + graph.nodes.get(dst).name + ".");
                        System.out.println("------------------------------------");
                    } else {
                        System.out.println("------------------------------------");
                        System.out.println("You did not block " + graph.nodes.get(dst).name + ".");
                        System.out.println("------------------------------------");
                    }
                }
                case 15 -> {
                    System.out.println("------------------------------------");
                    System.out.println("Exiting the program. Goodbye!");
                    System.out.println("------------------------------------");
                    input.close();
                    return;
                }
                default -> {
                    System.out.println("------------------------------------");
                    System.out.println("Invalid choice. Please select a valid option (1-15).");
                    System.out.println("------------------------------------");
                }
            }
            System.out.println();
        }
    }

    static void printMenu() {
        System.out.println("---------- Migo-Mi ----------");
        System.out.println("[1] Add Person");
        System.out.println("[2] Send Friend Request");
        System.out.println("[3] Display Network");
        System.out.println("[4] Show Mutual Friends");
        System.out.println("[5] Find Largest Circle");
        System.out.println("[6] No Friends");
        System.out.println("[7] Unfriend");
        System.out.println("[8] Search Friends");
        System.out.println("[9] Display All Persons");
        System.out.println("[10] Display All Circles");
        System.out.println("[11] View Friend Requests");
        System.out.println("[12] Smart Friend Suggestions");
        System.out.println("[13] Block a Person");
        System.out.println("[14] Unblock a Person");
        System.out.println("[15] Exit");
    }

    static void showPersons(GraphCode graph) {
        System.out.printf("%-7s %-15s %-15s %-5s%n", "Idx", "Name", "Hobbies", "Age");
        for (int i = 0; i < graph.nodes.size(); i++) {
            Node n = graph.nodes.get(i);
            System.out.printf("%-7s %-15s %-15s %-5d%n", "[" + i + "]", n.name, n.hobbies, n.age);
        }
    }

    static void printSimpleList(GraphCode graph, List<Integer> list) {
        if (list.isEmpty()) {
            System.out.println("  (None)");
        } else {
            System.out.printf("%-7s %-15s %-15s %-5s%n", "Idx", "Name", "Hobbies", "Age");
            for (int idx : list) {
                Node n = graph.nodes.get(idx);
                System.out.printf("%-7s %-15s %-15s %-5d%n", "[" + idx + "]", n.name, n.hobbies, n.age);
            }
        }
    }

    static boolean isValidIndex(GraphCode graph, int idx) {
        if (idx < 0 || idx >= graph.nodes.size()) {
            System.out.println("------------------------------------");
            System.out.println("Invalid index.");
            System.out.println("------------------------------------");
            return false;
        }
        return true;
    }

    static boolean hasPending(List<FriendRequest> friendRequests, int a, int b) {
        for (FriendRequest fr : friendRequests) {
            if ((fr.fromIdx == a && fr.toIdx == b) || (fr.fromIdx == b && fr.toIdx == a)) {
                return true;
            }
        }
        return false;
    }

    static boolean isBlocked(Set<BlockRelation> blocks, int a, int b) {
        return blocks.contains(new BlockRelation(a, b));
    }

    /**
     * Depth-First Search (DFS) algorithm to find all persons connected to the
     * current person (used for finding friend circles). Recursively visits all
     * friends of a person and marks them as visited.
     */
    static void dfs(GraphCode graph, int curr, boolean[] visited, List<String> circle) {
        visited[curr] = true;
        circle.add(graph.nodes.get(curr).name);
        for (int i = 0; i < graph.nodes.size(); i++) {
            if (graph.matrix[curr][i] == 1 && !visited[i]) {
                dfs(graph, i, visited, circle);
            }
        }
    }

    /**
     * Get mutual friends between two people (helper for suggestions).
     */
    static List<String> getMutualFriends(GraphCode graph, int idx1, int idx2) {
        List<String> mutuals = new ArrayList<>();
        for (int i = 0; i < graph.nodes.size(); i++) {
            if (i != idx1 && i != idx2 && graph.matrix[idx1][i] == 1 && graph.matrix[idx2][i] == 1) {
                mutuals.add(graph.nodes.get(i).name);
            }
        }
        return mutuals;
    }

    /**
     * Returns the count of mutual friends between two people.
     */
    static int countMutualFriends(GraphCode graph, int idx1, int idx2) {
        int count = 0;
        for (int i = 0; i < graph.nodes.size(); i++) {
            if (i != idx1 && i != idx2 && graph.matrix[idx1][i] == 1 && graph.matrix[idx2][i] == 1) {
                count++;
            }
        }
        return count;
    }

    /**
     * Reads an integer from the user with prompt.
     */
    static int readInt(Scanner input, String prompt) {
        System.out.print(prompt);
        while (!input.hasNextInt()) {
            System.out.print("Invalid input. " + prompt);
            input.next();
        }
        int value = input.nextInt();
        input.nextLine();
        return value;
    }

    /**
     * Reads a line of text from the user with prompt.
     */
    static String readLine(Scanner input, String prompt) {
        System.out.print(prompt);
        return input.nextLine();
    }
}
