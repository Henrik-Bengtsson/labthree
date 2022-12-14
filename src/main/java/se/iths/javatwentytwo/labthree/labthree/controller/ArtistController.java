package se.iths.javatwentytwo.labthree.labthree.controller;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import se.iths.javatwentytwo.labthree.labthree.model.ArtistModel;
import se.iths.javatwentytwo.labthree.labthree.model.shapes.Shape;
import se.iths.javatwentytwo.labthree.labthree.model.shapes.ShapeType;

import java.io.File;

public class ArtistController {

    public ArtistModel artistModel = new ArtistModel();

    public GraphicsContext context;
    public Stage stage;

    public Button saveButton;
    public Button redoButton;
    public Button undoButton;

    public ToggleButton rectangleButton;
    public ToggleButton circleButton;
    public ToggleButton triangleButton;
    public ToggleButton selectButton;
    public ToggleGroup buttonToggleGroup;

    public ColorPicker colorPicker;
    public Spinner<Integer> sizeSpinner;

    public Canvas canvas;

    public ListView<String> messageChatList;
    public TextField textMessageField;
    public Button sendButtonTextField;
    public Button connectServer;
    public Button disconnectServer;
    public Label messageConnected;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void initialize() {
        colorPicker.valueProperty().bindBidirectional(artistModel.colorPickerProperty());
        sizeSpinner.getValueFactory().valueProperty().bindBidirectional(artistModel.sizeSpinnerProperty());
        textMessageField.textProperty().bindBidirectional(artistModel.getServerHandling().textMessageProperty());
        messageChatList.setItems(artistModel.getServerHandling().getObservableChatList());
        artistModel.getShapeListProperty().addListener(this::drawShape);
        setDisableProperty();
        setToggleButtonToShapeType();
    }

    private void setDisableProperty() {
        saveButton.disableProperty().bind(Bindings.isEmpty(artistModel.getShapeListProperty()));
        undoButton.disableProperty().bind(Bindings.isEmpty(artistModel.getUndoListProperty()));
        redoButton.disableProperty().bind(Bindings.isEmpty(artistModel.getRedoListProperty()));
        sendButtonTextField.disableProperty().bind(artistModel.getServerHandling().textMessageProperty().isEmpty());
        triangleButton.disableProperty().bind(Bindings.and(artistModel.getServerHandling().connectedProperty(), artistModel.getServerHandling().connectedProperty()));
    }

    private void setToggleButtonToShapeType() {
        rectangleButton.setUserData(ShapeType.RECT);
        circleButton.setUserData(ShapeType.CIRCLE);
        triangleButton.setUserData(ShapeType.TRIANGLE);
    }

    public void canvasClicked(MouseEvent mouseEvent) {
        artistModel.setPoint(mouseEvent.getX(), mouseEvent.getY());
        buttonSelected();
        if (artistModel.getServerHandling().connectedProperty().get() && !artistModel.getShapeListProperty().isEmpty())
            artistModel.getServerHandling().sendShape(artistModel.getShapeListProperty().get(artistModel.getShapeListProperty().size() - 1));
    }

    private void drawShape(Observable observable) {
        context = canvas.getGraphicsContext2D();
        context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        for (Shape shape : artistModel.getShapeListProperty()) {
            shape.draw(context);
        }
    }

    public void buttonSelected() {
        try {
            if (!selectButton.isSelected())
                artistModel.createShapeToList((ShapeType) buttonToggleGroup.getSelectedToggle().getUserData());
            else
                artistModel.changeShape(colorPicker.getValue(), sizeSpinner.getValue());
        } catch (NullPointerException ignored) {
        }
    }

    public void undo() {
        artistModel.undoLastCommand();
    }

    public void redo() {
        artistModel.redoLastCommand();
    }

    public void sendMessageClicked() {
        artistModel.getServerHandling().sendMessage();
    }

    public void connectToServer() {
        artistModel.getServerHandling().connectedProperty().setValue(true);
        artistModel.getServerHandling().connectServer(artistModel);
        messageConnected.textProperty().setValue(connectServer.getText());
    }

    public void disconnectToServer() {
        artistModel.getServerHandling().connectedProperty().setValue(false);
        messageConnected.textProperty().setValue(disconnectServer.getText());
        artistModel.getServerHandling().disconnectServer();
    }

    public void startNewPaint() {
        artistModel.clearLists();
    }

    public void closeProgram() {
        System.exit(0);
    }

    private static FileChooser getFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SVG", "*.svg"));
        return fileChooser;
    }

    public void saveFile() {
        FileChooser fileChooser = getFileChooser();
        fileChooser.setTitle("Save file");
        File savePath = fileChooser.showSaveDialog(stage);
        if (savePath != null)
            artistModel.saveToFile(savePath.toPath());
    }
}
