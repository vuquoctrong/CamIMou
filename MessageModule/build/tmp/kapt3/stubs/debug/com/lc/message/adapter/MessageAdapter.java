package com.lc.message.adapter;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 0}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0001\u0017BC\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\u0012\u0012\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\n0\t\u0012\u0012\u0010\u000b\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\n0\t\u00a2\u0006\u0002\u0010\rJ\b\u0010\u000e\u001a\u00020\u000fH\u0016J\u0018\u0010\u0010\u001a\u00020\n2\u0006\u0010\u0011\u001a\u00020\u00022\u0006\u0010\u0012\u001a\u00020\u000fH\u0016J\u0018\u0010\u0013\u001a\u00020\u00022\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u000fH\u0016R\u001a\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\n0\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u000b\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\n0\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0018"}, d2 = {"Lcom/lc/message/adapter/MessageAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lcom/lc/message/adapter/MessageAdapter$MessageHolder;", "mContext", "Landroid/content/Context;", "messageList", "", "Lcom/lc/message/entity/AlarmMassge;", "delAlarmMassge", "Lkotlin/Function1;", "", "showCover", "", "(Landroid/content/Context;Ljava/util/List;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;)V", "getItemCount", "", "onBindViewHolder", "holder", "position", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "MessageHolder", "MessageModule_debug"})
public final class MessageAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<com.lc.message.adapter.MessageAdapter.MessageHolder> {
    private final android.content.Context mContext = null;
    private final java.util.List<com.lc.message.entity.AlarmMassge> messageList = null;
    private final kotlin.jvm.functions.Function1<com.lc.message.entity.AlarmMassge, kotlin.Unit> delAlarmMassge = null;
    private final kotlin.jvm.functions.Function1<java.lang.String, kotlin.Unit> showCover = null;
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public com.lc.message.adapter.MessageAdapter.MessageHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull()
    android.view.ViewGroup parent, int viewType) {
        return null;
    }
    
    @java.lang.Override()
    public void onBindViewHolder(@org.jetbrains.annotations.NotNull()
    com.lc.message.adapter.MessageAdapter.MessageHolder holder, int position) {
    }
    
    @java.lang.Override()
    public int getItemCount() {
        return 0;
    }
    
    public MessageAdapter(@org.jetbrains.annotations.NotNull()
    android.content.Context mContext, @org.jetbrains.annotations.NotNull()
    java.util.List<com.lc.message.entity.AlarmMassge> messageList, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.lc.message.entity.AlarmMassge, kotlin.Unit> delAlarmMassge, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> showCover) {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 4, 0}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\r\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0011\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\b\u00a8\u0006\u0013"}, d2 = {"Lcom/lc/message/adapter/MessageAdapter$MessageHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "view", "Landroid/view/View;", "(Landroid/view/View;)V", "msgContentTv", "Landroid/widget/TextView;", "getMsgContentTv", "()Landroid/widget/TextView;", "msgCoverIv", "Lpl/droidsonroids/gif/GifImageView;", "getMsgCoverIv", "()Lpl/droidsonroids/gif/GifImageView;", "msgDelLayout", "Landroid/view/ViewGroup;", "getMsgDelLayout", "()Landroid/view/ViewGroup;", "msgTimeTv", "getMsgTimeTv", "MessageModule_debug"})
    public static final class MessageHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        @org.jetbrains.annotations.NotNull()
        private final android.widget.TextView msgContentTv = null;
        @org.jetbrains.annotations.NotNull()
        private final android.widget.TextView msgTimeTv = null;
        @org.jetbrains.annotations.NotNull()
        private final pl.droidsonroids.gif.GifImageView msgCoverIv = null;
        @org.jetbrains.annotations.NotNull()
        private final android.view.ViewGroup msgDelLayout = null;
        
        @org.jetbrains.annotations.NotNull()
        public final android.widget.TextView getMsgContentTv() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final android.widget.TextView getMsgTimeTv() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final pl.droidsonroids.gif.GifImageView getMsgCoverIv() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final android.view.ViewGroup getMsgDelLayout() {
            return null;
        }
        
        public MessageHolder(@org.jetbrains.annotations.NotNull()
        android.view.View view) {
            super(null);
        }
    }
}