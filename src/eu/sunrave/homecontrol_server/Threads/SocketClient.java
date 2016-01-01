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
                Main.logger.notice("Disconnected...");
                Main.logger.notice("Atempting to reconnect...");
                int counter = 1;
                int reconnectintval = 1000;
                while (true) {
                    try {
                        Thread.sleep(reconnectintval);
                    } catch (Exception ex) {
                        Main.logger.debug("Couldn't wait to retry again");
                    }

                    Main.logger.debug("Atempt " + counter);
                    if (Functions.Reconnect()) {
                        Main.logger.notice("Reconnected to server after " + counter + " tries");
                        Packet p = new Packet(Main.identifier, Packet.PacketType.registration);
                        if (Main.forceConnect) {
                            p.data = "force";
                        }
                        Functions.SendPacket(p, Main.mainServerSocket);
                        break;
                    } else {
                        if (counter % 60 == 0) {
                            reconnectintval += 500;
                            Main.logger.notice("Increased reconnect interval to " + reconnectintval);
                        }
                        Main.logger.debug("Failed to reconnect to server");
                    }
                    counter++;
                }
            }
        }
    }
}