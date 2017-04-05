package com.sun.async;

import com.alibaba.fastjson.JSONObject;
import com.sun.util.JedisAdapter;
import com.sun.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by ty on 2017/4/2.
 */
@Service
public class EventProducer {
    @Autowired
    JedisAdapter jedisAdapter;

    public boolean fireEvent(EventModel eventModel){
        try{
            String value = JSONObject.toJSONString(eventModel);
            String key = RedisKeyUtil.getEventQueueKey();
            jedisAdapter.lpush(key,value);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
