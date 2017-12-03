package com.andreas.client.view;

import com.andreas.client.controller.ClientController;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class UploadView {

    @FXML
    TextField filenameField;

    @FXML
    CheckBox publicAccessCheckBox, readOnlyCheckBox;

    @FXML
    Text statusText;


    @FXML
    public void initialize(){
        statusText.setText("");
    }

    @FXML
    public void upload(){
        if (filenameField.getText().equals("")){
            statusText.setText("Filename can not be empty.");
            return;
        }
        String filename = filenameField.getText();
        boolean readOnly = readOnlyCheckBox.isSelected();
        boolean publicAccess = publicAccessCheckBox.isSelected();

        ClientController.getInstance().uploadFile(filename, readOnly, publicAccess);

    }


}
