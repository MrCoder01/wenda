package com.sun.async.handler;

import com.sun.async.EventHandler;
import com.sun.async.EventModel;
import com.sun.async.EventType;
import com.sun.model.EntityType;
import com.sun.model.Message;
import com.sun.model.User;
import com.sun.service.MessageService;
import com.sun.service.UserService;
import com.sun.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by ty on 2017/4/4.
 */
@Component
public class FollowHandler implements EventHandler{
    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Override
    public void doHanler(EventModel eventModel) {
        Message message = new Message();
        message.setFromId(WendaUtil.SYSTEM_USERID);
        message.setToId(eventModel.getEntityOwnerId());
        message.setCreatedDate(new Date());
        User user = userService.getById(eventModel.getActorId());

        if(eventModel.getEntityType()== EntityType.ENTITY_QUESTION){
            message.setContent("用户" + user.getName()
                    + "关注了你的问题,http://127.0.0.1:8080/question/" + eventModel.getEntityId());
        }else if(eventModel.getEntityType()== EntityType.ENTITY_USER){
            message.setContent("用户" + user.getName()
                    + "关注了你,http://127.0.0.1:8080/user/" + eventModel.getActorId());

        }
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventType() {
        return Arrays.asList(EventType.FOLLOW);
    }
}
