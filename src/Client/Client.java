package Client;
import javax.swing.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
/**
 * Client
 * Created by Admin
 * Date 1/11/2022 - 12:26 AM
 * Description: ...
 */
public class Client {
    public static int port = 3000;
    public static Socket socket;
    public static DataOutputStream dos;
    public static DataInputStream dis;
    public static void Connect() {
        String[] temp=new String[2];
        try {
            if (socket != null) {
                socket.close();
            }
            InetAddress a = InetAddress.getByName("localhost");
            socket = new Socket(a, port);
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error Connect Server","Client Fail",JOptionPane.INFORMATION_MESSAGE);
        }
    }
}