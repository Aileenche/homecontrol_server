package eu.sunrave.homecontrol_server.Handler;

import eu.sunrave.homecontrol_server.Libs.Functions;
import eu.sunrave.homecontrol_server.Libs.Packet;
import eu.sunrave.homecontrol_server.Main;

/**
 * Created by Admin on 29.12.2015.
 */
public class ClientPacketHandler {
    public void handle(Packet p) {
        switch (p.pakettype) {
            case registration:
                if (p.data.toString().equals("whoareyou")) {
                    Main.logger.debug("Received whoareyou from Server, Prepair and send Answer...");
                    Packet answer = new Packet(Main.identifier, Packet.PacketType.registration);
                    if (Main.forceConnect) {
                        p.data = "force";
                    }
                    Functions.SendPacket(answer, Main.mainServerSocket);
                    Main.logger.debug("Sent Answer!");

                }
                break;
            case ping:
                Main.logger.debug("Received Ping from Server, returning Pong");
                Packet answer = new Packet(Main.identifier, Packet.PacketType.pong);
                Functions.SendPacket(answer, Main.mainServerSocket);
                break;
            case registrationComplete:
                Main.logger.notice(p.identifier + ": " + p.data);
                Main.logger.notice("Server HandShake successful!");
                break;
            case message:
                Main.logger.notice(p.identifier + ": " + p.data);
                break;
            case data:
                //TODO handle server data when it sends stuff to the clients
                break;
        }
    }
}