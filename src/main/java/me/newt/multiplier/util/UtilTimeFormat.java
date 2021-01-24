package me.newt.multiplier.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class UtilTimeFormat {

    /**
     * Format long-time into a neat and readable String.
     * @param time Time to format.
     * @return Formatted time.
     */
    public static String formatDuration(long time) {
        if (time == -1)
            return "Until server restart";

        TimeUnit timeUnit;
        if (time < 60000) timeUnit = TimeUnit.SECONDS;
        else if (time < 3600000) timeUnit = TimeUnit.MINUTES;
        else if (time < 86400000) timeUnit = TimeUnit.HOURS;
        else timeUnit = TimeUnit.DAYS;

        String formattedTime;
        double numberOfTheseUnits;

        switch (timeUnit) {
            case DAYS:
                numberOfTheseUnits = trim(time / 86400000d);
                formattedTime = numberOfTheseUnits + " Day";
                break;
            case HOURS:
                numberOfTheseUnits = trim(time / 3600000d);
                formattedTime = numberOfTheseUnits + " Hour";
                break;
            case MINUTES:
                numberOfTheseUnits = trim(time / 60000d);
                formattedTime = numberOfTheseUnits + " Minute";
                break;
            case SECONDS:
                numberOfTheseUnits = trim(time / 1000d);
                formattedTime = numberOfTheseUnits + " Second";
                break;
            default:
                numberOfTheseUnits = 0d;
                formattedTime = "0.0 Second";
                break;
        }

        if (numberOfTheseUnits != 1) formattedTime += "s";
        return formattedTime;
    }

    /**
     * Trim down doubles to one decimal.
     * @param number Double to trim.
     * @return Trimmed double.
     */
    private static double trim(double number) {
        DecimalFormatSymbols decimalSymbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat decimalFormat = new DecimalFormat("#.#", decimalSymbols);
        return Double.parseDouble(decimalFormat.format(number));
    }
}
