package wang.relish.algorithm.design

/**
 * leetcode原题
 *
 * https://leetcode.cn/problems/design-underground-system/description/
 */

class UndergroundSystem() {

    val checkIn = hashMapOf<Int, Pair<String, Int>>() // <乘客,<地铁站, 入站时间>>

    val total = hashMapOf<String, Pair<Int, Int>>() // <出站#入站, <总时长, 个数>>

    fun checkIn(id: Int, stationName: String, t: Int) {
        checkIn[id] = stationName to t
    }

    fun checkOut(id: Int, stationName: String, t: Int) {
        val pair = checkIn[id] ?: return
        val checkInStationName = pair.first
        val checkInTime = pair.second
        val key = "${checkInStationName}#$stationName"
        val pair2 = total[key]
        val totalTime = pair2?.first ?: 0
        val times = pair2?.second ?: 0
        total[key] = Pair(totalTime + t - checkInTime, times + 1)
    }

    fun getAverageTime(startStation: String, endStation: String): Double {
        return total["${startStation}#${endStation}"]?.let {
            it.first * 1.0 / it.second
        } ?: 0.0
    }
}

/**
 * Your UndergroundSystem object will be instantiated and called as such:
 * var obj = UndergroundSystem()
 * obj.checkIn(id,stationName,t)
 * obj.checkOut(id,stationName,t)
 * var param_3 = obj.getAverageTime(startStation,endStation)
 */