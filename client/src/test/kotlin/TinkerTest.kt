import com.tinkertian.snowflake.api.domain.exception.SnowflakeException
import org.junit.Test

class TinkerTest {
    @Test
    fun first() {
        var exception = SnowflakeException(1, "")
        exception.id = 2
        println()
    }
}