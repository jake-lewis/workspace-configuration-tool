package controllers.editor;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import model.configuration.Configuration;

public class TextEditorController implements EditorController {

    private String textContent;

    private TextArea textArea;

    public TextEditorController(Tab textTab) {

        BorderPane borderPane = (BorderPane) textTab.getContent();
        textArea = (TextArea) borderPane.getCenter();

        ObservableList<Node> toolbarItems = ((ToolBar) borderPane.getBottom()).getItems();

        for (Node item : toolbarItems) {
            switch (item.getId()) {
                case "resetBtn" : ((Button) item).setOnAction(event -> textArea.setText(textContent));
                break;
                case "applyBtn" : ((Button) item).setOnAction(event -> apply());
            }
        }
    }

    public void populate(Configuration configuration) {
        textArea.clear();
        textContent = configuration.getTextContent();
        textArea.setText(textContent);
    }

    private void apply() {
        System.out.println("apply");
    }
}