package com.mm.android.mobilecommon.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TabWidget;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * A simple text label view that can be applied as a "badge" to any given {@link View}.
 * This class is intended to be instantiated at runtime rather than included in XML layouts.
 *
 * 一个简单的文本标签视图，可以作为“徽章”应用到任何给定的{@link View}。
 * 这个类的目的是在运行时实例化,而不是包含在XML布局中。
 */
public class BadgeView extends AppCompatTextView {

    private final static int POSITION_TOP_LEFT = 1;
    private final  static int POSITION_TOP_RIGHT = 2;
    private final  static int POSITION_BOTTOM_LEFT = 3;
    private final  static int POSITION_BOTTOM_RIGHT = 4;
    private final  static int POSITION_CENTER = 5;

    private final  static int DEFAULT_MARGIN_DIP = 5;
    private final  static int DEFAULT_LR_PADDING_DIP = 5;
    private final  static int DEFAULT_CORNER_RADIUS_DIP = 9;
    private final  static int DEFAULT_POSITION = POSITION_TOP_RIGHT;
    private final  static int DEFAULT_BADGE_COLOR = Color.parseColor("#CCFF0000"); // Color.RED;
    private final static  int DEFAULT_TEXT_COLOR = Color.WHITE;

	private static Animation fadeIn;
	private static Animation fadeOut;

	private Context context;
	private View target;

	private int badgePosition;
	private int badgeMarginH;
	private int badgeMarginV;
	private int badgeColor;

	private boolean isShown;

	private ShapeDrawable badgeBg;

	private int targetTabIndex;

	public BadgeView(Context context) {
		this(context, (AttributeSet) null, android.R.attr.textViewStyle);
	}

	public BadgeView(Context context, AttributeSet attrs) {
		 this(context, attrs, android.R.attr.textViewStyle);
	}

	/**
     * Constructor
     * create a new BadgeView instance attached to a target {@link View}.
     * @param context context for this view.
     * @param target the View to attach the badge to.
	 *
	 * 构造器
	 * 创建一个新的BadgeView实例附加到目标{@link View}。
	 * @param context 此视图的上下文。
	 * @param target 要将徽章附加到的视图。
     */
	public BadgeView(Context context, View target) {
		 this(context, null, android.R.attr.textViewStyle, target, 0);
	}

	/**
     * Constructor
     * create a new BadgeView instance attached to a target {@link TabWidget}
     * tab at a given index.
     * @param context context for this view.
     * @param target the TabWidget to attach the badge to.
     * @param index the position of the tab within the target.
	 *
	 * 构造器
	 * 创建一个新的BadgeView实例附加到目标{@link TabWidget}
	 * 在给定的索引上。
	 * @param context 此视图的上下文。
	 * @param target 要将徽章附加到的TabWidget。
	 * @param index TAB在目标中的位置。
     */
	public BadgeView(Context context, TabWidget target, int index) {
		this(context, null, android.R.attr.textViewStyle, target, index);
	}
	
	public BadgeView(Context context, AttributeSet attrs, int defStyle) {
		this(context, attrs, defStyle, null, 0);
	}
	
	public BadgeView(Context context, AttributeSet attrs, int defStyle, View target, int tabIndex) {
		super(context, attrs, defStyle);
		init(context, target, tabIndex);
	}

	private void init(Context context, View target, int tabIndex) {
		
		this.context = context;
		this.target = target;
		this.targetTabIndex = tabIndex;
		
		// apply defaults
		badgePosition = DEFAULT_POSITION;
		badgeMarginH = dipToPixels(DEFAULT_MARGIN_DIP);
		badgeMarginV = badgeMarginH;
		badgeColor = DEFAULT_BADGE_COLOR;
		
		setTypeface(Typeface.DEFAULT_BOLD);
		int paddingPixels = dipToPixels(DEFAULT_LR_PADDING_DIP);
		setPadding(paddingPixels, 0, paddingPixels, 0);
		setTextColor(DEFAULT_TEXT_COLOR);
		
		fadeIn = new AlphaAnimation(0, 1);
		fadeIn.setInterpolator(new DecelerateInterpolator());
		fadeIn.setDuration(200);

		fadeOut = new AlphaAnimation(1, 0);
		fadeOut.setInterpolator(new AccelerateInterpolator());
		fadeOut.setDuration(200);
		
		isShown = false;
		
		if (this.target != null) {
			applyTo(this.target);
		} else {
			show();
		}
		
	}

