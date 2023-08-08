package com.example.client.entity;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.File;
import java.net.MalformedURLException;
public class Message {
    protected String username;
    protected String time;

    public Message(String username, String time) {
        this.username = username;
        this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    protected HBox generateHbox(Pos pos) {
        HBox hBox = new HBox();
        hBox.setAlignment(pos);
        return hBox;
    }

    protected ImageView avatar() {
        File file = new File("images/user.png");
        Image image = new Image(file.toURI().toString());
        ImageView imageView = new ImageView();
        imageView.setImage(image);
        imageView.setFitHeight(50);
        imageView.setFitWidth(50);

        return imageView;
    }

    protected TextFlow textFlow() {
        TextFlow textFlow = new TextFlow();
        return textFlow;
    }

    protected TextFlow textFlow1() {
        TextFlow textFlow = new TextFlow();
        return textFlow;
    }

    protected HBox hBox(Text text) {
        HBox hBox1 = new HBox();
        Region region = new Region();
        region.setPrefWidth(text.getLayoutBounds().getWidth() - 10);
        hBox1.getChildren().add(region);

        return hBox1;
    }

    public HBox sendMessage() throws MalformedURLException{
        HBox hBox = new HBox();
        return hBox;
    }

    public HBox receiveMessage() throws MalformedURLException {
        HBox hBox = new HBox();
        return hBox;
    }
}
