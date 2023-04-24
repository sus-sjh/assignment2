package Client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public  class  Client implements ActionListener{
    private JFrame frame;
    private Socket socket;
    private BufferedReader br;
    private PrintWriter pw;
    private String name;
    private HashMap<String, Integer> rooms_map;
    private HashMap<String, Integer> users_map;
    private HashMap<String, Integer> users_map2;
    private JTextField host_textfield;
    private JTextField port_textfield;
    private JTextField text_field;
    private JTextField name_textfiled;
    private JLabel rooms_label;
    private JLabel users_label;
    private JList<String> roomlist;
    private JList<String> userlist;
    private JList<String> userlist2;
    private JTextPane msgArea;
    private JScrollPane textScrollPane;
    private JScrollBar vertical;
    DefaultListModel<String> rooms_model;
    DefaultListModel<String> users_model;
    DefaultListModel<String> users_model2;
    private JFrame loginFrame;
    private JFrame registerFrame;
    private JFrame chatFrame;
    private JFrame roomFrame;
    private JFrame roomChatFrame;
    private JFrame roomCreateFrame;
    private JFrame roomJoinFrame;

    public Client(){
        rooms_map = new HashMap<>();
        users_map = new HashMap<>();
        users_map2 = new HashMap<>();
        initialize();
    }


    public boolean connect(String host, int port){
        try {
            socket = new Socket(host, port);
            System.out.println("Connected to server!"+socket.getRemoteSocketAddress());
            br=new BufferedReader(new InputStreamReader(System.in));
            pw=new PrintWriter(socket.getOutputStream());
            ClientThread thread = new ClientThread(socket, this);
            thread.start();

            return true;

        } catch (IOException e) {
            System.out.println("Server error");
            JOptionPane.showMessageDialog(frame, "服务器无法连接！");
            return false;
        }
    }


    public void sendMsg(String msg, String code){
        try {
            pw.println("<code>"+code+"</code><msg>"+msg+"</msg>");
            pw.flush();
        } catch (Exception e) {
            System.out.println("error in sendMsg()");
            JOptionPane.showMessageDialog(frame, "请先连接服务器！");
        }
    }

    private void initialize() {
        /*设置窗口的UI风格和字体*/
        setUIStyle();
        setUIFont();

        JFrame frame = new JFrame("ChatOnline");
        JPanel panel = new JPanel();
        JPanel headPanel = new JPanel();
        JPanel footPanel = new JPanel();
        JPanel leftPanel = new JPanel();
        JPanel rightPanel = new JPanel();
        BorderLayout layout = new BorderLayout();
        GridBagLayout gridBagLayout = new GridBagLayout();
        FlowLayout flowLayout = new FlowLayout();
        frame.setBounds(100, 100, 800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(panel);
        frame.setLayout(layout);
        headPanel.setLayout(flowLayout);
        footPanel.setLayout(gridBagLayout);
        leftPanel.setLayout(gridBagLayout);
        rightPanel.setLayout(gridBagLayout);
        leftPanel.setPreferredSize(new Dimension(130, 0));
        rightPanel.setPreferredSize(new Dimension(130, 0));
        host_textfield = new JTextField("127.0.0.1");
        port_textfield = new JTextField("10001");
        name_textfiled = new JTextField("匿名");
        host_textfield.setPreferredSize(new Dimension(100, 25));
        port_textfield.setPreferredSize(new Dimension(70, 25));
        name_textfiled.setPreferredSize(new Dimension(150, 25));

        JLabel host_label = new JLabel("服务器IP");
        JLabel port_label = new JLabel("端口");
        JLabel name_label = new JLabel("昵称");
        JButton single_chat=new JButton("一对一聊天");
        JButton head_connect = new JButton("连接");
        JButton head_create = new JButton("创建聊天");
        headPanel.add(host_label);
        headPanel.add(host_textfield);
        headPanel.add(port_label);
        headPanel.add(port_textfield);
        headPanel.add(head_connect);
        headPanel.add(name_label);
        headPanel.add(name_textfiled);
        headPanel.add(head_create);
        headPanel.add(single_chat);
        JButton foot_emoji = new JButton("表情");
        JButton foot_send = new JButton("发送");
        text_field = new JTextField();
        footPanel.add(text_field, new GridBagConstraints(0, 0, 1, 1, 100, 100,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        footPanel.add(foot_emoji, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        footPanel.add(foot_send, new GridBagConstraints(2, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        rooms_label = new JLabel("当前聊天数：0");
        users_label = new JLabel("聊天人数：0");
        JButton join_button = new JButton("加入聊天");
        JButton esc_button = new JButton("退出聊天");
        rooms_model = new DefaultListModel<>();
        users_model = new DefaultListModel<>();
        users_model2 = new DefaultListModel<>();
        roomlist = new JList<>(rooms_model);
        userlist = new JList<>(users_model);
        JScrollPane roomListPane = new JScrollPane(roomlist);
        JScrollPane userListPane = new JScrollPane(userlist);
        leftPanel.add(rooms_label, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        leftPanel.add(join_button, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        leftPanel.add(esc_button, new GridBagConstraints(0, 2, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        leftPanel.add(roomListPane, new GridBagConstraints(0, 3, 1, 1, 100, 100,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        rightPanel.add(users_label, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        rightPanel.add(userListPane,new GridBagConstraints(0, 1, 1, 1, 100, 100,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        userlist2 = new JList<>(users_model2);
        JPanel onlineUsersPanel = new JPanel(flowLayout);
        JScrollPane userScrollPane = new JScrollPane(userlist2);
        onlineUsersPanel.add(userScrollPane);
        JFrame onlineUsersFrame = new JFrame("在线用户列表");
        onlineUsersFrame.setContentPane(userScrollPane);
        onlineUsersFrame.setSize(300, 500);
        onlineUsersFrame.setLocationRelativeTo(null);
        onlineUsersFrame.setVisible(true);
        JLabel users_label = new JLabel("在线用户");
        users_label.setFont(new Font("Arial", Font.BOLD, 14));
        users_label.setForeground(Color.BLACK);
        users_label.setVisible(true);
        msgArea = new JTextPane();
        msgArea.setEditable(false);
        textScrollPane = new JScrollPane();
        textScrollPane.setViewportView(msgArea);
        vertical = new JScrollBar(JScrollBar.VERTICAL);
        vertical.setAutoscrolls(true);
        textScrollPane.setVerticalScrollBar(vertical);
        panel.add(headPanel, "North");
        panel.add(footPanel, "South");
        panel.add(leftPanel, "West");
        panel.add(rightPanel, "East");
        panel.add(textScrollPane, "Center");
        head_connect.addActionListener(this);
        single_chat.addActionListener(this);
        foot_send.addActionListener(this);
        head_create.addActionListener(this);
        foot_emoji.addActionListener(this);
        join_button.addActionListener(this);
        esc_button.addActionListener(this);
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        switch (cmd) {
            case "连接":
                String strHost = host_textfield.getText();
                String strPort = port_textfield.getText();
                connect(strHost, Integer.parseInt(strPort));
                String nameSet = JOptionPane.showInputDialog("请输入你的昵称：");
                name_textfiled.setText(nameSet);
                name_textfiled.setEditable(false);
                port_textfield.setEditable(false);
                host_textfield.setEditable(false);
                sendMsg(nameSet, "setName");
                sendMsg("", "list");
                sendMsg("", "listuser");
                break;
            case "加入聊天":
                String selected = roomlist.getSelectedValue();
                if(rooms_map.containsKey(selected)){
                    sendMsg(""+rooms_map.get(selected), "join");
                }
                break;
            case "退出聊天":
                sendMsg("", "esc");
                break;
            case "发送":
                String text = text_field.getText();
                text_field.setText("");
                if(text.length()!=0){
                    sendMsg(text, "message");
                }
                break;
            case "表情":
                IconDialog dialog = new IconDialog(frame, this);
                break;
            case "创建聊天":
                String string = JOptionPane.showInputDialog("请输入你的聊天名称");
                if(string==null || string.equals("")){
                    string = name+(int)(Math.random()*10000)+"的聊天";
                    sendMsg(string, "create");
                } else {
                    sendMsg(string, "create");
                }
                break;
            case "一对一聊天":
                String id = JOptionPane.showInputDialog("请输入对方的id");
                if(id!=null && !id.equals("")){
                    sendMsg(id, "single");
                }
                break;
            default:
                break;
        }

    }

    public void addUser(String content){
        if(content.length()>0){
            Pattern pattern = Pattern.compile("<name>(.*)</name><id>(.*)</id>");
            Matcher matcher = pattern.matcher(content);
            if(matcher.find()){
                String name = matcher.group(1);
                String id = matcher.group(2);
                insertUser(Integer.parseInt(id), name);
                insertMessage(textScrollPane, msgArea, null, "系统：", name+"加入了聊天室");
            }
        }
        users_label.setText("聊天人数："+users_map.size());
    }
    public void addUser1(String content){
        if(content.length()>0){
            Pattern pattern = Pattern.compile("<name>(.*)</name><id>(.*)</id>");
            Matcher matcher = pattern.matcher(content);
            if(matcher.find()){
                String name = matcher.group(1);
                String id = matcher.group(2);
                insertUser1(Integer.parseInt(id), name);
                insertMessage(textScrollPane, msgArea, null, "系统：", name+"加入了聊天室");
            }
        }
        users_label.setText("聊天人数："+users_map.size());
    }
    public void insertUser(int id, String name){
        users_map.put(name, id);
        users_model.addElement(name);
    }

    public void delUser(String content){
        if(content.length()>0){
            int id = Integer.parseInt(content);
            Set<String> set = users_map.keySet();
            Iterator<String> iter = set.iterator();
            String name=null;
            while(iter.hasNext()){
                name = iter.next();
                if(users_map.get(name)==id){
                    users_model.removeElement(name);
                    break;
                }
            }
            users_map.remove(name);
            insertMessage(textScrollPane, msgArea, null, "系统：", name+"退出了聊天室");
        }
        users_label.setText("聊天人数："+users_map.size());
    }


    public void delUser1(String content){
        System.out.println("yes");
        if(content.length()>0){
            int id = Integer.parseInt(content);
            Set<String> set = users_map2.keySet();
            Iterator<String> iter = set.iterator();
            String name=null;
            while(iter.hasNext()){
                name = iter.next();
                if(users_map2.get(name)==id){
                    users_model2.removeElement(name);
                    break;
                }
            }
            users_map2.remove(name);
        }
    }

    public void updateUser(String content){
        if(content.length()>0){
            Pattern pattern = Pattern.compile("<id>(.*)</id><name>(.*)</name>");
            Matcher matcher = pattern.matcher(content);
            if(matcher.find()){
                String id = matcher.group(1);
                String name = matcher.group(2);
                insertUser(Integer.parseInt(id), name);
            }
        }
    }

    public void listUsers(String content){
        String name = null;
        String id=null;
        Pattern rough_pattern=null;
        Matcher rough_matcher=null;
        Pattern detail_pattern=null;
        if(content.length()>0){
            rough_pattern = Pattern.compile("<member>(.*?)</member>");
            rough_matcher = rough_pattern.matcher(content);
            while(rough_matcher.find()){
                String detail = rough_matcher.group(1);
                detail_pattern = Pattern.compile("<name>(.*)</name><id>(.*)</id>");
                Matcher detail_matcher = detail_pattern.matcher(detail);
                if(detail_matcher.find()){
                    name = detail_matcher.group(1);
                    id = detail_matcher.group(2);
                    insertUser(Integer.parseInt(id), name);
                }
            }
        }
        users_label.setText("聊天内人数："+users_map.size());
    }
    public void listUsers1(String content){
        String name = null;
        String id=null;
        Pattern rough_pattern=null;
        Matcher rough_matcher=null;
        Pattern detail_pattern=null;
        if(content.length()>0){
            rough_pattern = Pattern.compile("<member>(.*?)</member>");
            rough_matcher = rough_pattern.matcher(content);
            while(rough_matcher.find()){
                String detail = rough_matcher.group(1);
                detail_pattern = Pattern.compile("<name>(.*)</name><id>(.*)</id>");
                Matcher detail_matcher = detail_pattern.matcher(detail);
                if(detail_matcher.find()){
                    name = detail_matcher.group(1);
                    id = detail_matcher.group(2);
                    insertUser1(Integer.parseInt(id), name);
                }
            }
        }
    }

    public void updateTextArea(String content){
        insertMessage(textScrollPane, msgArea, null, "系统：", content);
    }



    public void updateTextAreaFromUser1(String content){
        if(content.length()>0){
            Pattern pattern = Pattern.compile("<from>(.*)</from><smsg>(.*)</smsg>");
            Matcher matcher = pattern.matcher(content);
            if(matcher.find()){
                String from = matcher.group(1);
                String smsg = matcher.group(2);
                String fromName = getUserName(from);
                if(fromName.equals(name))
                    fromName = "你";
                if(smsg.startsWith("<emoji>")){
                    String emojiCode = smsg.substring(7, smsg.length()-8);
                    System.out.println(emojiCode);
                    insertMessage(textScrollPane, msgArea, emojiCode, fromName+"说：", (String) null);
                    return ;
                }
                String[] smsgs=smsg.split("/n");
                insertMessage(textScrollPane, msgArea, null, fromName+"说：", smsgs);

            }
        }
    }



    public void updateTextAreaFromUser(String content){
        if(content.length()>0){
            Pattern pattern = Pattern.compile("<from>(.*)</from><smsg>(.*)</smsg>");
            Matcher matcher = pattern.matcher(content);
            if(matcher.find()){
                String from = matcher.group(1);
                String smsg = matcher.group(2);
                String fromName = getUserName(from);
                if(fromName.equals(name))
                    fromName = "你";
                if(smsg.startsWith("<emoji>")){
                    String emojiCode = smsg.substring(7, smsg.length()-8);
                    System.out.println(emojiCode);
                    insertMessage(textScrollPane, msgArea, emojiCode, fromName+"说：", (String) null);
                    return ;
                }
                String[] smsgs=smsg.split("/n");
                insertMessage(textScrollPane, msgArea, null, fromName+"说：", smsgs);

            }
        }
    }

    public void showEscDialog(String content){
        JOptionPane.showMessageDialog(frame, content);
        /*清除消息区内容，清除用户数据模型内容和用户map内容，更新聊天内人数*/
        msgArea.setText("");
        users_model.clear();
        users_map.clear();
        users_label.setText("聊天人数：0");

    }
    public void addRoom(String content){
        if(content.length()>0){
            Pattern pattern = Pattern.compile("<rid>(.*)</rid><rname>(.*)</rname>");
            Matcher matcher = pattern.matcher(content);
            if(matcher.find()){
                String rid = matcher.group(1);
                String rname = matcher.group(2);
                insertRoom(Integer.parseInt(rid), rname);
            }
        }
        rooms_label.setText("当前聊天数："+rooms_map.size());
    }


    public void addRoom1(String content){
        if(content.length()>0){
            Pattern pattern = Pattern.compile("<rid>(.*)</rid><rname>(.*)</rname>");
            Matcher matcher = pattern.matcher(content);
            if(matcher.find()){
                String rid = matcher.group(1);
                String rname = matcher.group(2);
                insertRoom1(Integer.parseInt(rid), rname);
            }
        }
    }

    private void insertRoom1(int parseInt, String rname) {
    }

    public void delRoom(String content){
        if(content.length()>0){
            int delRoomId = Integer.parseInt(content);

            Set<String> set = rooms_map.keySet();
            Iterator<String> iter = set.iterator();
            String rname=null;
            while(iter.hasNext()){
                rname = iter.next();
                if(rooms_map.get(rname)==delRoomId){
                    rooms_model.removeElement(rname);
                    break;
                }
            }
            rooms_map.remove(rname);
        }
        rooms_label.setText("当前聊天数："+rooms_map.size());
    }

    public void listRooms(String content){
        String rname = null;
        String rid=null;
        Pattern rough_pattern=null;
        Matcher rough_matcher=null;
        Pattern detail_pattern=null;
        if(content.length()>0){
            rough_pattern = Pattern.compile("<room>(.*?)</room>");
            rough_matcher = rough_pattern.matcher(content);
            while(rough_matcher.find()){
                String detail = rough_matcher.group(1);
                detail_pattern = Pattern.compile("<rname>(.*)</rname><rid>(.*)</rid>");
                Matcher detail_matcher = detail_pattern.matcher(detail);
                if(detail_matcher.find()){
                    rname = detail_matcher.group(1);
                    rid = detail_matcher.group(2);
                    insertRoom(Integer.parseInt(rid), rname);
                }
            }
        }
        rooms_label.setText("当前聊天数："+rooms_map.size());
    }

    private void insertRoom(Integer rid, String rname){
        if(!rooms_map.containsKey(rname)){
            rooms_map.put(rname, rid);
            rooms_model.addElement(rname);
        }else{
            rooms_map.remove(rname);
            rooms_model.removeElement(rname);
            rooms_map.put(rname, rid);
            rooms_model.addElement(rname);
        }
        rooms_label.setText("当前聊天数："+rooms_map.size());
    }

    private void insertUser(Integer id, String name){
        if(!users_map.containsKey(name)){
            users_map.put(name, id);
            users_model.addElement(name);
        }else{
            users_map.remove(name);
            users_model.removeElement(name);
            users_map.put(name, id);
            users_model.addElement(name);
        }
        users_label.setText("聊天人数："+users_map.size());
    }
    public void insertUser1(int id, String name){
        if(!users_map2.containsKey(name)){
            users_map2.put(name, id);
            users_model2.addElement(name);
        }else{
            users_map2.remove(name);
            users_model2.removeElement(name);
            users_map2.put(name, id);
            users_model2.addElement(name);
        }

    }


    private String getUserName(String strId){
        int uid = Integer.parseInt(strId);
        Set<String> set = users_map.keySet();
        Iterator<String> iterator = set.iterator();
        String cur=null;
        while(iterator.hasNext()){
            cur = iterator.next();
            if(users_map.get(cur)==uid){
                return cur;
            }
        }
        return "";
    }


    private String getRoomName(String strId){
        int rid = Integer.parseInt(strId);
        Set<String> set = rooms_map.keySet();
        Iterator<String> iterator = set.iterator();
        String cur = null;
        while(iterator.hasNext()){
            cur = iterator.next();
            if(rooms_map.get(cur)==rid){
                return cur;
            }
        }
        return "";
    }

    private void insertMessage(JScrollPane scrollPane, JTextPane textPane,
                               String icon_code, String title, String[] content){
        StyledDocument document = textPane.getStyledDocument();
        SimpleAttributeSet title_attr = new SimpleAttributeSet();
        StyleConstants.setBold(title_attr, true);
        StyleConstants.setForeground(title_attr, Color.BLUE);
        SimpleAttributeSet content_attr = new SimpleAttributeSet();
        StyleConstants.setBold(content_attr, false);
        StyleConstants.setForeground(content_attr, Color.BLACK);
        Style style = null;
        if(icon_code!=null){
            String path =icon_code+".png";
            ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource(path));
            Image img = icon.getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT);
            icon = new ImageIcon(img);
            style = document.addStyle("icon", null);
            StyleConstants.setIcon(style, icon);
        }

        try {
            document.insertString(document.getLength(), title+"\n", title_attr);
            if(style!=null) {document.insertString(document.getLength(), "\n", style);}
            else {
                for (String content1 : content){
                    document.insertString(document.getLength(), " " + content1 + "\n", content_attr);
                }
            }
        } catch (BadLocationException ex) {
            System.out.println("Bad location exception");
        }
        vertical.setValue(vertical.getMaximum());
    }
    private void insertMessage(JScrollPane scrollPane, JTextPane textPane,
                               String icon_code, String title, String content){
        StyledDocument document = textPane.getStyledDocument();
        SimpleAttributeSet title_attr = new SimpleAttributeSet();
        StyleConstants.setBold(title_attr, true);
        StyleConstants.setForeground(title_attr, Color.BLUE);
        SimpleAttributeSet content_attr = new SimpleAttributeSet();
        StyleConstants.setBold(content_attr, false);
        StyleConstants.setForeground(content_attr, Color.BLACK);
        Style style = null;
        if(icon_code!=null){
            String path =icon_code+".png";
            Icon icon = new ImageIcon(ClassLoader.getSystemResource(path));
            Image img = ((ImageIcon) icon).getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT);
            icon = new ImageIcon(img);
            style = document.addStyle("icon", null);
            StyleConstants.setIcon(style, icon);
        }

        try {
            document.insertString(document.getLength(), title+"\n", title_attr);
            if(style!=null)
                document.insertString(document.getLength(), "\n", style);
            else
                document.insertString(document.getLength(), " "+content+"\n", content_attr);

        } catch (BadLocationException ex) {
            System.out.println("Bad location exception");
        }
        /*设置滑动条到最后*/
        vertical.setValue(vertical.getMaximum());
    }
    public static void setUIFont()
    {
        Font f = new Font("微软雅黑", Font.PLAIN, 14);
        String[] names ={ "Label", "CheckBox", "PopupMenu","MenuItem", "CheckBoxMenuItem",
                "JRadioButtonMenuItem","ComboBox", "Button", "Tree", "ScrollPane",
                "TabbedPane", "EditorPane", "TitledBorder", "Menu", "TextArea","TextPane",
                "OptionPane", "MenuBar", "ToolBar", "ToggleButton", "ToolTip",
                "ProgressBar", "TableHeader", "Panel", "List", "ColorChooser",
                "PasswordField","TextField", "Table", "Label", "Viewport",
                "RadioButtonMenuItem","RadioButton", "DesktopPane", "InternalFrame"
        };
        for (String item : names) {
            UIManager.put(item+ ".font",f);
        }
    }

    public static void setUIStyle(){
        String lookAndFeel =UIManager.getSystemLookAndFeelClassName();
        try {
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }
    public Component getFrame() {
        return frame;
    }

    public void setFrame(Component frame) {
        this.frame = (JFrame) frame;
    }

    public void setLoginFrame(Component frame) {
        this.loginFrame = (JFrame) frame;
    }

    public void setRegisterFrame(Component frame) {
        this.registerFrame = (JFrame) frame;
    }

    public void setChatFrame(Component frame) {
        this.chatFrame = (JFrame) frame;
    }

    public void setRoomFrame(Component frame) {
        this.roomFrame = (JFrame) frame;
    }

    public void setRoomChatFrame(Component frame) {
        this.roomChatFrame = (JFrame) frame;
    }

    public void setRoomCreateFrame(Component frame) {
        this.roomCreateFrame = (JFrame) frame;
    }

    public void setRoomJoinFrame(Component frame) {
        this.roomJoinFrame = (JFrame) frame;
    }


    public void delRoom1(String msg) {
    }
}
