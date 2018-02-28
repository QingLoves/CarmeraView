package com.example.team.carmeraview.util;

import android.util.Log;

/**
 * 日志打印工具类
 * Created by AwenZeng on 2016/12/2.
 */
public class LogUtil {
    private static final String TAG = LogUtil.class.getName();
    public static Boolean isDebug = true; // 日志文件总开关

    public static void w(String tag, Object msg) { // 警告信息
        log(tag,msg.toString(), 'w');
    }

    public static void e(String tag, Object msg) { // 错误信息
        log(tag,msg.toString(), 'e');
    }

    public static void d(String tag, Object msg) {// 调试信息
        log(tag,msg.toString(), 'd');
    }

    public static void i(String tag, Object msg) {//
        log(tag,msg.toString(), 'i');
    }

    public static void v(String tag, Object msg) {
        log(tag,msg.toString(), 'v');
    }

    public static void w(String tag, String text) {
        log(tag,text, 'w');
    }




    /**
     * Android原生log
     * 根据tmsg和等级，输出日志
     * @param msg
     * @param level
     * @return void
     */
    public static void log(String msg, char level) {
        if (isDebug) {
            if ('e' == level) { // 输出错误信息
                Log.e(TAG,msg);
            } else if ('w' == level) {
                Log.w(TAG,msg);
            } else if ('d' == level) {
                Log.d(TAG,msg);
            } else if ('i' == level) {
                Log.i(TAG,msg);
            } else {
                Log.v(TAG,msg);
            }
        }
    }

    public static void log (String tag,String msg,char level){
        if (isDebug) {
            if ('e' == level) { // 输出错误信息
                Log.e(tag,msg);
            } else if ('w' == level) {
                Log.w(tag,msg);
            } else if ('d' == level) {
                Log.d(tag,msg);
            } else if ('i' == level) {
                Log.i(tag,msg);
            } else {
                Log.v(tag,msg);
            }
        }
    }

    /**
     * Android原生log
     * @param msg
     */
    public static void androidLog(String msg) {
        if (isDebug) {
            Log.i(TAG,msg);
        }
    }

}
