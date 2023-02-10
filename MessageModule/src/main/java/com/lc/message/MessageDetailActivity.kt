package com.lc.message

import android.app.ProgressDialog
import android.graphics.Rect
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lc.message.adapter.MessageAdapter
import com.lc.message.api.DeviceMessageService
import com.lc.message.api.IMessageCallBack
import com.lc.message.api.data.AlarmMessageData
import com.lc.message.entity.AlarmMassge
import com.lc.message.popup.MessageEditPopup
import com.lc.message.popup.PicDetailPopup
import com.lc.message.view.MessageDateView
import com.lechange.common.log.Logger
import com.lechange.pulltorefreshlistview.Mode
import com.lechange.pulltorefreshlistview.PullToRefreshBase
import com.mm.android.mobilecommon.openapi.TokenHelper
import com.mm.android.mobilecommon.utils.TimeUtils
import com.mm.android.mobilecommon.utils.UIUtils
import com.mm.android.mobilecommon.widget.LcPullToRefreshRecyclerView

import java.util.*


class MessageDetailActivity : AppCompatActivity(),
        PullToRefreshBase.OnRefreshListener2<RecyclerView>,
        View.OnClickListener, MessageDateView.OnDayChangeListener {

    private lateinit var recyclerView: LcPullToRefreshRecyclerView
    private lateinit var tvTitle: TextView
    private lateinit var messageDataView: MessageDateView
    private lateinit var ivEdit: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var resultLayout: ViewGroup
    private lateinit var resultIV: ImageView
    private lateinit var descTv: TextView

    private val mProgressDialog: ProgressDialog by lazy {
        val progressDialog =
                ProgressDialog(this@MessageDetailActivity, R.style.mobile_common_custom_dialog)
        val window = progressDialog.window
        window?.setWindowAnimations(R.style.mobile_common_dialog_anima)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog
    }

    private val callBack = object : IMessageCallBack.IAlarmMessageCallBack {
        override fun getAlarmMessage(result: AlarmMessageData.Response, dateString: String) {
            messageList.addAll(result.data.alarms)
            messageAdapter.notifyDataSetChanged()
            cache[dateString]?.let {
                it.data.nextAlarmId = result.data.nextAlarmId
                it.data.alarms.addAll(result.data.alarms)
            } ?: let { cache[dateString] = result }
            recyclerView.onRefreshComplete()
            // 查询到的数量不等于请求的数量，等于没有数据了
            if (result.data.alarms.size != MAX_COUNT) {
                recyclerView.mode = Mode.PULL_FROM_START
            } else {
                recyclerView.mode = Mode.BOTH
            }
            if (cache[dateString]?.data?.alarms?.size == 0) {
                noMessage()
            } else {
                showMessage()
            }

        }

        override fun deleteSuccess(alarmMassges: List<AlarmMassge>) {
            messageEditPopup.dismiss()
            mProgressDialog.dismiss()
            messageList.removeAll(alarmMassges)
            messageAdapter.notifyDataSetChanged()
            if (messageList.size == 0) {
                getAlarmMessage(messageDataView.dateList[messageDataView.selectLastPosition()])
            }
        }

        override fun onError(throwable: Throwable, dateString: String) {
            if (TextUtils.isEmpty(dateString)) {
                mProgressDialog.dismiss()
                Toast.makeText(this@MessageDetailActivity, throwable.message, Toast.LENGTH_LONG)
                        .show()
                return
            }
            recyclerView.onRefreshComplete()
            cache[dateString]?.let {
                showMessage()
            } ?: onError()
        }

    }
    private val messageList = mutableListOf<AlarmMassge>()
    private var deviceId = ""
    private var channelId = ""
    private var picDetailPopup: PicDetailPopup? = null
    private val messageEditPopup: MessageEditPopup by lazy {
        MessageEditPopup(this, tvTitle.text.toString()) {
            mProgressDialog.show()
            DeviceMessageService.newInstance()
                    .deleteAlarmMessage(
                            TokenHelper.getInstance().accessToken,
                            it,
                            deviceId,
                            channelId,
                            callBack
                    )
        }
    }
    private val messageAdapter: MessageAdapter by lazy {
        MessageAdapter(this, messageList, {
            mProgressDialog.show()
            DeviceMessageService.newInstance()
                    .deleteAlarmMessage(
                            TokenHelper.getInstance().accessToken,
                            arrayListOf<AlarmMassge>().apply { add(it) },
                            deviceId,
                            channelId,
                            callBack
                    )
        }, {
            if (picDetailPopup?.isShowing == true) {
                picDetailPopup?.dismiss()
                picDetailPopup = null
            }
            picDetailPopup = PicDetailPopup(this)
            picDetailPopup!!.show(deviceId, it, findViewById(android.R.id.content))
        })
    }
    private val cache = HashMap<String, AlarmMessageData.Response>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_detail)
        initView()
        initData()
    }

    private fun initData() {
        deviceId = intent.getStringExtra("deviceId") ?: ""
        channelId = intent.getStringExtra("channelId") ?: ""
        tvTitle.text = intent.getStringExtra("deviceName")
        getAlarmMessage(Date())
    }

    private fun initView() {
        tvTitle = findViewById(R.id.tv_message_title)
        recyclerView = findViewById(R.id.message_recycler)
        findViewById<ImageView>(R.id.iv_back).setOnClickListener(this)
        messageDataView = findViewById(R.id.alarm_message_date_view)
        progressBar = findViewById(R.id.message_progress_bar)
        resultLayout = findViewById(R.id.result_layout)
        resultIV = findViewById(R.id.result_iv)
        descTv = findViewById(R.id.desc_tv)
        ivEdit =
                findViewById<ImageView?>(R.id.iv_edit).apply { setOnClickListener(this@MessageDetailActivity) }
        messageDataView.setDayChangeListener(this)
        recyclerView.refreshableView.layoutManager = LinearLayoutManager(this)
        recyclerView.mode = Mode.BOTH
        recyclerView.setOnRefreshListener(this@MessageDetailActivity)
        recyclerView.refreshableView.adapter = messageAdapter
        recyclerView.refreshableView.addItemDecoration(
                object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                            outRect: Rect,
                            view: View,
                            parent: RecyclerView,
                            state: RecyclerView.State
                    ) {
                        if (parent.getChildAdapterPosition(view) != 0) {
                            outRect.top = UIUtils.dip2px(this@MessageDetailActivity, 0.3f)
                            outRect.bottom = UIUtils.dip2px(this@MessageDetailActivity, 0.3f)
                        }
                    }
                }
        )
    }

    private fun getAlarmMessage(date: Date, isLoading: Boolean = true) {
        val dateString = TimeUtils.long2String(date.time, TimeUtils.REQUEST_DATE_FORMAT)
        Logger.e("======", "dateString:$dateString")
        resultLayout.visibility = View.GONE
        AlarmMessageData().apply {
            data.channelId = channelId
            data.deviceId = deviceId
            data.count = 30
            data.token = TokenHelper.getInstance().subAccessToken
            data.beginTime = TimeUtils.getBeginTime(date)
            data.endTime = TimeUtils.getEndTime(date)
            cache[dateString]?.let {
                if (it.data.alarms.size == 0) {
                    if (TimeUtils.isToday(Calendar.getInstance().apply { time = date })) {
                        data.nextAlarmId = ""
                        onLoading()
                        DeviceMessageService.newInstance()
                                .getAlarmMessage(dateString, this, callBack)
                    } else {
                        noMessage()
                    }
                } else {
                    showMessage()
                    if (isLoading) {
                        data.nextAlarmId = "${it.data.nextAlarmId}"
                        DeviceMessageService.newInstance().getAlarmMessage(dateString, this, callBack)
                    }
                }
            } ?: let {
                data.nextAlarmId = ""
                onLoading()
                DeviceMessageService.newInstance().getAlarmMessage(dateString, this, callBack)
            }
        }
    }

    override fun onPullDownToRefresh(p0: PullToRefreshBase<RecyclerView>?) {
        messageList.clear()
        messageAdapter.notifyDataSetChanged()
        val date = messageDataView.dateList[messageDataView.selectLastPosition()]
        val dateString = TimeUtils.long2String(date.time, TimeUtils.REQUEST_DATE_FORMAT)
        cache.remove(dateString)
        getAlarmMessage(date)
    }

    override fun onPullUpToRefresh(p0: PullToRefreshBase<RecyclerView>?) {
        val date = messageDataView.dateList[messageDataView.selectLastPosition()]
        val dateString = TimeUtils.long2String(date.time, TimeUtils.REQUEST_DATE_FORMAT)
        Logger.e("======", "dateString:$dateString")
        cache[dateString]?.let {
            val messageData = AlarmMessageData().apply {
                data.channelId = channelId
                data.deviceId = deviceId
                data.count = 30
                data.token = TokenHelper.getInstance().subAccessToken
                data.beginTime = TimeUtils.getBeginTime(date)
                data.endTime = TimeUtils.getEndTime(date)
                data.nextAlarmId = "${it.data.nextAlarmId}"
            }
            DeviceMessageService.newInstance().getAlarmMessage(dateString, messageData, callBack)
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_back -> onBackPressed()
            R.id.result_layout -> getAlarmMessage(messageDataView.dateList[messageDataView.selectLastPosition()])
            R.id.iv_edit -> {
                messageList.takeIf {
                    it.size > 0
                }?.let {
                    messageEditPopup.show(findViewById(android.R.id.content), messageList)
                } ?: let {
                    Toast.makeText(this, R.string.no_message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onBackPressed() {
        when {
            messageEditPopup.isShowing -> {
                messageEditPopup.dismiss()
            }
            picDetailPopup?.isShowing == true -> {
                picDetailPopup?.dismiss()
                picDetailPopup = null
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    override fun onDayChangeClick(position: Int, toDate: Date) {
        messageList.clear()
        val dateString = TimeUtils.long2String(toDate.time, TimeUtils.REQUEST_DATE_FORMAT)
        Logger.e("======", "dateString:$dateString")
        resultLayout.visibility = View.GONE
        AlarmMessageData().apply {
            data.channelId = channelId
            data.deviceId = deviceId
            data.count = MAX_COUNT
            data.token = TokenHelper.getInstance().subAccessToken
            data.beginTime = TimeUtils.getBeginTime(toDate)
            data.endTime = TimeUtils.getEndTime(toDate)
            cache[dateString]?.let {
                if (it.data.alarms.size == 0) {
                    if (TimeUtils.isToday(Calendar.getInstance().apply { time = toDate })) {
                        data.nextAlarmId = ""
                        onLoading()
                        DeviceMessageService.newInstance()
                                .getAlarmMessage(dateString, this, callBack)
                    } else {
                        noMessage()
                    }
                } else {
                    // 支持上啦和下拉操作
                    recyclerView.mode = Mode.BOTH
                    showMessage()
                    messageList.addAll(it.data.alarms)
                    messageAdapter.notifyDataSetChanged()
                }
            } ?: let {
                data.nextAlarmId = ""
                onLoading()
                DeviceMessageService.newInstance().getAlarmMessage(dateString, this, callBack)
            }
        }
    }


    private fun noMessage() {
        recyclerView.visibility = View.GONE
        progressBar.visibility = View.GONE
        resultLayout.visibility = View.VISIBLE
        resultLayout.setOnClickListener(null)
        descTv.setText(R.string.no_message)
        resultIV.setImageResource(R.mipmap.pic_quesheng1)
    }

    private fun onError() {
        recyclerView.visibility = View.GONE
        progressBar.visibility = View.GONE
        resultLayout.visibility = View.VISIBLE
        resultLayout.setOnClickListener(this@MessageDetailActivity)
        val spannableBuild =
                SpannableStringBuilder(getString(R.string.message_fail_desc))
        // 多语言适配，翻译时需要注意“，”
        val span = ForegroundColorSpan(resources.getColor(R.color.c20))
        val lastIndexOf = spannableBuild.lastIndexOf(",") + 1
        spannableBuild.setSpan(span, lastIndexOf, spannableBuild.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        descTv.text = spannableBuild
        resultIV.setImageResource(R.mipmap.pic_quesheng2)
    }

    private fun showMessage() {
        recyclerView.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        resultLayout.visibility = View.GONE
    }

    private fun onLoading() {
        recyclerView.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        resultLayout.visibility = View.GONE
    }

    companion object {
        private const val MAX_COUNT = 30
    }
}