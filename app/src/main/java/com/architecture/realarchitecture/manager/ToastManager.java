package com.architecture.realarchitecture.manager;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.widget.Toast;

import com.architecture.realarchitecture.MainApp;

/**
 * Author: xumin
 * Date: 16/4/24
 */
public class ToastManager {
    private static Context sContext = MainApp.getAppContext();
    private static Handler sHandler = new Handler(sContext.getMainLooper());
    private static Toast sToast;

    public static void show(final String message) {
        sHandler.post(new Runnable() {
            @Override
            public void run() {
                if (sToast != null) {
                    sToast.cancel();
                }

                sToast = Toast.makeText(sContext, message, Toast.LENGTH_SHORT);
                sToast.setGravity(Gravity.CENTER, 0, 0);
                sToast.show();
            }
        });
    }

    public static void show(int resId) {
        show(sContext.getString(resId));
    }

    // TODO: 2016/4/24 remove
    @Deprecated
    public static void show(Context context, int resId) {
        show(resId);
    }

    // TODO: 2016/4/24 remove
    @Deprecated
    public static void show(Context context, final String message) {
        show(message);
    }
}
