package wang.relish.algorithm.handler.mini


open class Handler {

    val mLooper: Looper
    val mQueue: MessageQueue

    constructor() {
        mLooper = Looper.myLooper()
        mQueue = mLooper.mQueue
    }

    constructor(looper: Looper) {
        mLooper = looper
        mQueue = mLooper.mQueue
    }

    fun sendMessage(msg: Message) {
        sendMessageDelay(msg, 0L)
    }

    fun sendMessageDelay(msg: Message, upTime: Long) {
        val t = maxOf(upTime, 0L)
        msg.target = this
        mQueue?.enqueueMessage(msg, System.currentTimeMillis() + t)
    }

    open fun handleMessage(msg: Message) {
    }

    fun dispatchMessage(msg: Message) {
        handleMessage(msg)
    }
}