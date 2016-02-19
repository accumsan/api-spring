package com.minhdd.app.controller;

import com.minhdd.app.config.AppProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by minhdao on 18/02/16.
 */

@Controller
public class HomeController {
    @RequestMapping("/home")
    public String home(Model model) {
        model.addAttribute("message", AppProperties.getInstance().getProperty(AppProperties.PROP_KEYS.TEST));
        return "home";
    }
}
