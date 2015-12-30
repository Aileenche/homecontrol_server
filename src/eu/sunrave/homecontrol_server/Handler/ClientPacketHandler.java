package eu.sunrave.homecontrol_server.Handler;

import eu.sunrave.homecontrol_server.Libs.Packet;
import eu.sunrave.homecontrol_server.Main;

/**
 * Created by Admin on 29.12.2015.
 */
public class ClientPacketHandler {
    public void handle(Packet p) {
        switch (p.pakettype) {
            case registration:
                Main.logger.notice((String) p.data);
                break;
            case data:
                //TODO handle server data when it sends stuff to the clients
                break;
        }
    }
}