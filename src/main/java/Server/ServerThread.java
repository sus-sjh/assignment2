package Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Room.Room;
import Room.RoomList;
import User.User;


public class ServerThread extends Thread {
    private User user;
    private ArrayList<User> userList;
    private RoomList map;   
    private long roomId;
    private PrintWriter pw;
    public ServerThread(User user,
                        ArrayList<User> userList, RoomList map){
        this.user=user;
        this.userList=userList;
        this.map=map;
        pw=null;
        roomId = -1;
    }
    
    public void run(){
        try{
            for (int i = 0; true;) {
                String msg=user.getBr().readLine();
                System.out.println(msg); 
                parseMsg(msg);
            }
        }catch (SocketException se) { 
            System.out.println("user "+user.getName()+" logout.");
            System.out.println("yes");

        }catch (Exception e) { 
            e.printStackTrace();
        }finally {
            try {
          
                remove(user);
                user.getBr().close();
                user.getSocket().close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
    
    private void parseMsg(String msg) throws IOException {
        String code = null;
        String message=null;
        if(msg.length()>0){
            /*匹配指令类型部分的字符串*/
            Pattern pattern = Pattern.compile("<code>(.*)</code>");
            Matcher matcher = pattern.matcher(msg);
            if(matcher.find()){
                code = matcher.group(1);
            }
            /*匹配消息部分的字符串*/
            pattern = Pattern.compile("<msg>(.*)</msg>");
            matcher = pattern.matcher(msg);
            if(matcher.find()){
                message = matcher.group(1);
            }

            switch (Objects.requireNonNull(code)) {
                case "join" -> {
                    // add to room list
                    if (roomId == -1) {
                        assert message != null;
                        roomId = Long.parseLong(message);
                        map.join(user, roomId);
                        sendRoomMsgExceptSelf(buildCodeWithMsg("<name>" + user.getName() + "</name><id>" + user.getId() + "</id>", 11));
                        returnMsg(buildCodeWithMsg("你加入了聊天:" + map.getRoom(roomId).getName(), 1));
                        returnMsg(buildCodeWithMsg(getMembersInRoom(), 21));
                    } else {
                        map.esc(user, roomId);
                        sendRoomMsg(buildCodeWithMsg("" + user.getId(), 12));
                        long oldRoomId = roomId;
                        assert message != null;
                        roomId = Long.parseLong(message);
                        map.join(user, roomId);
                        sendRoomMsgExceptSelf(buildCodeWithMsg("<name>" + user.getName() + "</name><id>" + user.getId() + "</id>", 11));
                        returnMsg(buildCodeWithMsg("你退出聊天:" + map.getRoom(oldRoomId).getName() + ",并加入了聊天:" + roomId, 1));
                        returnMsg(buildCodeWithMsg(getMembersInRoom(), 21));
                    }
                }
                case "esc" -> {
                    // esc from room
                    if (roomId != -1) {
                        int flag = map.esc(user, roomId);
                        sendRoomMsgExceptSelf(buildCodeWithMsg("" + user.getId(), 12));
                        long oldRoomId = roomId;
                        roomId = -1;
                        returnMsg(buildCodeWithMsg("你已经成功退出聊天，不会收到消息", 2));
                        if (flag == 0) {
                            sendMsg(buildCodeWithMsg("" + oldRoomId, 13));
                        }
                    } else {
                        returnMsg(buildCodeWithMsg("你尚未加入任何聊天", 2));
                    }
                }
                case "list" -> returnMsg(buildCodeWithMsg(getRoomsList(), 3));
                case "listuser" -> {
                    returnMsg(buildCodeWithMsg(getUserList(), 7));
                    sendMsg(buildCodeWithMsg(getUserList(), 7));
                }
                case "message" ->
                        sendRoomMsg(buildCodeWithMsg("<from>" + user.getId() + "</from><smsg>" + message + "</smsg>", 4));
                case "create" -> {
                    roomId = map.createRoom(message);
                    map.join(user, roomId);
                    sendMsg(buildCodeWithMsg("<rid>" + roomId + "</rid><rname>" + message + "</rname>", 15));
                    returnMsg(buildCodeWithMsg("你进入了创建的聊天：" + map.getRoom(roomId).getName(), 5));
                    returnMsg(buildCodeWithMsg(getMembersInRoom(), 21));
                }
                case "single" -> {
                    String message1 = message + "and" + user.getName();
                    String message2 = "用户" + user.getName() + "邀请" + message + "进行聊天";
                    roomId = map.createRoom(message);
                    map.join(user, roomId);
                    sendMsg(buildCodeWithMsg("<rid>" + roomId + "</rid><rname>" + message1 + "</rname>", 15));
                    returnMsg(buildCodeWithMsg("你进入了私聊的聊天：" + map.getRoom(roomId).getName(), 5));
                    returnMsg(buildCodeWithMsg(getMembersInRoom(), 21));
                    sendMsg(buildCodeWithMsg(message2, 6));
                }
                case "setName" -> {
                    user.setName(message);
                    sendRoomMsg(buildCodeWithMsg("<id>" + user.getId() + "</id><name>" + message + "</name>", 16));
                }
                case "quit" -> {
                    if (roomId != -1) {
                        map.esc(user, roomId);
                        sendRoomMsgExceptSelf(buildCodeWithMsg("" + user.getId(), 12));
                        long oldRoomId = roomId;
                        roomId = -1;
                        returnMsg(buildCodeWithMsg("你已经成功退出聊天，不会收到消息", 2));
                        sendMsg(buildCodeWithMsg("" + oldRoomId, 13));
                    }
                    remove(user);
                    user.getBr().close();
                    user.getSocket().close();
                }

                default -> System.out.println("not valid message from user" + user.getId());
            }


        }

    }

    private String getUserList() {
        StringBuilder stringBuffer = new StringBuilder();
        for(User each: userList){
            stringBuffer.append("<member><name>").append(each.getName()).append("</name><id>").append(each.getId()).append("</id></member>");
        }
        return stringBuffer.toString();
    }

    private String getMembersInRoom(){
        Room room = map.getRoom(roomId);
        StringBuilder stringBuffer = new StringBuilder();
        if(room != null){
            ArrayList<User> users = room.getUsers();
            for(User each: users){
                stringBuffer.append("<member><name>").append(each.getName()).append("</name><id>").append(each.getId()).append("</id></member>");
            }
        }
        return stringBuffer.toString();
    }


    private String getRoomsList(){
        String[][] strings = map.listRooms();
        StringBuilder sb = new StringBuilder();
        for (String[] string : strings) {
            sb.append("<room><rname>").append(string[1]).append("</rname><rid>").append(string[0]).append("</rid></room>");
        }
        return sb.toString();
    }

    private String buildCodeWithMsg(String msg, int code){
        return "<code>"+code+"</code><msg>"+msg+"</msg>\n";
    }

    private void sendMsg(String msg) {
        for(User each:userList){
            try {
                pw=each.getPw();
                pw.println(msg);
                pw.flush();
                System.out.println(msg);
            } catch (Exception e) {
                System.out.println("exception in sendMsg()");
            }
        }
    }

    private void sendRoomMsg(String msg){
        Room room = map.getRoom(roomId);
        if(room != null){
            ArrayList<User> users = room.getUsers();
            for(User each: users){
                pw = each.getPw();
                pw.println(msg);
                pw.flush();
            }
        }
    }

    private void sendRoomMsgExceptSelf(String msg){
        Room room = map.getRoom(roomId);
        if(room != null){
            ArrayList<User> users = room.getUsers();
            for(User each: users){
                if(each.getId()!=user.getId()){
                    pw = each.getPw();
                    pw.println(msg);
                    pw.flush();
                }
            }
        }
    }

    private void returnMsg(String msg){
        try{
            pw = user.getPw();
            pw.println(msg);
            pw.flush();
        }catch (Exception e) {
            System.out.println("exception in returnMsg()");
        }
    }

    private void remove(User user){
        sendMsg(buildCodeWithMsg(""+user.getId(), 8));
        if(roomId!=-1){
            int flag=map.esc(user, roomId);
            sendRoomMsgExceptSelf(buildCodeWithMsg(""+user.getId(), 12));
            long oldRoomId=roomId;
            roomId = -1;
            if(flag==0){
                sendMsg(buildCodeWithMsg(""+oldRoomId, 13));
            }
        }
        userList.remove(user);
    }

    private void sendUserList(){
        sendMsg(buildCodeWithMsg(getUserList(), 7));
    }

    private void sendRoomList(){
        sendMsg(buildCodeWithMsg(getRoomsList(), 3));
    }

    private void sendRoomUserList(){
        returnMsg(buildCodeWithMsg(getMembersInRoom(), 21));
    }

}