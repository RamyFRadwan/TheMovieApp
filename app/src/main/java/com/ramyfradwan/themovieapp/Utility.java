package com.ramyfradwan.themovieapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

///**
// * Created by RamyFRadwan on 07/10/15.
// */
public class Utility {
    public static final String YOUTUBE_PACKAGE_NAME = "com.google.android.youtube";
    public static Context context;

    public Utility(Context context) {
        Utility.context = context;
    }

    public static void preferPackageForIntent(Context context, Intent intent, String packageName) {
        PackageManager pm = context.getPackageManager();
        for (ResolveInfo resolveInfo : pm.queryIntentActivities(intent, 0)) {
            if (resolveInfo.activityInfo.packageName.equals(packageName)) {
                intent.setPackage(packageName);
                break;
            }
        }
    }

}
