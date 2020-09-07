package in.pratanumandal.demo;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

public class Controller {

    @FXML private BorderPane mainPane;

    @FXML
    public void initialize() {

        // create a new code area
        CustomCodeArea codeArea = new CustomCodeArea();

        // add code area to main pane
        mainPane.setCenter(codeArea);

        // highlight brackets
        BracketHighlighter bracketHighlighter = new BracketHighlighter(codeArea);

        // auto complete loops
        codeArea.setOnKeyTyped(keyEvent -> {
            // clear bracket highlighting
            bracketHighlighter.clearBracket();

            // get typed character
            String character = keyEvent.getCharacter();

            // add a ] if [ is typed
            if (character.equals("[")) {
                int position = codeArea.getCaretPosition();
                codeArea.insert(position, "]", "loop");
                codeArea.moveTo(position);
            }
            // remove next ] if ] is typed
            else if (character.equals("]")) {
                int position = codeArea.getCaretPosition();
                if (position != codeArea.getLength()) {
                    String nextChar = codeArea.getText(position, position + 1);
                    if (nextChar.equals("]")) codeArea.deleteText(position, position + 1);
                }
            }

            // refresh bracket highlighting
            bracketHighlighter.highlightBracket();
        });

    }

}
