package com.xuxiang.envirmonitor.model;

import android.annotation.SuppressLint;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xuxiang.envirmonitor.R;
import com.xuxiang.envirmonitor.utils.GlobalVarUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hsian on 2017/10/23.
 */

public class MainCardAdapter extends RecyclerView.Adapter<MainCardAdapter.ViewHolder> {

    private List<MulDevStatusBean.DataBean.DevicesBean> mListMulDevStatus;
    private List<MulDevDataBean.DataBean.DevicesBean> mListMulDevData;

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayoutCompat mllayContainer;
        ImageView mivStatus;
        TextView mtvTitle, mtvDevId, mtvDate, mtvTemp, mtvHumi, mtvVolt;

        ViewHolder(View pView) {
            super(pView);
            mllayContainer = (LinearLayoutCompat) pView;
            mivStatus = pView.findViewById(R.id.iv_mainitem_status);
            mtvTitle = pView.findViewById(R.id.tv_mainitem_title);
            mtvDate = pView.findViewById(R.id.tv_mainitem_date);
            mtvDevId = pView.findViewById(R.id.tv_mainitem_deviceid);
            mtvTemp = pView.findViewById(R.id.tv_mainitem_temperature);
            mtvHumi = pView.findViewById(R.id.tv_mainitem_humidity);
            mtvVolt = pView.findViewById(R.id.tv_mainitem_voltage);
        }
    }

    public MainCardAdapter(List<MulDevStatusBean.DataBean.DevicesBean> pListMulDevStatus,
                           List<MulDevDataBean.DataBean.DevicesBean> pListMulDevData) {
        this.mListMulDevStatus = pListMulDevStatus;
        this.mListMulDevData = pListMulDevData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup pViewGroup, int pViewType) {
        final View _view = LayoutInflater.from(pViewGroup.getContext()).inflate(R.layout.main_item, pViewGroup, false);
        return new ViewHolder(_view);
    }

    private int mCurPosition = 0;

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final ViewHolder pHolder, final int pPosition) {
        pHolder.itemView.setTag(pPosition);
        if (mOnItemFocusChangeListener != null) {
            pHolder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    mCurPosition = (int) pHolder.itemView.getTag();
                    mOnItemFocusChangeListener.onItemFocusChangeListener(pHolder.itemView, hasFocus, mCurPosition);
                }
            });
        }
        if (mOnItemClickListener != null) {
            pHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, pHolder.getLayoutPosition());
                }
            });
        }
        MulDevStatusBean.DataBean.DevicesBean _mulDevStatus = mListMulDevStatus.get(pPosition);
        MulDevDataBean.DataBean.DevicesBean _mulDevData = mListMulDevData.get(pPosition);

        pHolder.mtvTitle.setText(_mulDevStatus.getTitle());
        pHolder.mtvDevId.setText(_mulDevStatus.getId());
        if (_mulDevData.getDatastreams() == null) {
            pHolder.mivStatus.setImageResource(R.drawable.ic_offline);
            pHolder.mtvDate.setText("    No Data");
            pHolder.mtvTemp.setText("NaN");
            pHolder.mtvHumi.setText("NaN");
            pHolder.mtvVolt.setText("NaN");
        } else {
            if (!_mulDevStatus.isOnline())
                pHolder.mivStatus.setImageResource(R.drawable.ic_offline);//离线图标
            else if (_mulDevStatus.isOnline()) {
                pHolder.mivStatus.setImageResource(R.drawable.ic_online);//在线图标
            }
            Map<String, Integer> _map = new HashMap<>();
            List<MulDevDataBean.DataBean.DevicesBean.DatastreamsBean> _listMulDevData = _mulDevData.getDatastreams();
            for (int i = 0; i < _listMulDevData.size(); i++)
                _map.put(_listMulDevData.get(i).getId(), i);
            try {
                pHolder.mtvDate.setText(_listMulDevData.get(_map.get(GlobalVarUtils.RSSI)).getAt());
            } catch (Exception e) {
            }
            try {
                pHolder.mtvTemp.setText(_listMulDevData.get(_map.get(GlobalVarUtils.Temp)).getValue());
            } catch (Exception e) {
            }
            try {
                pHolder.mtvHumi.setText(_listMulDevData.get(_map.get(GlobalVarUtils.Humi)).getValue());
            } catch (Exception e) {
            }
            try {
                pHolder.mtvVolt.setText(_listMulDevData.get(_map.get(GlobalVarUtils.Volt)).getValue());
            } catch (Exception e) {
            }
        }
    }

    @Override
    public int getItemCount() {
        return mListMulDevStatus.size();
    }

    /*设置item点击事件的接口*/
    public interface OnItemClickListener {
        void onItemClick(View pView, int pPosition);
    }

    /*设置item选中的接口*/
    public interface OnItemFocusChangeListener {
        void onItemFocusChangeListener(View pView, boolean pHasFocus, int pPosition);
    }

    private OnItemClickListener mOnItemClickListener;
    private OnItemFocusChangeListener mOnItemFocusChangeListener;

    public void setOnItemClickListener(OnItemClickListener pListener) {
        mOnItemClickListener = pListener;
    }

    public void setOnItemFocusChangeListener(OnItemFocusChangeListener pListener) {
        mOnItemFocusChangeListener = pListener;
    }
}
