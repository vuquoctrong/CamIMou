package com.mm.android.mobilecommon.widget.sticky.stickylistheaders;

import android.content.Context;
import android.widget.Checkable;

class CheckableWrapperView extends WrapperView implements Checkable {

	public CheckableWrapperView(final Context context) {
		super(context);
	}

	@Override
	public boolean isChecked() {
		return ((Checkable) item).isChecked();
	}

	@Override
	public void setChecked(final boolean checked) {
		((Checkable) item).setChecked(checked);
	}

	@Override
	public void toggle() {
		setChecked(!isChecked());
	}
}
