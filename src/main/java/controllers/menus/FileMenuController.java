package controllers.menus;

import controllers.CommandDelegator;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.DirectoryChooser;
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
            if (item.getId() != null) {
                assignAction(item);
            } else if (item instanceof Menu) {
                for (MenuItem subItem : ((Menu) item).getItems()) {
                    assignAction(subItem);
                }
            }
        }
    }

    private void assignAction(MenuItem item) {
        switch (item.getId()) {
            case "fileNewEmpty":
                item.setOnAction((e) -> newFileEmpty());
                break;
            case "fileNewFolders":
                item.setOnAction((e) -> newFileFolder());
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
            case "fileExit":
                item.setOnAction((e) -> Platform.exit());
                break;
            default:
                System.out.println(item.getId());
        }
    }

    private void newFileEmpty() {
        try {
            CommandDelegator.getINSTANCE().publish(new OpenConfigCommand(ConfigurationFactory.create(FileType.XML)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void newFileFolder() {
        try {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Create configuration from folder structure");

            File directory = directoryChooser.showDialog(stage);
            if (directory != null) {
                CommandDelegator.getINSTANCE().publish(new OpenConfigCommand(ConfigurationFactory.create(directory, FileType.XML)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void open() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open existing configuration file");
            fileChooser.setInitialDirectory(new File("C:\\Git Repositories\\project\\src\\main\\resources\\example config"));

            populateExtensions(fileChooser);

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

    private void populateExtensions(FileChooser fileChooser) {
        for (FileType type : FileType.values()) {
            if (type != FileType.UNSUPPORTED) {
                List<String> extensions = new LinkedList<>();
                for (String value : type.getValues()) {
                    extensions.add("*." + value);
                }
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(type.name(), extensions));
            }
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
