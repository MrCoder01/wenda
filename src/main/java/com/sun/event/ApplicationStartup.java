package com.sun.event;

import com.sun.util.SchedulerRequestCount;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * Created by TY on 2017/8/30.
 */
@Component
public class ApplicationStartup implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        SchedulerRequestCount schedulerRequestCount = event.getApplicationContext().getBean(SchedulerRequestCount.class);
        schedulerRequestCount.doCount();
    }
}
