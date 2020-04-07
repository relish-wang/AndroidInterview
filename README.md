# Android面试题整理

对近几年遇到/看到的面试题做了些整理。有些给了详细的解答(如有错误, 欢迎指正); 有些做了自己的思路说明(有其他思路的, 也可与我交流分享); 还有些懒得写答案了, 随缘更新(看不惯的, 你打我呀)。

[TOC]

## 〇、 操作系统

### 线程与进程的区别

**线程是CPU调度的最小单位。**

**进程是资源分配的最小单位**。

只是两种粒度不同的说法而已。

一个进程可以包含多个线程。

## 一、计算机网络

### 1 TCP三次握手

#### ① 三次握手的过程

刚开始客户端处于 closed 的状态，服务端处于 listen 状态。然后
     1、第一次握手：客户端给服务端发一个 SYN 报文，并指明客户端的初始化序列号 ISN（c）。此时客户端处于 SYN_Send 状态。
     2、第二次握手：服务器收到客户端的 SYN 报文之后，会以自己的 SYN 报文作为应答，并且也是指定了自己的初始化序列号 ISN(s)，同时会把客户端的 ISN + 1 作为 ACK 的值，表示自己已经收到了客户端的 SYN，此时服务器处于 SYN_REVD 的状态。
     3、第三次握手：客户端收到 SYN 报文之后，会发送一个 ACK 报文，当然，也是一样把服务器的 ISN + 1 作为 ACK 的值，表示已经收到了服务端的 SYN 报文，此时客户端处于 establised 状态。
     4、服务器收到 ACK 报文之后，也处于 establised 状态，此时，双方已建立起了链接。

![][3handshake]

#### ② 为啥要三次握手，两次行不行？

第一次握手：客户端发送网络包，服务端收到了。这样服务端就能得出结论：客户端的发送能力、服务端的接收能力是正常的。
第二次握手：服务端发包，客户端收到了。这样客户端就能得出结论：服务端的接收、发送能力，客户端的接收、发送能力是正常的。不过此时服务器并不能确认客户端的接收能力是否正常。
第三次握手：客户端发包，服务端收到了。这样服务端就能得出结论：客户端的接收、发送能力正常，服务器自己的发送、接收能力也正常。

简而言之，少于三次无法确认双方的收发能力是否正常。

#### ③ 三次握手的作用

   1、确认双方的接受能力、发送能力是否正常。
   2、指定自己的初始化序列号(ISN)，为后面的可靠传输做准备。
   3、如果是 https 协议的话，三次握手这个过程，还会进行数字证书的验证以及加密密钥的生成

#### ④（ISN）是固定的吗？
三次握手的一个重要功能是客户端和服务端交换ISN(Initial Sequence Number), 以便让对方知道接下来接收数据的时候如何按序列号组装数据。**如果ISN是固定的，攻击者很容易猜出后续的确认号，因此 ISN 是动态生成的。**

#### ⑤ 三次握手过程中可以携带数据吗？

第一次、第二次握手不可以携带数据，而第三次握手是可以携带数据的。因为此时客户端已经验证了服务端的收发能力，也知道自己的收发能力没问题。

RFC793文档里说到，带有SYN标志的过程包是不可以携带数据的，也就是说三次握手的前两次是不可以携带数据的（逻辑上看，连接还没建立，携带数据好像也有点说不过去）。重点就是第三次握手可不可以携带数据。RFC793文档里有一句话：

> Data or controls which were queued for transmission may be included.

### 2 TCP四次挥手

#### ① 四次挥手的过程

刚开始双方都处于 establised 状态，假如是客户端先发起关闭请求，则：
1、第一次挥手：客户端发送一个 FIN 报文，报文中会指定一个序列号。此时客户端处于FIN_WAIT1状态。    
2、第二次挥手：服务端收到 FIN 之后，会发送 ACK 报文，且把客户端的序列号值 + 1 作为 ACK 报文的序列号值，表明已经收到客户端的报文了，此时服务端处于 CLOSE_WAIT状态。客户端接收到报文之后，进入FIN_WAIT2状态
3、第三次挥手：如果服务端也想断开连接了，和客户端的第一次挥手一样，发给 FIN 报文，且指定一个序列号。此时服务端处于 LAST_ACK 的状态。
4、第四次挥手：客户端收到 FIN 之后，一样发送一个 ACK 报文作为应答，且把服务端的序列号值 + 1 作为自己 ACK 报文的序列号值，此时客户端处于 TIME_WAIT 状态。需要过一阵子以确保服务端收到自己的 ACK 报文之后才会进入 CLOSED 状态
5、服务端收到 ACK 报文之后，就关闭连接了，处于 CLOSED 状态。

![][4handbye]

#### ② 为什么客户端发送 ACK 之后不直接关闭，而是要等一阵子才关闭？

