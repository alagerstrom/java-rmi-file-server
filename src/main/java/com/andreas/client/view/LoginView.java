package com.andreas.client.view;

import com.andreas.client.controller.ClientController;
import com.andreas.common.Constants;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.channels.CompletionHandler;

public class LoginView {

    @FXML
    TabPane tabPane;

    @FXML
    TextField
            loginUsernameField,
            loginPasswordField,
            registerUsernameField,
            registerPasswordField;
    @FXML
    Text
            loginStatusText,
            registerStatusText;

    @FXML
    public void initialize() {
        loginStatusText.setText("Connecting...");
        registerStatusText.setText("");
        tabPane.setDisable(true);

        ClientController.getInstance().connect(new CompletionHandler<Void, String>() {
            @Override
            public void completed(Void result, String attachment) {
                Platform.runLater(() -> {
                    loginStatusText.setText(attachment);
                    tabPane.setDisable(false);
                });
            }

            @Override
            public void failed(Throwable exc, String attachment) {
                Platform.runLater(() -> {
                    loginStatusText.setText(attachment);
                    tabPane.setDisable(false);
                });
            }
        });
    }

    @FXML
    public void login() {
        tabPane.setDisable(true);
        String username = loginUsernameField.getText();
        if (username == null || username.equals("")) {
            registerStatusText.setText("Username can not be empty.");
            tabPane.setDisable(false);
            return;
        }
        String password = loginPasswordField.getText();
        ClientController.getInstance().login(username, password, new CompletionHandler<Void, String>() {
            @Override
            public void completed(Void result, String attachment) {
                Platform.runLater(() -> {
                    loginStatusText.setText(attachment);
                    tabPane.setDisable(false);
                    tabPane.getScene().getWindow().hide();
                    showMainView();
                });
            }

            @Override
            public void failed(Throwable exc, String attachment) {
                Platform.runLater(() -> {
                    tabPane.setDisable(false);
                    loginStatusText.setText(attachment);
                });
            }
        });

    }

    private void showMainView() {

        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource(ViewPath.MAIN.toString()));
        } catch (IOException e) {
            System.err.println("Failed to show main view.");
        }
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(Constants.WINDOW_TITLE);
        stage.show();

    }

    @FXML
    public void register() {
        String username = registerUsernameField.getText();
        if (username.equals("")) {
            registerStatusText.setText("Username can not be empty.");
            return;
        }
        String password = registerPasswordField.getText();
        ClientController.getInstance().registerUser(username, password, new CompletionHandler<Void, String>() {
            @Override
            public void completed(Void result, String attachment) {
                Platform.runLater(() -> {
                    registerStatusText.setText(attachment);
                    tabPane.getScene().getWindow().hide();
                    showMainView();
                });
            }

            @Override
            public void failed(Throwable exc, String attachment) {
                Platform.runLater(() -> {
                    registerStatusText.setText(attachment);
                });
            }
        });
    }

}
