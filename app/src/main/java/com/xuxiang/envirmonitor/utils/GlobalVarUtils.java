package com.xuxiang.envirmonitor.utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by hsiang on 2017/10/26.
 */

@SuppressLint("Registered")
public class GlobalVarUtils extends Application {

    //将平台端定义的数据流名称与应用中所用变量名对应
    public final static String Temp = "Temp";
    public final static String Humi = "Humi";
    public final static String Volt = "Volt";
    public final static String RSSI = "RSSI";

    private static String sApikey;
    private static int sDevCount;
    private static String sDevIds;
    private static SharedPreferences sSharedPrefs;

    public static String getApikey() {
        return sApikey;
    }

    public static void setApikey(String pApikey) {
        GlobalVarUtils.sApikey = pApikey;
    }

    public static int getDevCount() {
        return sDevCount;
    }

    public static void setDevCount(int pDevCount) {
        GlobalVarUtils.sDevCount = pDevCount;
    }

    public static String getDevIds() {
        return sDevIds;
    }

    public static void setDevIds(String pDevIds) {
        GlobalVarUtils.sDevIds = pDevIds;
    }

    public static void load() {
        sSharedPrefs = PreferenceManager.getDefaultSharedPreferences(ContextUtils.context());
        GlobalVarUtils.setApikey(sSharedPrefs.getString("apiKey", null));
        GlobalVarUtils.setDevCount(sSharedPrefs.getInt("devCount", 0));
        GlobalVarUtils.setDevIds(sSharedPrefs.getString("devIds", ""));
    }

    public static void save() {
        SharedPreferences.Editor _sharedPrefsEditor = sSharedPrefs.edit();
        _sharedPrefsEditor.putString("apiKey", GlobalVarUtils.getApikey());
        _sharedPrefsEditor.putInt("devCount", GlobalVarUtils.getDevCount());
        _sharedPrefsEditor.putString("devIds", GlobalVarUtils.getDevIds());
        _sharedPrefsEditor.apply();
    }
}