package com.sun.controller;

import com.sun.model.EntityType;
import com.sun.model.Feed;
import com.sun.model.HostHolder;
import com.sun.service.FeedService;
import com.sun.service.FollowService;
import com.sun.util.JedisAdapter;
import com.sun.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ty on 2017/4/4.
 */
@Controller
public class FeedController {
    private static final Logger logger = LoggerFactory.getLogger(FeedController.class);

    @Autowired
    FeedService feedService;

    @Autowired
    FollowService followService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    JedisAdapter jedisAdapter;

    /**
     * 用户读取的新鲜事由系统推送(推)，直接从Redis(自定义存储，也可存在数据库中)中读取，Redis得中数据由大V触发了具有监听器的事件时生成（压入）
     * 适合粉丝量较小的用户（需要在每个粉丝的新鲜事队列中都添加一份）
     * @param model
     * @return
     */
    @RequestMapping(path={"/pushfeeds"},method={RequestMethod.GET,RequestMethod.POST})
    public String getPushFeeds(Model model){
        int localUserId = hostHolder.getUser()!=null?hostHolder.getUser().getId():0;
        List<String> feedIds = jedisAdapter.lrange(RedisKeyUtil.getTimelineKey(localUserId),0,10);
        List<Feed> feeds = new ArrayList<>();
        for(String feedId:feedIds){
            Feed feed = feedService.getById(Integer.parseInt(feedId));
            if(feed!=null){
                feeds.add(feed);
            }
        }
        model.addAttribute("feeds",feeds);
        return "feeds";
    }

    /**
     * 用户读取的新鲜事根据关注的人生成，未登录时不生成，登陆后再从数据库读取（拉）
     * @param model
     * @return
     */
    @RequestMapping(path={"/pullfeeds"},method={RequestMethod.GET,RequestMethod.POST})
    public String getPullFeeds(Model model){
        int localUserId = hostHolder.getUser()!=null?hostHolder.getUser().getId():0;
        List<Integer> followees = new ArrayList<>();
        if(localUserId!=0){
            //获取关注的人，根据关注的人获取新鲜事
            //不耗内存，性能主要依靠读取速度
            followees = followService.getFollowees(localUserId, EntityType.ENTITY_USER,Integer.MAX_VALUE);
        }
        List<Feed> feeds = feedService.getUserFeeds(Integer.MAX_VALUE,followees,10);
        model.addAttribute("feeds",feeds);
        return "feeds";
    }
}
