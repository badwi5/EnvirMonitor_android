package com.xuxiang.envirmonitor;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chinamobile.iot.onenet.OneNetApiCallback;
import com.xuxiang.envirmonitor.model.FuzzyDevIdBean;
import com.xuxiang.envirmonitor.utils.GlobalVarUtils;
import com.xuxiang.envirmonitor.utils.GsonHelper;
import com.xuxiang.envirmonitor.utils.OneNetHelper;
import com.xuxiang.envirmonitor.utils.ToastHelper;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private EditText mEditText;
    private ProgressBar mProgressBar;
    private FuzzyDevIdBean mFuzzyDevIdBean;

    private String mApiKey;
    private int mDevCount;
    private String mDevIds = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mProgressBar = findViewById(R.id.pgb_login);
        mEditText = ((TextInputLayout) (findViewById(R.id.tilay_login))).getEditText();
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView pTextView, int pActionId, KeyEvent pKeyEvent) {
                if (pActionId == EditorInfo.IME_ACTION_DONE || pActionId == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mApiKey = GlobalVarUtils.getApikey();
        if (mApiKey != null)
            mEditText.setText(mApiKey);

        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View pView) {
                attemptLogin();
            }
        });
    }

    private void attemptLogin() {
        mApiKey = mEditText.getText().toString();
        if (TextUtils.isEmpty(mApiKey)) {
            mEditText.setError("请输入API KEY");
            mEditText.requestFocus();
            return;
        } else if (mApiKey.length() < 28) {
            mEditText.setError("API KEY长度不足");
            mEditText.requestFocus();
            return;
        } else if (!mApiKey.matches("^[a-zA-Z0-9=]{1,28}$")) {
            mEditText.setError("API KEY包含非法字符");
            mEditText.requestFocus();
            return;
        }

        showProgress(true);
        hideKeyboard();
        OneNetHelper.setAppKey(mApiKey);
        OneNetHelper.fuzzyQueryDevices(new HashMap<String, String>(), new OneNetApiCallback() {
            @Override
            public void onSuccess(String pResponse) {
                showProgress(false);
                mFuzzyDevIdBean = GsonHelper.get().toObject(pResponse, FuzzyDevIdBean.class);
                if (mFuzzyDevIdBean.getErrno() != 0)
                    ToastHelper.makeText(ToastHelper.Message.LOGINFAILED);
                else {
                    mDevCount = mFuzzyDevIdBean.getData().getTotal_count();
                    if (mDevCount > 0) {
                        for (int i = 0; ; ) {
                            mDevIds = mDevIds.concat(mFuzzyDevIdBean.getData().getDevices().get(i).getId());
                            i++;
                            if (i < mDevCount)
                                mDevIds = mDevIds.concat(",");
                            else break;
                        }
                    }
                    GlobalVarUtils.setApikey(mApiKey);
                    GlobalVarUtils.setDevCount(mDevCount);
                    GlobalVarUtils.setDevIds(mDevIds);
                    GlobalVarUtils.save();
                    Intent _intent = new Intent();
                    setResult(RESULT_OK, _intent);
                    onBackPressed();
                }
            }

            @Override
            public void onFailed(Exception e) {
                showProgress(false);
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showProgress(final boolean pIfShow) {
        int _shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        mProgressBar.setVisibility(pIfShow ? View.VISIBLE : View.INVISIBLE);
        mProgressBar.animate().setDuration(_shortAnimTime).alpha(
                pIfShow ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressBar.setVisibility(pIfShow ? View.VISIBLE : View.INVISIBLE);
            }
        });
    }

    private void hideKeyboard() {
        View _view = getCurrentFocus();
        if (_view != null) {
            if (getSystemService(Context.INPUT_METHOD_SERVICE) != null) {
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                        hideSoftInputFromWindow(_view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}
