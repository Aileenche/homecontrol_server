package eu.sunrave.homecontrol_server.Libs;

import java.io.Serializable;

/**
 * Created by zeesh on 12/30/2015.
 */

public class Packet implements Serializable {

    public String identifier;
    ;
    public PacketType pakettype;
    public Object data;

    public Packet(String identifier, PacketType packettype) {
        this.identifier = identifier;
        this.pakettype = packettype;
    }

    public enum PacketType {
        registration,
        data
    }
}