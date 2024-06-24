package wang.relish.algorithm.handler.mini

import java.util.concurrent.LinkedBlockingDeque
import kotlin.math.min

class MessageQueue {


    // val queue = LinkedBlockingDeque<Message>()
    // 单链表头
    var mMessages: Message? = null

    fun enqueueMessage(msg: Message) {
        enqueueMessage(msg, 0L)
    }

    fun enqueueMessage(msg: Message, `when`: Long) {
        if (msg.target == null) {
            throw IllegalArgumentException("Message must have a target.");
        }
        msg.`when` = `when`
        var p: Message? = mMessages
        if (p == null || `when` == 0L || `when` < p.`when`) {
            msg.next = p
            mMessages = msg
        } else {
            var prev: Message?
            while (true) {
                prev = p
                p = p?.next
                if (p == null || `when` < p.`when`) {
                    break
                }
            }
            msg.next = p
            prev?.next = msg
        }
    }

    private fun last(): Message? {
        val h = mMessages
        if (h == null) return null
        var cur = h
        while (cur?.next != null) {
            cur = cur?.next
        }
        return cur
    }

    internal fun next(): Message? {
        var nextPollTimeoutMillis = 0L
        while (true) {
            nativePollOnce(nextPollTimeoutMillis)
            synchronized(this) {
                val now = System.currentTimeMillis()
                var prevMsg: Message? = null
                var msg = mMessages
                if (msg != null && now < msg.`when`) {
                    nextPollTimeoutMillis = min(msg.`when` - now, Integer.MAX_VALUE.toLong())
                } else {
                    if (prevMsg != null) {
                        prevMsg.next = msg?.next
                    } else {
                        mMessages = msg?.next
                    }
                    msg?.next = null
                    return msg
                }
            }
        }
        return null
    }

    fun nativePollOnce(nextPollTimeout: Long) {
        Thread.sleep(nextPollTimeout)
    }
}