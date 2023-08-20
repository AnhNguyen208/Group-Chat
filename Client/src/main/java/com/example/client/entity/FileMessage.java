package com.example.client.entity;

import com.example.client.FileChooserSample;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileMessage extends Message{
    private final File file;
    public FileMessage(String username, String time, File file) {
        super(username, time);
        this.file = file;
    }

    protected TextFlow textFlow(Text text, ImageView imageView) {
        TextFlow textFlow = new TextFlow(imageView, text);
        textFlow.setMaxWidth(380);
        textFlow.setStyle(
                "-fx-color: rgb(239, 242, 255);" +
                        "-fx-background-color: rgb(15, 125, 242);" +
                        "-fx-background-radius: 20px;");

        textFlow.setPadding(new Insets(5, 10, 5, 10));
        return textFlow;
    }

    protected TextFlow textFlow1(Text text, ImageView imageView) {
        TextFlow textFlow = new TextFlow(text, imageView);
        textFlow.setMaxWidth(380);
        textFlow.setStyle(
                "-fx-background-color: rgb(233, 233, 235);" +
                        "-fx-background-radius: 20px;");
        textFlow.setPadding(new Insets(5, 10, 5, 10));

        return textFlow;
    }

    public HBox sendMessage() {
        HBox hBox = generateHbox(Pos.CENTER_RIGHT);

        ImageView imageView = avatar();
        HBox.setMargin(imageView, new Insets(0, 10, 0, 10));

        VBox vBox1 = new VBox();

        Text text = new Text("  " + file.getName());
        text.setFont(new Font("Arial", 24));
        text.setFill(Color.color(0.934, 0.925, 0.996));
        text.setOnMouseClicked(e -> {
            try {
                Desktop desktop = Desktop.getDesktop();
                desktop.open(file);
            } catch (IOException ex) {
                Logger.getLogger(
                        FileChooserSample.class.getName()).log(
                        Level.SEVERE, null, ex
                );
            }
        });

        File file = new File("images/file1.jpg");
        javafx.scene.image.Image image = new Image(file.toURI().toString());
        ImageView imageView1 = new ImageView();
        imageView1.setImage(image);
        imageView1.setFitHeight(25);
        imageView1.setFitWidth(20);

        Label name = new Label(username);
        name.setPrefWidth(text.getLayoutBounds().getWidth() + 10);

        vBox1.getChildren().add(name);
        vBox1.getChildren().add(textFlow(text, imageView1));
        vBox1.getChildren().add(new Label(time));
        hBox.getChildren().add(vBox1);
        hBox.getChildren().add(imageView);

        return hBox;
    }

    public HBox receiveMessage() {
        HBox hBox = generateHbox(Pos.CENTER_LEFT);

        ImageView imageView = avatar();
        HBox.setMargin(imageView, new Insets(0, 10, 0, 10));

        VBox vBox1 = new VBox();
        Text text = new Text(file.getName() + "  ");
        text.setFont(new Font("Arial", 24));
        text.setOnMouseClicked(e -> {
            try {
                Desktop desktop = Desktop.getDesktop();
                desktop.open(file);
            } catch (IOException ex) {
                Logger.getLogger(
                        FileChooserSample.class.getName()).log(
                        Level.SEVERE, null, ex
                );
            }
        });

        File file = new File("images/file.jpg");
        javafx.scene.image.Image image = new Image(file.toURI().toString());
        ImageView imageView1 = new ImageView();
        imageView1.setImage(image);
        imageView1.setFitHeight(25);
        imageView1.setFitWidth(20);

        vBox1.getChildren().add(new Label(username));
        vBox1.getChildren().add(textFlow1(text, imageView1));
        vBox1.getChildren().add(hBox(text));

        vBox1.getChildren().add(new Label(time));
        hBox.getChildren().add(imageView);
        hBox.getChildren().add(vBox1);
        return hBox;
    }
}
