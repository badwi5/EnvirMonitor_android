package com.xuxiang.envirmonitor;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.chinamobile.iot.onenet.OneNetApiCallback;
import com.xuxiang.envirmonitor.model.MulDevStatusBean;
import com.xuxiang.envirmonitor.model.SinDevDataBean;
import com.xuxiang.envirmonitor.utils.GlobalVarUtils;
import com.xuxiang.envirmonitor.utils.GsonHelper;
import com.xuxiang.envirmonitor.utils.OneNetHelper;
import com.xuxiang.envirmonitor.utils.ToastHelper;
import com.xuxiang.envirmonitor.view.ChartHintView;
import com.xuxiang.envirmonitor.view.CustomSwipeRefreshLayout;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class DetailActivity extends AppCompatActivity {

    //region 变量初始化
    private CustomSwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mtvDate;
    private TextView mtvTemp;
    private TextView mtvHumi;
    private TextView mtvVolt;
    private EditText metFrom;
    private EditText metTo;
    private ImageView mivStatus;
    private ImageView mivWifiSig;
    private LineChartView mLineChartView;
    private ChartHintView mChartHint;
    private ConstraintLayout mConstraintLayout;
    private RadioGroup mRdgpRange, mRdgpZoom;

    private ConstraintSet mConstraintSet = new ConstraintSet();
    private int[] mChartContainerPosition = new int[2];
    private String mDevId;
    private List<PointValue> mPointValuesT, mPointValuesH, mPointValuesV;
    private List<AxisValue> mAxisXValuesTop, mAxisXValuesBottom;
    private Axis mAxisXTop, mAxisXBottom;
    private List<Line> mLines;
    private LineChartData mLineChartData;
    private Line mLineV, mLineH, mLineT;
    private float mOldZoomLevel = 1;
    private boolean mIsRdgpZoomChecked = true;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //region findView
        mtvDate = findViewById(R.id.tv_detail_date);
        mtvTemp = findViewById(R.id.tv_detail_temperature);
        mtvHumi = findViewById(R.id.tv_detail_humidity);
        mtvVolt = findViewById(R.id.tv_detail_voltage);
        metFrom = ((TextInputLayout) (findViewById(R.id.tilay_detail_from))).getEditText();
        metFrom.setOnFocusChangeListener(mOnFocusChangeListener);
        metTo = ((TextInputLayout) (findViewById(R.id.tilay_detail_to))).getEditText();
        metTo.setOnFocusChangeListener(mOnFocusChangeListener);
        mivStatus = findViewById(R.id.iv_detail_status);
        mivWifiSig = findViewById(R.id.iv_detail_wifisig);
        mRdgpRange = findViewById(R.id.rdgp_detail_range);
        mRdgpRange.setOnCheckedChangeListener(mOnRadioGroupChecked);
        mRdgpZoom = findViewById(R.id.rdgp_detail_zoom);
        mRdgpZoom.setOnCheckedChangeListener(mOnRadioGroupChecked);
        mLineChartView = findViewById(R.id.lcv_detail);
        mChartHint = findViewById(R.id.charthintview_detail);
        mConstraintLayout = findViewById(R.id.clay_detail_bottom);
        mConstraintLayout.post((new Runnable() {
            @Override
            public void run() {
                mConstraintLayout.getLeft();
                mConstraintLayout.getTop();
                mConstraintLayout.getLocationOnScreen(mChartContainerPosition);
            }
        }));
        mConstraintSet.clone(mConstraintLayout);
        //endregion

        //region 下拉刷新swipeRefresh
        mSwipeRefreshLayout = findViewById(R.id.swr_detail);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshOverview();
            }
        });
        //endregion

        final Bundle _bundle = getIntent().getExtras();
        ((TextView) (findViewById(R.id.tv_detail_title))).setText(_bundle.getString("Title", "NaN"));
        mDevId = _bundle.getString("ID", "NaN");
        ((TextView) (findViewById(R.id.tv_detail_deviceid))).setText(mDevId);

        if (_bundle.getBoolean("Status", false))
            mivStatus.setImageResource(R.drawable.ic_online);
        else mivStatus.setImageResource(R.drawable.ic_offline);

        drawOverview(_bundle);
        initLineChart();
        drawLineChart((SinDevDataBean.DataBean) getIntent().getParcelableExtra("Data"));
    }

    private void getLatestData() {
        Map<String, String> _params = new HashMap<>();
        _params.put("newadd", "true");

        OneNetHelper.queryDataPoints(mDevId, _params, new OneNetApiCallback() {
            @Override
            public void onSuccess(String response) {
                SinDevDataBean _sinDevDataBean = GsonHelper.get().toObject(response, SinDevDataBean.class);
                if (_sinDevDataBean.getCode() != 0) {
                    mSwipeRefreshLayout.setRefreshing(false);
                    ToastHelper.makeText(ToastHelper.Message.FAILED);
                } else {
                    List<SinDevDataBean.DataBean.DatastreamsBean> _listSinDevData = _sinDevDataBean.getData().getDatastreams();
                    Bundle _bundle = new Bundle();
                    for (int i = 0; i < _listSinDevData.size(); i++) {                //将最新数据点依次存入对应数据流名的包中
                        _bundle.putString(_listSinDevData.get(i).getId(), _listSinDevData.get(i).getDatapoints().get(0).getValue());
                        if (_listSinDevData.get(i).getId().equals(GlobalVarUtils.RSSI))
                            _bundle.putString("Date", _listSinDevData.get(i).getDatapoints().get(0).getAt().substring(0, 19));
                    }
                    drawOverview(_bundle);
                    mSwipeRefreshLayout.setRefreshing(false);
                    ToastHelper.makeText(ToastHelper.Message.SUCCESS);
                }
            }

            @Override
            public void onFailed(Exception e) {
                mSwipeRefreshLayout.setRefreshing(false);
                ToastHelper.makeText(e);
            }
        });
    }

    private void getMoreData(Date pFromDate, Date pToDate) {
        Map<String, String> _params = new HashMap<>();
        SimpleDateFormat _format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        _params.put("start", _format.format(pFromDate));
        _params.put("end", _format.format(pToDate));
        _params.put("datastream_id", GlobalVarUtils.Temp + "," + GlobalVarUtils.Humi + "," + GlobalVarUtils.Volt + "," + GlobalVarUtils.RSSI);
        _params.put("limit", "6000");
        _params.put("sort", "DESC");
        OneNetHelper.queryDataPoints(mDevId, _params, new OneNetApiCallback() {
            @Override
            public void onSuccess(String response) {
                SinDevDataBean _sinDevDataBean = GsonHelper.get().toObject(response, SinDevDataBean.class);
                if (_sinDevDataBean.getCode() != 0) {
                    ToastHelper.makeText(ToastHelper.Message.FAILED);
                } else {
                    if (_sinDevDataBean.getData().getCount() == 6000) {
                        ToastHelper.makeText("数据点过多，已重设查询范围");
                        String _str = _sinDevDataBean.getData().getDatastreams().get(0).getDatapoints().get(2000 - 1).getAt();
                        metFrom.requestFocus();
                        metFrom.setError("日期已重设");
                        metFrom.setText(_str.substring(0, 10));
                    } else
                        ToastHelper.makeText(ToastHelper.Message.SUCCESS);
                    drawLineChart(_sinDevDataBean.getData());
                }
            }

            @Override
            public void onFailed(Exception e) {
                ToastHelper.makeText(e);
            }
        });
    }

    private void refreshOverview() {
        mSwipeRefreshLayout.setRefreshing(true);
        OneNetHelper.queryMultiDevices(mDevId, new OneNetApiCallback() {
            @Override
            public void onSuccess(String pResponse) {
                MulDevStatusBean _mulDevStatusBean = GsonHelper.get().toObject(pResponse, MulDevStatusBean.class);
                if (_mulDevStatusBean.getCode() != 0) {
                    mSwipeRefreshLayout.setRefreshing(false);
                    ToastHelper.makeText(ToastHelper.Message.FAILED);
                } else {
                    if (_mulDevStatusBean.getData().getDevices().get(0).isOnline())
                        mivStatus.setImageResource(R.drawable.ic_online);
                    else mivStatus.setImageResource(R.drawable.ic_offline);
                    getLatestData();
                }
            }

            @Override
            public void onFailed(Exception e) {
                mSwipeRefreshLayout.setRefreshing(false);
                ToastHelper.makeText(e);
            }
        });
    }

    private void drawOverview(Bundle pBundle) {
        mtvDate.setText(pBundle.getString("Date", "NaN"));
        mtvTemp.setText(pBundle.getString(GlobalVarUtils.Temp, "NaN"));
        mtvHumi.setText(pBundle.getString(GlobalVarUtils.Humi, "NaN"));
        mtvVolt.setText(pBundle.getString(GlobalVarUtils.Volt, "NaN"));

        int _wifiSig = Integer.parseInt(pBundle.getString(GlobalVarUtils.RSSI));
        if (_wifiSig == 100)
            mivWifiSig.setImageResource(R.drawable.ic_wifi_sig_5);
        else if (_wifiSig > 66)
            mivWifiSig.setImageResource(R.drawable.ic_wifi_sig_4);
        else if (_wifiSig > 33)
            mivWifiSig.setImageResource(R.drawable.ic_wifi_sig_3);
        else if (_wifiSig > 0)
            mivWifiSig.setImageResource(R.drawable.ic_wifi_sig_2);
        else mivWifiSig.setImageResource(R.drawable.ic_wifi_sig_1);
    }

    private void initLineChart() {
        mPointValuesT = new ArrayList<>();
        mPointValuesH = new ArrayList<>();
        mPointValuesV = new ArrayList<>();
        mAxisXValuesTop = new ArrayList<>();
        mAxisXValuesBottom = new ArrayList<>();
        mLines = new ArrayList<>();
        mLineChartData = new LineChartData();

        //region 折线外观样式设置
        mLineV = new Line();
        mLineV.setColor(getResources().getColor(R.color.colorThemeVolt)).setStrokeWidth(1);//折线的颜色、粗细
        mLineV.setFilled(true);//是否填充曲线的面积
        mLineV.setHasLines(true);//是否用直线显示。如果为false 则没有曲线只有点显示
        mLineV.setHasPoints(true);//是否显示圆点 如果为false 则没有圆点只有点显示
        mLineV.setPointRadius(0);
        mLineV.setHasLabelsOnlyForSelected(true);//隐藏数据，触摸可以显示

        mLineH = new Line();
        mLineH.setColor(getResources().getColor(R.color.colorThemeHumi)).setStrokeWidth(1);//折线的颜色、粗细
        mLineH.setFilled(true);//是否填充曲线的面积
        mLineH.setHasLines(true);//是否用直线显示。如果为false 则没有曲线只有点显示
        mLineH.setHasPoints(true);//是否显示圆点 如果为false 则没有圆点只有点显示
        mLineH.setPointRadius(0);
        mLineH.setHasLabelsOnlyForSelected(true);//隐藏数据，触摸可以显示

        mLineT = new Line();
        mLineT.setColor(getResources().getColor(R.color.colorThemeTemp)).setStrokeWidth(1);//折线的颜色、粗细
        mLineT.setFilled(true);//是否填充曲线的面积
        mLineT.setHasLines(true);//是否用直线显示。如果为false 则没有曲线只有点显示
        mLineT.setHasPoints(true);//是否显示圆点 如果为false 则没有圆点只有点显示
        mLineT.setPointRadius(0);
        mLineT.setHasLabelsOnlyForSelected(true);//隐藏数据，触摸可以显示

        mLines.add(mLineV);
        mLines.add(mLineH);
        mLines.add(mLineT);
        //endregion

        //region 坐标轴
        mAxisXTop = new Axis(); //X轴
        mAxisXTop.setMaxLabelChars(10);
        mAxisXTop.setHasLines(false);

        mAxisXBottom = new Axis(); //X轴
        mAxisXBottom.setMaxLabelChars(6);
        mAxisXBottom.setHasLines(true);

        Axis _axisYLeft = Axis.generateAxisFromRange(10, 70, 10);  //Y轴
        _axisYLeft.setHasLines(true);

        List<AxisValue> _axisValues = new ArrayList<>();
        for (int i = 1; i < 8; i++) {
            AxisValue _axisValue = new AxisValue(i * 10);
            _axisValue.setLabel(String.valueOf(i * 40));
            _axisValues.add(_axisValue);
        }
        Axis _axisYRight = new Axis(_axisValues);  //Y轴

        mLineChartData.setAxisYLeft(_axisYLeft);
        mLineChartData.setAxisYRight(_axisYRight);
        //endregion

        //region 图表交互设置
        mLineChartView.setInteractive(true);
        mLineChartView.setZoomType(ZoomType.HORIZONTAL);
        mLineChartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        mLineChartView.setValueSelectionEnabled(true);//设置图表数据是否可选中进行显示
        mLineChartView.setOnTouchListener(mTouchListener);
        //endregion
    }

    private void drawLineChart(SinDevDataBean.DataBean pSinDevDataBean) {
        Map<String, Integer> _map = new HashMap<>();
        List<SinDevDataBean.DataBean.DatastreamsBean> _listDatastreams = pSinDevDataBean.getDatastreams();
        for (int i = 0; i < _listDatastreams.size(); i++)
            _map.put(_listDatastreams.get(i).getId(), i);
        int _count = _listDatastreams.get(_map.get(GlobalVarUtils.RSSI)).getDatapoints().size();      //以RSSI为标准计算上传数据量

        try {
            List<SinDevDataBean.DataBean.DatastreamsBean.DatapointsBean> _listTDatapoints = _listDatastreams.get(_map.get(GlobalVarUtils.Temp)).getDatapoints();
            List<SinDevDataBean.DataBean.DatastreamsBean.DatapointsBean> _listHDatapoints = _listDatastreams.get(_map.get(GlobalVarUtils.Humi)).getDatapoints();
            List<SinDevDataBean.DataBean.DatastreamsBean.DatapointsBean> _listVDatapoints = _listDatastreams.get(_map.get(GlobalVarUtils.Volt)).getDatapoints();
            mPointValuesT.clear();
            mPointValuesH.clear();
            mPointValuesV.clear();
            mAxisXValuesTop.clear();
            mAxisXValuesBottom.clear();

            //region 设置折线点数据
            for (int i = 0, j = _count - 1; j >= 0; i++, j--) {
                mPointValuesT.add(new PointValue(i, Float.parseFloat(_listTDatapoints.get(j).getValue())));
                mPointValuesH.add(new PointValue(i, Float.parseFloat(_listHDatapoints.get(j).getValue())));
                mPointValuesV.add(new PointValue(i, Float.parseFloat(_listVDatapoints.get(j).getValue()) / 4).setLabel(_listVDatapoints.get(j).getValue()));
                mAxisXValuesTop.add(new AxisValue(i).setLabel(_listTDatapoints.get(j).getAt().substring(0, 10)));
                mAxisXValuesBottom.add(new AxisValue(i).setLabel(_listTDatapoints.get(j).getAt().substring(11, 19)));
            }

            mLineT.setValues(mPointValuesT);
            mLineH.setValues(mPointValuesH);
            mLineV.setValues(mPointValuesV);
            //endregion
        } catch (Exception e) {
            ToastHelper.makeText(e);
            return;
        }

        //region 设置折线坐标数据

        mAxisXTop.setValues(mAxisXValuesTop);  //X轴 年月日
        mAxisXBottom.setValues(mAxisXValuesBottom);  //X轴 时分秒
        mLineChartData.setAxisXTop(mAxisXTop);
        mLineChartData.setAxisXBottom(mAxisXBottom);
        mLineChartData.setLines(mLines);
        mLineChartView.setLineChartData(mLineChartData);

        //endregion

        //region 图表视图设置
        Viewport _viewport = new Viewport(0, 80, _count - 1, 0);
        mLineChartView.setMaximumViewport(_viewport);
        mLineChartView.setCurrentViewport(_viewport);
        //endregion

        float _maxZoom = _count / 30 + 1;
        mLineChartView.setMaxZoom(_maxZoom);
        ((RadioButton) (findViewById(R.id.rdbtn_detail_zoom2))).setText("×" + (int) (Math.sqrt(_maxZoom)));
        ((RadioButton) (findViewById(R.id.rdbtn_detail_zoom3))).setText("×" + (int) _maxZoom);
        mRdgpZoom.check(R.id.rdbtn_detail_zoom1);
    }

    public void onEditTextClicked(View pView) {
        final EditText _editText = (EditText) pView;
        final Calendar _calendar = Calendar.getInstance();
        DatePickerDialog _DataPickerDialog = new DatePickerDialog(DetailActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker pView, int pYear, int pMonthOfYear, int pDayOfMonth) {
                _calendar.set(pYear, pMonthOfYear, pDayOfMonth);
                _editText.setText(DateFormat.format("yyyy-MM-dd", _calendar));
            }
        }, _calendar.get(Calendar.YEAR), _calendar.get(Calendar.MONTH), _calendar.get(Calendar.DAY_OF_MONTH));
        _DataPickerDialog.show();
    }

    public void onButtonClicked(View pView) {
        View _view = getCurrentFocus();
        if (_view != null) {
            if (getSystemService(Context.INPUT_METHOD_SERVICE) != null) {
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                        hideSoftInputFromWindow(_view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        try {
            metFrom.clearFocus();
            metTo.clearFocus();
        } catch (Exception e) {
        }
        boolean isValid = true;
        Date _fromDate = new Date();
        Date _toDate = new Date();
        SimpleDateFormat _format = new SimpleDateFormat("yyyy-MM-dd");
        _format.setLenient(false);
        try {
            _fromDate = _format.parse(metFrom.getText().toString());
        } catch (ParseException pE) {
            metFrom.setError("格式错误");
            isValid = false;
        }
        try {
            _toDate = _format.parse(metTo.getText().toString());
        } catch (ParseException pE) {
            metTo.setError("格式错误");
            isValid = false;
        }
        if (isValid) {
            if (_toDate.before(_fromDate)) {
                metFrom.setError("超出范围");
            } else {
                Calendar _calendar = Calendar.getInstance();
                Date _nowDate = _calendar.getTime();
                mRdgpRange.clearCheck();
                long _diff = _nowDate.getTime() - _toDate.getTime();
                if (_diff >= 24 * 60 * 60 * 1000) {
                    _calendar.setTime(_toDate);
                    _calendar.add(Calendar.DAY_OF_YEAR, 1);
                    getMoreData(_fromDate, _calendar.getTime());
                } else if (_diff < 0) {
                    metTo.setError("超出范围");
                } else
                    getMoreData(_fromDate, _nowDate);
            }
        }
    }

    //region LineChartView TouchListener
    View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            v.onTouchEvent(event);
            if (mIsRdgpZoomChecked && (Math.abs(mLineChartView.getZoomLevel() - mOldZoomLevel) > 0.01)) {
                mOldZoomLevel = mLineChartView.getZoomLevel();
                mRdgpZoom.clearCheck();
                mIsRdgpZoomChecked = false;
            }
            //region ChartHintView
            if (((LineChartView) v).getSelectedValue().isSet()) {
                SelectedValue _selectedValue = ((LineChartView) v).getSelectedValue();
                int _index = _selectedValue.getSecondIndex();
                float _locX, _locY;
                _locX = event.getRawX() - mChartContainerPosition[0] - mChartHint.getRectWidth();
                _locY = event.getRawY() - mChartContainerPosition[1] - mChartHint.getRectHeight();
                if (_locX < 0)
                    _locX = _locX + mChartHint.getRectWidth();
                if (_locY < 0)
                    _locY = _locY + mChartHint.getRectHeight();
                mConstraintSet.setMargin(R.id.charthintview_detail, ConstraintSet.START, (int) _locX);
                mConstraintSet.setMargin(R.id.charthintview_detail, ConstraintSet.TOP, (int) _locY);
                mConstraintSet.applyTo(mConstraintLayout);
                mChartHint.setText(
                        String.valueOf(mAxisXValuesTop.get(_index).getLabelAsChars())
                                + " " + String.valueOf(mAxisXValuesBottom.get(_index).getLabelAsChars()),
                        String.valueOf(mPointValuesT.get(_index).getY()),
                        String.valueOf(mPointValuesH.get(_index).getY()),
                        String.valueOf(mPointValuesV.get(_index).getLabelAsChars()));
                mChartHint.setVisibility(View.VISIBLE);
            } else {
                mChartHint.setVisibility(View.GONE);
            }
            //endregion
            return true;
        }
    };
    //endregion

    //region RadioGroup CheckedChangeListener
    RadioGroup.OnCheckedChangeListener mOnRadioGroupChecked = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            try {
                metFrom.clearFocus();
                metTo.clearFocus();
            } catch (Exception e) {
            }
            RadioButton _rdbtn = findViewById(checkedId);
            if (_rdbtn == null)
                return;
            if (group == mRdgpRange) {
                Calendar _now = Calendar.getInstance();
                Date _toDate = _now.getTime();
                switch (checkedId) {
                    case R.id.rdbtn_detail_range1:
                        _now.add(Calendar.HOUR_OF_DAY, -12);
                        break;
                    case R.id.rdbtn_detail_range2:
                        _now.add(Calendar.HOUR_OF_DAY, -24);
                        break;
                    case R.id.rdbtn_detail_range3:
                        _now.add(Calendar.HOUR_OF_DAY, -72);
                        break;
                }
                Date _fromDate = _now.getTime();
                getMoreData(_fromDate, _toDate);
            } else if (group == mRdgpZoom) {
                mIsRdgpZoomChecked = true;
                mOldZoomLevel = Float.parseFloat(_rdbtn.getText().toString().substring(1));
                mLineChartView.setZoomLevelWithAnimation(mLineChartView.getCurrentViewport().centerX(), 0, mOldZoomLevel);
            }
        }
    };
    //endregion

    EditText.OnFocusChangeListener mOnFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!metFrom.isFocused() && !metTo.isFocused()) {
                metFrom.setError(null);
                metTo.setError(null);
            }
        }
    };
}

