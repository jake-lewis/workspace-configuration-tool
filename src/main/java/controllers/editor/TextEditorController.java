package controllers.editor;

import javafx.scene.control.TextArea;
import model.configuration.Configuration;

public class TextEditorController implements EditorController {

    private TextArea textArea;

    public TextEditorController(TextArea textEditor) { textArea = textEditor; }

    public void populate(Configuration configuration) {
        textArea.clear();
        textArea.setText(configuration.getTextContent());
    }
}