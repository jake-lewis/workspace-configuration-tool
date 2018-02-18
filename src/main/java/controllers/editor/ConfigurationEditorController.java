package controllers.editor;

import controllers.CommandDelegator;
import model.configuration.Configuration;
import model.configuration.ConfigurationFactory;
import model.commands.concrete.OpenConfigCommand;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeView;
import model.Directory;
import model.executors.UndoableExecutor;

import java.net.URL;
import java.util.ResourceBundle;

public class ConfigurationEditorController implements Initializable, UndoableExecutor<OpenConfigCommand> {

    private VisualEditorController visualEditorController;
    private TextEditorController textEditorController;
    private Configuration configuration;

    @FXML
    TreeView<Directory> visualEditor;

    @FXML
    TextArea textEditor;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CommandDelegator.getINSTANCE().subscribe(this, OpenConfigCommand.class);
        visualEditorController = new VisualEditorController(visualEditor);
        textEditorController = new TextEditorController(textEditor);
        configuration = ConfigurationFactory.create();
    }

    @Override
    public void execute(OpenConfigCommand command) throws Exception {
        command.setPrevConfig(configuration);
        configuration = ConfigurationFactory.create(command.getFile());
        command.setNewConfig(configuration);
        visualEditorController.populateVisualEditor(configuration);
    }

    @Override
    public void unexecute(OpenConfigCommand command) throws Exception {
        configuration = command.getPrevConfig();
        visualEditorController.populateVisualEditor(configuration);
    }

    @Override
    public void reexecute(OpenConfigCommand command) throws Exception {
        configuration = command.getNextConfig();
        visualEditorController.populateVisualEditor(configuration);
    }
}