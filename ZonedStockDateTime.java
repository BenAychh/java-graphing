package net.candhcapital.Graphing;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 *
 * @author Corporate
 */
public class ZonedStockDateTime {

  /**
   * The date on which we make all of our modifications.
   */
  private ZonedDateTime self;
  /**
   * The zone we are standardizing to.
   */
  private ZoneId zone;

  /**
   *
   * @param pldt **the date to start with on**
   */
  public ZonedStockDateTime(final LocalDateTime pldt) {
    zone = ZoneId.of("America/New_York");
    LocalDateTime ldt = pldt;
    if (isValidDate(ldt)) {
      self = ZonedDateTime.of(ldt, zone);
    } else {
      boolean moveToNextMorning = false;
      while (ldt.getDayOfWeek() == DayOfWeek.SATURDAY
          || ldt.getDayOfWeek() == DayOfWeek.SUNDAY
          || MarketConstants.isHoliday(ldt)) {
        ldt = ldt.plusDays(1);
        moveToNextMorning = true;
      }
      if (isAfterEnd(ldt) && !moveToNextMorning) {
        ldt = ldt.withHour(actualEndHour(ldt.toLocalDate()))
            .withMinute(MarketConstants.ENDMINUTE)
            .withSecond(MarketConstants.ENDSECOND);
      } else {
        ldt = ldt.withHour(MarketConstants.STARTHOUR)
            .withMinute(MarketConstants.STARTMINUTE)
            .withSecond(MarketConstants.STARTSECOND);
      }
      self = ZonedDateTime.of(ldt, ZoneId.of("America/New_York"));
    }
  }

  public ZonedStockDateTime(final ZonedDateTime pZdt) {
    zone = ZoneId.of("America/New_York");
    ZonedDateTime zdt = ZonedDateTime.ofInstant(pZdt.toInstant(), zone);
    LocalDateTime ldt = zdt.toLocalDateTime();
    if (isValidDate(ldt)) {
      self = ZonedDateTime.of(ldt, zone);
    } else {
      boolean moveToNextMorning = false;
      while (ldt.getDayOfWeek() == DayOfWeek.SATURDAY
          || ldt.getDayOfWeek() == DayOfWeek.SUNDAY
          || MarketConstants.isHoliday(ldt)) {
        ldt = ldt.plusDays(1);
        moveToNextMorning = true;
      }
      if (isAfterEnd(ldt) && !moveToNextMorning) {
        ldt = ldt.withHour(actualEndHour(ldt.toLocalDate()))
            .withMinute(MarketConstants.ENDMINUTE)
            .withSecond(MarketConstants.ENDSECOND);
      } else {
        ldt = ldt.withHour(MarketConstants.STARTHOUR)
            .withMinute(MarketConstants.STARTMINUTE)
            .withSecond(MarketConstants.STARTSECOND);
      }
      self = ZonedDateTime.of(ldt, ZoneId.of("America/New_York"));
    }
  }

  /**
   * Adds the selected number of days to our current date. Skips over weekend
   * days and all days that show up in the holidays section. If the skip ends on
   * a day that is a short day (listed in marketShortDays) and the time is after
   * the market closes then it will default to the last second of the day.
   *
   * @param days **the number of days to be added
   **
   * @param shortDays **use false to ignore shortened market days**
   */
  public final void addDays(final int days, final boolean shortDays) {
    ZonedDateTime oldSelf = self;
    self = self.plusDays(days);
    for (int i = 0; i <= oldSelf.until(self, ChronoUnit.DAYS); i++) {
      DayOfWeek dayOfWeek = oldSelf.plusDays(i).getDayOfWeek();
      if (dayOfWeek == DayOfWeek.SATURDAY
          || dayOfWeek == DayOfWeek.SUNDAY
          || MarketConstants.isHoliday(oldSelf.plusDays(i)
              .toLocalDate())) {
        self = self.plusDays(1);
      }
    }
    while (self.getDayOfWeek() == DayOfWeek.SATURDAY
        || self.getDayOfWeek() == DayOfWeek.SUNDAY
        || MarketConstants.isHoliday(self.toLocalDate())) {
      self = self.plusDays(1);
    }
    if (MarketConstants.isShortDay(self.toLocalDate()) && shortDays) {
      if (isAfterEnd(self.toLocalDateTime())) {
        self = self.withHour(actualEndHour())
            .withMinute(MarketConstants.ENDMINUTE)
            .withSecond(MarketConstants.ENDSECOND);
      }
    }
  }

