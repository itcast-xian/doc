package cn.itcast.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    private Integer id;
    private String name;
    private String teacherName;
    private Double price;
    private String description;
    private Date createtime;
    private Date updatetime;
    private Integer createuser;
    private Integer updateuser;

}
