package com.sun.async;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ty on 2017/4/2.
 */
public class EventModel {
    private EventType type;
    private int actorId;
    //目标实体(点赞回答，则目标则为回答)
    private int entityType;
    private int entityId;
    private int entityOwnerId;

    private Map<String,String> exts = new HashMap<String,String>();

    public EventModel(){

    }

    public EventModel(EventType type){
        this.type = type;
    }

    public EventType getType() {
        return type;
    }

    public EventModel setType(EventType type) {
        this.type = type;
        return this;
    }

    public int getActorId() {
        return actorId;
    }

    public EventModel setActorId(int actorId) {
        this.actorId = actorId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public EventModel setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public EventModel setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityOwnerId() {
        return entityOwnerId;
    }

    public EventModel setEntityOwnerId(int entityOwnerId) {
        this.entityOwnerId = entityOwnerId;
        return this;
    }

    public String getExts(String key) {
        return exts.get(key);
    }

    public EventModel setExts(String key,String value) {
        this.exts.put(key,value);
        return this;
    }

    public Map<String, String> getExts() {
        return exts;
    }

    public void setExts(Map<String, String> exts) {
        this.exts = exts;
    }
}
