package com.lipata.whatsforlunch;

import java.util.Calendar;

/**
 * Created by jlipata on 6/21/16.
 */
public class Utility {

    public static String formatDate(long date){

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DATE);
        int year = calendar.get(Calendar.YEAR);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(month)
                .append("/")
                .append(day)
                .append("/")
                .append(year);

        return stringBuilder.toString();
    }
}
