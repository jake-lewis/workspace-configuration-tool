package controllers.dialogs;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.configuration.Directory;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileSelectorController implements Initializable {
    @FXML
    TextField pathField;

    @FXML
    Button browseBtn;

    @FXML
    CheckBox prefixCheck;

    @FXML
    ChoiceBox<String> prefixGenChoice;

    @FXML
    Label prefixGenLbl;

    @FXML
    Label prefixFieldLbl;

    @FXML
    TextField prefixField;

    @FXML
    Button confirmBtn;

    @FXML
    Button cancelBtn;

//    @FXML
//    TreeView<Directory> locationTreeView;

    private Stage stage;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        browseBtn.setOnAction(event -> onBrowse());
        prefixGenChoice.getItems().add("Manual");
        //prefixGenChoice.getItems().add("From Location");
        prefixGenChoice.getSelectionModel().selectFirst();
        prefixCheck.selectedProperty().addListener(observable -> {
            prefixGenChoice.setDisable(prefixCheck.isSelected());
            prefixGenLbl.setDisable(prefixCheck.isSelected());
            if (!prefixCheck.isSelected()) {
                prefixGenChoice.getSelectionModel().selectFirst();
                prefixFieldLbl.setDisable(false);
                prefixField.setDisable(false);
//                locationTreeView.setDisable(true);
            } else {
                prefixFieldLbl.setDisable(true);
                prefixField.setDisable(true);
                //locationTreeView.setDisable(true);
            }
        });

        prefixGenChoice.getSelectionModel().selectedItemProperty().addListener(observable -> {
            if (!prefixCheck.isSelected()) {
                boolean manual = prefixGenChoice.getSelectionModel().getSelectedItem()
                        .equalsIgnoreCase("manual");

                prefixFieldLbl.setDisable(!manual);
                prefixField.setDisable(!manual);
                //locationTreeView.setDisable(manual);
            }
        });

        confirmBtn.setOnAction(event -> onConfirm());
    }

    public void setStageAndDirectories(Stage stage, TreeItem<Directory> rootDirectory) {
        this.stage = stage;
        //this.locationTreeView.setRoot(rootDirectory);
        //this.locationTreeView.setShowRoot(false);
    }

    public String getPath() {
        return pathField.getText();
    }

    public boolean includesPrefix() {
        return prefixCheck.isSelected();
    }

    public String getPrefix() {
        if (prefixCheck.isSelected()) {
            Pattern pattern = Pattern.compile("(.*? )(.*)");
            String fileName = pathField.getText().substring(pathField.getText().lastIndexOf("\\") +1);
            Matcher matcher = pattern.matcher(fileName);
            matcher.find();
            return matcher.group(1);
        }

        return prefixField.getText();
    }

    private void onBrowse() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Add file to workspace");
        File init = new File("./");
        if (init.isDirectory()) {
            fileChooser.setInitialDirectory(init);
        }

        File file = fileChooser.showOpenDialog(this.stage);
        if (file != null) {
            pathField.setText(file.getPath());
            prefixCheck.setSelected(fileHasPrefix(file));
        }
    }

    private boolean fileHasPrefix(File file) {
        Pattern pattern = Pattern.compile("(.*? )(.*)(\\.[^ ]+)");
        Matcher matcher = pattern.matcher(file.getPath().substring(file.getPath().lastIndexOf("\\") +1));
        return matcher.find();
    }

    private void onConfirm() {
        stage.close();
    }
}
