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
                Packet pack = new Packet(Main.identifier, PacketType.registration);
                pack.data = "Welcome to the server " + Main.clients.get(clientid).identifier;
                Functions.SendPacket(pack, Main.clientSockets.get(clientid));
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
}

