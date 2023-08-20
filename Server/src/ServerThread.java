import java.io.*;
import java.net.Socket;
import java.util.*;

public class ServerThread implements Runnable {
    private final Socket socketOfServer;
    private final int clientNumber;
    private String clientName;
    private Map<String, List<String>> group;
    private BufferedReader is;
    private BufferedWriter os;
    private boolean isClosed;

    public BufferedReader getIs() {
        return is;
    }

    public BufferedWriter getOs() {
        return os;
    }

    public int getClientNumber() {
        return clientNumber;
    }

    public String getClientName() {
        return clientName;
    }

    public Map<String, List<String>> getGroup() {
        return group;
    }

    public ServerThread(Socket socketOfServer, int clientNumber) {
        this.socketOfServer = socketOfServer;
        this.clientNumber = clientNumber;
        System.out.println("Server thread number " + clientNumber + " Started");
        isClosed = false;
    }

    @Override
    public void run() {
        try {
            // Mở luồng vào ra trên Socket tại Server.
            is = new BufferedReader(new InputStreamReader(socketOfServer.getInputStream()));
            os = new BufferedWriter(new OutputStreamWriter(socketOfServer.getOutputStream()));
            System.out.println("Khời động luông mới thành công, ID là: " + clientNumber);

            String message;
            while (!isClosed) {
                message = is.readLine();
                System.out.println("Message: " + message);
                if (message == null) {
                    break;
                }
                String[] messageSplit = message.split("[|]");
//                if(messageSplit[0].equals("SEND_TO_GLOBAL")){
//                    String temp = "GLOBAL_MESSAGE" + "|" + this.clientName + "|" + messageSplit[1] +
//                            "|" + messageSplit[2];
//                    writeMessageHistory("messageHistory.txt", temp);
//                    Server.serverThreadBus.boardCast(this.getClientNumber(), temp);
//                }

                if(messageSplit[0].equals("SEND_MESSAGE_TO_GROUP")){
                    String temp = "MESSAGE_FROM_GROUP" + "|" + messageSplit[1] + "|" + this.clientName +
                            "|" + messageSplit[2] + "|" + messageSplit[3];
                    writeMessageHistory(messageSplit[1] + ".txt", temp);
                    Server.serverThreadBus.boardCast(this.clientName, group.get(messageSplit[1]), temp);
                }
//                if(messageSplit[0].equals("send-to-person")){
//                    Server.serverThreadBus.sendMessageToPerson(Integer.parseInt(messageSplit[3]),"Client "
//                            + messageSplit[2]+" (tới bạn): "+messageSplit[1]);
//                }
                if(messageSplit[0].equals("JOIN_GROUP")){
                    if (messageSplit.length < 3) {
                        write("LOGIN|FAILED");
                        closed();
                        break;
                    }
//                    System.out.println("JOIN GROUP REQUEST");
                    if(checkUser(messageSplit[1], messageSplit[2])) {
                        clientName = messageSplit[1];
                        write("GET_ID|" + this.clientNumber);
                        write("LOGIN|SUCCESS");
                        Server.serverThreadBus.sendOnlineList();
                        sendGroupList();
                        Server.serverThreadBus.sendMessageToPerson(this.clientNumber, getAllUser());
                        Server.serverThreadBus.mutilCastSend("NOTIFICATION|--- " + this.clientName +" đã đăng nhập---");

                        for (String groupName : group.keySet()) {
                            String fileName = "files/" + groupName + ".txt";
                            if(new File(fileName).exists()) {
                                loadMessageHistory(fileName);
                            }
                        }
                    } else {
                        write("LOGIN|FAILED");
                        closed();
                    }
                }
//                if(messageSplit[0].equals("SEND_FILE")) {
//                    receiveFile(messageSplit[1]);
//                    String temp = "GLOBAL_FILE" + "|" + this.clientName + "|" + messageSplit[1] + "|" + messageSplit[2];
//                    writeMessageHistory("messageHistory.txt", temp);
//                    Server.serverThreadBus.boardCast(this.getClientNumber(), temp, messageSplit[1]);
//                }

                if(messageSplit[0].equals("SEND_FILE_TO_GROUP")) {
                    receiveFile(messageSplit[2]);
                    String temp = "FILE_FROM_GROUP" + "|" + messageSplit[1] + "|" + this.clientName +
                            "|" + messageSplit[2] + "|" + messageSplit[3];
                    writeMessageHistory(messageSplit[1] + ".txt", temp);
                    Server.serverThreadBus.boardCast(this.clientName, group.get(messageSplit[1]), temp, messageSplit[2]);
                }

                if(messageSplit[0].equals("SEND_FILE_TO_PERSON")) {
                    receiveFile(messageSplit[2]);
                    String temp = "FILE_FROM_PERSON" + "|" + messageSplit[1] + "|" + this.clientName +
                            "|" + messageSplit[2] + "|" + messageSplit[3];
                    writeMessageHistory(messageSplit[1] + ".txt", temp);
                    Server.serverThreadBus.sendMessageToPerson(messageSplit[1], temp, messageSplit[2]);
                }

                if(messageSplit[0].equals("SEND_MESSAGE_TO_PERSON")){
                    String temp = "MESSAGE_FROM_PERSON" + "|" + messageSplit[1] + "|" + this.clientName +
                            "|" + messageSplit[2] + "|" + messageSplit[3];
                    writeMessageHistory(messageSplit[1] + ".txt", temp);
                    Server.serverThreadBus.sendMessageToPerson(messageSplit[1], temp);
                }

                if (messageSplit[0].equals("CREATE_GROUP")) {
                    List<String> strings = new ArrayList<>();
                    for(int i = 2; i < messageSplit.length; i++) {
                        strings.add(messageSplit[i]);
                    }
                    for (ServerThread serverThread: Server.serverThreadBus.getListServerThreads()) {
                        serverThread.getGroup().put(messageSplit[1], strings);
                    }
                    updateGroupList(message.substring(message.indexOf("|") + 1), strings);
                }
            }
        } catch (IOException e) {
            closed();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void write(String message) throws IOException{
        os.write(message);
        os.newLine();
        os.flush();
    }

    public void closed() {
        isClosed = true;
        Server.serverThreadBus.remove(clientNumber);
        System.out.println(this.clientNumber+" đã thoát");
        Server.serverThreadBus.sendOnlineList();
        Server.serverThreadBus.mutilCastSend("global-message"+","+"---Client "+this.clientNumber+" đã thoát---");
    }

    private String getAllUser() {
        String mes = "ALL_USER|";
        try {
            File myObj = new File("account.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String [] split = data.split("[|]");
                mes += split[0] + "|";
                //System.out.println(data);
            }
            myReader.close();

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return mes;
    }

    private boolean checkUser(String username, String password) {
        try {
            File myObj = new File("account.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String [] split = data.split("[|]");
                if(split[0].equals(username) && split[1].equals(password)) {
                    return true;
                }
                //System.out.println(data);
            }
            myReader.close();

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return false;
    }

    private void updateGroupList(String info, List<String> list) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("group.txt", true));
        writer.append('\n');
        writer.append(info);
        writer.close();

        String mes = "UPDATE_GROUP_LIST|";
        mes += info.replace("|", "-");

        Server.serverThreadBus.boardCast(list, mes);
    }

    private void sendGroupList() {
        String res = "GROUP_LIST|";
        try {
            this.group = new HashMap<>();
            File myObj = new File("group.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String temp = "";
                List<String> list = new ArrayList<>();
                boolean temp1 = false;
                String data = myReader.nextLine();
                String [] split = data.split("[|]");
                temp += split[0];
                for (int i = 1; i < split.length; i++) {
                    if(split[i].equals(this.clientName)) {
                        temp1 = true;
                    }
                    temp += "-" + split[i];
                    list.add(split[i]);
                }
                temp += "|";

                if (temp1) {
                    res += temp;
                    this.group.put(split[0], list);
                }
            }
            myReader.close();
            Server.serverThreadBus.sendMessageToPerson(this.clientNumber, res);
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private void receiveFile(String fileName) throws Exception{
//        System.out.println(fileName);
        DataInputStream dataInputStream  = new DataInputStream(socketOfServer.getInputStream());
        int bytes = 0;
        FileOutputStream fileOutputStream = new FileOutputStream("files/" + fileName);

        long size = dataInputStream.readLong();     // read file size
//        System.out.println("Size of file: " + size);
        byte[] buffer = new byte[4*1024];
        while (size > 0 && (bytes = dataInputStream.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
            fileOutputStream.write(buffer,0,bytes);
            size -= bytes;      // read upto file size
        }
        fileOutputStream.close();
    }

    public void sendFile(String message, String path) throws Exception{
        write(message);
        DataOutputStream dataOutputStream = new DataOutputStream(socketOfServer.getOutputStream());
        int bytes = 0;
        File file = new File("files/" + path);
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

    public void writeMessageHistory(String filename, String message) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("files/" + filename, true));
        writer.append(message);
        writer.append('\n');
        writer.close();
    }

    public void loadMessageHistory(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line = reader.readLine();

            while ((line != null) && (line.length() > 0)) {
                if(line.contains("FILE")) {
                    String [] split = line.split("[|]");
                    Server.serverThreadBus.sendMessageToPerson(this.clientNumber, line, split[3]);
                } else {
                    Server.serverThreadBus.sendMessageToPerson(this.clientNumber, line);
                }
                line = reader.readLine();
                Thread.sleep(3000);
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
