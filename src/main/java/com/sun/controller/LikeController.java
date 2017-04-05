package com.sun.controller;

import com.sun.async.EventModel;
import com.sun.async.EventProducer;
import com.sun.async.EventType;
import com.sun.model.Comment;
import com.sun.model.EntityType;
import com.sun.model.HostHolder;
import com.sun.service.CommentService;
import com.sun.service.LikeService;
import com.sun.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.aspectj.weaver.tools.cache.SimpleCacheFactory.path;

/**
 * Created by ty on 2017/4/1.
 */
@Controller
public class LikeController {
    private static final Logger logger = LoggerFactory.getLogger(LikeController.class);

    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    EventProducer eventProducer;

    @Autowired
    CommentService commentService;

    @RequestMapping(path = {"/like"},method = {RequestMethod.POST})
    @ResponseBody
    public String addLike(@RequestParam("commentId") int commentId){
        if(hostHolder.getUser()==null){
            return WendaUtil.getJSONString(999);
        }
        long likeCount=0;
        try{
            likeCount = likeService.addLike(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT,commentId);

            //异步增加点赞自动发信息
            Comment comment = commentService.getById(commentId);
            eventProducer.fireEvent(new EventModel(EventType.LIKE)
                    .setActorId(hostHolder.getUser().getId())
                    .setEntityId(commentId)
                    .setEntityType(EntityType.ENTITY_COMMENT)
                    .setEntityOwnerId(comment.getUserId())
                    .setExts("questionId",String.valueOf(comment.getEntityId())));
        } catch (Exception e){
            logger.error("点赞失败"+e.getMessage());
        }
        return WendaUtil.getJSONString(0,String.valueOf(likeCount));
    }

    @RequestMapping(path = {"/dislike"},method = {RequestMethod.POST})
    @ResponseBody
    public String addDisLike(@RequestParam("commentId") int commentId){
        if(hostHolder.getUser()==null){
            return WendaUtil.getJSONString(999);
        }
        long likeCount=0;
        try{
            likeCount = likeService.addDisLike(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT,commentId);
        } catch (Exception e){
            logger.error("点踩失败"+e.getMessage());
        }
        return WendaUtil.getJSONString(0,String.valueOf(likeCount));
    }
}
