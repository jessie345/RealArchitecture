package com.architecture.realarchitecture.manager;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ArrayRes;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import com.architecture.realarchitecture.MainApp;

/**
 * Created by liushuo on 16/5/11.
 */
public class ResourceManager {

    public static Resources getResource() {
        Context context = MainApp.getAppContext();
        if (context == null) return null;

        return context.getResources();
    }

    public static String getString(@StringRes int resId) {
        Resources res = getResource();
        if (res == null) return "";

        return res.getString(resId);
    }

    public static String getString(@StringRes int resId, Object... formatArgs) {
        Resources res = getResource();
        if (res == null) return "";

        return res.getString(resId, formatArgs);
    }

    public static String[] getStringArray(@ArrayRes int resId) {
        Resources res = getResource();
        if (res == null) return new String[0];

        return res.getStringArray(resId);
    }

    public static float getDimention(@DimenRes int resId) {
        Resources res = getResource();
        if (res == null) return -1;

        return res.getDimension(resId);
    }

    @ColorInt
    public static int getColor(@ColorRes int resId) {
        Resources res = getResource();
        if (res == null) return -1;

        return res.getColor(resId);
    }

    public static Drawable getDrawable(@DrawableRes int resId) {
        Resources res = getResource();
        if (res == null) return new BitmapDrawable();

        return res.getDrawable(resId);

    }
}
