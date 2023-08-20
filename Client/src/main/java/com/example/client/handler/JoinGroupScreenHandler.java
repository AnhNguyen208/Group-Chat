package com.example.client.handler;

import com.example.client.entity.User;
import com.example.client.utils.Config;
import com.example.client.utils.ConnectServer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class JoinGroupScreenHandler extends BaseScreenHandler implements Initializable {
    @FXML
    private TextField username;
    @FXML
    private TextField password;
    @FXML
    private Button btnConnect;
    public JoinGroupScreenHandler(Stage stage, String screenPath) throws IOException {
        super(stage, screenPath);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnConnect.setOnMouseClicked(e -> {
            if ((!username.getText().equals("")) && (!password.getText().equals(""))) {
                try {
                    BaseScreenHandler.user = new User();
                    BaseScreenHandler.user.setUsername(username.getText());
                    BaseScreenHandler.connectServer = new ConnectServer();
                    String request = "JOIN_GROUP|" + username.getText() + "|" + password.getText() + "|";
                    BaseScreenHandler.connectServer.setUpSocket();
                    BaseScreenHandler.connectServer.write(request);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                displayAlert(Alert.AlertType.ERROR, "Lỗi", "Hãy nhập tài khoản và mật khẩu");
            }
        });

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Runnable updater = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if(BaseScreenHandler.isLoginSuccess) {
                                File theDir = new File("files/" + BaseScreenHandler.user.getUsername().replace(" ",
                                        "_"));
                                if (!theDir.exists()){
                                    theDir.mkdirs();
                                }
                                GroupChatScreenHandler groupChatScreenHandler = new GroupChatScreenHandler(stage, Config.GROUP_CHAT_SCREEN_PATH);
                                groupChatScreenHandler.show();
                                BaseScreenHandler.isLoginSuccess = false;
                            }

                            if (BaseScreenHandler.isIsLoginFailed){
                                displayAlert(Alert.AlertType.ERROR, "Lỗi", "Đăng nhập không thành công");
                                BaseScreenHandler.isIsLoginFailed = false;
                            }
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                };
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    // UI update is run on the Application thread
                    Platform.runLater(updater);
                }
            }
        });
        // don't let thread prevent JVM shutdown
        thread.setDaemon(true);
        thread.start();
    }


}
