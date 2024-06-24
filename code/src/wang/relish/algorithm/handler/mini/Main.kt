package wang.relish.algorithm.handler.mini

import kotlin.concurrent.thread


fun main() {
    Looper.prepareMain()
    sendMessageInOtherThread()
    Looper.loop()
}

fun now(): String {
    return (System.currentTimeMillis() % 100000).toString()
}


fun sendMessageInOtherThread() {
    // 线程 1
    thread {
        val handler = object : Handler(Looper.sMainLooper) {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                msg.callback?.run()?.also { println("=====${now()}=====") }
            }
        }

        val msg1 = Message.obtain()
        msg1.callback = Runnable {
            println("${Thread.currentThread().name} handler1 我是消息3s---${now()}")
        }
        handler.sendMessageDelay(msg1, 3000L)

        val msg2 = Message.obtain()
        msg2.callback = Runnable {
            println("${Thread.currentThread().name} handler1 我是消息1s---${now()}")
        }
        handler.sendMessageDelay(msg2, 1000L)

        val msg25 = Message.obtain()
        msg25.callback = Runnable {
            println("累了")
        }
        handler.sendMessage(msg25)

        val msg3 = Message.obtain()
        msg3.callback = Runnable {
            println("${Thread.currentThread().name} handler1 我是消息quit")
            Looper.getMainLooper().quit()
        }
        handler.sendMessageDelay(msg3, 7000L)

        println("${Thread.currentThread().name}   handler1 发送时间---${now()}");
    }
    // 线程2
    thread {
        val handler = object : Handler(Looper.sMainLooper) {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                msg.callback?.run()?.also { println("=====${now()}=====") }
            }
        }

        val msg1 = Message.obtain()
        msg1.callback = Runnable {
            println("${Thread.currentThread().name} handler2 我是消息4s---${now()}")
        }
        handler.sendMessageDelay(msg1, 4000L)

        val msg2 = Message.obtain()
        msg2.callback = Runnable {
            println("${Thread.currentThread().name} handler2 我是消息2s---${now()}")
        }
        handler.sendMessageDelay(msg2, 2000L)

        val msg3 = Message.obtain()
        msg3.callback = Runnable {
            println("${Thread.currentThread().name} handler2 我是消息quit")
            Looper.getMainLooper().quit()
        }
        handler.sendMessageDelay(msg3, 6000L)
        println("${Thread.currentThread().name}   handler2 发送时间---${now()}");
    }
}
