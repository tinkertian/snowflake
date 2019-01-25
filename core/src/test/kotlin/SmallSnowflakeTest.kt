import com.tinkertian.snowflake.core.LargeSnowflake
import jodd.io.FileUtil
import java.io.File
import kotlin.concurrent.thread

class SmallSnowflakeTest {
    /**
     * JVM: -Xms16g -Xmx16g
     */
    //@Test
    fun writeFile() {
        var seq = 0
        thread {
            while (true) {
                println(seq / 10000)
                Thread.sleep(5000)
                if (seq > 200000000) {
                    break
                }
            }
        }

        var buffer: StringBuffer
        var index = 0
        var sfArray = Array(1024) { i -> LargeSnowflake(i) }
        val file = FileUtil.createTempFile("SID", "", File("c:/_tmp"), true)
        for (i in 0..100) {
            buffer = StringBuffer()
            var arr = Array(1000000) { sfArray[index++ % 1023].next() }
            for (sid in arr) {
                seq++
                buffer.append(sid.toString() + "\n")
            }
            FileUtil.appendString(file, buffer.toString())
        }
    }
}