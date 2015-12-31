package eu.sunrave.homecontrol_server.Handler;

import eu.sunrave.homecontrol_server.Libs.Functions;
import eu.sunrave.homecontrol_server.Libs.Packet;
import eu.sunrave.homecontrol_server.Main;
import eu.sunrave.homecontrol_server.Threads.Clients;

import static eu.sunrave.homecontrol_server.Libs.Packet.PacketType;

/**
 * Created by Admin on 29.12.2015.
 */
public class ServerPacketHandler {
    public static void handle(Packet p, Clients client) {
        switch (p.pakettype) {
            case registration:
                Packet pack = new Packet(Main.identifier, PacketType.registration);
                pack.data = "Welcome to the server " + client.identifier;
                Functions.SendPacket(pack, client.socket);
                break;
            case command:
                String[] splitted = ((String) p.data).split(" ");
                if (!((String) p.data).contains(" ")) {
                    splitted = new String[2];
                    splitted[0] = ((String) p.data);
                    splitted[1] = "0";
                }
                String msg = "";
                for (int i = 1; i < splitted.length; i++) {
                    msg += " " + splitted[i];
                }
                if (splitted[0].equals("rss")) {
                    Main.shutdown();
                } else if (splitted[0].equals("bc")) {
                    for (int i = 0; i < Main.clientHarv.size(); i++) {
                        if (Main.clientHarv.get(i) != null) {
                            Packet packet = new Packet(p.identifier, Packet.PacketType.message);
                            packet.data = "[BROADCAST]" + msg;
                            Functions.SendPacket(packet, Main.clientHarv.get(i).socket);
                        }
                    }
                    Main.logger.notice(p.identifier + ": [BROADCAST]" + msg);
                } else {
                    Main.logger.notice(p.identifier + ": " + p.data);
                }
                break;
            case message:
                Main.logger.notice(p.identifier + ": " + p.data);
                break;
            case data:
                //TODO handle client data when it sends stuff to the server
                break;
        }
    }
}