package com.sun.async;

import com.alibaba.fastjson.JSON;
import com.sun.util.JedisAdapter;
import com.sun.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ty on 2017/4/2.
 */
@Service
//根据EventHandler分配任务，EventHandler实现类按业务事先写好
public class EventConsumer implements InitializingBean,ApplicationContextAware{
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    private ApplicationContext applicationContext;
    //根据EventHandler实现类来派发任务（借助cofig实现），EventType与EventHandler实现类是多对多关系
    private Map<EventType,List<EventHandler>> config = new HashMap<>();

    @Autowired
    JedisAdapter jedisAdapter;

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String,EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
        if(beans != null){
            for(Map.Entry<String,EventHandler> entry : beans.entrySet()){
                List<EventType> eventTypes = entry.getValue().getSupportEventType();
                for(EventType eventType: eventTypes){
                    if(!config.containsKey(eventType)){
                        config.put(eventType,new ArrayList<EventHandler>());
                    }
                    config.get(eventType).add(entry.getValue());
                }
            }
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    String key = RedisKeyUtil.getEventQueueKey();
                    List<String> events = jedisAdapter.brpop(0,key);
                    for(String event:events){
                        //第一个一般返回为key值
                        if(event.equals(key)){
                            continue;
                        }
                        EventModel eventModel = JSON.parseObject(event,EventModel.class);
                        if(!config.containsKey(eventModel.getType())){
                            logger.error("不能识别的事件");
                            continue;
                        }
                        for(EventHandler handler:config.get(eventModel.getType())){
                            handler.doHanler(eventModel);
                        }
                    }
                }
            }
        });
        thread.start();
    }

    //获取spring上下文，进而获取EventHandler实现类的实例
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


}
