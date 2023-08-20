package com.example.client;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public final class FileChooserSample extends Application {
    @Override
    public void start(Stage primaryStage) {
        ContextMenu contextMenu = new ContextMenu();
        Menu menu1 = new Menu("Scrollable Submenu");
        Menu menu2 = new Menu("Regular Submenu");
        contextMenu.getItems().addAll(menu1, menu2);

        for (int i = 1 ; i <= 10; i++) {
            CheckMenuItem checkMenuItem = new CheckMenuItem("Option " + i);
            menu1.getItems().add(checkMenuItem);
        }

        Button button = new Button("Menu");
        button.setContextMenu(contextMenu);

        BorderPane root = new BorderPane();
        root.setTop(new HBox(button));

        Scene scene = new Scene(root, 300, 250);

        primaryStage.setTitle("Dropdown Menu");
        primaryStage.setScene(scene);
        primaryStage.show();

        button.setOnAction(e -> button.getContextMenu().show(button.getScene().getWindow()));
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}