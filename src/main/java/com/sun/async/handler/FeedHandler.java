package com.sun.async.handler;

import com.alibaba.fastjson.JSONObject;
import com.sun.async.EventHandler;
import com.sun.async.EventModel;
import com.sun.async.EventType;
import com.sun.model.EntityType;
import com.sun.model.Feed;
import com.sun.model.Question;
import com.sun.model.User;
import com.sun.service.FeedService;
import com.sun.service.FollowService;
import com.sun.service.QuestionService;
import com.sun.service.UserService;
import com.sun.util.JedisAdapter;
import com.sun.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 推送新鲜事，大V触发了具有监听器的事件（fireEvent）后，执行此方法，将新鲜事压入Redis，
 * 大V粉丝登陆后，直接从Redis中读取到此新鲜事
 * Created by ty on 2017/4/4.
 */
//特定事件发生后自动执行，类似事件监听器功能，俺需要添加监听器
@Component
public class FeedHandler implements EventHandler{
    @Autowired
    FollowService followService;

    @Autowired
    UserService userService;

    @Autowired
    FeedService feedService;

    @Autowired
    JedisAdapter jedisAdapter;

    @Autowired
    QuestionService questionService;

    //指定LIKE、FOLLOW事件执行后，触发该方法
    @Override
    public List<EventType> getSupportEventType() {
        return Arrays.asList(new EventType[]{EventType.COMMENT, EventType.FOLLOW});
    }

    @Override
    public void doHanler(EventModel eventModel) {
        Feed feed = new Feed();
        feed.setCreatedDate(new Date());
        feed.setType(eventModel.getType().getValue());
        feed.setUserId(eventModel.getActorId());
        feed.setData(buildFeedData(eventModel));
        if(feed.getData()==null){
            // 不支持的feed
            return;
        }
        feedService.addFeed(feed);

        //获取触发此事件用户的所有粉丝
        List<Integer> followers = followService.getFollowers(EntityType.ENTITY_USER, eventModel.getActorId(),Integer.MAX_VALUE);
        // 系统队列(0默认是系统占用)
        followers.add(0);
        // 给所有粉丝推事件（压入新鲜事，粉丝还未读取）
        //读取快，粉丝非常多时非常消耗内存
        for(int follower:followers){
            String timelineKey = RedisKeyUtil.getTimelineKey(follower);
            jedisAdapter.lpush(timelineKey,String.valueOf(feed.getId()));
        }
    }

    private String buildFeedData(EventModel model){
        Map<String,String> map = new HashMap<>();
        User actor = userService.getById(model.getActorId());
        if(actor==null){
            return null;
        }
        map.put("userId",String.valueOf(actor.getId()));
        map.put("userHead",actor.getHeadUrl());
        map.put("userName",actor.getName());
        //针对问题的新鲜事（点赞问题，关注问题）
        if(model.getType()==EventType.COMMENT||(model.getType()==EventType.FOLLOW && model.getEntityType()== EntityType.ENTITY_QUESTION)){
            Question question = questionService.getById(model.getEntityId());
            if(question == null){
                return null;
            }
            map.put("questionId",String.valueOf(model.getEntityId()));
            map.put("questionTitle",question.getTitle());
            return JSONObject.toJSONString(map);
        }
        return null;
    }


}

