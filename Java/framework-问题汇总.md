# 问题汇总

## 1. Mybatis

### 1.1 day01

#### 补充讲解

1. 回顾了 web 阶段所学的知识点，点出 mybatis 框架的位置
2. 特殊符号的转义和 CDATA 块
3. log4j 配置输出 mapper 的 sql 日志
4. 利用列别名解决查询时字段和属性名不一致问题
5. 利用 `<setting name="mapUnderscoreToCamelCase" value="true"/>` 解决查询时字段和属性名不一致问题
6. mapper 方法如果传递多个参数怎么解决（map 或 实体对象）

### 1.2 day02

#### 补充讲解

1. 接口 mapper 的参数有多个时，怎么解决（map 或 实体对象 或 @Param 注解）
2. 强调 接口 mapper 的方法名要唯一
3. 补充 #{} 与 ${} 的区别

#### 问题

自定义类型转换器，此案例是在 day02 讲解（当时没有问题），但会影响 day03 的练习

```java
public class DateTypeHandler extends BaseTypeHandler<Date> {
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, Date date, JdbcType jdbcType) throws SQLException {
        long time = date.getTime();
        preparedStatement.setLong(i,time);
    }
    public Date getNullableResult(ResultSet resultSet, String s) throws SQLException {
        long aLong = resultSet.getLong(s);
        Date date = new Date(aLong);
        return date;
    }
    public Date getNullableResult(ResultSet resultSet, int i) throws SQLException {
        long aLong = resultSet.getLong(i);
        Date date = new Date(aLong);
        return date;
    }
    public Date getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        long aLong = callableStatement.getLong(i);
        Date date = new Date(aLong);
        return date;
    }
}
```

在核心配置文件中配置：

```xml
<typeHandlers>
    <package name="com.itheima.handler"/>
</typeHandlers>
```

在 user 表中有 `birthday bigint` 字段，User 对象中有 `Date birthday` 属性
在 order 表中有 `ordertime datetime` 字段， Order 对象中有 `Date ordertime` 属性

#### 错误现象

用户表中的长整型会被正确转换为日期对象，但订单表中的 datetime 总是转换为 1970 年的时间

#### 原因

因为类型转换器尝试将订单表的 datetime 按长整型处理，转换为 java 日期（2019看做了长整型，后面的月日等被忽略了）

#### 解决

* 方法1，建议统一日期格式，要么都用长整型，要么都用 datetime（建议以后不用 bigint -> date 作为类型转换器的例子）
* 方法2，将类型转换器加在字段级别，而不是全局配置，但要注意无论查询、还是新增都要增加字段级别的 typeHandler 配置，见下面配置

```xml
<insert id="save" parameterType="user">
    insert into user values(#{id},#{username},#{password},#{birthday,typeHandler=com.itheima.handler.DateTypeHandler},'')
</insert>
<resultMap id="orderMap" type="order">
    <id column="oid" property="id"></id>
    <result column="ordertime" property="ordertime"></result>
    <result column="total" property="total"></result>
    <association property="user" javaType="user">
        <id column="uid" property="id"></id>
        <result column="username" property="username"></result>
        <result column="password" property="password"></result>
        <result column="birthday" property="birthday" typeHandler="com.itheima.handler.DateTypeHandler"></result>
    </association>
</resultMap>
```

## 2. Spring

### 2.1 spring 第一天

* 需要注意视频中没有讲解 IOC 概念，需要适当补充

### 2.2 spring 第二天

* 补充了 BeanPostProcessor，利用它生成代理，对 userService 做功能增强


### 2.3 spring 第三天

分析了Cglib 中 Method 和 MethodProxy 的执行性能

总结了 aop 的工作流程
```
阶段1： spring容器启动时
1) 扫描到切面,检查每个切点表达式
2) 根据切点表达式确定目标，创建代理对象
3) 之后的依赖注入，注入就是代理对象了

阶段2： 调用代理对象的方法
1) 再次检查切点表达式
2) 如果切点与目标方法匹配就执行通知方法，并且执行目标方法（结合通知和目标）
3) 如果切点与目标方法不匹配，就只执行目标方法
```

### 2.4 spring 第四天

* 补充了事务隔离级别中，Required, Supports, Requires_new 的几种组合调用对事务的影响
* 补充了事务回滚处理时的一些注意事项
	* spring 事务管理是如果出现的是 运行时异常会回滚事务，如果出现的是检查异常(Exception) 默认仍会提交事务
	* 如果希望回滚检查异常，在事务属性上添加 rollback-for="java.lang.Exception"
	* 在 spring 的事务管理中，业务方法执行时，如果出现异常，应当把异常继续向上抛，而不应当自己捉住不处理
* 教给学员配置 log4j 以观察事务的执行情况，注意在 spring 5 的版本中，必须通过 slf4j 来使用 log4j
