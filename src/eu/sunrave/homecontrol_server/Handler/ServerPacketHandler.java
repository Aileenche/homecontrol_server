package eu.sunrave.homecontrol_server.Handler;

import eu.sunrave.homecontrol_server.Libs.Functions;
import eu.sunrave.homecontrol_server.Libs.Packet;
import eu.sunrave.homecontrol_server.Main;

import static eu.sunrave.homecontrol_server.Libs.Packet.PacketType;

/**
 * Created by Admin on 29.12.2015.
 */
public class ServerPacketHandler {
    public static void handle(Packet p, int clientid) {
        switch (p.pakettype) {
            case registration:
                Registeration(p, clientid);
                break;
            case command:
                if (p.data.equals("rss")) {
                    Main.shutdown();
                } else {
                    Main.logger.notice(p.identifier + ": " + p.data);
                }
                break;
            case data:
                //TODO handle client data when it sends stuff to the server
                break;
        }
    }

    private static void Registeration(Packet p, int clientid) {
        int prevClientID = -1;
        int counter = 0;
        for (int i = 0; i < Main.clients.size(); i++) {
            if (p.identifier == Main.clients.get(i).identifier) {
                if (Main.clients.get(clientid) != Main.clients.get(i)) {
                    prevClientID = i;
                }
                counter++;
            }
        }
        if (counter > 1) {
            if (p.data != "force") {
                Main.logger.debug("Another user tried to connect with already registered idetentifier " + Main.clientSockets.get(clientid).getRemoteSocketAddress().toString());
                try {
                    Main.clientSockets.get(clientid).close();
                } catch (Exception e) {

                }
                Main.clientSockets.remove(clientid);
                Main.clients.remove(clientid);
                for (int i = clientid; i < Main.clientSockets.size(); i++) {
                    Main.clients.get(i).id = i;
                }
            } else {
                Main.logger.debug("Someone force connected with same identifier " + Main.clientSockets.get(prevClientID).getRemoteSocketAddress().toString());
                try {
                    Main.clientSockets.get(prevClientID).close();
                } catch (Exception e) {

                }
                Main.clientSockets.remove(prevClientID);
                Main.clients.remove(prevClientID);
                for (int i = prevClientID; i < Main.clientSockets.size(); i++) {
                    Main.clients.get(i).id = i;
                }
            }
        } else {
            Packet pack = new Packet(Main.identifier, PacketType.registration);
            pack.data = "Welcome to the server " + Main.clients.get(clientid).identifier;
            Functions.SendPacket(pack, Main.clientSockets.get(clientid));
        }
    }
}