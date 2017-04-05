package com.sun.controller;

import com.sun.model.HostHolder;
import com.sun.model.Message;
import com.sun.model.User;
import com.sun.model.ViewObject;
import com.sun.org.apache.xpath.internal.operations.Mod;
import com.sun.service.MessageService;
import com.sun.service.UserService;
import com.sun.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ty on 2017/3/31.
 */
@Controller
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    HostHolder hostHolder;

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @RequestMapping(path = {"/msg/list"}, method = {RequestMethod.GET})
    public String addMessage(Model model) {
        try {
            if(hostHolder.getUser()==null){
                return "redirect:/reglogin";
            }
            int localUserId = hostHolder.getUser().getId();
            List<Message> conversationList = messageService.getConversationList(localUserId, 0, 10);
            List<ViewObject> conversations = new ArrayList<ViewObject>();
            for (Message message : conversationList) {
                ViewObject vo = new ViewObject();
                vo.set("message", message);
                int targetId = message.getFromId() == localUserId ? message.getToId() : message.getFromId();
                vo.set("user", userService.getById(targetId));
                vo.set("unread", messageService.getConversationUnreadCount(localUserId, message.getConversationId()));
                conversations.add(vo);
            }
            model.addAttribute("conversations", conversations);

        }catch (Exception e){
            logger.error("查看消息失败"+e.getMessage());
        }
        return "letter";
    }

    @RequestMapping(path = {"/msg/detail"}, method = {RequestMethod.GET})
    public String addMessage(Model model,@RequestParam("conversationId") String conversationId) {
        try{
            List<Message> messageList = messageService.getConversationDetail(conversationId,0,10);
            List<ViewObject> messages = new ArrayList<>();
            for (Message m:messageList) {
                ViewObject vo = new ViewObject();
                vo.set("message",m);
                vo.set("user",userService.getById(m.getFromId()));
                messages.add(vo);
            }
            model.addAttribute("messages",messages);
        }catch (Exception e ){
            logger.error("读取消息失败"+e.getMessage());
        }

        return "letterdetail";
    }

    @RequestMapping(path = {"/msg/addMessage"}, method = {RequestMethod.POST})
    @ResponseBody
    public String addMessage(@RequestParam("toName") String toName,
                             @RequestParam("content") String content) {
        try {
            if (hostHolder.getUser() == null) {
                return WendaUtil.getJSONString(999, "未登录");
            }

            User user = userService.getByName(toName);
            if (user == null) {
                return WendaUtil.getJSONString(1, "用户不存在");
            }

            Message message = new Message();
            message.setCreatedDate(new Date());
            message.setFromId(hostHolder.getUser().getId());
            message.setToId(user.getId());
            message.setContent(content);
            messageService.addMessage(message);
            return WendaUtil.getJSONString(0);

        } catch (Exception e) {
            logger.error("发送消息失败" + e.getMessage());
            return WendaUtil.getJSONString(1, "发信失败");
        }
    }
}