这其中的原因就是，要确保服务器是否已经收到了我们的 ACK 报文，如果没有收到的话，服务器会重新发 FIN 报文给客户端，客户端再次收到 FIN 报文之后，就知道之前的 ACK 报文丢失了，然后再次发送 ACK 报文。至于 TIME_WAIT 持续的时间至少是一个报文的来回时间。一般会设置一个计时，如果过了这个计时没有再次收到 FIN 报文，则代表对方成功收到 ACK 报文，此时处于 CLOSED 状态。

#### ③ 为什么握手是三次，挥手是四次？

如果类比三次握手，在第二次挥手的时候直接发FIN + ACK明显不合理，因为被动方可能没有数据发送完，你这么关太草率了，所以需要四次。

### 3 HTTP和HTTPS

#### ① http和https有什么区别

HTTPS协议="SSL/TLS+HTTP协议"构建的可进行加密传输、身份认证的网络协议，是HTTP的安全版。

| 区别         | HTTP             | HTTPS                                                        |
| ------------ | ---------------- | ------------------------------------------------------------ |
| **工作层**   | 应用层           | 传输层                                                       |
| **标准端口** | 80               | 443                                                          |
| **传输方式** | 明文传输         | SSL加密传输                                                  |
| **工作耗时** | TCP握手          | TCP握手+SSL握手。<br>耗时点: <br>1.协议交互所增加的网络 RTT(round trip time, 往返时延)。<br/>2.加解密相关的计算耗时。<br />3. 页面加载时间增加50%, 耗电增加10~20% |
| **费用**     | 0                | 需要到CA申请证书，一般免费证书较少，需要一定费用             |
| **安全性**   | 连接简单，无状态 | 可进行加密传输、身份认证的网络协议，比HTTP协议安全。         |

**HTTPS的优点**

尽管HTTPS并非绝对安全，掌握根证书的机构、掌握加密算法的组织同样可以进行中间人形式的攻击，但HTTPS仍是现行架构下最安全的解决方案，主要有以下几个有点：

- 使用HTTPS协议可认证用户和服务器，确保数据发送到正确的客户机和服务器；
- HTTPS是现行架构下最安全的解决方案，虽然不是绝对安全，但它大幅增加了中间人攻击的成本。
- 谷歌曾在2014年8月份调整搜索引擎算法，并称“比起同等HTTP网站，采用HTTPS加密的网站在搜索结果中的排名将会更高”。

**HTTP是无状态协议？**

(1)、无状态协议对于事务处理没有记忆能力。缺少状态意味着如果后续处理需要前面的信息，则它必须重传，这样可能导致每次连接传送的数据量增大。
(2)、无状态协议解决办法： 通过1、Cookie 2、通过Session会话保存。

HTTPS是有状态的(TLS/SSL)。Http/2有一些有状态组件，但对于应用层来说也是无状态的。http/1是没有状态。

