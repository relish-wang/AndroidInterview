# RecyclerView

## RecyclerView怎么画分割线？

ItemDecoration

## RecyclerView如何绿色红色间隔展示分割线
```kotlin
class MyDivide : RecyclerView.ItemDecoration() {
    private val paint = Paint()
    /**
     * 在Item渲染之前
     */
    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
    }

    /**
     * 在Item渲染之后
     */
    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        for (i in 0 until parent.childCount) {// 可见item
            val child = parent.getChildAt(i) ?: continue // itemView
            val p = parent.getChildAdapterPosition(child) // correct position
            paint.color = if (p % 2 == 0) Color.RED else Color.GREEN
            c.drawRect(
                child.left.toFloat(),
                child.top.toFloat(),
                child.right.toFloat(),
                child.top.toFloat() - 20F,
                paint
            )
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.set(0, 0, 0, 20)
    }
}
```

## 画分割线是在ViewHolder渲染之前还是渲染之后？
draw: Item渲染之前  
drawOver: Item渲染之后 

## RecyclerView的优化

1 减少布局层级,避免过度绘制（使用ConstraintLayout减少布局层级）

2 setHasFixedSize(true)

3 根据需求修改RecyclerView默认的绘制缓存选项
(用空间换时间，来提高滚动的流畅性。)
```java
 recyclerView.setItemViewCacheSize(20);
 recyclerView.setDrawingCacheEnabled(true);
 recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
```

4 onBindViewHolder减少逻辑判断，减少临时对象创建(例: 复用事件监听, 在外部创建监听)

5 避免整个列表的数据更新，只更新受影响的布局(例:修改单条item时, notifyItemChanged;上拉加载更多时,notifyItemRangeInserted;DiffUtil)

6 scrollingCache=false animationCache=false(针对ListView)

7 共用RecycledViewPool(在嵌套RecyclerView中，如果子RecyclerView具有相同的adapter，那么可以设置RecyclerView.setRecycledViewPool(pool)来共用一个RecycledViewPool)

8 增加RecyclerView预留的额外空间
```java
new LinearLayoutManager(this) {
    @Override
    protected int getExtraLayoutSpace(RecyclerView.State state) {
        return size;
    }
};
```

9 回收资源(通过重写RecyclerView.onViewRecycled(holder)来回收资源。)

## RecyclerView如何设置多类型的Item
为不同的viewType设置不同的item布局

## 对比ListView和RecyclerView的缓存机制
| 缓存级别 | 实际变量 | 含义|
|:-:|:-:|:-:|
| 一级缓存 | mAttachedScrap和mChangedScrap | 这是优先级最高的缓存，RecyclerView在获取ViewHolder时,优先会到这两个缓存来找。其中mAttachedScrap存储的是当前还在屏幕中的ViewHolder，mChangedScrap存储的是数据被更新的ViewHolder,比如说调用了Adapter的notifyItemChanged方法。可能有人对这两个缓存还是有点疑惑，不要急，待会会详细的解释。|
| 二级缓存 | mCachedViews | 默认大小为2，通常用来存储预取的ViewHolder，同时在回收ViewHolder时，也会可能存储一部分的ViewHolder，这部分的ViewHolder通常来说，意义跟一级缓存差不多。|
| 三级缓存 | ViewCacheExtension | 自定义缓存,通常用不到，在本文中先忽略 |
| 四级缓存 | RecyclerViewPool | 根据ViewType来缓存ViewHolder，每个ViewType的数组大小为5，可以动态的改变。|



