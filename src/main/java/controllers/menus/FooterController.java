package controllers.menus;

import controllers.CommandDelegator;
import controllers.ExecutionRecord;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class FooterController implements PropertyChangeListener {

    @FXML
    private Label statusLabel;

    public FooterController() {
        CommandDelegator.getINSTANCE().addListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() instanceof CommandDelegator) {
            ExecutionRecord record = CommandDelegator.getINSTANCE().getLatestExecutionRecord().get();
            statusLabel.setText(String.format("%s %s", record.getOperation(), record.getCommand().getName()));
        }
    }
}
