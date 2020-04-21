# 使用happens-before关系处理Java内存一致性问题

> 原文: [《Handling Java Memory Consistency with happens-before relationship》](https://medium.com/@kasunpdh/handling-java-memory-consistency-with-happens-before-relationship-95ddc837ab13)

[TOC]

如果要用Java开发多线程应用程序，则需要了解如何在Java内存中处理共享变量。一个重要的因素就是happens-before关系。为了理解Java中happens-before关系，您需要熟悉并发编程中可见性的概念。

## **Visibility**

> 如果一个线程中的某个动作对另一线程可见，则第二个线程可以观察到该动作的结果。

为了进一步了解上述语句，让我们看一下现代共享内存多处理器的体系结构。

如今，几乎所有计算机的处理器内部具有多个内核，每个内核都能够处理多个执行线程。对于每个核心，都存在多个级别的缓存。

![][processor_arch]

> 图片来源: https://wiki.sei.cmu.edu/confluence/display/java/Concurrency%2C+Visibility%2C+and+Memory

对共享变量的写入操作的可见性可能会由于每个内核中的缓存而在写入主存储器时出现延迟，从而导致问题。这可能导致另一个线程读取该变量的旧值（而不是最后更新的值）。

让我们考虑一下两个线程对同一个变量执行读写操作的情况。

```java
public class StopThread {
        private static boolean stopRequested;
        public static void main(String[] args)
                throws InterruptedException {
            Thread backgroundThread = new Thread(new Runnable() {
                public void run() {
                    int i = 0;
                    while (!stopRequested)
                        i++;
                }
            });
            backgroundThread.start();
            TimeUnit.SECONDS.sleep(1);
            stopRequested = true;
        }
    }
```

在上述的示例中(来自Joshua Bloch撰写的《Effective Java》)，有一个后台线程（`backgroundThread`）将递增` i`的值，直到`stopRequested`布尔值变为`true`。通过程序的主线程启动线程后，它将休眠1秒钟，并使“`stopRequested`变为`true`。

结果如何？理想情况下，程序应运行1秒钟，并且在`stopRequested`变为`true`之后，`backgroundThread`应结束，从而终止整个程序。

但是，如果您在具有多个内核的计算机上运行以上命令，则会发现该程序可以继续执行而不会被终止。在`stopRequested`变量上执行写操作时会出现问题。无法保证`stopRequested`变量（从主线程）中的值更改对于我们创建的`backgroundThread`可见。由于从main方法到`stopRequested`变量为`true`的写操作对`backgroundThread`不可见，因此它将进入无限循环。

当主线程和我们的` backgroundThread`在处理器内部的两个不同内核上运行时，`stopRequested`将被加载到执行`backgroundThread`的内核的缓存中。主线程将`stopRequested`值的更新值保留在其他内核的缓存中。由于现在`stopRequested`值位于两个不同的缓存中，因此`backgroundThread`可能看不到更新的值。

为了避免这些类型的内存不一致问题，Java引入了happens-before关系。

## **Happens-before relationship**

Java对happens-before关系的定义如下所示：

> 可以通过happens-before关系来排序两个动作。如果一个动作发生在另一个动作之前，则第一个动作对第二个动作可见，并在第二个动作之前排序。

据此，如果在写操作和读操作之间存在happends-before关系，则可以保证一个线程的写结果对于另一线程的读取是可见的。因此，如果我们能够在动作之间建立happends-before关系，我们将能够保持内存的一致性。

## **Synchronizing**

`synchronized`关键字被广泛用于实现线程之间的互斥。这意味着使用`synchronized`关键字，我们可以将特定代码块或方法的访问限制为仅一个线程。单个锁在期望访问特定同步块或方法的线程之间传递。每个线程将等待，直到另一个线程完成同步块(或同步方法)的执行并释放锁。

但是，同步还有另一个重要用途。它也可以用于实现代码块或方法之间的happens-before关系。如果有两个具有相同锁定的同步块/方法，则在同步块/方法内部的动作之间存在happens-before关系。这是由于以下事实：对象锁的解锁（退出同步块/方法）发生在随后的每次获取同一对象锁之前。

让我们更改初始代码，使其包含用于`stopRequested`变量的读写操作的同步方法

```java
public class StopThread {
        private static boolean stopRequested;
        private static synchronized void requestStop() {
            stopRequested = true;
        }
        private static synchronized boolean stopRequested() {
            return stopRequested;
        }
        public static void main(String[] args)
                throws InterruptedException {
            Thread backgroundThread = new Thread(new Runnable() {
                public void run() {
                    int i = 0;
                    while (!stopRequested())
                        i++;
                }
            });
            backgroundThread.start();
            TimeUnit.SECONDS.sleep(1);
            requestStop();
        }
    }
```

由于现在同步了对`stopRequested`变量的读写操作，因此在`stopRequested`变量的读/写操作之间建立了happens-before关系，从而可以查看所有线程。重要的是，要注意读取和写入操作都需要同步才能实现happens-before的关系。

对于与我们的示例类似的情况，仅使用`synchronized`关键字具有可见性可能不是最佳解决方案。由于线程在获取锁时被阻塞，因此同步会对性能产生影响。因此，当需要互斥（一次仅允许一个线程访问给定资源）时，`synchronized`关键字更为合适。

对于只需要可见性的情况，Java引入了一个简单的新关键字，称为` volatile`。

## **Volatile Fields**(易失字段)

> 在每次后续读取同一字段之前，都会对易失字段进行写操作。

我们可以使用`volatile`关键字使`stopRequested`变量成为一个易失字段，从而与`stopRequested`的写和读操作建立happens-before关系。

```java
public class StopThread {
        private static volatile boolean stopRequested;
        public static void main(String[] args)
                throws InterruptedException {
            Thread backgroundThread = new Thread(new Runnable() {
                public void run() {
                    int i = 0;
                    while (!stopRequested)
                        i++;
                }
            });
            backgroundThread.start();
            TimeUnit.SECONDS.sleep(1);
            stopRequested = true;
        }
    }
```

但是，请务必注意，volatile关键字不能替代同步块/方法。仅当使用happens-before关系实现共享变量的可见性时，此选项才有用。当我们需要在线程之间实现互斥时，我们仍然必须使用同步。

考虑下面的序列号生成器示例

```java
private static int nextSerialNumber = 0;

public static int generateSerialNumber() { 
    return nextSerialNumber++;
}
```

由于缺少增量运算符（++）中的原子性（不会同时发生所有读取-修改-写入操作），因此上述代码不是线程安全的。执行以下行时，各种线程可能以不同的状态（读或写）结束。

```java
return nextSerialNumber++;
```

但是，使`nextSerialNumber`易变将不会在增量操作期间实现互斥（因为volatile关键字只能用于实现可见性）。一个适当的解决方法是使`generateSerialNumber()`方法同步。

除了同步(synchronization)和易变性(volatility)，Java还为happens-before关系定义了几套规则。您可以从[Oracle文档](https://docs.oracle.com/javase/specs/jls/se8/html/jls-17.html#jls-17.4.5)中详细找到它们。

## References

[1] Effective java (2nd edition) by joshua bloch

[2] https://docs.oracle.com/javase/specs/jls/se8/html/jls-17.html#jls-17.4.5

[3] https://wiki.sei.cmu.edu/confluence/display/java/Concurrency%2C+Visibility%2C+and+Memory

[4] http://jeremymanson.blogspot.com/2007/08/atomicity-visibility-and-ordering.html


[processor_arch]: ./art/processor_arch.jpg