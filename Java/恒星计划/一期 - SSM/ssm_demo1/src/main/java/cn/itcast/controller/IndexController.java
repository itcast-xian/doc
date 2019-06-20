package cn.itcast.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/index")
public class IndexController {


    @RequestMapping("/{path}")
    public String index(@PathVariable("path") String path){
        return path;
    }

}
