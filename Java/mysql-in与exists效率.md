# in 与 exists 效率比较
来自学员问题

## 1. 测试表与测试数据
来自教学资料
```sql
CREATE TABLE `dept` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `NAME` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `NAME` (`NAME`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

CREATE TABLE `emp` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `NAME` varchar(10) DEFAULT NULL,
  `gender` char(1) DEFAULT NULL,
  `salary` double DEFAULT NULL,
  `join_date` date DEFAULT NULL,
  `dept_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `dept_id` (`dept_id`),
  CONSTRAINT `emp_ibfk_1` FOREIGN KEY (`dept_id`) REFERENCES `dept` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
```
不同之处是在部门的 name 列上加了索引，其中 dept 有 4 条记录，emp 表有 5 条记录

## 2. 业务需求
查询部门名称为'财务部'或'开发部'的员工信息

## 3. 测试环境与结果(MySQL 5.5.40)
```sql
mysql> explain select * from emp where exists (select * from dept where (name='开发部'or name='财务部') and dept.id=emp.dept_id);
+----+--------------------+-------+--------+---------------+---------+---------+---------------------+------+-------------+
| id | select_type        | table | type   | possible_keys | key     | key_len | ref                 | rows | Extra       |
+----+--------------------+-------+--------+---------------+---------+---------+---------------------+------+-------------+
|  1 | PRIMARY            | emp   | ALL    | NULL          | NULL    | NULL    | NULL                |    5 | Using where |
|  2 | DEPENDENT SUBQUERY | dept  | eq_ref | PRIMARY,NAME  | PRIMARY | 4       | day04_1.emp.dept_id |    1 | Using where |
+----+--------------------+-------+--------+---------------+---------+---------+---------------------+------+-------------+
2 rows in set (0.00 sec)

mysql> explain select * from emp where dept_id in (select id from dept where name='开发部' or name='财务部');
+----+--------------------+-------+-----------------+---------------+---------+---------+------+------+-------------+
| id | select_type        | table | type            | possible_keys | key     | key_len | ref  | rows | Extra       |
+----+--------------------+-------+-----------------+---------------+---------+---------+------+------+-------------+
|  1 | PRIMARY            | emp   | ALL             | NULL          | NULL    | NULL    | NULL |    5 | Using where |
|  2 | DEPENDENT SUBQUERY | dept  | unique_subquery | PRIMARY,NAME  | PRIMARY | 4       | func |    1 | Using where |
+----+--------------------+-------+-----------------+---------------+---------+---------+------+------+-------------+
2 rows in set (0.00 sec)

mysql> explain select emp.* from emp inner join dept on emp.dept_id=dept.id where dept.name='开发部' or dept.name='财务部';
+----+-------------+-------+-------+---------------+---------+---------+-----------------+------+--------------------------+
| id | select_type | table | type  | possible_keys | key     | key_len | ref             | rows | Extra                    |
+----+-------------+-------+-------+---------------+---------+---------+-----------------+------+--------------------------+
|  1 | SIMPLE      | dept  | index | PRIMARY,NAME  | NAME    | 63      | NULL            |    4 | Using where; Using index |
|  1 | SIMPLE      | emp   | ref   | dept_id       | dept_id | 5       | day04_1.dept.id |    1 | Using where              |
+----+-------------+-------+-------+---------------+---------+---------+-----------------+------+--------------------------+
2 rows in set (0.00 sec)
```


## 4. 测试环境与结果(MySQL 8.0.12)
```sql
mysql> explain select * from emp where exists (select * from dept where (name='开发部'or name='财务部') and dept.id=emp.dept_id);
+----+--------------------+-------+------------+--------+---------------+---------+---------+-------------------+------+----------+-------------+
| id | select_type        | table | partitions | type   | possible_keys | key     | key_len | ref               | rows | filtered | Extra       |
+----+--------------------+-------+------------+--------+---------------+---------+---------+-------------------+------+----------+-------------+
|  1 | PRIMARY            | emp   | NULL       | ALL    | NULL          | NULL    | NULL    | NULL              |    5 |   100.00 | Using where |
|  2 | DEPENDENT SUBQUERY | dept  | NULL       | eq_ref | PRIMARY,NAME  | PRIMARY | 4       | test4.emp.dept_id |    1 |    50.00 | Using where |
+----+--------------------+-------+------------+--------+---------------+---------+---------+-------------------+------+----------+-------------+
2 rows in set, 2 warnings (0.00 sec)

mysql> explain select * from emp where dept_id in (select id from dept where name='开发部' or name='财务部');
+----+-------------+-------+------------+-------+---------------+---------+---------+---------------+------+----------+--------------------------+
| id | select_type | table | partitions | type  | possible_keys | key     | key_len | ref           | rows | filtered | Extra                    |
+----+-------------+-------+------------+-------+---------------+---------+---------+---------------+------+----------+--------------------------+
|  1 | SIMPLE      | dept  | NULL       | range | PRIMARY,NAME  | NAME    | 63      | NULL          |    2 |   100.00 | Using where; Using index |
|  1 | SIMPLE      | emp   | NULL       | ref   | dept_id       | dept_id | 5       | test4.dept.id |    1 |   100.00 | NULL                     |
+----+-------------+-------+------------+-------+---------------+---------+---------+---------------+------+----------+--------------------------+
2 rows in set, 1 warning (0.00 sec)

mysql> explain select emp.* from emp inner join dept on emp.dept_id=dept.id where dept.name='开发部' or dept.name='财务部';
+----+-------------+-------+------------+-------+---------------+---------+---------+---------------+------+----------+--------------------------+
| id | select_type | table | partitions | type  | possible_keys | key     | key_len | ref           | rows | filtered | Extra                    |
+----+-------------+-------+------------+-------+---------------+---------+---------+---------------+------+----------+--------------------------+
|  1 | SIMPLE      | dept  | NULL       | range | PRIMARY,NAME  | NAME    | 63      | NULL          |    2 |   100.00 | Using where; Using index |
|  1 | SIMPLE      | emp   | NULL       | ref   | dept_id       | dept_id | 5       | test4.dept.id |    1 |   100.00 | NULL                     |
+----+-------------+-------+------------+-------+---------------+---------+---------+---------------+------+----------+--------------------------+
2 rows in set, 1 warning (0.00 sec)
```