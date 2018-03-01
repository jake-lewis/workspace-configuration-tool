package controllers.editor;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import model.configuration.Configuration;

public class ApplyConfigurationController implements EditorController{
    private Configuration configuration;
    private TextField projectNameField;
    private TextField rootField;
    private TextField targetField;

    public ApplyConfigurationController(Tab applyConfigTab) {

        BorderPane applyConfigPane = (BorderPane) applyConfigTab.getContent();
        GridPane configurationProperties = (GridPane) ((BorderPane) applyConfigPane.getLeft()).getTop();

        ObservableList<Node> gridPaneChildren = configurationProperties.getChildren();
        for (Node node : gridPaneChildren) {
            if (node.getId() != null) {
                switch (node.getId()) {
                    case "projectNameField":
                        projectNameField = (TextField) node;
                        break;
                    case "rootField":
                        rootField = (TextField) node;
                        break;
                    case "targetField":
                        targetField = (TextField) node;
                        break;
                }
            }
        }
    }

    @Override
    public void populate(Configuration configuration) {
        this.configuration = configuration;

        if (configuration != null) {
            projectNameField.setText(configuration.getProjectName());
            rootField.setText(configuration.getProjectRootPath());
            targetField.setText(configuration.getProjectTargetPath());
        } else {
            projectNameField.setText(null);
            rootField.setText(null);
            targetField.setText(null);
        }
    }
}
