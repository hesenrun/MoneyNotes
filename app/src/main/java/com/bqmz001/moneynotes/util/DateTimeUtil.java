package com.bqmz001.moneynotes.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static synchronized String timestampToDate(long timestamp) {
        Date date = new Date(timestamp);
        String str = simpleDateFormat.format(date);
        return str;
    }

    public static synchronized String textToDateTime(String source) throws ParseException {
        return simpleDateFormat.format(simpleDateFormat.parse(source));
    }

    public static synchronized long textToTimestamp(String time) throws Exception {
        return simpleDateFormat.parse(time).getTime();
    }

    //获取今年
    public static synchronized int getNowYear() {
        return new DateTime().getYear();
    }

    //获取本月
    public static synchronized int getNowMonth() {
        return new DateTime().getMonthOfYear();
    }

    //获取今天
    public static synchronized int getNowDay() {
        return new DateTime().getDayOfMonth();
    }

    //获取今天的0：00的字符串
    public static synchronized long getNowDayStartTimeStamp() {
        return new DateTime().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0).getMillis();
    }

    //获取今天的23：59：59.999的字符串
    public static synchronized long getNowDayEndTimeStamp() {
        return new DateTime().withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).withMillisOfSecond(59).getMillis();
    }

    //获取当前时间
    public static synchronized long getNow() {
        return new DateTime().getMillis();
    }

    //获取本月第一天0时
    public static synchronized long getFirstTimeOfThisMonth() {
        return new DateTime()
                .withDayOfMonth(new DateTime().dayOfMonth().getMinimumValue())
                .withHourOfDay(0)
                .withMinuteOfHour(0)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0)
                .getMillis();
    }

    //获取本月最后一天的最后时间
    public static synchronized long getLastTimeOfThisMonth() {
        return new DateTime()
                .withDayOfMonth(new DateTime().dayOfMonth().getMaximumValue())
                .withHourOfDay(23)
                .withMinuteOfHour(59)
                .withSecondOfMinute(59)
                .withMillisOfSecond(999)
                .getMillis();
    }

    //获取本周的第一天0：00(周一)
    public static synchronized long getFirstTimeOfThisWeek() {
        return new DateTime()
                .withWeekyear(new DateTime().getYear())
                .withWeekOfWeekyear(new DateTime().getWeekOfWeekyear())
                .withDayOfWeek(DateTimeConstants.MONDAY)
                .withHourOfDay(0)
                .withMinuteOfHour(0)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0)
                .getMillis();
    }

    //获取本周的最后一天的最后时刻
    public static synchronized long getLastTimeOfThisWeek() {
        return new DateTime()
                .withWeekyear(new DateTime().getYear())
                .withWeekOfWeekyear(new DateTime().getWeekOfWeekyear())
                .withDayOfWeek(DateTimeConstants.SUNDAY)
                .withHourOfDay(23)
                .withMinuteOfHour(59)
                .withSecondOfMinute(59)
                .withMillisOfSecond(999)
                .getMillis();
    }

    //获取某月第一天——我不信还有哪个月能给2号起步

    //获取某月的最后一天
    public static synchronized int getLastDayOfMonth(int year, int month) {
        DateTime dateTime = new DateTime(year, month, 1, 0, 0);
        return dateTime.dayOfMonth().getMaximumValue();
    }

    //获取某一周的第一天0：00
    public static synchronized long getFirstTimeOfWeek(int year, int weekOfYear) {
        return new DateTime()
                .withWeekyear(year)
                .withWeekOfWeekyear(weekOfYear)
                .withDayOfWeek(DateTimeConstants.MONDAY)
                .withHourOfDay(0)
                .withMinuteOfHour(0)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0)
                .getMillis();
    }

    //获取某一周最后一天的最后时间
    public static synchronized long getLastTimeOfWeek(int year, int weekOfYear) {
        return new DateTime()
                .withWeekyear(year)
                .withWeekOfWeekyear(weekOfYear)
                .withDayOfWeek(DateTimeConstants.SUNDAY)
                .withHourOfDay(23)
                .withMinuteOfHour(59)
                .withSecondOfMinute(59)
                .withMillisOfSecond(999)
                .getMillis();
    }

    //获取某一年第一天时间
    public static synchronized long getFirstTimeOfYear(int year) {
        return new DateTime(year, 1, 1, 0, 0, 0, 0).getMillis();
    }

    //获取某一年最后一天时间
    public static synchronized long getLastTimeOfYear(int year) {
        return new DateTime(year, 12, 31, 23, 59, 59, 999).getMillis();

    }

    //获取某一天的开始/结束时间——我不信有一天能有比24小时多的少的，至少电脑上看来是这样
}
