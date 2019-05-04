# 返回值和异常的问题

## 1. 变量两种赋值方法
下面两种哪种更好，请说明理由？
```java
public static void test1() {
    Emp emp = null;
    for (int i = 0; i < 10; i++) {
        emp = new Emp();
    }
}

public static void test2() {
    for (int i = 0; i < 10; i++) {
        Emp emp = new Emp();
    }
}
```
我的理解
* 两种占用栈帧的 slot 个数都一样，不存在第二种占用更多内存的问题
* 变量的作用范围应当尽可能小，所以推荐第二种

## 2. 返回值的位置
```java
public List<Emp> findAll(){
	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;
	List<Emp> list = null;
	try {
		conn = JDBCUtils.getConnection();
		String sql = "select * from emp";
		stmt = conn.createStatement();
		rs = stmt.executeQuery(sql);
		Emp emp = null;
		list = new ArrayList<Emp>();
		while(rs.next()){
			// ... 封装至 Emp 对象
			list.add(emp);
		}
        return list;
	} catch (SQLException e) {
		e.printStackTrace(); // 问题1. 如果异常出现，仍然会执行下面的 return，合理吗？
        // 问题3. 一旦异常被捕获，调用者不知道异常的发生，仍然认为返回值可用，有没有可能返回一个 残缺的 list
	}finally {
		JDBCUtils.close(rs, stmt, conn);
	}
	return list; // 问题2： 可能返回 null 集合，好吗？
}
```

我一般的写法：
```java
public List<Emp> findAll(){
	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;
	try {
		conn = JDBCUtils.getConnection();
		String sql = "select * from emp";
		stmt = conn.createStatement();
		rs = stmt.executeQuery(sql);		
		List<Emp> list = new ArrayList<Emp>();
		while(rs.next()){
            Emp emp = new Emp();
			// ... 封装至 Emp 对象
			list.add(emp);
		}
        return list; // 只有正常才返回值
	} catch (SQLException e) {
        // 出现异常，包装后抛出，或其他自定义未检查异常都可以
        // 好处，不会丢失异常信息，不强制处理异常，同时通知调用者，出错了
		throw new RuntimeException(e);
	}finally {
		JDBCUtils.close(rs, stmt, conn);
	}
    // 不会在 finally 之后见到 return，增强可读性
}
```

