package com.sun.controller;

import com.sun.dao.QuestionDao;
import com.sun.dao.UserDao;
import com.sun.model.*;
import com.sun.service.CommentService;
import com.sun.service.FollowService;
import com.sun.service.QuestionService;
import com.sun.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ty on 2017/3/28.
 */
@Controller
public class IndexController {

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;

    @Autowired
    FollowService followService;

    @Autowired
    CommentService commentService;

    @Autowired
    HostHolder hostHolder;

    @RequestMapping(path={"/","/index"})
    public String index(Model model){

        List<Question> questionList = questionService.getLastQuestion(0,0,10);
        List<ViewObject> vos = new ArrayList<>();
        for(Question question:questionList){
            ViewObject vo = new ViewObject();
            vo.set("question",question);
            vo.set("user",userService.getById(question.getUserId()));
            vo.set("followCount", followService.getFollowersCount(EntityType.ENTITY_QUESTION, question.getId()));
            vos.add(vo);
        }
        model.addAttribute("vos",vos);
        return "index";
    }

    @RequestMapping(path={"/user/{userId}"})
    public String userIndex(Model model, @PathVariable("userId") int userId){
        model.addAttribute("vos", getQuestions(userId, 0, 10));

        User user = userService.getById(userId);
        ViewObject vo = new ViewObject();
        vo.set("user", user);
        vo.set("commentCount", commentService.getUserCommentCount(userId));
        vo.set("followerCount", followService.getFollowersCount(EntityType.ENTITY_USER, userId));
        vo.set("followeeCount", followService.getFolloweeCount(userId, EntityType.ENTITY_USER));
        if (hostHolder.getUser() != null) {
            vo.set("followed", followService.isFollower(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId));
        } else {
            vo.set("followed", false);
        }
        model.addAttribute("profileUser", vo);
        return "profile";
    }

    private List<ViewObject> getQuestions(int userId, int offset, int limit) {
        List<Question> questionList = questionService.getLastQuestion(userId, offset, limit);
        List<ViewObject> vos = new ArrayList<>();
        for (Question question : questionList) {
            ViewObject vo = new ViewObject();
            vo.set("question", question);
            vo.set("followCount", followService.getFollowersCount(EntityType.ENTITY_QUESTION, question.getId()));
            vo.set("user", userService.getById(question.getUserId()));
            vos.add(vo);
        }
        return vos;
    }
}
