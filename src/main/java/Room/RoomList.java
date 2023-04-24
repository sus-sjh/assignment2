package Room;

import java.awt.image.DirectColorModel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import User.User;

public class RoomList {
    private HashMap<Long, Room> map;
    private long unusedRoomId;
    public static long MAX_ROOMS = 9999;
    private int totalRooms;

    /**
     * 未使用的roomid从1算起，起始的聊天总数为0
     */
    public RoomList(){
        map = new HashMap<>();
        unusedRoomId = 1;
        totalRooms = 0;
    }

    /**
     * 创建一个新的聊天，使用未使用的聊天号进行创建，如果没有可以使用的则就创建失败
     * @param name： 聊天的名字
     * @return 创建的聊天的id
     */
    public long createRoom(String name){
        if(totalRooms<MAX_ROOMS){
            if(name.length()==0){
                name = ""+unusedRoomId;
            }
            Room room = new Room(name, unusedRoomId);
            map.put(unusedRoomId, room);
            totalRooms++;
            return unusedRoomId++;
        }else{
            return -1;
        }
    }
    /**
     * 用户加入一个聊天
     * @param user
     * @param roomID
     * @return
     */
    public boolean join(User user, long roomID){
        if(map.containsKey(roomID)){
            map.get(roomID).addUser(user);
            return true;
        }else{
            return false;
        }
    }

    /**
     * 用户退出他的聊天
     * @param user
     * @param roomID
     * @return
     */
    public int esc(User user, long roomID){
        if(map.containsKey(roomID)){
            int number = map.get(roomID).delUser(user);
            /*如果这个聊天剩下的人数为0，那么删除该聊天*/
            if(number==0){
                map.remove(roomID);
                totalRooms--;
                return 0;
            }
            return 1;
        }else{
            return -1;
        }
    }
    /**
     * 列出所有聊天的列表，返回一个二维数组，strings[i][0]放聊天的id，string[i][1]放聊天的name
     * @return
     */
    public String[][] listRooms(){
        String[][] strings = new String[totalRooms][2];
        int i=0;
        /*将map转化为set并使用迭代器来遍历*/
        Set<Entry<Long, Room>> set = map.entrySet();
        Iterator<Entry<Long, Room>> iterator = set.iterator();
        while(iterator.hasNext()){
            Map.Entry<Long, Room> entry = iterator.next();
            long key = entry.getKey();
            Room value = entry.getValue();
            strings[i][0]=""+key;
            strings[i][1]=value.getName();
        }
        return strings;
    }

    /**
     * 通过roomID来获得聊天
     * @param roomID
     * @return
     */
    public Room getRoom(long roomID){
        if(map.containsKey(roomID)){
            return map.get(roomID);
        }
        else
            return null;
    }

    public HashMap<Long, Room> getMap() {
        return map;
    }

    public void setMap(HashMap<Long, Room> map) {
        this.map = map;
    }

    public long getUnusedRoomId() {
        return unusedRoomId;
    }

    public void setUnusedRoomId(long unusedRoomId) {
        this.unusedRoomId = unusedRoomId;
    }

    public int getTotalRooms() {
        return totalRooms;
    }

    public void setTotalRooms(int totalRooms) {
        this.totalRooms = totalRooms;
    }

    public static long getMAX_ROOMS() {
        return MAX_ROOMS;
    }

    public static void setMAX_ROOMS(long mAX_ROOMS) {
        MAX_ROOMS = mAX_ROOMS;
    }

}
