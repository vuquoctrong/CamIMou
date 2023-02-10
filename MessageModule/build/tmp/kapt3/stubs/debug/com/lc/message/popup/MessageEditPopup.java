package com.lc.message.popup;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 0}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000R\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u00002\u00020\u0001B>\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\'\u0010\u0006\u001a#\u0012\u0019\u0012\u0017\u0012\u0004\u0012\u00020\t0\b\u00a2\u0006\f\b\n\u0012\b\b\u000b\u0012\u0004\b\b(\f\u0012\u0004\u0012\u00020\r0\u0007\u00a2\u0006\u0002\u0010\u000eJ\b\u0010\u001a\u001a\u00020\rH\u0002J\u001c\u0010\u001b\u001a\u00020\r2\u0006\u0010\u001c\u001a\u00020\u00162\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\t0\bR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R/\u0010\u0006\u001a#\u0012\u0019\u0012\u0017\u0012\u0004\u0012\u00020\t0\b\u00a2\u0006\f\b\n\u0012\b\b\u000b\u0012\u0004\b\b(\f\u0012\u0004\u0012\u00020\r0\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0013\u001a\u0004\u0018\u00010\u0014X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0016X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u0018X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\u0018X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001d"}, d2 = {"Lcom/lc/message/popup/MessageEditPopup;", "Landroid/widget/PopupWindow;", "context", "Landroid/content/Context;", "titleName", "", "del", "Lkotlin/Function1;", "", "Lcom/lc/message/entity/AlarmMassge;", "Lkotlin/ParameterName;", "name", "messageList", "", "(Landroid/content/Context;Ljava/lang/String;Lkotlin/jvm/functions/Function1;)V", "deleteBtn", "Landroid/widget/Button;", "editRecyclerView", "Landroidx/recyclerview/widget/RecyclerView;", "messageEditAdapter", "Lcom/lc/message/adapter/MessageEditAdapter;", "picView", "Landroid/view/View;", "selectedTv", "Landroid/widget/TextView;", "titleTv", "deleteDialog", "show", "view", "MessageModule_debug"})
public final class MessageEditPopup extends android.widget.PopupWindow {
    private final android.view.View picView = null;
    private final android.widget.TextView selectedTv = null;
    private final android.widget.TextView titleTv = null;
    private final android.widget.Button deleteBtn = null;
    private final androidx.recyclerview.widget.RecyclerView editRecyclerView = null;
    private com.lc.message.adapter.MessageEditAdapter messageEditAdapter;
    private final android.content.Context context = null;
    private final kotlin.jvm.functions.Function1<java.util.List<com.lc.message.entity.AlarmMassge>, kotlin.Unit> del = null;
    
    private final void deleteDialog() {
    }
    
    public final void show(@org.jetbrains.annotations.NotNull()
    android.view.View view, @org.jetbrains.annotations.NotNull()
    java.util.List<com.lc.message.entity.AlarmMassge> messageList) {
    }
    
    public MessageEditPopup(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String titleName, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.util.List<com.lc.message.entity.AlarmMassge>, kotlin.Unit> del) {
        super(null);
    }
}