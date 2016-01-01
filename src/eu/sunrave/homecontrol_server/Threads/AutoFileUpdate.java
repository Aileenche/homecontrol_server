package eu.sunrave.homecontrol_server.Threads;

import eu.sunrave.homecontrol_server.Libs.Functions;
import eu.sunrave.homecontrol_server.Libs.Packet;
import eu.sunrave.homecontrol_server.Main;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Created by Admin on 01.01.2016.
 */
public class AutoFileUpdate implements Runnable {
    public String deployDir = "/deploy/";
    public Path deployPath = Paths.get(Main.rundir.toString(), deployDir);
    public WatchKey key;
    public WatchService watcher;

    public AutoFileUpdate() {
        File path = new File(deployPath.toString());
        if (path.exists()) {
            Main.logger.debug("Deployfolder [" + path + "] exists");
        } else {
            Main.logger.notice("Deployfolder [" + path + "] not exists, try Create...");
            if (path.mkdirs()) {
                Main.logger.debug("Deployfolder [" + path + "] Created!");
            } else {
                Main.logger.error("Deployfolder [" + path + "] not created...");
            }
        }
        try {
            watcher = FileSystems.getDefault().newWatchService();
            deployPath.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        while (true) {
            try {
                key = watcher.take();
            } catch (InterruptedException ex) {
                return;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();

                @SuppressWarnings("unchecked")
                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path fileName = ev.context();

                Main.logger.debug(kind.name() + ": " + fileName);

                if (kind == ENTRY_MODIFY && fileName.toString().equals("homecontrol_server.jar")) {
                    try {
                        Thread.sleep(1000);
                        Files.copy(new File(deployPath + "/homecontrol_server.jar").toPath(), new File(Main.rundir + "/homecontrol_server.jar").toPath(), StandardCopyOption.REPLACE_EXISTING);
                        new File(deployPath + "/homecontrol_server.jar").delete();
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    //SendUpdateToClients();
                    Main.shutdown();
                }
            }
            boolean valid = key.reset();
            if (!valid) {
                break;
            }
        }
    }

    public void SendUpdateToClients() {
        File file = new File(Main.rundir + "/homecontrol_server.jar");
        byte[] data;
        try {
            data = Files.readAllBytes(file.toPath());
        } catch (Exception e) {
            Main.logger.debug("Couldn't get updates bytes when sending to clients");
            return;
        }
        for (int i = 0; i < Main.clientHarv.size(); i++) {
            if (Main.clientHarv.get(i) != null) {
                Clients c = Main.clientHarv.get(i);

                int splitAmount = (int) Math.ceil(data.length / 8192);
                byte[][] splitData = new byte[splitAmount][];
                int counter = 0;
                for (int j = 0; j < splitData.length; j++) {
                    splitData[j] = new byte[(int) Math.ceil(data.length / splitAmount)];
                    for (int k = 0; k < (int) Math.ceil(data.length / splitAmount); k++) {
                        splitData[j][k] = data[counter];
                        counter++;
                    }
                }

                Packet start = new Packet(Main.identifier, Packet.PacketType.file_start);
                start.data = splitAmount;
                Functions.SendPacket(start, c.socket);


                Packet[] packets = new Packet[splitAmount];
                for (int k = 0; k < packets.length; k++) {
                    packets[k] = new Packet(Main.identifier, Packet.PacketType.file);
                    packets[k].data = splitData[k];
                    packets[k].number = k;
                    Functions.SendPacket(packets[k], c.socket);
                }

                Packet end = new Packet(Main.identifier, Packet.PacketType.file_end);
                end.data = Functions.checkSum(file.getPath());
                Functions.SendPacket(end, c.socket);
            }
        }
    }
}