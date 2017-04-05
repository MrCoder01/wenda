package com.sun.async;

import java.util.List;

/**
 * Created by ty on 2017/4/2.
 */
public interface EventHandler {
    void doHanler(EventModel eventModel);

    List<EventType> getSupportEventType();
}
