package eu.sunrave.homecontrol_server.Libs;

import eu.sunrave.homecontrol_server.Main;

import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by zeesh on 12/29/2015.
 */
public class Functions {
    public static void SendMessage(String message, Socket socket) {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(Main.identifier + " " + message);
        } catch (Exception e) {
            Main.logger.debug("Error: message couldn't be sent");
        }
    }

    public int getClientIDFromName(String name) {
        for (int i = 0; i < Main.clients.size(); i++) {
            if (Main.clients.get(i).name == name) {
                return i;
            }
        }
        return -1;
    }
}
