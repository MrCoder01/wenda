package com.sun.async.handler;

import com.sun.async.EventHandler;
import com.sun.async.EventModel;
import com.sun.async.EventType;
import com.sun.model.Message;
import com.sun.model.User;
import com.sun.service.MessageService;
import com.sun.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by ty on 2017/4/2.
 */
@Component
public class LikeHandler implements EventHandler{
    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Override
    public void doHanler(EventModel eventModel) {
        Message message = new Message();
        message.setFromId(eventModel.getActorId());
        message.setToId(eventModel.getEntityOwnerId());
        message.setCreatedDate(new Date());
        User user = userService.getById(eventModel.getActorId());
        message.setContent("用户"+user.getName()+"赞了你的评论,http://127.0.0.1:8080/question/"
                + eventModel.getExts("questionId"));
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventType() {
        return Arrays.asList(EventType.LIKE);
    }


}
