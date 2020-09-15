package com.kag;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;

/**
 * @Description： 服务器
 * @Author: 陈子康
 * @Date: 2020/5/8
 */
public class ParaKServer {

    private JFrame ServerFrame = new JFrame();
    private JPanel BackPanel = (JPanel) ServerFrame.getContentPane();
    private JPanel SettingPanel = new JPanel();
    private JLabel Label_Max = new JLabel("MaxNumber of connect");
    private JLabel Label_Port = new JLabel("Port");
    private JLabel Label_Send = new JLabel("Say");
    private JTextArea Text_Area = new JTextArea();
    private JTextField Text_Message = new JTextField();
    private JTextField Text_Max = new JTextField();
    private JTextField Text_Port = new JTextField();
    private JButton Button_Start = new JButton("Start");
    private JButton Button_Stop = new JButton("Stop");
    private JButton Button_Send = new JButton("Say");
    private JButton Button_SendIcon = new JButton();
    private JScrollPane RightScroll = new JScrollPane();
    private JScrollPane LeftScroll = new JScrollPane();
    private TitledBorder SettingBorder = new TitledBorder("服务器设置：");
    private TitledBorder OnlineBorder = new TitledBorder("在线用户");
    private TitledBorder InformationBorder = new TitledBorder("消息显示");
    private JList UserList = new JList();
    private DefaultListModel ListModel = new DefaultListModel();

    private ServerSocket serverSocket;
    private boolean isStart = false;
    private ServerThread serverThread;
    private ArrayList<ClientThread> ClientList;

    private final ImageIcon BackIcon = new ImageIcon("img/ServerBack.png");
    private final ImageIcon LogoIcon = new ImageIcon("img/QQ-Server.png");
    private final ImageIcon SendIcon = new ImageIcon("img/send.png");

    private final Font Label_Font = new Font("微软雅黑", Font.BOLD, 15);
    private final Font Button_Font = new Font("华文楷体", Font.BOLD, 18);
    private final Font Border_Font = new Font("华文楷体", Font.PLAIN, 15);
    private final Font Text_Font = new Font("微软雅黑", Font.PLAIN, 15);
    private final Font Area_Font = new Font("华文楷体", Font.PLAIN, 18);

    public ParaKServer() {
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
        ServerFrame.getLayeredPane().setLayout(null);
        ServerFrame.getLayeredPane().add(BackLabel, new Integer(Integer.MIN_VALUE));
        /**
         * @Description: 设置Logo
         */
        ServerFrame.setIconImage(LogoIcon.getImage());
        /**
         * @Description: 设置窗体大小
         */
        ServerFrame.setBounds(600, 200, BackIcon.getIconWidth(), BackIcon.getIconHeight());
    }

