package com.xuxiang.envirmonitor.utils;

import java.util.List;

/**
 * Created by hsiang on 2017/11/3.
 */

public abstract class BaseJson {
    private static BaseJson sBaseJson;

    BaseJson() {
    }

    public static BaseJson get() {
        if (sBaseJson == null)
            sBaseJson = new GsonHelper();
        return sBaseJson;
    }

    public abstract String toJson(Object pSrc);

    public abstract <T> T toObject(String pJson, Class<T> pClass);

    public abstract <T> T toObject(byte[] pBytes, Class<T> pClass);

    public abstract <T> List<T> toList(String pJson, Class<T> pClass);
}