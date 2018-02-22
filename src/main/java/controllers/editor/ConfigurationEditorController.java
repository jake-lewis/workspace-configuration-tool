package controllers.editor;

import controllers.CommandDelegator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
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

public class ConfigurationEditorController implements Initializable, EditorController {

    private List<EditorController> editorControllers = new ArrayList<>();
    private Configuration configuration;

    @FXML
    Tab textEditorTab;

    @FXML
    Tab visualEditorTab;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CommandDelegator.getINSTANCE().subscribe(new OpenConfigExecutor(), OpenConfigCommand.class);
        editorControllers.add(new VisualEditorController(visualEditorTab));
        editorControllers.add(new TextEditorController(textEditorTab));
        configuration = ConfigurationFactory.create();
    }

    @Override
    public void populate(Configuration configuration) {
        for (EditorController controller : editorControllers) {
            controller.populate(configuration);
        }
    }

    private class OpenConfigExecutor implements UndoableExecutor<OpenConfigCommand> {

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
    }
}