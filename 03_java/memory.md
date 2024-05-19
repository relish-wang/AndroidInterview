# 内存管理

## JVM(Java虚拟机)和JMM(Java内存模型)

### JMM

堆: 对象。
线程栈: 调用栈+本地变量

线程私有的：程序计数器、虚拟机栈、本地方法栈

线程共享的：堆、方法区、直接内存 (非运行时数据区的一部分)

Java 虚拟机规范对于运行时数据区域的规定是相当宽松的。  
以堆为例：堆可以是连续空间，也可以不连续。堆的大小可以固定，也可以在运行时按需扩展。    
虚拟机实现者可以使用任何垃圾回收算法管理堆，甚至完全不进行垃圾收集也是可以的。

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
4 Native方法(JNI)栈内引用的对象
5 所有被同步锁synchronized持有的对象
6 Java虚拟机内部的引用。<sub>一些常驻的异常对象(如： NullPointerException、OutOfMemoryError)，系统类加载器。</sub>

### gc触发的时机, 回收优先级(哪些对象会优先回收)

GcRetentionPolicy
每个Space都有自己的gc回收策略，如下图所示：

kGcRetentionPolicyNeverCollect：不需要回收某个Space所包含的垃圾对象（因为该Space可能不存在垃圾对象）。
kGcRetentionPolicyAlwaysCollect：每次垃圾回收都需要处理某个Space空间。
kGcRetentionPolicyFullCollect：直到最后时刻才回收某个Space空间中的垃圾对象。这个最后时刻就是所谓的full GC。

![][art_gc]


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

WeakReference 和 ReferenceQueue，即 LeakCanary 利用了 Java 的 WeakReference 和 ReferenceQueue，通过将 Activity 包装到 WeakReference 中，
被 WeakReference 包装过的 Activity 对象如果能够被回收，则说明引用可达，垃圾回收器就会将该 WeakReference 引用存放到 ReferenceQueue 中。
假如我们要监视某个 Activity 对象，LeakCanary 就会去 ReferenceQueue 找这个对象的引用，如果找到了，说明该对象是引用可达的，能被 GC 回收，
如果没有找到，说明该对象有可能发生了内存泄漏。最后，LeakCanary 会将 Java 堆转储到一个 .hprof 文件中，再使用 Shark（堆分析工具）析 .hprof 文件并定位堆转储中“滞留”的对象，
并对每个"滞留"的对象找出 GC roots 的最短强引用路径，并确定是否是泄露，如果泄漏，建立导致泄露的引用链。最后，再将分析完毕的结果以通知的形式展现出来。

原文链接：https://blog.csdn.net/hello_1995/article/details/120075342

### ② 弱引用和软引用的区别

**没有强引用指向弱引用的指向的对象时，弱引用就会被回收。**
即WeakReference不改变原有的强引用对象的垃圾回收机制。一旦其指示对象没有任何强引用对象时，此对象即进入正常的垃圾回收流程。

而软引用需要在**没有强引用指向弱引用的指向的对象**且**内存不足**时才会回收它指向的对象。

### ③ 为什么会出现内存抖动？如何处理(注意是处理不是预防)

**原因**: 短时间内申请大量临时对象, 又在短时间内释放临时对象。(比如: 循环体内;
自定义View的onDraw方法内;Adapter的getView/onBindViewHolder内申请对象)

- 定位: android profile|Tools->Android->Android Device Monitor
- LeakCanary
- ~~MAT工具(Eclipse Memory Analyzer)~~ 太过时了,应该不会再用了。
- IDEA plugin: JProfiler
- AndroidStudio: Profiler
  - Allocations：Java堆中的实例个数
  - Native Size：Native层分配的内存大小
  - Shallow Size：本对象实例在Java堆中占用的内存大小
  - Retained Size：这个类的实例本身的对象，以及它直接或者间接引用的所有对象占用的内存大小
![](./art/profiler.png)

- [《Android 性能优化 - 彻底解决内存抖动》](https://juejin.im/post/5a7ff867f265da4e865a6b5b)

[art_gc]: ./art/art_gc.png

[android_gc]: https://juejin.cn/post/6966205309782065159

[深入理解Android ART虚拟机]: https://weread.qq.com/web/reader/3ee32e60717f5af83ee7b37ke3732b703119e3796ae8bea