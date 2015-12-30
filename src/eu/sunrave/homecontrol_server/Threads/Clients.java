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
    public boolean isRegistered;

    public Clients(int id) {
        this.id = id;
    }

    //TODO When client sends a message server recives them here
    @Override
    public void run() {
        isRegistered = false;
        while (true) {
            try {
                InputStream IR = Main.clientSockets.get(id).getInputStream();
                byte[] data = new byte[Resources.MaxPacketSize];
                IR.read(data);
                Packet p = (Packet) Functions.deserialize(data);
                identifier = p.identifier;
                ip = Main.clientSockets.get(id).getRemoteSocketAddress().toString();
                if (p.pakettype == Packet.PacketType.registration && !isRegistered) {
                    isRegistered = true;
                } else {
                    Main.logger.debug("No register packet was sent from " + Main.clientSockets.get(id).getRemoteSocketAddress().toString());
                    Main.clientSockets.get(id).close();
                    Main.clientSockets.remove(id);
                    Main.clients.remove(id);
                    for (int i = id; i < Main.clientSockets.size(); i++) {
                        Main.clients.get(i).id = Main.clients.get(i).id - 1;
                    }
                    break;
                }
                Main.serverPacketHandler.handle(p, id);
            } catch (Exception e) {
                Main.logger.debug("Unable to get correct responce from client " + Main.clientSockets.get(id).getRemoteSocketAddress().toString());
                Main.clientSockets.remove(id);
                Main.clients.remove(id);
                for (int i = id; i < Main.clientSockets.size(); i++) {
                    Main.clients.get(i).id = Main.clients.get(i).id - 1;
                }
                break;
            }
        }
    }
}