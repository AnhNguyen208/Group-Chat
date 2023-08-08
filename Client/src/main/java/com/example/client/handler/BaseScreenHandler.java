package com.example.client.handler;

import com.example.client.entity.Message;
import com.example.client.entity.User;
import com.example.client.utils.ConnectServer;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class BaseScreenHandler extends FXMLScreenHandler {
    private Scene scene;
    protected final Stage stage;
    protected static ConnectServer connectServer;
    public static List<User> onlineList;
    public static User user;
    public static Message newMessage;
    public static boolean isLoginSuccess;
    public static boolean isNewMessage;
    public static boolean isUpdateOnlineList;

    public BaseScreenHandler(Stage stage, String screenPath) throws IOException {
        super(screenPath);
        this.stage = stage;
    }

    public void show() {
        if (this.scene == null) {
            this.scene = new Scene(this.content);
        }
        this.stage.setScene(this.scene);
        this.stage.show();
    }
    public void displayAlert(Alert.AlertType alertType, String title, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText("Thông báo:");
        alert.setContentText(contentText);
        alert.showAndWait();
    }

}
