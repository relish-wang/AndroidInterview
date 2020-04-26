# 计算机网络

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
| ----------- | ------------------ | -- |
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




[3handshake]: ./art/3handshake.png
[4handbye]: ./art/4handbye.png