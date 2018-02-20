package controllers.menus;

import controllers.CommandDelegator;
import model.commands.concrete.OpenConfigCommand;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

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
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                CommandDelegator.getINSTANCE().publish(new OpenConfigCommand(file));
            }

        } catch (Exception e) {
            //TODO
            e.printStackTrace();
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
