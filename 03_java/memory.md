# 内存管理

## JVM(Java虚拟机)和JMM(Java内存模型)

### ① 了解内存泄露相关的知识吗？为什么会产生内存泄漏？

一个生命周期长的对象被生命周期短所持有。最终导致OOM。

- 匿名内部类
- context
- AsyncTask
- bitmap未recycle()
- 游标、IO流未及时关闭

## ② 了解GC流程吗？

通过多个GCRoot触发查找引用树, 要是对象不在引用树上的话, 就认为是可回收的。

### ③ 有哪些对象可以作为GC Root？

1 虚拟机栈中引用的对象(栈帧中的本地变量表)
2 方法中类的静态属性引用的对象
3 方法区中常量引用的对象
4 Native 方法引用的对象

###  gc触发的时机, 回收优先级(哪些对象会优先回收)

- Minor GC触发条件：当Eden区满时，触发Minor GC。

- Full GC触发条件：

（1）调用System.gc时，系统建议执行Full GC，但是不必然执行

（2）老年代空间不足

（3）方法区空间不足

（4）通过Minor GC后进入老年代的平均大小大于老年代的可用内存

（5）由Eden区、From Space区向To Space区复制时，对象大小大于To Space可用内存，则把该对象转存到老年代，且老年代的可用内存小于该对象大小

### ④ 如何检测内存泄漏？

LeakCanary

### ⑤ LeakCanary如何实现内存泄漏的检查？LeakCanary如何判断对象有没有被回收？

TODO

### ② 弱引用和软引用的区别

**没有强引用指向弱引用的指向的对象时，弱引用就会被回收。**即WeakReference不改变原有的强引用对象的垃圾回收机制。一旦其指示对象没有任何强引用对象时，此对象即进入正常的垃圾回收流程。

而软引用需要在**没有强引用指向弱引用的指向的对象**且**内存不足**时才会回收它指向的对象。

### ③ 为什么会出现内存抖动？如何处理(注意是处理不是预防)

**原因**: 短时间内申请大量临时对象, 又在短时间内释放临时对象。(比如: 循环体内; 自定义View的onDraw方法内;Adapter的getView/onBindViewHolder内申请对象)

- 定位: android profile|Tools->Android->Android Device Monitor
- LeakCanary
- MAT工具

- [《Android 性能优化 - 彻底解决内存抖动》](https://juejin.im/post/5a7ff867f265da4e865a6b5b)