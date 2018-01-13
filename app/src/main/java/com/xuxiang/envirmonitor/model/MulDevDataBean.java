package com.xuxiang.envirmonitor.model;

import java.util.List;

/**
 * Created by hsiang on 2017/11/3.
 */

public class MulDevDataBean {

    /**
     * errno : 0
     * data : {"devices":[{"title":"监控","id":"13606144","datastreams":[{"at":"2017-11-01 13:10:39","id":"temperature","value":26.9},{"at":"2017-11-01 13:10:39","id":"humidity","value":35.3},{"at":"2017-11-01 13:10:39","id":"RSSI","value":71},{"at":"2017-11-01 13:10:39","id":"voltage","value":214}]}]}
     * error : succ
     */

    private int errno;
    private DataBean data;
    private String error;

    public int getErrno() {
        return errno;
    }

    public void setErrno(int errno) {
        this.errno = errno;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public static class DataBean {
        private List<DevicesBean> devices;

        public List<DevicesBean> getDevices() {
            return devices;
        }

        public void setDevices(List<DevicesBean> devices) {
            this.devices = devices;
        }

        public static class DevicesBean {
            /**
             * title : 监控
             * id : 13606144
             * datastreams : [{"at":"2017-11-01 13:10:39","id":"temperature","value":26.9},{"at":"2017-11-01 13:10:39","id":"humidity","value":35.3},{"at":"2017-11-01 13:10:39","id":"RSSI","value":71},{"at":"2017-11-01 13:10:39","id":"voltage","value":214}]
             */

            private String title;
            private String id;
            private List<DatastreamsBean> datastreams;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public List<DatastreamsBean> getDatastreams() {
                return datastreams;
            }

            public void setDatastreams(List<DatastreamsBean> datastreams) {
                this.datastreams = datastreams;
            }

            public static class DatastreamsBean {
                /**
                 * at : 2017-11-01 13:10:39
                 * id : temperature
                 * value : 26.9
                 */

                private String at;
                private String id;
                private double value;

                public String getAt() {
                    return at;
                }

                public void setAt(String at) {
                    this.at = at;
                }

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public double getValue() {
                    return value;
                }

                public void setValue(double value) {
                    this.value = value;
                }
            }
        }
    }
}
