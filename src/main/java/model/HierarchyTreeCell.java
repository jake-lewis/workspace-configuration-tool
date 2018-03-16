package model;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.geometry.Point2D;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import model.configuration.Directory;

import java.util.LinkedList;
import java.util.List;


/**
 * TreeCell implementation that has predefined drag and drop functionality support <br>
 * <br>
 * Adapted from example at https://gist.github.com/andytill/4009620 <br>
 */
public class HierarchyTreeCell extends TreeCell<Directory> implements Observable {

    private enum WorkDropType {DROP_INTO, REORDER}

    /**
     * Using a static here, it's just too convenient.
     */
    private static TreeItem<Directory> draggedTreeItem;

    private static WorkDropType workDropType;

    private List<InvalidationListener> listeners = new LinkedList<>();

    public HierarchyTreeCell() {
        getStyleClass().add("tree-cell");

        setOnDragOver(event -> {
            if (isDraggableToParent() && isNotAlreadyChildOfTarget(HierarchyTreeCell.this.getTreeItem()) && draggedTreeItem.getParent() != getTreeItem()) {
                Point2D sceneCoordinates = HierarchyTreeCell.this.localToScene(0d, 0d);

                double height = HierarchyTreeCell.this.getHeight();

                // get the y coordinate within the control
                double y = event.getSceneY() - (sceneCoordinates.getY());

                // if the drop is three quarters of the way down the control
                // then the drop will be a sibling and not into the tree item

                // set the dnd effect for the required action
                if (y > (height * .75d)) {
                    setEffect(null);

                    getStyleClass().add("dnd-below");

                    workDropType = WorkDropType.REORDER;
                } else {
                    getStyleClass().remove("dnd-below");

                    InnerShadow shadow;

                    shadow = new InnerShadow();
                    shadow.setOffsetX(1.0);
                    shadow.setColor(Color.web("#666666"));
                    shadow.setOffsetY(1.0);
                    setEffect(shadow);

                    workDropType = WorkDropType.DROP_INTO;
                }

                event.acceptTransferModes(TransferMode.MOVE);
            }
        });

        setOnDragDetected(event -> {
            ClipboardContent content;

            content = new ClipboardContent();
            content.putString("Temp String");

            Dragboard dragboard;

            dragboard = getTreeView().startDragAndDrop(TransferMode.MOVE);
            dragboard.setContent(content);

            draggedTreeItem = getTreeItem();

            event.consume();
        });

        setOnDragDropped(event -> {
            boolean dropOK = false;

            if (draggedTreeItem != null) {

                TreeItem<Directory> draggedItemParent = draggedTreeItem.getParent();

                Directory draggedWork = draggedTreeItem.getValue();

                if (workDropType == WorkDropType.DROP_INTO) {
                    if (isDraggableToParent() && isNotAlreadyChildOfTarget(HierarchyTreeCell.this.getTreeItem()) && draggedTreeItem.getParent() != getTreeItem()) {

                        //todo replace with command
                        draggedItemParent.getValue().getChildren().remove(draggedWork);
                        getTreeItem().getValue().getChildren().add(draggedWork);

                        notifyListeners();

                        getTreeItem().setExpanded(true);
                    }
                } else if (workDropType == WorkDropType.REORDER) {
                    //todo
                    System.out.println("REORDER?");
                }

                dropOK = true;

                draggedTreeItem = null;
            }

            event.setDropCompleted(dropOK);
            event.consume();
        });

        setOnDragExited(event -> {
            // remove all dnd effects
            setEffect(null);
            getStyleClass().remove("dnd-below");
        });
    }

    private boolean isDraggableToParent() {
        //todo
        //return draggedTreeItem.getValue().isDraggableTo(getTreeItem().getValue());
        return true;
    }

    private boolean isNotAlreadyChildOfTarget(TreeItem<Directory> treeItemParent) {
        return draggedTreeItem != treeItemParent &&
                (treeItemParent.getParent() == null || isNotAlreadyChildOfTarget(treeItemParent.getParent()));

    }

    protected void updateItem(Directory item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
            setText(item.toString());
            setGraphic(getTreeItem().getGraphic());
        } else {
            setText(null);
            setGraphic(null);
        }
    }

    @Override
    public void addListener(InvalidationListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        this.listeners.remove(listener);
    }

    private void notifyListeners() {
        for (InvalidationListener listener : listeners) {
            listener.invalidated(this);
        }
    }
}
