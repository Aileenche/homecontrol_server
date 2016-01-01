package eu.sunrave.homecontrol_server.Handler;

import eu.sunrave.homecontrol_server.Libs.Functions;
import eu.sunrave.homecontrol_server.Libs.Packet;
import eu.sunrave.homecontrol_server.Main;

import java.io.File;
import java.nio.file.Files;

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
            case file_start:
                Main.varshandler.update_file_converted = new byte[(int) p.data][];
                break;
            case file:
                Main.varshandler.update_file_converted[p.number] = (byte[]) p.data;
                break;
            case file_end:
                int counter = 0;
                for (int i = 0; i < Main.varshandler.update_file_converted.length; i++) {
                    for (int k = 0; k < Main.varshandler.update_file_converted[0].length; k++) {
                        Main.varshandler.update_file[counter] = Main.varshandler.update_file_converted[i][k];
                        counter++;
                    }
                }

                File dir = new File(Main.rundir + "/downloaded_content/");
                if (dir.exists()) {
                    Main.logger.debug("downloaded_content [" + dir + "] exists");
                } else {
                    Main.logger.notice("downloaded_content [" + dir + "] not exists, try Create...");
                    if (dir.mkdirs()) {
                        Main.logger.debug("downloaded_content [" + dir + "] Created!");
                    } else {
                        Main.logger.error("downloaded_content [" + dir + "] not created...");
                    }
                }
                File file = new File(dir.getPath() + "/homecontrol_server.jar");
                try {
                    Files.write(file.toPath(), Main.varshandler.update_file);
                } catch (Exception e) {
                    Main.logger.debug("Error while coping update");
                }
                if (Functions.checkSum(file.getPath()) != p.data) {
                    Main.logger.debug("File checksum did not match");
                } else {
                    Main.logger.debug("update downloaded successfully");
                }
                break;
        }
    }
}