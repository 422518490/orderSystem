package com.yaya.common.util;


import com.yaya.common.constant.ErrorRespCode;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

/**
 * @author liaoyubo
 * @version 1.0
 * @date 2019/5/22
 * @description
 */
@Slf4j
public class DateUtils {

    /**
     * 默认日期格式
     */
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    /**
     * 默认日期时间格式
     */
    public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 默认时间格式
     */
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";

    public static final String DATETIME_FORMAT_NO_SYMBOL = "yyyyMMddHHmmss";

    public static final String DATE_FORMAT_NO_SYMBOL = "yyyyMMdd";

    public static final String DATETIME_FORMAT_T = "yyyy-MM-ddTHH:mm:ss";

    /**
     * 判断字符串是否为指定的日期格式
     *
     * @param dateStr 需要判断的字符串
     * @param format  指定的日期格式
     * @return true=字符串是日期格式
     */
    public static boolean isDateFormat(String dateStr, String format) {
        try {
            //判断日期字符串和日期格式是否为空
            if (!Optional.ofNullable(dateStr).isPresent() || "".equals(dateStr)
                    || !Optional.ofNullable(format).isPresent() || "".equals(format)) {
                return false;
            }
            new SimpleDateFormat(format).parse(dateStr);
        } catch (ParseException e) {
            log.error("判断字符串是否为指定的日期格式解析错误:" + e);
            return false;
        }
        return true;
    }

