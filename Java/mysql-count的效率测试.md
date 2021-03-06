# `count(*)`  的效率测试

抽了点时间验证了一下 `count(*)` 的效率问题

1. mysql 版本 5.5.40 （各个版本之间应当差异不大）
2. 表及测试数据
```sql
CREATE TABLE `big` (
  `id` char(20) NOT NULL,
  `name` int(11) DEFAULT NOT NULL,
  PRIMARY KEY (`id`),
  KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
```
插入 1000 万条记录，类似于下面这样，两列上数值时一样的，都建立了索引，目的如下：
* 据说在 `count(*)`  统计行数时，会选择一个较小的索引进行行数统计，这样，name 的索引遍历的时间应当小于 id 索引遍历时间，char(20)长度远超过 int 的 4 个字节
* `count(id)` 要将 id 的值，从引擎层返回给 mysql 的服务层，因此 id 长度越长，需要传输的数据也越多
* `count(*)` 做过优化，引擎层不必将数据返回给 mysql 的数据层，因此传输的数据会相比会少

```
+----------------------+------+
| id                   | name |
+----------------------+------+
| 00000000000000000001 |    1 |
| 00000000000000000002 |    2 |
| 00000000000000000003 |    3 |
| 00000000000000000004 |    4 |
| 00000000000000000005 |    5 |
| 00000000000000000006 |    6 |
| 00000000000000000007 |    7 |
| 00000000000000000008 |    8 |
| 00000000000000000009 |    9 |
| 00000000000000000010 |   10 |
+----------------------+------+
```

测试：
```sql
set profiling = 1;
select count(*) from big;
select count(*) from big;
select count(*) from big;
select count(id) from big;
select count(id) from big;
select count(id) from big;
show profiles;
```

结论：
```
|       22 |   4.53440700 | select count(*) from big  |
|       23 |   4.52487000 | select count(*) from big  |
|       24 |   4.42985450 | select count(*) from big  |
|       25 |   4.79279375 | select count(id) from big |
|       26 |   4.62936500 | select count(id) from big |
|       27 |   4.73743775 | select count(id) from big
```
