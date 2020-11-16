package com.tinkertian.snowflake.spring;

import com.tinkertian.snowflake.core.LargeSnowflake;
import com.tinkertian.snowflake.core.SmallSnowflake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class SnowflakeComponent {
    private final static Logger           logger             = LoggerFactory.getLogger(SnowflakeComponent.class);
    private final static String           splitStr           = "-";
    private final static Object           largeSnowflakeLock = new Object();
    private final static Object           smallSnowflakeLock = new Object();
    private static       int              largeStart;
    private static       int              largeEnd;
    private static       int              largeCurrentIndex;
    private static       int              smallStart;
    private static       int              smallEnd;
    private static       int              smallCurrentIndex;
    private static       SmallSnowflake[] smallSnowflakeArray;
    private static       LargeSnowflake[] largeSnowflakeArray;

    private final ApplicationContext applicationContext;

    public SnowflakeComponent(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    private void init() {
        if (applicationContext != null) {
            Environment environment   = applicationContext.getEnvironment();
            boolean     containsLarge = environment.containsProperty("snowflake.node.large");
            boolean     containsSmall = environment.containsProperty("snowflake.node.small");
            if (containsLarge && containsSmall) {
                String largeStr = environment.getProperty("snowflake.node.large");
                String smallStr = environment.getProperty("snowflake.node.small");
                if (largeStr == null || smallStr == null)
                    throw new NullPointerException();
                String largeStartStr = largeStr.split(splitStr)[0];
                String largeEndStr   = largeStr.split(splitStr)[1];
                String smallStartStr = smallStr.split(splitStr)[0];
                String smallEndStr   = smallStr.split(splitStr)[0];
                largeStart        = Integer.parseInt(largeStartStr);
                largeEnd          = Integer.parseInt(largeEndStr);
                smallStart        = Integer.parseInt(smallStartStr);
                smallEnd          = Integer.parseInt(smallEndStr);
                largeCurrentIndex = largeStart;
                smallCurrentIndex = smallStart;
                logger.info("Snowflake node large: {}-{}, small {}-{}", largeStart, largeEnd, smallStart, smallEnd);
            } else {
                logger.error("Not found snowflake.node.small and snowflake.node.small for environment");
            }
        } else {
            logger.error("Spring ApplicationContext is null");
        }
    }

    public static long nextLarge() {
        if (largeSnowflakeArray == null) {
            synchronized (largeSnowflakeLock) {
                if (largeSnowflakeArray == null) {
                    largeSnowflakeArray = new LargeSnowflake[largeEnd + 1];
                    for (int i = largeStart; i <= largeEnd; i++) {
                        largeSnowflakeArray[i] = new LargeSnowflake(i);
                    }
                }
            }
        }
        if (largeCurrentIndex > largeEnd) {
            largeCurrentIndex = largeStart;
        }
        return largeSnowflakeArray[largeCurrentIndex++].next();
    }

    public static long nextSmall() {
        if (smallSnowflakeArray == null) {
            synchronized (smallSnowflakeLock) {
                if (smallSnowflakeArray == null) {
                    smallSnowflakeArray = new SmallSnowflake[smallEnd + 1];
                    for (int i = smallStart; i <= smallEnd; i++) {
                        smallSnowflakeArray[i] = new SmallSnowflake(i);
                    }
                }
            }
        }

        if (smallCurrentIndex > smallEnd) {
            smallCurrentIndex = smallStart;
        }
        return smallSnowflakeArray[smallCurrentIndex++].next();
    }
}