    /**
     * 将Date时间转成字符串
     */
    public static String dateToStr(Date date, String format) {
        //判断日期字符串和日期格式是否为空
        if (!Optional.ofNullable(date).isPresent()
                || !Optional.ofNullable(format).isPresent() || "".equals(format)) {
            return ErrorRespCode.STRING_NULL_ERROR;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    /**
     * 将Date改成指定Date类型
     */
    public static Date formatDate(Date date, String format) {
        Date newDate = null;
        try {
            //判断日期格式是否为空
            if (!Optional.ofNullable(format).isPresent() || "".equals(format)) {
                newDate = ErrorRespCode.DATE_NULL_ERROR;
            }
            //判断日期字符串是否为空,为空则取当前日期
            if (!Optional.ofNullable(date).isPresent()) {
                newDate = now(format);
            } else {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
                newDate = simpleDateFormat.parse(dateToStr(date, format));
            }
        } catch (ParseException e) {
            log.error("将Date改成指定Date类型解析错误:" + e);
            e.printStackTrace();
        }
        return newDate;
    }

    /**
     * 将字符串时间改成Date类型
     */
    public static Date strToDate(String dateStr, String format) throws ParseException {
        //判断日期字符串和日期格式是否为空
        if (!Optional.ofNullable(dateStr).isPresent() || "".equals(dateStr)
                || !Optional.ofNullable(format).isPresent() || "".equals(format)) {
            return ErrorRespCode.DATE_NULL_ERROR;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date date = simpleDateFormat.parse(dateStr);
        return date;
    }

    /**
     * 将字符串时间改成指定格式化类型
     */
    public static String toStringDate(String dateStr, String format) {
        String result = "";
        //判断日期字符串和日期格式是否为空
        if (!Optional.ofNullable(dateStr).isPresent() || "".equals(dateStr)
                || !Optional.ofNullable(format).isPresent() || "".equals(format)) {
            return ErrorRespCode.STRING_NULL_ERROR;
        }
        try {
            Date date = new SimpleDateFormat(format).parse(dateStr);
            result = new SimpleDateFormat(format).format(date);
        } catch (ParseException e) {
            log.error("将字符串时间改成指定格式化类型解析错误:" + e);
            e.printStackTrace();
        }
        return result;
    }

    /***
     * 计算两个日期相差多少秒
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int getTimeDelta(Date date1, Date date2) {
        //判断日期字符串和日期格式是否为空
        if (!Optional.ofNullable(date1).isPresent()
                || !Optional.ofNullable(date2).isPresent()) {
            return ErrorRespCode.INTEGER_NULL_ERROR;
        }
        //单位是秒
        long timeDelta = (date1.getTime() - date2.getTime()) / 1000;
        int secondsDelta = timeDelta > 0 ? (int) timeDelta : (int) Math.abs(timeDelta);
        return secondsDelta;
    }

    /**
     * 计算指定时间与当前时间相差多少秒
     *
     * @param date 指定时间
     * @return
     */
    public static int getTimeDelta(Date date) {
        //判断日期字符串和日期格式是否为空
        if (!Optional.ofNullable(date).isPresent()) {
            return ErrorRespCode.INTEGER_NULL_ERROR;
        }
        return getTimeDelta(new Date(), date);
    }

    /**
     * 获取指定时间与当前时间相差的时间，输出格式为xx天xx小时xx分钟xx秒
     *
     * @param date
     * @return
     */
    public static String getTimeDeltaToString(Date date) {
        //判断日期字符串和日期格式是否为空
        if (!Optional.ofNullable(date).isPresent()) {
            return ErrorRespCode.STRING_NULL_ERROR;
        }
        int diff = getTimeDelta(date);
        long days = diff / (60 * 60 * 24);
        long hours = (diff - days * (60 * 60 * 24)) / (60 * 60);
        long minutes = (diff - days * (60 * 60 * 24) - hours * (60 * 60)) / 60;
        long seconds = (diff - days * (60 * 60 * 24) - hours * (60 * 60) - minutes * 60);
        StringBuilder result = new StringBuilder();
        if (days > 0) {
            result.append(days).append("天");
        }
        if (hours > 0) {
            result.append(hours).append("小时");
        }
        if (minutes > 0) {
            result.append(minutes).append("分");
        }
        if (seconds > 0) {
            result.append(seconds).append("秒");
        }
        return result.toString();
    }

    /**
     * 获取现在的时间
     *
     * @return Date
     */
    public static Date now() {
        return Calendar.getInstance().getTime();
    }

    /**
     * 获取现在的时间并以指定格式输出
     *
     * @param format 时间格式
     * @return Date
     */
    public static Date now(String format) throws ParseException {
        //判断日期格式是否为空
        if (!Optional.ofNullable(format).isPresent() || "".equals(format)) {
            return ErrorRespCode.DATE_NULL_ERROR;
        }
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return strToDate(formatter.format(now()), format);
    }

    /**
     * 获取指定日期是星期几的代码
     *
     * @param dt
     * @return 0=星期天
     */
    public static int getWeekCodeOfDate(Date dt) {
        //判断日期是否为空
        if (!Optional.ofNullable(dt).isPresent()) {
            return ErrorRespCode.INTEGER_NULL_ERROR;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        return w < 0 ? 0 : w;
    }

    /**
     * 获取指定日期是星期几
     *
     * @param dt
     * @return
     */
    public static String getWeekOfDate(Date dt) {
        //判断日期是否为空
        if (!Optional.ofNullable(dt).isPresent()) {
            return ErrorRespCode.STRING_NULL_ERROR;
        }
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        return weekDays[getWeekCodeOfDate(dt)];
    }

    /**
     * 得到指定时间与当前时间之间相差的天数
     *
     * @param start 指定时间
     * @return 指定时间与当前时间之间相差的天数
     */
    public static long betweenDays(Date start) {
        //判断日期是否为空
        if (!Optional.ofNullable(start).isPresent()) {
            return ErrorRespCode.INTEGER_NULL_ERROR;
        }
        LocalDate startDateInclusive = dateToLocalDate(start);
        return LocalDate.now().toEpochDay() - startDateInclusive.toEpochDay();
    }

    /**
     * 得到指定时间与当前时间之间相差的天数
     *
     * @param startDate 指定时间
     * @return 指定时间与当前时间之间相差的天数
     */
    public static long betweenDays(String startDate) throws ParseException {
        //判断日期是否为空
        if (startDate == null || "".equals(startDate)) {
            return ErrorRespCode.INTEGER_NULL_ERROR;
        }
        Date start = strToDate(startDate, "yyyy-MM-dd");
        LocalDate startDateInclusive = dateToLocalDate(start);
        return LocalDate.now().toEpochDay() - startDateInclusive.toEpochDay();
    }

    /**
     * 得到两个之间之间相差的天数
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 开始时间与结束时间相差的天数
     */
    public static long betweenDays(Date start, Date end) {
        //判断日期是否为空
        if (!Optional.ofNullable(start).isPresent()
                || !Optional.ofNullable(end).isPresent()) {
            return ErrorRespCode.INTEGER_NULL_ERROR;
        }
        LocalDate startDateInclusive = dateToLocalDate(start);
        LocalDate endDateExclusive = dateToLocalDate(end);
        return endDateExclusive.toEpochDay() - startDateInclusive.toEpochDay();
    }

    /**
     * 得到两个之间之间相差的天数
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 开始时间与结束时间相差的天数
     */
    public static long betweenDays(String startDate, String endDate) throws ParseException {
        //判断日期是否为空
        if (!Optional.ofNullable(startDate).isPresent() || "".equals(startDate)
                || !Optional.ofNullable(endDate).isPresent() || "".equals(endDate)) {
            return ErrorRespCode.INTEGER_NULL_ERROR;
        }
        Date start = strToDate(startDate, "yyyy-MM-dd");
        Date end = strToDate(endDate, "yyyy-MM-dd");
        LocalDate startDateInclusive = dateToLocalDate(start);
        LocalDate endDateExclusive = dateToLocalDate(end);
        return endDateExclusive.toEpochDay() - startDateInclusive.toEpochDay();
    }

    public static LocalDate dateToLocalDate(Date date) {
        //判断日期是否为空
        if (!Optional.ofNullable(date).isPresent()) {
            return ErrorRespCode.LOCALDATE_NULL_ERROR;
        }
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * 获取在参考时间的基础上增加或者减少秒数后的时间
     *
     * @param date         参考时间
     * @param secondAmount 秒数，如果是减少秒数传一个负数
     * @return 格式为 yyyy-MM-dd HH:mm:ss 的新时间
     */
    public static Date getDateBySecond(Date date, int secondAmount) {
        //判断日期是否为空
        if (!Optional.ofNullable(date).isPresent()) {
            return ErrorRespCode.DATE_NULL_ERROR;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, secondAmount);
        return formatDate(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 在当前日期上增加指定的天数
     *
     * @param day 天数
     * @return 增加天数后的日期
     */
    public static Date addDay(int day) {
        return addDay(now(), day);
    }

    /**
     * 判断时间是否在时间段内
     *
     * @param nowTime
     * @param beginTime
     * @param endTime
     * @return
     */
    public static boolean belongCalendar(Date nowTime, Date beginTime, Date endTime) {
        //判断日期是否为空
        if (!Optional.ofNullable(nowTime).isPresent() || !Optional.ofNullable(beginTime).isPresent()
                || !Optional.ofNullable(endTime).isPresent()) {
            return false;
        }
        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(beginTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        return date.after(begin) && date.before(end);
    }

    /**
     * 在指定日期上加指定的天数
     *
     * @param date 基础日期
     * @param day  天数
     * @return 增加天数后的日期
     */
    public static Date addDay(Date date, int day) {
        //判断日期是否为空
        if (!Optional.ofNullable(date).isPresent()) {
            return ErrorRespCode.DATE_NULL_ERROR;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, day);
        return c.getTime();
    }

    /**
     * 在指定日期上加指定的分钟数
     *
     * @param date   基础日期
     * @param minute 分钟数
     * @return 增加分钟数后的日期
     */
    public static Date addMinute(Date date, int minute) {
        //判断日期是否为空
        if (!Optional.ofNullable(date).isPresent()) {
            return ErrorRespCode.DATE_NULL_ERROR;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MINUTE, minute);
        return c.getTime();
    }

    /**
     * 将时间转换为时间戳
     *
     * @param dateStr
     * @return
     * @throws ParseException
     */
    public static String dateToStamp(String dateStr) throws ParseException {
        //判断日期字符串是否为空
        if (!Optional.ofNullable(dateStr).isPresent() || "".equals(dateStr)) {
            return ErrorRespCode.STRING_NULL_ERROR;
        }
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(dateStr);
        long ts = date.getTime();
        res = String.valueOf(ts);
        return res;
    }

    /**
     * 将时间戳转换为时间
     *
     * @param time 时间戳
     * @return 日期格式的时间
     */
    public static Date stampToDate(Long time) {
        //判断时间戳是否为空
        if (!Optional.ofNullable(time).isPresent()) {
            return ErrorRespCode.DATE_NULL_ERROR;
        }
        return stampToDate(time, DEFAULT_DATETIME_FORMAT);
    }

    /**
     * 将时间戳转换为时间
     *
     * @param time 时间戳
     * @return 日期格式的时间
     */
    public static Date stampToDate(Long time, String format) {
        //判断时间戳和日期格式是否为空
        if (!Optional.ofNullable(time).isPresent()
                || !Optional.ofNullable(format).isPresent() || "".equals(format)) {
            return ErrorRespCode.DATE_NULL_ERROR;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        String d = dateFormat.format(time);
        Date date = null;
        try {
            date = dateFormat.parse(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 将时间戳转换为时间
     *
     * @param timeStr
     * @return
     */
    public static String stampToDate(String timeStr) {
        //判断时间戳是否为空
        if (!Optional.ofNullable(timeStr).isPresent() || "".equals(timeStr)) {
            return ErrorRespCode.STRING_NULL_ERROR;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DEFAULT_DATETIME_FORMAT);
        Date date = new Date(Long.parseLong(timeStr));
        return simpleDateFormat.format(date);
    }

    /**
     * 获取当前日期的年月日
     *
     * @return
     */
    public static String getCurrentDate(String format) {
        //判断日期格式是否为空
        if (!Optional.ofNullable(format).isPresent() || "".equals(format)) {
            return ErrorRespCode.STRING_NULL_ERROR;
        }
        Date curdate = new Date();
        return dateToStr(curdate, format);
    }

    public static long betweenSecond(Date beginDate, Date endDate) throws Exception {
        if (!Optional.ofNullable(beginDate).isPresent()
                || !Optional.ofNullable(endDate).isPresent()) {
            throw new Exception("比较的时间不能为空");
        }

        ZoneId zoneId = ZoneId.systemDefault();
        Instant beginInstant = beginDate.toInstant();
        LocalDateTime beginLocal = beginInstant.atZone(zoneId).toLocalDateTime();
        Instant endInstant = endDate.toInstant();
        LocalDateTime endLocal = endInstant.atZone(zoneId).toLocalDateTime();
        return Duration.between(beginLocal, endLocal).getSeconds();
    }

    public static long betweenDays(int year, int month, int day) {
        LocalDate today = LocalDate.now();
        LocalDate localDate = LocalDate.of(year, month, day);
        // 直接使用日期类中的方法计算日期间隔天数
        long days = localDate.until(today, ChronoUnit.DAYS);
        return days;
    }

    public static long betweenWeeks(int year, int month, int day) {
        LocalDate today = LocalDate.now();
        LocalDate localDate = LocalDate.of(year, month, day);
        // 直接使用日期类中的方法计算日期间隔周数
        long weeks = localDate.until(today, ChronoUnit.WEEKS);
        return weeks;
    }

    public static long betweenMonths(int year, int month, int day) {
        LocalDate today = LocalDate.now();
        LocalDate localDate = LocalDate.of(year, month, day);
        // 直接使用日期类中的方法计算日期间隔月数
        long months = localDate.until(today, ChronoUnit.MONTHS);
        return months;
    }

    public static long betweenYears(int year, int month, int day) {
        LocalDate today = LocalDate.now();
        LocalDate localDate = LocalDate.of(year, month, day);
        // 直接使用日期类中的方法计算日期间隔年数
        long years = localDate.until(today, ChronoUnit.YEARS);
        return years;
    }

    public static void main(String[] args) throws Exception {
        /*Date now = DateUtils.now();
        Date timeoutBeginDate = DateUtils.getDateBySecond(now, 10 * -1);
        String date = DateUtils.dateToStr(timeoutBeginDate, DateUtils.DEFAULT_DATETIME_FORMAT);
        System.out.println(now);
        System.out.println(date);*/
        long days = betweenDays(2019, 6, 14);
        System.out.println(days);
        long days1 = betweenDays(2019, 6, 7);
        System.out.println(days1);
        long weeks = betweenWeeks(2019, 6, 8);
        System.out.println(weeks);
        long days2 = betweenDays(DateUtils.strToDate("2019-11-15 10:27:35", DateUtils.DEFAULT_DATETIME_FORMAT), DateUtils.now());
        System.out.println(days2);
    }
}
