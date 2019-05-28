package cn.itcast.service.impl;

import cn.itcast.common.PageResult;
import cn.itcast.interceptor.CurrentUserHolder;
import cn.itcast.mapper.CourseMapper;
import cn.itcast.pojo.Course;
import cn.itcast.pojo.User;
import cn.itcast.service.CourseService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseMapper courseMapper;

    @Override
    public void add(Course course) {
        course.setCreatetime(new Date());
        course.setUpdatetime(new Date());

        User user = CurrentUserHolder.getUser();
        course.setCreateuser(user.getId());
        course.setUpdateuser(user.getId());

        courseMapper.add(course);
    }

    @Override
    public void update(Course course) {
        User user = CurrentUserHolder.getUser();
        course.setUpdateuser(user.getId());

        course.setUpdatetime(new Date());

        courseMapper.update(course);
    }

    @Override
    public void delete(Integer id) {
        courseMapper.delete(id);
    }

    @Override
    public PageResult<Course> search(Course course, Integer page, Integer pageSize) {

        if(page == null){
            page = 1;
        }
        if(pageSize == null){
            pageSize = 10;
        }

        PageHelper.startPage(page,pageSize);
        Page p = (Page) courseMapper.findList(course);

        return new PageResult<Course>(p.getTotal(),p.getResult());
    }

    @Override
    public Course findOne(Integer id) {
        return courseMapper.findOne(id);
    }
}
