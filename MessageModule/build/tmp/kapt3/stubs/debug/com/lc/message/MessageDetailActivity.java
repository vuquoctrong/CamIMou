package com.lc.message;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 0}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u00b8\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u0000 M2\u00020\u00012\b\u0012\u0004\u0012\u00020\u00030\u00022\u00020\u00042\u00020\u0005:\u0001MB\u0005\u00a2\u0006\u0002\u0010\u0006J\u001a\u00102\u001a\u0002032\u0006\u00104\u001a\u0002052\b\b\u0002\u00106\u001a\u000207H\u0002J\b\u00108\u001a\u000203H\u0002J\b\u00109\u001a\u000203H\u0002J\b\u0010:\u001a\u000203H\u0002J\b\u0010;\u001a\u000203H\u0016J\u0012\u0010<\u001a\u0002032\b\u0010=\u001a\u0004\u0018\u00010>H\u0016J\u0012\u0010?\u001a\u0002032\b\u0010@\u001a\u0004\u0018\u00010AH\u0014J\u0018\u0010B\u001a\u0002032\u0006\u0010C\u001a\u00020D2\u0006\u0010E\u001a\u000205H\u0016J\b\u0010F\u001a\u000203H\u0002J\b\u0010G\u001a\u000203H\u0002J\u0018\u0010H\u001a\u0002032\u000e\u0010I\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010JH\u0016J\u0018\u0010K\u001a\u0002032\u000e\u0010I\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010JH\u0016J\b\u0010L\u001a\u000203H\u0002R\u001a\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\n0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082.\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u0013\u001a\u00020\u00148BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0017\u0010\u0018\u001a\u0004\b\u0015\u0010\u0016R\u001b\u0010\u0019\u001a\u00020\u001a8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u001d\u0010\u0018\u001a\u0004\b\u001b\u0010\u001cR\u000e\u0010\u001e\u001a\u00020\u001fX\u0082.\u00a2\u0006\u0002\n\u0000R\u001b\u0010 \u001a\u00020!8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b$\u0010\u0018\u001a\u0004\b\"\u0010#R\u0014\u0010%\u001a\b\u0012\u0004\u0012\u00020\'0&X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010(\u001a\u0004\u0018\u00010)X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010*\u001a\u00020+X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010,\u001a\u00020-X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010.\u001a\u00020\u0012X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010/\u001a\u000200X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u00101\u001a\u00020\u000fX\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006N"}, d2 = {"Lcom/lc/message/MessageDetailActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "Lcom/lechange/pulltorefreshlistview/PullToRefreshBase$OnRefreshListener2;", "Landroidx/recyclerview/widget/RecyclerView;", "Landroid/view/View$OnClickListener;", "Lcom/lc/message/view/MessageDateView$OnDayChangeListener;", "()V", "cache", "Ljava/util/HashMap;", "", "Lcom/lc/message/api/data/AlarmMessageData$Response;", "callBack", "Lcom/lc/message/api/IMessageCallBack$IAlarmMessageCallBack;", "channelId", "descTv", "Landroid/widget/TextView;", "deviceId", "ivEdit", "Landroid/widget/ImageView;", "mProgressDialog", "Landroid/app/ProgressDialog;", "getMProgressDialog", "()Landroid/app/ProgressDialog;", "mProgressDialog$delegate", "Lkotlin/Lazy;", "messageAdapter", "Lcom/lc/message/adapter/MessageAdapter;", "getMessageAdapter", "()Lcom/lc/message/adapter/MessageAdapter;", "messageAdapter$delegate", "messageDataView", "Lcom/lc/message/view/MessageDateView;", "messageEditPopup", "Lcom/lc/message/popup/MessageEditPopup;", "getMessageEditPopup", "()Lcom/lc/message/popup/MessageEditPopup;", "messageEditPopup$delegate", "messageList", "", "Lcom/lc/message/entity/AlarmMassge;", "picDetailPopup", "Lcom/lc/message/popup/PicDetailPopup;", "progressBar", "Landroid/widget/ProgressBar;", "recyclerView", "Lcom/mm/android/mobilecommon/widget/LcPullToRefreshRecyclerView;", "resultIV", "resultLayout", "Landroid/view/ViewGroup;", "tvTitle", "getAlarmMessage", "", "date", "Ljava/util/Date;", "isLoading", "", "initData", "initView", "noMessage", "onBackPressed", "onClick", "v", "Landroid/view/View;", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onDayChangeClick", "position", "", "toDate", "onError", "onLoading", "onPullDownToRefresh", "p0", "Lcom/lechange/pulltorefreshlistview/PullToRefreshBase;", "onPullUpToRefresh", "showMessage", "Companion", "MessageModule_debug"})
public final class MessageDetailActivity extends androidx.appcompat.app.AppCompatActivity implements com.lechange.pulltorefreshlistview.PullToRefreshBase.OnRefreshListener2<androidx.recyclerview.widget.RecyclerView>, android.view.View.OnClickListener, com.lc.message.view.MessageDateView.OnDayChangeListener {
    private com.mm.android.mobilecommon.widget.LcPullToRefreshRecyclerView recyclerView;
    private android.widget.TextView tvTitle;
    private com.lc.message.view.MessageDateView messageDataView;
    private android.widget.ImageView ivEdit;
    private android.widget.ProgressBar progressBar;
    private android.view.ViewGroup resultLayout;
    private android.widget.ImageView resultIV;
    private android.widget.TextView descTv;
    private final kotlin.Lazy mProgressDialog$delegate = null;
    private final com.lc.message.api.IMessageCallBack.IAlarmMessageCallBack callBack = null;
    private final java.util.List<com.lc.message.entity.AlarmMassge> messageList = null;
    private java.lang.String deviceId = "";
    private java.lang.String channelId = "";
    private com.lc.message.popup.PicDetailPopup picDetailPopup;
    private final kotlin.Lazy messageEditPopup$delegate = null;
    private final kotlin.Lazy messageAdapter$delegate = null;
    private final java.util.HashMap<java.lang.String, com.lc.message.api.data.AlarmMessageData.Response> cache = null;
    private static final int MAX_COUNT = 30;
    public static final com.lc.message.MessageDetailActivity.Companion Companion = null;
    
