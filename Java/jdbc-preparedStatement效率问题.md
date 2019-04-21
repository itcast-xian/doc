# 视频中提到 PreparedStatement 会比Statement 更高效率但是并没有说原因
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

但是！！ mysql 的 jdbc 程序就算用了 PreparedStatement, 默认也不会做上述优化，因此效率与 Statement 没有区别。

如果希望能让 PreparedStatement 发挥作用，需要在 mysql 的连接字符串上添加参数 `useServerPrepStmts=true` 但提升的效率也比较有限（这是一个很长的话题）