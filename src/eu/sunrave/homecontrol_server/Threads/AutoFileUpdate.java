package eu.sunrave.homecontrol_server.Threads;

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
                        File old = new File(Main.rundir + "/homecontrol_server.jar");
                        old.delete();
                        Main.logger.debug("src: " + deployPath.toString() + "/homecontrol_server.jar");
                        Main.logger.debug("tar: " + Main.rundir + "/homecontrol_server.jar");
                        Path newFile = Paths.get(Main.rundir.toString(), "/homecontrol_server.jar");
                        Files.move(fileName.toAbsolutePath(), newFile, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //Main.shutdown();
                }
            }
            boolean valid = key.reset();
            if (!valid) {
                break;
            }
        }
    }
}