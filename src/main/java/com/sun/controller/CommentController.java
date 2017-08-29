package com.sun.controller;

import com.sun.async.EventModel;
import com.sun.async.EventProducer;
import com.sun.async.EventType;
import com.sun.model.*;
import com.sun.service.CommentService;
import com.sun.service.MessageService;
import com.sun.service.MyExecutorService;
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

    @Autowired
    MyExecutorService myExecutorService;

    @Autowired
    MessageService messageService;

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
//            eventProducer.fireEvent(new EventModel(EventType.COMMENT).setActorId(hostHolder.getUser().getId())
//                    .setEntityType(EntityType.ENTITY_QUESTION)
//                    .setEntityId(questionId)
//                    .setEntityOwnerId(questionService.getById(questionId).getUserId()));
            //异步，hostHolder可能已经为空，必须提前保存user
            User user = hostHolder.getUser();
            myExecutorService.execute(new Runnable() {
                @Override
                public void run() {
                    Message message = new Message();
                    Question question = questionService.getById(questionId);
                    message.setFromId(user.getId());
                    message.setToId(question.getUserId());
                    message.setCreatedDate(new Date());

                    message.setContent("您好，用户" + user.getName()
                            + "给你的问题：“" + question.getTitle() +"”增加了新回答！快去看看吧。");
                    messageService.addMessage(message);
                }
            });

        } catch (Exception e) {
            logger.error("增加问题失败" + e.getMessage());
        }
        return "redirect:/question/" + questionId;
    }

}
