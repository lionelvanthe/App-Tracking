package com.example.apptracking.broadcastreceiver;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.apptracking.R;
import com.example.apptracking.data.local.UsageTime;
import com.example.apptracking.data.model.AppUsageLimit;
import com.example.apptracking.ui.activity.limit.LimitReachedActivity;
import com.example.apptracking.ui.activity.main.MainActivity;
import com.example.apptracking.utils.Const;
import com.example.apptracking.utils.Utils;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Bundle args = intent.getBundleExtra(Const.EXTRA_APP_USAGE_TIME_LIMIT);
        AppUsageLimit appUsageLimit = (AppUsageLimit) args.getSerializable(Const.APP_USAGE_TIME_LIMIT);

        UsageTime usageTime = UsageTime.getInstance(context);

        long endTime = System.currentTimeMillis();
        usageTime.getUsageTime(Utils.getStartTimeOfToday(), endTime);

        if (appUsageLimit.getUsageTimeLimit() - usageTime.geUsageTimeFollowPackageName(appUsageLimit.getPackageName()) > 2000) {
            appUsageLimit.setUsageTimeOfDay(usageTime.geUsageTimeFollowPackageName(appUsageLimit.getPackageName()));
            appUsageLimit.setAlarm(context, manager);
        } else {
            if (appUsageLimit.getWarningType() == 0) {
                sendNotification(context, appUsageLimit);
            } else {
                startLimitReachedActivity(context, appUsageLimit);
            }
        }
    }

    private void startLimitReachedActivity(Context context, AppUsageLimit appUsageLimit) {
        try {
            Intent ringer = new Intent(context, LimitReachedActivity.class);
            ringer.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ringer.putExtra(Const.EXTRA_APP_USAGE_TIME_LIMIT_TO_ALARM_ACTIVITY, appUsageLimit);
            context.startActivity(ringer);
        } catch (Exception e) {
            Log.d("Thenv", "startLimitReachedActivity: " + e);
        }

    }

    private void sendNotification(Context context, AppUsageLimit appUsageLimit) {
        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Const.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_app_foreground)
                .setContentIntent(pendingIntentForNotification(context))
                .setLargeIcon(Utils.drawableToBitmap(Utils.getPackageIcon(context, context.getPackageName())))
                .setContentTitle(context.getString(R.string.usage_limit_is_exceeded))
                .setContentText(context.getString(R.string.content_text_notification, appUsageLimit.getName(), Utils.formatMilliSeconds(appUsageLimit.getUsageTimeLimit())))
                .setOngoing(false)
                .setSubText(context.getString(R.string.sub_text_notification))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        manager.notify(1, builder.build());
    }

    private PendingIntent pendingIntentForNotification(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        return pendingIntent;
    }

}