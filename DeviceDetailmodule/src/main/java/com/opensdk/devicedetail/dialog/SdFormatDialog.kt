package com.opensdk.devicedetail.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.mm.android.mobilecommon.base.BaseDialogFragment
import com.opensdk.devicedetail.R


class SdFormatDialog : BaseDialogFragment() {
    private var tvCancel: TextView? = null
    private var tvFormat: TextView? = null
    private var onClick: OnClick? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireActivity(), R.style.checks_dialog)
        val inflater = requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.fragment_sd_dialog_tip, null, false)
        initView(view)
        dialog.setContentView(view)
        return dialog
    }

    private fun initView(mView: View) {
        tvCancel = mView.findViewById(R.id.tv_cancel)
        tvFormat = mView.findViewById(R.id.tv_format)
        tvCancel?.setOnClickListener(View.OnClickListener { dismiss() })
        tvFormat?.setOnClickListener(View.OnClickListener {
            if (onClick != null) {
                onClick?.onSure()
                dismiss()
            }
        })
    }

    interface OnClick {
        fun onSure()
    }

    fun setOnClick(onClick: OnClick) {
        this.onClick = onClick
    }
}