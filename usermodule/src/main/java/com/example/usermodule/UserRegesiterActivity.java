package com.example.usermodule;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.example.usermodule.net.IUserDataCallBack;
import com.example.usermodule.net.UserNetManager;
import com.mm.android.mobilecommon.AppConsume.ProviderManager;
import com.mm.android.mobilecommon.base.BaseActivity;
import com.mm.android.mobilecommon.openapi.TokenHelper;
import com.mm.android.mobilecommon.route.RoutePathManager;
import com.mm.android.mobilecommon.utils.LogUtil;
import com.mm.android.mobilecommon.utils.PreferencesHelper;
import com.mm.android.mobilecommon.utils.WordInputFilter;
import com.mm.android.mobilecommon.widget.ClearEditText;

import static com.mm.android.mobilecommon.route.RoutePathManager.ActivityPath.UserRegesiterPath;
import static com.mm.android.mobilecommon.utils.WordInputFilter.NEW_PHONE;
import static com.mm.android.mobilecommon.utils.WordInputFilter.REGEX_EMAIL_GUO;
import static com.mm.android.mobilecommon.utils.WordInputFilter.REGEX_EMAIl;
import static com.mm.android.mobilecommon.utils.WordInputFilter.REX_EMAIL;
import static com.mm.android.mobilecommon.utils.WordInputFilter.REX_PHONE;
@Route(path =UserRegesiterPath )
public class UserRegesiterActivity extends BaseActivity implements View.OnClickListener{

    public final static String TAG = UserRegesiterActivity.class.getSimpleName();
    ClearEditText mName;
    LinearLayout mRegesiter;
    ImageView mBack;
    private String mUserName;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.user_regesiter_activity);
        initView();
    }

    private void initView() {
        mName = findViewById(R.id.login_username);
        mRegesiter = findViewById(R.id.login_regesiter);
        mBack = findViewById(R.id.iv_back);
        mRegesiter.setOnClickListener(this);
        mBack.setOnClickListener(this);
        if (0 == ProviderManager.getAppProvider().getAppType()){
        }else{
            mName.setFilters(new InputFilter[]{new WordInputFilter(REX_EMAIL)});
        }
        mName.setTextChangeListener(mTextChangedListener);
    }

    private ClearEditText.ITextChangeListener mTextChangedListener = new ClearEditText.ITextChangeListener() {
        @Override
        public void afterChanged(EditText v, Editable s) {
            mUserName = mName.getText().toString().trim();
        }

        @Override
        public void beforeChanged(EditText v, CharSequence s, int start, int count, int after) {
            if(v.getId() == R.id.login_username && mName.getText().toString().contains("****")) {
                mName.setText("");
            }
        }

        @Override
        public void onTextChanged(EditText v, CharSequence text, int start, int lengthBefore, int lengthAfter) {

        }
    };


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.login_regesiter) {
            if (0 == ProviderManager.getAppProvider().getAppType()){
                if (!(mUserName!=null&&mUserName.matches(NEW_PHONE))&&!(mUserName!=null&&mUserName.matches(REGEX_EMAIL_GUO))){
                    Toast.makeText(UserRegesiterActivity.this,R.string.phone_not_match_tip,Toast.LENGTH_SHORT).show();
                    return;
                }
            }else{
                if (!(mUserName!=null&&mUserName.matches(REGEX_EMAIL_GUO))){
                    Toast.makeText(UserRegesiterActivity.this,R.string.phone_not_match_tip,Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            UserNetManager.getInstance().createAccountToken(mUserName, new IUserDataCallBack() {
                @Override
                public void onCallBackOpenId(String str) {
                    LogUtil.debugLog(TAG,"openId"+str);
                    if (str!=null){
                        TokenHelper.getInstance().openid =str;
                        //PreferencesHelper.getInstance(getApplicationContext()).set(Constants.OPENID,str);
                        getSubAccountToken(str);
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    Toast.makeText(UserRegesiterActivity.this,throwable.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }else if (id==R.id.iv_back){
            finish();
        }
    }

    private void getSubAccountToken(String openId){
        UserNetManager.getInstance().subAccountToken(openId, new IUserDataCallBack() {
            @Override
            public void onCallBackOpenId(String str) {
                if (str!=null){
                    //暂不保存到sp中了，因为获取需要context。
                  //  PreferencesHelper.getInstance(getApplicationContext()).set(Constants.SUBACCOUNTTOKEN,str);
                    TokenHelper.getInstance().setSubAccessToken(str, UserRegesiterActivity.this.getApplication());
                    toDeviceList();
                    finish();
                }
            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
    }

    private void toDeviceList(){
        ARouter.getInstance().build(RoutePathManager.ActivityPath.DeviceListActivityPath).navigation();
    }
}
