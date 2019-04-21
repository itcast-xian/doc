# c3p0 问题

如下代码
```java
public static void main(String[] args) throws SQLException, InterruptedException {
    ComboPooledDataSource ds = new ComboPooledDataSource();
    System.out.println("初始化连接数："+ds.getInitialPoolSize());
    printInfo(ds);
    printInfo(ds);
}

private static void printInfo(ComboPooledDataSource ds) throws SQLException {
    long start = System.nanoTime();
    ds.getConnection();
    long end = System.nanoTime();
    System.out.println("花费时间：" + (end-start) * 0.000001 + "毫秒");
    System.out.println("总连接："+ds.getNumConnections()+" 忙："+ds.getNumBusyConnections()+ " 闲："+ds.getNumIdleConnections());
}
```
输出：
```
初始化连接数：5
花费时间：796.101372毫秒
总连接：3 忙：1 闲：2
花费时间：0.192853毫秒
总连接：4 忙：2 闲：2
```

学员疑问：为何总连接数不是5，这时因为
* 连接池是在第一次 getConnection 时才执行初始化
* 连接池初始化连接需要一定时间
