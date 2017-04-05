package com.sun.service;

import com.sun.dao.QuestionDao;
import com.sun.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * Created by ty on 2017/3/28.
 */
@Service
public class QuestionService {

    @Autowired
    QuestionDao questionDao;

    @Autowired
    SensitiveService sensitiveService;

    public List<Question> getLastQuestion(int userId, int offset, int limit){
        return  questionDao.selectQuestion(userId,offset,limit);
    }

    public int addQuestion(Question question){
        //HTML过滤
        question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));
        question.setContent(HtmlUtils.htmlEscape(question.getContent()));
        //敏感词过滤
        question.setTitle(sensitiveService.filter(question.getTitle()));
        question.setContent(sensitiveService.filter(question.getContent()));
        return questionDao.addQuestion(question)>0?question.getId():0;
    }

    public Question getById(int qid){
        return questionDao.getById(qid);
    }

    public boolean updateCount(int id,int count){
        return questionDao.updataCount(id,count)>0;
    }
}
