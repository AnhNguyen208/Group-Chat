import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ServerThread implements Runnable {
    private Socket socketOfServer;
    private int clientNumber;
    private String clientName;
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
                if(messageSplit[0].equals("SEND_TO_GLOBAL")){
                    String temp = "GLOBAL_MESSAGE" + "|" + this.clientName + "|" + messageSplit[1] +
                            "|" + messageSplit[2];
                    writeMessageHistory(temp);
                    Server.serverThreadBus.boardCast(this.getClientNumber(), temp);
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
                        Server.serverThreadBus.mutilCastSend("NOTIFICATION|--- " + this.clientName +" đã đăng nhập---");

                        System.out.println("Check exists file for " + this.clientNumber + ": " + (new File("files/messageHistory.txt").exists()));
                        if(new File("files/messageHistory.txt").exists()) {
                            loadMessageHistory();
                        }
                    } else {
                        write("LOGIN|FAILED");
                        closed();
                    }
                }
                if(messageSplit[0].equals("SEND_FILE")) {
                    receiveFile(messageSplit[1]);
                    String temp = "GLOBAL_FILE" + "|" + this.clientName + "|" + messageSplit[1] + "|" + messageSplit[2];
                    writeMessageHistory(temp);
                    Server.serverThreadBus.boardCast(this.getClientNumber(), temp, messageSplit[1]);
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

    public void writeMessageHistory(String message) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("files/messageHistory.txt", true));
        writer.append(message);
        writer.append('\n');
        writer.close();
    }

    public void loadMessageHistory() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("files/messageHistory.txt"));
            String line = reader.readLine();

            while ((line != null) && (line.length() > 0)) {
                Server.serverThreadBus.sendMessageToPerson(this.clientNumber, line);
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
