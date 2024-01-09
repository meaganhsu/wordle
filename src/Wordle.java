import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Random;
import java.io.*;
public class Wordle {
    private static final String GREEN = "\033[0;32m";
    private static final String YELLOW = "\033[0;33m";
    private static final String RESET = "\033[0m";
    private static final String BOLD = "\u001B[1m";
    private static Scanner scanner;
    private static String input = null;
    public static void main(String[] args) throws IOException {
        menu();
    }
    public static void menu() throws NoSuchElementException, IOException {
        while (true) {
            scanner = new Scanner(System.in);
            System.out.println(BOLD + "WORDLE" + RESET);
            System.out.println("play");
            System.out.println("stats");
            System.out.println("quit\n");
            input = scanner.nextLine().toLowerCase();
            System.out.println();

            if (input.equals("play")) {
                System.out.println("- you have six attempts to guess a five letter word.");
                System.out.println("- " + GREEN + "green" + RESET + " indicates that the letter is in the word and in the correct position.");
                System.out.println("- " + YELLOW + "yellow" + RESET + " indicates that the letter is in the word but in the wrong position.\n");
                boolean w = game();
                addStats(w);
            } else if (input.equals("stats")) {
                stats();
            }
            else if (input.equals("quit")) {
                System.exit(0);
                break;
            }
            else {
                System.out.println("invalid. try again.\n");
            }
        }
        scanner.close();
    }
    public static boolean game() throws IOException, NoSuchElementException {
        String ans = generateAns().toLowerCase();
        scanner = new Scanner(System.in);
        int attempts = 6;
        boolean win = false;

        while (attempts > 0) {
            System.out.println("Guess #" + (7-attempts) + ": ");
            String guess = scanner.nextLine().toLowerCase();       // getting user's guess

            if (guess.length() != 5) {
                System.out.println("please enter a five-letter word.\n");
                continue;
            }
            else if (!guess.matches("[a-zA-Z]+")) {
                System.out.println("please enter alphabetical letters only.\n");
                continue;
            }
            else if (!inDictionary(guess)) {
                System.out.println("the guess must be a real word.\n");
                continue;
            }

            if (guess.equals(ans)) {
                for (char c : guess.toCharArray()) System.out.print(GREEN + c + RESET);
                win = true;
                break;       // automatic win, game ends
            }

            char[] tempAns = ans.toCharArray();
            char[] tempGuess = guess.toCharArray();
            int[] result = new int[tempGuess.length];

            for (int i = 0; i < tempGuess.length; i++) {
                if (tempGuess[i] == tempAns[i]) {
                    result[i] = 2;      // green = 2 (indicating that the element holding '2' should be coloured green)
                    tempAns[i] = ' ';
                    tempGuess[i] = ' ';       // emptying the letter so that it isn't read twice
                }
            }
            for (int i = 0; i < tempGuess.length; i++) {
                if (tempGuess[i] != ' ') {
                    for (int j = 0; j < tempAns.length; j++) {
                        if (tempGuess[i] == tempAns[j]) {
                            result[i] = 1;          // yellow = 1
                            tempAns[j] = ' ';
                            tempGuess[i] = ' ';
                            break;
                        }
                    }
                }
            }
            for (int i = 0; i < result.length; i++) {
                if (result[i] == 2) System.out.print(GREEN + guess.charAt(i) + RESET);
                else if (result[i] == 1) System.out.print(YELLOW + guess.charAt(i) + RESET);
                else System.out.print(guess.charAt(i));
            }

            attempts--;
            System.out.println("\n");
            System.out.println("you have " + attempts + " attempt(s) left.");
        }

        if (win) {
            System.out.println("\n\ngood job! you guessed the word in " + (7-attempts) + " attempt(s).");
            System.out.println("thanks for playing.\n");
            return true;
        }
        else  {
            System.out.println("\n\nyou ran out of guesses. the answer was " + GREEN + ans + RESET + ".");
            System.out.println("better luck next time!\n");
            return false;
        }

    }
    public static void addStats (boolean win) throws IOException, NoSuchElementException {
        File statsFile = new File ("stats.txt");
        FileWriter fileWriter = new FileWriter(statsFile, true);
        Scanner statsScan = new Scanner(statsFile);
        scanner = new Scanner(System.in);
        boolean userFound = false;
        boolean invalid = false;
        stats();

        while (true) {
            System.out.println("if you already have a previous save, enter the name the save.\nif not, enter a new name and create a new save.");
            String player = scanner.nextLine().toLowerCase().replaceAll("\\s", "");
            System.out.println();
            int n = 0;     // line no.

            while (statsScan.hasNextLine()) {
                String[] line = statsScan.nextLine().split(" ");    // creating array for each line
                String name = "";
                for (int i = 0; i < line.length-2; i++) {       // names w/ spaces
                    name += line[i];
                    if (i != line.length-3) name += " ";
                }
                String temp = name.replaceAll("\\s", "").toLowerCase();     // removing white spaces

                if (player.equals(temp)) {
                    System.out.println("this you?");
                    int gamesPlayed = Integer.parseInt(line[line.length-2]);
                    int gamesWon = Integer.parseInt(line[line.length-1]);
                    int winPercentage = (gamesWon * 100)/ gamesPlayed;
                    System.out.println("player: " + name);
                    System.out.println("total played: " + gamesPlayed);
                    System.out.println("games won: " + gamesWon + " | " + winPercentage + "%\n");
                    input = scanner.nextLine().toLowerCase();      // y or n input
                    System.out.println();

                    if (input.equals("n") || input.equals("no")) {
                        invalid = true;      // —> re-prompt
                        break;
                    }
                    else if (input.equals("y") || input.equals("yes")) {
                        gamesPlayed++;
                        if (win) gamesWon++;
                        String update = name + " " + gamesPlayed + " " + gamesWon;     // amended line
                        updateStats("stats.txt", n+1, update);
                        userFound = true;
                        break;
                    } else {
                        System.out.println("invalid input. ");
                        invalid = true;
                    }
                }
                n++;      // line no.
            }
            if (invalid) invalid = false;        // —> re-prompt
            else if (userFound) break;
            else {
                System.out.println();
                input = "";
                if (win) fileWriter.write("\n" + player + " 1 1");
                else fileWriter.write("\n" + player + " 1 0");
                fileWriter.close();
                break;
            }
        }
        statsScan.close();
    }
    public static void stats() throws FileNotFoundException, java.io.IOException {
        File statsFile = new File("stats.txt");
        Scanner statsScan = new Scanner(statsFile);
        while (statsScan.hasNextLine()) {
            String data = statsScan.nextLine();
            String[] stats = data.split(" ");
            String name = "";
            for (int i = 0; i < stats.length-2; i++) name += stats[i] + " ";  // everything but the last two elements = name
            int gamesPlayed = Integer.parseInt(stats[stats.length-2]);
            int gamesWon = Integer.parseInt(stats[stats.length-1]);
            int winPercentage = (gamesWon * 100)/ gamesPlayed;
            System.out.println("player: " + name);
            System.out.println("total played: " + gamesPlayed);
            System.out.println("games won: " + gamesWon + " | " + winPercentage + "%\n");
        }
        System.out.println();
        statsScan.close();
    }
    public static String generateAns() throws FileNotFoundException {
        File ansFile = new File("words.txt");
        Scanner ansScanner = new Scanner(ansFile);
        Random random = new Random();
        int x = random.nextInt(2307);     // generating random number —> (answer)
        String ans = null;

        for (int i = 0; i <= x; i++) {
            if (i == x) {
                ans = String.valueOf(ansScanner.nextLine());
            } else ansScanner.nextLine();
        }

        ansScanner.close();
        return ans;
    }
    public static boolean inDictionary(String guess) throws FileNotFoundException {
        File wordsFile = new File("dictionary.txt");
        Scanner dictScanner = new Scanner(wordsFile);
        while (dictScanner.hasNextLine()) {
            if (dictScanner.nextLine().equals(guess)) {
                dictScanner.close();
                return true;
            }
        }
        dictScanner.close();
        return false;
    }
    public static void updateStats(String filePath, int lineNum, String replace) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        StringBuilder data = new StringBuilder();
        String line;
        int x = 1;

        while (reader.ready()) {      // read and modifying file line by line
            line = reader.readLine();

            if (x == lineNum) data.append(replace);
            else data.append(line);

            if (!reader.ready()) break;
            data.append("\n");      // append \n only if not the final line
            x++;
        }

        PrintWriter writer = new PrintWriter(new FileWriter(filePath)); // writing modified content back to file
        writer.write(data.toString());
        reader.close();
        writer.close();
    }
}