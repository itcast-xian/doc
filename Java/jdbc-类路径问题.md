# 类路径问题

在编写 jdbcutils 工具类时，用到了类路径下搜索资源文件(*.properties)，这里视频的讲解前后有些不一致，学生疑问较多（对ClassLoader很疑惑，总以为与反射相关），还有一个问题是不知道为啥 `src/jdbc.properties` 会找不到文件

我的讲解方法：
1. ClassLoader的主业是完成类加载，它还有个副业，是获取`类路径`下的文件
2. 讲解类路径
3. 讲解 src 与类路径的关系

* 方式1（推荐）
采用字节输入流，注意用完关闭
```java
// 1. 以类路径的方式来获取输入流
// 1.1 获取类加载器, 可以用来加载类，也也可用来找到`类路径`下的文件
ClassLoader classLoader = JdbcDemo12.class.getClassLoader();
// 1.2 找到配置文件, 相对于类路径的  / 表示所有类的起点
InputStream in = classLoader.getResourceAsStream("jdbc.properties");
// 如果文件不在类路径的顶层
// InputStream in = classLoader.getResourceAsStream("cn/itcast/util/3.properties");
// 2. 使用
Properties prop = new Properties();
prop.load(in);
in.close();
```
* 方式2（不推荐）
视频中一开始采用的方式，但后续的视频中又更改为方式1
```java
ClassLoader classLoader = JdbcDemo12.class.getClassLoader();
URL res  = classLoader.getResource("jdbc.properties");
// 问题1；这里如果 path 中出现了空格，会发生文件找不到异常，前面方式1没有此问题
String path = res.getPath();
// 2. 使用
Properties prop = new Properties();
// 问题2：这里没有正确关闭 reader
prop.load(new FileReader(path));
```
