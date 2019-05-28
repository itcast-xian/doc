package cn.itcast.controller;

import cn.itcast.common.PageResult;
import cn.itcast.common.Result;
import cn.itcast.log.OperateLog;
import cn.itcast.pojo.Course;
import cn.itcast.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/course")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @RequestMapping("/add")
    @OperateLog
    public Result add(@RequestBody Course course){
        courseService.add(course);
        return new Result(true,"操作成功");
    }

    @RequestMapping("/update")
    @OperateLog
    public Result update(@RequestBody Course course){
        courseService.update(course);
        return new Result(true,"操作成功");
    }

    @RequestMapping("/findOne")
    @OperateLog
    public Course findOne(Integer id){
        return courseService.findOne(id);
    }

    @RequestMapping("/delete")
    @OperateLog
    public Result delete(Integer id){
        courseService.delete(id);
        return new Result(true,"操作成功");
    }

    @RequestMapping("/search")
    public PageResult<Course> search(@RequestBody Course course, Integer page , Integer pageSize){
        return courseService.search(course,page,pageSize);
    }

}
