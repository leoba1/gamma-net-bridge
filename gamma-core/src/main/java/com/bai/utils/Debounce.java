package com.bai.utils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/8/28 10:25
 */
public class Debounce {
    private Timer timer;
    private long delay;
    private boolean isDebouncing;

    public Debounce(long delay) {
        this.delay = delay;
        this.timer = new Timer();
        this.isDebouncing = false;
    }

    public synchronized void debounce(final Runnable runnable) {
        if (isDebouncing) {
            timer.cancel();
        }

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                isDebouncing = false;
                runnable.run();
            }
        }, delay);

        isDebouncing = true;
    }
}
