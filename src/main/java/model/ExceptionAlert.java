package model;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionAlert extends Alert {

    public ExceptionAlert(Exception exception) {
        super(AlertType.ERROR);

        this.setHeaderText(exception.getLocalizedMessage());
        this.setContentText("An error occurred while trying to perform an action: " + exception.getLocalizedMessage());

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea();

        StringWriter stackTraceWriter = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTraceWriter));
        textArea.setText(stackTraceWriter.toString());

        textArea.setEditable(false);
        textArea.setWrapText(false);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        this.getDialogPane().setExpandableContent(expContent);
    }

    private ExceptionAlert(AlertType alertType) {
        super(alertType);
    }

    private ExceptionAlert(AlertType alertType, String contentText, ButtonType... buttons) {
        super(alertType, contentText, buttons);
    }
}
