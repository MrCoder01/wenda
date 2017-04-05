package com.sun.service;

import com.sun.dao.CommentDao;
import com.sun.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * Created by ty on 2017/3/31.
 */
@Service
public class CommentService {
    @Autowired
    CommentDao commentDao;

    @Autowired
    SensitiveService sensitiveService;

    public int addComment(Comment comment){
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveService.filter(comment.getContent()));
        return commentDao.addComment(comment)>0?comment.getId():0;
    }

    public List<Comment> getCommentByEntity(int entityId, int entityType){
        return commentDao.selectCommentByEntity(entityId,entityType);
    }

    public Comment getById(int id){
        return commentDao.getById(id);
    }

    public int getCommentCountByEntity(int entityId,int entityType){
        return commentDao.getCommentCountByEntity(entityId,entityType);
    }

    public int getUserCommentCount(int userId){
        return commentDao.getUserCommentCount(userId);
    }

    public boolean deleteCommentById(int commentId,int status){
        return  commentDao.updateCommentStatus(commentId,status)>0;
    }
}
