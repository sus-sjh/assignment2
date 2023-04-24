package Client;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class ClientThread extends Thread{
    private Socket socket;
    private Client client;
    private BufferedReader br;
    private PrintWriter pw;
    public ClientThread(Socket socket, Client client){
        this.client = client;
        this.socket = socket;
        try {
            br=new BufferedReader(new InputStreamReader(socket.getInputStream()));

        } catch (IOException e) {
            System.out.println("cannot get input stream from socket.");
        }
    }

    public void run() {
        try{
            br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while(true){
                String msg = br.readLine();
                parseMessage(msg);
            }
        }catch (SocketException s){
            System.out.println("Server is closed");
            JOptionPane.showMessageDialog(client.getFrame(), "服务器已关闭！");

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void parseMessage(String message){
        String code = null;
        String msg=null;

        try {
        if(message.length()>0){
            var pattern = Pattern.compile("<code>(.*)</code>");
            Matcher matcher;
            matcher = pattern.matcher(message);
            if(matcher.find()){
                code = matcher.group(1);
            }
            pattern = Pattern.compile("<msg>(.*)</msg>");
            matcher = pattern.matcher(message);
            if(matcher.find()){
                msg = matcher.group(1);
            }
            System.out.println(code+":"+msg);
            switch(code){
                case "1", "5", "6":
                    client.updateTextArea(msg);
                    break;
                case "2":
                    client.showEscDialog(msg);
                    break;
                case "3":
                    client.listRooms(msg);
                    break;
                case "4":
                    client.updateTextAreaFromUser(msg);
                    break;
                case "7":
                    client.listUsers1(msg);
                    break;
                case "8":
                    client.delUser1(msg);
                    break;
                case "9":
                    client.delRoom1(msg);
                    break;
                case "10":
                    client.addRoom1(msg);
                    break;
                case "11":
                    client.addUser(msg);
                    break;
                case "12":
                    client.delUser(msg);
                    break;
                case "13":
                    client.delRoom(msg);
                    break;
                case "15":
                    client.addRoom(msg);
                    break;
                case "16":
                    client.updateUser(msg);
                    break;
                case "21":
                    client.listUsers(msg);
                    break;
            }
        }
        }catch (Exception e) {
            System.out.println("parseMessage error");
        }

    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public BufferedReader getBr() {
        return br;
    }

    public void setBr(BufferedReader br) {
        this.br = br;
    }

    public PrintWriter getPw() {
        return pw;
    }

    public void setPw(PrintWriter pw) {
        this.pw = pw;
    }




}
