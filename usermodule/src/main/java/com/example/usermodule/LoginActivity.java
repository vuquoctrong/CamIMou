package com.example.usermodule;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.example.usermodule.net.IUserDataCallBack;
import com.example.usermodule.net.UserNetManager;
import com.mm.android.mobilecommon.openapi.TokenHelper;
import com.mm.android.mobilecommon.AppConsume.BusinessException;
import com.mm.android.mobilecommon.AppConsume.ProviderManager;
import com.mm.android.mobilecommon.base.BaseActivity;
import com.mm.android.mobilecommon.openapi.TokenHelper;
import com.mm.android.mobilecommon.route.RoutePathManager;
import com.mm.android.mobilecommon.utils.LogUtil;
import com.mm.android.mobilecommon.utils.WordInputFilter;
import com.mm.android.mobilecommon.widget.ClearEditText;

import static com.mm.android.mobilecommon.route.RoutePathManager.ActivityPath.LoginActivityPath;
import static com.mm.android.mobilecommon.utils.WordInputFilter.NEW_PHONE;
import static com.mm.android.mobilecommon.utils.WordInputFilter.REGEX_EMAIL_GUO;
import static com.mm.android.mobilecommon.utils.WordInputFilter.REGEX_EMAIl;
import static com.mm.android.mobilecommon.utils.WordInputFilter.REX_EMAIL;
import static com.mm.android.mobilecommon.utils.WordInputFilter.REX_PHONE;
@Route(path =LoginActivityPath )
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    public static final String TAG = BaseActivity.class.getSimpleName();
    ClearEditText mName;
    LinearLayout mLoginBtn;
    TextView mRegesiter;
    ImageView mReturn;
    private String mUserName;
    private int type;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        initView();
    }

    private void initView(){
        mName = findViewById(R.id.login_username);
        mLoginBtn = findViewById(R.id.login_login);
        mRegesiter = findViewById(R.id.user_register);
        mReturn = findViewById(R.id.iv_back);
        mReturn.setOnClickListener(this);
        mLoginBtn.setOnClickListener(this);
        mRegesiter.setOnClickListener(this);
//        mName.setText("112233@qq.com".trim());
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
        if (id == R.id.login_login) {
            if (0 == ProviderManager.getAppProvider().getAppType()){
                if (!(mUserName!=null&&mUserName.matches(NEW_PHONE))&&!(mUserName!=null&&mUserName.matches(REGEX_EMAIL_GUO))){
//                    Toast.makeText(LoginActivity.this,R.string.phone_not_match_tip,Toast.LENGTH_SHORT).show();
                    return;
                }
            }else{
                if (!(mUserName!=null&&mUserName.matches(REGEX_EMAIL_GUO))){
//                    Toast.makeText(LoginActivity.this,R.string.phone_not_match_tip,Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            UserNetManager.getInstance().getOpenIdByAccount(mUserName, new IUserDataCallBack() {
                @Override
                public void onCallBackOpenId(String str) {
                    LogUtil.debugLog(TAG,"openid::"+str);
                    if (str!=null){
                        TokenHelper.getInstance().openid = str;
                        getSubAccountToken(str);
                    }else{
                        Toast.makeText(LoginActivity.this,"openid is not null",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    LogUtil.debugLog(TAG,"msg:::"+throwable.getMessage());
                    if (throwable.getMessage().contains("OP1009")){
//                        Toast.makeText(LoginActivity.this,R.string.account_not_has,Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(LoginActivity.this,throwable.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else if (id == R.id.user_register) {
            toRegesiterPage();
        }else if (id == R.id.iv_back){
            finish();
        }
    }


    private void toRegesiterPage(){
        Intent intent = new Intent(this,UserRegesiterActivity.class);
        startActivity(intent);
    }

    private void getSubAccountToken(String openId){
        UserNetManager.getInstance().subAccountToken(openId, new IUserDataCallBack() {
            @Override
            public void onCallBackOpenId(String str) {
                if (str!=null){
                    //暂不保存到sp中了，因为获取需要context。
                    //  PreferencesHelper.getInstance(getApplicationContext()).set(Constants.SUBACCOUNTTOKEN,str);
                    LogUtil.debugLog(TAG,"str:::"+str);
                    TokenHelper.getInstance().setSubAccessToken(str, LoginActivity.this.getApplication());
                    toDeviceList();
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
