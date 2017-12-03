package com.andreas.client.view;

import com.andreas.client.controller.ClientController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class UploadView {

    private File choosenFile = null;

    @FXML
    TextField filenameField;

    @FXML
    CheckBox publicAccessCheckBox, readOnlyCheckBox;

    @FXML
    Text statusText;


    @FXML
    public void initialize(){
        statusText.setText("");
        filenameField.setEditable(false);
    }

    @FXML
    public void upload(){
        if (choosenFile == null){
            statusText.setText("You must choose a file");
            return;
        }
        boolean readOnly = readOnlyCheckBox.isSelected();
        boolean publicAccess = publicAccessCheckBox.isSelected();

        ClientController.getInstance().uploadFile(choosenFile, readOnly, publicAccess);

    }

    @FXML
    public void chooseFile(ActionEvent actionEvent){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null)
            filenameField.setText(file.getName());
    }


}
