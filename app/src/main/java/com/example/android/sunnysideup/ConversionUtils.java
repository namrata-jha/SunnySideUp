package com.example.android.sunnysideup;

import org.joda.time.DateTimeComparator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

class ConversionUtils {

    static String[] unixToDateTimeFormatter(long unixSeconds, double offset){

        long currOffset = (long) (offset - 5.5) * 3600L;
        long offsetSec =  (unixSeconds+currOffset) * 1000L;
        Date date = new Date(offsetSec);
        String datePattern = "d MMM yyyy";
        String timePattern = "h:mm a";
        SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
        SimpleDateFormat timeFormat = new SimpleDateFormat(timePattern);


        return new String[]{dateFormat.format(date), timeFormat.format(date), timeOfTheDay(date)};
    }

    private static String timeOfTheDay(Date date){

        SimpleDateFormat parser = new SimpleDateFormat("HH:mm");

        Date currentTime = new Date();
        Date morningStart = new Date();
        Date sunsetStart = new Date();
        Date nightStart = new Date();
        Date sunriseStart = new Date();

        try {
            morningStart = parser.parse("06:30");
            sunsetStart = parser.parse("17:30");
            nightStart = parser.parse("19:30");
            sunriseStart = parser.parse("05:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            currentTime = parser.parse(parser.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        DateTimeComparator comparator = DateTimeComparator.getTimeOnlyInstance();

        if(comparator.compare(currentTime, morningStart) >= 0 && comparator.compare(currentTime, sunriseStart) > 0
                && comparator.compare(currentTime, nightStart) < 0 && comparator.compare(currentTime, sunsetStart) < 0){
            return "day";
        }

        else if(comparator.compare(currentTime, sunsetStart) >= 0 && comparator.compare(currentTime, nightStart) < 0
                && comparator.compare(currentTime, morningStart) > 0 && comparator.compare(currentTime, sunriseStart) > 0){
            return "sunset";
        }

        else if((comparator.compare(currentTime, nightStart) >= 0 && comparator.compare(currentTime, sunriseStart) > 0
                && comparator.compare(currentTime, morningStart) > 0 && comparator.compare(currentTime, sunsetStart) > 0) ||
                (comparator.compare(currentTime, morningStart) < 0 && comparator.compare(currentTime, sunriseStart) < 0
                && comparator.compare(currentTime, nightStart) < 0 && comparator.compare(currentTime, sunsetStart) < 0)){
            return "night";
        }

//        else (comparator.compare(currentTime, sunriseStart) >= 0 && comparator.compare(morningStart, currentTime) >= 0)
        else
            return "sunrise";

    }

}
