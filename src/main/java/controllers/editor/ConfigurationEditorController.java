package controllers.editor;

import controllers.CommandDelegator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import model.ExceptionAlert;
import model.commands.concrete.CloseConfigCommand;
import model.commands.concrete.EditorConfigCommand;
import model.commands.concrete.SaveConfigCommand;
import model.configuration.*;
import model.executors.Executor;
import model.executors.UndoableExecutor;

import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ConfigurationEditorController implements Initializable, EditorController {

    private List<EditorController> editorControllers = new ArrayList<>();

    @FXML
    Tab textEditorTab;

    @FXML
    Tab visualEditorTab;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        editorControllers.add(new VisualEditorController(visualEditorTab));
        editorControllers.add(new TextEditorController(textEditorTab));
        ParentController.getInstance().register(this);
    }

    @Override
    public void populate(Configuration configuration) {
        for (EditorController controller : editorControllers) {
            controller.populate(configuration);
        }
    }
}