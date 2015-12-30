package eu.sunrave.homecontrol_server.Threads;

import eu.sunrave.homecontrol_server.Libs.Functions;
import eu.sunrave.homecontrol_server.Libs.Packet;
import eu.sunrave.homecontrol_server.Main;
import eu.sunrave.homecontrol_server.Resources;

import java.io.InputStream;

/**
 * Created by Admin on 29.12.2015.
 */
public class Clients implements Runnable {
    public String identifier;
    public int id;
    public String ip;

    public Clients(int id) {
        this.id = id;
    }

    //TODO When client sends a message server recives them here
    @Override
    public void run() {
        while (true) {
            try {
                InputStream IR = Main.clientSockets.get(id).getInputStream();
                byte[] data = new byte[Resources.MaxPacketSize];
                IR.read(data);
                Packet p = (Packet) Functions.deserialize(data);
                identifier = p.identifier;
                ip = Main.clientSockets.get(id).getRemoteSocketAddress().toString();
                Main.serverPacketHandler.handle(p, id);
            } catch (Exception e) {
                Main.logger.debug("Unable to get correct responce from client " + Main.clientSockets.get(id).getRemoteSocketAddress().toString());
                Main.clientSockets.remove(id);
                Main.clients.remove(id);
                break;
            }
        }
    }
}