    /**
     * @Description: 设置窗口属性
     */
    private void initFrameProperty() {
        ServerFrame.setTitle("ParaK-Server");
        ServerFrame.setVisible(true);
        ServerFrame.setResizable(false);
        ServerFrame.setLayout(null);
        ServerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
        ServerFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (isStart) {
                    closeServer(); // 关闭连接
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
         *@Description: 设置上区域
         */
        SettingPanel.setOpaque(false);
        SettingPanel.setLayout(null);
        SettingPanel.setBounds(10, 10, 750, 60);
        SettingBorder.setTitleFont(Border_Font);
        SettingPanel.setBorder(SettingBorder);
        ServerFrame.getLayeredPane().add(SettingPanel);

        ServerFrame.add(Label_Max);
        ServerFrame.add(Label_Port);
        //Label_Max.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        Label_Max.setFont(Label_Font);
        Label_Port.setFont(Label_Font);
        Label_Max.setBounds(40, 30, 200, 30);
        Label_Port.setBounds(350, 30, 80, 30);

        ServerFrame.add(Text_Max);
        ServerFrame.add(Text_Port);
        Text_Max.setFont(Text_Font);
        Text_Port.setFont(Text_Font);
        Text_Max.setOpaque(false);
        Text_Port.setOpaque(false);
        Text_Max.setText("20");
        Text_Port.setText("3333");
        Text_Max.setBounds(240, 30, 80, 30);
        Text_Port.setBounds(400, 30, 80, 30);

        ServerFrame.add(Button_Start);
        ServerFrame.add(Button_Stop);
        Button_Start.setOpaque(false);
        Button_Stop.setOpaque(false);
        Button_Start.setBounds(520, 30, 100, 30);
        Button_Stop.setBounds(620, 30, 100, 30);
        Button_Start.addActionListener(new StartServer());
        Button_Stop.addActionListener(new StopServer());

        /**
         * @Description: 设置中区域
         */

        UserList = new JList(ListModel);
        ServerFrame.add(UserList);
        UserList.setOpaque(false);
        UserList.setBackground(new Color(0, 0, 0, 0));
        UserList.setFont(Text_Font);
        UserList.setBounds(30, 100, 150, 420);
        ServerFrame.add(Text_Area);
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
        ServerFrame.getContentPane().add(LeftScroll);
        ServerFrame.getContentPane().add(RightScroll);
        LeftScroll.setBounds(30, 100, 150, 420);
        RightScroll.setBounds(250, 100, 450, 420);
        OnlineBorder.setTitleFont(Border_Font);
        InformationBorder.setTitleFont(Border_Font);
        LeftScroll.setBorder(OnlineBorder);
        RightScroll.setBorder(InformationBorder);

        /**
         * @Description: 设置下区域
         */
        ServerFrame.add(Label_Send);
        Label_Send.setFont(Label_Font);
        Label_Send.setBounds(30, 560, 50, 30);
        ServerFrame.add(Text_Message);
        Text_Message.setOpaque(false);
        Text_Message.setFont(Text_Font);
        Text_Message.setBounds(80, 560, 530, 30);
        ServerFrame.add(Button_Send);
        Button_Send.setFont(Button_Font);
        Button_Send.setOpaque(false);
        Button_Send.setBounds(630, 560, 80, 30);
        ServerFrame.add(Button_SendIcon);
        Button_SendIcon.setIcon(SendIcon);
        Button_SendIcon.setBounds(710, 560, 30, 30);

        Text_Message.addActionListener(new SendAction());
        Button_Send.addActionListener(new SendAction());
        Button_SendIcon.addActionListener(new SendAction());
    }

    /**
     * @Description: 启动服务器
     * @param max
     * @param port
     * @throws BindException
     */
    public void serverStart(int max, int port) throws BindException {
        try {
            ClientList = new ArrayList<ClientThread>();
            serverSocket = new ServerSocket(port);
            serverThread = new ServerThread(serverSocket, max);
            serverThread.start();
            isStart = true;
        } catch (BindException e) {
            isStart = false;
            JOptionPane.showMessageDialog(ServerFrame, "端口号已被占用，请换一个！", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e1) {
            e1.printStackTrace();
            isStart = false;
            JOptionPane.showMessageDialog(ServerFrame, "启动服务器异常！", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * @Description: 关闭服务器
     */
    private void closeServer() {
        try {
            if (serverThread != null)
                serverThread.stop();// 停止服务器线程

            for (int i = ClientList.size() - 1; i >= 0; i--) {
                // 给所有在线用户发送关闭命令
                ClientList.get(i).getWriter().println("CLOSE");
                ClientList.get(i).getWriter().flush();
                // 释放资源
                ClientList.get(i).stop();// 停止此条为客户端服务的线程
                ClientList.get(i).reader.close();
                ClientList.get(i).writer.close();
                ClientList.get(i).socket.close();
                ClientList.remove(i);
            }
            if (serverSocket != null) {
                serverSocket.close();// 关闭服务器端连接
            }
            ListModel.removeAllElements();// 清空用户列表
            isStart = false;
        } catch (IOException e) {
            e.printStackTrace();
            isStart = true;
        }
    }

    /**
     * @Description:  群发服务器消息
     */
    private void sendServerMessage(String message) {
        for (int i = ClientList.size() - 1; i >= 0; i--) {
            ClientList.get(i).getWriter().println(Calendar.getInstance().getTime().toLocaleString() + " " + "服务器：" + message);
            ClientList.get(i).getWriter().flush();
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

    class SendAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!isStart) {
                JOptionPane.showMessageDialog(ServerFrame, "服务器还未启动,不能发送消息！", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (ClientList.size() == 0) {
                JOptionPane.showMessageDialog(ServerFrame, "没有用户在线,不能发送消息！", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String message = Text_Message.getText().trim();
            if (message == null || message.equals("")) {
                JOptionPane.showMessageDialog(ServerFrame, "消息不能为空！", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            sendServerMessage(message); // 群发服务器消息
            Text_Area.append(Calendar.getInstance().getTime().toLocaleString() + " " + "服务器：" + Text_Message.getText() + "\r\n");
            Text_Message.setText(null);
        }
    }

    class StartServer implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (isStart) {
                JOptionPane.showMessageDialog(ServerFrame, "服务器已处于启动状态，不要重复启动！",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int max;
            int port;
            try {
                try {
                    max = Integer.parseInt(Text_Max.getText());
                } catch (Exception e1) {
                    throw new Exception("人数上限为正整数！");
                }
                if (max <= 0) {
                    throw new Exception("人数上限为正整数！");
                }
                try {
                    port = Integer.parseInt(Text_Port.getText());
                } catch (Exception e1) {
                    throw new Exception("端口号为正整数！");
                }
                if (port <= 0) {
                    throw new Exception("端口号为正整数！");
                }
                serverStart(max, port);
                Text_Area.append(Calendar.getInstance().getTime().toLocaleString() + " " + "服务器已成功启动!  人数上限：" + max + ", 端口：" + port + "\r\n");
                JOptionPane.showMessageDialog(ServerFrame, "服务器成功启动!");
                Button_Start.setEnabled(false);
                Text_Max.setEnabled(false);
                Text_Port.setEnabled(false);
                Button_Stop.setEnabled(true);
            } catch (Exception exc) {
                JOptionPane.showMessageDialog(ServerFrame, exc.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    class StopServer implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!isStart) {
                JOptionPane.showMessageDialog(ServerFrame, "服务器还未启动，无需停止！", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                closeServer();
                Button_Start.setEnabled(true);
                Text_Max.setEnabled(true);
                Text_Port.setEnabled(true);
                Button_Stop.setEnabled(false);
                Text_Area.append(Calendar.getInstance().getTime().toLocaleString() + " " + "服务器成功停止...\r\n");
                JOptionPane.showMessageDialog(ServerFrame, "服务器成功停止！");
            } catch (Exception exc) {
                JOptionPane.showMessageDialog(ServerFrame, "停止服务器发生异常！", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * @Description: 服务器线程
     */
    class ServerThread extends Thread {
        private ServerSocket serverSocket;
        private int max;// 人数上限

        // 服务器线程的构造方法
        public ServerThread(ServerSocket serverSocket, int max) {
            this.serverSocket = serverSocket;
            this.max = max;
        }

        public void run() {
            while (true) { // 不停的等待客户端的链接
                try {
                    Socket socket = serverSocket.accept();
                    if (ClientList.size() == max) { // 如果已达人数上限
                        BufferedReader r = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter w = new PrintWriter(socket.getOutputStream());
                        // 接收客户端的基本用户信息
                        String inf = r.readLine();
                        StringTokenizer st = new StringTokenizer(inf, "@");
                        User user = new User(st.nextToken(), st.nextToken());
                        // 反馈连接成功信息
                        w.println("MAX@服务器：对不起，" + user.getName() + "[IP" +user.getIp() + "]，服务器在线人数已达上限，请稍后尝试连接！");
                        w.flush();
                        // 释放资源
                        r.close();
                        w.close();
                        socket.close();
                        continue;
                    }
                    ClientThread client = new ClientThread(socket);
                    client.start();// 开启对此客户端服务的线程
                    ClientList.add(client);
                    ListModel.addElement(client.getUser().getName());// 更新在线列表
                    Text_Area.append(Calendar.getInstance().getTime().toLocaleString() + " " + client.getUser().getName() + "[IP" +client.getUser().getIp() + "]" + "上线!\r\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 为一个客户端服务的线程
    class ClientThread extends Thread {
        private Socket socket;
        private BufferedReader reader;
        private PrintWriter writer;
        private User user;

        public BufferedReader getReader() {
            return reader;
        }

        public PrintWriter getWriter() {
            return writer;
        }

        public User getUser() {
            return user;
        }

        // 客户端线程的构造方法
        public ClientThread(Socket socket) {
            try {
                this.socket = socket;
                reader = new BufferedReader(new InputStreamReader(socket
                        .getInputStream()));
                writer = new PrintWriter(socket.getOutputStream());
                // 接收客户端的基本用户信息
                String inf = reader.readLine();
                StringTokenizer st = new StringTokenizer(inf, "@");
                user = new User(st.nextToken(), st.nextToken());
                // 反馈连接成功信息
                writer.println(Calendar.getInstance().getTime().toLocaleString() + " " + user.getName() + "[IP" + user.getIp() + "] 与服务器连接成功!");
                writer.flush();
                // 反馈当前在线用户信息
                if (ClientList.size() > 0) {
                    String temp = "";
                    for (int i = ClientList.size() - 1; i >= 0; i--) {
                        temp += (ClientList.get(i).getUser().getName()  + ClientList.get(i).getUser().getIp())  +"@";
                    }
                    writer.println("USERLIST@" + ClientList.size() + "@" + temp);
                    writer.flush();
                }
                // 向所有在线用户发送该用户上线命令
                for (int i = ClientList.size() - 1; i >= 0; i--) {
                    ClientList.get(i).getWriter().println(
                            "ADD@" + user.getName() + user.getIp());
                    ClientList.get(i).getWriter().flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @SuppressWarnings("deprecation")
        public void run() {// 不断接收客户端的消息，进行处理。
            String message = null;
            while (true) {
                try {
                    message = reader.readLine();// 接收客户端消息
                    if (message.equals("CLOSE"))// 下线命令
                    {
                        Text_Area.append(Calendar.getInstance().getTime().toLocaleString() + " " + this.getUser().getName() + "[IP" + this.getUser().getIp() + "]" + "下线!\r\n");
                        // 断开连接释放资源
                        reader.close();
                        writer.close();
                        socket.close();

                        // 向所有在线用户发送该用户的下线命令
                        for (int i = ClientList.size() - 1; i >= 0; i--) {
                            ClientList.get(i).getWriter().println("DELETE@" + user.getName());
                            ClientList.get(i).getWriter().flush();
                        }

                        ListModel.removeElement(user.getName());// 更新在线列表

                        // 删除此条客户端服务线程
                        for (int i = ClientList.size() - 1; i >= 0; i--) {
                            if (ClientList.get(i).getUser() == user) {
                                ClientThread temp = ClientList.get(i);
                                ClientList.remove(i); // 删除此用户的服务线程
                                temp.stop(); // 停止这条服务线程
                                return;
                            }
                        }
                    } else {
                        dispatcherMessage(message);// 转发消息
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // 转发消息
        public void dispatcherMessage(String message) {
            StringTokenizer stringTokenizer = new StringTokenizer(message, "@");
            String source = stringTokenizer.nextToken();
            String owner = stringTokenizer.nextToken();
            String content = stringTokenizer.nextToken();
            message = source + "：" + content;
            Text_Area.append(Calendar.getInstance().getTime().toLocaleString() + " " + message + "\r\n");
            if (owner.equals("ALL")) { // 群发
                for (int i = ClientList.size() - 1; i >= 0; i--) {
                    ClientList.get(i).getWriter().println(Calendar.getInstance().getTime().toLocaleString() + " " + message);
                    ClientList.get(i).getWriter().flush();
                }
            }
        }
    }

}
