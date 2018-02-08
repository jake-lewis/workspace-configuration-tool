package controllers.menus;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

class EditMenuController {

    EditMenuController (Menu menu) {

        //Assign actions to named menu items
        for (MenuItem item: menu.getItems()) {
            switch (item.getId()) {
                case "editCut":
                    item.setOnAction((e) -> cut()); break;
                case "editCopy":
                    item.setOnAction((e) -> copy()); break;
                case "editPaste":
                    item.setOnAction((e) -> paste()); break;
                case "editDelete":
                    item.setOnAction((e) -> delete()); break;
                default:
                    System.out.println(item.getId());
            }
        }
    }

    private void cut() {
        System.out.println("Cut");
    }

    private void copy() {
        System.out.println("Copy");
    }

    private void paste() {
        System.out.println("Paste");
    }

    private void delete() {
        System.out.println("Delete");
    }
}