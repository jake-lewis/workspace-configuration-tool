package controllers.editor;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeView;

import java.net.URL;
import java.util.ResourceBundle;

public class ConfigurationEditorController implements Initializable {

    @FXML
    TreeView visualEditor;

    @FXML
    TextArea textEditor;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        VisualEditorController visualEditorController = new VisualEditorController(visualEditor);
        TextEditorController textEditorController = new TextEditorController(textEditor);
    }
}