  /**
   * Adds the selected number of days to our current date. Skips over weekend
   * days and all days that show up in the holidays section. If the skip ends on
   * a day that is a short day (listed in marketShortDays) and the time is after
   * the market closes then it will default to the last second of the day. This
   * can be turned off by calling plusDays(days, false)
   *
   * @param days **the number of days to be added**
   */
  public final void addDays(final int days) {
    addDays(days, true);
  }

  /**
   * Subtracts the selected number of days from our current date. Skips over
   * weekend days and all days that show up in the holidays section.
   *
   * @param days **the number of days to be subtracted
   **
   * @param shortDays **use false to ignore shortened market days**
   */
  public final void subtractDays(final int days, final boolean shortDays) {
    ZonedDateTime oldSelf = self;
    self = self.minusDays(days);
    for (int i = 0; i <= (-1) * oldSelf.until(self, ChronoUnit.DAYS); i++) {
      DayOfWeek dayOfWeek = oldSelf.minusDays(i).getDayOfWeek();
      if (dayOfWeek == DayOfWeek.SATURDAY
          || dayOfWeek == DayOfWeek.SUNDAY
          || MarketConstants.isHoliday(
              oldSelf.minusDays(i).toLocalDate())) {
        self = self.minusDays(1);
      }
    }
    while (self.getDayOfWeek() == DayOfWeek.SATURDAY
        || self.getDayOfWeek() == DayOfWeek.SUNDAY
        || MarketConstants.isHoliday(self.toLocalDate())) {
      self = self.minusDays(1);
    }
    if (MarketConstants.isShortDay(self.toLocalDate()) && shortDays) {
      if (isAfterEnd(self.toLocalDateTime())) {
        self = self.withHour(actualEndHour())
            .withMinute(MarketConstants.ENDMINUTE)
            .withSecond(MarketConstants.ENDSECOND);
      }
    }
  }

  /**
   * Ease of access method for subtract days.
   *
   * @param days **The number of days to be subtracted**
   */
  public final void subtractDays(final int days) {
    subtractDays(days, true);
  }

  /**
   * Adds the number of seconds specified. If more than getDayLengthInSeconds()
   * are specified, then it moves it on to the next day. Does not use a 24 hour
   * day but uses a 6.5 hour day. At the time of this writing (11/18/2015) the
   * current market time is 23400, hence adding 23400 seconds will move you to
   * the next market day at the exact same time.
   *
   * @param pSeconds **the number of seconds to be added**
   */
  public final void addSeconds(final long pSeconds) {
    long seconds = pSeconds;
    while (seconds > getDayLengthInSeconds()) {
      seconds = seconds - getDayLengthInSeconds();
      addDays(1);
    }
    for (int i = 0; i < seconds; i++) {
      self = self.plusSeconds(1);
      if (isAfterEnd(self.toLocalDateTime())) {
        moveToNextDay();
      }
    }
  }

  /**
   * Subtracts the number of seconds specified. If more than
   * getDayLengthInSeconds() are specified, then it moves it on to the previous
   * day. Does not use a 24 hour day but uses a 6.5 hour day. At the time of
   * this writing (11/18/2015) the current market time is 23400, hence
   * subtracting 23400 seconds will move you to the previous market day at the
   * exact same time.
   *
   * @param pSeconds **the number of seconds to be subtracted**
   */
  public final void subtractSeconds(final long pSeconds) {
    long seconds = pSeconds;
    while (seconds > getDayLengthInSeconds()) {
      seconds = seconds - getDayLengthInSeconds();
      subtractDays(1);
    }
    for (int i = 0; i < seconds; i++) {
      self = self.minusSeconds(1);
      if (isBeforeStart(self.toLocalDateTime())) {
        moveToPreviousDay();
      }
    }
  }

