package wang.relish.algorithm.chain

import wang.relish.algorithm.common.ListNode


/**
 * 删除单向链表中的第K个节点
 *
 * @param head 单向链表
 * @param k 删除倒数第k个节点
 */
private fun deleteReverseNode(head: ListNode?, k: Int) {
    var t: ListNode? = head
    val list = arrayListOf<ListNode>()
    while (t != null) {
        list.add(t)
        t = t.next
    }
    list[list.size - k - 1].next = list[list.size - k + 1]
}

fun main(args: Array<String>) {
    val list = ListNode.newList("[ 1, 2, 3, 4, 5, 6, 7 ]")
    println(list)
    deleteReverseNode(list, 5)
    println(list)
}