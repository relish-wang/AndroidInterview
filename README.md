# Android面试题整理

对近几年遇到/看到的面试题做了些整理。

[TOC]

## [操作系统](./01_operation_system/operation_system.md)

- 什么是操作系统？
- 线程与进程的关系与区别
- 进程的生命周期
- 进程通信方式(Android进程通信方式Binder)
- 进程与端口

## [计算机网络](./02_computer_network/computer_network.md)

- HTTP三次握手
  - 为什么是三次？两次够不够？
  - 第三次是否可以携带数据？
- HTTP四次挥手
- HTTP常见header和作用
- HTTP和HTTPS区别
- HTTPS的SSL握手流程

## [Java基础](./03_java/java.md)

- final关键字的作用
- [HashMap常见面试题](./03_java/hashmap.md)
  - 基本数据结构组成
  - get/set方法做了什么
  - 构造器的参数和扩容策略
  - HashTable、ConcurrentHashMap与HashMap的区别及具体实现
  - LinkedHashMap
  - 如何处理hash冲突
  - HashMap的链表和红黑树互转的节点, 为什么这样设计？
  - SparseArray、ArrayMap和HashMap对比
- [多线程](./03_java/multi_thread.md)
  - 线程死锁的原因和解决办法
  - wait和sleep有什么区别
  - volatile关键字的原理
    - 原子性？
    - 线程安全？
  - synchronized关键字原理, 使用在不同位置分别锁的是什么？
  - 线程池
  - 线程安全集合类
    - Map
    - List
    - 线程安全集合工具
  - 锁的分类
    - 乐观锁/悲观锁
    - 独享锁/共享锁
    - 互斥锁/读写锁
    - 可重入锁
    - 公平锁/非公平锁
    - 分段锁
    - 偏向锁/轻量级锁/重量级锁
    - 自旋锁

## [kotlin基础](./04_kotlin/kotlin.md)

- 协程
  - 原理
  - suspend关键字作用
  - 什么叫「非阻塞式」挂起
  - 异常处理
- 特殊类
  - sealed class(密封类)
  - value class(内联类)
- kotlin空安全处理
- 高阶函数
- companion是什么
- kotlin如何快速创建单例
- kotlin增量编译

## [Android基础](05_android/android.md)
- Android基础
  - Android架构分几层？
  - 从手机桌面点击App到第一个Activity启动，发生了什么？
  - Window/WindowManger/WindowMangerService
- [RecyclerView](./05_android/recyclerview.md)
  - ItemDecoration
  - RecyclerView的优化
  - 多类型item
  - 缓存机制
- ViewPager的三个Adapter要怎么选择使用
  - PagerAdapter
  - FragmentPagerAdapter
  - FragmentStatePagerAdapter
- [开源框架](./05_android/open_source.md)
  - okhttp/retrofit
  - LeakCanary
  - RxJava
  - ARouter
  - Glide
- [储存](./05_android/storage.md
  - 内部私有目录
  - 外部私有目录
  - SharedPreference
  - mmkv
- Handler/Looper/MessageQueue关系
- IntentService
- View事件分发
  - onTouch、onTouchEvent、onClick
  - ACTION_CANCEL
- View绘制相关
  - 垂直同步
  - textView.setText()调用两次, 触发几次垂直刷新?
  - requestLayout()/invalidate()区别
  - 开启/关闭硬件加速对View绘制有何影响
  - 如何用Drawable优雅地实现自定义View的动画效果
  - RenderThread
- 自定义View优化
- 遇到过页面卡顿吗? 可能是什么原因造成的？如何排查发生的原因？最佳实践？
- 跨进程
  - Binder
  - Messager
- 跨端
  - JSBridge原理
- 打包
  - 简述打包流程
    - 混淆在哪一步
    - R文件生成在哪一步
    - apk里的resuource.arsc有什么用
  - 如何优雅地打渠道包((applicationId一致, 仅资源文件不同, 答flavors的不得分)
    - walle
- 安全相关
- 内存优化最佳实践
  - 内存泄漏
  - 内存抖动
  - 其他
- 内存泄漏排查
  - Android Profiler
  - LeakCanary/KOOM

## [数据结构与算法](06_algorithm/algorithm.md)
- 堆排序实现
- 二叉树的最小公共父节点
- 回文数判断
- 删除链表的倒数第k个节点
- 其他智力题
- 单链表实现LRUCache

## [反问面试官的问题(含解析)](07_q&a/Q&A.md)
1 帮助面试者更好地了解应聘公司的各方面情况(比如团队氛围、团队规模、技术架构选型、未来战略方向等), 为自己挑选心仪的公司有更多的评判依据。
2 帮助面试者更好地针对不同问题采取不同的应对策略。

- 了解公司情况
  - 团队规模
  - 技术架构选型
  - 基建设施情况
    - 自动化埋点
    - ABTest、客户端配置中心
    - 日志回捞
    - ANR/Crash/OOM治理&监控
    - 冷启动速度监控
    - 页面打开速度监控
    - 包体积大小监控
    - lint静态代码检测
  - 可以让技术面试官简单介绍一下自己
  - 当前招聘的岗位对哪方面的技能有比较高的要求

