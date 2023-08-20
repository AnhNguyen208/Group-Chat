package com.example.client.entity;

import com.example.client.FileChooserSample;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImageMessage extends Message{
    private BufferedImage bufferedImage;
    private File file;

    public ImageMessage(String username, String time, File file, BufferedImage bufferedImage) {
        super(username, time);
        this.file = file;
        this.bufferedImage = bufferedImage;
    }

    private ImageView generateImage() throws MalformedURLException {
        int width = this.bufferedImage.getWidth();
        int height = this.bufferedImage.getHeight();
        ImageView imageView1 = new ImageView(file.toURI().toURL().toExternalForm());
        imageView1.setFitHeight((double) (height * 380) /width);
        imageView1.setFitWidth(380);
        imageView1.setPreserveRatio(true);
        imageView1.setSmooth(true);

        imageView1.setOnMouseClicked(e-> {
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
        return imageView1;
    }

    public HBox sendMessage() throws MalformedURLException {
        HBox hBox = generateHbox(Pos.CENTER_RIGHT);

        ImageView imageView = avatar();
        HBox.setMargin(imageView, new Insets(0, 10, 0, 10));

        VBox vBox1 = new VBox();
        Label name = new Label(username);
        name.setPrefWidth(generateImage().getFitWidth() + 10);

        vBox1.getChildren().add(name);
        vBox1.getChildren().add(generateImage());

        vBox1.getChildren().add(new javafx.scene.control.Label(time));
        hBox.getChildren().add(vBox1);
        hBox.getChildren().add(imageView);

        return hBox;
    }

    public HBox receiveMessage() throws MalformedURLException {
        HBox hBox = generateHbox(Pos.CENTER_LEFT);

        ImageView imageView = avatar();
        HBox.setMargin(imageView, new Insets(0, 10, 0, 10));

        VBox vBox1 = new VBox();
        vBox1.getChildren().add(new javafx.scene.control.Label(username));
        vBox1.getChildren().add(generateImage());

        vBox1.getChildren().add(new Label(time));
        hBox.getChildren().add(imageView);
        hBox.getChildren().add(vBox1);
        return hBox;
    }
}
