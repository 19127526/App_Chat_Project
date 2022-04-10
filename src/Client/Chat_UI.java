package Client;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
/**
 * Client
 * Created by Admin
 * Date 1/11/2022 - 12:26 AM
 * Description: ...
 */
public class Chat_UI extends JFrame {
    private JButton Button_Upload=new JButton(), Button_Send=new JButton(), Button_Down=new JButton();
    private JTextField message=new JTextField("");
    private HashMap<String,byte[]> file =new HashMap<String,byte[]>();
    private JPanel Frame;
    private DefaultListModel<String> tempp = new DefaultListModel<>();
    private JComboBox<String> user_online = new JComboBox<String>();
    private JScrollPane jScrollPane;
    private DataInputStream dis;
    private DataOutputStream dos;
    private JLabel user_receiver = new JLabel(" ");
    private HashMap<String, JTextPane> Users_chat_window = new HashMap<String, JTextPane>();
    private JList<String> User_Onlines = new JList<String>();
    private String value ="";
    private Thread Middleware;
    private JTextPane WinChat;
    private String username;

    public Chat_UI(String username, DataInputStream dis, DataOutputStream dos) {
        this.username = username;
        this.dis = dis;
        this.dos = dos;
        Middleware = new Thread(new MiddleWare(this.dis));
        Middleware.start();

        Frame =new JPanel();
        this.setContentPane(Frame);

        BoxLayout box = new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS);
        this.setLayout(box);

        JPanel tittle_Panel = new JPanel();
        JLabel title = new JLabel("Chat Client");
        title.setFont(new Font("Serif", Font.PLAIN, 70));
        tittle_Panel.add(title);

        Frame.add(tittle_Panel);


