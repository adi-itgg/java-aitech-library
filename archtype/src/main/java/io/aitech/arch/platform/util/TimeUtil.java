package io.aitech.arch.platform.util;

import lombok.experimental.UtilityClass;
import lombok.val;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.Period;

@UtilityClass
public final class TimeUtil {

  public static String measureDynamicTookTime(OffsetDateTime startTime) {
    return measureDynamicTookTime(startTime, OffsetDateTime.now());
  }

  public static String measureDynamicTookTime(OffsetDateTime startTime, OffsetDateTime endTime) {
    val period = Period.between(startTime.toLocalDate(), endTime.toLocalDate());
    int years = period.getYears();
    int months = period.getMonths();
    int days = period.getDays();

    Duration duration = Duration.between(startTime.toLocalTime(), endTime.toLocalTime());
    long hours = duration.toHours();
    long minutes = duration.toMinutes() % 60;
    long seconds = duration.getSeconds() % 60;
    long milliseconds = duration.toMillis();

    val sb = new StringBuilder();
    if (years > 0) {
      sb.append(years);
      if (years > 1) {
        sb.append(" years ");
      } else {
        sb.append(" year ");
      }
    }

    if (months > 0) {
      sb.append(months);
      if (months > 1) {
        sb.append(" months ");
      } else {
        sb.append(" month ");
      }
    }

    if (days > 0) {
      sb.append(days);
      if (days > 1) {
        sb.append(" days ");
      } else {
        sb.append(" day ");
      }
    }

    if (hours > 0) {
      sb.append(hours);
      if (hours > 1) {
        sb.append(" hours ");
      } else {
        sb.append(" hour ");
      }
    }

    if (minutes > 0) {
      sb.append(minutes);
      if (minutes > 1) {
        sb.append(" minutes ");
      } else {
        sb.append(" minute ");
      }
    }

    if (seconds > 0) {
      sb.append(seconds);
      if (seconds > 1) {
        sb.append(" seconds ");
      } else {
        sb.append(" second ");
      }
    } else if (milliseconds > 0) {
      sb.append(milliseconds).append("ms ");
    }

    return sb.toString();
  }

}
