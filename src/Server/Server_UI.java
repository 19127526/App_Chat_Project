package Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
/**
 * Client
 * Created by Admin
 * Date 1/11/2022 - 12:26 AM
 * Description: ...
 */
public class Server_UI extends JFrame implements ActionListener {
    private JButton Button_Start, Button_Stop;
    private Thread thread;
    private static int port = 3000;
    private static JLabel s;
    public static JTextArea window;
    public Server_UI() throws Exception {
        BoxLayout box = new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS);
        this.setLayout(box);
        JPanel headpanel = new JPanel();
        headpanel.setLayout(new BoxLayout(headpanel, BoxLayout.Y_AXIS));
        JLabel title = new JLabel();
        title.setText("SERVER");
        title.setFont(new Font("Serif", Font.PLAIN, 70));
        title.setAlignmentX(CENTER_ALIGNMENT);
        headpanel.add(title);

        this.add(headpanel);


        JPanel chatpanel = new JPanel();
        window = new JTextArea();
        window.setBackground(Color.BLACK);
        window.setForeground(Color.WHITE);
        window.setFont(new Font("Serif", Font.PLAIN, 18));
        window.setPreferredSize(new Dimension(500, 500));
        JScrollPane jScrollPane = new JScrollPane(window);
        chatpanel.add(jScrollPane);

        this.add(chatpanel);

        JPanel endpanel = new JPanel();
        JPanel statuspanel = new JPanel();
        statuspanel.setLayout(new BoxLayout(statuspanel, BoxLayout.LINE_AXIS));
        JLabel Status = new JLabel("Status: ");
        Status.setFont(new Font("Serif", Font.PLAIN, 15));

        s = new JLabel("STOP");
        s.setFont(new Font("Serif", Font.PLAIN, 16));

        statuspanel.add(Status);
        statuspanel.add(s);
        endpanel.add(statuspanel);

        this.add(endpanel);


        JPanel button_panel = new JPanel();
        button_panel.setLayout(new BoxLayout(button_panel, BoxLayout.LINE_AXIS));
        Button_Start = new JButton("START");
        Button_Stop = new JButton("STOP");
        Button_Start.addActionListener(this);
        Button_Start.setActionCommand("start");
        Button_Stop.addActionListener(this);
        Button_Stop.setActionCommand("stop");
        button_panel.add(Button_Start);
        button_panel.add(Box.createRigidArea(new Dimension(25, 0)));
        button_panel.add(Button_Stop);

        this.add(button_panel);
        this.add(Box.createRigidArea(new Dimension(0, 200)));
        this.setTitle("Server");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setSize(600, 650);
        this.setVisible(true);
    }

    public static void GUI() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Server_UI ui = new Server_UI();
                    ui.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args) {
        Server_UI.GUI();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command=e.getActionCommand();
        if (command.equals("start")) {
            try {
                thread = new Thread() {
                    public void run() {
                        try {
                            new Server();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                };
                port = 3000;
                thread.start();
                window.append("Server is starting on port " + port + "\n");
                s.setText("RUNNING");
                Button_Start.setEnabled(false);
                Button_Stop.setEnabled(true);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        } if (command.equals("stop")) {
            try {
                Client.Client.Connect();
                Client.Client.dos.writeUTF("#stopserver");
                Client.Client.dos.flush();
                window.append("Server is stopping on port "+port+"\n");
                Button_Stop.setEnabled(false);
                Button_Start.setEnabled(true);
                s.setText("OFF");
            } catch (Exception eexception) {
                eexception.printStackTrace();
                s.setText("OFF");
                Button_Stop.setEnabled(false);
                Button_Start.setEnabled(true);
                window.append("Server is stopping on port "+port+"\n");
            }
        }
    }
}
