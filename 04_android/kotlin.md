# Kotlin

## ① 用过协程吗？讲讲原理

协程就是一个线程框架。跟Java的Executor; Android的Handler、AsyncTask一样。性能不分上下，但它的优点是写法简单。可以轻松的写出并发的代码。用同步的写法，写出异步的代码。

suspend关键字的作用: 提醒。提醒调用者，我这个函数需要在协程或另一个挂起函数中被调用。

## ② kotlin的空安全是怎么回事？如何判空的？使用kotlin一定不会出现NPE吗？

kotlin将每种类类型定义了两种类型，一种可空类型，一种非可空类型。两种是不同的类型。在对可空类型进行操作的时候，需要对它的空进行额外的判断。可以使用`?.`操作符进行安全调用。非可空类型相当于可空类型的一个子集。非可控类型可以直接赋值给可空类型；反之不行。

以下几种情况会出现NPE:
- 1 显式调用throw NullPointerException()
- 2 使用了`!!`的对象是空对象
- 3 有些数据在初始化时不一致
- 4 外部Java代码导致(Kotlin没有对Java类型进行判空处理)

## ③ 高阶函数

将函数用作参数或返回值的函数。

## ④ compaion是个啥

内联对象

## kotlin如何快速创建单例

object类

## 异常处理

将CoroutineExceptionHandler传入lanuch。

```kotlin
val handler = CoroutinesExceptionHandler{ _, e -> println(e) }
GlobalScope.launch(handler){
    // do sth.
}
```

CoroutineExceptionHandler间接继承CoroutinesContext。

类似Thread.uncatchedExceptionHandler;

## 接口

Kotlin的接口可以有包含实现的函数。

### 被实现(继承)多个接口有相同的方法签名如何调用？

```kotlin
interface A {
    fun work(){
        println("A work.")
    }
}

interface B {
    fun work(){
        println("B work.")
    }
}

class C : A, B{
    override fun work(){
        super<A>.work()// 调用A的work函数
        super<B>.work()// 调用B的work函数
    }
}
```