package eu.sunrave.homecontrol_server.Libs;

import eu.sunrave.homecontrol_server.Main;

import java.io.*;
import java.net.Socket;

/**
 * Created by zeesh on 12/29/2015.
 */
public class Functions {
    public static void SendPacket(Packet p, Socket socket) {
        try {
            OutputStream out = socket.getOutputStream();
            out.write(serialize(p));
        } catch (Exception e) {
            Main.logger.debug("Error: message couldn't be sent");
            Main.logger.debug("" + e);
        }
    }

    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ObjectOutputStream o = new ObjectOutputStream(b);
        o.writeObject(obj);
        return b.toByteArray();
    }

    public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream b = new ByteArrayInputStream(bytes);
        ObjectInputStream o = new ObjectInputStream(b);
        return o.readObject();
    }

    public int getClientIDFromName(String name) {
        for (int i = 0; i < Main.clients.size(); i++) {
            if (Main.clients.get(i).identifier == name) {
                return i;
            }
        }
        return -1;
    }
}