	private void applyTo(View target) {
		
		LayoutParams lp = target.getLayoutParams();
		ViewParent parent = target.getParent();
		FrameLayout container = new FrameLayout(context);
		
		if (target instanceof TabWidget) {
			
			// set target to the relevant tab child container
			target = ((TabWidget) target).getChildTabViewAt(targetTabIndex);
			this.target = target;

			if(target != null){
				((ViewGroup) target).addView(container, new LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.MATCH_PARENT));

				this.setVisibility(View.GONE);
				container.addView(this);

			}
		} else {
			
			// TODO verify that parent is indeed a ViewGroup
			ViewGroup group = (ViewGroup) parent; 
			int index = group.indexOfChild(target);
			
			group.removeView(target);
			group.addView(container, index, lp);
			
			container.addView(target);
	
			this.setVisibility(View.GONE);
			container.addView(this);
			
			group.invalidate();
			
		}
		
	}
	
	/**
     * Make the badge visible in the UI.
     *
	 * 让徽章在UI中可见。
     */
	public void show() {
		show(false, null);
	}
	
	/**
     * Make the badge visible in the UI.
     * @param animate flag to apply the default fade-in animation.
	 *
	 * 让徽章在UI中可见。
	 * @param animate 标志来应用默认的渐隐动画。
     */
	public void show(boolean animate) {
		show(animate, fadeIn);
	}
	
	/**
     * Make the badge visible in the UI.
     * @param anim Animation to apply to the view when made visible.
	 *
	 * 让徽章在UI中可见。
	 * @param anim 使视图可见时应用于视图的动画。
     */
	public void show(Animation anim) {
		show(true, anim);
	}
	
	/**
     * Make the badge non-visible in the UI.
     *
	 * 使徽章在UI中不可见。
     */
	public void hide() {
		hide(false, null);
	}
	
	/**
     * Make the badge non-visible in the UI.
     * @param animate flag to apply the default fade-out animation.
	 *
	 * 使徽章在UI中不可见。
	 * @param animate 标志来应用默认的淡出动画。
     */
	public void hide(boolean animate) {
		hide(animate, fadeOut);
	}
	
	/**
     * Make the badge non-visible in the UI.
     * @param anim Animation to apply to the view when made non-visible.
	 *
	 * 使徽章在UI中不可见。
	 * @param anim 设置为不可见时应用于视图的动画。
     */
	public void hide(Animation anim) {
		hide(true, anim);
	}
	
	/**
     * Toggle the badge visibility in the UI.
     *
	 * 切换UI中的徽章可见性。
     */
	public void toggle() {
		toggle(false, null, null);
	}
	
	/**
     * Toggle the badge visibility in the UI.
     * @param animate flag to apply the default fade-in/out animation.
	 *
	 * 切换UI中的徽章可见性
	 * @param animate 标志来应用默认的渐入渐出动画。
     */
	public void toggle(boolean animate) {
		toggle(animate, fadeIn, fadeOut);
	}
	
	/**
     * Toggle the badge visibility in the UI.
     * @param animIn Animation to apply to the view when made visible.
     * @param animOut Animation to apply to the view when made non-visible.
	 *
	 * 切换UI中的徽章可见性
	 * @param animIn 使视图可见时应用于视图的动画。
	 * @param animOut 设置为不可见时应用于视图的动画。
     */
	public void toggle(Animation animIn, Animation animOut) {
		toggle(true, animIn, animOut);
	}
	
	private void show(boolean animate, Animation anim) {
		if (getBackground() == null) {
			if (badgeBg == null) {
				badgeBg = getDefaultBackground();
			}
			setBackgroundDrawable(badgeBg);
		}
		applyLayoutParams();
		
		if (animate) {
			this.startAnimation(anim);
		}
		this.setVisibility(View.VISIBLE);
		isShown = true;
	}
	
	private void hide(boolean animate, Animation anim) {
		this.setVisibility(View.GONE);
		if (animate) {
			this.startAnimation(anim);
		}
		isShown = false;
	}
	
	private void toggle(boolean animate, Animation animIn, Animation animOut) {
		if (isShown) {
			hide(animate && (animOut != null), animOut);	
		} else {
			show(animate && (animIn != null), animIn);
		}
	}
	
	/**
     * Increment the numeric badge label. If the current badge label cannot be converted to
     * an integer value, its label will be set to "0".
     * @param offset the increment offset.
	 *
	 * 增加数字徽章标签。如果当前徽章标签不能转换为整数值，则其标签将设置为“0”。
	 * @param offset 增加抵消。
     */
	public int increment(int offset) {
		CharSequence txt = getText();
		int i;
		if (txt != null) {
			try {
				i = Integer.parseInt(txt.toString());
			} catch (NumberFormatException e) {
				i = 0;
			}
		} else {
			i = 0;
		}
		i = i + offset;
		setText(String.valueOf(i));
		return i;
	}
	
	/**
     * Decrement the numeric badge label. If the current badge label cannot be converted to
     * an integer value, its label will be set to "0".
     * @param offset the decrement offset.
	 *
	 * 递减数字徽章标签。如果当前徽章标签不能转换为整数值，则其标签将设置为“0”。
	 * @param offset 减量抵消。
     */
	public int decrement(int offset) {
		return increment(-offset);
	}
	
	private ShapeDrawable getDefaultBackground() {
		
		int r = dipToPixels(DEFAULT_CORNER_RADIUS_DIP);
		float[] outerR = new float[] {r, r, r, r, r, r, r, r};
        
		RoundRectShape rr = new RoundRectShape(outerR, null, null);
		ShapeDrawable drawable = new ShapeDrawable(rr);
		drawable.getPaint().setColor(badgeColor);
		
		return drawable;
		
	}
	
	private void applyLayoutParams() {
		
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		
		switch (badgePosition) {
		case POSITION_TOP_LEFT:
			lp.gravity = Gravity.LEFT | Gravity.TOP;
			lp.setMargins(badgeMarginH, badgeMarginV, 0, 0);
			break;
		case POSITION_TOP_RIGHT:
			lp.gravity = Gravity.RIGHT | Gravity.TOP;
			lp.setMargins(0, badgeMarginV, badgeMarginH, 0);
			break;
		case POSITION_BOTTOM_LEFT:
			lp.gravity = Gravity.LEFT | Gravity.BOTTOM;
			lp.setMargins(badgeMarginH, 0, 0, badgeMarginV);
			break;
		case POSITION_BOTTOM_RIGHT:
			lp.gravity = Gravity.RIGHT | Gravity.BOTTOM;
			lp.setMargins(0, 0, badgeMarginH, badgeMarginV);
			break;
		case POSITION_CENTER:
			lp.gravity = Gravity.CENTER;
			lp.setMargins(0, 0, 0, 0);
			break;
		default:
			break;
		}
		
		setLayoutParams(lp);
		
	}

	/**
     * Returns the target View this badge has been attached to.
     *
	 * 返回已附加此徽章的目标视图。
     */
	public View getTarget() {
		return target;
	}

	/**
     * Is this badge currently visible in the UI?
     *
	 * 这个徽章目前在UI中可见吗?
     */
	@Override
	public boolean isShown() {
		return isShown;
	}

	/**
     * Returns the positioning of this badge.
     * one of POSITION_TOP_LEFT, POSITION_TOP_RIGHT, POSITION_BOTTOM_LEFT, POSITION_BOTTOM_RIGHT, POSTION_CENTER.
     *
	 * 返回此徽章的位置。
	 * 左上位置，右上位置，左下位置，右下位置，中间位置。
     */
	public int getBadgePosition() {
		return badgePosition;
	}

	/**
     * Set the positioning of this badge.
     * @param layoutPosition one of POSITION_TOP_LEFT, POSITION_TOP_RIGHT, POSITION_BOTTOM_LEFT, POSITION_BOTTOM_RIGHT, POSTION_CENTER.
     *
	 * 设置此徽章的位置。
	 *  @param layoutPosition 在左上位置，右上位置，左下位置，右下位置，中间位置，其中之一。
     */
	public void setBadgePosition(int layoutPosition) {
		this.badgePosition = layoutPosition;
	}

	/**
     * Returns the horizontal margin from the target View that is applied to this badge.
     *
	 * 返回应用于此徽章的目标视图的水平边距。
     */
	public int getHorizontalBadgeMargin() {
		return badgeMarginH;
	}
	
	/**
     * Returns the vertical margin from the target View that is applied to this badge.
     *
	 * 返回应用于此徽章的目标视图的垂直边距。
     */
	public int getVerticalBadgeMargin() {
		return badgeMarginV;
	}

	/**
     * Set the horizontal/vertical margin from the target View that is applied to this badge.
     * @param badgeMargin the margin in pixels.
	 *
	 * 设置应用于此徽章的目标视图的水平/垂直边距。
	 * @param badgeMargin 以像素为单位的空白。
     */
	public void setBadgeMargin(int badgeMargin) {
		this.badgeMarginH = badgeMargin;
		this.badgeMarginV = badgeMargin;
	}
	
	/**
     * Set the horizontal/vertical margin from the target View that is applied to this badge.
     * @param horizontal margin in pixels.
     * @param vertical margin in pixels.
	 *
	 * 设置应用于此徽章的目标视图的水平/垂直边距。
	 * @param horizontal 边缘像素。
	 * @param vertical 边缘像素。
     */
	public void setBadgeMargin(int horizontal, int vertical) {
		this.badgeMarginH = horizontal;
		this.badgeMarginV = vertical;
	}
	
	/**
     * Returns the color value of the badge background.
     *
	 * 返回徽章背景的颜色值。
     */
	public int getBadgeBackgroundColor() {
		return badgeColor;
	}

	/**
     * Set the color value of the badge background.
     * @param badgeColor the badge background color.
	 *
	 * 设置徽章背景的颜色值。
	 * @param badgeColor 徽章的背景颜色。
     */
	public void setBadgeBackgroundColor(int badgeColor) {
		this.badgeColor = badgeColor;
		badgeBg = getDefaultBackground();
	}
	
	private int dipToPixels(int dip) {
		Resources r = getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());
		return (int) px;
	}

}
