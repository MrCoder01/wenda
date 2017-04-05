package com.sun.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ty on 2017/3/28.
 */
public class ViewObject {
    private Map<String,Object> objs = new HashMap<>();

    public void set(String key,Object obj){
        objs.put(key,obj);
    }

    public Object get(String key){
        return objs.get(key);
    }
}
