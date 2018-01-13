package com.xuxiang.envirmonitor.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by hsian on 2017/11/13.
 */

public class SinDevDataBean {

    /**
     * errno : 0
     * data : {"count":4,"datastreams":[{"datapoints":[{"at":"2017-11-13 09:50:39.203","value":66}],"id":"RSSI"},{"datapoints":[{"at":"2017-11-13 09:50:39.204","value":24.4}],"id":"temperature"},{"datapoints":[{"at":"2017-11-13 09:50:39.203","value":44.2}],"id":"humidity"},{"datapoints":[{"at":"2017-11-13 09:50:39.204","value":218}],"id":"voltage"}]}
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

    public static class DataBean implements Parcelable{
        /**
         * count : 4
         * datastreams : [{"datapoints":[{"at":"2017-11-13 09:50:39.203","value":66}],"id":"RSSI"},{"datapoints":[{"at":"2017-11-13 09:50:39.204","value":24.4}],"id":"temperature"},{"datapoints":[{"at":"2017-11-13 09:50:39.203","value":44.2}],"id":"humidity"},{"datapoints":[{"at":"2017-11-13 09:50:39.204","value":218}],"id":"voltage"}]
         */

        private int count;
        private List<DatastreamsBean> datastreams;

        protected DataBean(Parcel in) {
            count = in.readInt();
            datastreams = in.createTypedArrayList(DatastreamsBean.CREATOR);
        }

        public static final Creator<DataBean> CREATOR = new Creator<DataBean>() {
            @Override
            public DataBean createFromParcel(Parcel in) {
                return new DataBean(in);
            }

            @Override
            public DataBean[] newArray(int size) {
                return new DataBean[size];
            }
        };

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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(count);
            dest.writeTypedList(datastreams);
        }

        public static class DatastreamsBean implements Parcelable{
            /**
             * datapoints : [{"at":"2017-11-13 09:50:39.203","value":66}]
             * id : RSSI
             */

            private String id;
            private List<DatapointsBean> datapoints;

            protected DatastreamsBean(Parcel in) {
                id = in.readString();
                datapoints = in.createTypedArrayList(DatapointsBean.CREATOR);
            }

            public static final Creator<DatastreamsBean> CREATOR = new Creator<DatastreamsBean>() {
                @Override
                public DatastreamsBean createFromParcel(Parcel in) {
                    return new DatastreamsBean(in);
                }

                @Override
                public DatastreamsBean[] newArray(int size) {
                    return new DatastreamsBean[size];
                }
            };

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

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(id);
                dest.writeTypedList(datapoints);
            }

            public static class DatapointsBean implements Parcelable{
                /**
                 * at : 2017-11-13 09:50:39.203
                 * value : 66
                 */

                private String at;
                private double value;

                protected DatapointsBean(Parcel in) {
                    at = in.readString();
                    value = in.readDouble();
                }

                public static final Creator<DatapointsBean> CREATOR = new Creator<DatapointsBean>() {
                    @Override
                    public DatapointsBean createFromParcel(Parcel in) {
                        return new DatapointsBean(in);
                    }

                    @Override
                    public DatapointsBean[] newArray(int size) {
                        return new DatapointsBean[size];
                    }
                };

                public String getAt() {
                    return at;
                }

                public void setAt(String at) {
                    this.at = at;
                }

                public double getValue() {
                    return value;
                }

                public void setValue(double value) {
                    this.value = value;
                }

                @Override
                public int describeContents() {
                    return 0;
                }

                @Override
                public void writeToParcel(Parcel dest, int flags) {
                    dest.writeString(at);
                    dest.writeDouble(value);
                }
            }
        }
    }
}
