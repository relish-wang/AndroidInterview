# 提升性能Tips

- bitmap(极易OOM)
  - 缓存
  - 缩放
  - 工作线程中处理
  - 显示选择合适的像素格式: ARGB_8888、ARGB_4444、ARGB_565、ALPHA_8
  - 处理大量图像时, 重用所分配的内存(HOW?), 避免内存抖动
  - 根据屏幕尺寸和密度挑选合适的图片压缩方案和压缩率以及图片分辨率
- 不要在类内部调用get/set(没有JIT: 直接访问比get访问快3被; 有JIT: 直接访问比get访问快7倍。)
- private的非内部类为了访问外部类代码, 在编译时会生成静态方法, 会导致访问变慢。改成包作用域可以避免, 但会导致包级别其他类也能访问这个内部类。这点需要开发者自己取舍。
- 减少apk大小
  - 移除未使用代码(minifyEnable true)
  - 移除未使用资源或不必要的资源
    - shrinkResources true // 移除未使用的资源
    - resConfig "en", "zh" // 仅保留中文和英文的资源(包括依赖的其他库也会剔除其他语言的资源); 屏幕密度、方向、语言、Android版本等。


# 帧率监控

腾讯的matrix。

- 7.0以下设备

在帧率这部分，Matrix 创新性的 hook 了 Choreographer 的 CallbackQueue，同时还通过反射调用 addCallbackLocked 在每一个回调队列的头部添加了自定义的 FrameCallback。如果回调了这个 Callback，那么这一帧的渲染也就开始了，当前在 Looper 中正在执行的消息就是渲染的消息。这样除了监控帧率外，还能监控到当前帧的各个阶段耗时数据。
除此之外，帧率回调和 Looper 的 Printer 结合使用，能够在出现卡顿帧的时候去 dump 主线程信息，便于业务方解决卡顿，但是频繁拼接字符串会带来一定的性能开销（println 方法调用时有字符串拼接）。

> 利用Choreographer的postcallback方法接口轮询方式，能够对帧率进行统计。

- 7.0及以上设备
Window.addOnFrameMetricsAvailableListener

## 滑动帧率监控

View里如果有滑动行为产生最终都会调用到**onScrollChanged()**,
当该方法调用的时候，会将mAttachInfo的mViewScrollChanged值设为true

如上代码ViewRootImpl的draw方法会如果check到mAttachInfo.
mViewScrollChanged值为true就会就会调用
ViewTreeObserver的dispatchOnScrollChanged()方法，
只要我们在viewTreeObserver设置监听，就能获取到界面是否正在滑动这一重要事件。