  /**
   * Adds the specified number of minutes to our shortened day.
   *
   * @param minutes **the number of minutes to be added**
   */
  public final void addMinutes(final long minutes) {
    addSeconds(minutes * MarketConstants.SECONDSINMINUTE);
  }

  /**
   * Adds the specified number of hours to our shortened day.
   *
   * @param hours **the number of hours to add**
   */
  public final void plusHours(final long hours) {
    addSeconds(hours * MarketConstants.SECONDSINHOUR);
  }

  /**
   * Subtracts the specified number of minutes from our shortened day.
   *
   * @param minutes **the number of minutes to be subtracted**
   */
  public final void subtractMinutes(final long minutes) {
    subtractSeconds(minutes * MarketConstants.SECONDSINMINUTE);
  }

  /**
   * Subtracts the specified number of hours from our shortened day.
   *
   * @param hours **the number of hours to subtract**
   */
  public final void subtractHours(final long hours) {
    subtractSeconds(hours * MarketConstants.SECONDSINHOUR);
  }

  /**
   *
   * @return a string formatted in yyyy-MM-dd HH:mm:ss
   */
  @Override
  public final String toString() {
    DateTimeFormatter dtf
        = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    return toString(dtf);
  }

  /**
   *
   * @param dtf the DateTimeFormatter for date formatting.
   * @return a string formatted by the given DTF
   */
  public final String toString(String pattern) {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
    return self.format(dtf);
  }

  /**
   *
   * @param dtf the DateTimeFormatter for date formatting.
   * @return a string formatted by the given DTF
   */
  public final String toString(final DateTimeFormatter dtf) {
    return self.format(dtf);
  }

  /**
   *
   * @return a string formatted in yyyy-MM-dd HH:mm:ss
   */
  public final String toSQLString() {
    DateTimeFormatter dtf
        = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z");
    return toString(dtf);
  }

  /**
   * Gets the time in regular ZonedDateTime format. Useful for comparisons.
   *
   * @return ZonedDateTime for temporal comparison.
   */
  public final ZonedDateTime getZonedDateTime() {
    return ZonedDateTime.of(self.toLocalDateTime(), zone);
  }

  /**
   * Gets the an end-cap for a day we have as a ZonedStockDateTime.
   *
   * @return ZonedStockDateTime for last second of today.
   */
  public final ZonedStockDateTime getTodaysEndDateTime() {
    return new ZonedStockDateTime(self.toLocalDateTime()
        .plusSeconds(getDayLengthInSeconds()
            - getSecondsSinceMarketOpened(
                self.toLocalDateTime()) - 1));
  }
  /**
   * Gets the an end-cap for a day we have as a ZonedStockDateTime.
   *
   * @return ZonedStockDateTime for last second of today.
   */
  public final ZonedStockDateTime getTodaysBeginningDateTime() {
    return new ZonedStockDateTime(self.toLocalDateTime()
        .minusSeconds(getSecondsSinceMarketOpened(
                self.toLocalDateTime())));
  }
  public final ZonedStockDateTime getTodaysMiddleDayTime() {
    return new ZonedStockDateTime(self.toLocalDateTime()
        .withHour(MarketConstants.MIDDLEHOUR)
        .withMinute(MarketConstants.MIDDLEMINUTE)
        .withSecond(MarketConstants.MIDDLESECOND));
  }

  /**
   * Makes a copy of our ZonedStockDateTime for manipulation without change.
   *
   * @return copy of itself.
   */
  public final ZonedStockDateTime getCopy() {
    return new ZonedStockDateTime(self.toLocalDateTime());
  }

  /**
   * Mirrors LocalDateTime.isBefore(LocalDateTime) functionality.
   *
   * @param ldt **the date to check
   **
   * @return true if this is before the parameter.
   */
  public final boolean isBefore(final LocalDateTime ldt) {
    return (self.toLocalDateTime().isBefore(ldt));
  }

