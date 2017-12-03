package com.andreas.client.view;

import com.andreas.client.controller.ClientController;
import com.andreas.common.dto.FileMetaDTO;
import com.andreas.common.Constants;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.channels.CompletionHandler;

public class MainView {
    @FXML
    TableColumn<FileMetaDTO, String>
            filenameColumn,
            ownerColumn,
            visibilityColumn,
            accessRightsColumn,
            sizeColumn;

    @FXML
    TableView<FileMetaDTO> fileTable;

    @FXML
    public void initialize() {
        filenameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFilename()));
        ownerColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getOwner().toString()));
        visibilityColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFileAccess()));
        accessRightsColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAccessRight()));
        sizeColumn.setCellValueFactory(data -> new SimpleStringProperty("" + data.getValue().getSize()));
        fileTable.setItems(ClientController.getInstance().getAllFiles());
    }

    @FXML
    public void upload() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(ViewPath.UPLOAD.toString()));
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle(Constants.WINDOW_TITLE);
        stage.show();
    }

    @FXML
    public void download() {
        FileMetaDTO fileMeta = fileTable.getSelectionModel().getSelectedItem();
        if (fileMeta == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No selected file");
            alert.setHeaderText("No selected file");
            alert.setContentText("You must select a file to download");
            alert.showAndWait();
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose File");
        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null){
            ClientController.getInstance().download(file, fileMeta, new CompletionHandler<Void, String>() {
                @Override
                public void completed(Void result, String attachment) {
                    Platform.runLater(()->{
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("File download");
                        alert.setHeaderText("File download");
                        alert.setContentText("File downloaded successfully");
                        alert.showAndWait();
                    });


                }

                @Override
                public void failed(Throwable exc, String attachment) {
                    Platform.runLater(()->{
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("File download");
                        alert.setHeaderText("Failed to download file");
                        alert.setContentText(attachment);
                        alert.showAndWait();
                    });
                }
            });
        }

    }

    @FXML
    public void refresh(){
        ClientController.getInstance().refreshFiles();
    }

    @FXML
    public void subscribe() {
        FileMetaDTO fileMeta = fileTable.getSelectionModel().getSelectedItem();
        if (fileMeta == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No selected file");
            alert.setHeaderText("No selected file");
            alert.setContentText("You must select a file to subscribe");
            alert.showAndWait();
            return;
        }
        ClientController.getInstance().subscribe(fileMeta);
    }

    @FXML
    public void delete(){
        FileMetaDTO fileMeta = fileTable.getSelectionModel().getSelectedItem();
        if (fileMeta == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No selected file");
            alert.setHeaderText("No selected file");
            alert.setContentText("You must select a file to delete");
            alert.showAndWait();
            return;
        }
        ClientController.getInstance().deleteFile(fileMeta, new CompletionHandler<Void, String>() {
            @Override
            public void completed(Void result, String attachment) {
                Platform.runLater(()->{
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Deleted");
                    alert.setHeaderText("Deleted");
                    alert.setContentText("File was successfully deleted.");
                    alert.showAndWait();
                });
            }

            @Override
            public void failed(Throwable exc, String attachment) {
                Platform.runLater(()->{
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Error");
                    alert.setContentText(attachment);
                    alert.showAndWait();
                });
            }
        });
    }

    @FXML
    public void unregister() {
        ClientController.getInstance().unregister(new CompletionHandler<Void, Void>() {
            @Override
            public void completed(Void result, Void attachment) {
                Platform.runLater(() -> showLoginView());
            }

            @Override
            public void failed(Throwable exc, Void attachment) {

            }
        });

    }

    @FXML
    public void logout() {
        ClientController.getInstance().logout(new CompletionHandler<Void, Void>() {
            @Override
            public void completed(Void result, Void attachment) {
                Platform.runLater(() -> showLoginView());
            }

            @Override
            public void failed(Throwable exc, Void attachment) {
                System.err.println("Failed to log out");
                exc.printStackTrace();
                Platform.runLater(()->showLoginView());
            }
        });

    }

    private void showLoginView() {
        fileTable.getScene().getWindow().hide();
        try {
            Parent root = FXMLLoader.load(getClass().getResource(ViewPath.LOGIN.toString()));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle(Constants.WINDOW_TITLE);
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to show login view");
        }
    }

}
