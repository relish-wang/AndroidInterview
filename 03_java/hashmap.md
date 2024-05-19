# HashMap相关面试题

## ① 阐述HashMap的原理

**HashMap的基本组成**

HashMap的主干是一个Node数组(Node实现了Map.Entry接口)。Node是HashMap的基本组成单元，每一个Node包含一个key-value键值对。

>简单来说，HashMap由数组+链表+红黑树组成的，数组是HashMap的主体，链表和红黑树则是主要为了解决哈希冲突而存在的。当链表上的节点数量大于8时会转化为红黑树；当红黑树的节点数量小于6时会转化为链表。

注: *JDK 1.8对HashMap进行了比较大的优化，底层实现由之前的“数组+链表”改为“数组+链表+[红黑树](https://github.com/julycoding/The-Art-Of-Programming-By-July/blob/master/ebook/zh/03.01.md)”*

*拓展:*[为什么HashMap中链表长度超过8会转换成红黑树](https://www.cnblogs.com/rgever/p/9643872.html)

**get()方法**

get(key)方法时获取key的hash值，计算hash&(n-1)得到在数组中的位置first=tab[hash&(n-1)],先判断first的key是否与参数key相等，不等就遍历后面的链表/树找到相同的key值返回对应的Value值即可

**put()方法**

- 1 判断键值对数组tab[]是否为空或为null，否则以默认大小resize()；
- 2 根据键值key计算hash值得到插入的数组索引i，如果tab[i]==null，直接新建节点添加，否则转入
- 3 判断当前数组中处理hash冲突的方式为链表还是红黑树(check第一个节点类型即可),分别处理

**HashMap的构造器和扩容策略**

HashMap有4个构造器，其他构造器如果用户没有传入initialCapacity(**初始容量**)和loadFactor(**扩容因子**)这两个参数，会使用默认值:*initialCapacity*默认为`16`(上限为2<sup>30</sup>，即`1073741824`<sub>这个数字再次翻倍将达到`Integer.MAX_VALUE + 1`,超出`Integer`的最大值</sub>)，*loadFactory*默认为`0.75`。

**扩容因子**：代表容器里有效数据和容量的比例。扩容因子越大，则空间利用率越高，查询效率越低；反之，空间利用率越低，查询效率越高。

注: *为什么需要使用加载因子，为什么需要扩容呢？因为如果填充比很大，说明利用的空间很多，如果一直不进行扩容的话，链表就会越来越长，这样查找的效率很低，因为链表的长度很大（当然最新版本使用了红黑树后会改进很多），扩容之后，将原来链表数组的每一个链表分成奇偶两个子链表分别挂在新链表数组的散列位置，这样就减少了每个链表的长度，提高查找效率。*

查看源码中HashMap的`resize`方法可得到以下结论:

- 1 HashMap的最大容量为2<sup>30</sup>
- 2 当容量不足时, 容量会增长为原来的一倍。

```java
final Node<K,V>[] resize() {
    Node<K,V>[] oldTab = table;
    int oldCap = (oldTab == null) ? 0 : oldTab.length;
    int oldThr = threshold;
    int newCap, newThr = 0;
    if (oldCap > 0) {
        if (oldCap >= MAXIMUM_CAPACITY) {
            threshold = Integer.MAX_VALUE;
            return oldTab;
        }
        else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
                 oldCap >= DEFAULT_INITIAL_CAPACITY)
            newThr = oldThr << 1; // double threshold
    }
    else if (oldThr > 0) // initial capacity was placed in threshold
        newCap = oldThr;
    else {               // zero initial threshold signifies using defaults
        newCap = DEFAULT_INITIAL_CAPACITY;
        newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
    }
    // ...省略部分代码...
    return newTab;
}
```


## ② HashMap和HashTable有何区别

- HashMap允许key和value为null，Hashtable不允许。
- HashMap的默认初始容量为16，Hashtable为11。
- HashMap的扩容为原来的2倍，Hashtable的扩容为原来的2倍加1。
- HashMap是非线程安全的，Hashtable是线程安全的。
- HashMap的hash值重新计算过，Hashtable直接使用hashCode。
- HashMap去掉了Hashtable中的contains方法。
- HashMap继承自AbstractMap类，Hashtable继承自Dictionary类。

## ③ 那么在线程安全场景下应该使用什么数据结构？

Hashtable和ConcurrentHashMap。

## ④ Hashtable和ConcurrentHashMap是如何实现线程安全的？为什么推荐使用ConcurrentHashMap而不是Hashtable？

**Hashtable线程安全的实现**

在Hashtable的源码里，put,get,remove等方法都加了 synchronized 关键字。

**ConcurrentHashMap线程安全的实现**

ConcurrentHashMap采用锁分段技术。首先将数据分成一段一段的存储，然后给每一段数据配一把锁，当一个线程占用锁访问其中一个段数据时，其他段的数据也能被其他线程访问。

**为什么推荐使用ConcurrentHashMap**

线程竞争激烈的情况下，HashTable的效率非常低下。因为当一个线程访问HashTable的同步方法时，其他线程访问HashTable的同步方法时，可能会进入阻塞或轮询状态。而ConcurrentHashMap采用锁分段技术，当多线程访问容器里不同数据段的数据时，线程间就不会存在锁竞争，从而可以有效的提高并发访问效率。


## ⑤ LinkedHashMap

继承自HashMap。双向链表。线程不安全。(面试官很容易延伸到LRUCache, 然后再让你自己实现一个图片加载框架。)

## ⑥ 为什么重写equals方法时一般都会重写hashcode方法？

equals: 为了方便比较两个对象内容是否相等

hashcode: 用于返回调用该方法的对象的散列码值，此方法将返回整数形式的散列码值。

基于散列法的集合需要使用 hashCode()方法返回的散列码值存储和管理元素，例如Hashtable、HashMap和HashSet等，在使用这些集合时，首先会根据元素对象的散列码值确定其存储位置，然后再根据equals()方法结果判断元素对象是否已存在，最后根据判断结果执行不同处理。

## ⑦ 如何处理hash冲突
**(1)开放寻址法(open addressing)**

从原来的位置向下查找第一个空的插槽。

优点: 简单

缺点: 容易聚集

优化1: "下一个"改为"下三个"或"下n个", 需要注意的是这个n必须最终能够访问表中的所有元素, 为确保这一点，建议将表的大小设置为质数

优化2: 用平方数代替恒定的跳跃值。h+1, h+4, h+9, h+16,以此类推.rehash(pos)=(h+i<sup>2</sup>)

```java
let hash(x) be the slot index computed using hash function.  
If slot hash(x) % S is full, then we try (hash(x) + 1*1) % S
If (hash(x) + 1*1) % S is also full, then we try (hash(x) + 2*2) % S
If (hash(x) + 2*2) % S is also full, then we try (hash(x) + 3*3) % S
```

**(2)链地址法**

这也是Java的HashMap使用的方法。

当出现Hash冲突时, 将在冲突位置形成一条链表。

**(3)再哈希法**

决定位置的因素: key+hash次数

当出现hash冲突的时候, 将hash次数+1, 再次hash(计算位置)

**(4)公共溢出区域法**

维护两张表。一张主表，一张溢出表。未冲突的数据放置在主表, 溢出的数据按顺序放在溢出表。取值时，判断hash出来的位置和主表中对应位置的key是否相等，相等直接取; 不等的话线性遍历溢出表。

优点: 当冲突数据少的时候, 查找效率很高。

## ⑧ 为什么链表转红黑树是8？
链表的平均查找长度是n/2=8/2=4;红黑树的平均查找长度是log<sub>2</sub>n=log<sub>2</sub>8=3; 刚好在8的时候红黑树的查找效率优于链表。
## ⑨ 为什么红黑树转链表是6？
- 留了个7为了防止链表和树的频繁创建
- 红黑树的平均查找长度是log(n)，如果长度为8，平均查找长度为log(8)=3，链表的平均查找长度为n/2，当长度为8时，平均查找长度为8/2=4，这才有转换成树的必要；链表长度如果是小于等于6，6/2=3，而log(6)=2.6，虽然速度也很快的，但是转化为树结构和生成树的时间并不会太短。
      