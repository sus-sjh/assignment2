package Server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.json.*;
import Room.Room;
import Room.RoomList;
import User.User;

public class Server {
    private ArrayList<User> allUsers;
    private RoomList rooms;
    private int port;
    private ServerSocket ss;
    private long unusedUserID;
    public final long MAX_USERS = 999999;

    public Server(int port) throws Exception {
        allUsers = new ArrayList<>();
        rooms = new RoomList();
        this.port=port;
        unusedUserID=1;
        ss = new ServerSocket(port);
        System.out.println("Server is builded!");
    }


    private long getNextUserID(){
        if(unusedUserID < MAX_USERS)
            return unusedUserID++;
        else
            return -1;
    }


    public void startListen() throws Exception{
        for(int i=0;true;){
            Socket socket = ss.accept();
            long id = getNextUserID();
            if(id != -1){
                User user = new User("User"+id, id, socket);
                System.out.println(user.getName() + " is login...");
                allUsers.add(user);
                ServerThread thread = new ServerThread(user, allUsers, rooms);
                thread.start();
            }else{
                System.out.println("Server is full!");
                socket.close();
            }
        }
    }


    public static void main(String[] args) {
        try {
            Server server = new Server(10001);
            server.startListen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getPort() {
        return port;
    }

    public ServerSocket getSs() {
        return ss;
    }

    public ArrayList<User> getAllUsers() {
        return allUsers;
    }

    public long getMAX_USERS() {
        return MAX_USERS;
    }

    public long getUnusedUserID() {
        return unusedUserID;
    }

    public RoomList getRooms() {
        return rooms;
    }

    public void setRooms(RoomList rooms) {
        this.rooms = rooms;
    }

    public void setAllUsers(ArrayList<User> allUsers) {
        this.allUsers = allUsers;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setSs(ServerSocket ss) {
        this.ss = ss;
    }

    public void setUnusedUserID(long unusedUserID) {
        this.unusedUserID = unusedUserID;
    }

    public void remove(User user) {
        allUsers.remove(user);
    }

    public void add(User user) {
        allUsers.add(user);
    }


}