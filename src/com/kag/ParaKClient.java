package com.kag;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @Description： 客户端
 * @Author: 陈子康
 * @Date: 2020/5/8
 */
public class ParaKClient extends JFrame {

    private JPanel BackPanel = (JPanel) getContentPane();
    private JPanel SettingPanel = new JPanel();
    private JLabel Label_HostIP = new JLabel("Server IP");
    private JLabel Label_Port = new JLabel("Server Port");
    private JLabel Label_Name = new JLabel("Name");
    private JLabel Label_Send = new JLabel("Say");
    private JTextField Text_HostIP = new JTextField("127.0.0.1");
    private JTextField Text_Port = new JTextField("3333");
    private JTextField Text_Name = new JTextField("ParaK");
    private JButton Button_Connect = new JButton("Connect");
    private JButton Button_Disconnect = new JButton("Disconnect");
    private JButton Button_Send = new JButton("Say");
    private JButton Button_SendIcon = new JButton();
    private JList UserList = new JList();
    private JTextArea Text_Area = new JTextArea();
    private JTextField Text_Send = new JTextField();
    private JScrollPane LeftScroll = new JScrollPane();
    private JScrollPane RightScroll = new JScrollPane();
    private TitledBorder SettingBorder = new TitledBorder("客户机设置：");
    private TitledBorder OnlineBorder = new TitledBorder("在线用户");
    private TitledBorder InformationBorder = new TitledBorder("消息显示");

    private boolean isConnected = false;
    private Socket socket;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;
    private MessageThread messageThread;
    private DefaultListModel ListModel = new DefaultListModel();
    private Map<String, User> onLineUsers = new HashMap<>();

    private final ImageIcon BackIcon = new ImageIcon("img/ClientBack.png");
    private final ImageIcon LogoIcon = new ImageIcon("img/QQ-Client.png");
    private final ImageIcon SendIcon = new ImageIcon("img/send.png");

    private final Font Label_Font = new Font("微软雅黑", Font.BOLD, 15);
    private final Font Button_Font = new Font("华文楷体", Font.BOLD, 18);
    private final Font Border_Font = new Font("华文楷体", Font.PLAIN, 15);
    private final Font Text_Font = new Font("微软雅黑", Font.PLAIN, 15);
    private final Font Area_Font = new Font("华文楷体", Font.PLAIN, 18);

    public ParaKClient() {
        super();
        initFrameBackground();
        initFrameProperty();
        initFrameComponent();
    }

    /**
     * @Description: 设置窗口背景
     */
    private void initFrameBackground() {
        /**
         * @Description: 设置背景
         */
        JLabel BackLabel = new JLabel(BackIcon);
        BackLabel.setBounds(0, 0, BackIcon.getIconWidth(),BackIcon.getIconHeight());
        BackPanel.setOpaque(false);
        BackPanel.setLayout(new FlowLayout());
        getLayeredPane().setLayout(null);
        getLayeredPane().add(BackLabel, new Integer(Integer.MIN_VALUE));
        /**
         * @Description: 设置Logo
         */
        setIconImage(LogoIcon.getImage());
        /**
         * @Description: 设置窗体大小
         */
        setBounds(600, 200, BackIcon.getIconWidth(), BackIcon.getIconHeight());
    }

