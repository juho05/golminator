package de.julianhofmann.ui;

import de.julianhofmann.App;
import de.julianhofmann.Loop;
import de.julianhofmann.world.PatternCategory;
import de.julianhofmann.world.World;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class PrimaryController implements Initializable {

    public static final String iconsDir = getDataDir() + "/icons/";

    @FXML private CheckMenuItem themeMenuItem;
    @FXML private Button playButton, pauseButton, stopButton;
    @FXML private HBox toolBar;
    @FXML private HBox leftPaneToolBar;
    @FXML private Pane canvasPane;
    @FXML private Canvas canvas;
    @FXML private MenuItem saveMenuItem, saveSelectionMenuItem;
    @FXML private Menu rotateMenu, flipMenu;
    @FXML private MenuItem rotateLeftMenuItem, rotateRightMenuItem, flipHorizontalMenuItem, flipVerticalMenuItem;
    @FXML private MenuItem undoMenuItem, redoMenuItem, copyMenuItem, cutMenuItem, pasteMenuItem, deleteMenuItem;
    @FXML private MenuItem playMenuItem, pauseMenuItem, stopMenuItem, fasterMenuItem, resetSpeedMenuItem;
    @FXML private MenuItem zoomInMenuItem, zoomOutMenuItem, resetZoomMenuItem, resetCameraMenuItem;
    @FXML private Text fileNameLabel;
    @FXML private BorderPane leftPane;
    @FXML private Text delayIndicator, zoomIndicator;
    @FXML private Button changeCategoryButton, renameTreeItemButton, deleteTreeItemButton;

    private ImageView playIcon, playIconDisabled, pauseIcon, pauseIconDisabled, stopIcon, stopIconDisabled;
    private ImageView changeCategoryIcon, changeCategoryIconDisabled, renameIcon, renameIconDisabled, deleteIcon, deleteIconDisabled;

    /* ******************** Initialization *************************** */

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //noinspection ResultOfMethodCallIgnored
        new File(iconsDir).mkdirs();

        App.ui.setContentPane(new ContentPane(canvasPane, canvas));
        App.ui.setPatternList(new PatternList(leftPane));

        initToolBar();
        initFileMenu();
        initEditMenu();
        initSimulationMenu();
        initViewMenu();
        initStatusBar();

        updateColors(App.settings.isDarkTheme());
        App.ui.setPrimaryController(this);
    }

    private void initToolBar() {
        App.world.filePathProperty().addListener((p, o, n) -> updateFileNameLabel());
        App.world.savedProperty().addListener((p, o, n) -> updateFileNameLabel());

        playIcon = new ImageView(new Image(new File(iconsDir + "play_btn.png").toURI().toString()));
        playIconDisabled = new ImageView(new Image(new File(iconsDir + "play_btn_disabled.png").toURI().toString()));
        playIcon.setFitHeight(15);
        playIcon.setPreserveRatio(true);
        playIconDisabled.setFitHeight(15);
        playIconDisabled.setPreserveRatio(true);

        pauseIcon = new ImageView(new Image(new File(iconsDir + "pause_btn.png").toURI().toString()));
        pauseIconDisabled = new ImageView(new Image(new File(iconsDir + "pause_btn_disabled.png").toURI().toString()));
        pauseIcon.setFitHeight(15);
        pauseIcon.setPreserveRatio(true);
        pauseIconDisabled.setFitHeight(15);
        pauseIconDisabled.setPreserveRatio(true);

        stopIcon = new ImageView(new Image(new File(iconsDir + "stop_btn.png").toURI().toString()));
        stopIconDisabled = new ImageView(new Image(new File(iconsDir + "stop_btn_disabled.png").toURI().toString()));
        stopIcon.setFitHeight(15);
        stopIcon.setPreserveRatio(true);
        stopIconDisabled.setFitHeight(15);
        stopIconDisabled.setPreserveRatio(true);

        changeCategoryIcon = new ImageView(new Image(new File(iconsDir + "change_category_btn.png").toURI().toString()));
        changeCategoryIconDisabled = new ImageView(new Image(new File(iconsDir + "change_category_btn_disabled.png").toURI().toString()));
        changeCategoryIcon.setFitHeight(18);
        changeCategoryIcon.setPreserveRatio(true);
        changeCategoryIconDisabled.setFitHeight(18);
        changeCategoryIconDisabled.setPreserveRatio(true);

        renameIcon = new ImageView(new Image(new File(iconsDir + "rename_btn.png").toURI().toString()));
        renameIconDisabled = new ImageView(new Image(new File(iconsDir + "rename_btn_disabled.png").toURI().toString()));
        renameIcon.setFitHeight(18);
        renameIcon.setPreserveRatio(true);
        renameIconDisabled.setFitHeight(18);
        renameIconDisabled.setPreserveRatio(true);

        deleteIcon = new ImageView(new Image(new File(iconsDir + "delete_btn.png").toURI().toString()));
        deleteIconDisabled = new ImageView(new Image(new File(iconsDir + "delete_btn_disabled.png").toURI().toString()));
        deleteIcon.setFitHeight(18);
        deleteIcon.setPreserveRatio(true);
        deleteIconDisabled.setFitHeight(18);
        deleteIconDisabled.setPreserveRatio(true);

        App.world.stateProperty().addListener((p, o, n) -> updateToolBarButtons());

        App.settings.darkThemeProperty().addListener((p, o, n) -> updateColors(n));

        updateFileNameLabel();
        updateToolBarButtons();
    }

    private void initFileMenu() {
        App.world.savedProperty().addListener((p, o, n) -> updateFileMenuItems());
        App.ui.getContentPane().getSelectionManager().selectingProperty().addListener((p, o, n) -> updateFileMenuItems());
        updateFileMenuItems();
    }

    private void initEditMenu() {
        App.world.getUndoManager().undoAvailableProperty().addListener((p, o, n) -> updateEditMenuItems());
        App.world.getUndoManager().redoAvailableProperty().addListener((p, o, n) -> updateEditMenuItems());
        App.ui.getContentPane().getSelectionManager().selectingProperty().addListener((p, o, n) -> updateEditMenuItems());
        App.ui.getContentPane().getSelectionManager().clipboardEmptyProperty().addListener((p, o, n) -> updateEditMenuItems());
        App.world.stateProperty().addListener((p, o, n) -> updateEditMenuItems());
        updateEditMenuItems();
    }

    private void initSimulationMenu() {
        App.world.stateProperty().addListener((p, o, n) -> updateSimulationMenuItems());
        App.loop.updateDelayProperty().addListener((p, o, n) -> updateSimulationMenuItems());
        App.loop.actualUpdateDelayProperty().addListener((p, o, n) -> updateSimulationMenuItems());
        updateSimulationMenuItems();
    }

    private void initViewMenu() {
        themeMenuItem.selectedProperty().bindBidirectional(App.settings.darkThemeProperty());
        App.world.cameraXProperty().addListener((p, o, n) -> updateViewMenuItems());
        App.world.cameraYProperty().addListener((p, o, n) -> updateViewMenuItems());
        App.world.cellSizeProperty().addListener((p, o, n) -> updateViewMenuItems());
        updateViewMenuItems();
    }

    private void initStatusBar() {
        App.loop.updateDelayProperty().addListener((p, o, n) -> updateStatusBar());
        App.world.cellSizeProperty().addListener((p, o, n) -> updateStatusBar());
        App.world.cameraXProperty().addListener((p, o, n) -> updateStatusBar());
        App.world.cameraYProperty().addListener((p, o, n) -> updateStatusBar());
        updateStatusBar();
    }

    /* ************************ ToolBar Action Methods ************************** */

    @FXML
    private void play() {
        App.world.setState(World.RUNNING);
    }

    @FXML
    private void pause() {
        App.world.setState(World.PAUSED);
    }

    @FXML
    private void stop() {
        App.world.setState(World.STOPPED);
    }

    @FXML
    private void changeCategory() {
        App.ui.getPatternList().changeCategory();
    }

    @FXML
    private void renameTreeItem() {
        App.ui.getPatternList().renameTreeItem();
    }

    @FXML
    private void deleteTreeItem() {
        App.ui.getPatternList().deleteTreeItem();
    }

    /* ************************ Menu Action Methods ************************** */

    /* -------------------- File Menu -------------------------- */

    @FXML
    private void newFile() {
        App.world.newFile();
    }

    @FXML
    private void openFile() {
        App.ui.open();
    }

    @FXML
    private void saveFile() {
        App.ui.save();
    }

    @FXML
    private void saveFileAs() {
        App.ui.saveAs();
    }

    @FXML
    private void saveSelection() {
        if (App.ui.getContentPane().getSelectionManager().isSelecting()) {
            String name = App.ui.inputDialog("Auswahl speichern", "Name:", "");
            if (name.isBlank()) {
                return;
            }
            if (!App.ui.getPatternList().getPatternManager().nameExists(name)) {
                String category = App.ui.inputDialog("Auswahl speichern", "Kategorie:", "");
                if (!category.isBlank()) {
                    if (!App.ui.getPatternList().getPatternManager().savePattern(name, category, App.ui.getContentPane().getSelectionManager().toJson(name, category))) {
                        App.ui.alert(Alert.AlertType.ERROR, "Fehler", "Die Struktur konnte nicht gespeichert werden!");
                    }
                }
            } else {
                App.ui.alert(Alert.AlertType.ERROR, "Fehler", "Der Name existiert bereits!");
            }
        }
    }

    @FXML
    private void exit() {
        App.exit();
    }

    /* -------------------- Edit Menu -------------------------- */

    @FXML
    private void undo() {
        App.world.getUndoManager().undo();
    }

    @FXML
    private void redo() {
        App.world.getUndoManager().redo();
    }

    @FXML
    private void copy() {
        App.ui.getContentPane().getSelectionManager().copy();
    }

    @FXML
    private void cut() {
        App.ui.getContentPane().getSelectionManager().cut();
    }

    @FXML
    private void paste() {
        App.ui.getContentPane().getSelectionManager().paste();
    }

    @FXML
    private void rotateLeft() {
        App.ui.getContentPane().getSelectionManager().rotateLeft();
    }

    @FXML
    private void rotateRight() {
        App.ui.getContentPane().getSelectionManager().rotateRight();
    }

    @FXML
    private void flipHorizontal() {
        App.ui.getContentPane().getSelectionManager().flipHorizontal();
    }

    @FXML
    private void flipVertical() {
        App.ui.getContentPane().getSelectionManager().flipVertical();
    }

    @FXML
    private void delete() {
        App.ui.getContentPane().getSelectionManager().delete();
    }

    /* -------------------- Simulation Menu -------------------------- */

    @FXML
    private void speedUpSimulation() {
        App.loop.addUpdateDelay(-10);
    }

    @FXML
    private void slowDownSimulation() {
        App.loop.addUpdateDelay(10);
    }

    @FXML
    private void changeSimulationDelay() {
        String input = App.ui.inputDialog("Verzögerung ändern", "Verzögerung:", Integer.toString(App.loop.getUpdateDelay()));
        if (!input.isBlank()) {
            try {
                int delay = Integer.parseInt(input);
                if (delay >= 0) {
                    App.loop.setUpdateDelay(delay);
                    return;
                }
            } catch (NumberFormatException ignore) { }
        }
        App.ui.alert(Alert.AlertType.ERROR, "Fehler", "Ungültige Verzögerung", ButtonType.OK);
    }

    @FXML
    private void resetSimulationDelay() {
        App.loop.setUpdateDelay(Loop.DEFAULT_UPDATE_DELAY);
    }

    /* -------------------- View Menu -------------------------- */

    @FXML
    private void zoomIn() {
        App.world.addCellSize(App.world.getCellSize() / 10);
    }

    @FXML
    private void zoomOut() {
        App.world.addCellSize(-(App.world.getCellSize() / 10));
    }

    @FXML
    private void resetZoom() {
        App.world.setCellSize(World.DEFAULT_CELL_SIZE);
    }

    @FXML
    private void resetCamera() {
        App.world.setCellSize(World.DEFAULT_CELL_SIZE);
        App.world.setCameraX(World.DEFAULT_CAMERA_X);
        App.world.setCameraY(World.DEFAULT_CAMERA_Y);
    }

    /* ************************ Update Methods ************************** */

    private void updateColors(boolean darkTheme) {
        if (darkTheme) {
            toolBar.setStyle("-fx-background-color: #353535");
            leftPaneToolBar.setStyle("-fx-background-color: #404040");
            playButton.setStyle("-fx-border-width: 0; -fx-border-color: none; -fx-background-color: #353535");
            pauseButton.setStyle("-fx-border-width: 0; -fx-border-color: none; -fx-background-color: #353535");
            stopButton.setStyle("-fx-border-width: 0; -fx-border-color: none; -fx-background-color: #353535");
            changeCategoryButton.setStyle("-fx-border-width: 0; -fx-border-color: none; -fx-background-color: #404040");
            renameTreeItemButton.setStyle("-fx-border-width: 0; -fx-border-color: none; -fx-background-color: #404040");
            deleteTreeItemButton.setStyle("-fx-border-width: 0; -fx-border-color: none; -fx-background-color: #404040");
        } else {
            toolBar.setStyle("-fx-border-width: 0; -fx-border-color: none; -fx-background-color: #E0E0E0");
            leftPaneToolBar.setStyle("-fx-border-width: 0; -fx-border-color: none; -fx-background-color: #EAEAEA");
            playButton.setStyle("-fx-border-width: 0; -fx-border-color: none; -fx-background-color: #E0E0E0");
            pauseButton.setStyle("-fx-border-width: 0; -fx-border-color: none; -fx-background-color: #E0E0E0");
            stopButton.setStyle("-fx-border-width: 0; -fx-border-color: none; -fx-background-color: #E0E0E0");
            changeCategoryButton.setStyle("-fx-border-width: 0; -fx-border-color: none; -fx-background-color: #EAEAEA");
            renameTreeItemButton.setStyle("-fx-border-width: 0; -fx-border-color: none; -fx-background-color: #EAEAEA");
            deleteTreeItemButton.setStyle("-fx-border-width: 0; -fx-border-color: none; -fx-background-color: #EAEAEA");
        }
    }

    public void updateToolBarButtons() {
        playButton.setDisable(App.world.getState() == World.RUNNING);
        playButton.setGraphic(playButton.isDisable() ? playIconDisabled : playIcon);

        pauseButton.setDisable(App.world.getState() != World.RUNNING);
        pauseButton.setGraphic(pauseButton.isDisable() ? pauseIconDisabled : pauseIcon);

        stopButton.setDisable(App.world.getState() == World.STOPPED);
        stopButton.setGraphic(stopButton.isDisable() ? stopIconDisabled : stopIcon);

        changeCategoryButton.setDisable(App.world.getState() == World.RUNNING || App.ui.getPatternList().getTreeView().getSelectionModel().getSelectedItem() == null || App.ui.getPatternList().getTreeView().getSelectionModel().getSelectedItem().getValue() instanceof PatternCategory);
        changeCategoryButton.setGraphic(changeCategoryButton.isDisable() ? changeCategoryIconDisabled : changeCategoryIcon);

        renameTreeItemButton.setDisable(App.world.getState() == World.RUNNING || App.ui.getPatternList().getTreeView().getSelectionModel().getSelectedItem() == null);
        renameTreeItemButton.setGraphic(renameTreeItemButton.isDisable() ? renameIconDisabled : renameIcon);

        deleteTreeItemButton.setDisable(App.world.getState() == World.RUNNING || App.ui.getPatternList().getTreeView().getSelectionModel().getSelectedItem() == null);
        deleteTreeItemButton.setGraphic(deleteTreeItemButton.isDisable() ? deleteIconDisabled : deleteIcon);
    }

    private void updateFileNameLabel() {
        if (App.world.getFilePath() != null) {
            String[] temp = App.world.getFilePath().split(System.getProperty("file.separator"));
            fileNameLabel.setText(">> " + temp[temp.length-1] + (App.world.isSaved() ? "" : "*"));
        } else {
            fileNameLabel.setText(">> Unbenannt.gol"+(App.world.isSaved() ? "" : "*"));
        }
    }

    private void updateFileMenuItems() {
        saveMenuItem.setDisable(App.world.isSaved());
        saveSelectionMenuItem.setDisable(!App.ui.getContentPane().getSelectionManager().isSelecting());
    }

    private void updateEditMenuItems() {
        undoMenuItem.setDisable(App.world.getState() != World.STOPPED || !App.world.getUndoManager().isUndoAvailable());
        redoMenuItem.setDisable(App.world.getState() != World.STOPPED || !App.world.getUndoManager().isRedoAvailable());
        copyMenuItem.setDisable(App.world.getState() == World.RUNNING || !App.ui.getContentPane().getSelectionManager().isSelecting());
        cutMenuItem.setDisable(App.world.getState() == World.RUNNING || !App.ui.getContentPane().getSelectionManager().isSelecting());
        pasteMenuItem.setDisable(App.world.getState() == World.RUNNING || App.ui.getContentPane().getSelectionManager().isClipboardEmpty());
        rotateMenu.setDisable(App.world.getState() == World.RUNNING || !App.ui.getContentPane().getSelectionManager().isSelecting());
        rotateLeftMenuItem.setDisable(App.world.getState() == World.RUNNING || !App.ui.getContentPane().getSelectionManager().isSelecting());
        rotateRightMenuItem.setDisable(App.world.getState() == World.RUNNING || !App.ui.getContentPane().getSelectionManager().isSelecting());
        flipMenu.setDisable(App.world.getState() == World.RUNNING || !App.ui.getContentPane().getSelectionManager().isSelecting());
        flipHorizontalMenuItem.setDisable(App.world.getState() == World.RUNNING || !App.ui.getContentPane().getSelectionManager().isSelecting());
        flipVerticalMenuItem.setDisable(App.world.getState() == World.RUNNING || !App.ui.getContentPane().getSelectionManager().isSelecting());
        deleteMenuItem.setDisable(App.world.getState() == World.RUNNING || !App.ui.getContentPane().getSelectionManager().isSelecting());
    }

    private void updateSimulationMenuItems() {
        playMenuItem.setDisable(App.world.getState() == World.RUNNING);
        pauseMenuItem.setDisable(App.world.getState() != World.RUNNING);
        stopMenuItem.setDisable(App.world.getState() == World.STOPPED);

        fasterMenuItem.setDisable(App.loop.getUpdateDelay() == 0);
        resetSpeedMenuItem.setDisable(App.loop.getUpdateDelay() == Loop.DEFAULT_UPDATE_DELAY);
    }

    private void updateViewMenuItems() {
        resetCameraMenuItem.setDisable(App.world.getCameraX() == World.DEFAULT_CAMERA_X && App.world.getCameraY() == World.DEFAULT_CAMERA_Y && App.world.getCellSize() == World.DEFAULT_CELL_SIZE);
        resetZoomMenuItem.setDisable(App.world.getCellSize() == World.DEFAULT_CELL_SIZE);
        zoomOutMenuItem.setDisable(App.world.getCellSize() == World.MIN_CELL_SIZE);
        zoomInMenuItem.setDisable(App.world.getCellSize() == World.MAX_CELL_SIZE);
    }

    public void updateStatusBar() {
        delayIndicator.setText("Verzögerung: " + App.loop.getActualUpdateDelay() + "ms/" + App.loop.getUpdateDelay() + "ms");
        zoomIndicator.setText("Zoom: " + Math.round((App.world.getCellSize() * 10)) + "%");
    }

    private static String getDataDir() {
        String rootPath;
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("windows")) {
            rootPath = System.getenv("APPDATA");
        } else {
            rootPath = System.getenv("XDG_DATA_HOME");

            if(rootPath == null) {
                rootPath = System.getProperty("user.home")+"/.local/share";
            }
        }

        return rootPath + "/golminator";
    }
}
