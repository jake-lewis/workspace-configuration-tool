package controllers.editor;

import controllers.CommandDelegator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import model.ExceptionAlert;
import model.commands.concrete.CloseConfigCommand;
import model.commands.concrete.ConfigCommand;
import model.commands.concrete.OpenConfigCommand;
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
    private Configuration configuration;

    @FXML
    Tab textEditorTab;

    @FXML
    Tab visualEditorTab;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CommandDelegator.getINSTANCE().subscribe(new ConfigExecutor(), ConfigCommand.class);
        CommandDelegator.getINSTANCE().subscribe(new SaveExecutor(), SaveConfigCommand.class);
        CommandDelegator.getINSTANCE().subscribe(new CloseExecutor(), CloseConfigCommand.class);
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

    private class SaveExecutor implements Executor<SaveConfigCommand> {
        @Override
        public void execute(SaveConfigCommand command) throws Exception {
            File file = command.getFile();

            if (file != null) {
                switch (FileType.fromFile(file)) {
                    case XML:
                        if (configuration instanceof XMLConfiguration) {
                            try {
                                XMLConfiguration.save((XMLConfiguration) configuration, file);
                            } catch (IOException | TransformerException e) {
                                ExceptionAlert alert = new ExceptionAlert(e);
                                alert.showAndWait();
                            }
                        } else {
                            ExceptionAlert alert = new ExceptionAlert(
                                    new InvalidConfigurationException("The current configuration is of a different type to the file selected"));
                            alert.showAndWait();
                        }
                        break;
                    default:
                        ExceptionAlert alert = new ExceptionAlert(
                                new InvalidConfigurationException("Unsupported file type"));
                        alert.showAndWait();
                }
            }
        }
    }

    private class CloseExecutor implements UndoableExecutor<CloseConfigCommand> {

        @Override
        public void unexecute(CloseConfigCommand command) throws Exception {
            populate(command.getConfiguration());
        }

        @Override
        public void reexecute(CloseConfigCommand command) {
            populate(null);
        }

        @Override
        public void execute(CloseConfigCommand command) throws Exception {
            command.setConfiguration(configuration);
            populate(null);
        }
    }
}