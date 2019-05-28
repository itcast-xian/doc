package cn.itcast.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationLog {
    //ID
    private Integer id;
    //操作的类
    private String operateClass;
    //操作的方法
    private String operateMethod;
    //参数值 , 键值对形式
    private String paramAndValue;
    //返回值类型
    private String returnClass;
    //返回值
    private String returnValue;
    //操作耗时
    private Long costTime;
    //操作人
    private Integer operateUser;
    //操作时间
    private Date operateTime;

}

