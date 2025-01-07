package com.hfad2.projectmanagmentapplication.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    private static final long MINUTE_MILLIS = 60 * 1000;
    private static final long HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final long DAY_MILLIS = 24 * HOUR_MILLIS;

    public static String formatTimestamp(Date timestamp) {
        long now = System.currentTimeMillis();
        long time = timestamp.getTime();
        long diff = now - time;

        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < HOUR_MILLIS) {
            long minutes = diff / MINUTE_MILLIS;
            return minutes + "m ago";
        } else if (diff < DAY_MILLIS) {
            long hours = diff / HOUR_MILLIS;
            return hours + "h ago";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd", Locale.getDefault());
            return sdf.format(timestamp);
        }
    }
}