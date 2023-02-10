package com.mm.android.mobilecommon.widget;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * The BottomMenuDialog button resource (Note: The property in TextAppearance will be overwritten by the TextColor and TextSize properties)
 *
 * BottomMenuDialog按钮资源（注：TextAppearance中属性会被TextColor和TextSize属性覆盖）
 */
public class CommonMenu4Lc implements Parcelable {
    private int     mTextResId;                                 //按钮文字字符串资源
	private int     mTextColorResId;                            //按钮文字颜色资源
	private float   mTextSize              = -1;                //按钮文字大小
	private int     mBackgroundResId;                           //按钮背景资源
	private int     mTextAppearanceResId;                       //文字外观Style
	private int[]   mMargins               = new int[4];        //按钮Margins
	
    public CommonMenu4Lc() {

    }
    
    public CommonMenu4Lc(int strRes) {
        mTextResId = strRes;
    }

	protected CommonMenu4Lc(Parcel in) {
		mTextResId = in.readInt();
		mTextColorResId = in.readInt();
		mTextSize = in.readFloat();
		mBackgroundResId = in.readInt();
		mTextAppearanceResId = in.readInt();
		mMargins = in.createIntArray();
	}

	public static final Creator<CommonMenu4Lc> CREATOR = new Creator<CommonMenu4Lc>() {
		@Override
		public CommonMenu4Lc createFromParcel(Parcel in) {
			return new CommonMenu4Lc(in);
		}

		@Override
		public CommonMenu4Lc[] newArray(int size) {
			return new CommonMenu4Lc[size];
		}
	};

	public int getTextAppearance() {
		return mTextAppearanceResId;
	}

	public void setTextAppearance(int textAppearance) {
		mTextAppearanceResId = textAppearance;
	}

	public int getStringId() {
        return mTextResId;
	}

	public void setStringId(int stringId) {
        mTextResId = stringId;
	}

	public int getColorId() {
		return mTextColorResId;
	}

	public void setColorId(int colorId) {
		mTextColorResId = colorId;
	}

	public int getDrawId() {
		return mBackgroundResId;
	}

	public void setDrawId(int drawId) {
		mBackgroundResId = drawId;
	}

	public int[] getMargins() {
        return mMargins;
    }
	
	public void setMargins(int left, int top, int right, int bottom) {
	    mMargins[0] = left;
	    mMargins[1] = top;
	    mMargins[2] = right;
	    mMargins[3] = bottom;
    }
	
	public float getTextSize() {
        return mTextSize;
    }

    public void setTextSize(int textSize) {
        this.mTextSize = textSize;
    }

    @Override
	public int describeContents() {

		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		arg0.writeInt(mTextResId);
		arg0.writeInt(mTextColorResId);
		arg0.writeFloat(mTextSize);
		arg0.writeInt(mBackgroundResId);
		arg0.writeInt(mTextAppearanceResId);
		arg0.writeIntArray(mMargins);
	}
}
