package com.lc.message.popup

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Message
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import com.lc.message.R
import com.mm.android.mobilecommon.utils.ImageHelper
import com.mm.android.mobilecommon.utils.UIUtils
import pl.droidsonroids.gif.GifImageView

class PicDetailPopup(private val context: Context) :
    PopupWindow(UIUtils.dip2px(context, 345f), UIUtils.dip2px(context, 245f)) {

    private val picView: View =
        LayoutInflater.from(context).inflate(R.layout.popup_pic_detail, null)

    private var picIv: GifImageView

    init {
        isOutsideTouchable = true
        isTouchable = true
        isFocusable = true
        picIv = picView.findViewById(R.id.pic_detail_iv)
        setOnDismissListener {
            dismiss()
        }
    }

    fun show(deviceId: String, picUrl: String, view: View) {
        contentView = picView
        try {
            ImageHelper.loadCacheImage(picUrl, deviceId, deviceId, 0,
                object : Handler() {
                    override fun handleMessage(msg: Message) {
                        super.handleMessage(msg)
                        try {
                            picIv.setImageDrawable(msg.obj as Drawable)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        animationStyle = R.style.PopupDialog_Scale
        showAtLocation(view, Gravity.CENTER, 0, 0)
    }
}