package com.example.client.handler;

import com.example.client.entity.*;
import com.example.client.utils.Config;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class GroupChatScreenHandler extends BaseScreenHandler implements Initializable {
    @FXML
    private MenuButton menuButton;
    @FXML
    private Label nameLabel;
    @FXML
    private ImageView avatar;
    @FXML
    private VBox chatBox;
    @FXML
    private TextField message;
    @FXML
    private Button btnSend;
    @FXML
    private Menu onlineListMenu;
    @FXML
    private Menu groupListMenu;
    @FXML
    private MenuItem createGroupMenuItem;
    @FXML
    private ImageView upload;
    public GroupChatScreenHandler(Stage stage, String screenPath) throws IOException {
        super(stage, screenPath);
    }

    private void displayOnlineList() {
        onlineListMenu.getItems().clear();
        List<MenuItem> itemList = new ArrayList<>();
        for (User user : BaseScreenHandler.onlineList) {
            MenuItem menuItem = new MenuItem(user.getUsername());
            menuItem.setId(user.getId().toString());
            menuItem.setOnAction(e -> {
                chatBox.getChildren().clear();
                nameLabel.setText(menuItem.getText());
                File file = new File("images/user.png");
                Image image = new Image(file.toURI().toString());
                avatar.setImage(image);
                BaseScreenHandler.receiver = user;
                BaseScreenHandler.currentGroup = null;
            });
            itemList.add(menuItem);
        }
        onlineListMenu.getItems().addAll(itemList);
    }

    private void displayGroupList() {
        groupListMenu.getItems().clear();
        List<MenuItem> itemList = new ArrayList<>();
        for (Group group: BaseScreenHandler.groupList) {
            MenuItem item = new MenuItem(group.getGroupName());
            item.setOnAction(e -> {
                chatBox.getChildren().clear();
                nameLabel.setText(item.getText());
                File file = new File("images/people.png");
                Image image = new Image(file.toURI().toString());
                avatar.setImage(image);
                BaseScreenHandler.currentGroup = group;
                for (HBox hbox: group.getMessageList()) {
                    chatBox.getChildren().add(hbox);
                }
                BaseScreenHandler.receiver = null;
            });
            itemList.add(item);
        }

        groupListMenu.getItems().addAll(itemList);
    }

    private boolean checkGroupOrPerson() {
        boolean isGroup = false;
        for (Group group: BaseScreenHandler.groupList) {
            if((BaseScreenHandler.currentGroup != null) &&
                    (group.getGroupName().equals(BaseScreenHandler.currentGroup.getGroupName()))) {
                isGroup = true;
            }
        }
        return isGroup;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            menuButton.setText("Xin chào, " + BaseScreenHandler.user.getUsername());
            createGroupMenuItem.setOnAction(e -> {
                try {
                    CreateGroupScreenHandler createGroupScreenHandler = new CreateGroupScreenHandler(this.stage, Config.CREATE_GROUP_CHAT_SCREEN_PATH);
                    createGroupScreenHandler.show();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
            displayGroupList();
            File file = new File("images/upload.jpg");
            Image image = new Image(file.toURI().toString());
            upload.setImage(image);
            btnSend.setOnMouseClicked(e -> {
                if((BaseScreenHandler.currentGroup != null) || (BaseScreenHandler.receiver != null)) {
                    try {
                        Message message1 = new TextMessage(BaseScreenHandler.user.getUsername(), message.getText(),
                                DateTimeFormatter.ofPattern("HH:mm").format(LocalDateTime.now()));
                        if(checkGroupOrPerson()) {
                            BaseScreenHandler.currentGroup.getMessageList().add(message1.sendMessage());
                            BaseScreenHandler.connectServer.write("SEND_MESSAGE_TO_GROUP" + "|" +
                                    nameLabel.getText() + "|" + ((TextMessage) message1).getContent() +
                                    "|" + message1.getTime() + "|");
                        } else {
                            BaseScreenHandler.connectServer.write("SEND_MESSAGE_TO_PERSON" + "|" +
                                    nameLabel.getText() + "|" + ((TextMessage) message1).getContent() +
                                    "|" + message1.getTime() + "|");
                        }
                        chatBox.getChildren().add(message1.sendMessage());
                        message.clear();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    displayAlert(Alert.AlertType.WARNING, "Cảnh báo",
                            "Bạn chưa chọn nhóm(người nhân) để nhắn tin");
                }

            });
            upload.setOnMouseClicked(e-> {
                if((BaseScreenHandler.currentGroup != null) || (BaseScreenHandler.receiver != null)) {
                    FileChooser fileChooser = new FileChooser();
                    File file1 = fileChooser.showOpenDialog(stage);
                    if (file1 != null) {
                        try {
                            String mimetype = Files.probeContentType(file1.toPath());
                            if (mimetype != null && mimetype.split("/")[0].equals("image")) {
                                try {
                                    Message message1 = new ImageMessage(BaseScreenHandler.user.getUsername(),
                                            DateTimeFormatter.ofPattern("HH:mm").format(LocalDateTime.now()), file1,
                                            ImageIO.read(file1));
                                    chatBox.getChildren().add(message1.sendMessage());
                                    if(checkGroupOrPerson()) {
                                        BaseScreenHandler.currentGroup.getMessageList().add(message1.sendMessage());
                                        BaseScreenHandler.connectServer.sendFile("SEND_FILE_TO_GROUP" + "|" +
                                                nameLabel.getText() + "|" + file1.getName() + "|" +
                                                message1.getTime() + "|", file1.getPath());
                                    } else {
                                        BaseScreenHandler.connectServer.sendFile("SEND_FILE_TO_PERSON" + "|" +
                                                nameLabel.getText() + "|" + file1.getName() + "|" +
                                                message1.getTime() + "|", file1.getPath());
                                    }
                                } catch (Exception ex) {
                                    throw new RuntimeException(ex);
                                }
                            } else {
                                try {
                                    Message message1 = new FileMessage(BaseScreenHandler.user.getUsername(),
                                            DateTimeFormatter.ofPattern("HH:mm").format(LocalDateTime.now()), file1);
                                    chatBox.getChildren().add(message1.sendMessage());
                                    if(checkGroupOrPerson()) {
                                        BaseScreenHandler.currentGroup.getMessageList().add(message1.sendMessage());
                                        BaseScreenHandler.connectServer.sendFile("SEND_FILE_TO_GROUP" + "|" +
                                                nameLabel.getText() + "|" + file1.getName() + "|" +
                                                message1.getTime() + "|", file1.getPath());
                                    } else {
                                        BaseScreenHandler.connectServer.sendFile("SEND_FILE_TO_PERSON" + "|" +
                                                nameLabel.getText() + "|" + file1.getName() + "|" +
                                                message1.getTime() + "|", file1.getPath());
                                    }
                                } catch (Exception ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                } else {
                    displayAlert(Alert.AlertType.WARNING, "Cảnh báo", "Bạn chưa chọn nhóm để nhắn tin");
                }

            });
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Runnable updater = new Runnable() {
                        @Override
                        public void run() {
                            if(isNewMessage) {
                                try {
                                    if(checkGroupOrPerson()) {
                                        BaseScreenHandler.currentGroup.getMessageList().add(newMessage.receiveMessage());
                                    }
                                    chatBox.getChildren().add(newMessage.receiveMessage());
                                    isNewMessage = false;
                                } catch (MalformedURLException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            if(isUpdateOnlineList) {
                                displayOnlineList();
                                isUpdateOnlineList = false;
                            }
                            if (isIsUpdateGroupList) {
                                displayGroupList();
                                isIsUpdateGroupList = false;
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
