package com.example.apptracking.data.model;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.example.apptracking.broadcastreceiver.AlarmReceiver;
import com.example.apptracking.ui.activity.main.MainActivity;
import java.io.Serializable;
import java.util.Calendar;

@Entity(tableName = "appUsageLimit")
public class AppUsageLimit extends App implements Serializable {

    protected long usageTimeOfDay;
    private long usageTimeLimit = 0;
    private int warningType;
    private String textDisplayed;

    public AppUsageLimit(String name, String packageName) {
        super(name, packageName);
    }

    public long getUsageTimeLimit() {
        return usageTimeLimit;
    }

    public void setUsageTimeLimit(long usageTimeLimit) {
        this.usageTimeLimit = usageTimeLimit;

    }

    public String getTextDisplayed() {
        return textDisplayed;
    }

    public void setTextDisplayed(String textDisplayed) {
        this.textDisplayed = textDisplayed;
    }

    public int getWarningType() {
        return warningType;
    }

    public void setWarningType(int warningType) {
        this.warningType = warningType;
    }

    public long getUsageTimeOfDay() {
        return usageTimeOfDay;
    }

    public void setUsageTimeOfDay(long usageTimeOfDay) {
        this.usageTimeOfDay = usageTimeOfDay;
    }


    public void setTime(Context context, AlarmManager manager, long timeMillis) {
        this.usageTimeLimit = timeMillis;
        //set(context, manager);
    }

//    @Nullable
//    public Calendar getNext() {
//        Calendar next = Calendar.getInstance();
//        next.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
//        next.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
//        next.set(Calendar.SECOND, 0);
//
//        return next;
//
//    }
//
//    public void set(Context context, AlarmManager manager) {
//        Calendar nextTime = getNext();
//        if (nextTime != null) {
//            setAlarm(context, manager, nextTime.getTimeInMillis());
//            return nextTime.getTime();
//        } else {
//           return time.getTime();
//        }
//    }

    public void setAlarm(Context context, AlarmManager manager) {
        manager.setAlarmClock(
                new AlarmManager.AlarmClockInfo(
                        Calendar.getInstance().getTimeInMillis() + this.usageTimeLimit - this.usageTimeOfDay,
                        PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0)
                ),
                getIntent(context)
        );

//        manager.set(AlarmManager.RTC_WAKEUP,
//                timeMillis - (long) PreferenceData.SLEEP_REMINDER_TIME.getValue(context),
//                PendingIntent.getService(context, 0, new Intent(context, SleepReminderService.class), 0));
//
//        SleepReminderService.refreshSleepTime(context);
    }

    public void cancel(Context context, AlarmManager manager) {
        manager.cancel(getIntent(context));

    }

    private PendingIntent getIntent(Context context) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(AlarmReceiver.EXTRA_ALARM_ID, this);
//        intent.putExtra(AlarmReceiver.EXTRA_ALARM_ID, this.name);
        return PendingIntent.getBroadcast(context, this.name.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
