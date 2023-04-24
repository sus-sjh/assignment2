package Room;

import java.util.ArrayList;
import java.util.List;

import User.User;


public class Room {
    private String name;
    private long roomId;
    private ArrayList<User> list;
    private int totalUsers;
    private boolean isPrivate;

    /**
     * 使用聊天的名称和id来new一个聊天
     *
     * @param name
     * @param roomId
     */
    public Room(String name, long roomId) {
        this.name = name;
        this.roomId = roomId;
        this.totalUsers = 0;
        list = new ArrayList<>();
    }

    /**
     * 获得聊天的名字
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * 设置聊天的新名字
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获得聊天的id号
     *
     * @return
     */
    public long getRoomId() {
        return roomId;
    }

    /**
     * 设置聊天的id
     *
     * @param roomId
     */
    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    /**
     * 向聊天中加入一个新用户
     *
     * @param user
     */
    public void addUser(User user) {
        if (!list.contains(user)) {
            list.add(user);
            totalUsers++;
        } else {
            System.out.println("User is already in Room<" + name + ">:" + user);
        }
    }

    /**
     * 从聊天中删除一个用户
     *
     * @param user
     * @return 目前该聊天中的总用户数目
     */
    public int delUser(User user) {
        if (list.contains(user)) {
            list.remove(user);
            return --totalUsers;
        } else {
            System.out.println("User is not in Room<" + name + ">:" + user);
            return totalUsers;
        }
    }

    /**
     * 获得当前聊天的用户列表
     *
     * @return
     */
    public ArrayList<User> getUsers() {
        return list;
    }

    /**
     * 获得当前聊天的用户昵称的列表
     *
     * @return
     */
    public String[] getUserNames() {
        String[] userList = new String[list.size()];
        int i = 0;
        for (User each : list) {
            userList[i++] = each.getName();
        }
        return userList;
    }

    public ArrayList<User> getList() {
        return list;
    }

    public void setList(ArrayList<User> list) {
        this.list = list;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public int getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(int totalUsers) {
        this.totalUsers = totalUsers;
    }

    public String toString() {
        return "Room<" + name + ">:" + roomId;
    }

}
