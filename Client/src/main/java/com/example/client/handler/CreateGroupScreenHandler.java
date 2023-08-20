package com.example.client.handler;

import com.example.client.entity.User;
import com.example.client.utils.Config;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CreateGroupScreenHandler extends BaseScreenHandler implements Initializable {
    @FXML
    private TextField groupName;
    @FXML
    private MenuButton groupMember;
    @FXML
    private ListView<String> listView;
    @FXML
    private Button btnCreate;
    @FXML
    private Button btnCancel;
    public CreateGroupScreenHandler(Stage stage, String screenPath) throws IOException {
        super(stage, screenPath);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<String> memberList = new ArrayList<>();
        memberList.add(BaseScreenHandler.user.getUsername());
        List<CheckMenuItem> itemList = new ArrayList<>();
        List<User> friendList = BaseScreenHandler.allUser;
        for(User user: friendList) {
            if (!user.getUsername().equals(BaseScreenHandler.user.getUsername())) {
                CheckMenuItem checkMenuItem = new CheckMenuItem(user.getUsername());
                checkMenuItem.setId(user.getId().toString());
                itemList.add(checkMenuItem);
            }
        }
        groupMember.setText("Chọn thành viên nhóm");
        groupMember.getItems().addAll(itemList);

        for (CheckMenuItem item : itemList) {
            item.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
                if (newValue) {
                    listView.getItems().add(item.getText());
                    memberList.add(item.getText());
                } else {
                    listView.getItems().remove(item.getText());
                    memberList.remove(item.getText());
                }
            });
        }

        btnCreate.setOnMouseClicked(e -> {
            if(memberList.size() < 3) {
                displayAlert(Alert.AlertType.WARNING, "Cảnh báo", "Thành viên nhóm cần ít nhất 3 người");
            } else if (groupName.getText().isEmpty()) {
                displayAlert(Alert.AlertType.WARNING, "Cảnh báo", "Hãy điền tên nhóm");
            } else {
                try {
                    String mes = "CREATE_GROUP|" + groupName.getText() + "|";
                    for (String string: memberList) {
                        mes += string + "|";
                    }
                    BaseScreenHandler.connectServer.write(mes);
                    GroupChatScreenHandler groupChatScreenHandler = new GroupChatScreenHandler(this.stage, Config.GROUP_CHAT_SCREEN_PATH);
                    groupChatScreenHandler.show();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        btnCancel.setOnMouseClicked(e -> {
            try {
                GroupChatScreenHandler groupChatScreenHandler = new GroupChatScreenHandler(this.stage, Config.GROUP_CHAT_SCREEN_PATH);
                groupChatScreenHandler.show();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

    }
}
