package com.xuxiang.envirmonitor.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hsiang on 2017/11/3.
 */

public class GsonHelper extends BaseJson {
    private Gson mGson = new Gson();

    @Override
    public String toJson(Object pSrc) {
        return mGson.toJson(pSrc);
    }

    @Override
    public <T> T toObject(String pJson, Class<T> pClass) {
        return mGson.fromJson(pJson, pClass);
    }

    @Override
    public <T> T toObject(byte[] pBytes, Class<T> pClass) {
        return mGson.fromJson(new String(pBytes), pClass);
    }

    @Override
    public <T> List<T> toList(String pJson, Class<T> pClass) {
        Type _type = new TypeToken<ArrayList<T>>() {
        }.getType();
        List<T> _list = mGson.fromJson(pJson, _type);
        return _list;
    }
}