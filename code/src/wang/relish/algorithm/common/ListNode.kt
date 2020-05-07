package wang.relish.algorithm.common

/**
 * 链表节点
 */
data class ListNode(
        val data: String,
        var next: ListNode? = null
) {

    companion object {
        fun newList(str: String): ListNode {
            val arr = str.replace(Regex("^\\[|]$"), "").trim().split(",")
            val head = ListNode(arr[0], null)
            var t = head
            var s: ListNode?
            for (i: Int in 1 until arr.size) {
                s = ListNode(arr[i].trim())
                t.next = s
                t = s
            }
            return head
        }
    }

    override fun toString(): String {
        var t: ListNode? = this
        val sb = StringBuilder()
        do {
            sb.append(t!!.data)
            sb.append("->")
            t = t.next
        } while (t != null)
        sb.append("null")
        return sb.toString()
    }
}