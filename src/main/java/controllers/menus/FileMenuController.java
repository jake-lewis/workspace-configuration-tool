package controllers.menus;

import controllers.CommandDelegator;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.ExceptionAlert;
import model.commands.concrete.OpenConfigCommand;
import model.configuration.ConfigurationFactory;
import model.configuration.FileType;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

class FileMenuController {
    private final Stage stage;

    FileMenuController(Stage stage, Menu menu) {
        this.stage = stage;

        //Assign actions to named menu items
        for (MenuItem item : menu.getItems()) {
            switch (item.getId()) {
                case "fileNew":
                    item.setOnAction((e) -> newFile());
                    break;
                case "fileOpen":
                    item.setOnAction((e) -> open());
                    break;
                case "fileSave":
                    item.setOnAction((e) -> save());
                    break;
                case "fileSaveAs":
                    item.setOnAction((e) -> saveAs());
                    break;
                case "fileClose":
                    item.setOnAction((e) -> close());
                    break;
                default:
                    System.out.println(item.getId());
            }
        }
    }

    private void newFile() {
        System.out.println("New");
    }

    private void open() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open existing configuration file");
            fileChooser.setInitialDirectory(new File("C:\\Git Repositories\\project\\src\\main\\resources\\example config"));

            for (FileType type : FileType.values()) {
                if (type != FileType.UNSUPPORTED) {
                    List<String> extensions = new LinkedList<>();
                    for (String value : type.getValues()) {
                        extensions.add("*." + value);
                    }
                    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(type.name(), extensions));
                }
            }

            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                CommandDelegator.getINSTANCE().publish(new OpenConfigCommand(ConfigurationFactory.create(file)));
            }

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new ExceptionAlert(e);
            alert.showAndWait();
        }
    }

    private void save() {
        System.out.println("Save");
    }

    private void saveAs() {
        System.out.println("Save As");
    }

    private void close() {
        System.out.println("Close");
    }
}
