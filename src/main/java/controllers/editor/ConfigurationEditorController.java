package controllers.editor;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeView;
import model.Directory;

import java.net.URL;
import java.util.ResourceBundle;

public class ConfigurationEditorController implements Initializable {

    private VisualEditorController visualEditorController;
    private TextEditorController textEditorController;

    @FXML
    TreeView<Directory> visualEditor;

    @FXML
    TextArea textEditor;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        visualEditorController = new VisualEditorController(visualEditor);
        textEditorController = new TextEditorController(textEditor);
    }
}