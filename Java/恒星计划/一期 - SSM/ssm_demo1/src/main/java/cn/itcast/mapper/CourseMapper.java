package cn.itcast.mapper;

import cn.itcast.common.PageResult;
import cn.itcast.pojo.Course;

import java.util.List;

public interface CourseMapper {

    public void add(Course course);

    public void update(Course course);

    public void delete(Integer id);

    public List<Course> findList(Course course);

    public Course findOne(Integer id);
}
