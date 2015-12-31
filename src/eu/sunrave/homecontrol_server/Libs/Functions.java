package eu.sunrave.homecontrol_server.Libs;

import eu.sunrave.homecontrol_server.Main;
import eu.sunrave.homecontrol_server.Resources;
import eu.sunrave.homecontrol_server.Threads.Clients;

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

    public static boolean Reconnect() {
        //Attempt to connect to the server
        try {
            Main.mainServerSocket = new Socket(Resources.socketServerIP, Resources.SocketServerPort);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void printStacktoDebug(Exception e) {
        boolean current = Main.debugMode;
        Main.debugMode = true;
        for (int i = 0; i < e.getStackTrace().length; i++) {
            Main.logger.debug("" + e.getStackTrace()[i]);
        }
        Main.debugMode = current;
    }

    public static int getFreeClientSlot() {
        for (int i = 0; i < Main.clientHarv.size(); i++) {
            if (Main.clientHarv.get(i) == null) {
                return i;
            }
        }
        return -1;
    }

    public static boolean checkDoubleIdentifier(String identifier) {
        for (int i = 0; i < Main.clientHarv.size(); i++) {
            Clients client = Main.clientHarv.get(i);
            if (client != null) {
                if (client.identifier.equals(identifier)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean disconnectAndCleanUpClient(String identifier) {
        for (int i = 0; i < Main.clientHarv.size(); i++) {
            Clients client = Main.clientHarv.get(i);
            if (client != null) {
                if (client.identifier.equals(identifier)) {
                    try {
                        Main.clientHarv.get(i).socket.close();
                    } catch (IOException e) {
                        return false;
                    }
                    Main.clientHarv.set(i, null);
                }
            }
        }
        return true;
    }

    public static int countClients() {
        int counter = 0;
        for (int i = 0; i < Main.clientHarv.size(); i++) {
            if (Main.clientHarv.get(i) != null) {
                counter++;
            }
        }
        return counter;
    }
}
