package com.sun.async.handler;

import com.sun.async.EventHandler;
import com.sun.async.EventModel;
import com.sun.async.EventType;
import com.sun.controller.CommentController;
import com.sun.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Created by nowcoder on 2016/8/28.
 */
@Component
public class AddQuestionHandler implements EventHandler {
    private static final Logger logger = LoggerFactory.getLogger(AddQuestionHandler.class);
    @Autowired
    SearchService searchService;

    @Override
    public void doHanler(EventModel model) {
        try {
            searchService.indexQuestion(model.getEntityId(),
                    model.getExts("title"), model.getExts("content"));
        } catch (Exception e) {
            logger.error("增加题目索引失败");
        }
    }

    @Override
    public List<EventType> getSupportEventType() {
        return Arrays.asList(EventType.ADD_QUESTION);
    }
}
