package de.julianhofmann.ui;

import de.julianhofmann.App;
import de.julianhofmann.util.PatternManager;
import de.julianhofmann.world.Pattern;
import de.julianhofmann.world.PatternCategory;
import de.julianhofmann.world.World;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;

import java.util.ArrayList;

public class PatternList {

    private final PatternManager patternManager;
    private final BorderPane leftPane;
    private TreeView<Pattern> treeView;
    private TreeItem<Pattern> lastFocus;

    public PatternList(BorderPane leftPane) {
        this.leftPane = leftPane;
        patternManager = new PatternManager();
        treeView = createTreeView();
        leftPane.setCenter(treeView);
    }

    private TreeView<Pattern> createTreeView() {
        TreeItem<Pattern> root = new TreeItem<>();

        ArrayList<Pattern> patterns = patternManager.getPatterns();

        ArrayList<TreeItem<Pattern>> categories = new ArrayList<>();
        ArrayList<String> categoryNames = new ArrayList<>();

        for (Pattern pattern : patterns) {
            TreeItem<Pattern> patternItem = new TreeItem<>(pattern);
            int index = categoryNames.indexOf(pattern.getCategory());
            if (index == -1) {
                TreeItem<Pattern> categoryItem = new TreeItem<>(new PatternCategory(pattern.getCategory()));
                categoryItem.getChildren().add(patternItem);
                categories.add(categoryItem);
                categoryNames.add(pattern.getCategory());
            } else {
                categories.get(index).getChildren().add(patternItem);
            }
        }

        root.getChildren().addAll(categories);


        TreeView<Pattern> newTreeView = new TreeView<>(root);
        newTreeView.setShowRoot(false);

        newTreeView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                TreeItem<Pattern> item = newTreeView.getSelectionModel().getSelectedItem();
                if (mouseEvent.getClickCount() == 2) {
                    if (item != null && !(item.getValue() instanceof PatternCategory)) {
                        App.ui.getContentPane().getSelectionManager().spawnPattern(item.getValue());
                    }
                }
                if (lastFocus != null && lastFocus == item) {
                    newTreeView.getSelectionModel().clearSelection();
                    lastFocus = null;
                } else {
                    lastFocus = item;
                }
            }
        });

        newTreeView.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> deletePattern());

        App.world.stateProperty().addListener((p, o, n) -> newTreeView.setDisable(n.intValue() == World.RUNNING));
        newTreeView.getSelectionModel().selectedItemProperty().addListener((p, o, n) -> App.ui.getPrimaryController().updateToolBarButtons());

        return newTreeView;
    }

    public void deletePattern() {
        TreeItem<Pattern> selectedItem = treeView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            ButtonType button = App.ui.alert(Alert.AlertType.CONFIRMATION, "Bist du dir sicher?", "Bist du dir sicher, dass du \"" + selectedItem.getValue().toString() + "\" " + (!selectedItem.isLeaf() ? "und alle seine untergeordneten Strukturen " : "") + "löschen möchtest?", ButtonType.YES, ButtonType.NO).orElse(null);
            if (button != null && button == ButtonType.YES) {
                patternManager.deletePattern(selectedItem.getValue());
            }
        }
    }

    public PatternManager getPatternManager() {
        return patternManager;
    }

    public void refresh() {
        lastFocus = null;
        treeView = createTreeView();
        leftPane.setCenter(treeView);
        App.ui.getPrimaryController().updateToolBarButtons();
    }

    public TreeView<Pattern> getTreeView() {
        return treeView;
    }
}
