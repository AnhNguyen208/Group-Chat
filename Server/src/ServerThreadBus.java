import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ServerThreadBus {
    private List<ServerThread> listServerThreads;

    public List<ServerThread> getListServerThreads() {
        return listServerThreads;
    }

    public ServerThreadBus() {
        listServerThreads = new ArrayList<>();
    }

    public void add(ServerThread serverThread){
        listServerThreads.add(serverThread);
    }

    public void mutilCastSend(String message){ //like sockets.emit in socket.io
        for(ServerThread serverThread : Server.serverThreadBus.getListServerThreads()){
            try {
                serverThread.write(message);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

//    public void boardCast(int id, String message){
//        for(ServerThread serverThread : Server.serverThreadBus.getListServerThreads()){
//            if (serverThread.getClientNumber() != id) {
//                try {
//                    serverThread.write(message);
//                } catch (IOException ex) {
//                    ex.printStackTrace();
//                }
//            }
//        }
//    }

    public void boardCast(String username, List<String> list, String message) {
        for(ServerThread serverThread : Server.serverThreadBus.getListServerThreads()){
            if ((list.contains(serverThread.getClientName())) && (!serverThread.getClientName().equals(username))) {
                try {
                    serverThread.write(message);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void boardCast(List<String> list, String message) {
        for(ServerThread serverThread : Server.serverThreadBus.getListServerThreads()){
            if (list.contains(serverThread.getClientName())) {
                try {
                    serverThread.write(message);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

//    public void boardCast(int id, String message, String path){
//        for(ServerThread serverThread : Server.serverThreadBus.getListServerThreads()){
//            if (serverThread.getClientNumber() != id) {
//                try {
//                    serverThread.sendFile(message, path);
//                } catch (IOException ex) {
//                    ex.printStackTrace();
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }
//    }

    public void boardCast(String username, List<String> list, String message, String path){
        for(ServerThread serverThread : Server.serverThreadBus.getListServerThreads()){
            if ((list.contains(serverThread.getClientName())) && (!serverThread.getClientName().equals(username))) {
                try {
                    serverThread.sendFile(message, path);
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public int getLength(){
        return listServerThreads.size();
    }

    public void sendOnlineList(){
        String res = "";
        List<ServerThread> threadBus = Server.serverThreadBus.getListServerThreads();
        for(ServerThread serverThread : threadBus){
            res += "|" + serverThread.getClientNumber() + "-" + serverThread.getClientName();
        }
        Server.serverThreadBus.mutilCastSend("UPDATE_ONLINE_LIST" + res);
    }


    public void sendMessageToPerson(int id, String message){
        for(ServerThread serverThread : Server.serverThreadBus.getListServerThreads()){
            if(serverThread.getClientNumber() == id){
                try {
                    serverThread.write(message);
                    break;
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void sendMessageToPerson(String username, String message){
        for(ServerThread serverThread : Server.serverThreadBus.getListServerThreads()){
            if(serverThread.getClientName().equals(username)){
                try {
                    serverThread.write(message);
                    break;
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void sendMessageToPerson(int id, String message, String path){
        for(ServerThread serverThread : Server.serverThreadBus.getListServerThreads()){
            if(serverThread.getClientNumber() == id){
                try {
                    serverThread.sendFile(message, path);
                    break;
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void sendMessageToPerson(String username, String message, String path){
        for(ServerThread serverThread : Server.serverThreadBus.getListServerThreads()){
            if(serverThread.getClientName().equals(username)){
                try {
                    serverThread.sendFile(message, path);
                    break;
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void remove(int id){
        for(int i=0; i<Server.serverThreadBus.getLength(); i++){
            if(Server.serverThreadBus.getListServerThreads().get(i).getClientNumber()==id){
                Server.serverThreadBus.listServerThreads.remove(i);
            }
        }
    }
}