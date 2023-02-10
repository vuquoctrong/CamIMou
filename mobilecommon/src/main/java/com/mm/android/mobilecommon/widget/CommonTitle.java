package com.mm.android.mobilecommon.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mm.android.mobilecommon.R;


/**
 * <p>
 * Tool class instructions: left button, left inside button, middle button, right inside button, right button,
 * all support picture and text background two choice, left and right button default support picture, middle button default support text
 * </p>
 * <p>
 * xNote： Declaration in XML：style="@style/common_switch_title"
 * </p>
 * <p>
 * Introduction to function usage:
 * </p>
 * <p>
 * initView(int tvLeftResId, int tvRightResId, int tvCenterResId) Initialization control: left button, middle button, right button, belong to the common usage
 * </p>
 * <p>
 * getTextViewXXX() Obtain the xxx button for custom function expansion. For example, implement the listview function to expand down
 * </p>
 * <p>
 * setTitleXXXView(int resId, int colorId, int textSizeDimenId) Set the xxx button text, text color, text size
 * </p>
 * <p>
 * setTitleXXX(int resId) Set the xxx button picture or text
 * </p>
 * <p>
 * setTextColorXXX(int colorId) Set the color resource for the xxx button text
 * </p>
 * <p>
 * setTextSizeXXX(int textSizeDimenId) Set the size of the xxx button text
 * </p>
 * <p>
 * setTitleTextXXX(int resId) The text of the XXX button is set by resid
 * </p>
 * <p>
 * setTitleTextXXX(String titleTextXXX) The text of the XXX button is set by string
 * </p>
 * <p>
 * setVisibleXXX(int flag)  Setting XXX Whether to hide by flag，flag:View.GONE,View.INVISIBLE,View.VISIBLE;
 * </p>
 *
 *
 *
 * <p>
 * 工具类使用说明：左按钮，左内按钮，中间按钮，右内按钮，右按钮，均支持图片和文字背景二选一，左右按钮默认支持图片，中间按钮默认支持文字
 * </p>
 * <p>
 * xml使用注意：需要在xml文件中使用style="@style/common_title"
 * </p>
 * <p>
 * 函数使用介绍：
 * </p>
 * <p>
 * initView(int tvLeftResId, int tvRightResId, int tvCenterResId) 初始化控件：左按钮，中间按钮，右按钮，属于普通用法
 * </p>
 * <p>
 * getTextViewXXX() 获取xxx按钮，进行自定义功能扩展操作。例如，实现展开下来listview的功能
 * </p>
 * <p>
 * setTitleXXXView(int resId, int colorId, int textSizeDimenId) 设置xxx按钮的文字，文字颜色，文字大小
 * </p>
 * <p>
 * setTitleXXX(int resId) 设置xxx按钮图片或文字
 * </p>
 * <p>
 * setTextColorXXX(int colorId) 设置xxx按钮文字的颜色资源
 * </p>
 * <p>
 * setTextSizeXXX(int textSizeDimenId) 设置xxx按钮文字的大小
 * </p>
 * <p>
 * setTitleTextXXX(int resId) 通过resId设置xxx按钮的文字
 * </p>
 * <p>
 * setTitleTextXXX(String titleTextXXX) 通过String设置xxx按钮的文字
 * </p>
 * <p>
 * setVisibleXXX(int flag) 通过flag设置xxx是否隐藏，flag:View.GONE,View.INVISIBLE,View.VISIBLE;
 * </p>
 */
