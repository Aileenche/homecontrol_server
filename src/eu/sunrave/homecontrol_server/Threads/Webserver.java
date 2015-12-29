package eu.sunrave.homecontrol_server.Threads;

import eu.sunrave.homecontrol_server.Main;

/**
 * Created by Admin on 28.12.2015.
 */
public class Webserver implements Runnable {
    public boolean isRunning;
    private Thread t;
    private String threadName;
    private HTTPServer server;

    public Webserver(String name) {
        threadName = name;
        Main.logger.notice("Creating " + threadName);
    }

    public void run() {
        Main.logger.notice("Running " + threadName);
        while (isRunning) {
            try {
                Thread.sleep(10);
            } catch (Exception e) {

            }
        }
        Main.logger.notice("Thread " + threadName + " exiting.");
    }

    public void start() {
        Main.logger.notice("Starting " + threadName);
        if (t == null) {
            t = new Thread(this, threadName);
            isRunning = true;
            t.start();
            server = new HTTPServer();
            server.start();
        }
    }

    public void stop() {
        Main.logger.notice("Stopping " + threadName);
        if (t != null) {
            server.stop();
            isRunning = false;
            t.interrupt();
            t = null;
        }
    }

}