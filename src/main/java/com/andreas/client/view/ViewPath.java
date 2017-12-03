package com.andreas.client.view;

public enum ViewPath {
    LOGIN("/login_view.fxml"),
    MAIN("/main_view.fxml"),
    UPLOAD("/upload_view.fxml");

    String name;

    ViewPath(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
