package com.lechange.demo.tools;


import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author 323087
 */
public class GridItemDecoration extends RecyclerView.ItemDecoration {
    protected int mSpace = 10;

    private boolean mIncludeEdge = true;

    public GridItemDecoration(int space) {
        this.mSpace = space;
    }

    public GridItemDecoration(int space, boolean includeEdge) {
        this.mSpace = space;
        this.mIncludeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.top = mSpace;
        outRect.bottom = mSpace;
        outRect.right = mSpace;
        outRect.left = mSpace;
    }
}