public class CommonTitle extends RelativeLayout
// implements OnClickListener
{
    /**
     * Left button ID
     *
     * 左侧按钮ID
     */
    public static final int ID_LEFT = 0;

    /**
     * Left inner button ID
     *
     * 左侧内按钮ID
     */
    public static final int ID_LEFT_2 = 1;

    /**
     * Right button ID
     *
     * 右侧按钮ID
     */
    public static final int ID_RIGHT = 2;

    /**
     * ID of the right inner button
     *
     * 右侧内按钮ID
     */
    public static final int ID_RIGHT_2 = 3;

    /**
     * The middle button idid, temporarily without the monitor
     *
     * 中间按钮ID, 暂时不加监听器
     */
    public static final int ID_CENTER = 4;

    /**
     * Left button/Left button LinearLayout
     *
     * 左侧按钮/ 左侧按钮LinearLayout
     */
    private TextView mTitleLeftTv;

    private LinearLayout mTitleLeftLl;

    /**
     * Left button (inside)/Left button (inside)LinearLayout
     *
     * 左侧按钮(靠内) /左侧按钮(靠内)LinearLayout
     */
    private TextView mTitleLeft2Tv;

    private LinearLayout mTitleLeft2Ll;

    /**
     * Right button/Right button LinearLayout
     *
     * 右侧按钮 /右侧按钮LinearLayout
     */
    private TextView mTitleRightTv;

    private LinearLayout mTitleRightLl;

    /**
     * Right button (inside)/Right button (inside)LinearLayout
     *
     * 右侧按钮(靠内) /右侧按钮(靠内)LinearLayout
     */
    private TextView mTitleRight2Tv;

    private LinearLayout mTitleRight2Ll;

    /**
     * The text title/text title linearlayout
     *
     * 文字标题/文字标题LinearLayout
     */
    private TextView mTitleCenterTv;

    private LinearLayout mTitleCenterLl;

    /**
     * Click on the monitor
     *
     * 点击监听
     */
    private OnTitleClickListener mListener;

    private View mBottomV;
    private boolean mIsSupportChangeCenterWidth =false;
    /**
     * By default, hide the left 2 and right 2 buttons and create a new instance CommonTitle.
     * @param context
     * @param attrs
     *
     * 默认隐藏左2和右2的按钮 ，创建一个新的实例CommonTitle.
     * @param context
     * @param attrs
     */
    public CommonTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.mobile_common_widget_common_title, this);
        initView();
        setListeners();
        setVisibleLeft2(View.GONE);
        setVisibleRight2(View.GONE);
    }

    private void initView() {
        mBottomV = findViewById(R.id.v_bottom_line);
        mTitleLeftLl = findViewById(R.id.ll_title_left);
        mTitleLeft2Ll = findViewById(R.id.ll_title_left2);
        mTitleRightLl = findViewById(R.id.ll_title_right);
        mTitleRight2Ll = findViewById(R.id.ll_title_right2);
        mTitleCenterLl = findViewById(R.id.ll_title_center);

        mTitleLeftTv = findViewById(R.id.tv_title_left);
        mTitleLeft2Tv = findViewById(R.id.tv_title_left2);
        mTitleRightTv = findViewById(R.id.tv_title_right);
        mTitleRight2Tv = findViewById(R.id.tv_title_right2);
        mTitleCenterTv = findViewById(R.id.tv_title_center);

        mTitleLeftTv.setTextColor(getResources().getColor(R.color.c43));
        mTitleLeft2Tv.setTextColor(getResources().getColor(R.color.c43));
        mTitleRightTv.setTextColor(getResources().getColor(R.color.c43));
        mTitleRight2Tv.setTextColor(getResources().getColor(R.color.c43));
        mTitleCenterTv.setTextColor(getResources().getColor(R.color.c2));

        mTitleLeftTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources()
                .getDimensionPixelSize(R.dimen.mobile_common_common_title_text_size_mid));
        mTitleLeft2Tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimensionPixelSize(R.dimen.mobile_common_common_title_text_size_mid));
        mTitleRightTv.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimensionPixelSize(R.dimen.mobile_common_common_title_text_size_mid));
        mTitleRight2Tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimensionPixelSize(R.dimen.mobile_common_common_title_text_size_mid));
        mTitleCenterTv.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimensionPixelSize(R.dimen.mobile_common_common_title_text_size_large));
       
       //横屏时不显示，只支持竖屏下的宽度设置。 //初始化时，写死中间标题的宽度
        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        int commonTitleWidth = dm.widthPixels;
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
			commonTitleWidth = dm.widthPixels;
        }else  if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
        	commonTitleWidth = dm.heightPixels;
        }
        
        int centerTitleWidth = commonTitleWidth - 4 * dp2px(dm, 48);// 减去4个按钮的占用空间

		mTitleCenterTv.setWidth(centerTitleWidth);
    }
    
    private  int dp2px(DisplayMetrics dm , float dp){
    	final float scale = dm.density;
    	return (int) (dp * scale + 0.5f);
    }
    
    private void setListeners() {
        mTitleLeftLl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onCommonTitleClick(ID_LEFT);
                }

            }
        });

        mTitleLeft2Ll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onCommonTitleClick(ID_LEFT_2);
                }

            }
        });

        mTitleRightLl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onCommonTitleClick(ID_RIGHT);
                }

            }
        });

        mTitleRight2Ll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.onCommonTitleClick(ID_RIGHT_2);
            }
        });

        mTitleCenterLl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.onCommonTitleClick(ID_CENTER);
            }
        });
    }

    /**
     * Initialization parameters, buttons can support picture or text background
     * @param tvLeftResId The left button
     * @param tvRightResId The right button
     * @param tvCenterResId The middle button
     *
     * 初始化参数，按钮均可支持图片或者文字背景
     * @param tvLeftResId 左按钮
     * @param tvRightResId 右按钮
     * @param tvCenterResId 中间按钮
     */

    public void initView(int tvLeftResId, int tvRightResId, int tvCenterResId) {
        setTitleLeftView(tvLeftResId, 0, 0);
        setTitleRightView(tvRightResId, 0, 0);
        setTitleCenterView(tvCenterResId, 0, 0);
    }

    public TextView getTextViewRight() {
     return mTitleRightTv;
     }

    //
     public TextView getTextViewRight2() {
     return mTitleRight2Tv;
     }
    //
    public TextView getTextViewCenter() {
        return mTitleCenterTv;
    }

    /**
     * Sets whether to select the button
     * @param selected {true,false}
     * @param id {ID_LEFT ,ID_LEFT_2,ID_RIGHT,ID_RIGHT_2,ID_CENTER}
     *
     * 设置是否选中按钮
     * @param selected {true,false}
     * @param id {ID_LEFT ,ID_LEFT_2,ID_RIGHT,ID_RIGHT_2,ID_CENTER}
     */
    public void setTitleSelected(boolean selected, int id) {
        View v = findViewByID(id);
        if (v != null) {
            v.setSelected(selected);
        }
    }

    /**
     * Set whether the button is available
     *
     * 设置按钮是否可用
     */
  

	public void setEnabled(boolean enabled, int id) {
		View parent = findParentViewById(id);
		if (parent != null) {

			View v = findViewByID(id);
			if (v != null) {

				parent.setEnabled(enabled);
				v.setEnabled(enabled);
			}
		}

	}

	public boolean isEnable(int id){
        View parent = findParentViewById(id);
        if (parent != null) {

            View v = findViewByID(id);
            if (v != null) {
               return v.isEnabled();
            }
        }

        return false;
    }

    private View findParentViewById(int id) {
        switch (id) {
            case ID_LEFT:
                return mTitleLeftLl;
            case ID_LEFT_2:
                return mTitleLeft2Ll;
            case ID_RIGHT:
                return mTitleRightLl;
            case ID_RIGHT_2:
                return mTitleRight2Ll;
            case ID_CENTER:
                return mTitleCenterLl;
            default:
                return null;
        }
    }

    private View findViewByID(int id) {
        switch (id) {
            case ID_LEFT:
                return mTitleLeftTv;
            case ID_LEFT_2:
                return mTitleLeft2Tv;
            case ID_RIGHT:
                return mTitleRightTv;
            case ID_RIGHT_2:
                return mTitleRight2Tv;
            case ID_CENTER:
                return mTitleCenterTv;
            default:
                return null;
        }
    }

    /*------------------------------------------getTextView END------------------------------*/

    /*-----------------------------------setTitleView START---------------------------------*/
    public void setTitleLeftView(int resId, int colorId, int textSizeDimenId) {
        setTitleLeft(resId);
        setTextColorLeft(colorId);
        setTextSizeLeft(textSizeDimenId);
    }

    public void setTitleRightView(int resId, int colorId, int textSizeDimenId) {
        setTitleRight(resId);
        setTextColorRight(colorId);
        setTextSizeRight(textSizeDimenId);
    }

    public void setTitleCenterView(int resId, int colorId, int textSizeDimenId) {
        setTitleCenter(resId);
        setTextColorCenter(colorId);
        setTextSizeCenter(textSizeDimenId);
    }

    /*-----------------------------------setTitleView END---------------------------------*/
    /*------------------------------------------setTitle START------------------------------*/
    /**
     * Set the left button image or text
     * @param leftResId
     *
     * 设置左边按钮图片或文字
     * @param leftResId
     */
    public void setTitleLeft(int leftResId) {
        if (mTitleLeftTv != null) {
            if (leftResId != 0) {
                if (mTitleLeftLl != null && mTitleLeftLl.getVisibility() != View.VISIBLE)
                    mTitleLeftLl.setVisibility(VISIBLE);
                Drawable drawable = null;
                try {
                    drawable = getResources().getDrawable(leftResId);
                } catch (Exception e) {
                    // e.printStackTrace();
                } finally {
                    if (drawable != null) {
                        mTitleLeftTv.setBackgroundResource(leftResId);
                        mTitleLeftTv.setText(null);
                    } else {
                        mTitleLeftTv.setText(leftResId);
                        mTitleLeftTv.setBackgroundResource(0);
                    }
                }
            } else {
                if (mTitleLeftLl != null)
                    mTitleLeftLl.setVisibility(GONE);
            }
        }
    }

    /**
     * Set the left button image or text
     *
     * 设置左边按钮图片或文字
     */
    public void setTitleLeft2(int left2ResId) {
        if (mTitleLeft2Tv != null) {
            if (left2ResId != 0) {
                if (mTitleLeft2Ll != null && mTitleLeft2Ll.getVisibility() != View.VISIBLE)
                    mTitleLeft2Ll.setVisibility(VISIBLE);
                Drawable drawable = null;
                try {
                    drawable = getResources().getDrawable(left2ResId);
                } catch (Exception e) {
                    // e.printStackTrace();
                } finally {
                    if (drawable != null) {
                        mTitleLeft2Tv.setBackgroundResource(left2ResId);
                        mTitleLeft2Tv.setText(null);
                    } else {
                        mTitleLeft2Tv.setText(left2ResId);
                        mTitleLeft2Tv.setBackgroundResource(0);
                    }
                }
            } else {
                if (mTitleLeft2Ll != null)
                    mTitleLeft2Ll.setVisibility(INVISIBLE);
            }
        }
    }

    /**
     * Set the image or text of the right button
     * @param rightResId
     *
     * 设置右边按钮图片或文字
     * @param rightResId
     */
    public void setTitleRight(int rightResId) {
        if (mTitleRightTv != null) {
            if (rightResId != 0) {
                if (mTitleRightLl != null && mTitleRightLl.getVisibility() != View.VISIBLE)
                    mTitleRightLl.setVisibility(VISIBLE);
                Drawable drawable = null;
                try {
                    drawable = getResources().getDrawable(rightResId);
                } catch (Exception e) {
                    // e.printStackTrace();
                } finally {
                    if (drawable != null) {
                        mTitleRightTv.setBackgroundResource(rightResId);
                        mTitleRightTv.setText(null);
                    } else {
                        mTitleRightTv.setText(rightResId);
                        mTitleRightTv.setBackgroundResource(0);
                    }

                }
            } else {
                if (mTitleRightLl != null)
                    mTitleRightLl.setVisibility(INVISIBLE);
            }
        }
    }

    public void setTitleRight2(int right2ResId) {
        if (mTitleRight2Tv != null) {
            if (right2ResId != 0) {
                if (mTitleRight2Ll != null && mTitleRight2Ll.getVisibility() != View.VISIBLE)
                    mTitleRight2Ll.setVisibility(VISIBLE);
                Drawable drawable = null;
                try {
                    drawable = getResources().getDrawable(right2ResId);
                } catch (Exception e) {
                    // e.printStackTrace();
                } finally {
                    if (drawable != null) {
                        mTitleRight2Tv.setBackgroundResource(right2ResId);
                        mTitleRight2Tv.setText(null);
                    } else {
                        mTitleRight2Tv.setText(right2ResId);
                        mTitleRight2Tv.setBackgroundResource(0);
                    }
                }
            } else {
                if (mTitleRight2Ll != null)
                    mTitleRight2Ll.setVisibility(INVISIBLE);
            }
        }
    }

    /**
     * Set the middle button image or text
     * @param centerResId
     *
     * 设置中间按钮图片或文字
     * @param centerResId
     */
    public void setTitleCenter(int centerResId) {
        if (mTitleCenterTv != null) {
            if (centerResId != 0) {
                if (mTitleCenterLl != null && mTitleCenterLl.getVisibility() != View.VISIBLE)
                    mTitleCenterLl.setVisibility(VISIBLE);
                Drawable drawable = null;
                try {
                    drawable = getResources().getDrawable(centerResId);
                } catch (Exception e) {
                    // e.printStackTrace();
                } finally {
                    if (drawable != null) {
                        mTitleCenterTv.setBackgroundResource(centerResId);
                        mTitleCenterTv.setText(null);
                    } else {
                        mTitleCenterTv.setText(centerResId);
                        mTitleCenterTv.setBackgroundResource(0);
                        setTitleCenterWidth();
                    }
                }
            } else {
                if (mTitleCenterLl != null)
                    mTitleCenterLl.setVisibility(INVISIBLE);
            }
        }
    }

    /*------------------------------------------setTitle END------------------------------*/
    /*-----------------------------------------setTextColor START--------------------------------------*/
    public void setTextColorLeft(int colorId) {
        if (mTitleLeftTv != null) {
            mTitleLeftTv.setTextColor(colorId != 0 ? getResources().getColor(colorId) : getResources().getColor(
                    R.color.c50));
        }
    }

    public void setTextColorLeft2(int colorId) {
        if (mTitleLeft2Tv != null) {
            mTitleLeft2Tv.setTextColor(colorId != 0 ? getResources().getColor(colorId) : getResources().getColor(
                    R.color.c50));
        }
    }

    public void setTextColorRight(int colorId) {
        if (mTitleRightTv != null) {
            mTitleRightTv.setTextColor(colorId != 0 ? getResources().getColorStateList(colorId) : getResources()
                    .getColorStateList(R.color.mobile_common_title_text_color_selector));
        }
    }

    public void setTextColorRight2(int colorId) {
        if (mTitleRight2Tv != null) {
            mTitleRight2Tv.setTextColor(colorId != 0 ? getResources().getColor(colorId) : getResources().getColor(
                    R.color.c43));
        }
    }

    public void setTextColorCenter(int colorId) {
        if (mTitleCenterTv != null) {
            mTitleCenterTv.setTextColor(colorId != 0 ? getResources().getColor(colorId) : getResources().getColor(
                    R.color.c2));
        }
    }

    /*-----------------------------------------setTextColor END--------------------------------------*/
    /*-----------------------------------------setTextSize START--------------------------------------*/
    public void setTextSizeLeft(int textSizeDimenId) {
        if (mTitleLeftTv != null) {
            mTitleLeftTv.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    textSizeDimenId != 0 ? getResources().getDimensionPixelSize(textSizeDimenId) : getResources()
                            .getDimensionPixelSize(R.dimen.mobile_common_common_title_text_size_mid));
        }
    }

    public void setTextSizeLeft2(int textSizeDimenId) {
        if (mTitleLeft2Tv != null) {
            mTitleLeft2Tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    textSizeDimenId != 0 ? getResources().getDimensionPixelSize(textSizeDimenId) : getResources()
                            .getDimensionPixelSize(R.dimen.mobile_common_common_title_text_size_mid));
        }
    }

    public void setTextSizeRight(int textSizeDimenId) {
        if (mTitleRightTv != null) {
            mTitleRightTv.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    textSizeDimenId != 0 ? getResources().getDimensionPixelSize(textSizeDimenId) : getResources()
                            .getDimensionPixelSize(R.dimen.mobile_common_common_title_text_size_mid));
        }
    }

    public void setTextSizeRight2(int textSizeDimenId) {
        if (mTitleRight2Tv != null) {
            mTitleRight2Tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    textSizeDimenId != 0 ? getResources().getDimensionPixelSize(textSizeDimenId) : getResources()
                            .getDimensionPixelSize(R.dimen.mobile_common_common_title_text_size_mid));
        }
    }

    public void setTextSizeCenter(int textSizeDimenId) {
        if (mTitleCenterTv != null) {
            mTitleCenterTv.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    textSizeDimenId != 0 ? getResources().getDimensionPixelSize(textSizeDimenId) : getResources()
                            .getDimensionPixelSize(R.dimen.mobile_common_common_title_text_size_large));
        }
    }

    /*-----------------------------------------setTextSize END--------------------------------------*/

    /*-----------------------------------------setIcon START--------------------------------------*/

    public void setIconLeft(int resId) {
        if (mTitleLeftLl != null) {
            if (mTitleLeftLl.getVisibility() != View.VISIBLE) {
                mTitleLeftLl.setVisibility(View.VISIBLE);
            }
            setTitleLeft(resId);
        }
    }

    public void setIconLeft2(int resId) {
        if (mTitleLeft2Ll != null) {
            if (mTitleLeft2Ll.getVisibility() != View.VISIBLE) {
                mTitleLeft2Ll.setVisibility(View.VISIBLE);
            }
            setTitleLeft2(resId);
        }
    }

    public void setIconRight(int resId) {
        if (mTitleRightLl != null) {
            if (mTitleRightLl.getVisibility() != View.VISIBLE) {
                mTitleRightLl.setVisibility(View.VISIBLE);
            }
            setTitleRight(resId);
        }
    }

    public void setIconRight2(int resId) {
        if (mTitleRight2Ll != null) {
            if (mTitleRight2Ll.getVisibility() != View.VISIBLE) {
                mTitleRight2Ll.setVisibility(View.VISIBLE);
            }
            setTitleRight2(resId);
        }
    }

    public void setIconCenter(int resId) {
        if (mTitleCenterLl != null) {
            if (mTitleCenterLl.getVisibility() != View.VISIBLE) {
                mTitleCenterLl.setVisibility(View.VISIBLE);
            }
            setTitleCenter(resId);
        }
    }

    /*-----------------------------------------setIcon END--------------------------------------*/
    /*-----------------------------------------setTextById START--------------------------------------*/

    public void setTitleTextLeft(int resId) {
        if (mTitleLeftLl != null) {
            if (mTitleLeftLl.getVisibility() != View.VISIBLE) {
                mTitleLeftLl.setVisibility(View.VISIBLE);
            }
            setTitleLeft(resId);
        }
    }

    public void setTitleTextLeft2(int resId) {
        if (mTitleLeft2Ll != null) {
            if (mTitleLeft2Ll.getVisibility() != View.VISIBLE) {
                mTitleLeft2Ll.setVisibility(View.VISIBLE);
            }
            setTitleLeft(resId);
        }
    }

    public void setTitleTextRight(int resId) {
        if (mTitleRightLl != null) {
            if (mTitleRightLl.getVisibility() != View.VISIBLE) {
                mTitleRightLl.setVisibility(View.VISIBLE);
            }
            setTitleRight(resId);
        }
    }

    public void setTitleTextRight2(int resId) {
        if (mTitleRight2Ll != null) {
            if (mTitleRight2Ll.getVisibility() != View.VISIBLE) {
                mTitleRight2Ll.setVisibility(View.VISIBLE);
            }
            setTitleRight2(resId);
        }
    }

    public void setTitleTextCenter(int resId) {
        if (mTitleCenterLl != null) {
            if (mTitleCenterLl.getVisibility() != View.VISIBLE) {
                mTitleCenterLl.setVisibility(View.VISIBLE);
            }
            setTitleCenter(resId);
        }
    }

    /*-----------------------------------------setTextById END--------------------------------------*/
    /*-----------------------------------------setTextByString START--------------------------------------*/
    public void setTitleTextLeft(String titleTextLeft) {
        if (mTitleLeftTv != null) {
            if (mTitleLeftTv.getVisibility() != View.VISIBLE) {
                mTitleLeftTv.setVisibility(View.VISIBLE);
            }
            mTitleLeftTv.setText(titleTextLeft);
            mTitleLeftTv.setBackgroundResource(0);
        }
    }

    public void setTitleTextLeft2(String titleTextLeft2) {
        if (mTitleLeft2Tv != null) {
            if (mTitleLeft2Tv.getVisibility() != View.VISIBLE) {
                mTitleLeft2Tv.setVisibility(View.VISIBLE);
            }
            mTitleLeft2Tv.setText(titleTextLeft2);
            mTitleLeft2Tv.setBackgroundResource(0);
        }
    }

    public void setTitleTextRight(String titleTextRight) {
        if (mTitleRightTv != null) {
            if (mTitleRightTv.getVisibility() != View.VISIBLE) {
                mTitleRightTv.setVisibility(View.VISIBLE);
            }
            mTitleRightTv.setText(titleTextRight);
            mTitleRightTv.setBackgroundResource(0);
        }
    }

    public void setTitleTextRight2(String titleTextRight2) {
        if (mTitleRight2Tv != null) {
            if (mTitleRight2Tv.getVisibility() != View.VISIBLE) {
                mTitleRight2Tv.setVisibility(View.VISIBLE);
            }
            mTitleRight2Tv.setText(titleTextRight2);
            mTitleRight2Tv.setBackgroundResource(0);
        }
    }

    public void setTitleTextCenter(String titleTextCenter) {
        if (mTitleCenterLl != null) {
            if (mTitleCenterLl.getVisibility() != View.VISIBLE) {
                mTitleCenterLl.setVisibility(View.VISIBLE);
            }
            mTitleCenterTv.setText(titleTextCenter);
            mTitleCenterTv.setBackgroundResource(0);
            setTitleCenterWidth();
        }
    }
    /**
     * To support the problem of excessive number of words in the title of the display page callback, add the ability to dynamically change the width of the middle
     * Only the web page is supported to change the title width
     * @param isSupportChangeCenterWidth
     *
     * 为了支持显示网页回调中标题字数过多的问题，增加动态修改中间的宽度功能
     * 只支持web修改标题宽度
     * @param isSupportChangeCenterWidth
     */
    public void setChangeCenterWidthForWebView(boolean isSupportChangeCenterWidth){
    	mIsSupportChangeCenterWidth = isSupportChangeCenterWidth;
    }

    @Deprecated
    private void setTitleCenterWidth(){

    }
    /*-----------------------------------------setTextByString END--------------------------------------*/
    /*-----------------------------------------setVisible START--------------------------------------*/

    public void setVisibleLeft(int flag) {
        if (mTitleLeftLl != null) {
            mTitleLeftLl.setVisibility(flag);
        }
    }

    public void setVisibleLeft2(int flag) {
        if (mTitleLeft2Ll != null) {
            mTitleLeft2Ll.setVisibility(flag);
        }
    }

    public void setVisibleRight(int flag) {
        if (mTitleRightLl != null) {
            mTitleRightLl.setVisibility(flag);
        }
    }

    public void setEnableRight(boolean enable) {
        if (mTitleRightLl != null) {
            mTitleRightLl.setEnabled(enable);
        }
    }

    public boolean getEnableRight() {
        return mTitleRightLl.isEnabled();
    }

    public void setVisibleRight2(int flag) {
        if (mTitleRight2Ll != null) {
            mTitleRight2Ll.setVisibility(flag);
        }
    }

    public void setVisibleCenter(int flag) {
        if (mTitleCenterLl != null) {
            mTitleCenterLl.setVisibility(flag);
        }
    }

    public void setVisibleBottom(int flag) {
        if (mBottomV != null) {
            mBottomV.setVisibility(flag);
        }
    }

    public boolean isIconLeftVisible(){
        if (mTitleLeftLl != null) {
            return mTitleLeftLl.getVisibility() == VISIBLE;
        }
        return false;
    }

    /*-----------------------------------------setVisible END--------------------------------------*/
    public void setOnTitleClickListener(OnTitleClickListener listener) {
        mListener = listener;
    }

    public interface OnTitleClickListener {
        public void onCommonTitleClick(int id);
    }
}
