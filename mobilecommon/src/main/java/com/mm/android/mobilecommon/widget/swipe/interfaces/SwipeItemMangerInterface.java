package com.mm.android.mobilecommon.widget.swipe.interfaces;

import com.mm.android.mobilecommon.widget.swipe.SwipeLayout;
import com.mm.android.mobilecommon.widget.swipe.implments.SwipeItemMangerImpl;

import java.util.List;

public interface SwipeItemMangerInterface {

    public void openItem(int position);

    public void closeItem(int position);
    
    public void colseItemNoDataChange(int position);

    public void closeAllExcept(SwipeLayout layout);

    public List<Integer> getOpenItems();

    public List<SwipeLayout> getOpenLayouts();

    public void removeShownLayouts(SwipeLayout layout);

    public boolean isOpen(int position);

    public SwipeItemMangerImpl.Mode getMode();

    public void setMode(SwipeItemMangerImpl.Mode mode);
}
