package wang.relish.algorithm.handler.mini

class Looper {

    internal val mQueue: MessageQueue
    private var quit: Boolean = false

    private constructor() {
        mQueue = MessageQueue()
    }

    fun quit() {
        quit = true
    }

    companion object {
        @JvmStatic
        val sThreadLocal = ThreadLocal<Looper>()

        @JvmStatic
        lateinit var sMainLooper: Looper

        @JvmStatic
        fun myLooper(): Looper = sThreadLocal.get()

        @JvmStatic
        fun prepare() {
            if (sThreadLocal.get() != null) {
                throw RuntimeException("Only one Looper may be created per thread");
            }
            sThreadLocal.set(Looper())
        }

        @JvmStatic
        fun prepareMain() {
            prepare()
            sMainLooper = myLooper()
        }

        @JvmStatic
        fun getMainLooper(): Looper = sMainLooper

        @JvmStatic
        fun loop() {
            val me = myLooper()
            if (me == null) {
                throw RuntimeException("No Looper; Looper.prepare() wasn't called on this thread.");
            }
            while (true) {
                val msg = me.mQueue.next()
                if (msg == null) {
                    continue
                }
                msg.target?.dispatchMessage(msg)
                if (me.quit) return
            }
        }
    }
}