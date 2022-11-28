import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class MURATCAN_ALTUN_S021707 {
    static String last = "";
    static String start = "";
    static HashMap<Character, String> lowercaseReplacements = new HashMap<Character, String>();

    public static void main(String[] args) {
        Scanner fileReader;
        ArrayList<String> nonTerminal = new ArrayList<>();
        ArrayList<String> terminal = new ArrayList<>();
        HashMap<String, String> rules = new HashMap<>();
        File path = new File("C:\\Users\\muratcan.altun\\IdeaProjects\\CFGtoChomsky\\src\\G1.txt");

        try {
            fileReader = new Scanner(path);
        } catch (FileNotFoundException e) {
            System.out.println("Invalid path, please try again.");
            return;
        }

        fill(fileReader, "NON-TERMINAL", "TERMINAL", nonTerminal);
        fill(fileReader, "TERMINAL", "RULES", terminal);

        initializeMap(rules, nonTerminal);

        fillMap(rules, terminal, fileReader);

        start = fileReader.nextLine();
        addStart(nonTerminal, rules);

        boolean hasEpsilon = true;
        do {
            removeEpsilon(rules);
            for (String s : rules.values()) {
                hasEpsilon = s.contains("e");
            }
        } while (hasEpsilon);

        boolean hasUnit = true;
        do {
            removeUnit(rules, nonTerminal);

            ArrayList<String> values = new ArrayList<>(rules.values());
            for (String s : values) {
                ArrayList<String> split = new ArrayList<>(Arrays.asList(s.split(" \\| ")));
                for (String str : split) {
                    hasUnit = str.length() == 1;
                }
            }

        } while (hasUnit);

        printMap(nonTerminal, terminal, rules, start);
    }

    static void fill(Scanner fileReader, String start, String stop, ArrayList<String> arrayList) {
        if (last.equals(start) || fileReader.nextLine().equals(start)) {
            while (fileReader.hasNext()) {
                last = fileReader.nextLine();

                if (!last.equals(stop)) {
                    arrayList.add(last);
                } else {
                    break;
                }
            }
        } else {
            System.out.println("Could not find " + start +". Exiting...");
        }
    }

    static void initializeMap(HashMap<String, String> map, ArrayList<String> terminal) {
        for (String s : terminal) {
            map.put(s, "");
        }
    }

    static void fillMap(HashMap<String, String> map, ArrayList<String> alphabet, Scanner fileReader) {
        if (last.equals("RULES") || fileReader.nextLine().equals("RULES")) {
            while (fileReader.hasNext()) {
                last = fileReader.nextLine();

                if (!last.equals("START")) {
                    if (map.get(last.substring(0,1)).equals("")) {
                        map.replace(last.substring(0,1), map.get(last.substring(0,1)), last.substring(2));
                    } else {
                        map.replace(last.substring(0,1), map.get(last.substring(0,1)), map.get(last.substring(0,1)) + " | " + last.substring(2));
                    }

                } else {
                    break;
                }
            }
        } else {
            System.out.println("Could not find RULES. Exiting...");
        }
    }

    static void printMap(ArrayList<String> nonTerminal, ArrayList<String> terminal, HashMap<String, String> rules, String start) {
        System.out.println("NON-TERMINAL");
        for (String s : nonTerminal) {
            System.out.println(s);
        }

        System.out.println("TERMINAL");
        for (String s : terminal) {
            System.out.println(s);
        }

        System.out.println("RULES");
        for (String s : rules.keySet()) {
            String[] split = rules.get(s).split(" \\| ");
            for (String str : split) {
                System.out.println(s + ":" + str);
            }
        }

        System.out.println("START");
        System.out.println(start);
    }

    static void addStart(ArrayList<String> nonTerminal, HashMap<String, String> rules) {
        char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        Random random = new Random();

        do {
            start = "" + alphabet[random.nextInt(alphabet.length)];
        } while (nonTerminal.contains(start));

        String s = nonTerminal.get(0);
        nonTerminal.add(0, start);
        rules.put(start, s);
    }

    static void removeEpsilon(HashMap<String, String> rules) {
        String toChange = "";

        // Find the last occurrence of epsilon
        for (String s : rules.keySet()) {
            ArrayList<String> values = new ArrayList<>(Arrays.asList(rules.get(s).split(" \\| ")));
            if (values.contains("e")) {
                toChange = s;
            }
        }

        // Remove the found epsilon
        for (String s : rules.keySet()) {
            if (s.equals(toChange)) {
                rules.replace(s, rules.get(s).replace("| e", ""));
            }
        }

        // Add new occurrences
        for (String s : rules.keySet()) {
            ArrayList<String> values = new ArrayList<>(Arrays.asList(rules.get(s).split(" \\| ")));
            ArrayList<String> newValues = new ArrayList<>();

            for (String value : values) {
                if (value.contains(toChange)) {
                    String temp = "";
                    for (char c : value.toCharArray()) {

                        if (!("" + c).equals(toChange)) {
                            temp += c;
                        }
                    }
                    newValues.add(temp);
                }
            }

            for (String value : newValues) {
                values.add(value);
            }

            String finalString = values.get(0);

            if (values.size() > 1) {
                for (int i = 1; i < values.size(); i++) {
                    finalString += " | " + values.get(i);
                }
            }

            rules.replace(s, finalString);
        }
    }

    static void removeUnit(HashMap<String, String> rules, ArrayList<String> nonTerminal) {
        String toChange = "";

        // Find which non-terminal to remove
        ArrayList<String> values = new ArrayList<>(rules.values());
        for (String s : values) {
            ArrayList<String> split = new ArrayList<>(Arrays.asList(s.split(" \\| ")));
            for (String str : split) {
                if (str.length() == 1 && nonTerminal.contains(str)) {
                    toChange = str;
                }
            }
        }

        // Add the rules of the said non-terminal and remove it from all terminals
        ArrayList<String> toAddValues = new ArrayList<>(Arrays.asList(rules.get(toChange).split(" \\| ")));
        for (String s : rules.keySet()) {
            ArrayList<String> split = new ArrayList<>(Arrays.asList(rules.get(s).split(" \\| ")));

            if (split.contains(toChange)) {
                for (String str : toAddValues) {
                    if (!split.contains(str)) {
                        split.add(str);
                    }
                }
                split.remove(toChange);

                String newStr = split.get(0);
                if (split.size() > 1) {
                    for (int i = 1; i < split.size(); i++) {
                        newStr += " | " + split.get(i);
                    }
                }
                rules.put(s, newStr);
            }
        }
    }


}
