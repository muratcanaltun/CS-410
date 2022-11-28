import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class NFAtoDFA {
    static String last = "";
    public static void main(String[] args) {
        Scanner fileReader;
        ArrayList<String> alphabet = new ArrayList<>();
        ArrayList<String> states = new ArrayList<>();
        ArrayList<String> startStop = new ArrayList<>();
        HashMap<String, ArrayList<String>> transitions = new HashMap<>();
        File path = new File("NFA1.txt");

        try {
            fileReader = new Scanner(path);
        } catch (FileNotFoundException e) {
            System.out.println("Invalid path, please try again.");
            return;
        }
        // Fill the alphabet and states arraylists
        fill(fileReader, "ALPHABET", "STATES", alphabet);
        fill(fileReader, "STATES", "START", states);

        // Assign the starting state to startStop member 0, assign the final state to startStop member 1 onwards
        fill(fileReader, "START", "FINAL", startStop);
        fill(fileReader, "FINAL", "TRANSITIONS", startStop);

        // Initialize a map for the transitions
        // The map works with pairs like the following:
        // The state names are used as keys, and their values are lists that hold
        // the transitions for ALL the symbols in the alphabet
        initializeMap(transitions, states, alphabet.size());

        // Fill the map from the transitions
        fillMap(transitions, alphabet, fileReader);

        // Add the extra states for symbols like AB, BC, AC, ABC
        HashMap<String, ArrayList<String>> transition_hist1 = new HashMap<>();
        HashMap<String, ArrayList<String>> transition_hist2 = new HashMap<>();

        while (!transition_hist1.equals(transitions) || !transition_hist2.equals(transitions)) {
            initializeExtra(transitions, states, alphabet.size());
            fillExtra(transitions, states);

            transition_hist2 = new HashMap<>(transition_hist1);
            transition_hist1 = new HashMap<>(transitions);
        }

        ArrayList<String> tmp = new ArrayList<>();

        for (String s : transitions.keySet()) {
            for (int i = 1; i < startStop.size(); i++) {
                if (s.contains(startStop.get(i))) {
                    tmp.add(s);
                }
            }
        }

        for (String s : tmp) {
            if (!startStop.contains(s)) {
                startStop.add(s);
            }
        }

        printMap(alphabet, states, startStop, transitions);
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

    static void initializeMap(HashMap<String, ArrayList<String>> map, ArrayList<String> states, int alphabetLength) {
        for (String s : states) {
            ArrayList<String> keys = new ArrayList<>();

            for (int i = 0; i < alphabetLength; i++) {
                keys.add("");
            }

            map.put(s, keys);
        }
    }

    static void fillMap(HashMap<String, ArrayList<String>> map, ArrayList<String> alphabet, Scanner fileReader) {
        Scanner stringScanner;

        if (last.equals("TRANSITIONS") || fileReader.nextLine().equals("TRANSITIONS")) {
            while (fileReader.hasNext()) {
                last = fileReader.nextLine();

                if (!last.equals("END")) {
                    stringScanner = new Scanner(last);

                    String key = stringScanner.next();
                    String symbol = stringScanner.next();
                    String value = stringScanner.next();

                    ArrayList<String> tempList = map.get(key);
                    ArrayList<String> newList = new ArrayList<>(tempList);

                    newList.set(alphabet.indexOf(symbol), newList.get(alphabet.indexOf(symbol)) + value);

                    map.replace(key, tempList, newList);
                } else {
                    break;
                }
            }
        } else {
            System.out.println("Could not find TRANSITIONS. Exiting...");
        }
    }

    static void initializeExtra(HashMap<String, ArrayList<String>> map, ArrayList<String> states, int alphabetLength) {
        HashMap<String, ArrayList<String>> tempMap = new HashMap<>();
        for (Map.Entry<String, ArrayList<String>> set : map.entrySet()) {
            ArrayList<String> temp = new ArrayList<>(set.getValue());

            temp.removeAll(states);

            if (temp.size() > 0) {
                for (String s : temp) {
                    if (s.length() > 1) {
                        ArrayList<String> keys = new ArrayList<>();

                        String trimmed = removeDuplicate(s.toCharArray(), s.length());

                        for (int i = 0; i < alphabetLength; i++) {
                            keys.add("");
                        }
                        tempMap.put(trimmed, keys);
                    }
                }
            }
        }
        tempMap.forEach(map::putIfAbsent);
    }

    static void fillExtra(HashMap<String, ArrayList<String>> map, ArrayList<String> states) {
        HashMap<String, ArrayList<String>> tempMap = new HashMap<>(map);

        for (Map.Entry<String, ArrayList<String>> set : map.entrySet()) {
            if (!states.contains(set.getKey())) {
                ArrayList<String> temp = new ArrayList<>(set.getValue());
                int size = temp.size();

                for (String s : states) {
                    if (set.getKey().contains(s)) {
                        temp.addAll(map.get(s));
                    }
                }

                for (int i = 0; i < temp.size(); i++) {
                    int modulo = i % size;
                    if(!temp.get(modulo).contains(temp.get(i))) {
                        temp.set(modulo, temp.get(modulo) + temp.get(i));
                    }
                }
                ArrayList<String> temp2 = new ArrayList<>();

                for (int i = 0; i < size; i++) {
                    String deleted = removeDuplicate(temp.get(i).toCharArray(), temp.get(i).length());
                    sortString(deleted);
                    temp2.add(deleted);
                }
                temp2.set(0, sortString(temp2.get(0)));
                tempMap.put(set.getKey(), temp2);
                states.add(set.getKey());
            }
        }
        tempMap.forEach(map::put);
    }

    static void printMap(ArrayList<String> alphabet, ArrayList<String> states, ArrayList<String> startStop, HashMap<String, ArrayList<String>> transitions) {
        System.out.println("ALPHABET");
        for (String s : alphabet) {
            System.out.println(s);
        }

        System.out.println("STATES");
        for (String s : states) {
            System.out.println(s);
        }

        System.out.println("START");
        System.out.println(startStop.get(0));

        System.out.println("FINAL");
        for (int i = 1; i < startStop.size(); i++) {
            System.out.println(startStop.get(i));
        }

        System.out.println("TRANSITIONS");
        for (Map.Entry<String, ArrayList<String>> set : transitions.entrySet()) {
            for (int i = 0; i < set.getValue().size(); i++) {
                if (!set.getValue().get(i).equals("")) {
                    System.out.println(set.getKey() + " " + alphabet.get(i) + " " + set.getValue().get(i));
                }
            }
        }

        System.out.println("END");
    }

    static String removeDuplicate(char str[], int n)
    {
        int index = 0;
        for (int i = 0; i < n; i++) {
            int j;
            for (j = 0; j < i; j++) {
                if (str[i] == str[j]) {
                    break;
                }
            }
            if (j == i) {
                str[index++] = str[i];
            }
        }
        return String.valueOf(Arrays.copyOf(str, index));
    }

    public static String sortString(String inputString)
    {
        char tempArray[] = inputString.toCharArray();
        Arrays.sort(tempArray);
        return new String(tempArray);
    }
}
