package controllers.menus;

import controllers.editor.ConfigurationEditorController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Menu;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuBarController implements Initializable{

    @FXML
    Stage stage;

    @FXML
    Menu fileMenu;

    @FXML
    Menu editMenu;

    @FXML
    Menu helpMenu;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FileMenuController fileMenuController = new FileMenuController(stage, fileMenu);
        EditMenuController editMenuController = new EditMenuController(editMenu);
        HelpMenuController helpMenuController = new HelpMenuController(helpMenu);
    }
}
