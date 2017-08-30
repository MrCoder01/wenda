package com.sun.util;

import com.sun.aspect.RequestCountAspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by TY on 2017/8/30.
 */
@Component
public class SchedulerRequestCount {
    private static final Logger logger = LoggerFactory.getLogger(SchedulerRequestCount.class);

    @Autowired
    RequestCountAspect requestCountAspect;

    private long old;

    public void doCount(){
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(
                new DoCount(),
                0,
                30*1000,
                TimeUnit.MILLISECONDS);
    }

    class DoCount implements Runnable{

        @Override
        public void run() {
            long temp = requestCountAspect.getCount()-old;
            logger.info("requestCount of lmin:"+ temp);
            old = requestCountAspect.getCount();
        }
    }
}
