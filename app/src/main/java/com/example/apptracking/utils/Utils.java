package com.example.apptracking.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.core.content.res.ResourcesCompat;
import androidx.palette.graphics.Palette;

import com.example.apptracking.AppApplication;
import com.example.apptracking.R;
import com.example.apptracking.data.model.App;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Utils {
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static Drawable getPackageIcon(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        try {
            return manager.getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_launcher_foreground, context.getTheme());
    }

    public static String parsePackageName(PackageManager pckManager, String data) {
        ApplicationInfo applicationInformation;
        try {
            applicationInformation = pckManager.getApplicationInfo(data, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInformation = null;
        }
        return (String) (applicationInformation != null ? pckManager.getApplicationLabel(applicationInformation) : data);
    }

    public static boolean isInstalled(PackageManager packageManager, String packageName) {
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = packageManager.getApplicationInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return applicationInfo != null;
    }

    public static boolean isSystemApp(PackageManager manager, String packageName) {

        boolean isSystemApp = false;
        try {
            ApplicationInfo applicationInfo = manager.getApplicationInfo(packageName, 0);
            if (applicationInfo != null) {
                isSystemApp = (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0
                        || (applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return isSystemApp;
    }

    public static boolean openable(PackageManager packageManager, String packageName) {
        return packageManager.getLaunchIntentForPackage(packageName) != null;
    }

    public static String formatMilliSeconds(long milliSeconds) {
        long second = milliSeconds / 1000L;
        if (second < 60) {
            return String.format("%ss", second);
        } else if (second < 60 * 60) {
            return String.format("%sm %ss", second / 60, second % 60);
        } else {
            if ((second % (3600) % 60) == 0) {
                return String.format("%sh %sm", second / 3600, second % (3600) / 60);
            } else {
                return String.format("%sh %sm %ss", second / 3600, second % (3600) / 60, second % (3600) % 60);
            }
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static void getDomainColor(Context context, String packageName) {
        Bitmap bitmap = Utils.drawableToBitmap(Utils.getPackageIcon(context, packageName));
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                Palette.Swatch swatch = palette.getVibrantSwatch();
                if (swatch != null) {
                    AppApplication.getHashMapDomainColor().put(packageName, swatch.getRgb());
                } else {
                    AppApplication.getHashMapDomainColor().put(packageName, context.getResources().getColor(android.R.color.holo_blue_light));
                }
            }
        });
    }

    public static long getStartTimeOfToday() {
        long startTime;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startTime = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            startTime = calendar.getTimeInMillis();
        }

        return startTime;
    }

    public static String converterTimestampToDate(long timestamp) {
        DateFormat dateFormat  = new SimpleDateFormat("dd/MM/yyyy");
        // we create instance of the Date and pass milliseconds to the constructor
        // we create instance of the Date and pass milliseconds to the constructor
        Date date = new Date(timestamp);
        // now we format the res by using SimpleDateFormat
        // now we format the res by using SimpleDateFormat
        return dateFormat.format(date);
    }
}
