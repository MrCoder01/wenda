package com.sun.controller;

import com.sun.async.EventModel;
import com.sun.async.EventProducer;
import com.sun.async.EventType;
import com.sun.model.Comment;
import com.sun.model.EntityType;
import com.sun.model.HostHolder;
import com.sun.model.Question;
import com.sun.service.CommentService;
import com.sun.service.QuestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

/**
 * Created by ty on 2017/3/31.
 */
@Controller
public class CommentController {
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    CommentService commentService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    QuestionService questionService;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(path = {"/addComment"}, method = {RequestMethod.POST})
    public String addComment(@RequestParam("questionId") int questionId,
                             @RequestParam("content") String content) {
        try {
            //设置内容
            Comment comment = new Comment();
            comment.setContent(content);
            comment.setCreatedDate(new Date());
            comment.setEntityId(questionId);
            comment.setEntityType(EntityType.ENTITY_QUESTION);
            if (hostHolder.getUser() != null) {
                comment.setUserId(hostHolder.getUser().getId());
            } else {
                return "redirect:/reglogin";
            }
            commentService.addComment(comment);

            //在增加评论时，更新对应问题表中的评论数量，类似于外键
            int count = commentService.getCommentCountByEntity(comment.getEntityId(),comment.getEntityType());
            questionService.updateCount(questionId,count);

            //评论时增加事件处理
            eventProducer.fireEvent(new EventModel(EventType.COMMENT).setActorId(hostHolder.getUser().getId())
                    .setEntityType(EntityType.ENTITY_QUESTION)
                    .setEntityId(questionId)
                    .setEntityOwnerId(questionService.getById(questionId).getUserId()));
        } catch (Exception e) {
            logger.error("增加问题失败" + e.getMessage());
        }
        return "redirect:/question/" + questionId;
    }

}
