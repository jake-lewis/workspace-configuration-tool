package controllers.menus;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

class HelpMenuController {
    HelpMenuController (Menu menu) {

        //Assign actions to named menu items
        for (MenuItem item: menu.getItems()) {
            switch (item.getId()) {
                case "helpUserGuide":
                    item.setOnAction((e) -> userGuide()); break;
                case "helpAbout":
                    item.setOnAction((e) -> about()); break;
                default:
                    System.out.println(item.getId());
            }
        }
    }

    private void userGuide() {
        System.out.println("User Guide");
    }

    private void about() {
        System.out.println("About");
    }
}
