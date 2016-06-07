package net.candhcapital.Graphing;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 *
 * @author Corporate
 */
public final class MarketConstants {
    /**
     * the number of seconds in an hour.
     */
    public static final int SECONDSINHOUR = 3600;
    /**
     * The number of seconds in a minute.
     */
    public static final int SECONDSINMINUTE = 60;
    /**
     * the hour that our market opens.
     */
    public static final int STARTHOUR = 9;
    /**
     * the minute our market opens.
     */
    public static final int STARTMINUTE = 30;
    /**
     * The second our market opens.
     */
    public static final int STARTSECOND = 0;
    /**
     * The middle hour of our day.
     */
    public static final int MIDDLEHOUR = 12;
    /**
     * The middle minute of our day.
     */
    public static final int MIDDLEMINUTE = 45;
    /**
     * The middle second of our day;
     */
    public static final int MIDDLESECOND = 0;
    /**
     * The last hour that our market is open.
     */
    public static final int ENDHOUR = 15;
    /**
     * The final minute of our market being open.
     */
    public static final int ENDMINUTE = 59;
    /**
     * The final second our market is open.
     */
    public static final int ENDSECOND = 59;
    /**
     * Some market days are shorter, this is the hour they end.
     */
    public static final int SHORTENDHOUR = 12;
    /**
     * The list of market holidays. Needs to be updated periodically.
     */
    private static final LocalDate[] MARKETHOLIDAYS = {
        LocalDate.of(2015, 1, 1),
        LocalDate.of(2015, 1, 19),
        LocalDate.of(2015, 2, 16),
        LocalDate.of(2015, 4, 3),
        LocalDate.of(2015, 5, 25),
        LocalDate.of(2015, 7, 4),
        LocalDate.of(2015, 9, 7),
        LocalDate.of(2015, 11, 26),
        LocalDate.of(2015, 12, 25),
        LocalDate.of(2016, 1, 1),
        LocalDate.of(2016, 1, 18),
        LocalDate.of(2016, 2, 15),
        LocalDate.of(2016, 3, 25),
        LocalDate.of(2016, 5, 30),
        LocalDate.of(2016, 7, 4),
        LocalDate.of(2016, 9, 5),
        LocalDate.of(2016, 11, 24),
        LocalDate.of(2016, 12, 26),
    };
    /**
     * The list of market short days. Needs to be updated periodically.
     */
    private static final LocalDate[] MARKETSHORTDAYS = {
        LocalDate.of(2015, 11, 27),
        LocalDate.of(2015, 12, 24),
        LocalDate.of(2016, 11, 25),
    };

    /**
     * Determines if the date is a holiday.
     * @param ld **the date to check**
     * @return true if holiday.
     */
    public static boolean isHoliday(final LocalDate ld) {
        for (LocalDate date : MARKETHOLIDAYS) {
            if (ld.equals(date)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Ease of access LocalDateTime method for isHoliday.
     * @param ldt **The date in LocalDateTime form to check**
     * @return true if holiday.
     */
    public static boolean isHoliday(final LocalDateTime ldt) {
        return isHoliday(ldt.toLocalDate());
    }
    /**
     * Determines if the day is a short day.
     * @param ld **the date to check**
     * @return true if a short day.
     */
    public static boolean isShortDay(final LocalDate ld) {
        for (LocalDate date : MARKETSHORTDAYS) {
            if (ld.equals(date)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Ease of access LocalDateTime method for isShortDay.
     * @param ldt **The date in LocalDateTime form to check**
     * @return true if holiday.
     */
    public static boolean isShortDay(final LocalDateTime ldt) {
        return isShortDay(ldt.toLocalDate());
    }
    /**
     * Private constructor for utility class.
     */
    private MarketConstants() {
        //not called
    }
}
