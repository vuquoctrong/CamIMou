package com.lc.message.popup

import android.content.Context
import android.graphics.Rect
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lc.message.R
import com.lc.message.adapter.MessageEditAdapter
import com.lc.message.entity.AlarmMassge
import com.mm.android.mobilecommon.dialog.LCAlertDialog
import com.mm.android.mobilecommon.utils.UIUtils

class MessageEditPopup(
    private val context: Context,
    titleName: String,
    private val del: (messageList: MutableList<AlarmMassge>) -> Unit
) :
    PopupWindow(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT) {

    private val picView: View =
        LayoutInflater.from(context).inflate(R.layout.popup_message_edit, null).apply {
            findViewById<ImageView>(R.id.close_iv).apply {
                setOnClickListener {
                    dismiss()
                    messageEditAdapter?.setSelectedAll(false)
                    selectedTv.setText(R.string.message_selectall)
                    deleteBtn.isEnabled = messageEditAdapter?.messageSelectList?.size ?: 0 > 0
                }
            }
        }
    private val selectedTv: TextView = picView.findViewById(R.id.selected_tv)
    private val titleTv: TextView = picView.findViewById(R.id.title_tv)
    private val deleteBtn: Button = picView.findViewById<Button>(R.id.delete_btn).apply {
        isEnabled = false
        setOnClickListener { deleteDialog() }
    }
    private val editRecyclerView: RecyclerView =
        picView.findViewById<RecyclerView?>(R.id.edit_recycler).apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(
                object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        if (parent.getChildAdapterPosition(view) != 0) {
                            outRect.top = UIUtils.dip2px(context, 0.3f)
                            outRect.bottom = UIUtils.dip2px(context, 0.3f)
                        }
                    }
                }
            )
        }
    private var messageEditAdapter: MessageEditAdapter? = null

    init {
        titleTv.text = titleName
        selectedTv.setOnClickListener {
            if (selectedTv.text == context.resources.getString(R.string.message_selectall)) {
                messageEditAdapter?.setSelectedAll(true)
                selectedTv.setText(R.string.message_unselectall)
            } else {
                messageEditAdapter?.setSelectedAll(false)
                selectedTv.setText(R.string.message_selectall)
            }
            deleteBtn.isEnabled = messageEditAdapter?.messageSelectList?.size ?: 0 > 0
        }
    }

    private fun deleteDialog() {
        (context as? AppCompatActivity)?.let {
            val dialog: LCAlertDialog = LCAlertDialog.Builder(it)
                    .setGravity2(Gravity.CENTER)
                    .setTitle(R.string.message_module_delete_message_tip)
                    .setMessage(R.string.message_module_delete_message_confirm)
                    .setCancelButton(R.string.common_cancel_big, null)
                    .setConfirmButton(R.string.message_module_confirm_delete) { dialog, which, isChecked ->
                        messageEditAdapter?.let { del(it.messageSelectList) }
                        dialog.dismiss()
                    }.create()
            dialog.show(it.supportFragmentManager, null)
        }
    }

    fun show(view: View, messageList: MutableList<AlarmMassge>) {
        contentView = picView
        messageEditAdapter = MessageEditAdapter(context, messageList) {
            deleteBtn.isEnabled = messageEditAdapter?.messageSelectList?.size ?: 0 > 0
        }
        editRecyclerView.adapter = messageEditAdapter
        animationStyle = R.style.PopupDialog
        showAtLocation(view, Gravity.CENTER, 0, 0)
    }
}