  /**
   * Mirrors ZonedDateTime.isBefore(ZonedDateTime) functionality.
   *
   * @param zdt **the date to check
   **
   * @return true if this is before the parameter.
   */
  public final boolean isBefore(final ZonedDateTime zdt) {
    return (self.isBefore(zdt));
  }

  /**
   * Mirrors ZonedDateTime.isBefore(ZonedDateTime) functionality.
   *
   * @param zsdt **the date to check
   **
   * @return true if this is before the parameter.
   */
  public final boolean isBefore(final ZonedStockDateTime zsdt) {
    return self.isBefore(zsdt.getZonedDateTime());
  }

  /**
   * Mirrors ZonedDateTime.isAfter(ZonedDateTime) functionality.
   *
   * @param ldt **the date to check
   **
   * @return true if this is after the parameter.
   */
  public final boolean isAfter(final LocalDateTime ldt) {
    return (self.toLocalDateTime().isAfter(ldt));
  }

  /**
   * Mirrors ZonedDateTime.isAfter(ZonedDateTime) functionality.
   *
   * @param ldt **the date to check
   **
   * @return true if this is after the parameter.
   */
  public final boolean isAfter(final ZonedDateTime zdt) {
    return (self.isAfter(zdt));
  }

  public final boolean isAfter(final ZonedStockDateTime zsdt) {
    return (self.isAfter(zsdt.getZonedDateTime()));
  }

  /**
   * Returns the number of seconds between a starting time (self)and an ending
   * time.
   *
   * @param zsdt **the end time
   **
   * @return the number of seconds until the param date.
   */
  public final long getSecondsBetween(final ZonedStockDateTime zsdt) {
    return self.until(zsdt.getZonedDateTime(), ChronoUnit.SECONDS);
  }

  /**
   * Finds the number of seconds from self until the second date.
   *
   * @param endTime **The second date to count up to
   **
   * @return the number of seconds until the second date is reached.
   */
  public final long getMarketSecondsUntil(final ZonedStockDateTime endTime) {
    if (self.equals(endTime.getZonedDateTime())) {
      return 0;
    }
    if (endTime.isAfter(this)) {
      long seconds = 0;
      ZonedStockDateTime startTime = getCopy();
      while (startTime.isBefore(endTime)) {
        seconds += startTime.getDayLengthInSeconds();
        startTime.addDays(1);
      }
      if (!startTime.equals(endTime)) {
        startTime.subtractDays(1);
        seconds -= startTime.getDayLengthInSeconds();
        seconds += Duration.between(startTime.getZonedDateTime(),
            endTime.getZonedDateTime()).getSeconds();
      }
      return seconds;
    } else {
      return -1 * endTime.getMarketSecondsUntil(this);
    }
  }

  /**
   *
   * @return The total seconds in our market day. 0 if the day is a holiday,
   * Saturday or Sunday.
   */
  public final int getDayLengthInSeconds() {
    int temp = 0;
    if (self.getDayOfWeek() != DayOfWeek.SATURDAY
        && self.getDayOfWeek() != DayOfWeek.SUNDAY
        && !MarketConstants.isHoliday(self.toLocalDate())) {
      temp = MarketConstants.SECONDSINHOUR
          * (actualEndHour() - MarketConstants.STARTHOUR)
          + (MarketConstants.ENDMINUTE - MarketConstants.STARTMINUTE)
          * MarketConstants.SECONDSINMINUTE
          + (MarketConstants.ENDSECOND - MarketConstants.STARTSECOND)
          + 1;
    }
    return temp;
  }

  /**
   * gets an integer of the current time formatted as YYYYMMDD.
   * @return an 8 digit representation of the date (int).
   */
  public final String getIntegerDate() {
    final int yearPosition = 10000;
    final int monthPosition = 100;
    int date = self.getYear() * yearPosition
        + self.getMonthValue() * monthPosition
        + self.getDayOfMonth();
    return String.valueOf(date);
  }

  /**
   *
   * @param zsdt **the other ZonedStockDateTime to check against
   **
   * @return true if they are equal.
   */
  public final boolean equals(final ZonedStockDateTime zsdt) {
    return (self.equals(zsdt.getZonedDateTime()));
  }

