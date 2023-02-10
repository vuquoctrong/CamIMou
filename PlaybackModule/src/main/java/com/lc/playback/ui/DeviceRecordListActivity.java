package com.lc.playback.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.lc.playback.R;
import com.lc.playback.contacts.MethodConst;


public class DeviceRecordListActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = DeviceRecordListActivity.class.getSimpleName();
    private TextView tvCloudRecord;
    private TextView tvLocalRecord;
    private Bundle bundle;
    private DeviceCloudRecordListFragment deviceCloudRecordListFragment = DeviceCloudRecordListFragment.newInstance();
    private DeviceLocalRecordListFragment deviceLocalRecordListFragment = DeviceLocalRecordListFragment.newInstance();
    private FragmentManager supportFragmentManager;
    private Fragment currentFragment;
    public LinearLayout llEdit;
    public LinearLayout llAll;
    public LinearLayout llBack;
    public TextView tvEdit;
    private int recordType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_record_list);
        supportFragmentManager = getSupportFragmentManager();
        initView();
        initData();
    }

    private void initData() {
        bundle = getIntent().getExtras();
        recordType = bundle.getInt(MethodConst.ParamConst.recordType);
        if (recordType == MethodConst.ParamConst.recordTypeCloud ? showFragment(deviceCloudRecordListFragment) : showFragment(deviceLocalRecordListFragment)) {
            tvCloudRecord.setSelected(recordType == MethodConst.ParamConst.recordTypeCloud);
            tvLocalRecord.setSelected(recordType == MethodConst.ParamConst.recordTypeLocal);
        }
    }

    private void initView() {
        findViewById(R.id.ll_back).setOnClickListener(this);
        llBack = findViewById(R.id.ll_back);
        llEdit = findViewById(R.id.ll_edit);
        llAll = findViewById(R.id.ll_all);
        tvEdit = findViewById(R.id.tv_edit);
        tvCloudRecord = findViewById(R.id.tv_cloud_record);
        tvLocalRecord = findViewById(R.id.tv_local_record);
        tvCloudRecord.setOnClickListener(this);
        tvLocalRecord.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ll_back) {
            finish();
        } else if (id == R.id.tv_cloud_record) {
            if (showFragment(deviceCloudRecordListFragment)) {
                tvCloudRecord.setSelected(true);
                tvLocalRecord.setSelected(false);
            }
        } else if (id == R.id.tv_local_record) {
            if (showFragment(deviceLocalRecordListFragment)) {
                tvCloudRecord.setSelected(false);
                tvLocalRecord.setSelected(true);
            }
        }
    }

    private boolean showFragment(Fragment fragment) {
        if (currentFragment != fragment) {
            FragmentTransaction transaction = supportFragmentManager.beginTransaction();
            if (currentFragment != null) {
                transaction.hide(currentFragment);
            }
            currentFragment = fragment;
            fragment.setArguments(bundle);
            if (!fragment.isAdded()) {
                transaction.add(R.id.fr_content, fragment).show(fragment).commit();
            } else {
                transaction.show(fragment).commit();
            }
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            boolean delete = data.getBooleanExtra("data", false);
            if (delete && deviceCloudRecordListFragment != null) {
                deviceCloudRecordListFragment.deleteCloudRecord();
            }
        }
    }
}
