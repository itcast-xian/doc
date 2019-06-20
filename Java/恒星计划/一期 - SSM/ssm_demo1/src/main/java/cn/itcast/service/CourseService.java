package cn.itcast.service;

import cn.itcast.common.PageResult;
import cn.itcast.pojo.Course;

public interface CourseService {

    public void add(Course course);

    public void update(Course course);

    public void delete(Integer id);

    public PageResult<Course> search(Course course,Integer page , Integer pageSize);

    public Course findOne(Integer id);
}
