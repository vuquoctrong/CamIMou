/**
 * All rights Reserved.
 * 16:9宽高比的ImageView
 */
package com.mm.android.mobilecommon.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import com.mm.android.mobilecommon.R;

/**
 * Custom aspect ratio ImageView, default 1:1

 * user-defined attribute：
 *  <declare-styleable name="CommonRatioImageView">
    <attr name="width_relative" format="dimension" /> 相对宽度
    <attr name="height_relative" format="dimension" />相对高度
    </declare-styleable>
 *
 * use：
 * <CommonRatioImageView
     xmlns:app="http://schemas.android.com/apk/res-auto"
     android:layout_width="match_parent"
     android:layout_height="match_parent"
     app:width_relative="16dp"
     app:height_relative="9dp"
    />
 *
 *
 * 自定义宽高比的ImageView，默认1:1

 * 自定义属性：
 *  <declare-styleable name="CommonRatioImageView">
    <attr name="width_relative" format="dimension" /> 相对宽度
    <attr name="height_relative" format="dimension" />相对高度
    </declare-styleable>
 *
 * 应用：
 * <CommonRatioImageView
     xmlns:app="http://schemas.android.com/apk/res-auto"
     android:layout_width="match_parent"
     android:layout_height="match_parent"
     app:width_relative="16dp"
     app:height_relative="9dp"
    />
 *
 */
public class CommonRatioImageView extends AppCompatImageView {

	private float width_relative = 1.0f;
    private float height_relative = 1.0f;

    public CommonRatioImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CommonRatioImageView);
        if(array == null)return;
        width_relative = array.getDimension(R.styleable.CommonRatioImageView_width_relative,1.0f);
        height_relative = array.getDimension(R.styleable.CommonRatioImageView_height_relative,1.0f);
		array.recycle();
	}

	public CommonRatioImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CommonRatioImageView);
        if(array == null)return;
        width_relative = array.getDimension(R.styleable.CommonRatioImageView_width_relative,1.0f);
        height_relative = array.getDimension(R.styleable.CommonRatioImageView_height_relative,1.0f);
        array.recycle();
	}

	public CommonRatioImageView(Context context) {
		super(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
		int heightSize = Math.round(widthSize * height_relative/width_relative);
		heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}