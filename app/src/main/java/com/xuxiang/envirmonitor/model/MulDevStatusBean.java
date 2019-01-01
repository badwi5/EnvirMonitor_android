package com.xuxiang.envirmonitor.model;

import java.util.List;

/**
 * Created by hsiang on 2017/11/3.
 */

public class MulDevStatusBean{

    /**
     * code : 0
     * data : {"devices":[{"id":"13606144","online":true,"title":"测试1"},{"id":"13629870","online":false,"title":"测试2"}],"total_count":2}
     * msg : succ
     */

    private int code;
    private DataBean data;
    private String msg;

    public int getCode() {
        return code;
    }

    public DataBean getData() {
        return data;
    }

    public String getMsg() {
        return msg;
    }

    public static class DataBean {
        /**
         * devices : [{"id":"13606144","online":true,"title":"测试1"},{"id":"13629870","online":false,"title":"测试2"}]
         * total_count : 2
         */

        private List<DevicesBean> devices;

        public List<DevicesBean> getDevices() {
            return devices;
        }

        public static class DevicesBean {
            /**
             * id : 13606144
             * online : true
             * title : 测试1
             */

            private String id;
            private boolean online;
            private String title;

            public String getId() {
                return id;
            }

            public boolean isOnline() {
                return online;
            }

            public String getTitle() {
                return title;
            }
        }
    }
}