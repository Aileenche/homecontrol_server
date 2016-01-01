package eu.sunrave.homecontrol_server.Handler;

import eu.sunrave.homecontrol_server.Main;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Created by zeesh on 1/1/2016.
 */
public class UpdateHandler implements Runnable {

    String dir = "/root/";

    @Override
    public void run() {
        while (true) {
            File source = new File(dir + "update/homecontrol_server.jar");
            if (source.exists() && !source.isDirectory()) {
                File destination = new File(dir + "homecontrol_server.jar");
                try {
                    Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception e) {
                    Main.logger.debug("Could not copy update file");
                }
                try {
                    Files.delete(source.toPath());
                } catch (Exception e) {
                    Main.logger.debug("Could not clean up after update");
                }
                Main.logger.debug("UPDATE COMPLETE... Restarting.");
                Main.isStopped = true;
            }
        }
    }
}