    private final android.app.ProgressDialog getMProgressDialog() {
        return null;
    }
    
    private final com.lc.message.popup.MessageEditPopup getMessageEditPopup() {
        return null;
    }
    
    private final com.lc.message.adapter.MessageAdapter getMessageAdapter() {
        return null;
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void initData() {
    }
    
    private final void initView() {
    }
    
    private final void getAlarmMessage(java.util.Date date, boolean isLoading) {
    }
    
    @java.lang.Override()
    public void onPullDownToRefresh(@org.jetbrains.annotations.Nullable()
    com.lechange.pulltorefreshlistview.PullToRefreshBase<androidx.recyclerview.widget.RecyclerView> p0) {
    }
    
    @java.lang.Override()
    public void onPullUpToRefresh(@org.jetbrains.annotations.Nullable()
    com.lechange.pulltorefreshlistview.PullToRefreshBase<androidx.recyclerview.widget.RecyclerView> p0) {
    }
    
    @java.lang.Override()
    public void onClick(@org.jetbrains.annotations.Nullable()
    android.view.View v) {
    }
    
    @java.lang.Override()
    public void onBackPressed() {
    }
    
    @java.lang.Override()
    public void onDayChangeClick(int position, @org.jetbrains.annotations.NotNull()
    java.util.Date toDate) {
    }
    
    private final void noMessage() {
    }
    
    private final void onError() {
    }
    
    private final void showMessage() {
    }
    
    private final void onLoading() {
    }
    
    public MessageDetailActivity() {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 4, 0}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lcom/lc/message/MessageDetailActivity$Companion;", "", "()V", "MAX_COUNT", "", "MessageModule_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}