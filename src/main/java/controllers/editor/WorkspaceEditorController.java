package controllers.editor;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import model.configuration.Configuration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class WorkspaceEditorController implements Initializable, EditorController {

    private List<EditorController> editorControllers = new ArrayList<>();

    @FXML
    private Tab applyConfigTab;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        editorControllers.add(new ApplyConfigurationController(applyConfigTab));
        ParentController.getInstance().register(this);
    }

    @Override
    public void populate(Configuration configuration) {
        for (EditorController controller: editorControllers) {
            controller.populate(configuration);
        }
    }
}