Cookie和其他有状态机制是后来在单独的RFC中定义的添加内容。它们不是原始[ HTTP / 1.0 ](http://www.it1352.com/"https://tools.ietf.org/search/rfc1945/")规范的一部分，并且未在 HTTP 1.1 RFC 。 HTTP 1被认为是无状态的，尽管在实践中我们使用标准化的有状态机制。 HTTP / 2在其标准中定义了有状态组件，因此是有状态的。特定的HTTP / 2应用程序可以使用HTTP / 2功能的子集来维护无状态。

#### ② http协议报文中有哪些header是必须的，分别有什么作用？

**Connection**

`close`: 表示无论客户端还是服务端都想要关闭连接。HTTP/1.0请求的默认配置。

`keep-alive`: 表示客户端想要保持连接打开。HTTP / 1.1请求的默认设置为具有持久连接。标头列表是要由它们之间的第一个非透明代理或高速缓存删除的标头的名称：这些标头定义了发射器与第一个实体（而不是目标节点）之间的连接。

| header           | 说明                  | 归属 |
| ----------- | ------------------ | |
| User- Agent | 浏览器信息 | request |
| Content-Length | Body字节大小 | entity |
| Accept-Encoding | 可接受的文本压缩算法(gzip, deflate) | request |
| Cookie | 会话状态管理(4kb、键值对) | request |
| Host | 请求主机器名(1.1后强制使用)<br>Host: \<host>:\<port>   port可选<br>如果一个 HTTP/1.1 请求缺少 Host 头字段或者设置了超过一个的 Host 头字段，[`400`](https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Status/400)（Bad Request）状态码会被返回 | request |
|Connection | `close`: 表示无论客户端还是服务端都想要关闭连接。HTTP/1.0请求的默认配置。<br>`keep-alive`: 表示客户端想要保持连接打开。HTTP / 1.1请求的默认设置为具有持久连接。<br>当前的事务完成后，是否会关闭网络连接 | Genaral |
| Content-Type | 告诉客户端实际返回的内容的内容类型 | entity |

**Host**

域名

- User-Agaent、Content-Length、Host等

**Cookie**

  - 作用
    - 会话状态管理（如用户登录状态、购物车、游戏分数或其它需要记录的信息）
    - 个性化设置（如用户自定义设置、主题等）
    - 浏览器行为跟踪（如跟踪分析用户行为等）
    
  - 容量: 4kb

  - 格式: 键值对。等号(=)间隔键值, 分号(;)间隔对。可以没有值。

  - 只允许https使用的cookie:  `__Secure-` 或 `__Host-`开头的 

#### ③ 介绍几个对称加密和非对称加密算法

对称加密: 加密和解密使用相同的密钥。如:DES、AES
非对称加密: 公开密钥（publickey）和私有密钥（privatekey）如: RSA
Hash算法: MD5、SHA

## 二、数据结构

### 1 堆排序的实现

完成二叉树

大根堆：父节点的值大于等于左右子节点值

小根堆：父节点的值小于等于左右子节点值

(以大根堆为例)先构建最大堆：

然后对二叉树进行遍历，把根节点从树上取下来。然后比较左右子树节点，较大的那一边的节点成为二叉树的根节点，剩下的重新构建最大堆。依次操作，最初取节点的顺序所对应的值就是从大到小排列的。

### 2 二叉树

#### ① 在二叉树中, 已知两个节点的, 如何找到这两个节点的最小公共父节点

情况一：root未知，但是每个节点都有parent指针
此时可以分别从两个节点开始，沿着parent指针走向根节点，得到两个链表，然后求两个链表的第一个公共节点，这个方法很简单，不需要详细解释的。



情况二：节点只有左、右指针，没有parent指针，root已知
思路：有两种情况，一是要找的这两个节点（a, b），在要遍历的节点（root）的两侧，那么这个节点就是这两个节点的最近公共父节点；
二是两个节点在同一侧，则 root->left 或者 root->right 为 NULL，另一边返回a或者b。那么另一边返回的就是他们的最小公共父节点。
递归有两个出口，一是没有找到a或者b，则返回NULL；二是只要碰到a或者b，就立刻返回。


分两种情况:

1 根节点(root)未知, 节点包含parent指针。

此时可以分别从两个节点开始，沿着parent指针走向根节点，得到两个链表，然后求两个链表的第一个公共节点。

2 根节点(root)已知, 节点仅包含左右节点指针。

有两种情况，一是要找的这两个节点（a, b），在要遍历的节点（root）的两侧，那么这个节点就是这两个节点的最近公共父节点；
二是两个节点在同一侧，则 root->left 或者 root->right 为 NULL，另一边返回a或者b。那么另一边返回的就是他们的最小公共父节点。
递归有两个出口，一是没有找到a或者b，则返回NULL；二是只要碰到a或者b，就立刻返回。

## 三、Java

### 1 final关键字修饰方法、类、字段有什么区别
**类:** 不能被继承(比如: String)

**方法:** 不能被重写

**字段:** 值不能被修改(属性能被修改)

### 2 多线程

#### ① 线程死锁的原因和解决办法

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

#### ② wait和sleep有什么区别

**sleep不会释放对象锁。**sleep()方法是线程类（Thread）的静态方法，导致此线程暂停执行指定时间，将执行机会给其他线程，但是监控状态依然保持，到时后会自动恢复（线程回到就绪（ready）状态），因为调用 sleep 不会释放对象锁。

**wait会释放对象锁。**wait() 是 Object 类的方法，对此对象调用 wait()方法导致本线程放弃对象锁(线程暂停执行)，进入等待此对象的等待锁定池，只有针对此对象发出 notify 方法（或 notifyAll）后本线程才进入对象锁定池准备获得对象锁进入就绪状态。

#### ③ volatile关键字的原理

> A Java(TM) programming language keyword used in variable declarations that specifies that the variable is modified asynchronously by concurrently running threads.

使用volatile变量可降低内存一致性错误的风险，因为对volatile变量的任何写入都会与该变量的后续读取建立happens-before关系。这意味着对volatile变量的更改始终对其他线程可见。而且，这还意味着，当线程读取一个volatile变量时，它不仅会看到对volatile的最新更改，还会看到导致更改的代码的副作用。

- 能保证原子性吗 

  否

- 能保证线程安全吗 

  否。必须同时满足下面两个条件才能保证在并发环境的线程安全：
      

（1）对变量的写操作不依赖于当前值。
（2）该变量没有包含在具有其他变量的不变式中。
      
#### ④ synchronized关键字原理，修饰在不同的地方(代码块，静态方法，对象方法)分别是锁什么东西?

// TODO 

### 3 线程池

#### ① 线程池的构造方法的几个参数, 含义作用

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


#### ② ThreadLocal用过吗？讲讲原理
- ThreadLocal提供了线程的局部变量，每个线程都可以通过`set()`和`get()`来对这个局部变量进行操作，但不会和其他线程的局部变量进行冲突，**实现了线程的数据隔离**～。
- key为当前ThreadLocal对象，value为泛型。
- ThreadLocal内存泄漏的根源:
**由于ThreadLocalMap的生命周期跟Thread一样长，如果没有手动删除对应key就会导致内存泄漏，而不是因为弱引用**。
### 4 HashMap

#### ① 阐述HashMap的原理

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

HashMap有4个构造器，其他构造器如果用户没有传入initialCapacity(**初始容量**)和loadFactor(**扩容因子**)这两个参数，会使用默认值:*initialCapacity*默认为`16`(上限为`2^30`，即`Integer.MAX_VALUE`)，*loadFactory*默认为`0.75`。

**扩容因子**：代表容器里有效数据和容量的比例。扩容因子越大，则空间利用率越高，查询效率越低；反之，空间利用率越低，查询效率越高。

注: *为什么需要使用加载因子，为什么需要扩容呢？因为如果填充比很大，说明利用的空间很多，如果一直不进行扩容的话，链表就会越来越长，这样查找的效率很低，因为链表的长度很大（当然最新版本使用了红黑树后会改进很多），扩容之后，将原来链表数组的每一个链表分成奇偶两个子链表分别挂在新链表数组的散列位置，这样就减少了每个链表的长度，提高查找效率。*

查看源码中HashMap的`resize`方法可得到以下结论:

- 1 HashMap的最大容量为Integer.MAX_VALUE
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


#### ② HashMap和HashTable有何区别

- HashMap允许key和value为null，Hashtable不允许。
- HashMap的默认初始容量为16，Hashtable为11。
- HashMap的扩容为原来的2倍，Hashtable的扩容为原来的2倍加1。
- HashMap是非线程安全的，Hashtable是线程安全的。
- HashMap的hash值重新计算过，Hashtable直接使用hashCode。
- HashMap去掉了Hashtable中的contains方法。
- HashMap继承自AbstractMap类，Hashtable继承自Dictionary类。

#### ③ 那么在线程安全场景下应该使用什么数据结构？

Hashtable和ConcurrentHashMap。

#### ④ Hashtable和ConcurrentHashMap是如何实现线程安全的？为什么推荐使用ConcurrentHashMap而不是Hashtable？

**Hashtable线程安全的实现**

在Hashtable的源码里，put,get,remove等方法都加了 synchronized 关键字。

**ConcurrentHashMap线程安全的实现**

ConcurrentHashMap采用锁分段技术。首先将数据分成一段一段的存储，然后给每一段数据配一把锁，当一个线程占用锁访问其中一个段数据时，其他段的数据也能被其他线程访问。

**为什么推荐使用ConcurrentHashMap**

线程竞争激烈的情况下，HashTable的效率非常低下。因为当一个线程访问HashTable的同步方法时，其他线程访问HashTable的同步方法时，可能会进入阻塞或轮询状态。而ConcurrentHashMap采用锁分段技术，当多线程访问容器里不同数据段的数据时，线程间就不会存在锁竞争，从而可以有效的提高并发访问效率。


#### ⑤ LinkedHashMap

继承自HashMap。双向链表。线程不安全。(面试官很容易延伸到LRUCache, 然后再让你自己实现一个图片加载框架。)

#### ⑥ 为什么重写equals方法时一般都会重写hashcode方法？

equals: 为了方便比较两个对象内容是否相等

hashcode: 用于返回调用该方法的对象的散列码值，此方法将返回整数形式的散列码值。

基于散列法的集合需要使用 hashCode()方法返回的散列码值存储和管理元素，例如Hashtable、HashMap和HashSet等，在使用这些集合时，首先会根据元素对象的散列码值确定其存储位置，然后再根据equals()方法结果判断元素对象是否已存在，最后根据判断结果执行不同处理。

#### ⑦ 如何处理hash冲突
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

#### ⑧ 为什么链表转红黑树是8？
链表的平均查找长度是n/2=8/2=4;红黑树的平均查找长度是log<sub>2</sub>n=log<sub>2</sub>8=3; 刚好在8的时候红黑树的查找效率优于链表。
####  ⑨ 为什么红黑树转链表是6？
- 留了个7为了防止链表和树的频繁创建
- 红黑树的平均查找长度是log(n)，如果长度为8，平均查找长度为log(8)=3，链表的平均查找长度为n/2，当长度为8时，平均查找长度为8/2=4，这才有转换成树的必要；链表长度如果是小于等于6，6/2=3，而log(6)=2.6，虽然速度也很快的，但是转化为树结构和生成树的时间并不会太短。
      
### 5 JVM(Java虚拟机)和JMM(Java内存模型)

#### ① gc触发的时机, 回收优先级(哪些对象会优先回收)

- Minor GC触发条件：当Eden区满时，触发Minor GC。
  
- Full GC触发条件：

（1）调用System.gc时，系统建议执行Full GC，但是不必然执行

（2）老年代空间不足
（3）方法区空间不足
（4）通过Minor GC后进入老年代的平均大小大于老年代的可用内存

（5）由Eden区、From Space区向To Space区复制时，对象大小大于To Space可用内存，则把该对象转存到老年代，且老年代的可用内存小于该对象大小

#### ② 弱引用和软引用的区别

**没有强引用指向弱引用的指向的对象时，弱引用就会被回收。**即WeakReference不改变原有的强引用对象的垃圾回收机制。一旦其指示对象没有任何强引用对象时，此对象即进入正常的垃圾回收流程。

而软引用需要在**没有强引用指向弱引用的指向的对象**且**内存不足**时才会回收它指向的对象。

#### ③ 为什么会出现内存抖动？如何处理(注意是处理不是预防)

- 定位: android profile|Tools->Android->Android Device Monitor
- LeakCanary
- MAT

### 6 NIO和BIO的区别

NIO只需要开启**一个线程**就可以处理来自**多个客户端**的IO事件

### 7 JVM定义了几种线程的状态

NEW、RUNNABLE、BLOCKED、WAITING、TIMED_WAITING、TERMINATED

```
public enum State {
    /**
     * Thread state for a thread which has not yet started.
     */
    NEW,

    /**
     * Thread state for a runnable thread.  A thread in the runnable
     * state is executing in the Java virtual machine but it may
     * be waiting for other resources from the operating system
     * such as processor.
     */
    RUNNABLE,

    /**
     * Thread state for a thread blocked waiting for a monitor lock.
     * A thread in the blocked state is waiting for a monitor lock
     * to enter a synchronized block/method or
     * reenter a synchronized block/method after calling
     * {@link Object#wait() Object.wait}.
     */
    BLOCKED,

    /**
     * Thread state for a waiting thread.
     * A thread is in the waiting state due to calling one of the
     * following methods:
     * <ul>
     *   <li>{@link Object#wait() Object.wait} with no timeout</li>
     *   <li>{@link #join() Thread.join} with no timeout</li>
     *   <li>{@link LockSupport#park() LockSupport.park}</li>
     * </ul>
     *
     * <p>A thread in the waiting state is waiting for another thread to
     * perform a particular action.
     *
     * For example, a thread that has called <tt>Object.wait()</tt>
     * on an object is waiting for another thread to call
     * <tt>Object.notify()</tt> or <tt>Object.notifyAll()</tt> on
     * that object. A thread that has called <tt>Thread.join()</tt>
     * is waiting for a specified thread to terminate.
     */
    WAITING,

    /**
     * Thread state for a waiting thread with a specified waiting time.
     * A thread is in the timed waiting state due to calling one of
     * the following methods with a specified positive waiting time:
     * <ul>
     *   <li>{@link #sleep Thread.sleep}</li>
     *   <li>{@link Object#wait(long) Object.wait} with timeout</li>
     *   <li>{@link #join(long) Thread.join} with timeout</li>
     *   <li>{@link LockSupport#parkNanos LockSupport.parkNanos}</li>
     *   <li>{@link LockSupport#parkUntil LockSupport.parkUntil}</li>
     * </ul>
     */
    TIMED_WAITING,

    /**
     * Thread state for a terminated thread.
     * The thread has completed execution.
     */
    TERMINATED;
}
```

## 三、Android

### 1 kotlin

#### ① 用过协程吗？讲讲原理

协程就是一个线程框架。跟Java的Executor; Android的Handler、AsyncTask一样。性能不分上下，但它的优点是写法简单。可以轻松的写出并发的代码。用同步的写法，写出异步的代码。

suspend关键字的作用: 提醒。提醒调用者，我这个函数需要在协程或另一个挂起函数中被调用。

#### ② kotlin如何判空的？

`?.`操作符

#### ③ 高阶函数


#### ④ compaion是个啥

内联对象


#### kotlin如何快速创建单例

object类

### 2 网络

#### ① retrofit动态代理不能代理抽象类 

不能，只能代理接口。

#### ② okhttp做了什么事



### 3 Android/data和data/data区别

私有目录。

### 4 Android有哪些创建多线程的方式
- IntentService是怎么关闭自己创建的线程的
- HandlerThread
- Handler/Looper/MessageQueue关系
	- 一个Looper可以有多个Handler吗？YES
	- 一个Handler可以发消息给另一个Handler处理吗？不能
	- Handler可以绑定多个Looper吗？不能

### 5 View绘制与分发

#### ① View的事件分发过程

触摸事件的分发自上而下分别是Activity->Window->View。顶级View一般是一个ViewGroup, 若事件一直未被处理则ViewGroup会下发给其下层的View直到下层View无Child为止;最底层的View也不处理事件的话，事件则会向上传递，直到传递到处理事件的View;若所有的View都不处理事件，那么事件最后会交由Activity处理。

- public boolean dispatchTouchEvent(MotionEvent ev)

  用来进行事件的分发。返回值表示是否消费当前事件。

- public boolean onInterceptTouchEvent(MotionEvent ev)

  是否拦截当前事件。

- public boolean onTouchEvent(MotionEvent ev)

  是否消费当前事件。(若不消费，在同一个事件序列中，当前View无法再次接收到事件)

以上三个方法的关系可以用以下伪代码来描述：

```java
public boolean dispatchTouchEvent(MotionEvent ev){
    boolean consume = false;
    if(onInterceptTouchEvent(ev)){
        consume = onToucheEvent(ev);
    } else {
        consume = child.dispatchTouchEvent(ev);
    }
    return consume;
}
```

#### ② onTouch、onTouchEvent和onClick的关系

当一个View需要处理事件时，且该View设置了OnTouchListener，那么onTouch被调用，若onTouch的返回值为false，则onTouchEvent会被调用; 返回true，则onTouchEvent不会被调用。即onTouch的优先级高于onTouchEvent。

在onTouchEvent中，如果View设置了OnClickListener，则会调用OnClick。故，OnClick事件的优先级低于onTouchEvent。

*注: 若希望深入了解View的触摸事件分发，建议阅读任玉刚的《Android开发艺术探索》的第3章3.4节。*

#### ③ 了解ACTION_CANCEL事件吗

**触发条件**

上层 View 回收事件处理权的时候，子View 才会收到一个 ACTION_CANCEL 事件。

举个例子:

> 上层 View 是一个 RecyclerView，它收到了一个 `ACTION_DOWN` 事件，由于这个可能是点击事件，所以它先传递给对应 ItemView，询问 ItemView 是否需要这个事件，然而接下来又传递过来了一个 `ACTION_MOVE`事件，且移动的方向和 RecyclerView 的可滑动方向一致，所以 RecyclerView 判断这个事件是滚动事件，于是要收回事件处理权，这时候对应的 ItemView 会收到一个 `ACTION_CANCEL` ，并且不会再收到后续事件。

#### ④ 简述绘制流程。垂直同步了解吗？16ms刷新了解吗

// TODO

#### ⑤ requestLayout()/invalidate()区别

// TODO

#### ⑥ 开启/关闭硬件加速对View绘制有何影响

// TODO

#### ⑦ 如何用Drawable优雅地实现自定义View的动画效果

// TODO

#### ⑧ 有没有遇到过页面卡顿？是什么原因造成的？最佳实践？如何排查发生的原因？

// TODO

#### ⑨ RecyclerView优化

- 1 `recyclerView.setHasFixedSize(true);`当Item的高度如是固定的，设置这个属性为true可以提高性能，尤其是当RecyclerView有条目插入、删除时性能提升更明显。

- 2 使用getExtraLayoutSpace为LayoutManager设置更多的预留空间

    ```java
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this) {
      @Override
        protected int getExtraLayoutSpace(RecyclerView.State state) {
          return 300;
        }
  };
  ```


### 6 Window

// TODO

### 7 跨进程通信

#### ①  Binder原理

// TODO

#### ② Messager

// TODO 

### 8 JSBridge的原理

// TODO

### 9 打包流程

#### ① 简述一下打包流程

- 混淆在哪一步
- R文件生成在哪一步,了解它的内容结构吗
- apk里的resuource.arsc有什么用

具体说来：

1. 通过AAPT工具进行资源文件（包括AndroidManifest.xml、布局文件、各种xml资源等）的打包，生成R.java文件。
2. 通过AIDL工具处理AIDL文件，生成相应的Java文件。
3. 通过Javac工具编译项目源码，生成Class文件。
4. 通过DX工具将所有的Class文件转换成DEX文件，该过程主要完成Java字节码转换成Dalvik字节码，压缩常量池以及清除冗余信息等工作。
5. 通过ApkBuilder工具将资源文件、DEX文件打包生成APK文件。
6. 利用KeyStore对生成的APK文件进行签名。
7. 如果是正式版的APK，还会利用ZipAlign工具进行对齐处理，对齐的过程就是将APK文件中所有的资源文件举例文件的起始距离都偏移4字节的整数倍，这样通过内存映射访问APK文件
    的速度会更快。

#### ② 如何快速优雅地打渠道包(applicationId一致, 仅资源文件不同, 答flavors的不得分)

// TODO 

### 10 Android安全

1 数据加密传输
2 https
3 代码混淆(proguard)
4 暴露的组件(四大组件)
android:exported=“false”
android:protectionLevel="signature"
5 使用WebView注意js注入
setJavaScriptEnabled(true)


### 11 开源框架

#### ① RxJava

知道哪些操作符,  举一个聚合操作符(zip)说说如何实现的？

#### ② okhttp+retrofit

框架做了些什么事情

- 允许连接到同一个主机地址的所有请求,提高请求效率

- 共享Socket,减少对服务器的请求次数

- 通过连接池,减少了请求延迟

- 缓存响应数据来减少重复的网络请求

- 减少了对数据流量的消耗

- 自动处理GZip压缩

#### ③ ARouter

如何实现路由？(建议从路由注册->路由调用两个方面讲，调用的时候是怎么一步一步找到路由注册的地方的)

#### ④ 热修复框架

热修复原理有哪些流派?各自的实现原理是什么？transform API用过吗？

// TODO 

#### ⑤ Glide

Glide的特点：

- 支持GIF动图

- 支持加载缩略图

- Activity生命周期的集成

- OkHttp和Volley的支持: 默认采用HttpUrlConnection作为网络协议栈

- 动画的支持：新增支持图片的淡入淡出动画效果

### 12 Android架构分为几层？(C/S架构)

6层。(自上而下)

- 应用框架层
- 进程通信层
- 系统服务层
- Android运行时层(Davlik/ART)
- 硬件抽象层
- Linux内核层
### 13 从手机桌面点击App到第一个Activity启动，发生了什么？

①点击桌面App图标，**Launcher**进程采用Binder IPC向**system_server**进程发起startActivity请求；

②system_server进程接收到请求后，向**zygote进程**发送创建进程的请求；

③Zygote进程**fork出新的子进程**，即App进程；

④**App进程**，通过Binder IPC向sytem_server进程发起**attachApplication请求**；

⑤system_server进程在收到请求后，进行一系列准备工作后，再通过binder IPC向App进程发送**scheduleLaunchActivity请求**；

⑥App进程的binder线程（ApplicationThread）在收到请求后，通过handler向主线程发送**LAUNCH_ACTIVITY消息**；

⑦主线程在收到Message后，通过反射机制创建目标Activity，并回调Activity.onCreate()等方法。

⑧到此，App便正式启动，开始进入Activity生命周期，执行完onCreate/onStart/onResume方法，UI渲染结束后便可以看到App的主界面。

### 14 如何做内存优化

#### ① 内存泄漏方面

- 错误的单例实现
- 内部类持有外部类的强引用
- 资源打开未关闭(Bitmap, IO流)

#### ② 内存抖动方面

- onDraw申请对象

- 循环体申请对象

- RecyclerView/ListView#Adapter申请对象

#### ③ 其他

- 加载大图避免OOM

- 动态广播注册与注销

- 使用SpareArray代替Map\<Integer, Object>(key不需要自动装箱;数据量小的情况下，随机访问效率更高)

- SharedPreferences性能优化:

  - 不要存放大key和大value。会引起界面卡，频繁GC，占用内存等。
  - 无关的配置项不要放在一起。文件大了读取效率也会降低(SP本质上是xml文件)

- 节制的使用Service，当启动一个Service时，系统总是倾向于保留这个Service依赖的进程，这样会造成系统资源的浪费，可以使用IntentService，执行完成任务后会自动停止。

- 当界面不可见时释放内存，可以重写Activity的onTrimMemory()方法，然后监听TRIM_MEMORY_UI_HIDDEN这个级别，这个级别说明用户离开了页面，可以考虑释放内存和资源。

- 避免在Bitmap浪费过多的内存，使用压缩过的图片，也可以使用Fresco等库来优化对Bitmap显示的管理。

- 使用优化过的数据集合SparseArray代替HashMap，HashMap为每个键值都提供一个对象入口，使用SparseArray可以免去基本对象类型转换为引用数据类想的时间。

#### ④ 排查

- Android Profiler

- LeakCanary

### 16 如何针对机型做自定义View的优化

合理使用warp_content，match_parent.
尽可能的是使用RelativeLayout
针对不同的机型，使用不同的布局文件放在对应的目录下，android会自动匹配。
尽量使用点9图片。
使用与密度无关的像素单位dp，sp
引入android的百分比布局。
切图的时候切大分辨率的图，应用到布局当中。在小分辨率的手机上也会有很好的显示效果。

### 15 ViewPager的三个Adapter要怎么选择使用?

PagerAdapter：当所要展示的视图比较简单时适用
FragmentPagerAdapter：当所要展示的视图是Fragment，并且数量比较少时适用
FragmentStatePagerAdapter：当所要展示的视图是Fragment，并且数量比较多时适用

## 四、手写代码

### 1 回文数判断(不能用字符串，不能申请额外空间)

如果一个数字正着和倒着都是一样的就称这个数字是回文数,如12321是回文数,个位与万位相同,十位与千位相同。 现在给出一个数判断是否是回文数。

输入描述

```
输入包括一行：一个非负整数n
```

输出描述

```
如果是回文数输出"This is a palindrome number!",如果不是回文数输出"This is not a palindrome number!"
```

示例1

## 输入

```
123
```

## 输出

```
This is not a palindrome number!
```

答:

```java
public boolean isPalindrome(int x) {
  int reverse = 0;
  int tmp = x;
  //数字反转操作
  while(tmp != 0){
    reverse = reverse * 10 + tmp % 10;
    tmp = tmp /10;
  }
  return reverse == x;
}
```




## 五、其他

### 1 玩一个游戏，54张牌，2个人玩，每人每次只能拿1~3张牌，你先拿，你会采取怎么样的策略？

第一次，我拿2张。

第二次你拿，第三次我拿。这一次算第一轮。确保这一轮拿4张。(即你拿1张时，我拿3张；你拿2张时，我拿两张；你拿3张时，我拿1张)

之后的每轮都是确保拿4张。

最后一轮刚好还是我拿到了底牌。(因为(54-2)/4=13，正好整除)

拓展问题：我后拿，如何保证拿到底牌？

1 1 52/4=13

2 3 可能就赢不了了。

3 3 48/4=12

之后的策略与上面一致。

### 2 赛马

>一共有25匹马，赛马场有5条跑道。
>你手上没有计时工具，只能通过比赛看出哪匹马跑得比哪匹马快，并不知道绝对速度。
>每匹马每次出场的状态一样。（马A跑得比马B快，无论比多少次都是这个结果。每匹马的速度是一定的，不变的）
>求：至少比多少场才能得出跑得最快的三匹马。

答：**7场**。每5匹一组，5场。每组获胜的马，再比一场，第6场。第6场获得第一名的马就是25匹马里最快的那匹。剩下还有可能是第二名、第三名的马有：**第6场的第二、三名，第6场的第一名（25匹里的冠军）之前所在组的第二、三名，第6场的第二名之前所在组的第二名**。这里正好又凑齐5匹马，再比一场。**第7场**的第一二名就是，25匹马的亚军和季军。

### 3 称重
>有一个天平, 有一个1000g重的物体，可以将它分为若干个整数重的小物体（作为砝码），要求这些砝码可以称出1~1000g以内任意重的物体。砝码只能放一边，求砝码数最少的分法。

答：**1 2 4 8 16 32 64 128 256 489**。理由：**二进制**。

进阶：
>砝码能放两边，求同样的问题。（比如我有重量分别为1、3、5的砝码，就可以称出重量为1、2、3、4、5、6、7、8、9的物体）

答：**1 3 9 27 81 243 636**。理由：我是**递推**出来的。从最小的开始，能称1g的物体必须要有一个1g的砝码。然后1g能称了，接着就要称2g。你可以再来个1g的也可以再来个2g的或者3g的，但是三者比较之下，加个3g的不仅能称2g还可以称3、4g，比加个1、2g划算。由此可见，只需将前面的砝码重量*2+1及可以得到下一个最“划算”的砝码重量。1、3已经可以称1~4g的物体，接下来要称5g。那就4+5=9。下一个砝码切成9g最划算。差不多可以总结规律了，就是**3的整数次幂等比序列**。



## 六、反问的话

### 客户端团队规模?职责划分？

一只手数的过来的可以不考虑了 但也不排除可能是大牛精英团队
根据业务线分多个业务组 或 基础中台组+多个业务组

### 贵公司的盈利情况和融资情况

要是一直烧钱还没找到盈利点的话 考虑一下加入后被裁的风险再决定是否加入

### 为什么有当前应聘岗位？

**上一任升职**:  好事。这个岗位风水好。
**上一任离职**:  得看什么原因离职。要是是留了一堆烂摊子的话 那就要慎重考虑是否加入了
**公司业务拓展缺人**:  再具体问问, 新业务的具体细节, 执行进度。计划什么时候上线。如果时间很赶, 那就要准备好加入后加班的心理准备。

### 问一下面试官对自己的评价或建议

面试官一般不会主动提出评价和建议。因为不是每个应聘者都愿意听。一些太直接的评价甚至会激怒应聘者("你也不怎么样，凭什么说我; 我都还没打算入职你们公司, 你有什么资格对我做出批评")。所以问这个问题最好稍稍引导一下面试官来说出一些客观的评价。比如，先夸一下对方，说面试的问题很有深度，有些我确实答得不好。无论应聘成功与否，我都希望这是一次有反馈，有价值的面试。经过这次面试，希望你能给现阶段的我一些建议和评价。可以是技术方向上的，也可以技术深度上的等等。这样比较好地引导对方来思考和回答这个问题。这个问题最好是技术终面的时候问(一般是技术老大, 或者你入职后的直属领导)，无论技术水平还是思想境界都比你高一个高度的。问出来的建议和评价绝对是对现阶段的你最有帮助的。

### App的技术体系

Java/Kotlin。用到的开源框架(网络的、图片加载的、MVC/P/VM的等)

H5、RN、flutter等, 各自占比。






[3handshake]: ./art/3handshake.png
[4handbye]: ./art/4handbye.png