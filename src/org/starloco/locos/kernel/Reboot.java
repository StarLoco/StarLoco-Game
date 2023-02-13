package org.starloco.locos.kernel;

import org.starloco.locos.database.DatabaseManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Reboot {

    private static byte remainingHours, remainingMinutes;

    public void initialize() {
        check();
    }

    public static boolean check() {
        Date date = Calendar.getInstance().getTime();

        int actualHour = Integer.parseInt(new SimpleDateFormat("HH").format(date));
        int actualMinute = Integer.parseInt(new SimpleDateFormat("mm").format(date));
        int total = actualHour * 60 + actualMinute;

        double restant = (24 * 60) - (total - (5 * 60));

        byte hour = (byte) (restant / 60);
        byte minute = (byte) (((restant / 60) - hour) * 60);

        Reboot.remainingHours = hour;
        Reboot.remainingMinutes = minute;
        switch (actualHour) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
                Reboot.remainingHours -= 24;
                break;
        }

        return (hour == 0 && minute == 0) || (actualHour == 4 && actualMinute == 59);
    }

    public static String toStr() {
        String im = "Im115;";
        if (Reboot.remainingHours == 0) {
            im += Reboot.remainingMinutes + (Reboot.remainingMinutes > 1 ? " minutes" : " minute");
        } else {
            im += Reboot.remainingHours + (Reboot.remainingHours > 1 ? " heures et " : " heure et ");
            im += Reboot.remainingMinutes + (Reboot.remainingMinutes > 1 ? " minutes" : " minute");
        }
        return im;
    }
}