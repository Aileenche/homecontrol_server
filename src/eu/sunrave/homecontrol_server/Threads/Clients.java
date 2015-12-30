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
    public boolean isStop;

    public Clients(int id) {
        this.id = id;
    }

    //TODO When client sends a message server recives them here
    @Override
    public void run() {
        isStop = false;
        isRegistered = false;
        while (true) {
            try {
                if (isStop)
                    break;

                InputStream IR = Main.clientSockets.get(id).getInputStream();
                byte[] data = new byte[Resources.MaxPacketSize];
                IR.read(data);
                Packet p = (Packet) Functions.deserialize(data);
                identifier = p.identifier;
                ip = Main.clientSockets.get(id).getRemoteSocketAddress().toString();
                if (!isRegistered) {
                    if (!TryRegister(p))
                        break;
                }
                Main.serverPacketHandler.handle(p, id);
            } catch (Exception e) {
                Main.logger.debug("Unable to get correct responce from client " + Main.clientSockets.get(id).getRemoteSocketAddress().toString());
                Main.clientSockets.remove(id);
                Main.clients.remove(id);
                for (int i = id; i < Main.clientSockets.size(); i++) {
                    Main.clients.get(i).id = i;
                }
                break;
            }
        }
    }

    private boolean TryRegister(Packet p) {
        if (p.pakettype == Packet.PacketType.registration) {
            isRegistered = true;
        } else {
            Main.logger.debug("No register packet was sent from " + identifier);
            try {
                Main.clientSockets.get(id).close();
            } catch (Exception ex) {

            }
            Main.clientSockets.remove(id);
            Main.clients.remove(id);
            for (int i = id; i < Main.clientSockets.size(); i++) {
                Main.clients.get(i).id = i;
            }
            return false;
        }

        int prevClientID = -1;
        int counter = 0;
        for (int i = 0; i < Main.clients.size(); i++) {
            if (p.identifier.equals(Main.clients.get(i).identifier)) {
                if (Main.clients.get(id) != Main.clients.get(i)) {
                    prevClientID = i;
                }
                counter++;
            }
        }

        if (counter > 1) {
            if (p.data.equals("force")) {
                Main.logger.debug("User already connected with that identifier " + identifier);
                Main.clientSockets.remove(id);
                Main.clients.remove(id);
                for (int i = id; i < Main.clientSockets.size(); i++) {
                    Main.clients.get(i).id = i;
                }
                return false;
            } else {
                Main.logger.debug("Removing previous user with same identifier" + identifier);
                Main.clients.get(prevClientID).isStop = true;
                Main.clientSockets.remove(prevClientID);
                Main.clients.remove(prevClientID);
                for (int i = id; i < Main.clientSockets.size(); i++) {
                    Main.clients.get(i).id = i;
                }
            }
        }

        return true;
    }
}