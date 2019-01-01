package com.xuxiang.envirmonitor.model;

import java.util.List;

/**
 * Created by hsiang on 2017/11/3.
 */

public class FuzzyDevIdBean {

    /**
     * code : 0
     * data : {"devices":[{"act_time":"2018-12-19 16:44:37","auth_info":"12E171029006","create_time":"2017-10-29 16:08:51","desc":"","id":"20128886","last_login":"2018-12-20 08:07:30","location":{"lat":0,"lon":0},"online":false,"private":false,"protocol":"MQTT","tags":[],"title":"测试9"},{"auth_info":"12E171029005","create_time":"2017-10-29 16:08:39","desc":"","id":"20128883","location":{"lat":0,"lon":0},"online":false,"private":false,"protocol":"MQTT","tags":[],"title":"测试8"},{"auth_info":"12E171029004","create_time":"2017-10-29 16:08:28","desc":"","id":"20128882","location":{"lat":0,"lon":0},"online":false,"private":false,"protocol":"MQTT","tags":[],"title":"测试7"},{"auth_info":"12E171029003","create_time":"2017-10-29 16:08:07","desc":"","id":"20128880","location":{"lat":0,"lon":0},"online":false,"private":false,"protocol":"MQTT","tags":[],"title":"测试6"},{"auth_info":"12E171029002","create_time":"2017-10-29 16:07:45","desc":"","id":"20128877","location":{"lat":0,"lon":0},"online":false,"private":false,"protocol":"MQTT","tags":[],"title":"测试5"},{"auth_info":"12E171029001","create_time":"2017-10-29 16:07:32","desc":"","id":"20128874","location":{"lat":0,"lon":0},"online":false,"private":false,"protocol":"MQTT","tags":[],"title":"测试4"},{"auth_info":"12E171016001","create_time":"2017-10-16 22:27:30","desc":"","id":"19916638","location":{"lat":0,"lon":0},"online":false,"private":false,"protocol":"MQTT","tags":[],"title":"测试3"},{"act_time":"2018-12-19 10:49:34","auth_info":"12E170911001","create_time":"2017-09-11 17:28:53","desc":"","id":"13629870","last_login":"2018-12-23 17:46:34","location":{"lat":0,"lon":0},"online":false,"private":false,"protocol":"MQTT","tags":[],"title":"测试2"},{"act_time":"2018-12-14 11:34:50","auth_info":"12E170910001","create_time":"2017-09-10 16:52:42","desc":"","id":"13606144","last_login":"2018-12-23 21:14:08","location":{"lat":0,"lon":0},"online":true,"private":false,"protocol":"MQTT","tags":[],"title":"测试1"}],"page":1,"per_page":30,"total_count":9}
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
         * devices : [{"act_time":"2018-12-19 16:44:37","auth_info":"12E171029006","create_time":"2017-10-29 16:08:51","desc":"","id":"20128886","last_login":"2018-12-20 08:07:30","location":{"lat":0,"lon":0},"online":false,"private":false,"protocol":"MQTT","tags":[],"title":"测试9"},{"auth_info":"12E171029005","create_time":"2017-10-29 16:08:39","desc":"","id":"20128883","location":{"lat":0,"lon":0},"online":false,"private":false,"protocol":"MQTT","tags":[],"title":"测试8"},{"auth_info":"12E171029004","create_time":"2017-10-29 16:08:28","desc":"","id":"20128882","location":{"lat":0,"lon":0},"online":false,"private":false,"protocol":"MQTT","tags":[],"title":"测试7"},{"auth_info":"12E171029003","create_time":"2017-10-29 16:08:07","desc":"","id":"20128880","location":{"lat":0,"lon":0},"online":false,"private":false,"protocol":"MQTT","tags":[],"title":"测试6"},{"auth_info":"12E171029002","create_time":"2017-10-29 16:07:45","desc":"","id":"20128877","location":{"lat":0,"lon":0},"online":false,"private":false,"protocol":"MQTT","tags":[],"title":"测试5"},{"auth_info":"12E171029001","create_time":"2017-10-29 16:07:32","desc":"","id":"20128874","location":{"lat":0,"lon":0},"online":false,"private":false,"protocol":"MQTT","tags":[],"title":"测试4"},{"auth_info":"12E171016001","create_time":"2017-10-16 22:27:30","desc":"","id":"19916638","location":{"lat":0,"lon":0},"online":false,"private":false,"protocol":"MQTT","tags":[],"title":"测试3"},{"act_time":"2018-12-19 10:49:34","auth_info":"12E170911001","create_time":"2017-09-11 17:28:53","desc":"","id":"13629870","last_login":"2018-12-23 17:46:34","location":{"lat":0,"lon":0},"online":false,"private":false,"protocol":"MQTT","tags":[],"title":"测试2"},{"act_time":"2018-12-14 11:34:50","auth_info":"12E170910001","create_time":"2017-09-10 16:52:42","desc":"","id":"13606144","last_login":"2018-12-23 21:14:08","location":{"lat":0,"lon":0},"online":true,"private":false,"protocol":"MQTT","tags":[],"title":"测试1"}]
         * page : 1
         * per_page : 30
         * total_count : 9
         */

        private int total_count;
        private List<DevicesBean> devices;

        public int getTotal_count() {
            return total_count;
        }

        public List<DevicesBean> getDevices() {
            return devices;
        }

        public static class DevicesBean {
            /**
             * act_time : 2018-12-19 16:44:37
             * auth_info : 12E171029006
             * create_time : 2017-10-29 16:08:51
             * desc :
             * id : 20128886
             * last_login : 2018-12-20 08:07:30
             * location : {"lat":0,"lon":0}
             * online : false
             * private : false
             * protocol : MQTT
             * tags : []
             * title : 测试9
             */

            private String auth_info;
            private String id;
            private String last_login;
            private boolean online;
            private String title;

            public String getAuth_info() {
                return auth_info;
            }

            public String getId() {
                return id;
            }

            public String getLast_login() {
                return last_login;
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
