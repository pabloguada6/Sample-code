package cat.pkg3l.advanced.programming;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author pablo
 */
public class Client extends Thread {

    private Socket sk;
    private DataOutputStream dos;
    private DataInputStream dis;
    private final String id;
    private WaterPark water_park;

    public Client(String id) {
        this.id = id;
    }

    public WaterPark getWater_park() {
        return water_park;
    }

    public DataOutputStream getDos() {
        return dos;
    }

    public DataInputStream getDis() {
        return dis;
    }

    @Override
    public void run() {
        try {
            sk = new Socket("127.0.0.1", 10578);
            dis = new DataInputStream(sk.getInputStream());
            dos = new DataOutputStream(sk.getOutputStream());
            System.out.println("Client " + id + " created");
            dos.writeUTF(id);
            dos.writeUTF("Client " + id + " has connected");
            String message;
            message = dis.readUTF();
            System.out.println(message);
            System.out.println("Client module running...");
            while (true) {

            }
        } catch (IOException ex) {

        }
    }
}
