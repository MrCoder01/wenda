package com.sun.service;

import com.sun.dao.LoginTicketDao;
import com.sun.dao.UserDao;
import com.sun.model.LoginTicket;
import com.sun.model.User;
import com.sun.util.WendaUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by ty on 2017/3/28.
 */
@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private LoginTicketDao loginTicketDao;

    public User getByName(String name){
        return userDao.selectByName(name);
    }

    public User getById(int id){
        User user = userDao.getById(id);
        return  user;
    }

    public Map<String,String> register(String name, String password){

        Map<String,String> map = new HashMap<>();

        //StringUtils.isBlank(" ")==true; StringUtils.isEmpty(" ")==false
        if(StringUtils.isBlank(name)){
            map.put("msg","用户名不能为空");
            return map;
        }

        if(StringUtils.isBlank(password)){
            map.put("msg","密码不能为空");
            return map;

        }

        User user = userDao.selectByName(name);
        if(user!=null){
            map.put("msg","用户名已存在");
            return map;
        }

        user = new User();
        user.setName(name);
        user.setHeadUrl(String.format("/images/res/%d.jpg", new Random().nextInt(12)));
        //md5加密
        user.setSalt(UUID.randomUUID().toString().substring(0,5));
        user.setPassword(WendaUtil.MD5(password+user.getSalt()));
        userDao.addUser(user);

        map.put("ticket",addTicket(user.getId()));
        return  map;
    }

    public Map<String,String> login(String name, String password){

        Map<String,String> map = new HashMap<>();

        if(StringUtils.isBlank(name)){
            map.put("msg","用户名不能为空");
            return map;
        }

        if(StringUtils.isBlank(password)){
            map.put("msg","密码不能为空");
            return map;

        }

        User user = userDao.selectByName(name);
        if(user==null){
            map.put("msg","用户不存在，请注册");
            return map;
        }
        if(!WendaUtil.MD5(password+user.getSalt()).equals(user.getPassword())){
            map.put("msg","密码错误");
            return map;
        }


        map.put("ticket",addTicket(user.getId()));
        return  map;
    }

    private String addTicket(int userId){

        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(userId);
        ticket.setStatus(0);
        Date now= new Date();
        now.setTime(3600*24*100+now.getTime());
        ticket.setExpired(now);
        ticket.setTicket(UUID.randomUUID().toString().replaceAll("-",""));
        loginTicketDao.addTicket(ticket);

        return  ticket.getTicket();
    }

    public void logout(String ticket){
        loginTicketDao.updateStatus(ticket,1);
    }
}
