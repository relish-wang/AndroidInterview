# kotlin

## [协程](./coroutines.md)

## 特殊类

### 密封类(sealed class)
密封类的所有直接子类在编译期就被唯一确定
密封类的子类可以拥有多个实例
密封类和它的直接子类必须声明在同一个package下
密封类本身是abstract的，必须通过子类来实例化
密封类的构造器只能是protect或者private

### 内联类(value class) 

有且仅有一个包含单个基本类型参数的构造器
内联类可以有成员和方法，但没有字面量（也就是在堆中无法分配内存），只能对构造器中的参数做一些简单处理
内联类可以实现接口，但不能继承其他类，也不能被其他类继承。


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

### 增量编译

[《kotlin 编译 慢 Android studio kotlin编译过程》][kotlin编译]

[kotlin编译] :https://blog.51cto.com/u_12192/7715467