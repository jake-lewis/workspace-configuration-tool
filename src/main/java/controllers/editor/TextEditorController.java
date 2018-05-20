package controllers.editor;

import controllers.CommandDelegator;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import model.ExceptionAlert;
import model.commands.Command;
import model.commands.concrete.UpdateConfigCommand;
import model.configuration.Configuration;
import model.configuration.ConfigurationFactory;
import model.configuration.FileType;

public class TextEditorController implements EditorController {

    private Configuration configuration;

    private TextArea textArea;

    public TextEditorController(Tab textTab) {

        BorderPane borderPane = (BorderPane) textTab.getContent();
        textArea = (TextArea) borderPane.getCenter();

        ObservableList<Node> toolbarItems = ((ToolBar) borderPane.getBottom()).getItems();

        for (Node item : toolbarItems) {
            switch (item.getId()) {
                case "resetBtn" : ((Button) item).setOnAction(event ->
                        textArea.setText(this.configuration.getTextContent()));
                break;
                case "applyBtn" : ((Button) item).setOnAction(event -> apply());
                break;
            }
        }
    }

    public void populate(Configuration configuration) {
        textArea.clear();
        this.configuration = configuration;
        if (configuration != null) {
            textArea.setText(this.configuration.getTextContent());
        }
    }

    private void apply() {
        try {
            Command command = new UpdateConfigCommand(ConfigurationFactory.create(textArea.getText(), FileType.XML), configuration);
            CommandDelegator.getINSTANCE().publish(command);
        } catch (Exception e) {
            e.printStackTrace();

            Alert alert = new ExceptionAlert(e);
            alert.showAndWait();
        }
    }
}