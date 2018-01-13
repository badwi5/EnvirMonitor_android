package com.xuxiang.envirmonitor.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by hsiang on 2017/11/3.
 */

public class FuzzyDevIdBean {

    /**
     * errno : 0
     * data : {"per_page":30,"devices":[{"private":false,"protocol":"MQTT","create_time":"2017-09-10 16:52:42","online":true,"location":{"lat":0,"lon":0},"id":"13606144","auth_info":"id001","title":"监控","tags":[]}],"total_count":16,"page":1}
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
        /**
         * per_page : 30
         * devices : [{"private":false,"protocol":"MQTT","create_time":"2017-09-10 16:52:42","online":true,"location":{"lat":0,"lon":0},"id":"13606144","auth_info":"id001","title":"监控","tags":[]}]
         * total_count : 16
         * page : 1
         */

        private int per_page;
        private int total_count;
        private int page;
        private List<DevicesBean> devices;

        public int getPer_page() {
            return per_page;
        }

        public void setPer_page(int per_page) {
            this.per_page = per_page;
        }

        public int getTotal_count() {
            return total_count;
        }

        public void setTotal_count(int total_count) {
            this.total_count = total_count;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public List<DevicesBean> getDevices() {
            return devices;
        }

        public void setDevices(List<DevicesBean> devices) {
            this.devices = devices;
        }

        public static class DevicesBean {
            /**
             * private : false
             * protocol : MQTT
             * create_time : 2017-09-10 16:52:42
             * online : true
             * location : {"lat":0,"lon":0}
             * id : 13606144
             * auth_info : id001
             * title : 监控
             * tags : []
             */

            @SerializedName("private")
            private boolean privateX;
            private String protocol;
            private String create_time;
            private boolean online;
            private LocationBean location;
            private String id;
            private String auth_info;
            private String title;
            private List<?> tags;

            public boolean isPrivateX() {
                return privateX;
            }

            public void setPrivateX(boolean privateX) {
                this.privateX = privateX;
            }

            public String getProtocol() {
                return protocol;
            }

            public void setProtocol(String protocol) {
                this.protocol = protocol;
            }

            public String getCreate_time() {
                return create_time;
            }

            public void setCreate_time(String create_time) {
                this.create_time = create_time;
            }

            public boolean isOnline() {
                return online;
            }

            public void setOnline(boolean online) {
                this.online = online;
            }

            public LocationBean getLocation() {
                return location;
            }

            public void setLocation(LocationBean location) {
                this.location = location;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getAuth_info() {
                return auth_info;
            }

            public void setAuth_info(String auth_info) {
                this.auth_info = auth_info;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public List<?> getTags() {
                return tags;
            }

            public void setTags(List<?> tags) {
                this.tags = tags;
            }

            public static class LocationBean {
                /**
                 * lat : 0
                 * lon : 0
                 */

                private int lat;
                private int lon;

                public int getLat() {
                    return lat;
                }

                public void setLat(int lat) {
                    this.lat = lat;
                }

                public int getLon() {
                    return lon;
                }

                public void setLon(int lon) {
                    this.lon = lon;
                }
            }
        }
    }
}
