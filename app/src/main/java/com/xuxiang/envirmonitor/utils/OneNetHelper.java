package com.xuxiang.envirmonitor.utils;

import com.chinamobile.iot.onenet.OneNetApi;
import com.chinamobile.iot.onenet.OneNetApiCallback;
import com.chinamobile.iot.onenet.http.Urls;

import okhttp3.HttpUrl;

/**
 * Created by hsiang on 2017/11/1.
 */

public class OneNetHelper extends OneNetApi {

    public static void init() {
        init(ContextUtils.context(), false, null);
    }

    public static void queryMultiDevices(String pParams, OneNetApiCallback pCallback) {
        get(OneNetHelper.urlForQueryingMultiDevices(pParams), pCallback);
    }

    public static void queryMultiDevicesData(String pParams, OneNetApiCallback pCallback) {
        get(OneNetHelper.urlForQueryingMultiDevicesData(pParams), pCallback);
    }

    private static String urlForQueryingMultiDevices(String pParams) {
        HttpUrl.Builder _builder = new HttpUrl.Builder()
                .scheme(Urls.sScheme).host(Urls.sHost).addPathSegment("devices").addPathSegment("status");
        if (pParams != null)
            _builder.addEncodedQueryParameter("devIds", pParams);
        return _builder.toString();
    }

    private static String urlForQueryingMultiDevicesData(String pParams) {
        HttpUrl.Builder _builder = new HttpUrl.Builder()
                .scheme(Urls.sScheme).host(Urls.sHost).addPathSegment("devices").addPathSegment("datapoints");
        if (pParams != null)
            _builder.addEncodedQueryParameter("devIds", pParams);
        return _builder.toString();
    }
}
