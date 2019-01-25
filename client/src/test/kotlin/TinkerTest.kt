import com.tinkertian.snowflake.client.SnowflakeClient
import org.junit.Test

class TinkerTest {
    @Test
    fun first() {
        var sid = SnowflakeClient.next()
        println(sid)
    }
}