  /**
   * Checks to see if the current time is the beginning of the day.
   *
   * @return true if start of day.
   */
  public final boolean isBeginningOfDay() {
    LocalDateTime todaysBeginning
        = LocalDateTime.of(self.getYear(), self.getMonth(),
            self.getDayOfMonth(), MarketConstants.STARTHOUR,
            MarketConstants.STARTMINUTE, MarketConstants.STARTSECOND);
    return todaysBeginning.equals(self.toLocalDateTime());
  }
  
  /**
   * Moves to the next market morning.
   */
  private void moveToNextDay() {
    self = self.withHour(MarketConstants.STARTHOUR)
        .withMinute(MarketConstants.STARTMINUTE)
        .withSecond(MarketConstants.STARTSECOND);
    addDays(1);
  }

  /**
   * Moves to the previous market ending second.
   */
  private void moveToPreviousDay() {
    self = self.withHour(MarketConstants.ENDHOUR)
        .withMinute(MarketConstants.ENDMINUTE)
        .withSecond(MarketConstants.ENDSECOND);
    addDays(1);
  }

  /**
   *
   * @param ldt the date/time to check.
   * @return the number of elapsed seconds since the market opened today.
   */
  private int getSecondsSinceMarketOpened(final LocalDateTime ldt) {
    return MarketConstants.SECONDSINHOUR
        * (ldt.getHour() - MarketConstants.STARTHOUR)
        + MarketConstants.SECONDSINMINUTE
        * (ldt.getMinute() - MarketConstants.STARTMINUTE)
        + ldt.getSecond() - MarketConstants.STARTSECOND;
  }

  /**
   *
   * @param ldt the date/time to check.
   * @return the number of elapsed seconds since midnight.
   */
  private int getRealSecondsInDay(final LocalDateTime ldt) {
    return MarketConstants.SECONDSINHOUR * ldt.getHour()
        + MarketConstants.SECONDSINMINUTE * ldt.getMinute()
        + ldt.getSecond();
  }

  /**
   * Checks to see if the current time is before the market open.
   *
   * @param ldt **The time to check
   **
   * @return true if the current time is before the market open.
   */
  private boolean isBeforeStart(final LocalDateTime ldt) {
    int startInSeconds
        = MarketConstants.STARTHOUR * MarketConstants.SECONDSINHOUR
        + MarketConstants.STARTMINUTE * MarketConstants.SECONDSINMINUTE
        + MarketConstants.STARTSECOND;
    return (getRealSecondsInDay(ldt) < startInSeconds);
  }

  /**
   * Checks to see if the current time is after the market close.
   *
   * @param ldt **The time to check
   **
   * @return true if the current time is after the market closes.
   */
  private boolean isAfterEnd(final LocalDateTime ldt) {
    int endInSeconds
        = actualEndHour(ldt.toLocalDate()) * MarketConstants.SECONDSINHOUR
        + MarketConstants.ENDMINUTE * MarketConstants.SECONDSINMINUTE
        + MarketConstants.ENDSECOND;
    return (endInSeconds < getRealSecondsInDay(ldt));
  }

  /**
   *
   * @param ldt **need the time info to check
   **
   * @return true if the time is between market open and close.
   */
  private boolean isValidDate(final LocalDateTime ldt) {
    return (!isBeforeStart(ldt) && !isAfterEnd(ldt)
        && ldt.getDayOfWeek() != DayOfWeek.SATURDAY
        && ldt.getDayOfWeek() != DayOfWeek.SUNDAY
        && !MarketConstants.isHoliday(ldt.toLocalDate()));
  }

  /**
   * Determines if the day ends at 4pm or 1pm.
   *
   * @param ld **a datetime
   **
   * @return the end time.
   */
  private int actualEndHour(final LocalDate ld) {
    if (MarketConstants.isShortDay(ld)) {
      return MarketConstants.SHORTENDHOUR;
    } else {
      return MarketConstants.ENDHOUR;
    }
  }

  /**
   *
   * @return the ending hour for this date.
   */
  private int actualEndHour() {
    return actualEndHour(self.toLocalDate());
  }
}
