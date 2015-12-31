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
            System.out.println("[" + sdf.format(new Date()) + "][DEBUG]    -> " + text);
        }
    }

    public void con(String text) {
        System.out.println("[" + sdf.format(new Date()) + "][CONSOLE]  -> " + text);
    }

    public void notice(String text) {
        System.out.println("[" + sdf.format(new Date()) + "][Notice]   -> " + text);
    }

    public void warning(String text) {
        System.out.println("[" + sdf.format(new Date()) + "][Warning]  -> " + text);
    }

    public void error(String text) {
        System.out.println("[" + sdf.format(new Date()) + "][Error]    -> " + text);
        Main.shutdown();
    }

    public void critical(String text) {
        System.out.println("[" + sdf.format(new Date()) + "][CRITICAL] -> " + text);
        Main.shutdown();
    }
}
