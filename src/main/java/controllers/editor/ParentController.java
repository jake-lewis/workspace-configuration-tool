package controllers.editor;

import controllers.CommandDelegator;
import javafx.beans.property.BooleanProperty;
import javafx.scene.control.TreeItem;
import model.ExceptionAlert;
import model.commands.concrete.*;
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
    private boolean isExecuting = false;

    private ParentController() {
        CommandDelegator.getINSTANCE().subscribe(new ConfigExecutor(), ConfigCommand.class);
        CommandDelegator.getINSTANCE().subscribe(new TreeItemExpansionExecutor(), ToggleExpandTreeItemCommand.class);
        CommandDelegator.getINSTANCE().subscribe(new TreeViewExpansionExecutor(), ToggleTreeExpansionCommand.class);
        CommandDelegator.getINSTANCE().subscribe(new SaveExecutor(), SaveConfigCommand.class);
        CommandDelegator.getINSTANCE().subscribe(new CloseExecutor(), CloseConfigCommand.class);
        CommandDelegator.getINSTANCE().subscribe(new DnDExecutor(), DnDCommand.class);
        configuration = ConfigurationFactory.getNullConfig();
    }

    public static ParentController getInstance() {
        return INSTANCE;
    }

    public Configuration getConfiguration() {
        return configuration;
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

    public boolean isExecuting() {
        return isExecuting;
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

    private class TreeItemExpansionExecutor implements UndoableExecutor<ToggleExpandTreeItemCommand> {

        @Override
        public void unexecute(ToggleExpandTreeItemCommand command) throws Exception {
            isExecuting = true;
            TreeItem item = command.getTreeView().getTreeItem(command.getIndex());
            BooleanProperty expanded = item.expandedProperty();
            expanded.set(!expanded.get());
            isExecuting = false;
        }

        @Override
        public void reexecute(ToggleExpandTreeItemCommand command) throws Exception {
            unexecute(command);
        }

        @Override
        public void execute(ToggleExpandTreeItemCommand command) throws Exception {
            //NOOP
        }
    }

    private class TreeViewExpansionExecutor implements UndoableExecutor<ToggleTreeExpansionCommand> {

        @Override
        public void unexecute(ToggleTreeExpansionCommand command) throws Exception {
            isExecuting = true;
            command.getStrategy().unapply();
            isExecuting = false;
        }

        @Override
        public void reexecute(ToggleTreeExpansionCommand command) throws Exception {
            isExecuting = true;
            command.getStrategy().reapply();
            isExecuting = false;
        }

        @Override
        public void execute(ToggleTreeExpansionCommand command) throws Exception {
            isExecuting = true;
            command.getStrategy().apply();
            isExecuting = false;
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

    private class DnDExecutor implements UndoableExecutor<DnDCommand> {

        @Override
        public void unexecute(DnDCommand command) throws Exception {
            configuration = command.getPrevConfig();
            populate(configuration);
        }

        @Override
        public void execute(DnDCommand command) throws Exception {
            configuration = command.getNewConfig();
            populate(configuration);
        }
    }
}
