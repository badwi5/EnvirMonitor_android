package com.xuxiang.envirmonitor.utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Handler;
import android.widget.Toast;

/**
 * Created by hsiang on 2017/11/2.
 */

public class ToastHelper {
    @SuppressLint("StaticFieldLeak")
    private static Application sContext;

    public enum Message {
        LOGINFAILED, SUCCESS, FAILED, NO_DEVICE, NO_DATA, UNLOGIN, UNAVAILABLE, EXIT
    }

    private static final String[] STRINGS = {
            "登陆失败", "刷新成功", "刷新失败", "暂无产品信息", "暂无数据", "用户未登录", "功能暂未开放", "再按一次退出"
    };

    public static void init() {
        sContext = ContextUtils.context();
    }

    public static void makeText(Message pMessage) {
        showToast(STRINGS[pMessage.ordinal()]);
    }

    public static void makeText(Exception e) {
        showToast(e.toString());
    }

    public static void makeText(String string) {
        showToast(string);
    }

    private static Toast sToast;
    private static Handler sHandler = new Handler();
    private static Runnable sRunnable = new Runnable() {
        public void run() {
            sToast.cancel();
        }

    };

    private static void showToast(String pText) {
        sHandler.removeCallbacks(sRunnable);
        if (null != sToast) {
            sToast.setText(pText);
        } else {
            sToast = Toast.makeText(sContext, pText, Toast.LENGTH_SHORT);
        }
        sHandler.postDelayed(sRunnable, 5000);
        sToast.show();
    }
}