        JPanel Name_Panel = new JPanel();
        BoxLayout box_info = new BoxLayout(Name_Panel, BoxLayout.X_AXIS);
        Name_Panel.setLayout(box_info);
        JLabel u_user = new JLabel("Your Username: " + username);
        u_user.setFont(new Font("Serif", Font.PLAIN, 15));
        JLabel user_other = new JLabel("Online Usernames: ");
        user_other.setFont(new Font("Serif", Font.PLAIN, 15));
        user_online.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    user_receiver.setText((String) user_online.getSelectedItem());
                    if (WinChat != Users_chat_window.get(user_receiver.getText())) {
                        WinChat = Users_chat_window.get(user_receiver.getText());
                        WinChat.setBackground(Color.BLACK);
                        WinChat.setPreferredSize(new Dimension(0,200));
                        jScrollPane.setViewportView(WinChat);
                        jScrollPane.validate();
                        message.setText("");
                        if (!user_receiver.getText().equals(" ")){
                            if (!value.equals(user_receiver.getText()))
                                try {
                                    dos.writeUTF("//chatwithuser" + username + "//" + user_receiver.getText());
                                    value = user_receiver.getText();
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                        }
                    }
                    if (user_receiver.getText().isBlank()) {
                        Button_Upload.setEnabled(false);
                        Button_Down.setEnabled(false);
                        Button_Send.setEnabled(false);
                        message.setEnabled(false);
                    } else {
                        Button_Upload.setEnabled(true);
                        Button_Down.setEnabled(true);
                        Button_Send.setEnabled(true);
                        message.setEnabled(true);
                    }
                }

            }
        });
        Name_Panel.add(u_user);
        Name_Panel.add(Box.createRigidArea(new Dimension(100, 0)));
        Name_Panel.add(user_other);
        Name_Panel.add(user_online);
        Name_Panel.add(User_Onlines);
        Name_Panel.add(Box.createRigidArea(new Dimension(10, 0)));


        Frame.add(Name_Panel);




        JPanel chat_panel=new JPanel();
        BoxLayout box_mid = new BoxLayout(chat_panel, BoxLayout.Y_AXIS);
        chat_panel.setLayout(box_mid);
        jScrollPane = new JScrollPane();
        jScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        JPanel usernamePanel = new JPanel();
        jScrollPane.setColumnHeaderView(usernamePanel);
        user_receiver.setText(" ");
        user_receiver.setFont(new Font("Serif", Font.BOLD, 20));
        usernamePanel.add(user_receiver);
        JTextPane textPane=new JTextPane();
        Setup_window(textPane);
        Users_chat_window.put(" ", textPane);
        WinChat = Users_chat_window.get(" ");
        WinChat.setFont(new Font("Serif", Font.PLAIN, 14));
        WinChat.setEditable(false);
        WinChat.setBackground(Color.BLACK);
        WinChat.setPreferredSize(new Dimension(0,180));
        jScrollPane.setViewportView(WinChat);
        chat_panel.add(jScrollPane);


        Frame.add(chat_panel);


        JPanel end_panel = new JPanel();
        BoxLayout box_button = new BoxLayout(end_panel, BoxLayout.X_AXIS);
        end_panel.setLayout(box_button);

        JLabel Message = new JLabel("Message");
        Message.setFont(new Font("Serif", Font.PLAIN, 15));

        message = new JTextField("");
        message.setFont(new Font("Serif", Font.PLAIN, 15));
        message.setMaximumSize(new Dimension(300,50));
        message.setEnabled(false);
        message.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if ( user_receiver.getText().isBlank()||message.getText().isBlank()) {
                    Button_Send.setEnabled(false);
                } else {
                    Button_Send.setEnabled(true);
                }
            }
        });

        Button_Send = new JButton("Send");
        Button_Send.setFocusable(false);
        Button_Send.setFont(new Font("Serif", Font.PLAIN, 15));
        Button_Send.setEnabled(false);
        Button_Send.setAlignmentX(CENTER_ALIGNMENT);
        Button_Send.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    dos.writeUTF("//message");
                    dos.writeUTF(user_receiver.getText());
                    dos.writeUTF(message.getText());
                    dos.flush();
                } catch (IOException e1) {
                    e1.printStackTrace();
                    Setup_mess("error", "Network error!", true);
                }
                Setup_mess(username, message.getText(), true);
                message.setText("");
            }
        });

        Button_Upload = new JButton("Upload");
        Button_Upload.setFocusable(false);
        Button_Upload.setFont(new Font("Serif", Font.PLAIN, 15));
        Button_Upload.setEnabled(false);
        Button_Upload.setAlignmentX(CENTER_ALIGNMENT);
        Button_Upload.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                int value = chooser.showOpenDialog(Frame.getParent());
                if (value == JFileChooser.APPROVE_OPTION) {
                    BufferedInputStream inputStream;
                    byte[] bytes = new byte[(int) chooser.getSelectedFile().length()];
                    try {
                        inputStream = new BufferedInputStream(new FileInputStream(chooser.getSelectedFile()));
                        inputStream.read(bytes, 0, bytes.length);
                        dos.writeUTF("//uploadfile");
                        dos.writeUTF(user_receiver.getText());
                        dos.writeUTF(chooser.getSelectedFile().getName());
                        dos.writeUTF(String.valueOf(bytes.length));
                        int offset = 0;
                        int length = bytes.length;
                        int bufferSize = 2048;
                        while (length > 0) {
                            dos.write(bytes, offset, Math.min(length, bufferSize));
                            offset += Math.min(length, bufferSize);
                            length -= bufferSize;
                        }
                        dos.flush();
                        inputStream.close();
                        JOptionPane.showMessageDialog(null, "Upload File to Server Success");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });


        Button_Down = new JButton("Download");
        Button_Down.setAlignmentX(CENTER_ALIGNMENT);
        Button_Down.setFocusable(false);
        Button_Down.setFont(new Font("Serif", Font.PLAIN, 15));
        Button_Down.setEnabled(false);
        Button_Down.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String temp_="";
                for (Map.Entry entry : file.entrySet()) {
                    temp_+=(String)entry.getKey()+",";
                }
                String dialog=JOptionPane.showInputDialog(null,"Enter file: "+temp_);
                JFileChooser chooser = new JFileChooser();
                chooser.setSelectedFile(new File(dialog));
                chooser.setDialogTitle("Choose File");
                int userSelection = chooser.showSaveDialog(Frame);
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File Name_File_Save = chooser.getSelectedFile();
                    for (Map.Entry mapElement1 : file.entrySet()) {
                        if (dialog.equals(mapElement1.getKey())){
                            BufferedOutputStream outputStream = null;
                            byte[] bytes= file.get(mapElement1.getKey());
                            try {
                                outputStream = new BufferedOutputStream(new FileOutputStream(Name_File_Save));
                            } catch (FileNotFoundException e1) {
                                e1.printStackTrace();
                            }
                            JOptionPane.showMessageDialog(null, "Download File Success!!, File is in " + Name_File_Save.getAbsolutePath());
                            int successful = JOptionPane.showConfirmDialog(null, "Open File ?", "Download File Success", JOptionPane.YES_NO_OPTION);
                            if (successful == JOptionPane.YES_OPTION) {
                                try {
                                    Desktop.getDesktop().open(Name_File_Save);
                                } catch (IOException e2) {
                                    e2.printStackTrace();
                                }
                            }
                            if (outputStream != null) {
                                try {
                                    outputStream.write(bytes);
                                    outputStream.close();
                                } catch (IOException e3) {
                                    e3.printStackTrace();
                                }
                            }
                        }
                    }

                }
            }
        });

        end_panel.add(Message);
        end_panel.add(Box.createRigidArea(new Dimension(5, 0)));
        end_panel.add(message);
        end_panel.add(Box.createRigidArea(new Dimension(10, 0)));
        end_panel.add(Button_Send);
        end_panel.add(Box.createRigidArea(new Dimension(10, 0)));
        end_panel.add(Button_Upload);
        end_panel.add(Box.createRigidArea(new Dimension(10, 0)));
        end_panel.add(Button_Down);
        Frame.add(Box.createRigidArea(new Dimension(0, 20)));
        Frame.add(end_panel);

        JPanel logoutPannel=new JPanel();
        JButton LogOut_Button = new JButton("Log out");
        LogOut_Button.setFont(new Font("Serif", Font.PLAIN, 15));
        JFrame logTemp = this;
        LogOut_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    dos.writeUTF("//exit");
                    dos.flush();
                    try {
                        Middleware.join();
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    if (dos != null) {
                        dos.close();
                    }
                    if (dis != null) {
                        dis.close();
                    }
                    Login_UI.GUI();
                    logTemp.dispose();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        logoutPannel.add(LogOut_Button);
        Frame.add(logoutPannel);
        Frame.add(Box.createRigidArea(new Dimension(0,200)));


        this.setSize(new Dimension(650, 700));
        this.setTitle("Chat App");
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
        this.getRootPane().setDefaultButton(Button_Send);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                try {
                    dos.writeUTF("//exit");
                    dos.flush();
                    try {
                        Middleware.join();
                    } catch (InterruptedException exception) {
                        exception.printStackTrace();
                    }
                    if (dos != null) {
                        dos.close();
                    }
                    if (dis != null) {
                        dis.close();
                    }
                } catch (IOException exception) {
                    exception.printStackTrace();
                }

            }
        });
    }
    private void Setup_window(JTextPane j) {
        StyledDocument jtpStyledDocument = j.getStyledDocument();
        Style addStyle = j.addStyle("I'm a Style", null);
        StyleConstants.setForeground(addStyle, new Color(176, 176, 176));
        StyleConstants.setBold(addStyle, true);
        StyleConstants.setFontSize(addStyle, 30);
        StyleConstants.setAlignment(addStyle, StyleConstants.ALIGN_CENTER);
        try {
            jtpStyledDocument.insertString(jtpStyledDocument.getLength(), " Welcome "+username, addStyle);
            jtpStyledDocument.setParagraphAttributes(jtpStyledDocument.getLength(), 1, addStyle, false);
        } catch (BadLocationException exception) {
            exception.printStackTrace();
        }
    }
    private void Setup_mess(String username, String message, Boolean yourMessage) {
        StyledDocument styledDocument;
        if (username.equals(this.username)) {
            styledDocument = Users_chat_window.get(user_receiver.getText()).getStyledDocument();
        } else {
            styledDocument = Users_chat_window.get(username).getStyledDocument();
        }

        Style style = styledDocument.getStyle("User style");
        if (style == null) {
            style = styledDocument.addStyle("User style", null);
            StyleConstants.setBold(style, true);
        }
        if (yourMessage == true) {
            StyleConstants.setAlignment(style, StyleConstants.ALIGN_LEFT);
        } else {
            StyleConstants.setAlignment(style, StyleConstants.ALIGN_RIGHT);
        }
        StyleConstants.setForeground(style, Color.white);
        try { styledDocument.insertString(styledDocument.getLength(), username + ": ", style);
            styledDocument.setParagraphAttributes(styledDocument.getLength(), 1, style, false); }
        catch (BadLocationException e){}

        Style documentStyle = styledDocument.getStyle("Message style");
        if (documentStyle == null) {
            documentStyle = styledDocument.addStyle("Message style", null);
            StyleConstants.setForeground(documentStyle, Color.WHITE);
            StyleConstants.setBold(documentStyle, false);
        }
        try { styledDocument.insertString(styledDocument.getLength(), message + "\n",documentStyle); }
        catch (BadLocationException exception){
            exception.printStackTrace();
        }
    }
    class MiddleWare implements Runnable {
        DataInputStream dis;
        public MiddleWare(DataInputStream dis) {
            this.dis = dis;
        }
        public void run() {
            try {
                while (true) {
                    String line = dis.readUTF();
                    if (line.equals("//message")) {
                        String line1 = dis.readUTF();
                        String mess = dis.readUTF();
                        Setup_mess(line1, mess, false);
                    } else if (line.equals("//uploadfile")) {
                        String temp = dis.readUTF();
                        String name_file = dis.readUTF();
                        int size = Integer.parseInt(dis.readUTF());
                        int bufferSize = 2048;
                        byte[] bytes1 = new byte[bufferSize];
                        ByteArrayOutputStream file = new ByteArrayOutputStream();
                        while (size > 0) {
                            dis.read(bytes1, 0, Math.min(bufferSize, size));
                            file.write(bytes1, 0, Math.min(bufferSize, size));
                            size -= bufferSize;
                        }
                        Chat_UI.this.file.put(name_file,file.toByteArray());
                        JFileChooser chooser = new JFileChooser();
                        chooser.setSelectedFile(new File(name_file));
                        chooser.setDialogTitle("Specify a file to save");
                        int dialog = chooser.showSaveDialog(Frame);
                        if (dialog == JFileChooser.APPROVE_OPTION) {
                            File Name_File_Save = chooser.getSelectedFile();
                            for (Map.Entry entry : Chat_UI.this.file.entrySet()) {
                                if (name_file.equals(entry.getKey())){
                                    byte[] bytes= Chat_UI.this.file.get(entry.getKey());
                                    BufferedOutputStream outputStream = null;
                                    try {
                                        outputStream = new BufferedOutputStream(new FileOutputStream(Name_File_Save));
                                    } catch (FileNotFoundException exception) {
                                        exception.printStackTrace();
                                    }
                                    JOptionPane.showMessageDialog(null, "Download File Success!!, File is in " + Name_File_Save.getAbsolutePath());
                                    int success = JOptionPane.showConfirmDialog(null, "Open File ?", "Download File Success", JOptionPane.YES_NO_OPTION);
                                    if (success == JOptionPane.YES_OPTION) {
                                        try {
                                            Desktop.getDesktop().open(Name_File_Save);
                                        } catch (IOException exception) {
                                            exception.printStackTrace();
                                        }
                                    }
                                    if (outputStream != null) {
                                        try {
                                            outputStream.write(bytes);
                                            outputStream.close();
                                        } catch (IOException exception) {
                                            exception.printStackTrace();
                                        }
                                    }
                                }
                            }

                        }
                    } else if (line.contains("//confirmchatwiehuser")) {
                        String[] user = line.split("@");
                        String sender = user[1];
                        user_online.setSelectedItem(sender);
                    }
                    else if (line.equals("//usersonline")) {
                        String[] split = dis.readUTF().split(",");
                        user_online.removeAllItems();
                        tempp.removeAllElements();
                        String receiverText = user_receiver.getText();

                        boolean isChattingOnline = false;
                        for (String user: split) {
                            if (user.equals(username) == false) {
                                user_online.addItem(user);
                                if (!user.equals(" ")) tempp.addElement(user);
                                User_Onlines = new JList<>(tempp);
                                if (Users_chat_window.get(user) == null) {
                                    JTextPane pane = new JTextPane();
                                    pane.setFont(new Font("Serif", Font.PLAIN, 14));
                                    pane.setEditable(false);
                                    Users_chat_window.put(user, pane);
                                }
                            }
                            if (receiverText.equals(user)) {
                                isChattingOnline = true;
                            }
                        }
                        if (isChattingOnline == false) {
                            user_online.setSelectedItem(" ");
                        } else {
                            user_online.setSelectedItem(receiverText);
                        }

                        user_online.validate();
                    }
                    else if (line.equals("//quit")) {
                        break;
                    }
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            } finally {
                try {
                    if (dis != null) {
                        dis.close();
                    }
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }
}