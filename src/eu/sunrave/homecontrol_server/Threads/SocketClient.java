package eu.sunrave.homecontrol_server.Threads;

import eu.sunrave.homecontrol_server.Libs.Functions;
import eu.sunrave.homecontrol_server.Libs.Packet;
import eu.sunrave.homecontrol_server.Main;
import eu.sunrave.homecontrol_server.Resources;

import java.io.InputStream;

/**
 * Created by zeesh on 12/29/2015.
 */
public class SocketClient implements Runnable {

    //TODO When server sends a message client recives them here
    @Override
    public void run() {
        while (true) {
            try {
                InputStream IR = Main.mainServerSocket.getInputStream();
                byte[] data = new byte[Resources.MaxPacketSize];
                IR.read(data);
                Packet p = (Packet) Functions.deserialize(data);
                Main.clientPacketHandler.handle(p);

            } catch (Exception e) {
                Main.logger.debug("Error: reading from server\nDisconnecting...");
                break;
            }
        }
    }
}