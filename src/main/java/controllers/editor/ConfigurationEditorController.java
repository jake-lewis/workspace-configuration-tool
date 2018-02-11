package controllers.editor;

import controllers.CommandDelegator;
import model.executors.Executor;
import model.commands.concrete.OpenConfigCommand;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeView;
import model.Directory;
import parsers.XMLParser;

import java.net.URL;
import java.util.ResourceBundle;

public class ConfigurationEditorController implements Initializable, Executor<OpenConfigCommand> {

    private VisualEditorController visualEditorController;
    private TextEditorController textEditorController;

    @FXML
    TreeView<Directory> visualEditor;

    @FXML
    TextArea textEditor;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CommandDelegator.getINSTANCE().subscribe(this, OpenConfigCommand.class);
        visualEditorController = new VisualEditorController(visualEditor);
        textEditorController = new TextEditorController(textEditor);
    }

    @Override
    public void execute(OpenConfigCommand command) throws Exception {
        visualEditorController.populateVisualEditor(XMLParser.parse(command.getFile()));
    }
}