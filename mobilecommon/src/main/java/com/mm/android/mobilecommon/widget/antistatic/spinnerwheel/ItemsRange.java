package com.mm.android.mobilecommon.widget.antistatic.spinnerwheel;

    /**
     * Range for visible items.
     *
     * 可见项目的范围。
     */
    public class ItemsRange {
        // First item number
        private int first;
        
        // Items count
        private int count;
    
        /**
         * Default constructor. Creates an empty range
         *
         * 默认构造函数。创建空范围
         */
        public ItemsRange() {
            this(0, 0);
        }
        
        /**
         * Constructor
         * @param first the number of first item
         * @param count the count of items
         *
         * 构造器
         * @param first 第一个元素编号
         * @param count 元素个数
         */
        public ItemsRange(int first, int count) {
            this.first = first;
            this.count = count;
        }
        
        /**
         * Gets number of  first item
         * @return the number of the first item
         *
         * 获取第一个项的编号
         * @return 第一项的编号
         */
        public int getFirst() {
            return first;
        }
        
        /**
         * Gets number of last item
         * @return the number of last item
         *
         * 获取最后一项的编号
         * @return 最后一项的编号
         */
        public int getLast() {
            return getFirst() + getCount() - 1;
        }
        
        /**
         * Get items count
         * @return the count of items
         *
         * 得到元素个数
         * @return 元素个数
         */
        public int getCount() {
            return count;
        }
        
        /**
         * Tests whether item is contained by range
         * @param index the item number
         * @return true if item is contained
         *
         * 测试项是否由范围控制
         * @param index 元素编号
         * @return true 是否包含该元素
         */
        public boolean contains(int index) {
            return index >= getFirst() && index <= getLast();
        }
}