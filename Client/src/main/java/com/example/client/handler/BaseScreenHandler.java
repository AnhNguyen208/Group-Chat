package com.example.client.handler;

import com.example.client.entity.Group;
import com.example.client.entity.Message;
import com.example.client.entity.User;
import com.example.client.utils.ConnectServer;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

public class BaseScreenHandler extends FXMLScreenHandler {
    private Scene scene;
    protected final Stage stage;
    protected static ConnectServer connectServer;
    public static List<User> allUser;
    public static List<User> onlineList;
    public static List<Group> groupList;
    public static Group currentGroup;
    public static User receiver;
    public static User user;
    public static Message newMessage;
    public static boolean isLoginSuccess;
    public static boolean isIsLoginFailed;
    public static boolean isNewMessage;
    public static boolean isUpdateOnlineList;
    public static boolean isIsUpdateGroupList;

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

    public static void checkCurrentGroup(String groupName) throws MalformedURLException {
        if((currentGroup != null) && (currentGroup.getGroupName().equals(groupName))) {
            BaseScreenHandler.isNewMessage = true;
        }  else {
            for (Group group : BaseScreenHandler.groupList) {
                if(group.getGroupName().equals(groupName)) {
                    if(BaseScreenHandler.newMessage.getUsername().equals(BaseScreenHandler.user.getUsername())) {
                        group.getMessageList().add(BaseScreenHandler.newMessage.sendMessage());
                    } else {
                        group.getMessageList().add(BaseScreenHandler.newMessage.receiveMessage());
                    }
                }
            }
        }
    }
}
