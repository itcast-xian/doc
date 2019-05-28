package cn.itcast.pojo;

import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Emp {

    private Integer id;
    private String name;
    private Integer jobId;
    private Integer mgr;
    private Date joindate;
    private Double salary;
    private Double bonus;
    private Integer deptId;

}
