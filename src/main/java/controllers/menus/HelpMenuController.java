package controllers.menus;

import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import model.ExceptionAlert;

import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

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
        if (Desktop.isDesktopSupported()) {
            try {
                File guide = new File("./User_Guide.pdf");
                if (!guide.exists()) {
                    InputStream stream = ClassLoader.getSystemResourceAsStream("User_Guide.pdf");
                    Files.copy(stream, guide.toPath());
                }

                Desktop.getDesktop().open(guide);
            } catch (Exception ex) {
                Alert alert = new ExceptionAlert(ex);
                alert.showAndWait();
            }
        }
    }

    private void about() {
        System.out.println("About");
    }
}
