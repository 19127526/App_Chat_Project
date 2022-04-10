package Client;

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
public class Login_UI extends JFrame implements ActionListener {
    private JTextField user_login;
    private JPasswordField pass_login;

    public Login_UI() {
        BoxLayout box = new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS);
        this.setLayout(box);

        JLabel login = new JLabel("Login");
        login.setFont(new Font("Serif", Font.PLAIN, 60));
        JPanel login_pannel = new JPanel();
        login_pannel.add(login);

        user_login = new JTextField("");
        user_login.setFont(new Font("Serif", Font.PLAIN, 25));
        user_login.setMaximumSize(new Dimension(300, 30));
        JLabel user_text = new JLabel("            User            ");
        user_text.setFont(new Font("Serif", Font.PLAIN, 25));
        JPanel user_pannel = new JPanel();
        BoxLayout box_user = new BoxLayout(user_pannel, BoxLayout.LINE_AXIS);
        user_pannel.setLayout(box_user);
        user_pannel.add(user_text);
        user_pannel.add(user_login);


        pass_login = new JPasswordField("");
        pass_login.setFont(new Font("Serif", Font.PLAIN, 25));
        pass_login.setMaximumSize(new Dimension(300, 30));
        JLabel pass_text = new JLabel("        Password         ");
        pass_text.setFont(new Font("Serif", Font.PLAIN, 25));
        JPanel pass_pannel = new JPanel();
        BoxLayout box_pass = new BoxLayout(pass_pannel, BoxLayout.LINE_AXIS);
        pass_pannel.setLayout(box_pass);
        pass_pannel.add(pass_text);
        pass_pannel.add(pass_login);

        JButton button_register = new JButton("Register");
        button_register.setFont(new Font("Serif", Font.PLAIN, 20));
        button_register.setMaximumSize(new Dimension(100, 50));
        button_register.addActionListener(this);
        button_register.setActionCommand("register_in_login");
        JLabel text_register_notifi = new JLabel("Register here  ");
        text_register_notifi.setFont(new Font("Serif", Font.PLAIN, 20));
        JPanel register_panel = new JPanel();
        BoxLayout box_register = new BoxLayout(register_panel, BoxLayout.LINE_AXIS);
        register_panel.setLayout(box_register);
        register_panel.add(text_register_notifi);
        register_panel.add(button_register);


        JButton register_button = new JButton("Login");
        register_button.setFont(new Font("Serif", Font.PLAIN, 20));
        register_button.addActionListener(this);
        register_button.setActionCommand("login");

        JButton Cancel = new JButton("Cancel");
        Cancel.setFont(new Font("Serif", Font.PLAIN, 20));
        Cancel.setActionCommand("cancel_login");
        Cancel.addActionListener(this);

        JPanel button_pannel = new JPanel();
        BoxLayout box_button = new BoxLayout(button_pannel, BoxLayout.X_AXIS);
        button_pannel.setLayout(box_button);
        button_pannel.add(register_button);
        button_pannel.add(Box.createRigidArea(new Dimension(30, 0)));
        button_pannel.add(Cancel);

        this.add(login_pannel);
        this.add(Box.createRigidArea(new Dimension(0, 20)));
        this.add(user_pannel);
        this.add(pass_pannel);
        this.add(Box.createRigidArea(new Dimension(0, 20)));
        this.add(button_pannel);
        this.add(Box.createRigidArea(new Dimension(0, 45)));
        this.add(register_panel);
        this.add(Box.createRigidArea(new Dimension(0, 100)));


        this.setTitle("Login");
        this.setDefaultCloseOperation(this.DISPOSE_ON_CLOSE);
        this.resize(600, 400);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
    public String Login(String username, String password) {
        try {
            Client.Connect();
            Client.dos.writeUTF("//login");
            Client.dos.writeUTF(username);
            Client.dos.writeUTF(password);
            Client.dos.flush();
            String response = Client.dis.readUTF();
            return response;

        } catch (IOException e) {
            e.printStackTrace();
            return "Fail";
        }
    }
    public static void GUI() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Login_UI frame = new Login_UI();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args) {
        Login_UI a=new Login_UI();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command=e.getActionCommand();
        if(command.equals("cancel_login")){
            this.dispose();
        }
        if(command.equals("login")){
            String response = Login(user_login.getText().toString(),pass_login.getText().toString());
            if (response.equals("//loginsuccess") ) {
                String username=user_login.getText();
                JOptionPane.showMessageDialog(null,"Login successful!!!");
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        try {
                            Chat_UI frame = new Chat_UI(username, Client.dis, Client.dos);
                            frame.setVisible(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                this.dispose();
            }
            else if(response.equals("//incorrectlogin")){
                JOptionPane.showMessageDialog(null,"Login Incorrect");
            }
            else{
                JOptionPane.showMessageDialog(null,"Login Incorrect");
            }
        }
        if(command.equals("register_in_login")){
            this.dispose();
            Register_UI.GUI();
        }

    }

}
