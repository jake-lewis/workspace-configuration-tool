package controllers.editor;

import controllers.CommandDelegator;
import javafx.beans.property.BooleanProperty;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import model.ExceptionAlert;
import model.commands.concrete.*;
import model.configuration.*;
import model.executors.Executor;
import model.executors.UndoableExecutor;

import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class ParentController implements EditorController {

    private static ParentController INSTANCE = new ParentController();
    private static List<EditorController> controllers = new ArrayList<>();
    private Configuration configuration;
    private boolean isExecuting = false;

    private ParentController() {
        CommandDelegator.getINSTANCE().subscribe(new ConfigExecutor(), ConfigCommand.class);
        CommandDelegator.getINSTANCE().subscribe(new SingleExpansionExecutor(), ToggleExpandTreeItemCommand.class);
        CommandDelegator.getINSTANCE().subscribe(new ExpansionExecutor(), ExpandAllTreeCommand.class);
        CommandDelegator.getINSTANCE().subscribe(new CollapseExecutor(), CollapseAllTreeCommand.class);
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

    public boolean isExecuting() {
        return isExecuting;
    }

    private void expandTreeItem(TreeItem<?> item) {
        if (item != null && !item.isLeaf()) {
            item.setExpanded(true);
            for (TreeItem<?> child : item.getChildren()) {
                expandTreeItem(child);
            }
        }
    }

    private void collapseTreeItem(TreeItem<?> item) {
        //todo make command
        if (item != null && !item.isLeaf()) {

            //Don't collapse if it's the root
            if (item.getParent() != null) {
                item.setExpanded(false);
            }

            for (TreeItem<?> child : item.getChildren()) {
                collapseTreeItem(child);
            }
        }
    }

    private void applyExpansion(TreeItem sourceItem, TreeItem targetItem) {
        targetItem.setExpanded(sourceItem.isExpanded());

        ListIterator sourceIterator = sourceItem.getChildren().listIterator();
        ListIterator targetIterator = targetItem.getChildren().listIterator();

        while (sourceIterator.hasNext() && targetIterator.hasNext()) {
            applyExpansion((TreeItem) sourceIterator.next(), (TreeItem) targetIterator.next());
        }
    }

    private TreeView copyTreeView(TreeView source) {
        TreeView copy = new TreeView();

        if (source.getRoot() != null) {
            copy.setShowRoot(source.isShowRoot());
            copy.setRoot(copyTreeItem(source.getRoot()));
        }
        return copy;
    }

    private TreeItem copyTreeItem(TreeItem item) {
        TreeItem copy = new TreeItem();
        copy.setValue(item.getValue());
        copy.setGraphic(item.getGraphic());
        copy.setExpanded(item.isExpanded());

        for (Object subItem: item.getChildren()) {
            copy.getChildren().add(copyTreeItem((TreeItem) subItem));
        }

        return copy;
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

    private class SingleExpansionExecutor implements UndoableExecutor<ToggleExpandTreeItemCommand> {

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

    private class ExpansionExecutor implements UndoableExecutor<ExpandAllTreeCommand> {

        @Override
        public void unexecute(ExpandAllTreeCommand command) throws Exception {
            TreeItem prevRoot = command.getPrevTreeView().getRoot();
            TreeItem newRoot = command.getNewTreeView().getRoot();

            if (prevRoot != null && newRoot != null) {
                applyExpansion(prevRoot, newRoot);
            }
        }

        @Override
        public void execute(ExpandAllTreeCommand command) throws Exception {
            TreeView<?> treeView = command.getPrevTreeView();
            command.setPrevTreeView(copyTreeView(command.getPrevTreeView()));
            if (treeView.getRoot() != null) {
                isExecuting = true;
                expandTreeItem(treeView.getRoot());
                isExecuting = false;
                command.setNewTreeView(treeView);
            }
        }
    }

    private class CollapseExecutor implements UndoableExecutor<CollapseAllTreeCommand> {

        @Override
        public void unexecute(CollapseAllTreeCommand command) throws Exception {
            TreeItem prevRoot = command.getPrevTreeView().getRoot();
            TreeItem newRoot = command.getNewTreeView().getRoot();

            if (prevRoot != null && newRoot != null) {
                applyExpansion(prevRoot, newRoot);
            }
        }

        @Override
        public void execute(CollapseAllTreeCommand command) throws Exception {
            TreeView<?> treeView = command.getPrevTreeView();
            command.setPrevTreeView(copyTreeView(command.getPrevTreeView()));
            if (treeView.getRoot() != null) {
                isExecuting = true;
                collapseTreeItem(treeView.getRoot());
                isExecuting = false;
                command.setNewTreeView(treeView);
            }
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
