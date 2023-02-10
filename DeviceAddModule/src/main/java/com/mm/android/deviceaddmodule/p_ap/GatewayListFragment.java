package com.mm.android.deviceaddmodule.p_ap;


import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mm.android.deviceaddmodule.R;
import com.mm.android.deviceaddmodule.adapter.GatewayListAdapter;
import com.mm.android.deviceaddmodule.base.BaseDevAddFragment;
import com.mm.android.deviceaddmodule.contract.GatewayListConstract;
import com.mm.android.deviceaddmodule.helper.DeviceAddHelper;
import com.mm.android.deviceaddmodule.helper.PageNavigationHelper;
import com.mm.android.mobilecommon.entity.device.LCDevice;
import com.mm.android.deviceaddmodule.presenter.GatewayListPresenter;

import java.util.List;

/**
 * Gateway List Page
 *
 * 网关列表页
 **/
public class GatewayListFragment extends BaseDevAddFragment implements GatewayListConstract.View, View.OnClickListener,AdapterView.OnItemClickListener{
    private static final String SELECT_FLAG_PARAM="select_flag_param";
    GatewayListConstract.Presenter mPresenter;
    ListView mGatewayList;
    TextView mEmptyTipTxt,mApSnTxt,mNextBtn;
    ImageView mApImg;
    GatewayListAdapter mAdapter;

    public static GatewayListFragment newInstance(boolean hasSelectGateway) {
        GatewayListFragment fragment = new GatewayListFragment();
        Bundle args = new Bundle();
        args.putBoolean(SELECT_FLAG_PARAM,hasSelectGateway);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gateway_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DeviceAddHelper.updateTile(DeviceAddHelper.TitleMode.MORE);
    }

    @Override
    protected void initView(View view) {
        mGatewayList= view.findViewById(R.id.gatway_list);
        mEmptyTipTxt= view.findViewById(R.id.empty_tip);
        mApImg= view.findViewById(R.id.iv_ap_img);
        mApSnTxt= view.findViewById(R.id.tv_ap_sn);
        mNextBtn= view.findViewById(R.id.tv_next);
        mNextBtn.setOnClickListener(this);
        mGatewayList.setOnItemClickListener(this);
    }

    @Override
    protected void initData() {
        mPresenter=new GatewayListPresenter(this);
        boolean hasSelectGateway=false;
        if(getArguments()!=null){
            hasSelectGateway=getArguments().getBoolean(SELECT_FLAG_PARAM);
        }
        List<LCDevice> data=mPresenter.getGatewayData(hasSelectGateway);
        if(data!=null&&data.size()>0) {
            mNextBtn.setEnabled(true);
            mAdapter = new GatewayListAdapter(data, getActivity());
            mGatewayList.setAdapter(mAdapter);
            setSelectedPos(mPresenter.getSelectedpos());
            mNextBtn.setEnabled(mAdapter.getSelectPosition()!=-1);
        }else{
            showEmptyTip();
        }
    }

    private void showEmptyTip(){
        mGatewayList.setVisibility(View.GONE);
        mEmptyTipTxt.setVisibility(View.VISIBLE);
        mNextBtn.setEnabled(false);
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(id==R.id.tv_next){
            int curPos=0;
            if(mAdapter!=null) {
                curPos = mAdapter.getSelectPosition();
            }
            mPresenter.dispatchCurSelect(curPos);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LCDevice device = mAdapter.getItem(position);
        //只有在线的网关才可点击
        if (device != null && device.isOnline()) {
            if(position==mAdapter.getSelectPosition()) {//取消选择
                mAdapter.setSelectPosition(-1);
            }else{
                mAdapter.setSelectPosition(position);
            }
            mNextBtn.setEnabled(mAdapter.getSelectPosition()!=-1);
        }
    }

    @Override
    public void goTipPage() {
        PageNavigationHelper.gotoApPowerTipPage(this);
    }

    @Override
    public void setApSn(String apSn) {
        mApSnTxt.setText(apSn);
    }

    @Override
    public void setApImg(String img) {
        if (!TextUtils.isEmpty(img)) {
            Glide.with(GatewayListFragment.this.getContext()).load(img).into(mApImg);
        }
    }

    @Override
    public void setSelectedPos(int pos) {
        mAdapter.setSelectPosition(pos);
        mAdapter.notifyDataSetChanged();
    }
}
