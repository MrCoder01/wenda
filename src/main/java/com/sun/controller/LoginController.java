package com.sun.controller;

import com.sun.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * Created by ty on 2017/3/29.
 */
@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    UserService userService;

    @RequestMapping(path = {"/reg"}, method = {RequestMethod.POST})
    public String register(Model model,
                           @RequestParam("username") String username,
                           @RequestParam("password") String password,
                           //@RequestParam("headUrl") String headUrl,
                           @RequestParam(value = "next", required = false) String next,
                           HttpServletResponse response) {

        Map<String, String> map = userService.register(username, password);
        try {
            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket"));
                cookie.setPath("/");
                response.addCookie(cookie);
                if(!StringUtils.isBlank(next)){
                    return "redirect:" + next;
                }
            } else {
                model.addAttribute("msg", map.get("msg"));
                return "login";
            }

        } catch (Exception e) {
            logger.info("注册异常：" + e.getMessage());
            model.addAttribute("msg", "服务器错误");
            return "login";
        }
        model.addAttribute("msg", "注册成功");
        return "redirect:/";
    }

    @RequestMapping(path = {"/reglogin"}, method = {RequestMethod.GET})
    public String reglogin(Model model,
                           @RequestParam(value = "next", required = false) String next) {

        model.addAttribute("next", next);
        return "login";
    }

    @RequestMapping(path = {"/login"}, method = {RequestMethod.POST})
    public String login(Model model,
                        @RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam(value = "remeberme", defaultValue = "false") boolean remeberme,
                        @RequestParam(value = "next", required = false) String next,
                        HttpServletRequest request,
                        HttpServletResponse response) {

        Map<String, String> map = userService.login(username, password);
        try {
//            测试filter
//            HttpSession session = request.getSession();
//            session.setAttribute("user", userService.getByName(username));

            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket"));
                cookie.setPath("/");
                response.addCookie(cookie);
                if(!StringUtils.isBlank(next)){
                    return "redirect:" + next;
                }
            } else {
                model.addAttribute("msg", map.get("msg"));
                return "login";
            }

        } catch (Exception e) {
            model.addAttribute("msg", "服务器错误");
            return "login";
        }
        return "redirect:/";
    }

    @RequestMapping(path = {"/logout"})
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/";
    }
}
