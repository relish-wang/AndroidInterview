## 协程

### ① 用过协程吗？讲讲原理

它提供了轻量级的任务调度机制。协程的目的是在不阻塞线程的情况下实现任务的挂起和恢复。

协程就是一个线程框架。跟Java的Executor; Android的Handler、AsyncTask一样。性能不分上下，但它的优点是写法简单。可以轻松的写出并发的代码。用同步的写法，写出异步的代码。

比其他线程框架的**优势**: 可以在同一个代码块里进行线程的切换(避免回调地狱)

### ② suspend关键字

suspend关键字的作用: 提醒。提醒调用者，我这个函数需要在协程或另一个挂起函数中被调用。它并不起到挂起的功能。

- suspend挂起的是什么?   
  挂起的就是协程。协程所在的线程不再执行协程里的代码了。
- 从哪挂起?   
  从当前线程挂起。
- 什么时候切回来？
  协程在它指定的线程执行完毕后, 会切回它挂起时的线程。(切回来的动作: resume)
- 为什么一个suspend函数必须在协程或者另一个挂起函数中使用？  
  因为协程是需要恢复(resume)的, 一个suspend函数要是不在协程或一个挂起函数中使用, 它就没办法恢复。

### ③ 什么叫「非阻塞」式挂起？

非阻塞式: 不卡线程。用Java的Thread切线程也是非阻塞的。完全一样。

协程的非阻塞是只是一种看起来阻塞实际上不阻塞的写法而已。

CPU的计算耗时 + I/O耗时(和网络的数据交互, 它的等待是因为**网络传输的性能低于CPU的性能**。)

### 异常处理


将CoroutineExceptionHandler传入lanuch。

```kotlin
val handler = CoroutinesExceptionHandler{ _, e -> println(e) }
GlobalScope.launch(handler){
    // do sth.
}
```

CoroutineExceptionHandler间接继承CoroutinesContext。

类似Thread.uncatchedExceptionHandler;