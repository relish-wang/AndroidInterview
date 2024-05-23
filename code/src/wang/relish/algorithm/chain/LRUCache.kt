package wang.relish.algorithm.chain

/**
 * SoulApp一面算法题(240523)
 * 要求必须用单链表。
 *
 * LeetCode原题: https://leetcode.cn/problems/lru-cache/description/
 */
class LRUCache(val capacity: Int) {
    val dummy = ListNode(0)
    val map = hashMapOf<Int, ListNode>()
    fun get(key: Int): Int {
        val n = map[key] ?: return -1
        var c: ListNode? = dummy
        while (c != null && c.next != n) {
            c = c.next
        }
        if (c != null) {
            moveToHead(c, n)
        }
        return n.`val`
    }

    fun put(key: Int, value: Int) {
        val n = map[key]
        if (n == null) {
            val node = ListNode(value)

            val oldHead = dummy.next
            dummy.next = node
            node.next = oldHead

            map[key] = node

            if (map.size > capacity) {
                var c: ListNode? = dummy
                var i = 0
                while (c != null) {
                    c = c.next
                    i++

                    if (i == capacity) {
                        break
                    }
                }
                var needRemove: ListNode? = c?.next
                c?.next = null // 切断尾部
                if (needRemove != null) {//移除map里的node
                    for (entry in map) {
                        if (entry.value == needRemove) {
                            map.remove(entry.key)
                            break
                        }
                    }
                }
            }
        } else {
            var pre: ListNode? = dummy
            while (pre != null && pre.next != n) {
                pre = pre.next
            }
            n.`val` = value
            if (pre != null) {
                moveToHead(pre, n)
            }
        }
    }

    private fun moveToHead(pre: ListNode, n: ListNode) {
        pre.next = n.next

        val oldHead = dummy.next
        dummy.next = n
        n.next = oldHead
    }

    class ListNode(var `val`: Int) {
        var next: ListNode? = null
    }
}