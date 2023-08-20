package com.example.client.entity;

import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private String groupName;
    private List<User> userList;
    private List<HBox> messageList;

    public Group(String groupName) {
        this.groupName = groupName;
    }

    public Group(String groupName, List<User> userList) {
        this.groupName = groupName;
        this.userList = userList;
        this.messageList = new ArrayList<>();
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public List<HBox> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<HBox> messageList) {
        this.messageList = messageList;
    }
}
