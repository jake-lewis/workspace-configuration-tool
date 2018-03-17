package controllers.menus;

import controllers.CommandDelegator;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class FooterController implements InvalidationListener {

    @FXML
    private Label statusLabel;

    public FooterController() {
        CommandDelegator.getINSTANCE().addListener(this);
    }

    @Override
    public void invalidated(Observable observable) {
        if (observable instanceof CommandDelegator) {
            statusLabel.setText(CommandDelegator.getINSTANCE().getLastCommandStatus());
        }
    }
}
