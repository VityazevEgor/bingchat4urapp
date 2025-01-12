package com.bingchat4urapp_server.bingchat4urapp_server.Controlers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class MiscController {
    @GetMapping("lms")
    public ModelAndView getInvisiblePromtPage() {
        return new ModelAndView("misc/invisiblePromt");
    }
}
