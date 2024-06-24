package wang.relish.algorithm.arr

/**
 * 字节(Tiktok短视频)一面 20240613
 */
fun main(args: Array<String>) {
    val q = Queue<Int>(1)
    println(q.push(1))// 1
    println(q.push(2))// 2
    println(q.push(3))// 3
    println(q.size())// 3
    println(q.pop())// 1
    println(q.push(4))// 4
    println(q.size())// 3
}

class Queue<T>(var n: Int) {
    private var arr = Array<Any?>(n) {}
    var h = 0
    var t = 0

    fun pop(): T {
        val ans = arr[h]
        h = (h + 1) % n
        return ans as T
    }

    fun push(num: T): T {
        arr[t] = num as Any
        t = (t + 1) % n
        if (t == h) {
            val newSize = n shl 1
            val newArray = arr.copyOf(newSize)
            arr = newArray
            h = 0
            t = n
            //println("扩容: $n -> $newSize")
            n = newSize
        }
        return num
    }

    fun size(): Int {
        return if (t > h) {
            t - h
        } else {
            n - (h - t)
        }
    }
}