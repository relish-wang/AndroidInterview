# Java

[TOC]

## final关键字修饰方法、类、字段有什么区别
**类:** 不能被继承(比如: String)

**方法:** 不能被重写

**字段:** 值不能被修改(属性能被修改)

## 多线程

### ① 线程死锁的原因和解决办法

原因: **线程互相等待资源，又不释放自身的资源，导致无穷无尽的等待。**

产生死锁的必要条件有四个：
- 1.**互斥条件**: 所谓互斥就是进程在某一时间内独占资源。
- 2.**请求与保持条件**: 一个进程因请求资源而阻塞时，对已获得的资源保持不放。
- 3.**不剥夺条件**: 进程已获得资源，在末使用完之前，不能强行剥夺。
- 4.**循环等待条件**: 若干进程之间形成一种头尾相接的循环等待资源关系。

预防的具体方法：

- 1 **加锁顺序**(synchronized代码块嵌套)

```java
synchronized(objA){
	synchronized(objB){
		synchronized(objC){
				// do sth.
		}
	}
}
```

- 2 **加锁时限**

超过一定时间必释放锁。

- 3 **死锁检测**

用于记录线程和锁关系的数据结构(map、graph)。当某个线程请求所失败时，遍历表(map)或图(graph)，检查是否有死锁发生。当检测出有死锁发生的时候：① 释放所有锁，回退，并且等待一段随机的时间后重试; ② 给这些线程设置优先级，让一个（或几个）线程回退，剩下的线程就像没发生死锁一样继续保持着它们需要的锁。

解决死锁的措施:

- 1 **重启系统。**代价大。意味着在此之前所有的进程已经完成的计算工作全都白费。
- 2 **撤销进程，剥夺资源。**终止参与死锁的进程，收回他们所占有的资源，从而解除死锁。这时又分为两种情况: 一次性撤销参与死锁的进程，剥夺全部资源；或者逐步撤销参与死锁的进程，逐步回收死锁进程占有的资源。一般来说，选择逐步撤销的进程时，要按照一定的原则进行，目的是撤销那些代价最小的进程，比如按进程的优先级确定进程的代价;考虑进程运行时的代价和与此进程相关的外部作业的代价的因素。
- 3 **进程回退策略**，即让参与死锁的进程回退到发生死锁前的某一处，并由此点处继续执行，以求再次执行时不再发生死锁。虽然这是个较理想的办法，但是操作起来系统开销极大，要有对战记录进程的每一步变化，以便今后的回退，大多数情况下是无法做到的。    

### ② wait和sleep有什么区别

**sleep不会释放对象锁。**sleep()方法是线程类（Thread）的静态方法，导致此线程暂停执行指定时间，将执行机会给其他线程，但是监控状态依然保持，到时后会自动恢复（线程回到就绪（ready）状态），因为调用 sleep 不会释放对象锁。

**wait会释放对象锁。**wait() 是 Object 类的方法，对此对象调用 wait()方法导致本线程放弃对象锁(线程暂停执行)，进入等待此对象的等待锁定池，只有针对此对象发出 notify 方法（或 notifyAll）后本线程才进入对象锁定池准备获得对象锁进入就绪状态。

### ③ volatile关键字的原理

> A Java(TM) programming language keyword used in variable declarations that specifies that the variable is modified asynchronously by concurrently running threads.

使用volatile变量可降低内存一致性错误的风险，因为对volatile变量的任何写入都会与该变量的后续读取建立happens-before关系。这意味着对volatile变量的更改始终对其他线程可见。而且，这还意味着，当线程读取一个volatile变量时，它不仅会看到对volatile的最新更改，还会看到导致更改的代码的副作用。

- 能保证原子性吗 

  否

- 能保证线程安全吗 

  否。必须同时满足下面两个条件才能保证在并发环境的线程安全：
      
（1）对变量的写操作不依赖于当前值。

（2）该变量没有包含在具有其他变量的不变式中。
      
### ④ synchronized关键字原理，修饰在不同的地方(代码块，静态方法，对象方法)分别是锁什么东西?

静态方法: 当前类的锁。

非静态方法: 当前对象的锁。

结论: 类锁和对象锁不同，他们之间不会产生互斥

## 线程池

### ① 线程池的构造方法的几个参数, 含义作用

```java
// Java线程池的完整构造函数
public ThreadPoolExecutor(
      int corePoolSize, // 线程池长期维持的线程数，即使线程处于Idle状态，也不会回收。
      int maximumPoolSize, // 线程数的上限
      long keepAliveTime,  // 超过corePoolSize的线程的idle时长，
			TimeUnit unit,      // 超过这个时间，多余的线程会被回收。
      BlockingQueue<Runnable> workQueue, // 任务的排队队列
      ThreadFactory threadFactory, // 新线程的产生方式
      RejectedExecutionHandler handler) // 拒绝策略
```

### ② ThreadLocal用过吗？讲讲原理
- ThreadLocal提供了线程的局部变量，每个线程都可以通过`set()`和`get()`来对这个局部变量进行操作，但不会和其他线程的局部变量进行冲突，**实现了线程的数据隔离**～。
- key为当前ThreadLocal对象，value为泛型。
- ThreadLocal内存泄漏的根源:
**由于ThreadLocalMap的生命周期跟Thread一样长，如果没有手动删除对应key就会导致内存泄漏，而不是因为弱引用**。

## JVM(Java虚拟机)和JMM(Java内存模型)

### ① gc触发的时机, 回收优先级(哪些对象会优先回收)

- Minor GC触发条件：当Eden区满时，触发Minor GC。
  
- Full GC触发条件：

（1）调用System.gc时，系统建议执行Full GC，但是不必然执行

（2）老年代空间不足
（3）方法区空间不足
（4）通过Minor GC后进入老年代的平均大小大于老年代的可用内存

（5）由Eden区、From Space区向To Space区复制时，对象大小大于To Space可用内存，则把该对象转存到老年代，且老年代的可用内存小于该对象大小

### ② 弱引用和软引用的区别

**没有强引用指向弱引用的指向的对象时，弱引用就会被回收。**即WeakReference不改变原有的强引用对象的垃圾回收机制。一旦其指示对象没有任何强引用对象时，此对象即进入正常的垃圾回收流程。

而软引用需要在**没有强引用指向弱引用的指向的对象**且**内存不足**时才会回收它指向的对象。

### ③ 为什么会出现内存抖动？如何处理(注意是处理不是预防)

**原因**: 短时间内申请大量临时对象, 又在短时间内释放临时对象。(比如: 循环体内; 自定义View的onDraw方法内;Adapter的getView/onBindViewHolder内申请对象)

- 定位: android profile|Tools->Android->Android Device Monitor
- LeakCanary
- MAT

### NIO和BIO的区别

NIO只需要开启**一个线程**就可以处理来自**多个客户端**的IO事件

### JVM定义了几种线程的状态

NEW、RUNNABLE、BLOCKED、WAITING、TIMED_WAITING、TERMINATED

## HashMap

更多关于HashMap的面试题->[在这里](./hashmap.md)