package com.mm.android.mobilecommon.widget.linechart;

public class LineEntity {
    private long[] data;
    private int color;
    private boolean visible;

    public LineEntity(long[] data, int color, boolean visible) {
        this.data = data;
        this.color = color;
        this.visible = visible;
    }

    public long[] getData() {
        return data;
    }

    public void setData(long[] data) {
        this.data = data;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
