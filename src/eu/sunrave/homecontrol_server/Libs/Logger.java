package eu.sunrave.homecontrol_server.Libs;

import eu.sunrave.homecontrol_server.Main;
import eu.sunrave.homecontrol_server.Resources;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Admin on 28.12.2015.
 */
public class Logger {

    SimpleDateFormat sdf;

    public boolean init() {
        //TODO Create Logfile, Check and so on
        this.sdf = new SimpleDateFormat(Resources.DateFormat);
        return false;
    }

    public void debug(String text) {
        if (Main.debugMode) {
            System.out.println("[DEBUG]   [" + sdf.format(new Date()) + "] -> " + text);
        }
    }

    public void con(String text) {
        System.out.println("[CONSOLE][" + sdf.format(new Date()) + "] -> " + text);
    }

    public void notice(String text) {
        System.out.println("[Notice]  [" + sdf.format(new Date()) + "] -> " + text);
    }

    public void warning(String text) {
        System.out.println("[Warning] [" + sdf.format(new Date()) + "] -> " + text);
    }

    public void error(String text) {
        System.out.println("[Error]   [" + sdf.format(new Date()) + "] -> " + text);
        Main.isStopped = true;
    }

    public void critical(String text) {
        System.out.println("[CRITICAL][" + sdf.format(new Date()) + "] -> " + text);
    }
}
