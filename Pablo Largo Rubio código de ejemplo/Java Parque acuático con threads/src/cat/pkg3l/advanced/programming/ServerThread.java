package cat.pkg3l.advanced.programming;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pablo
 */
public class ServerThread extends Thread {

    private final Socket socket;
    private DataOutputStream dos;
    private DataInputStream dis;
    private final int idSession;
    private final WaterPark water_park;
    private String clientID;

    public ServerThread(Socket socket, int id, WaterPark wp) {
        this.socket = socket;
        this.water_park = wp;
        this.idSession = id;

    }

    @Override
    public void run() {
        try {
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            clientID = dis.readUTF();
            String request = "";
            request = dis.readUTF();
            System.out.println(request);
            dos.writeUTF("Successful connection");
            while (true) {
                try {
                    request = dis.readUTF();
                    if (("Minors query by " + clientID).equals(request)) {
                        dos.writeInt(this.water_park.getMinors_number());
                    } else if (("Slides uses query by " + clientID).equals(request)) {
                        BigPool big_pool = this.water_park.getBig_pool();
                        dos.writeInt(big_pool.getSlideA().getSlide_uses_number());
                        dos.writeInt(big_pool.getSlideB().getSlide_uses_number());
                        dos.writeInt(big_pool.getSlideC().getSlide_uses_number());
                    } else if (("Number users query by " + clientID).equals(request)) {
                        dos.writeInt(this.water_park.getChanging_room().calculateUsersNumber());
                        dos.writeInt(this.water_park.getWave_pool().calculateUsersNumber());
                        dos.writeInt(this.water_park.getChildren_pool().calculateUsersNumber());
                        dos.writeInt(this.water_park.getSun_beds().calculateUsersNumber());
                        dos.writeInt(this.water_park.getBig_pool().getSlideA().calculateUsersNumber());
                        dos.writeInt(this.water_park.getBig_pool().getSlideB().calculateUsersNumber());
                        dos.writeInt(this.water_park.getBig_pool().getSlideC().calculateUsersNumber());
                        dos.writeInt(this.water_park.getBig_pool().calculateUsersNumber());
                    } else if (("User query by " + clientID).equals(request)) {
                        String user = dis.readUTF();
                        User user_selected;
                        String location = "Not exist";
                        int num_activities = 0;
                        Iterator it = this.water_park.getUsers().iterator();
                        while (it.hasNext()) {
                            user_selected = (User) it.next();
                            if (user_selected.getName().equals(user)) {
                                location = user_selected.getActivity();
                                num_activities = user_selected.getCount_activities();
                            }
                        }
                        dos.writeUTF(location);
                        dos.writeInt(num_activities);
                    }
                    System.out.println(request);
                } catch (IOException ex) {
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
