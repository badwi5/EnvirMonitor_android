package com.xuxiang.envirmonitor.model;

import java.util.List;

/**
 * Created by hsiang on 2017/11/3.
 */

public class MulDevDataBean {

    /**
     * code : 0
     * data : {"devices":[{"datastreams":[{"at":"2018-12-23 21:50:00","id":"RSSI","value":66},{"at":"2018-12-23 21:50:00","id":"Humi","value":33.4},{"at":"2018-12-23 21:50:00","id":"Volt","value":220},{"at":"2018-12-23 21:50:00","id":"State","value":"online"},{"at":"2018-12-23 21:50:00","id":"Temp","value":14.6}],"id":"13606144","title":"测试1"}]}
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
        private List<DevicesBean> devices;

        public List<DevicesBean> getDevices() {
            return devices;
        }

        public static class DevicesBean {
            /**
             * datastreams : [{"at":"2018-12-23 21:50:00","id":"RSSI","value":66},{"at":"2018-12-23 21:50:00","id":"Humi","value":33.4},{"at":"2018-12-23 21:50:00","id":"Volt","value":220},{"at":"2018-12-23 21:50:00","id":"State","value":"online"},{"at":"2018-12-23 21:50:00","id":"Temp","value":14.6}]
             * id : 13606144
             * title : 测试1
             */

            private String title;
            private String id;
            private List<DatastreamsBean> datastreams;

            public String getTitle() {
                return title;
            }

            public String getId() {
                return id;
            }

            public List<DatastreamsBean> getDatastreams() {
                return datastreams;
            }

            public static class DatastreamsBean {
                /**
                 * at : 2018-12-23 21:50:00
                 * id : RSSI
                 * value : 66
                 */

                private String at;
                private String id;
                private String value;

                public String getAt() {
                    return at;
                }

                public String getId() {
                    return id;
                }

                public String getValue() {
                    return value;
                }
            }
        }
    }
}
