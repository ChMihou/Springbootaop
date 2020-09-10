package com.cmh.aop.controller;

import com.cmh.aop.annotation.PointcutAnnotation;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.UUID;

@Controller
@RequestMapping("/hello")
public class HelloController {

    @RequestMapping
    public String test(Model model, HttpSession session) {
        String token = UUID.randomUUID().toString();
        session.setAttribute("token",token);
        System.out.println(token);
        return "/test/hello";
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    @PointcutAnnotation
    public String hello(@RequestParam String name) {
        return "Hello " + name;
    }
}