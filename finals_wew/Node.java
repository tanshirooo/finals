package finals_wew;

public class Node {
    char data;        // Usually the first character of the name.
    String name;      // Person's name.
    String hobbies;   // Person's hobbies.
    int age;          // Person's age.

    public Node(char data, String name, String hobbies, int age) {
        this.data = data;
        this.name = name;
        this.hobbies = hobbies;
        this.age = age;
    }
}
