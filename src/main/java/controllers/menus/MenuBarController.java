package controllers.menus;

import controllers.CommandDelegator;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuBarController implements Initializable, InvalidationListener{

    private CommandDelegator commandDelegator;

    @FXML
    Stage stage;

    @FXML
    Menu fileMenu;

    @FXML
    Menu editMenu;

    @FXML
    Menu helpMenu;

    @FXML
    Button undoBtn;

    @FXML
    Button redoBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FileMenuController fileMenuController = new FileMenuController(stage, fileMenu);
        EditMenuController editMenuController = new EditMenuController(editMenu);
        HelpMenuController helpMenuController = new HelpMenuController(helpMenu);

        commandDelegator = CommandDelegator.getINSTANCE();

        //set disabled on initialisation. there should be nothing to undo
        undoBtn.setDisable(true);
        redoBtn.setDisable(true);

        undoBtn.setOnAction(event -> {
            try {
                boolean result = commandDelegator.undo();
                //TODO handle if no executor is subscribed
            } catch (Exception e) {
                e.printStackTrace();
                //TODO handle exception better
            }
        });

        redoBtn.setOnAction(event -> {
            try {
                boolean result = commandDelegator.redo();
                //TODO handle if no executor is subscribed
            } catch (Exception e) {
                e.printStackTrace();
                //TODO handle exception better
            }
        });

        commandDelegator.addListener(this);
    }

    private void updateUndoUI() {
        if (commandDelegator.canUndo()) {
            undoBtn.setDisable(false);
            Tooltip tooltip = undoBtn.getTooltip();
            if (null != tooltip) {
                tooltip.setText("Undo " + commandDelegator.getUndoName());
            }
        } else {
            undoBtn.setDisable(true);
        }
    }

    private void updateRedoUI() {
        if (commandDelegator.canRedo()) {
            redoBtn.setDisable(false);
            Tooltip tooltip = redoBtn.getTooltip();
            if (null != tooltip) {
                tooltip.setText("Redo " + commandDelegator.getRedoName());
            }
        } else {
            redoBtn.setDisable(true);
        }
    }

    @Override
    public void invalidated(Observable observable) {
        if (observable instanceof CommandDelegator) {
            updateUndoUI();
            updateRedoUI();
        }
    }
}
