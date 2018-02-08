package controllers.menus;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

class FileMenuController {
    FileMenuController (Menu menu) {

        //Assign actions to named menu items
        for (MenuItem item: menu.getItems()) {
            switch (item.getId()) {
                case "fileNew":
                    item.setOnAction((e) -> newFile()); break;
                case "fileOpen":
                    item.setOnAction((e) -> open()); break;
                case "fileSave":
                    item.setOnAction((e) -> save()); break;
                case "fileSaveAs":
                    item.setOnAction((e) -> saveAs()); break;
                case "fileClose":
                    item.setOnAction((e) -> close()); break;
                default:
                    System.out.println(item.getId());
            }
        }
    }

    private void newFile() {
        System.out.println("New");
    }

    private void open() {
        System.out.println("Open");
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
