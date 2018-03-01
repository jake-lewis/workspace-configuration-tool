package controllers.editor;

import controllers.CommandDelegator;
import model.ExceptionAlert;
import model.commands.concrete.CloseConfigCommand;
import model.commands.concrete.ConfigCommand;
import model.commands.concrete.SaveConfigCommand;
import model.configuration.*;
import model.executors.Executor;
import model.executors.UndoableExecutor;

import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParentController implements EditorController {

    private static ParentController INSTANCE = new ParentController();
    private static List<EditorController> controllers = new ArrayList<>();
    private Configuration configuration;

    private ParentController() {
        CommandDelegator.getINSTANCE().subscribe(new ConfigExecutor(), ConfigCommand.class);
        CommandDelegator.getINSTANCE().subscribe(new SaveExecutor(), SaveConfigCommand.class);
        CommandDelegator.getINSTANCE().subscribe(new CloseExecutor(), CloseConfigCommand.class);
        configuration = ConfigurationFactory.getNullConfig();
    }

    public static ParentController getInstance() {
        return INSTANCE;
    }

    public void register(EditorController controller) {
        controllers.add(controller);
    }

    @Override
    public void populate(Configuration configuration) {
        for (EditorController controller: controllers) {
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
