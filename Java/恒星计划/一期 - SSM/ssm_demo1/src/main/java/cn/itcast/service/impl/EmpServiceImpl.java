package cn.itcast.service.impl;

import cn.itcast.mapper.EmpMapper;
import cn.itcast.pojo.Emp;
import cn.itcast.service.EmpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EmpServiceImpl implements EmpService {

    @Autowired
    private EmpMapper empMapper;

    @Override
    public Emp findById(Integer id) {
        return empMapper.findById(id);
    }

}