    /**
     * @Description: 设置窗口属性
     */
    private void initFrameProperty() {
        setTitle("ParaK-Client");
        setVisible(true);
        setResizable(false);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (ClassNotFoundException e1) {
            printException(e1);
        } catch (UnsupportedLookAndFeelException e2) {
            printException(e2);
        } catch (InstantiationException e3) {
            printException(e3);
        } catch (IllegalAccessException e4) {
            printException(e4);
        }
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (isConnected) {
                    closeConnection(); // 关闭连接
                }
                System.exit(0); // 退出程序
            }
        });
    }

    /**
     * @Description: 设置窗口组件
     */
    private void initFrameComponent() {

        /**
         * @Description: 设置上区域
         */
        SettingPanel.setOpaque(false);
        SettingPanel.setLayout(null);
        SettingPanel.setBounds(10, 10, 750, 60);
        SettingBorder.setTitleFont(Border_Font);
        SettingPanel.setBorder(SettingBorder);
        getLayeredPane().add(SettingPanel);

        add(Label_HostIP);
        add(Label_Port);
        add(Label_Name);
        Label_HostIP.setFont(Label_Font);
        Label_Port.setFont(Label_Font);
        Label_Name.setFont(Label_Font);
        Label_HostIP.setBounds(20, 30, 70, 30);
        Label_Port.setBounds(200, 30, 90, 30);
        Label_Name.setBounds(400, 30, 80, 30);

        add(Text_HostIP);
        add(Text_Port);
        add(Text_Name);
        Text_HostIP.setFont(Text_Font);
        Text_Port.setFont(Text_Font);
        Text_Name.setFont(Text_Font);
        Text_HostIP.setOpaque(false);
        Text_Port.setOpaque(false);
        Text_Name.setOpaque(false);
        Text_HostIP.setBounds(95, 30, 80, 30);
        Text_Port.setBounds(290, 30, 80, 30);
        Text_Name.setBounds(455, 30, 80, 30);

        add(Button_Connect);
        add(Button_Disconnect);
        Button_Connect.setOpaque(false);
        Button_Disconnect.setOpaque(false);
        Button_Connect.setBounds(550, 30, 100, 30);
        Button_Disconnect.setBounds(650, 30, 100, 30);
        Button_Connect.addActionListener(new ConnectToServerAction());
        Button_Disconnect.addActionListener(new DisconnectToServerAction());

        /**
         * @Description: 设置中区域
         */
        UserList = new JList(ListModel);
        add(UserList);
        UserList.setOpaque(false);
        UserList.setBackground(new Color(0,0,0, 0));
        UserList.setFont(Text_Font);
        UserList.setBounds(30, 100, 150, 420);
        add(Text_Area);
        Text_Area.setEditable(false);
        Text_Area.setOpaque(false);
        Text_Area.setFont(Text_Font);
        Text_Area.setBounds(250, 100, 450, 420);
        LeftScroll = new JScrollPane(UserList);
        RightScroll = new JScrollPane(Text_Area);
        LeftScroll.setOpaque(false);
        RightScroll.setOpaque(false);
        LeftScroll.getViewport().setOpaque(false);
        RightScroll.getViewport().setOpaque(false);
        getContentPane().add(LeftScroll);
        getContentPane().add(RightScroll);
        LeftScroll.setBounds(30, 100, 150, 420);
        RightScroll.setBounds(250, 100, 450, 420);
        OnlineBorder.setTitleFont(Border_Font);
        InformationBorder.setTitleFont(Border_Font);
        LeftScroll.setBorder(OnlineBorder);
        RightScroll.setBorder(InformationBorder);

        /**
         * @Description: 设置下区域
         */
        add(Label_Send);
        Label_Send.setFont(Label_Font);
        Label_Send.setBounds(30, 560, 50, 30);
        add(Text_Send);
        Text_Send.setOpaque(false);
        Text_Send.setFont(Text_Font);
        Text_Send.setBounds(80, 560, 530, 30);
        add(Button_Send);
        Button_Send.setFont(Button_Font);
        Button_Send.setOpaque(false);
        Button_Send.setBounds(630, 560, 80, 30);
        add(Button_SendIcon);
        Button_SendIcon.setIcon(SendIcon);
        Button_SendIcon.setBounds(710, 560, 30, 30);

        Text_Send.addActionListener(new SendAction());
        Button_Send.addActionListener(new SendAction());
        Button_SendIcon.addActionListener(new SendAction());
    }

    /**
     * @Description: 单击Connect按钮事件
     */
    class ConnectToServerAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int port = 3333;
            if (isConnected) {
                JOptionPane.showMessageDialog(null, "以连接至服务器，请勿重复连接！", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                try {
                    port = Integer.parseInt(Text_Port.getText().trim());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "端口号不符号要求！", "Error", JOptionPane.ERROR_MESSAGE);
                }
                String hostIP = Text_HostIP.getText().trim();
                String name = Text_Name.getText();
                if (hostIP.equals("")) {
                    JOptionPane.showMessageDialog(null, "服务器IP不能为空！", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (name.equals("")) {
                    JOptionPane.showMessageDialog(null, "用户名不能为空！", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                boolean flag = connectToServer(port, hostIP, name);
                if (flag == false) {
                    JOptionPane.showMessageDialog(null, "连接失败！", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    setTitle(name);
                    JOptionPane.showMessageDialog(null, "连接成功！", "Success", JOptionPane.YES_NO_CANCEL_OPTION);
                }
            } catch (Exception exc) {
                JOptionPane.showMessageDialog(null, exc.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    class DisconnectToServerAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!isConnected) {
                JOptionPane.showMessageDialog(null, "已处于离线状态，请勿重复断开！", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                boolean flag = closeConnection();
                if (flag == false) {
                    JOptionPane.showMessageDialog(null, "断开连接发生异常！", "Warning", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "成功与服务器断开连接！", "Success", JOptionPane.YES_NO_CANCEL_OPTION);
                }
            } catch (Exception exc) {
                JOptionPane.showMessageDialog(null, exc.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    /**
     * @Description: 在消息框中按下Enter事件
     */
    class SendAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            /**
             * @Description: 发送消息
             */
            if (!isConnected) {
                JOptionPane.showMessageDialog(null, "无服务器连接，无法发送消息！",  "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String message = Text_Send.getText().trim();  //trim()可以字符串去除空格
            if (message == null || message.equals("")) {
                JOptionPane.showMessageDialog(null, "消息不能为空！",  "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String user_message = Text_Name.getText() + "@" + "ALL" +"@" + message;
            sendMessage(user_message);
            Text_Send.setText(null);
        }
    }

    /**
     * @Description: 发送消息
     * @param message
     */
    private void sendMessage(String message) {
        printWriter.println(message);
        printWriter.flush();
    }

    /**
     * @Description： 客户端连接到服务器
     * @param port
     * @param hostIP
     * @param name
     * @return
     */
    private boolean connectToServer(int port, String hostIP, String name) {
        try {
            socket = new Socket(hostIP, port);
            printWriter = new PrintWriter(socket.getOutputStream());
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            sendMessage(name + "@" + socket.getLocalAddress().toString());
            messageThread = new MessageThread(bufferedReader, Text_Area);
            messageThread.start();
            isConnected = true;  //连接成功
            return true;
        } catch (Exception e) {
            Text_Area.setText(Calendar.getInstance().getTime().toLocaleString() + " 连接服务器失败！");
            return false;
        }
    }

    /**
     * @Description: 客户端主动关闭连接
     */
    private synchronized boolean closeConnection() {
        try {
            sendMessage("CLOSE");  //发送断开连接命令给服务器
            messageThread.stop();  //停止接受消息线程
            //释放资源
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (printWriter != null) {
                printWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
            isConnected = false;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            isConnected = true;
            return false;
        }
    }

    /**
     * @Description: 统一异常日志
     * @param Exception
     */
    public static void printException(Exception e) {
        e.printStackTrace();
        System.out.println("[Time] " + Calendar.getInstance().getTime().toString());
        System.out.println("[Cause] " +  e.getCause());
        System.out.println("[Message] " +  e.getMessage());
    }

    /**
     * @Description: 接收消息的线程
     */
    class MessageThread extends Thread {
        private BufferedReader bufferReader;
        private JTextArea Text_Area;

        // 接收消息线程的构造方法
        public MessageThread(BufferedReader reader, JTextArea textArea) {
            this.bufferReader = reader;
            this.Text_Area = textArea;
        }

        // 被动的关闭连接
        public synchronized void closeCon() throws Exception {
            // 清空用户列表
            ListModel.removeAllElements();
            // 被动的关闭连接释放资源
            if (bufferReader != null) {
                bufferReader.close();
            }
            if (printWriter != null) {
                printWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
            isConnected = false;// 修改状态为断开
        }

        public void run() {
            String message = "";
            while (true) {
                try {
                    message = bufferReader.readLine();
                    StringTokenizer stringTokenizer = new StringTokenizer(message, "/@");
                    String command = stringTokenizer.nextToken(); // 命令
                    if (command.equals("CLOSE"))     // 服务器已关闭命令
                    {
                        Text_Area.append(Calendar.getInstance().getTime().toLocaleString() + " " + "服务器已关闭...\r\n");
                        closeCon();// 被动的关闭连接
                        return;// 结束线程
                    } else if (command.equals("ADD")) { // 有用户上线更新在线列表
                        String username = "";
                        String userIp = "";
                        if ((username = stringTokenizer.nextToken()) != null && (userIp = stringTokenizer.nextToken()) != null) {
                            User user = new User(username, userIp);
                            onLineUsers.put(username, user);
                            ListModel.addElement(username);
                        }
                    } else if (command.equals("DELETE")) { // 有用户下线更新在线列表
                        String username = stringTokenizer.nextToken();
                        User user = (User) onLineUsers.get(username);
                        onLineUsers.remove(user);
                        ListModel.removeElement(username);
                    } else if (command.equals("USERLIST")) { // 加载在线用户列表
                        int size = Integer.parseInt(stringTokenizer.nextToken());
                        String username = null;
                        String userIp = null;
                        for (int i = 0; i < size; i++) {
                            username = stringTokenizer.nextToken();
                            userIp = stringTokenizer.nextToken();
                            User user = new User(username, userIp);
                            onLineUsers.put(username, user);
                            ListModel.addElement(username);
                        }
                    } else if (command.equals("MAX")) { // 人数已达上限
                        Text_Area.append(stringTokenizer.nextToken() + stringTokenizer.nextToken() + "\r\n");
                        closeCon(); // 被动的关闭连接
                        JOptionPane.showMessageDialog(null, "服务器缓冲区已满！", "Error", JOptionPane.ERROR_MESSAGE);
                        return; // 结束线程
                    } else { // 普通消息
                        Text_Area.append(message + "\r\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
