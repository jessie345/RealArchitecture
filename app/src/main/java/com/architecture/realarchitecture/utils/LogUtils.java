package com.architecture.realarchitecture.utils;

import android.util.Log;

import com.architecture.realarchitecture.AppConfig;

/**
 * Author: xumin
 * Date: 16/5/25
 */
public class LogUtils {
    public static String tag = AppConfig.LOG_TAG;
    public static boolean isDebug = AppConfig.LOG_ENABLED;
    private static String SEPARATOR = " ⇢ ";

    private LogUtils() {
    }

    private static String generateTrace() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String trace = stackTrace[4].toString();

        return trace;
    }

    public static void trace() {
        if (!isDebug) return;

        String trace = generateTrace();
        String message = "*";
        Log.d(tag, message + SEPARATOR + trace);
    }

    public static void v(Object content) {
        if (!isDebug) return;

        String trace = generateTrace();
        String message = content == null ? "null" : content.toString();
        Log.v(tag, message + SEPARATOR + trace);
    }

    public static void v(Throwable tr) {
        if (!isDebug) return;

        String trace = generateTrace();
        String message = tr == null ? "null" : tr.getMessage();
        Log.v(tag, message + SEPARATOR + trace, tr);
    }

    public static void v(Object content, Throwable tr) {
        if (!isDebug) return;

        String trace = generateTrace();
        String message = content == null ? "null" : content.toString();
        Log.v(tag, message + SEPARATOR + trace, tr);
    }

    public static void d(Object content) {
        if (!isDebug) return;

        String trace = generateTrace();
        String message = content == null ? "null" : content.toString();
        Log.d(tag, message + SEPARATOR + trace);
    }

    public static void d(Throwable tr) {
        if (!isDebug) return;

        String trace = generateTrace();
        String message = tr == null ? "null" : tr.getMessage();
        Log.d(tag, message + SEPARATOR + trace, tr);
    }

    public static void d(Object content, Throwable tr) {
        if (!isDebug) return;

        String trace = generateTrace();
        String message = content == null ? "null" : content.toString();
        Log.d(tag, message + SEPARATOR + trace, tr);
    }

    public static void i(Object content) {
        if (!isDebug) return;

        String trace = generateTrace();
        String message = content == null ? "null" : content.toString();
        Log.i(tag, message + SEPARATOR + trace);
    }

    public static void i(Throwable tr) {
        if (!isDebug) return;

        String trace = generateTrace();
        String message = tr == null ? "null" : tr.getMessage();
        Log.i(tag, message + SEPARATOR + trace, tr);
    }

    public static void i(Object content, Throwable tr) {
        if (!isDebug) return;

        String trace = generateTrace();
        String message = content == null ? "null" : content.toString();
        Log.i(tag, message + SEPARATOR + trace, tr);
    }

    public static void w(Object content) {
        if (!isDebug) return;

        String trace = generateTrace();
        String message = content == null ? "null" : content.toString();
        Log.w(tag, message + SEPARATOR + trace);
    }

    public static void w(Throwable tr) {
        if (!isDebug) return;

        String trace = generateTrace();
        String message = tr == null ? "null" : tr.getMessage();
        Log.w(tag, message + SEPARATOR + trace, tr);
    }

    public static void w(Object content, Throwable tr) {
        if (!isDebug) return;

        String trace = generateTrace();
        String message = content == null ? "null" : content.toString();
        Log.w(tag, message + SEPARATOR + trace, tr);
    }

    public static void e(Object content) {
        if (!isDebug) return;

        String trace = generateTrace();
        String message = content == null ? "null" : content.toString();
        Log.e(tag, message + SEPARATOR + trace);
    }

    public static void e(Throwable tr) {
        if (!isDebug) return;

        String trace = generateTrace();
        String message = tr == null ? "null" : tr.getMessage();
        Log.e(tag, message + SEPARATOR + trace, tr);
    }

    public static void e(Object content, Throwable tr) {
        if (!isDebug) return;

        String trace = generateTrace();
        String message = content == null ? "null" : content.toString();
        Log.e(tag, message + SEPARATOR + trace, tr);
    }

    public static void wtf(Object content) {
        if (!isDebug) return;

        String trace = generateTrace();
        String message = content == null ? "null" : content.toString();
        Log.wtf(tag, message + SEPARATOR + trace);
    }

    public static void wtf(Throwable tr) {
        if (!isDebug) return;

        String trace = generateTrace();
        String message = tr == null ? "null" : tr.getMessage();
        Log.wtf(tag, message + SEPARATOR + trace, tr);
    }

    public static void wtf(Object content, Throwable tr) {
        if (!isDebug) return;

        String trace = generateTrace();
        String message = content == null ? "null" : content.toString();
        Log.wtf(tag, message + SEPARATOR + trace, tr);
    }
}