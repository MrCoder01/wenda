package com.sun.interceptor;

import com.sun.dao.LoginTicketDao;
import com.sun.dao.UserDao;
import com.sun.model.HostHolder;
import com.sun.model.LoginTicket;
import com.sun.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Created by ty on 2017/3/29.
 */
@Component
public class PassportInterceptor implements HandlerInterceptor {

    @Autowired
    LoginTicketDao loginTicketDao;

    @Autowired
    UserDao userDao;

    @Autowired
    HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String ticket=null;
        if(httpServletRequest!=null){
            // Cookie是key——value对
            if(httpServletRequest.getCookies()!=null){
                for(Cookie c:httpServletRequest.getCookies()){
                    if(c.getName().equals("ticket")){
                        ticket=c.getValue();
                        break;
                    }
                }
            }
        }

        if(ticket!=null){
            LoginTicket loginTicket = loginTicketDao.selectByTicket(ticket);
            if(loginTicket==null || loginTicket.getStatus()==1 || loginTicket.getExpired().before(new Date())){
                //return fa;se之后所有全不会执行，直接回客户端
                return true;
            }

            User user = userDao.getById(loginTicket.getUserId());
            hostHolder.setUser(user);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        if(modelAndView != null && hostHolder.getUser() != null){
            modelAndView.addObject("user",hostHolder.getUser());
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        hostHolder.clear();
    }
}
