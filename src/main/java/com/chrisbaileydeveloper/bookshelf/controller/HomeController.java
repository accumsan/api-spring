package com.chrisbaileydeveloper.bookshelf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by minhdao on 18/02/16.
 */

@Controller
public class HomeController {
    @RequestMapping("/home")
    public String home() {
        return "home";
    }
}
