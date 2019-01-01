package com.xuxiang.envirmonitor.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hsian on 2017/11/13.
 */

public class SinDevDataBean {

    /**
     * code : 0
     * data : {"count":12,"datastreams":[{"datapoints":[{"at":"2019-01-01 01:03:53.428","value":55},{"at":"2019-01-01 01:00:53.928","value":55},{"at":"2019-01-01 00:57:54.428","value":55}],"id":"RSSI"},{"datapoints":[{"at":"2019-01-01 01:03:53.429","value":22.8},{"at":"2019-01-01 01:00:53.928","value":22.5},{"at":"2019-01-01 00:57:54.428","value":22.2}],"id":"Temp"},{"datapoints":[{"at":"2019-01-01 01:03:53.429","value":228},{"at":"2019-01-01 01:00:53.928","value":227},{"at":"2019-01-01 00:57:54.428","value":228}],"id":"Volt"},{"datapoints":[{"at":"2019-01-01 01:03:53.428","value":22.7},{"at":"2019-01-01 01:00:53.928","value":22.9},{"at":"2019-01-01 00:57:54.427","value":22.9}],"id":"Humi"}]}
     * msg : succ
     */

    private int code;
    private DataBean data;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class DataBean implements Parcelable {
        /**
         * count : 12
         * datastreams : [{"datapoints":[{"at":"2019-01-01 01:03:53.428","value":55},{"at":"2019-01-01 01:00:53.928","value":55},{"at":"2019-01-01 00:57:54.428","value":55}],"id":"RSSI"},{"datapoints":[{"at":"2019-01-01 01:03:53.429","value":22.8},{"at":"2019-01-01 01:00:53.928","value":22.5},{"at":"2019-01-01 00:57:54.428","value":22.2}],"id":"Temp"},{"datapoints":[{"at":"2019-01-01 01:03:53.429","value":228},{"at":"2019-01-01 01:00:53.928","value":227},{"at":"2019-01-01 00:57:54.428","value":228}],"id":"Volt"},{"datapoints":[{"at":"2019-01-01 01:03:53.428","value":22.7},{"at":"2019-01-01 01:00:53.928","value":22.9},{"at":"2019-01-01 00:57:54.427","value":22.9}],"id":"Humi"}]
         */

        private int count;
        private List<DatastreamsBean> datastreams;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public List<DatastreamsBean> getDatastreams() {
            return datastreams;
        }

        public void setDatastreams(List<DatastreamsBean> datastreams) {
            this.datastreams = datastreams;
        }

        public static class DatastreamsBean implements Parcelable {
            /**
             * datapoints : [{"at":"2019-01-01 01:03:53.428","value":55},{"at":"2019-01-01 01:00:53.928","value":55},{"at":"2019-01-01 00:57:54.428","value":55}]
             * id : RSSI
             */

            private String id;
            private List<DatapointsBean> datapoints;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public List<DatapointsBean> getDatapoints() {
                return datapoints;
            }

            public void setDatapoints(List<DatapointsBean> datapoints) {
                this.datapoints = datapoints;
            }

            public static class DatapointsBean implements Parcelable {
                /**
                 * at : 2019-01-01 01:03:53.428
                 * value : 55
                 */

                private String at;
                private String value;

                public String getAt() {
                    return at;
                }

                public void setAt(String at) {
                    this.at = at;
                }

                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }

                @Override
                public int describeContents() {
                    return 0;
                }

                @Override
                public void writeToParcel(Parcel dest, int flags) {
                    dest.writeString(this.at);
                    dest.writeString(this.value);
                }

                public DatapointsBean() {
                }

                protected DatapointsBean(Parcel in) {
                    this.at = in.readString();
                    this.value = in.readString();
                }

                public static final Creator<DatapointsBean> CREATOR = new Creator<DatapointsBean>() {
                    @Override
                    public DatapointsBean createFromParcel(Parcel source) {
                        return new DatapointsBean(source);
                    }

                    @Override
                    public DatapointsBean[] newArray(int size) {
                        return new DatapointsBean[size];
                    }
                };
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(this.id);
                dest.writeList(this.datapoints);
            }

            public DatastreamsBean() {
            }

            protected DatastreamsBean(Parcel in) {
                this.id = in.readString();
                this.datapoints = new ArrayList<DatapointsBean>();
                in.readList(this.datapoints, DatapointsBean.class.getClassLoader());
            }

            public static final Creator<DatastreamsBean> CREATOR = new Creator<DatastreamsBean>() {
                @Override
                public DatastreamsBean createFromParcel(Parcel source) {
                    return new DatastreamsBean(source);
                }

                @Override
                public DatastreamsBean[] newArray(int size) {
                    return new DatastreamsBean[size];
                }
            };
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.count);
            dest.writeList(this.datastreams);
        }

        public DataBean() {
        }

        protected DataBean(Parcel in) {
            this.count = in.readInt();
            this.datastreams = new ArrayList<DatastreamsBean>();
            in.readList(this.datastreams, DatastreamsBean.class.getClassLoader());
        }

        public static final Parcelable.Creator<DataBean> CREATOR = new Parcelable.Creator<DataBean>() {
            @Override
            public DataBean createFromParcel(Parcel source) {
                return new DataBean(source);
            }

            @Override
            public DataBean[] newArray(int size) {
                return new DataBean[size];
            }
        };
    }
}
