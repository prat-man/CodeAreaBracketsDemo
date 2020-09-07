package in.pratanumandal.demo;

import java.util.*;

public class BracketHighlighter {

    // the code area
    private final CustomCodeArea codeArea;

    // the map of bracket pairs existing in code
    private Map<Integer, Integer> brackets;

    // the list of highlighted bracket pairs
    private List<BracketPair> bracketPairs;

    /**
     * Parameterized constructor
     * @param codeArea the code area
     */
    public BracketHighlighter(CustomCodeArea codeArea) {
        this.codeArea = codeArea;

        this.brackets = new HashMap<>();
        this.bracketPairs = new ArrayList<>();

        this.codeArea.addTextInsertionListener((start, end, text) -> clearBracket());
        this.codeArea.textProperty().addListener((obs, oldVal, newVal) -> initializeBrackets(newVal));
        this.codeArea.caretPositionProperty().addListener((obs, oldVal, newVal) -> highlightBracket(newVal));
    }

    /**
     * Method to initialize the bracket pairs (do only when text is changed)
     *
     * @param code the next text
     */
    private synchronized void initializeBrackets(String code) {

        // clear bracket map
        this.brackets.clear();

        // compute matching brackets and add to map
        Stack<Integer> stack = new Stack<>();
        int index = 0;

        while (index < code.length()) {
            int i = code.indexOf("[", index);
            int j = code.indexOf("]", index);

            if (i == -1 && j == -1) {
                break;
            } else if (i != -1 && (i < j || j == -1)) {
                stack.push(i);
                index = i + 1;
            } else if (j != -1) {
                if (!stack.isEmpty()) {
                    int k = stack.pop();
                    brackets.put(k, j);
                    brackets.put(j, k);
                }
                index = j + 1;
            }
        }

    }

    /**
     * Highlight the matching bracket at new caret position
     *
     * @param newVal the new caret position
     */
    private synchronized void highlightBracket(int newVal) {

        // first clear existing bracket highlights
        this.clearBracket();

        // detect caret position both before and after bracket
        String prevChar = (newVal > 0) ? codeArea.getText(newVal - 1, newVal) : "";
        if (prevChar.equals("[") || prevChar.equals("]")) newVal--;

        // get other half of matching bracket
        Integer other = this.brackets.get(newVal);

        if (other != null) {
            // other half exists
            BracketPair pair = new BracketPair(newVal, other);

            // highlight start
            if (pair.start < codeArea.getLength()) {
                String text = codeArea.getText(pair.start, pair.start + 1);
                if (text.equals("[") || text.equals("]")) {
                    List<String> styleList = new ArrayList<>();
                    styleList.add("loop");
                    styleList.add("match");

                    // if we run the following line with Platform.runLater, selection works
                    // but bracket highlighting breaks down
                    codeArea.setStyle(pair.start, pair.start + 1, styleList);
                }
            }

            // highlight end
            if (pair.end < codeArea.getLength()) {
                String text = codeArea.getText(pair.end, pair.end + 1);
                if (text.equals("[") || text.equals("]")) {
                    List<String> styleList = new ArrayList<>();
                    styleList.add("loop");
                    styleList.add("match");

                    // if we run the following line with Platform.runLater, selection works
                    // but bracket highlighting breaks down
                    codeArea.setStyle(pair.end, pair.end + 1, styleList);
                }
            }

            // add bracket pair to list
            this.bracketPairs.add(pair);
        }

    }

    /**
     * Highlight the matching bracket at current caret position
     */
    public synchronized void highlightBracket() {
        this.highlightBracket(codeArea.getCaretPosition());
    }

    /**
     * Clear the existing highlighted bracket styles
     */
    public synchronized void clearBracket() {

        Iterator<BracketPair> iterator = this.bracketPairs.iterator();
        while (iterator.hasNext()) {
            // get next bracket pair
            BracketPair pair = iterator.next();

            // clear start
            if (pair.start < codeArea.getLength()) {
                String text = codeArea.getText(pair.start, pair.start + 1);
                if (text.equals("[") || text.equals("]")) {
                    List<String> styleList = new ArrayList<>();
                    styleList.add("loop");

                    // if we run the following line with Platform.runLater, selection works
                    // but bracket highlighting breaks down
                    codeArea.setStyle(pair.start, pair.start + 1, styleList);
                }
            }

            // clear end
            if (pair.end < codeArea.getLength()) {
                String text = codeArea.getText(pair.end, pair.end + 1);
                if (text.equals("[") || text.equals("]")) {
                    List<String> styleList = new ArrayList<>();
                    styleList.add("loop");

                    // if we run the following line with Platform.runLater, selection works
                    // but bracket highlighting breaks down
                    codeArea.setStyle(pair.end, pair.end + 1, styleList);
                }
            }

            // remove bracket pair from list
            iterator.remove();
        }

    }

    /**
     * Class representing a pair of matching bracket indices
     */
    static class BracketPair {

        private int start;
        private int end;

        public BracketPair(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }

        @Override
        public String toString() {
            return "BracketPair{" +
                    "start=" + start +
                    ", end=" + end +
                    '}';
        }

    }

}
