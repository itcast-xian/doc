# 视频中提到 PreparedStatement 会比Statement 更高效率但是并没有说原因

## 1. MySQL 中 SQL 的执行流程
默认情况下，通过jdbc执行一条sql语句流程如下：
1. 将sql语句从客户端程序发送给数据库服务器
2. 由命令解析器进行`词法分析`、`语法分析`、生成`解析树`
3. 如果是查询，还要生成`查询计划`，对sql执行进行优化
4. 由访问控制模块`检查权限`、生成新的`解析树`
5. 进入表管理模块，打开对应的表文件
6. 调用存储引擎，执行
7. 将结果返回给客户端程序

其中2. 3. 4. 步每次都要执行

## 2. 预编译优化意义
* 在 mysql 内部可以通过预编译，减少sql语句编译和优化的次数，比如
```sql
select * from student where id = 1001;
select * from student where id = 1002;
```
会被视为两条不同的sql，编译和优化都会执行两次

mysql 支持预编译sql：
```sql
prepare sql1 from 'select * from student where sid = ?';

set @param:=1001;
execute sql1 using @param;

set @param:=1002;
execute sql1 using @param;
```
其中 sql1 会被视为同一条sql，只会执行一次编译和优化

## 3. MySQL jdbc 驱动对预编译的支持
但是！！ mysql 的 jdbc 程序就算用了 PreparedStatement, 默认也不会做上述优化，因此效率与 Statement 没有区别。

jdbc中要利用`预编译`的功能，需要使用`PreparedStatement`，普通`Statement`不行，另外对于MySQL来说，要在jdbc 连接字符串中加入参数：`useServerPrepStmts=true&cachePrepStmts=true&prepStmtCacheSize=25`
其中：
* `useServerPrepStmts=true`是开启MySQL的服务器预编译功能，即`PreparedStatement对象`会利用`prepare`语句
* `cachePrepStmts=true`是同一个连接的多个`PreparedStatement对象`能够被缓存，否则一旦`PreparedStatement对象`关闭，则下一个`PreparedStatement对象`执行相同sql时，还是会重新执行`prepare`
* `prepStmtCacheSize=n` 是表示`cachePrepStmts=true`时，能够缓存的语句个数，默认为25

> 注意，一些连接池如 Druid 采用了自行管理和缓存 PreparedStatement 对象的方式，

## 4. 调试和证明方法
启用 mysql 日志，修改 my.ini 文件：
```
[mysqld]
general-log=1
general_log_file="E:\mysql-8.0.12-winx64\data\mysql.log"
```
> 注意，日志写入有时会有一些延迟，不行就通过重启 MySQL 服务来刷新

```java
// 情况1，没有启用服务器端预编译
@Test
public void test1() {
    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/day04_1", "root", "root")) {
        try (PreparedStatement stmt = conn.prepareStatement("select * from emp where id = ?")) {
            stmt.setInt(1, 1001);
            stmt.executeQuery();
            stmt.setInt(1, 1002);
            stmt.executeQuery();
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    /*
        日志中 SQL 执行的情况：
        1 Query	SET autocommit=1
        1 Query	select * from emp where id = 1001
        1 Query	select * from emp where id = 1002
        1 Quit
     */
}

// 情况2，启用服务器端预编译
@Test
public void test2() {
    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/day04_1?useServerPrepStmts=true", "root", "root")) {
        try (PreparedStatement stmt = conn.prepareStatement("select * from emp where id = ?")) {
            stmt.setInt(1, 1001);
            stmt.executeQuery();
            stmt.setInt(1, 1002);
            stmt.executeQuery();
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    /*
        日志中 SQL 执行的情况：
        2 Query	SET autocommit=1
        2 Prepare	select * from emp where id = ?
        2 Execute	select * from emp where id = 1001
        2 Execute	select * from emp where id = 1002
        2 Close stmt
        2 Quit
     */
}

// 情况3，启用服务器端预编译,但两个 PreparedStatement 分别执行sql
@Test
public void test3() {
    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/day04_1?useServerPrepStmts=true", "root", "root")) {
        try (PreparedStatement stmt = conn.prepareStatement("select * from emp where id = ?")) {
            stmt.setInt(1, 1001);
            stmt.executeQuery();
        }
        try (PreparedStatement stmt = conn.prepareStatement("select * from emp where id = ?")) {
            stmt.setInt(1, 1002);
            stmt.executeQuery();
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    /*
        日志中 SQL 执行的情况：
        3 Query	SET autocommit=1
        3 Prepare	select * from emp where id = ?
        3 Execute	select * from emp where id = 1001
        3 Close stmt
        3 Prepare	select * from emp where id = ?
        3 Execute	select * from emp where id = 1002
        3 Close stmt
        3 Quit
     */
}

// 情况4，启用服务器端预编译,缓存 PreparedStatement
@Test
public void test4() {
    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/day04_1?useServerPrepStmts=true&cachePrepStmts=true&prepStmtCacheSize=25", "root", "root")) {
        try (PreparedStatement stmt = conn.prepareStatement("select * from emp where id = ?")) {
            stmt.setInt(1, 1001);
            stmt.executeQuery();
        }
        try (PreparedStatement stmt = conn.prepareStatement("select * from emp where id = ?")) {
            stmt.setInt(1, 1002);
            stmt.executeQuery();
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    /*
        日志中 SQL 执行的情况：
        4 Query	SET autocommit=1
        4 Prepare	select * from emp where id = ?
        4 Execute	select * from emp where id = 1001
        4 Execute	select * from emp where id = 1002
        4 Quit
     */
}
```

> 几个猜想待验证：
> * 似乎不开启服务器端预编译（测试1中），PreparedStatement 就没有`关闭`动作，我的理解是根本没有利用服务器端资源，PreparedStatement 不是真正的`资源`，因此无需关闭
> * 测试4中，缓存了服务器端PreparedStatement资源，因此也没有关闭动作，应该是连接关闭时一起释放

## 5. 通过观察日志对 SQL 注入的进一步理解
```java
// 情况5，sql 注入理解，可以看到，会对整个字符串值两边加单引号，然后对内部的单引号做了转义
@Test
public void test5() {
	try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/day04_1", "root", "root")) {
		try (PreparedStatement stmt = conn.prepareStatement("select * from emp where name = ?")) {
			stmt.setString(1, "1' or '1'='1");
			stmt.executeQuery();
		}
	} catch (SQLException e) {
		e.printStackTrace();
	}
	/*
		日志中 SQL 执行的情况：
		5 Query	SET autocommit=1
		5 Query	select * from emp where name = '1\' or \'1\'=\'1'
		5 Quit
	 */
}
```
> 对 allowMultiQueries=true 也做了进一步测试，暂时没找到突破手段
> * 原始sql `select * from emp where name = ?`
> * `stmt.setString(1, "\'; drop ...");` // 想多出一个反斜杠，让jdbc加的 `\` 转义失效
> * 生成的sql `select * from emp where name = '\'; drop ...'` 自己加的 `\` 应该被忽略掉了