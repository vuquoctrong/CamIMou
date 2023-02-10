package com.opensdk.devicedetail.ui

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.mm.android.deviceaddmodule.base.BaseDevAddFragment
import com.mm.android.mobilecommon.utils.DialogUtils
import com.opensdk.devicedetail.R
import com.opensdk.devicedetail.constance.MethodConst
import com.opensdk.devicedetail.contract.SdCardContract
import com.opensdk.devicedetail.dialog.SdFormatDialog
import com.opensdk.devicedetail.entity.DeviceDetailListData
import com.opensdk.devicedetail.presenter.SdCardPresenter
import com.opensdk.devicedetail.tools.GsonUtils

class SdCardFragment : BaseDevAddFragment(), SdCardContract.View, View.OnClickListener {
    private var mPresenter: SdCardContract.Presenter? = null
    private var deviceListBean: DeviceDetailListData.ResponseData.DeviceListBean? = null
    private var mView: View? = null
    private var mIvStoreStatus: ImageView? = null
    private var mTvStoreStatus: TextView? = null
    private var mPbStore: ProgressBar? = null
    private var mTvUseSpace: TextView? = null
    private var mBtnFormat: Button? = null
    private var sdFormatDialog: SdFormatDialog? = null
    private var mTvStoreBroken: TextView? = null


    //测试1111
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_sd_card, container, false)
        return mView
    }

    override fun initView(view: View?) {
        mIvStoreStatus = view?.findViewById(R.id.iv_store_status)
        mTvStoreStatus = view?.findViewById(R.id.tv_store_status)
        mPbStore = view?.findViewById(R.id.pb_store)
        mTvUseSpace = view?.findViewById(R.id.tv_use_space)
        mBtnFormat = view?.findViewById(R.id.btn_format)
        mBtnFormat?.setOnClickListener(this)
        mTvStoreBroken = view?.findViewById(R.id.tv_store_broken)
        mPresenter = SdCardPresenter(this)


        val deviceDetailActivity = activity as DeviceDetailActivity?
        deviceDetailActivity?.tvTitle?.text = resources.getString(R.string.sd_cards)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mPresenter?.onDestroyView()
    }

    override fun initData() {

        if (arguments == null) {
            return
        }
        val deviceListStr = arguments?.getString(MethodConst.ParamConst.deviceDetail)
        if (TextUtils.isEmpty(deviceListStr)) {
            return
        }
        deviceListBean = GsonUtils.fromJson(deviceListStr, DeviceDetailListData.ResponseData.DeviceListBean::class.java)

        mPresenter?.deviceSdCardStatus(deviceListBean?.deviceId)
    }

    override fun onClick(v: View) {
        val id = v.id
        if (id == R.id.btn_format) {
            showFormatDialog()
        }
    }

    override fun showSdNormalStatus() {
        hideView()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mIvStoreStatus?.setImageDrawable(activity?.getDrawable(R.drawable.sd_normal))
        } else {
            mIvStoreStatus?.setImageDrawable(activity?.resources?.getDrawable(R.drawable.sd_normal))
        }
        mTvStoreStatus?.setText(R.string.sd_normal)
        mPbStore?.visibility = View.VISIBLE
        mTvUseSpace?.visibility = View.VISIBLE
        mBtnFormat?.visibility = View.VISIBLE

    }

    override fun showSdBrokenStatus() {
        hideView()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mIvStoreStatus?.setImageDrawable(activity?.getDrawable(R.drawable.sd_fail))
        } else {
            mIvStoreStatus?.setImageDrawable(activity?.resources?.getDrawable(R.drawable.sd_fail))
        }
        mTvStoreBroken?.visibility = View.VISIBLE
        mBtnFormat?.visibility = View.VISIBLE
        mTvStoreStatus?.setText(R.string.sd_memory_broken)
    }

    override fun showSdFormatStatus() {
        hideView()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mIvStoreStatus?.setImageDrawable(activity?.getDrawable(R.drawable.sd_formatting))
        } else {
            mIvStoreStatus?.setImageDrawable(activity?.resources?.getDrawable(R.drawable.sd_formatting))
        }
        mTvStoreStatus?.setText(R.string.sd_formatting)
        mPresenter?.startFormat(deviceListBean?.deviceId)
    }


    override fun showSdStoreInfo(totalStorage: Long, usedStorage: Long,progressStore :Int) {
        //更新进度条
        mPbStore?.progress = progressStore
        //可用空间：totalStorage  已使用：usedStorage
        mTvUseSpace?.text = String.format(getString(R.string.sd_use_space), mPresenter?.getSizeGB(totalStorage - usedStorage), mPresenter?.getSizeGB(usedStorage))
    }


    override fun showFormatDialog() {
        sdFormatDialog = SdFormatDialog()
        sdFormatDialog?.show(requireActivity().supportFragmentManager, sdFormatDialog?.javaClass?.name)
        sdFormatDialog?.setOnClick(object : SdFormatDialog.OnClick {
            override fun onSure() {
                showSdFormatStatus()
            }
        })
    }

    private fun hideView() {
        mTvStoreBroken?.visibility = View.GONE
        mPbStore?.visibility = View.GONE
        mTvUseSpace?.visibility = View.GONE
        mTvUseSpace?.text = null
        mBtnFormat?.visibility = View.GONE
    }

    override fun showToastInfo(info: Int) {
        toastWithImg(info, 0)
    }

    override fun showLoadingDialog() {
        DialogUtils.show(activity)
    }

    override fun dismissLoadingDialog() {
        DialogUtils.dismiss()
    }

    companion object {
        fun newInstance(): SdCardFragment {
            val fragment = SdCardFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}