package cn.changjiahong.banker.utils

import kotlinx.datetime.Clock

object ShortSnowflake10{
    private const val machineId: Int = 11
    private const val epochSecond: Long = 1_700_000_000L // 固定时间起点（单位秒）

    private var lastSecond = -1L
    private var sequence = 0
    private val maxSequence = 999


    fun nextId(): Long {
        val nowSecond = Clock.System.now().toEpochMilliseconds() / 1000

        if (nowSecond < lastSecond) {
            throw IllegalStateException("时钟回拨")
        }

        if (nowSecond == lastSecond) {
            sequence = (sequence + 1) % (maxSequence + 1)
            if (sequence == 0) {
                while (Clock.System.now().toEpochMilliseconds() / 1000 <= lastSecond) {
                    // 等待下一秒
                }
                return nextId()
            }
        } else {
            sequence = 0
        }

        lastSecond = nowSecond

        val timePart = (nowSecond - epochSecond).toString().padStart(5, '0')   // 5位
        val machinePart = machineId.toString().padStart(2, '0')                // 2位
        val seqPart = sequence.toString().padStart(3, '0')                     // 3位

        return "$timePart$machinePart$seqPart".toLong()
    }
}


fun getSnowId(): Long{
    return ShortSnowflake10.nextId()
}