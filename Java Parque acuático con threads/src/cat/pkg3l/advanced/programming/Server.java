package cat.pkg3l.advanced.programming;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author pablo
 */
public class Server {

    public static void main(String[] args) {
        WaterParkInterface water_park = new WaterParkInterface();
        water_park.setVisible(true);
        ServerSocket ss;
        Socket socket;
        int serverID = 0;
        System.out.print("Starting server... ");
        try {
            ss = new ServerSocket(10578);
            System.out.println("\t[OK]");
            while (true) {
                serverID++;
                socket = ss.accept();
                System.out.println("Connection " + serverID + ": " + socket);
                ((ServerThread) new ServerThread(socket, serverID, water_park.getWp())).start();
            }
        } catch (IOException ex) {
        }
    }
}
