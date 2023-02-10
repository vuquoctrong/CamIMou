package com.lc.message.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lc.message.R
import com.lc.message.entity.AlarmMassge
import com.mm.android.mobilecommon.utils.ImageHelper
import com.mm.android.mobilecommon.utils.TimeUtils
import pl.droidsonroids.gif.GifImageView

class MessageEditAdapter(
    private val mContext: Context,
    private val messageList: MutableList<AlarmMassge>,
    val updateCheck: () -> Unit
) :
    RecyclerView.Adapter<MessageEditAdapter.MessageHolder>() {

    val messageSelectList = mutableListOf<AlarmMassge>()

    class MessageHolder(view: View) : RecyclerView.ViewHolder(view) {
        val msgContentTv: TextView = view.findViewById(R.id.msg_content)
        val msgTimeTv: TextView = view.findViewById(R.id.msg_time)
        val msgCoverIv: GifImageView = view.findViewById(R.id.msg_cover)
        val msgCheckboxIv: ImageView = view.findViewById(R.id.checkbox_iv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHolder {
        val view: View =
            LayoutInflater.from(mContext).inflate(R.layout.message_edit_adapter, parent, false)
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
        holder.msgCheckboxIv.isSelected = messageSelectList.contains(alarmMassge)
        holder.itemView.setOnClickListener {
            updateCheck(holder.msgCheckboxIv, alarmMassge)
        }
    }

    private fun updateCheck(checkBoxIv: ImageView, alarmMassge: AlarmMassge) {
        if (messageSelectList.contains(alarmMassge)) {
            checkBoxIv.isSelected = false
            messageSelectList.remove(alarmMassge)
        } else {
            checkBoxIv.isSelected = true
            messageSelectList.add(alarmMassge)
        }
        updateCheck()
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    fun setSelectedAll(isAll: Boolean) {
        if (isAll) {
            messageSelectList.addAll(messageList)
        } else {
            messageSelectList.clear()
        }
        notifyDataSetChanged()
    }
}