- 节点分配: 1023节点为测试节点勿在生产环境中使用
- JAVA客户端代码, jodd的http模块
```
package com.appgather.store.ags.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import jodd.http.HttpRequest;

import java.util.LinkedList;
import java.util.Queue;

public class SidUtil {
    private static final Queue<Long> SID_QUEUE = new LinkedList<>();
    private static final int         SID_COUNT = 100;

    public static long next() {
        if (SID_QUEUE.size() == 0) {
            synchronized (SidUtil.class) {
                if (SID_QUEUE.size() == 0) {
                    String host = "localhost";
                    String body = HttpRequest.get(host + ":1010/sf/next-small-batch/" + SID_COUNT)
                            .send()
                            .body();
                    try {
                        String[] sidArray = new ObjectMapper().readValue(body, new String[SID_COUNT].getClass());
                        for (String sidStr : sidArray) {
                            long sid = Long.valueOf(sidStr);
                            SID_QUEUE.offer(sid);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return SID_QUEUE.poll();
    }
}

```