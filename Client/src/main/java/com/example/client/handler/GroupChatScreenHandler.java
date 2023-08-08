package com.example.client.handler;

import com.example.client.entity.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    private VBox chatBox;
    @FXML
    private TextField message;
    @FXML
    private Button btnSend;
    @FXML
    private MenuButton listOnline;
    @FXML
    private ImageView upload;
    public GroupChatScreenHandler(Stage stage, String screenPath) throws IOException {
        super(stage, screenPath);
    }

//    public void displayAllMessage() throws IOException {
//        try {
//            File myObj = new File("message/group14.txt");
//            Scanner myReader = new Scanner(myObj);
//            while (myReader.hasNextLine()) {
//                String data = myReader.nextLine();
//                String [] split = data.split("[|]");
//                Message message1 = new Message(split[0], split[1], split[2]);
//                chatBox.getChildren().add(message1.sendMessage());
//                chatBox.getChildren().add(message1.receiveMessage());
//                //System.out.println(data);
//            }
//            myReader.close();
//        } catch (FileNotFoundException e) {
//            System.out.println("An error occurred.");
//            e.printStackTrace();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    private void displayOnlineList() {
        listOnline.getItems().clear();
        List<CheckMenuItem> itemList = new ArrayList<>();
        for (User user : BaseScreenHandler.onlineList) {
            CheckMenuItem checkMenuItem = new CheckMenuItem(user.getUsername());
            checkMenuItem.setId(user.getId().toString());
            itemList.add(checkMenuItem);
        }
        listOnline.getItems().addAll(itemList);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            File file = new File("images/upload.jpg");
            Image image = new Image(file.toURI().toString());
            upload.setImage(image);
            btnSend.setOnMouseClicked(e -> {
                try {
                    Message message1 = new TextMessage(BaseScreenHandler.user.getUsername(), message.getText(),
                            DateTimeFormatter.ofPattern("HH:mm").format(LocalDateTime.now()));
                    chatBox.getChildren().add(message1.sendMessage());
                    BaseScreenHandler.connectServer.write("SEND_TO_GLOBAL" + "|" +
                            ((TextMessage) message1).getContent() + "|" + message1.getTime() + "|");
                    message.clear();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
            upload.setOnMouseClicked(e-> {
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
                                BaseScreenHandler.connectServer.sendFile("SEND_FILE" + "|" + file1.getName() +
                                        "|" + message1.getTime() + "|", file1.getPath());
                            } catch (Exception ex) {
                                throw new RuntimeException(ex);
                            }
                        } else {
                            try {
                                Message message1 = new FileMessage(BaseScreenHandler.user.getUsername(),
                                        DateTimeFormatter.ofPattern("HH:mm").format(LocalDateTime.now()), file1);
                                chatBox.getChildren().add(message1.sendMessage());
                                BaseScreenHandler.connectServer.sendFile("SEND_FILE" + "|" + file1.getName() +
                                        "|" + message1.getTime() + "|", file1.getPath());
                            } catch (Exception ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
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
                        }
                    };
                    while (true) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
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
