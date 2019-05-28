package cn.itcast.controller;

import cn.itcast.pojo.Emp;
import cn.itcast.service.EmpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/emp")
public class EmpController {

    @Autowired
    private EmpService empService;

    @ResponseBody
    @RequestMapping("/findById")
    public Emp findById(Integer id){
        return empService.findById(id);
    }

    @RequestMapping("/index")
    public String index(){ // /WEB-INF/pages/1.jsp
        return "1";
    }

}
