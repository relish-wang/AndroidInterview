package wang.relish.algorithm.handler.mini

class Message {
    var what: Int = 0
    var target: Handler? = null
    var callback: Runnable? = null
    var `when`: Long = 0
    var next: Message? = null

    companion object {
        fun obtain(): Message {
            // 享元模式: 单链表50个
            return Message()
        }
    }
}