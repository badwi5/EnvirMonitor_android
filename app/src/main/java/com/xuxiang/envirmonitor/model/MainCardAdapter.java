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

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by hsian on 2017/10/23.
 */

public class MainCardAdapter extends RecyclerView.Adapter<MainCardAdapter.ViewHolder> {

    private List<MulDevStatusBean.DataBean.DevicesBean> mMulDevStatusBeans;
    private List<MulDevDataBean.DataBean.DevicesBean> mMulDevDataBeans;

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayoutCompat mllayContainer;
        ImageView mivStatus;
        TextView mtvTitle, mtvDevId, mtvDate, mtvTemperature, mtvHumidity, mtvVoltage;

        ViewHolder(View pView) {
            super(pView);
            mllayContainer = (LinearLayoutCompat) pView;
            mivStatus = pView.findViewById(R.id.iv_mainitem_status);
            mtvTitle = pView.findViewById(R.id.tv_mainitem_title);
            mtvDate = pView.findViewById(R.id.tv_mainitem_date);
            mtvDevId = pView.findViewById(R.id.tv_mainitem_deviceid);
            mtvTemperature = pView.findViewById(R.id.tv_mainitem_temperature);
            mtvHumidity = pView.findViewById(R.id.tv_mainitem_humidity);
            mtvVoltage = pView.findViewById(R.id.tv_mainitem_voltage);
        }
    }

    public MainCardAdapter(List<MulDevStatusBean.DataBean.DevicesBean> pMulDevStatusBeans,
                           List<MulDevDataBean.DataBean.DevicesBean> pMulDevDataBeans) {
        this.mMulDevStatusBeans = pMulDevStatusBeans;
        this.mMulDevDataBeans = pMulDevDataBeans;
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
        MulDevStatusBean.DataBean.DevicesBean _mulDevStatusBean = mMulDevStatusBeans.get(pPosition);
        MulDevDataBean.DataBean.DevicesBean _mulDevDataBean = mMulDevDataBeans.get(pPosition);

        pHolder.mtvTitle.setText(_mulDevStatusBean.getTitle());
        pHolder.mtvDevId.setText(_mulDevStatusBean.getId());
        if (_mulDevDataBean.getDatastreams() == null) {
            pHolder.mivStatus.setImageResource(R.drawable.ic_offline);
            pHolder.mtvDate.setText("    No Data");
            pHolder.mtvTemperature.setText("NaN");
            pHolder.mtvHumidity.setText("NaN");
            pHolder.mtvVoltage.setText("NaN");
        } else {
            if (!_mulDevStatusBean.isOnline())
                pHolder.mivStatus.setImageResource(R.drawable.ic_offline);//离线图标
            else if (_mulDevStatusBean.isOnline()) {
                pHolder.mivStatus.setImageResource(R.drawable.ic_online);//在线图标
            }
            pHolder.mtvDate.setText(_mulDevDataBean.getDatastreams().get(0).getAt());
            pHolder.mtvTemperature.setText(new DecimalFormat("##.0").format(_mulDevDataBean.getDatastreams().get(0).getValue()));
            pHolder.mtvHumidity.setText(new DecimalFormat("##.0").format(_mulDevDataBean.getDatastreams().get(1).getValue()));
            pHolder.mtvVoltage.setText(new DecimalFormat("###").format(_mulDevDataBean.getDatastreams().get(3).getValue()));
        }
    }

    @Override
    public int getItemCount() {
        return mMulDevStatusBeans.size();
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
