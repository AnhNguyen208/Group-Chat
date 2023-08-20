package com.example.client.utils;

import com.example.client.entity.*;
import com.example.client.handler.BaseScreenHandler;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ConnectServer {
    private Thread thread;
    private Socket socketOfClient;
    private BufferedWriter os;
    private BufferedReader is;
    public ConnectServer() throws IOException {
        // Gửi yêu cầu kết nối tới Server đang lắng nghe
        // trên máy 'localhost' cổng 7777.
        socketOfClient = new Socket("localhost", 8888);
        System.out.println("Kết nối thành công!");
        // Tạo luồng đầu ra tại client (Gửi dữ liệu tới server)
        os = new BufferedWriter(new OutputStreamWriter(socketOfClient.getOutputStream()));
        // Luồng đầu vào tại Client (Nhận dữ liệu từ server).
        is = new BufferedReader(new InputStreamReader(socketOfClient.getInputStream()));
    }

    public void setUpSocket() {
        try {
            thread = new Thread(() -> {
                try {
                    String message;
                    while (true) {
                        message = is.readLine();
                        System.out.println("Message: " + message);

                        if(message == null){
                            break;
                        }

                        String[] messageSplit = message.split("[|]");

                        if(messageSplit[0].equals("GET_ID")){
                            BaseScreenHandler.user.setId(Integer.valueOf(messageSplit[1]));
                        }

                        if (messageSplit[0].equals("UPDATE_ONLINE_LIST")) {
                            String[] messageSplit1;
                            BaseScreenHandler.onlineList = new ArrayList<>();
                            for (int i = 1; i < messageSplit.length; i++) {
                                BaseScreenHandler.isUpdateOnlineList = true;
                                messageSplit1 = messageSplit[i].split("-");
                                if(!messageSplit1[1].equals(BaseScreenHandler.user.getUsername())) {
                                    BaseScreenHandler.onlineList.add(new User(Integer.valueOf(messageSplit1[0]), messageSplit1[1]));
                                }
                            }
                        }

//                        if(messageSplit[0].equals("GLOBAL_MESSAGE")) {
//                            if(!BaseScreenHandler.user.getUsername().equals(messageSplit[1])) {
//                                BaseScreenHandler.newMessage = new TextMessage(messageSplit[1], messageSplit[2], messageSplit[3]);
//                                BaseScreenHandler.isNewMessage = true;
//                            }
//                        }

                        if(messageSplit[0].equals("MESSAGE_FROM_GROUP")) {
                            if(!BaseScreenHandler.user.getUsername().equals(messageSplit[1])) {
                                BaseScreenHandler.newMessage = new TextMessage(messageSplit[2], messageSplit[3], messageSplit[4]);
                                BaseScreenHandler.checkCurrentGroup(messageSplit[1]);
                            }
                        }

                        if(messageSplit[0].equals("MESSAGE_FROM_PERSON")) {
                            BaseScreenHandler.newMessage = new TextMessage(messageSplit[2], messageSplit[3], messageSplit[4]);
                            BaseScreenHandler.isNewMessage = true;
                        }

                        if(messageSplit[0].equals("LOGIN")) {
                            if(messageSplit[1].equals("SUCCESS")) {
                                BaseScreenHandler.isLoginSuccess = true;
                            } else {
                                BaseScreenHandler.isIsLoginFailed = true;
                            }
                        }

//                        if(messageSplit[0].equals("GLOBAL_FILE")) {
//                            receiveFile(messageSplit[2]);
//                            if(!BaseScreenHandler.user.getUsername().equals(messageSplit[1])) {
//                                File file = new File("files/" +
//                                        BaseScreenHandler.user.getUsername().replace(" ", "_") + "/" +
//                                        messageSplit[2]);
//                                String mimetype = Files.probeContentType(file.toPath());
//                                if (mimetype != null && mimetype.split("/")[0].equals("image")) {
//                                    BaseScreenHandler.newMessage = new ImageMessage(messageSplit[1], messageSplit[3],
//                                            file, ImageIO.read(file));
//                                } else {
//                                    BaseScreenHandler.newMessage = new FileMessage(messageSplit[1], messageSplit[3], file);
//                                }
//                                BaseScreenHandler.isNewMessage = true;
//                            }
//                        }

                        if(messageSplit[0].equals("FILE_FROM_GROUP")) {
                            receiveFile(messageSplit[3]);
                            if(!BaseScreenHandler.user.getUsername().equals(messageSplit[2])) {
                                File file = new File("files/" + BaseScreenHandler.user.getUsername() + "/" +
                                        messageSplit[3]);
                                String mimetype = Files.probeContentType(file.toPath());
                                if (mimetype != null && mimetype.split("/")[0].equals("image")) {
                                    BaseScreenHandler.newMessage = new ImageMessage(messageSplit[2], messageSplit[4],
                                            file, ImageIO.read(file));
                                } else {
                                    BaseScreenHandler.newMessage = new FileMessage(messageSplit[2], messageSplit[4], file);
                                }
                                BaseScreenHandler.checkCurrentGroup(messageSplit[1]);
                            }
                        }

                        if(messageSplit[0].equals("FILE_FROM_PERSON")) {
                            receiveFile(messageSplit[3]);
                            if(!BaseScreenHandler.user.getUsername().equals(messageSplit[2])) {
                                File file = new File("files/" + BaseScreenHandler.user.getUsername() + "/" +
                                        messageSplit[3]);
                                String mimetype = Files.probeContentType(file.toPath());
                                if (mimetype != null && mimetype.split("/")[0].equals("image")) {
                                    BaseScreenHandler.newMessage = new ImageMessage(messageSplit[2], messageSplit[4],
                                            file, ImageIO.read(file));
                                } else {
                                    BaseScreenHandler.newMessage = new FileMessage(messageSplit[2], messageSplit[4], file);
                                }
                                BaseScreenHandler.isNewMessage = true;
                            }
                        }

                        if (messageSplit[0].equals("GROUP_LIST")) {
                            BaseScreenHandler.groupList = new ArrayList<>();
                            String[] messageSplit1;
                            for (int i = 1; i < messageSplit.length; i++) {
                                messageSplit1 = messageSplit[i].split("-");
                                List<User> userList = new ArrayList<>();
                                for (int j = 1; j < messageSplit1.length; j++) {
                                    User user = new User();
                                    user.setUsername(messageSplit1[j]);
                                    userList.add(user);
                                }
                                BaseScreenHandler.groupList.add(new Group(messageSplit1[0], userList));
                            }
                        }

                        if (messageSplit[0].equals("UPDATE_GROUP_LIST")) {
                            String[] messageSplit1;
                            messageSplit1 = messageSplit[1].split("-");
                            List<User> userList = new ArrayList<>();
                            for (int j = 1; j < messageSplit1.length; j++) {
                                User user = new User();
                                user.setUsername(messageSplit1[j]);
                                userList.add(user);
                            }
                            BaseScreenHandler.groupList.add(new Group(messageSplit1[0], userList));
                            BaseScreenHandler.isIsUpdateGroupList = true;
                        }

                        if(messageSplit[0].equals("ALL_USER")) {
                            BaseScreenHandler.allUser = new ArrayList<>();
                            for (int i = 1; i < messageSplit.length; i++) {
                                BaseScreenHandler.allUser.add(new User(i, messageSplit[i]));
                            }
                        }
                    }
//                    os.close();
//                    is.close();
//                    socketOfClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void write(String message) throws IOException{
        os.write(message);
        os.newLine();
        os.flush();
    }

    public void sendFile(String message, String path) throws Exception{
        write(message);
        DataOutputStream dataOutputStream = new DataOutputStream(socketOfClient.getOutputStream());
        int bytes = 0;
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);
//        System.out.println("Size of file: " + file.length() + " File path: " + file.getPath());
        // send file size
        dataOutputStream.writeLong(file.length());
        // break file into chunks
        byte[] buffer = new byte[4*1024];
        while ((bytes=fileInputStream.read(buffer))!=-1){
            dataOutputStream.write(buffer,0,bytes);
            dataOutputStream.flush();
        }
        fileInputStream.close();
    }

    private void receiveFile(String fileName) throws Exception{
        DataInputStream dataInputStream  = new DataInputStream(socketOfClient.getInputStream());
        int bytes = 0;
        FileOutputStream fileOutputStream = new FileOutputStream("files/" +
                BaseScreenHandler.user.getUsername().replace(" ", "_") + "/" + fileName);

        long size = dataInputStream.readLong();     // read file size
//        System.out.println("Size of file: " + size);
        byte[] buffer = new byte[4*1024];
        while (size > 0 && (bytes = dataInputStream.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
            fileOutputStream.write(buffer,0,bytes);
            size -= bytes;      // read upto file size
        }
        fileOutputStream.close();
    }
}
