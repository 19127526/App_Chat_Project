package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Client
 * Created by Admin
 * Date 1/11/2022 - 12:26 AM
 * Description: ...
 */
public class Register_UI extends JFrame implements ActionListener {
    private JTextField User_Register;
    private JPasswordField Password_Register;
    private JPasswordField Confirm_Pass;

    Register_UI() {
        BoxLayout box = new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS);
        this.setLayout(box);

        JLabel register = new JLabel("Register");
        register.setFont(new Font("Serif", Font.PLAIN, 60));
        JPanel reg_pannel = new JPanel();
        reg_pannel.add(register);

        User_Register = new JTextField("");
        User_Register.setFont(new Font("Serif", Font.PLAIN, 25));
        User_Register.setMaximumSize(new Dimension(300, 30));
        JLabel user_text = new JLabel("            User            ");
        user_text.setFont(new Font("Serif", Font.PLAIN, 25));
        JPanel user_pannel = new JPanel();
        BoxLayout box_user = new BoxLayout(user_pannel, BoxLayout.LINE_AXIS);
        user_pannel.setLayout(box_user);
        user_pannel.add(user_text);
        user_pannel.add(User_Register);


        Password_Register = new JPasswordField("");
        Password_Register.setFont(new Font("Serif", Font.PLAIN, 25));
        Password_Register.setMaximumSize(new Dimension(300, 30));
        JLabel pass_text = new JLabel("        Password         ");
        pass_text.setFont(new Font("Serif", Font.PLAIN, 25));
        JPanel pass_pannel = new JPanel();
        BoxLayout box_pass = new BoxLayout(pass_pannel, BoxLayout.LINE_AXIS);
        pass_pannel.setLayout(box_pass);
        pass_pannel.add(pass_text);
        pass_pannel.add(Password_Register);

        Confirm_Pass = new JPasswordField("");
        Confirm_Pass.setFont(new Font("Serif", Font.PLAIN, 25));
        Confirm_Pass.setMaximumSize(new Dimension(300, 30));
        JLabel confirmpass_text = new JLabel(" Confirm Password  ");
        confirmpass_text.setFont(new Font("Serif", Font.PLAIN, 25));
        JPanel passconfirm_pannel = new JPanel();
        BoxLayout box_passconfirm = new BoxLayout(passconfirm_pannel, BoxLayout.LINE_AXIS);
        passconfirm_pannel.setLayout(box_passconfirm);
        passconfirm_pannel.add(confirmpass_text);
        passconfirm_pannel.add(Confirm_Pass);


        JButton register_button = new JButton("Register");
        register_button.setFont(new Font("Serif", Font.PLAIN, 20));
        register_button.addActionListener(this);
        register_button.setActionCommand("Register");

        JButton Cancel = new JButton("Cancel");
        Cancel.setFont(new Font("Serif", Font.PLAIN, 20));
        Cancel.setActionCommand("Cancel");
        Cancel.addActionListener(this);
        Cancel.setMaximumSize(new Dimension(register_button.getMaximumSize().width, register_button.getMaximumSize().height));

        JPanel button_pannel = new JPanel();
        BoxLayout box_button = new BoxLayout(button_pannel, BoxLayout.X_AXIS);
        button_pannel.setLayout(box_button);
        button_pannel.add(register_button);
        button_pannel.add(Box.createRigidArea(new Dimension(30, 0)));
        button_pannel.add(Cancel);

        this.add(reg_pannel);
        this.add(Box.createRigidArea(new Dimension(0, 20)));
        this.add(user_pannel);
        this.add(pass_pannel);
        this.add(passconfirm_pannel);
        this.add(Box.createRigidArea(new Dimension(0, 45)));
        this.add(button_pannel);
        this.add(Box.createRigidArea(new Dimension(0, 100)));

        this.setTitle("Register");
        this.setDefaultCloseOperation(this.DISPOSE_ON_CLOSE);
        this.resize(600, 400);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
    }


    public static void GUI() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Register_UI frame = new Register_UI();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void setText(){
    User_Register.setText("");
    Password_Register.setText("");
    Confirm_Pass.setText("");
    }
    public static void main(String[] args) {
        GUI();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals("Cancel")) {
            Login_UI.GUI();
            this.dispose();
        }
        if (command.equals("Register")) {
            if (User_Register.getText().isBlank() ||
                    Password_Register.getText().isBlank() ||
                    Confirm_Pass.getText().isBlank()) {
                JOptionPane.showMessageDialog(null, "Blank Input.", "Register Error", JOptionPane.INFORMATION_MESSAGE);
            }
            else if (!Password_Register.getText().equals(Confirm_Pass.getText())) {
                JOptionPane.showMessageDialog(null, "Password InCorrect!", "Register Error", JOptionPane.INFORMATION_MESSAGE);
            }
            else {
                BufferedWriter buffer = null;
                Client.Connect();
                try {
                    Client.dos.writeUTF("//register");
                    Client.dos.writeUTF(String.valueOf(User_Register.getText().toString()));
                    Client.dos.writeUTF(String.valueOf(Password_Register.getText().toString()));
                    Client.dos.flush();
                    String readUTF = Client.dis.readUTF();
                    if (readUTF.equals("//registersuccess")) {
                        JOptionPane.showMessageDialog(null, "Registered Successfully, Welcome to Chat App.");
                    } else
                        JOptionPane.showMessageDialog(null, "Register Fail !!!", "Register Error", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                setText();
            }
        }
    }
}
