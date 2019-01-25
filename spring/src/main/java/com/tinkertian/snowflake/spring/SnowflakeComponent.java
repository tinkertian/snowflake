package com.tinkertian.snowflake.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import com.tinkertian.snowflake.core.LargeSnowflake;
import com.tinkertian.snowflake.core.SmallSnowflake;

import javax.annotation.PostConstruct;

@Component
public class SnowflakeComponent {
    private final static Logger           logger             = LoggerFactory.getLogger(SnowflakeComponent.class);
    private final static Object           largeSnowflakeLock = new Object();
    private final static Object           smallSnowflakeLock = new Object();
    private static       Environment      environment;
    private static       int              largeStart;
    private static       int              largeEnd;
    private static       int              largeCurrentIndex;
    private static       int              smallStart;
    private static       int              smallEnd;
    private static       int              smallCurrentIndex;
    private static       SmallSnowflake[] smallSnowflakeArray;
    private static       LargeSnowflake[] largeSnowflakeArray;

    @Autowired
    ApplicationContext applicationContext;

    @PostConstruct
    private void init() {
        if (applicationContext != null) {
            this.environment = applicationContext.getEnvironment();
            if (environment.containsProperty("snowflake.node.large") &&
                    environment.containsProperty("snowflake.node.large")) {
                this.largeStart = Integer.valueOf(environment.getProperty("snowflake.node.large").split("-")[0]);
                this.largeEnd = Integer.valueOf(environment.getProperty("snowflake.node.large").split("-")[1]);
                this.largeCurrentIndex = this.largeStart;
                this.smallStart = Integer.valueOf(environment.getProperty("snowflake.node.small").split("-")[0]);
                this.smallEnd = Integer.valueOf(environment.getProperty("snowflake.node.small").split("-")[1]);
                this.smallCurrentIndex = this.smallStart;
                logger.info("Snowflake node large: {}-{}, small {}-{}", this.largeStart, this.largeEnd, this.smallStart, this.smallEnd);
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
