# Android开源框架

[TOC]

## LeakCanary原理。如何检测内存泄漏？

Application#registerActivityLifecycleCallbacks监听Activity的生命周期。

如果弱引用 reference 持有的对象被 GC 回收，JVM 就会把这个弱引用加入到与之关联的引用队列 referenceQueue 中

1 监听 Activity 的生命周期

2 在 onDestroy 的时候，创建相应的 Reference 和 ReferenceQueue，并启动后台进程去检测

3 一段时间之后，从 ReferenceQueue 读取，若读取不到相应 activity 的 Reference，有可能发生泄露了，这个时候，再触发 gc，一段时间之后，再去读取，若在从 ReferenceQueue 还是读取不到相应 activity 的 reference，可以断定是发生内存泄露了

4 发生内存泄露之后，dump，分析 hprof 文件，找到泄露路径（使用 [haha][haha] 库分析），发送到通知栏

[haha]: https://www.jianshu.com/p/31d2da927614

### LeakCanary如何判断对象有没有被回收？

// TODO


## RxJava

知道哪些操作符,  举一个聚合操作符(zip)说说如何实现的？

## okhttp+retrofit

框架做了些什么事情

- 允许连接到同一个主机地址的所有请求,提高请求效率

- 共享Socket,减少对服务器的请求次数

- 通过连接池,减少了请求延迟

- 缓存响应数据来减少重复的网络请求

- 减少了对数据流量的消耗

- 自动处理GZip压缩

### retrofit怎么实现的动态代理？

Proxy.newProxyInstance() 实现InvocationHandler。

## ARouter

如何实现路由？(建议从路由注册->路由调用两个方面讲，调用的时候是怎么一步一步找到路由注册的地方的)

## 热修复框架

热修复原理有哪些流派?各自的实现原理是什么？transform API用过吗？

// TODO

## Glide

Glide的特点：

- 支持GIF动图

- 支持加载缩略图

- Activity生命周期的集成

- OkHttp和Volley的支持: 默认采用HttpUrlConnection作为网络协议栈

- 动画的支持：新增支持图片的淡入淡出动画效果