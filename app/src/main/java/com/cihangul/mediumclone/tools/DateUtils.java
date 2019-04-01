package com.cihangul.mediumclone.tools;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtils {

    public static String getDateString(Date date) {
        try{
            if (date == null)
                return "";
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            return "" + day + "/" + month + "/" + year;

        }catch (Exception e){
            return "";
        }
    }
}
