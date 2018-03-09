package com.sun.controller;

import com.sun.async.EventModel;
import com.sun.async.EventProducer;
import com.sun.async.EventType;
import com.sun.model.*;
import com.sun.service.*;
import com.sun.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ty on 2017/3/30.
 */
@Controller
public class QuestionController {

    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);

    @Autowired
    QuestionService questionService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;

    @Autowired
    CommentService commentService;

    @Autowired
    LikeService likeService;

    @Autowired
    FollowService followService;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(value = "/question/add",method = {RequestMethod.POST})
    //不是返回页面，添此次注解
    @ResponseBody
    public String addQuestion(@RequestParam("title") String title,
                              @RequestParam("content") String content){

        try{
            Question question = new Question();
            question.setTitle(title);
            question.setContent(content);
            question.setCreatedDate(new Date());
            if(hostHolder.getUser()==null){
                question.setUserId(WendaUtil.ANONYMOUS_USERID);
            }else{
                question.setUserId(hostHolder.getUser().getId());
            }
            questionService.addQuestion(question);
            eventProducer.fireEvent(new EventModel(EventType.ADD_QUESTION)
                    .setActorId(question.getUserId()).setEntityId(question.getId())
                    .setExts("title", question.getTitle()).setExts("content", question.getContent()));
            //返回json形式的标志位给前台，是否添加成功（0成功）
            return WendaUtil.getJSONString(0);

        }catch (Exception e ){
            logger.error("增加题目失败" + e.getMessage());
        }

        return WendaUtil.getJSONString(1, "失败");
    }

    @RequestMapping(value = "/question/{qid}")
    public String questionDetails(Model model,
                                  @PathVariable("qid") int qid){

        try{
            Question question = questionService.getById(qid);
            //问题信息
            model.addAttribute("question",question);
            //model.addAttribute("user",userService.getById(question.getUserId()));

            //问题对应的评论
            List<Comment> commentList = commentService.getCommentByEntity(qid, EntityType.ENTITY_QUESTION);
            List<ViewObject> comments = new ArrayList<>();
            for (Comment c:commentList) {
                ViewObject vo = new ViewObject();
                vo.set("comment",c);
                vo.set("user",userService.getById(c.getUserId()));
                if(hostHolder.getUser()==null){
                    vo.set("liked",0);
                }else{
                    vo.set("liked",likeService.getLikeStatus(hostHolder.getUser().getId(),EntityType.ENTITY_COMMENT,c.getId()));
                }
                vo.set("likeCount",likeService.getLikeCount(EntityType.ENTITY_COMMENT,c.getId()));
                comments.add(vo);
            }
            model.addAttribute("comments",comments);

            // 获取关注的用户信息
            List<ViewObject> followUsers = new ArrayList<>();
            List<Integer> users = followService.getFollowers(EntityType.ENTITY_QUESTION,qid,0,10);
            for(Integer uid:users){
                ViewObject vo = new ViewObject();
                User u = userService.getById(uid);
                if(u ==null){
                    continue;
                }
                vo.set("name", u.getName());
                vo.set("headUrl", u.getHeadUrl());
                vo.set("id", u.getId());
                followUsers.add(vo);
            }
            model.addAttribute("followUsers", followUsers);
            if (hostHolder.getUser() != null) {
                model.addAttribute("followed", followService.isFollower(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, qid));
            } else {
                model.addAttribute("followed", false);
            }
            return "detail";
        }catch (Exception e ){
            //供出错查找，否则出错看不到详细信息
            logger.error("查看题目失败" + e.getMessage());
            return "/";
        }
    }
}
