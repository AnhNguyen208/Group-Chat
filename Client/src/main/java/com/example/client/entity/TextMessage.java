package com.example.client.entity;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class TextMessage extends Message{
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public TextMessage(String username, String content, String time) {
        super(username, time);
        this.content = content;
    }

    protected TextFlow textFlow(Text text) {
        TextFlow textFlow = new TextFlow(text);
        textFlow.setMaxWidth(380);
        textFlow.setStyle(
                "-fx-color: rgb(239, 242, 255);" +
                        "-fx-background-color: rgb(15, 125, 242);" +
                        "-fx-background-radius: 20px;");

        textFlow.setPadding(new javafx.geometry.Insets(5, 10, 5, 10));

        return textFlow;
    }

    protected TextFlow textFlow1(Text text) {
        TextFlow textFlow = new TextFlow(text);
        textFlow.setMaxWidth(380);
        textFlow.setStyle(
                "-fx-background-color: rgb(233, 233, 235);" +
                        "-fx-background-radius: 20px;");
        textFlow.setPadding(new Insets(5, 10, 5, 10));

        return textFlow;
    }


    @Override
    public HBox sendMessage() {
        HBox hBox = generateHbox(Pos.CENTER_RIGHT);

        ImageView imageView = avatar();
        HBox.setMargin(imageView, new Insets(0, 10, 0, 10));

        VBox vBox1 = new VBox();
        Text text = new Text(content);
        text.setFont(new Font("Arial", 24));
        text.setFill(Color.color(0.934, 0.925, 0.996));

        Label name = new Label(username);
        name.setPrefWidth(text.getLayoutBounds().getWidth() + 10);

        vBox1.getChildren().add(name);
        vBox1.getChildren().add(textFlow(text));
        vBox1.getChildren().add(new Label(time));

        hBox.getChildren().add(vBox1);
        hBox.getChildren().add(imageView);
        return hBox;
    }

    @Override
    public HBox receiveMessage() {
        HBox hBox = generateHbox(Pos.CENTER_LEFT);

        ImageView imageView = avatar();
        HBox.setMargin(imageView, new Insets(0, 10, 0, 10));

        VBox vBox1 = new VBox();
        Text text = new Text(content);
        text.setFont(new Font("Arial", 24));

        vBox1.getChildren().add(new Label(username));
        vBox1.getChildren().add(textFlow1(text));
        vBox1.getChildren().add(hBox(text));
        vBox1.getChildren().add(new Label(time));
        hBox.getChildren().add(imageView);
        hBox.getChildren().add(vBox1);
        return hBox;
    }
}
