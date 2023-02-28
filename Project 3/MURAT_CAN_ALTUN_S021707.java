import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class MURAT_CAN_ALTUN_S021707 {
    static String last = "";
    static String current = "";
    static int currentIndex = 0;

    public static void main(String[] args) {
        Scanner fileReader;
        ArrayList<String> inputAlphabet = new ArrayList<>();
        ArrayList<String> tapeAlphabet = new ArrayList<>();
        ArrayList<String> blank = new ArrayList<>();
        ArrayList<String> states = new ArrayList<>();
        ArrayList<String> startAcceptReject = new ArrayList<>();
        ArrayList<String> transitions = new ArrayList<>();
        ArrayList<String> detect = new ArrayList<>();
        ArrayList<String> route = new ArrayList<>();
        ArrayList<Character> tape = new ArrayList<>();

        File path = new File("Input_MURAT_CAN_ALTUN_S021707.txt");

        try {
            fileReader = new Scanner(path);
        } catch (FileNotFoundException e) {
            System.out.println("Invalid path, please try again.");
            return;
        }

        fill(fileReader, "INPUT", "TAPE", inputAlphabet);
        fill(fileReader, "TAPE", "BLANK", tapeAlphabet);
        fill(fileReader, "BLANK", "STATES", blank);
        fill(fileReader, "STATES", "START", states);
        fill(fileReader, "START", "ACCEPT", startAcceptReject);
        fill(fileReader, "ACCEPT", "REJECT", startAcceptReject);
        fill(fileReader, "REJECT", "TRANSITIONS", startAcceptReject);
        fill(fileReader, "TRANSITIONS", "DETECT", transitions);
        fill(fileReader, "DETECT", "END", detect);

        current = startAcceptReject.get(0);

        for (Character c : detect.get(0).toCharArray()) {
            tape.add(c);
        }
        tape.add(blank.get(0).toCharArray()[0]);

        route.add(current);

        Scanner stringScanner;

        ArrayList<Character> tape_hist1 = new ArrayList<>();
        ArrayList<Character> tape_hist2 = new ArrayList<>();
        int count = 0;
        while (!tape_hist1.equals(tape) && !tape_hist2.equals(tape) || count < transitions.size() * transitions.size()) {
            count++;

            for (String s : transitions) {
                stringScanner = new Scanner(s);

                String state = stringScanner.next();

                if (state.equals(current)) {
                    String change = stringScanner.next();

                    if(tape.get(currentIndex).equals(change.toCharArray()[0])) {
                        String newValue = stringScanner.next();
                        String shift = stringScanner.next();
                        String nextState = stringScanner.next();

                        tape.set(currentIndex, newValue.toCharArray()[0]);

                        if (shift.equals("R")) {
                            currentIndex++;
                        }
                        else if (shift.equals("L")) {
                            currentIndex--;
                        }

                        if (currentIndex < 0) {
                            break;
                        }

                        current = nextState;
                        route.add(current);
                    }
                }
            }

            if (currentIndex < 0) {
                break;
            }

            tape_hist2 = new ArrayList<>(tape_hist1);
            tape_hist1 = new ArrayList<>(tape);
        }

        StringBuilder travelled = new StringBuilder("ROUTE: ");

        for (String s : route) {
            travelled.append(s).append(" ");
        }

        System.out.println(travelled);

        if (current.equals(startAcceptReject.get(1))) {
            System.out.println("RESULT: Accepted");
        }
        else if (current.equals(startAcceptReject.get(2))) {
            System.out.println("RESULT: Rejected");
        }
        else  {
            System.out.println("RESULT: Loop");
        }
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


}
