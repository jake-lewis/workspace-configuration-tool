package controllers.editor;

import controllers.CommandDelegator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeView;
import javafx.scene.layout.GridPane;
import model.configuration.Directory;
import model.commands.concrete.OpenConfigCommand;
import model.configuration.Configuration;
import model.configuration.ConfigurationFactory;
import model.executors.UndoableExecutor;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ConfigurationEditorController implements Initializable, EditorController, UndoableExecutor<OpenConfigCommand> {

    private List<EditorController> editorControllers = new ArrayList<>();
    private Configuration configuration;

    @FXML
    TreeView<Directory> visualEditor;

    @FXML
    TextArea textEditor;

    @FXML
    GridPane projectPropertiesPane;

    @FXML
    GridPane nodePropertiesPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CommandDelegator.getINSTANCE().subscribe(this, OpenConfigCommand.class);
        editorControllers.add(new VisualEditorController(visualEditor, projectPropertiesPane, nodePropertiesPane));
        editorControllers.add(new TextEditorController(textEditor));
        configuration = ConfigurationFactory.create();
    }

    @Override
    public void execute(OpenConfigCommand command) throws Exception {
        command.setPrevConfig(configuration);
        configuration = ConfigurationFactory.create(command.getFile());
        command.setNewConfig(configuration);
        populate(configuration);
    }

    @Override
    public void unexecute(OpenConfigCommand command) throws Exception {
        configuration = command.getPrevConfig();
        populate(configuration);
    }

    @Override
    public void reexecute(OpenConfigCommand command) throws Exception {
        configuration = command.getNextConfig();
        populate(configuration);
    }

    @Override
    public void populate(Configuration configuration) {
        for (EditorController controller : editorControllers) {
            controller.populate(configuration);
        }
    }
}