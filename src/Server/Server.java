package Server;
import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
/**
 * Client
 * Created by Admin
 * Date 1/11/2022 - 12:26 AM
 * Description: ...
 */
public class Server {
    private Socket socket;
    private ServerSocket sv_socket;
    public static Vector<ClientHandle> clients = new Vector<ClientHandle>();
    private String file = "users.txt";


    private void WriteFile(String username, String password) throws IOException {
        FileWriter bw = new FileWriter(file,true);
        BufferedWriter bww = new BufferedWriter(bw);
        bww.write(username+"//"+password+"\n");
        bww.close();
    }

    private void ReadFile() {
        File file = new File(this.file);
        if (file.exists())
            try {
                BufferedReader br = new BufferedReader(new FileReader(file.getName()));
                String line = br.readLine();
                while (line != null) {
                    clients.add(new ClientHandle(line.split("//")[0], line.split("//")[1],false));
                    line = br.readLine();
                }
                br.close();
            } catch (Exception e) {
                e.printStackTrace();

            }
    }

    public static void updateOnlineUsers() {
        String mess = " ";
        for (ClientHandle client:clients) {
            if (client.getflag() == true) {
                mess += ",";
                mess += client.getusername();
            }
        }
        for (ClientHandle client:clients) {
            if (client.getflag() == true) {
                try {
                    client.getdataoutputstream().writeUTF("//usersonline");
                    client.getdataoutputstream().writeUTF(mess);
                    client.getdataoutputstream().flush();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }
    public Server() throws IOException {
        DataInputStream dis=null;
        DataOutputStream dos=null;
        try {
            this.ReadFile();
            sv_socket = new ServerSocket(3000);
            while (true) {
                socket = sv_socket.accept();
                InputStream is = socket.getInputStream();
                OutputStream os = socket.getOutputStream();
                dis = new DataInputStream(is);
                dos= new DataOutputStream(os);
                String line = dis.readUTF();
                if (line.equals("//register")) {
                    String user = dis.readUTF();
                    String pass = dis.readUTF();
                    if (user_existed(user) == false) {
                        ClientHandle ClientHandle = new ClientHandle(socket, user, pass, false);
                        clients.add(ClientHandle);
                        this.WriteFile(user,pass);
                        dos.writeUTF("//registersuccess");
                        dos.flush();
                    } else {
                        dos.writeUTF("//sameuse");
                        dos.flush();
                    }
                } else if (line.equals("//login")) {
                    String user = dis.readUTF();
                    String pass = dis.readUTF();
                    if (user_existed(user) == true) {
                        for (ClientHandle client : clients) {
                            if (client.getusername().equals(user)) {
                                if (pass.equals(client.getpassword())) {
                                    Server_UI.window.append(user + " just joined the chat app\n");
                                    ClientHandle handle = client;
                                    handle.setsocket(socket);
                                    handle.setflag(true);
                                    dos.writeUTF("//loginsuccess");
                                    dos.flush();
                                    new Thread(handle).start();
                                    updateOnlineUsers();
                                } else {
                                    dos.writeUTF("//incorrectlogin");
                                    dos.flush();
                                }
                                break;
                            }
                        }
                    } else {
                        dos.writeUTF("#incorrectpw");
                        dos.flush();
                    }
                }else if (line.equals("#stopserver")){
                    if (sv_socket != null) {
                        sv_socket.close();
                        dis.close();
                        dos.close();
                    }
                }
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            if (sv_socket != null) {
                sv_socket.close();
                dis.close();
                dos.close();
            }
        }
    }
    public boolean user_existed(String name) {
        for (ClientHandle client : clients) {
            if (client.getusername().equals(name)) {
                return true;
            }
        }
        return false;
    }
}

class ClientHandle implements Runnable {
    private DataInputStream dis;
    private DataOutputStream dos;
    private boolean flag;
    private String username;
    private String password;
    private Socket socket;

    public ClientHandle(Socket s, String username, String password, boolean flag) throws IOException {
        this.socket = s;
        this.username = username;
        this.password = password;
        this.dis = new DataInputStream(s.getInputStream());
        this.dos = new DataOutputStream(s.getOutputStream());
        this.flag = flag;
    }

    public ClientHandle(String username, String password, boolean flag) {
        this.username = username;
        this.password = password;
        this.flag = flag;
    }
    public boolean getflag() {
        return this.flag;
    }

    public String getpassword() {
        return this.password;
    }
    public String getusername() {
        return this.username;
    }


    public DataOutputStream getdataoutputstream() {
        return this.dos;
    }

    public void setsocket(Socket s) {
        this.socket = s;
        try {
            this.dis = new DataInputStream(s.getInputStream());
            this.dos = new DataOutputStream(s.getOutputStream());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void setflag(boolean flag) {
        this.flag = flag;
    }

    public void closesocket() {
        if (socket != null) {
            try {
                socket.close();
                dis.close();
                dos.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public void run() {

        while (true) {
            try {
                String line = null;
                line = dis.readUTF();
                if (line.equals("//exit")) {
                    Server_UI.window.append(username + " just left the chat room\n");
                    dos.writeUTF("//quit");
                    dos.flush();
                    socket.close();
                    this.flag = false;
                    Server.updateOnlineUsers();
                    break;
                }
                else if (line.equals("//message")) {
                    String readUTF = dis.readUTF();
                    String mess = dis.readUTF();
                    Lock reentrantLock = new ReentrantLock();
                    for (ClientHandle clienthandle : Server.clients) {
                        if (clienthandle.getusername().equals(readUTF)) {
                            reentrantLock.lock();
                            try {
                                clienthandle.getdataoutputstream().writeUTF("//message");
                                clienthandle.getdataoutputstream().writeUTF(this.username);
                                clienthandle.getdataoutputstream().writeUTF(mess);
                                clienthandle.getdataoutputstream().flush();
                                break;
                            } finally {
                                reentrantLock.unlock();
                            }
                        }
                    }
                }
                else if (line.contains("//chatwithuser")){
                    String user=line.substring(4);
                    String[] split=user.split("//");
                    Lock reentrantLock = new ReentrantLock();
                    for (ClientHandle clienthandle : Server.clients) {
                        if (clienthandle.getusername().equals(split[1])) {
                            reentrantLock.lock();
                            try {
                                clienthandle.getdataoutputstream().writeUTF("//confirmchatwiehuser@"+split[0]+"@"+split[1]);
                                clienthandle.getdataoutputstream().flush();
                                break;
                            } finally {
                                reentrantLock.unlock();
                            }
                        }
                    }
                }
                else if (line.equals("//uploadfile")) {
                    int i = 2048;
                    byte[] bytes = new byte[i];
                    String readUTF = dis.readUTF();
                    String file = dis.readUTF();
                    int length = Integer.parseInt(dis.readUTF());
                    Lock reentrantLock = new ReentrantLock();
                    for (ClientHandle client : Server.clients) {
                        if (client.getusername().equals(readUTF)) {
                            reentrantLock.lock();
                            try{
                                client.getdataoutputstream().writeUTF("//uploadfile");
                                client.getdataoutputstream().writeUTF(this.username);
                                client.getdataoutputstream().writeUTF(file);
                                client.getdataoutputstream().writeUTF(String.valueOf(length));
                                while (length > 0) {
                                    dis.read(bytes, 0, Math.min(length, i));
                                    client.getdataoutputstream().write(bytes, 0, Math.min(length, i));
                                    length -= i;
                                }
                                client.getdataoutputstream().flush();
                                break;}
                            finally {
                                reentrantLock.unlock();
                            }
                        }
                    }
                }

            } catch (EOFException exception){
                exception.printStackTrace();
                JOptionPane.showMessageDialog(null, "Stop Server!!","Server",JOptionPane.INFORMATION_MESSAGE);
                closesocket();
                System.exit(0);
            }
            catch (SocketException exception){
                exception.printStackTrace();
                JOptionPane.showMessageDialog(null, "Stop Server!!","Server",JOptionPane.INFORMATION_MESSAGE);
                closesocket();
                System.exit(0);
            }
            catch (IOException exception) {
                exception.printStackTrace();
                JOptionPane.showMessageDialog(null, "Upload File Fail!!","Upload File",JOptionPane.INFORMATION_MESSAGE);
            }

        }
    }

}