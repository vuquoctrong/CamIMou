package com.mm.android.mobilecommon.widget.antistatic.spinnerwheel.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mm.android.mobilecommon.utils.LogUtil;

/**
 * Abstract spinnerwheel adapter provides common functionality for adapters.
 *
 * 滚轮抽象适配器为适配器提供了通用功能。
 */
public abstract class AbstractWheelTextAdapter extends AbstractWheelAdapter {
    
    /** Text view resource. Used as a default view for adapter. */
    public static final int TEXT_VIEW_ITEM_RESOURCE = -1;
    
    /** No resource constant. */
    protected static final int NO_RESOURCE = 0;
    
    /** Default text color */
    public static final int DEFAULT_TEXT_COLOR =  0xFF000000;
    
    /** Default text color */
    public static final int LABEL_COLOR = 0xFF700070;
    
    /** Default text size */
    public static final int DEFAULT_TEXT_SIZE = 18;

    /// Custom text typeface
    private Typeface textTypeface;
    
    // Text settings
    private int textColor = DEFAULT_TEXT_COLOR;
    private int textSize = DEFAULT_TEXT_SIZE;
    
    // Current context
    protected Context context;
    // Layout inflater
    protected LayoutInflater inflater;
    
    // Items resources
    protected int itemResourceId;
    protected int itemTextResourceId;
    
    // Empty items resources
    protected int emptyItemResourceId;


    /**
     * Constructor
     * @param context the current context
     *
     * 构造器
     * @param context 当前上下文
     */
    protected AbstractWheelTextAdapter(Context context) {
        this(context, TEXT_VIEW_ITEM_RESOURCE);
    }

    /**
     * Constructor
     * @param context the current context
     * @param itemResource the resource ID for a layout file containing a TextView to use when instantiating items views
     *
     * 构造器
     * @param context 当前上下文
     * @param itemResource 布局文件的资源ID，其中包含在实例化项视图时使用的TextView
     */
    protected AbstractWheelTextAdapter(Context context, int itemResource) {
        this(context, itemResource, NO_RESOURCE);
    }
    
    /**
     * Constructor构造器
     * @param context the current context
     * @param itemResource the resource ID for a layout file containing a TextView to use when instantiating items views
     * @param itemTextResource the resource ID for a text view in the item layout
     *
     * @param context 当前上下文
     * @param itemResource 布局文件的资源ID，其中包含在实例化项视图时使用的TextView
     * @param itemTextResource 在项目布局中文本视图的资源ID
     */
    protected AbstractWheelTextAdapter(Context context, int itemResource, int itemTextResource) {
        this.context = context;
        itemResourceId = itemResource;
        itemTextResourceId = itemTextResource;
        
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
    /**
     * Gets text color
     * @return the text color
     *
     * 获取文本颜色
     *  @return 文本色
     */
    public int getTextColor() {
        return textColor;
    }
    
    /**
     * Sets text color
     * @param textColor the text color to set
     *
     * 设置文本色
     * @param textColor 要设置的文本色
     */
    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    /**
     * Sets text typeface
     * @param typeface typeface to set
     *
     * 设置文本字体
     * @param typeface 要设置的字体
     */
    public void setTextTypeface(Typeface typeface) {
        this.textTypeface = typeface;
    }

    /**
     * Gets text size
     * @return the text size
     *
     * 获取文本大小
     * @return 文本大小
     */
    public int getTextSize() {
        return textSize;
    }
    
    /**
     * Sets text size
     * @param textSize the text size to set
     *
     * 设置文本大小
     * @param textSize 要设置的大小
     */
    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }
    
    /**
     * Gets resource Id for items views
     * @return the item resource Id
     *
     * 获取项视图的资源Id
     * @return 项目资源Id
     */
    public int getItemResource() {
        return itemResourceId;
    }
    
    /**
     * Sets resource Id for items views
     * @param itemResourceId the resource Id to set
     *
     * 为项目视图设置资源Id
     * @param itemResourceId 要设置的资源Id
     */
    public void setItemResource(int itemResourceId) {
        this.itemResourceId = itemResourceId;
    }
    
