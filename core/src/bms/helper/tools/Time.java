package bms.helper.tools;

import java.util.Calendar;
import java.io.Serializable;

public class Time implements Serializable {
    private Calendar calendar;
    private static final long serialVersionUID = 1L;

    public Time() {
        calendar = Calendar.getInstance();
    }

    public Time(long f) {
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(f);
    }

    @Override
    public Object clone() {
        Time clone = new Time();
        clone.setTime(getYear(), getMonth(), getDay(), getHours(), getMinutes(), getSeconds());
        clone.getCalendar().set(Calendar.MILLISECOND, getMillisecond());
        return clone;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public long getTime() {
        return calendar.getTimeInMillis();
    }

    public int getDay() {
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public int getDayOfYear() {
        return calendar.get(Calendar.DAY_OF_YEAR);
    }

    public int getHours() {
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public int getMinutes() {
        return calendar.get(Calendar.MINUTE);
    }

    public int getMonth() {
        return calendar.get(Calendar.MONTH) + 1;
    }

    public int getSeconds() {
        return calendar.get(Calendar.SECOND);
    }

    public int getMillisecond() {
        return calendar.get(Calendar.MILLISECOND);
    }

    public int getYear() {
        return calendar.get(Calendar.YEAR);
    }

    public float getDisparityHours(Time time) {
        return Math.abs(getTime() - time.getTime()) / 1000 / 60 / 60;
    }

    public void setTime(long time) {
        calendar.setTimeInMillis(time);
    }

    public void setTime(int year, int mouth, int date, int hour, int min, int s) {
        calendar.set(year, mouth - 1, date, hour, min, s);
    }

    public void setTime(int mouth, int date, int hour, int min, int s) {
        setTime(getYear(), mouth, date, hour, min, s);
    }

    public void setTime(int date, int hour, int min, int s) {
        setTime(getMonth(), date, hour, min, s);
    }

    public void addDate(int m) {
        calendar.add(Calendar.DAY_OF_MONTH, m);
    }

    @Override
    public String toString() {
        return getYear() + "-" + getMonth() + "/" + getDay() + "-" + getHours() + "-" + getMinutes() + "-"
                + getSeconds();
    }

}
