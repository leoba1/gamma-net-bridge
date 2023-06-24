package com.bai.executor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/24 20:40
 */
public class ScheduledExecutor {

    private static ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    public static ScheduledExecutorService Executor(Runnable run) {
        executor.scheduleWithFixedDelay(run,10,5, TimeUnit.SECONDS);
        return executor;
    }
}
