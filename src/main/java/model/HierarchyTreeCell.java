package model;

import controllers.CommandDelegator;
import controllers.editor.ParentController;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.geometry.Point2D;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import model.commands.concrete.DnDCommand;
import model.configuration.Configuration;
import model.configuration.Directory;
import model.configuration.InvalidConfigurationException;
import model.configuration.XMLConfiguration;

import javax.xml.transform.TransformerException;


/**
 * TreeCell implementation that has predefined drag and drop functionality support <br>
 * <br>
 * Adapted from example at https://gist.github.com/andytill/4009620 <br>
 */
public class HierarchyTreeCell extends TreeCell<Directory> {

    private enum WorkDropType {DROP_INTO, REORDER}

    /**
     * Using a static here, it's just too convenient.
     */
    private static TreeItem<Directory> draggedTreeItem;

    private static WorkDropType workDropType;

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

                if (workDropType == WorkDropType.DROP_INTO) {
                    if (isDraggableToParent() && isNotAlreadyChildOfTarget(HierarchyTreeCell.this.getTreeItem()) && draggedTreeItem.getParent() != getTreeItem()) {
                        Configuration currentConfig = ParentController.getInstance().getConfiguration();

                        //TODO update for general config
                        if (currentConfig instanceof XMLConfiguration) {

                            XMLConfiguration config = (XMLConfiguration) currentConfig;

                            try {
                                XMLConfiguration prevConfig = XMLConfiguration.copy(config);
                                XMLConfiguration newConfig = XMLConfiguration.copy(config);

                                TreeItem<Directory> draggedItemParent = draggedTreeItem.getParent();
                                Directory draggedWork = draggedTreeItem.getValue();

                                draggedItemParent.getValue().getChildren().remove(draggedWork);
                                getTreeItem().getValue().getChildren().add(draggedWork);

                                newConfig.setDirectories(getTreeView().getRoot().getValue().getChildren());

                                CommandDelegator.getINSTANCE().publish(new DnDCommand(newConfig, prevConfig, draggedWork.getFullPrefix() + " " + draggedWork.getName()));

                            } catch (InvalidConfigurationException e) { //TODO improve
                                e.printStackTrace();
                            } catch (TransformerException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
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

    /**
     * Event dispatcher that disables double click expansion behaviour by consuming the event
     *
     * @see javafx.event.EventDispatcher
     */
    private class DisableExpansionEventDispatcher implements EventDispatcher {
        private final EventDispatcher originalDispatcher;

        /**
         * @param originalDispatcher Used to pass on any event that isn't a double click
         * @see DisableExpansionEventDispatcher
         */
        public DisableExpansionEventDispatcher(EventDispatcher originalDispatcher) {
            this.originalDispatcher = originalDispatcher;
        }

        /**
         * If the event is a double click, it will be consumed,
         * otherwise passes on event to original dispatcher
         *
         * @param event the event do dispatch
         * @param tail  the rest of the chain to dispatch event to
         * @return the return event or null if the event has been handled / consumed
         */
        @Override
        public Event dispatchEvent(Event event, EventDispatchChain tail) {
            if (event instanceof MouseEvent) {
                if (((MouseEvent) event).getButton() == MouseButton.PRIMARY
                        && ((MouseEvent) event).getClickCount() >= 2) {
                    event.consume();
                }
            }
            return originalDispatcher.dispatchEvent(event, tail);
        }
    }
}