    /**
     * Gets resource Id for text view in item layout 
     * @return the item text resource Id
     *
     * 在项目布局中获取文本视图的资源Id
     * @return 项目文本资源Id
     */
    public int getItemTextResource() {
        return itemTextResourceId;
    }
    
    /**
     * Sets resource Id for text view in item layout 
     * @param itemTextResourceId the item text resource Id to set
     *
     * 在项目布局中为文本视图设置资源Id
     * @param itemTextResourceId 要设置的项目文本资源Id
     */
    public void setItemTextResource(int itemTextResourceId) {
        this.itemTextResourceId = itemTextResourceId;
    }

    /**
     * Gets resource Id for empty items views
     * @return the empty item resource Id
     *
     * 获取空项视图的资Id
     * @return 空项目资源Id
     */
    public int getEmptyItemResource() {
        return emptyItemResourceId;
    }

    /**
     * Sets resource Id for empty items views
     * @param emptyItemResourceId the empty item resource Id to set
     *
     * 为空项视图设置资源Id
     * @param emptyItemResourceId 要设置的空项目资源Id
     */
    public void setEmptyItemResource(int emptyItemResourceId) {
        this.emptyItemResourceId = emptyItemResourceId;
    }

    /**
     * Returns text for specified item
     * @param index the item index
     * @return the text of specified items
     *
     * 返回指定项的文本
     * @param index 下标
     * @return 指定项的文本
     */
    protected abstract CharSequence getItemText(int index);

    @Override
    public View getItem(int index, View convertView, ViewGroup parent) {
        if (index >= 0 && index < getItemsCount()) {
            if (convertView == null) {
                convertView = getView(itemResourceId, parent);
            }
            if (convertView == null) {
                convertView = new View(context);
            }
            TextView textView = getTextView(convertView, itemTextResourceId);
            if (textView != null) {
                CharSequence text = getItemText(index);
                if (text == null) {
                    text = "";
                }
                textView.setText(text);
                configureTextView(textView);
            }
            return convertView;
        }
        return null;
    }

    @Override
    public View getEmptyItem(View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = getView(emptyItemResourceId, parent);
        }
        if (convertView instanceof TextView) {
            configureTextView((TextView)convertView);
        }
            
        return convertView;
    }

    /**
     * Configures text view. Is called for the TEXT_VIEW_ITEM_RESOURCE views.
     * @param view the text view to be configured
     *
     * 配置文本视图。为文本视图项资源视图调用。
     * @param view 要配置的文本视图
     */
    protected void configureTextView(TextView view) {
        if (itemResourceId == TEXT_VIEW_ITEM_RESOURCE) {
            view.setTextColor(textColor);
            view.setGravity(Gravity.CENTER);
            view.setTextSize(textSize);
            view.setLines(1);
        }
        if (textTypeface != null) {
            view.setTypeface(textTypeface);
        } else {
            view.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        }
    }
    
    /**
     * Loads a text view from view
     * @param view the text view or layout containing it
     * @param textResource the text resource Id in layout
     * @return the loaded text view
     *
     * 从视图中加载文本视图
     * @param view 包含它的文本视图或布局
     * @param textResource 布局中的文本资源Id
     * @return 加载的文本视图
     */
    private TextView getTextView(View view, int textResource) {
        TextView text = null;
        try {
            if (textResource == NO_RESOURCE && view instanceof TextView) {
                text = (TextView) view;
            } else if (textResource != NO_RESOURCE) {
                text = view.findViewById(textResource);
            }
        } catch (ClassCastException e) {
            LogUtil.errorLog("AbstractWheelAdapter", "You must supply a resource ID for a TextView");
            throw new IllegalStateException(
                    "AbstractWheelAdapter requires the resource ID to be a TextView", e);
        }
        
        return text;
    }
    
    /**
     * Loads view from resources
     * @param resource the resource Id
     * @return the loaded view or null if resource is not set
     *
     * 从资源加载视图
     * @param resource Resource Id
     * @return 加载的视图，如果没有设置resource则为空
     */
    private View getView(int resource, ViewGroup parent) {
        switch (resource) {
        case NO_RESOURCE:
            return null;
        case TEXT_VIEW_ITEM_RESOURCE:
            return new TextView(context);
        default:
            return inflater.inflate(resource, parent, false);
        }
    }
}
