package com.lc.message.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lc.message.R
import com.lc.message.entity.AlarmMassge
import com.mm.android.mobilecommon.utils.ImageHelper
import com.mm.android.mobilecommon.utils.TimeUtils
import pl.droidsonroids.gif.GifImageView
import java.util.*

class MessageAdapter(
        private val mContext: Context,
        private val messageList: MutableList<AlarmMassge>,
        private val delAlarmMassge: (AlarmMassge) -> Unit,
        private val showCover: (String) -> Unit
) :
    RecyclerView.Adapter<MessageAdapter.MessageHolder>() {

    class MessageHolder(view: View) : RecyclerView.ViewHolder(view) {
        val msgContentTv: TextView = view.findViewById(R.id.msg_content)
        val msgTimeTv: TextView = view.findViewById(R.id.msg_time)
        val msgCoverIv: GifImageView = view.findViewById(R.id.msg_cover)
        val msgDelLayout: ViewGroup = view.findViewById(R.id.layout_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHolder {
        val view: View =
            LayoutInflater.from(mContext).inflate(R.layout.message_adapter, parent, false)
        return MessageHolder(view)
    }

    override fun onBindViewHolder(holder: MessageHolder, position: Int) {
        val alarmMassge = messageList[position]
        holder.msgContentTv.text =  if (TextUtils.isEmpty(alarmMassge.msgType)) mContext.getText(R.string.common_other) else alarmMassge.msgType
        holder.msgTimeTv.text = TimeUtils.string2String(alarmMassge.localDate,TimeUtils.LONG_FORMAT,TimeUtils.SHORT_FORMAT)
        try {
            ImageHelper.loadCacheImage(
                    alarmMassge.thumbUrl,
                    alarmMassge.deviceId,
                    alarmMassge.deviceId,
                    position,
                    object : Handler() {
                        override fun handleMessage(msg: Message) {
                            super.handleMessage(msg)
                            try {
                                if (messageList[msg.arg1].thumbUrl.hashCode() == msg.what && msg.obj != null) {
                                    holder.msgCoverIv.setImageDrawable(msg.obj as Drawable)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        holder.msgDelLayout.setOnClickListener {
            delAlarmMassge(alarmMassge)
        }
        holder.msgCoverIv.setOnClickListener {
            if (alarmMassge.picurlArray.isNotEmpty()) {
                showCover(alarmMassge.picurlArray[0])
            } else {
                showCover(alarmMassge.thumbUrl)
            }

        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }
}