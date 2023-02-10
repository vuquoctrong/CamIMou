package com.lc.message.adapter;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 0}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0007\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0001 B)\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\u0012\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t\u00a2\u0006\u0002\u0010\u000bJ\b\u0010\u0011\u001a\u00020\u0012H\u0016J\u0018\u0010\u0013\u001a\u00020\n2\u0006\u0010\u0014\u001a\u00020\u00022\u0006\u0010\u0015\u001a\u00020\u0012H\u0016J\u0018\u0010\u0016\u001a\u00020\u00022\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u0012H\u0016J\u000e\u0010\u001a\u001a\u00020\n2\u0006\u0010\u001b\u001a\u00020\u001cJ\u0018\u0010\b\u001a\u00020\n2\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020\u0007H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010\u00a8\u0006!"}, d2 = {"Lcom/lc/message/adapter/MessageEditAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lcom/lc/message/adapter/MessageEditAdapter$MessageHolder;", "mContext", "Landroid/content/Context;", "messageList", "", "Lcom/lc/message/entity/AlarmMassge;", "updateCheck", "Lkotlin/Function0;", "", "(Landroid/content/Context;Ljava/util/List;Lkotlin/jvm/functions/Function0;)V", "messageSelectList", "getMessageSelectList", "()Ljava/util/List;", "getUpdateCheck", "()Lkotlin/jvm/functions/Function0;", "getItemCount", "", "onBindViewHolder", "holder", "position", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "setSelectedAll", "isAll", "", "checkBoxIv", "Landroid/widget/ImageView;", "alarmMassge", "MessageHolder", "MessageModule_debug"})
public final class MessageEditAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<com.lc.message.adapter.MessageEditAdapter.MessageHolder> {
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.lc.message.entity.AlarmMassge> messageSelectList = null;
    private final android.content.Context mContext = null;
    private final java.util.List<com.lc.message.entity.AlarmMassge> messageList = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.jvm.functions.Function0<kotlin.Unit> updateCheck = null;
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.lc.message.entity.AlarmMassge> getMessageSelectList() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public com.lc.message.adapter.MessageEditAdapter.MessageHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull()
    android.view.ViewGroup parent, int viewType) {
        return null;
    }
    
    @java.lang.Override()
    public void onBindViewHolder(@org.jetbrains.annotations.NotNull()
    com.lc.message.adapter.MessageEditAdapter.MessageHolder holder, int position) {
    }
    
    private final void updateCheck(android.widget.ImageView checkBoxIv, com.lc.message.entity.AlarmMassge alarmMassge) {
    }
    
    @java.lang.Override()
    public int getItemCount() {
        return 0;
    }
    
    public final void setSelectedAll(boolean isAll) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlin.jvm.functions.Function0<kotlin.Unit> getUpdateCheck() {
        return null;
    }
    
    public MessageEditAdapter(@org.jetbrains.annotations.NotNull()
    android.content.Context mContext, @org.jetbrains.annotations.NotNull()
    java.util.List<com.lc.message.entity.AlarmMassge> messageList, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> updateCheck) {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 4, 0}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\r\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0011\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\f\u00a8\u0006\u0013"}, d2 = {"Lcom/lc/message/adapter/MessageEditAdapter$MessageHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "view", "Landroid/view/View;", "(Landroid/view/View;)V", "msgCheckboxIv", "Landroid/widget/ImageView;", "getMsgCheckboxIv", "()Landroid/widget/ImageView;", "msgContentTv", "Landroid/widget/TextView;", "getMsgContentTv", "()Landroid/widget/TextView;", "msgCoverIv", "Lpl/droidsonroids/gif/GifImageView;", "getMsgCoverIv", "()Lpl/droidsonroids/gif/GifImageView;", "msgTimeTv", "getMsgTimeTv", "MessageModule_debug"})
    public static final class MessageHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        @org.jetbrains.annotations.NotNull()
        private final android.widget.TextView msgContentTv = null;
        @org.jetbrains.annotations.NotNull()
        private final android.widget.TextView msgTimeTv = null;
        @org.jetbrains.annotations.NotNull()
        private final pl.droidsonroids.gif.GifImageView msgCoverIv = null;
        @org.jetbrains.annotations.NotNull()
        private final android.widget.ImageView msgCheckboxIv = null;
        
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
        public final android.widget.ImageView getMsgCheckboxIv() {
            return null;
        }
        
        public MessageHolder(@org.jetbrains.annotations.NotNull()
        android.view.View view) {
            super(null);
        }
    }
}