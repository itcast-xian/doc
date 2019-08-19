# 问题汇总

## 1. MyBatis day01

### 1.1 补充讲解

#### 1. ORM 概念

#### 2. 特殊符号转义

   ```java
   <select id="findByLtId" parameterType="int" resultType="user">
       <!-- 利用转义字符，缺点是可读性差 -->
       <!--select * from user where id &lt; #{n} -->
   
       <![CDATA[ select * from user where id < #{n} ]]>
   </select>
   ```

   

#### 3. 模糊查询

   ```java
<select id="findByUsernameLike" parameterType="string" resultType="user">
    <!-- mysql concat可以接收多个参数值，但不通用 -->
    <!-- select * from user where username like concat('%', #{n}, '%') -->

    <!-- 两次调用 concat，更为通用 -->
    select * from user where username like concat('%', concat(#{n}, '%'))
</select>
   ```

   

#### 3. sql 日志

log4j 配置输出 mapper 的 SQL 日志

   ```
log4j.logger.<mapper 的 namespace>=debug
   ```

   

#### 4. 查询时字段和属性名不一致

   * 数据库的命名规则一般是用下划线分隔不同的单词，而 Java 的命名规则是用驼峰命名法

   * 解决方法1：查询字段上使用列别名

   * 解决方法2：利用 `<setting name="mapUnderscoreToCamelCase" value="true"/>` 

     

#### 5. xml mapper 方法传递多个参数

   * 传递 map，sql 中使用 #{ key } 获取值
   * 传递实体对象，sql 中使用 #{ 属性名 } 获取值



#### 6. 新增获取自增主键值

```xml
<insert id="" parameterType="" useGeneratedKeys="true" keyProperty="主键属性">
</insert>
```



## 2. MyBatis day02

### 2.1 补充讲解

#### 1. 接口 mapper 方法传递多个参数

* 传递 map，sql 中使用 #{ key } 获取值

* 传递实体对象，sql 中使用 #{ 属性名 } 获取值

* 接口参数上添加 @Param("xxxx") 注解，sql 中使用 #{ xxx } 获取值

* sql 语句中使用 #{ param1 }，#{ param2 } 获取值（我没有讲）

* sql 语句中使用 #{ arg0 }，#{ arg1 } 获取值（我没有讲）

  

#### 2. 接口 mapper 的方法名要唯一

学会查看异常

```
java.lang.IllegalArgumentException: Mapped Statements collection already contains value for 
```



#### 3. 补充 #{ } 与 ${ } 的区别

* `#{ }` 采用了 ？占位符方式，而 `${ }` 采用了直接拼接字符串方式

* `#{ }` 相对安全，而 `${ }` 有 SQL 注入风险

* `#{ }` 内部不能运算，而 `${ }` 可以执行简单运算（OGNL）

*  `${ }` 还能用在 MyBatis 核心配置文件中

  

#### 4. 分页的两种方式

* 逻辑分页 - 底层采用了 Jdbc可滚动结果集。性能差，通用性好

  ```java
  // 通过 new RowBounds(offset, limit) 调用
  public List<User> findByPage1(RowBounds rb);
  ```

  ```xml
  <select id="findByPage1" resultType="user">
      select * from user
  </select>
  ```

  

* 物理分页 - 直接使用 SQL 分页。性能好，通用性差。

  ```java
  List<User> findByPage2(@Param("o") int offset, @Param("l") int limit);
  ```

  ```xml
  <select id="findByPage2" resultType="user">
      select * from user limit #{o}, #{l}
  </select>
  ```

  



### 2.2 问题

#### 1. 自定义类型转换器问题

**问题描述**

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

**错误现象**

用户表中的长整型会被正确转换为日期对象，但订单表中的 datetime 总是转换为 1970 年的时间

**原因**

因为类型转换器尝试将订单表的 datetime 按长整型处理，转换为 java 日期（2019看做了长整型，后面的月日等被忽略了）

**解决**

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



## 4. spring day01

* 需要注意视频中没有讲解 IOC 概念，需要适当补充

## 5. spring day02

* 补充了 BeanPostProcessor，利用它生成代理，对 userService 做功能增强

## 6. spring day03

### 6.1 补充讲解

#### 1. 分析了 Cglib 的执行性能

Cglib 中 Method 和 MethodProxy 相比，MethodProxy 性能更好（没有反射）



#### 2. AOP 的工作流程

**阶段1： spring容器启动时**

1. 扫描到切面，检查每个切点表达式
2. 根据切点表达式确定目标，创建代理对象
3. 之后的依赖注入，注入就是代理对象了

**阶段2： 调用代理对象的方法时**

1. 再次检查切点表达式
2. 如果切点与目标方法匹配就执行通知方法，并且执行目标方法（结合通知和目标）
3. 如果切点与目标方法不匹配，就只执行目标方法



#### 3. 补充切面相关知识

* 切点表达式有用的还有 `@annotation(注解.class)`
* 切面执行的顺序问题：使用 `<aop:aspect order=""/>` 或 @Order 注解控制顺序

## 7. spring day04

### 7.1 补充讲解

#### 1. 传播行为的理解

* 补充了事务隔离级别中，Required, Supports, Requires_new 的几种组合调用对事务的影响



#### 2. 事务回滚注意事项

* spring 事务管理是如果出现的是【运行时异常】会回滚事务，如果出现的是【检查异常】 默认仍会提交事务
* 如果希望回滚检查异常，在事务属性上添加 rollback-for="java.lang.Exception"
* 在 spring 的事务管理中，业务方法执行时，如果出现异常，应当把异常继续向上抛，而不应当自己捉住不处理

* 教给学员配置 log4j 以观察事务的执行情况，注意在 spring 5 的版本中，必须通过 slf4j 来使用 log4j
