/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.candhcapital.Graphing;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * This class breaks an interval into the component parts. For example if the
 * dates given are 11/23/2015T9:30 to 11/27/2015T16:00 it will break that into
 * intervals as
 * Start: 11/23/2015T9:30 End: 11/23/2015T16:00
 * Start: 11/24/2015T9:30 End: 11/24/2015T16:00
 * Start: 11/25/2015T9:30 End: 11/25/2015T16:00
 * Start: 11/27/2015T9:30 End: 11/27:2015T13:00
 * Taking into account that Thanksgiving is on the 26th and that the 27th is
 * a shorter day, hence the end time of 13:00 instead of 14:00.
 * Gets Market Holidays and Short days from ZonedStockDateTime so that needs
 * to be updated on the regular with the holidays.
 * @author Corporate
 */
public class ZonedStockDateTimeIntervals {
    /**
     * the date and time our interval starts.
     */
    private LocalDateTime startDateTime;
    /**
     * the date and time our interval ends.
     */
    private LocalDateTime endDateTime;
    /**
     * The array of start values (mostly mornings).
     */
    private final ArrayList<ZonedStockDateTime> starts;
    /**
     * The array of end values (mostly afternoons).
     */
    private final ArrayList<ZonedStockDateTime> ends;
    /**
     * The count we are on for our next() function.
     */
    private int count = -1;
    /**
     * Populates the start and end times for our market day as well as the list
     * of holidays and short days for our market.
     */
    public ZonedStockDateTimeIntervals() {
        starts = new ArrayList<>();
        ends = new ArrayList<>();
    }
    /**
     * Populates the start and end times for our interval.
     * @param start **the start date and time**
     * @param end **the end date and time**
     */
    public final void setIntervalEndPoints(final LocalDateTime start,
            final LocalDateTime end) {
        startDateTime = start;
        endDateTime = end;
        count = -1;
        getIntervals();
    }
    public final void setIntervalEndPoints(final ZonedStockDateTime start,
            final ZonedStockDateTime end) {
        startDateTime = start.getZonedDateTime().toLocalDateTime();
        endDateTime = end.getZonedDateTime().toLocalDateTime();
        count = -1;
        getIntervals();
    }
    /**
     * Moves on to the next interval and returns true if there are any intervals
     * left in our starts and ends arrays.
     * @return true if there are any intervals left
     */
    public final boolean next() {
        count++;
        if (starts != null) {
            return (count < starts.size());
        } else {
            return false;
        }
    }
    /**
     * This function takes a query and replaces instances of the char string
     * [dates] with the correct start and end dates. Examples:
     * "Select * from foo where date [dates]" would return:
     * "Select * from foo where date BETWEEN '11/23/2015 9:30:00 -0500' AND
     * '11/23/2015 15:59:59 -0500'"
     * @param pQuery **the query we need to replace with datetimes**
     * @return a query with correct date times.
     */
    public final String getQuery(final String pQuery) {
        if (!pQuery.contains("[dates]")) {
            throw new IllegalArgumentException("String must contain <dates>");
        } else {
            return pQuery.replace("[dates]",
                    "datetime BETWEEN '" + starts.get(count).toSQLString() + "' AND '"
                            + ends.get(count).toSQLString() + "'");
        }
    }
    /**
     * Gets the actual start value of an interval.
     * @return **start time of an interval**
     */
    public final ZonedStockDateTime getStart() {
        return starts.get(count);
    }
    /**
     * Gets the actual End value of an interval.
     * @return **End time of an interval**
     */
    public final ZonedStockDateTime getEnd() {
        return ends.get(count);
    }
    /**
     * Gets the intervals for the starts and ends arrays.
     */
    private void getIntervals() {
        ZonedStockDateTime todayStart = new ZonedStockDateTime(startDateTime);
        ZonedStockDateTime todayEnd = todayStart.getTodaysEndDateTime();
        while (todayEnd.isBefore(endDateTime)) {
            starts.add(todayStart.getCopy());
            ends.add(todayEnd.getCopy());
            todayStart.addDays(1);
            todayEnd = todayStart.getTodaysEndDateTime();
        }
        if (todayStart.isBefore(endDateTime)) {
            starts.add(todayStart.getCopy());
            ends.add(new ZonedStockDateTime(endDateTime));
        }
    }
}
