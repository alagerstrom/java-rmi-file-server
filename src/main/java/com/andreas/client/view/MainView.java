package com.andreas.client.view;

import com.andreas.client.controller.ClientController;
import com.andreas.common.FileMetaDTO;
import com.andreas.server.model.FileMetaData;
import com.andreas.common.Constants;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

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
    TableView fileTable;

    @FXML
    public void initialize() {
        filenameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFilename()));
        ownerColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getOwner().toString()));
        visibilityColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFileAccess()));
        accessRightsColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAccessRight()));
        sizeColumn.setCellValueFactory(data -> new SimpleStringProperty("" + data.getValue().getSize()));
        ProgressIndicator progressIndicator = new ProgressIndicator();
        fileTable.setPlaceholder(progressIndicator);
        progressIndicator.setMaxSize(Constants.PROGRESS_INDICATOR_SIZE, Constants.PROGRESS_INDICATOR_SIZE);
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

    }

    @FXML
    public void subscribe() {

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
