package controllers.editor;

import controllers.CommandDelegator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import model.commands.concrete.ConfigCommand;
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
        CommandDelegator.getINSTANCE().subscribe(new ConfigExecutor(), ConfigCommand.class);
        editorControllers.add(new VisualEditorController(visualEditorTab));
        editorControllers.add(new TextEditorController(textEditorTab));
        configuration = ConfigurationFactory.getNullConfig();
    }

    @Override
    public void populate(Configuration configuration) {
        for (EditorController controller : editorControllers) {
            controller.populate(configuration);
        }
    }

    private class ConfigExecutor implements UndoableExecutor<ConfigCommand> {

        @Override
        public void execute(ConfigCommand command) throws Exception {
            command.setPrevConfig(configuration);
            configuration = command.getNextConfig();
            populate(configuration);
        }

        @Override
        public void unexecute(ConfigCommand command) throws Exception {
            configuration = command.getPrevConfig();
            populate(configuration);
        }

        @Override
        public void reexecute(ConfigCommand command) throws Exception {
            configuration = command.getNextConfig();
            populate(configuration);
        }
    }
}