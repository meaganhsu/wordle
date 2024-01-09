import java.util.Scanner;
public class Main {
    private static Scanner scan = new Scanner(System.in);
    private static String input = "";
    public static void main(String[] args) {
        menu();
    }
    public static void game() {
        int attempts = 0;
        int x = Math.random() *

    }
    public static void menu() {
        System.out.println("\u001B[1m" + "\t\tWORDLE" + "\u001B[0m");
        System.out.println("\t\tplay");
        System.out.println("\t\tstats");
        System.out.println("\t\tquit");
        input = scan.next().toLowerCase();
        if (input.equals("play")) game();
        else if (input.equals("stats")) stats();
        else if (input.equals("quit")) System.exit(0);
        else {
            System.out.println("invalid input.");
            System.out.print("\033[H\033[2J");
            System.out.flush();
            menu();
        }
    }
    public static void stats() {
        System.out.println("stats");
    }
    public static void addStats() {
        System.out.println("add");
    }
}