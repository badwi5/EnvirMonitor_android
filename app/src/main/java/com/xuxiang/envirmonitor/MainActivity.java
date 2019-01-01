package com.xuxiang.envirmonitor;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.chinamobile.iot.onenet.OneNetApiCallback;
import com.xuxiang.envirmonitor.model.MainCardAdapter;
import com.xuxiang.envirmonitor.model.MulDevDataBean;
import com.xuxiang.envirmonitor.model.MulDevStatusBean;
import com.xuxiang.envirmonitor.model.SinDevDataBean;
import com.xuxiang.envirmonitor.utils.GlobalVarUtils;
import com.xuxiang.envirmonitor.utils.GsonHelper;
import com.xuxiang.envirmonitor.utils.OneNetHelper;
import com.xuxiang.envirmonitor.utils.ToastHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class MainActivity extends AppCompatActivity {
    //region 变量初始化

    private CoordinatorLayout.LayoutParams mLayoutParams =
            new CoordinatorLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    private DrawerLayout mDrawerLayout;
    private MainCardAdapter mMainCardAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private NestedScrollView mNestedScrollView;

    private LineChartView mLineChartView;
    private List<PointValue> mPointValuesT, mPointValuesH, mPointValuesV;
    private List<AxisValue> mAxisXValuesTop, mAxisXValuesBottom;
    private Axis mAxisXTop, mAxisXBottom;
    private List<Line> mLines;
    private LineChartData mLineChartData;
    private Line mLineV, mLineH, mLineT;

    private MulDevStatusBean mMulDevStatusBean;
    private MulDevDataBean mMulDevDataBean;
    private SinDevDataBean mSinDevDataBean;
    private List<MulDevStatusBean.DataBean.DevicesBean> mListMulDevStatus = new ArrayList<>();
    private List<MulDevDataBean.DataBean.DevicesBean> mListMulDevData = new ArrayList<>();
    private int mItemPosition;
    private boolean mIsExit = false;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //region 初始化
        mLineChartView = findViewById(R.id.lcv_main);
        mNestedScrollView = findViewById(R.id.nsv_main);
        GlobalVarUtils.load();
        ToastHelper.init();
        OneNetHelper.init();
        initLineChart();
        //endregion

        //region 抽屉式菜单DrawerLayout
        Toolbar _toolbar = findViewById(R.id.tb_main);
        setSupportActionBar(_toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        _toolbar.setTitle("概览");
        mDrawerLayout = findViewById(R.id.drlay_main);
        NavigationView _navView = findViewById(R.id.nav_main);
        ActionBarDrawerToggle _actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, _toolbar, 0, 0) {
            @Override
            public void onDrawerOpened(View pDrawerView) {
                super.onDrawerOpened(pDrawerView);
            }

            @Override
            public void onDrawerClosed(View pDrawerView) {
                super.onDrawerClosed(pDrawerView);
            }
        };
        mDrawerLayout.addDrawerListener(_actionBarDrawerToggle);
        _actionBarDrawerToggle.syncState();

        _navView.setCheckedItem(R.id.nav_login);//默认选中登录菜单
        _navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem pMenuItem) {
                switch (pMenuItem.getItemId()) {
                    case R.id.nav_login:
                        startActivityForResult(new Intent(MainActivity.this, LoginActivity.class), 1);
                        break;
                    case R.id.nav_manager:
                        ToastHelper.makeText(ToastHelper.Message.UNAVAILABLE);
                        break;
                    case R.id.nav_settings:
                        ToastHelper.makeText(ToastHelper.Message.UNAVAILABLE);
                        break;
                    case R.id.nav_exit:
                        System.exit(0);
                        break;
                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
        //endregion

        //region 卡片列表RecyclerView
        RecyclerView _recyclerView = findViewById(R.id.ryv_main);
        _recyclerView.setHasFixedSize(true);
        mMainCardAdapter = new MainCardAdapter(mListMulDevStatus, mListMulDevData);
        _recyclerView.setAdapter(mMainCardAdapter);
        _recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mMainCardAdapter.setOnItemFocusChangeListener(new MainCardAdapter.OnItemFocusChangeListener() {
            @Override
            public void onItemFocusChangeListener(View pView, boolean pHasFocus, int pPosition) {
                if (pHasFocus) {
                    mItemPosition = pPosition;
                    Map<String, String> _params = new HashMap<>();
                    _params.put("datastream_id", GlobalVarUtils.Temp + "," + GlobalVarUtils.Humi + "," + GlobalVarUtils.Volt + "," + GlobalVarUtils.RSSI);
                    _params.put("limit", "100");
                    _params.put("sort", "DESC");
                    OneNetHelper.queryDataPoints(mMulDevStatusBean.getData().getDevices().get(pPosition).getId(), _params, new OneNetApiCallback() {
                        @Override
                        public void onSuccess(String pResponse) {
                            mSinDevDataBean = GsonHelper.get().toObject(pResponse, SinDevDataBean.class);
                            if (mSinDevDataBean.getCode() != 0) {
                                ToastHelper.makeText(ToastHelper.Message.FAILED);
                            } else {
                                if (mSinDevDataBean.getData().getCount() == 0)
                                    ToastHelper.makeText(ToastHelper.Message.NO_DATA);
                                else {
                                    showBottomSheet(mLayoutParams, mSwipeRefreshLayout, mNestedScrollView);
                                    drawLineChart(mSinDevDataBean.getData());
                                }
                            }
                        }

                        @Override
                        public void onFailed(Exception e) {
                            ToastHelper.makeText(e);
                        }
                    });
                } else hideBottomSheet(mLayoutParams, mSwipeRefreshLayout, mNestedScrollView);
            }
        });

        mMainCardAdapter.setOnItemClickListener(new MainCardAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View pView, int pPosition) {
                //触发点击事件的view已经focus
                if ((BottomSheetBehavior.from(mNestedScrollView).getState() == BottomSheetBehavior.STATE_COLLAPSED)
                        && (mSinDevDataBean.getData().getCount() != 0)) {
                    showBottomSheet(mLayoutParams, mSwipeRefreshLayout, mNestedScrollView);
                } else {
                    pView.clearFocus();
                }
            }
        });
        //endregion

        //region 下拉刷新swipeRefresh
        mSwipeRefreshLayout = findViewById(R.id.swr_main);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData(GlobalVarUtils.getApikey(), GlobalVarUtils.getDevCount(), GlobalVarUtils.getDevIds());
            }
        });

        refreshData(GlobalVarUtils.getApikey(), GlobalVarUtils.getDevCount(), GlobalVarUtils.getDevIds());
        //endregion
    }

    //刷新数据
    private void refreshData(final String pApiKey, final int pDevCount, final String pDevIds) {
        mSwipeRefreshLayout.setRefreshing(true);
        if (pApiKey != null) {
            OneNetHelper.setAppKey(pApiKey);
            if (pDevCount > 0) {
                updateDevice(pDevIds);
            } else {
                mListMulDevStatus.clear();
                mListMulDevData.clear();
                mMainCardAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
                ToastHelper.makeText(ToastHelper.Message.NO_DEVICE);
            }
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
            ToastHelper.makeText(ToastHelper.Message.UNLOGIN);
        }
    }

    //更新设备状态
    private void updateDevice(final String pDevIds) {
        OneNetHelper.queryMultiDevices(pDevIds, new OneNetApiCallback() {
            @Override
            public void onSuccess(String pResponse) {
                mMulDevStatusBean = GsonHelper.get().toObject(pResponse, MulDevStatusBean.class);
                if (mMulDevStatusBean.getCode() != 0) {
                    mSwipeRefreshLayout.setRefreshing(false);
                    ToastHelper.makeText(ToastHelper.Message.FAILED);
                } else {
                    updateDeviceData(pDevIds);
                }
            }

            @Override
            public void onFailed(Exception e) {
                mSwipeRefreshLayout.setRefreshing(false);
                ToastHelper.makeText(e);
            }
        });
    }

    //更新设备最新数据
    private void updateDeviceData(final String pDevIds) {
        OneNetHelper.queryMultiDevicesData(pDevIds, new OneNetApiCallback() {
            @Override
            public void onSuccess(String pResponse) {
                mMulDevDataBean = GsonHelper.get().toObject(pResponse, MulDevDataBean.class);
                if (mMulDevDataBean.getCode() != 0) {
                    ToastHelper.makeText(ToastHelper.Message.FAILED);
                } else {
                    mListMulDevStatus.clear();
                    mListMulDevStatus.addAll(mMulDevStatusBean.getData().getDevices());
                    mListMulDevData.clear();
                    mListMulDevData.addAll(mMulDevDataBean.getData().getDevices());
                    mMainCardAdapter.notifyDataSetChanged();
                    ToastHelper.makeText(ToastHelper.Message.SUCCESS);
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailed(Exception e) {
                mSwipeRefreshLayout.setRefreshing(false);
                ToastHelper.makeText(e);
            }
        });
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
        mLineV.setHasPoints(false);//是否显示圆点 如果为false 则没有圆点只有点显示

        mLineH = new Line();
        mLineH.setColor(getResources().getColor(R.color.colorThemeHumi)).setStrokeWidth(1);//折线的颜色、粗细
        mLineH.setFilled(true);//是否填充曲线的面积
        mLineH.setHasLines(true);//是否用直线显示。如果为false 则没有曲线只有点显示
        mLineH.setHasPoints(false);//是否显示圆点 如果为false 则没有圆点只有点显示

        mLineT = new Line();
        mLineT.setColor(getResources().getColor(R.color.colorThemeTemp)).setStrokeWidth(1);//折线的颜色、粗细
        mLineT.setFilled(true);//是否填充曲线的面积
        mLineT.setHasLines(true);//是否用直线显示。如果为false 则没有曲线只有点显示
        mLineT.setHasPoints(false);//是否显示圆点 如果为false 则没有圆点只有点显示

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

        Axis _axisYLeft = Axis.generateAxisFromRange(10, 70, 20);  //Y轴
        _axisYLeft.setHasLines(true);

        List<AxisValue> _axisValues = new ArrayList<>();
        for (int i = 1; i < 8; i += 2) {
            AxisValue _axisValue = new AxisValue(i * 10);
            _axisValue.setLabel(String.valueOf(i * 40));
            _axisValues.add(_axisValue);
        }
        Axis _axisYRight = new Axis(_axisValues);  //Y轴

        mLineChartData.setAxisYLeft(_axisYLeft);
        mLineChartData.setAxisYRight(_axisYRight);
        //endregion

        mLineChartView.setInteractive(false);
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
        mAxisXTop.setValues(mAxisXValuesTop);           //X轴 年月日
        mAxisXBottom.setValues(mAxisXValuesBottom);     //X轴 时分秒
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
    }

    //底部弹框点击事件
    public void onChartClicked(View pView) {
        Intent _intent = new Intent(this, DetailActivity.class);
        MulDevStatusBean.DataBean.DevicesBean _mulDevStatus = mListMulDevStatus.get(mItemPosition);
        List<MulDevDataBean.DataBean.DevicesBean.DatastreamsBean> _listMulDevData = mListMulDevData.get(mItemPosition).getDatastreams();

        Bundle _bundle = new Bundle();
        for (int i = 0; i < _listMulDevData.size(); i++) {                //将最新数据点依次存入对应数据流名的包中
            _bundle.putString(_listMulDevData.get(i).getId(), _listMulDevData.get(i).getValue());
            if (_listMulDevData.get(i).getId().equals(GlobalVarUtils.RSSI))
                _bundle.putString("Date", _listMulDevData.get(i).getAt());
        }
        _bundle.putString("Title", _mulDevStatus.getTitle());
        _bundle.putString("ID", _mulDevStatus.getId());
        _bundle.putBoolean("Status", _mulDevStatus.isOnline());
        _intent.putExtras(_bundle);
        _intent.putExtra("Data",mSinDevDataBean.getData());    //将所选设备最新100条数据存入包中
        this.startActivity(_intent);
    }

    Runnable mRunnable;
    Handler mHandler = new Handler();

    //显示底部弹框
    private void showBottomSheet(CoordinatorLayout.LayoutParams pLayoutParams, final View pChild, final View pDependency) {
        mHandler.removeCallbacks(mRunnable);
        pLayoutParams.dodgeInsetEdges = Gravity.BOTTOM;
        pChild.setLayoutParams(pLayoutParams);
        BottomSheetBehavior.from(pDependency).setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    //隐藏底部弹框
    private void hideBottomSheet(final CoordinatorLayout.LayoutParams pLayoutParams, final View pChild, final View pDependency) {
        BottomSheetBehavior.from(pDependency).setState(BottomSheetBehavior.STATE_COLLAPSED);
        mHandler.postDelayed(mRunnable = new Runnable() {
            public void run() {
                pLayoutParams.dodgeInsetEdges = Gravity.NO_GRAVITY;
                pLayoutParams.setBehavior(new AppBarLayout.ScrollingViewBehavior());
                pChild.setLayoutParams(pLayoutParams);
            }
        }, 250);
    }

    //添加搜索栏
    @Override
    public boolean onCreateOptionsMenu(Menu pMenu) {
        getMenuInflater().inflate(R.menu.searchbar, pMenu);
        MenuItem searchItem = pMenu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("输入设备名称或ID");
        // Configure the search info and add any event listeners
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastHelper.makeText(ToastHelper.Message.UNAVAILABLE);
            }
        });
        return super.onCreateOptionsMenu(pMenu);
    }

    //返回本页面时处理
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    refreshData(GlobalVarUtils.getApikey(), GlobalVarUtils.getDevCount(), GlobalVarUtils.getDevIds());
                }
                break;
            default:
        }
    }

    //重写返回键
    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (BottomSheetBehavior.from(mNestedScrollView).getState() == BottomSheetBehavior.STATE_EXPANDED) {
            hideBottomSheet(mLayoutParams, mSwipeRefreshLayout, mNestedScrollView);
            try {
                this.getCurrentFocus().clearFocus();
            } catch (Exception ignored) {
            }
        } else if (!mIsExit) {
            ToastHelper.makeText(ToastHelper.Message.EXIT);
            mIsExit = true;
            new Thread() {
                public void run() {
                    try {
                        Thread.sleep(2500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mIsExit = false;
                }
            }.start();
        } else
            super.onBackPressed();
    }
}
