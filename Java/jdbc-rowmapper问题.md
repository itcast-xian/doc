# rowMapper问题
有学员会问到，为什么 jdbcTemplate.query(sql, rowmapper) 的 list 从哪儿来，元素如何被加入 list

我是以伪代码方式讲解：

```java
public class JdbcTemplate {
	public List<Emp> query(String sql, RowMapper<Emp> rowMapper){
		Connection conn = ...
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();

		int i = 0;
        List<Emp> list = new ArrayList<>();
        while(rs.next()) {
        	Emp emp = rowMapper.mapRow(rs, i);
            i++;
            list.add(emp);
        }
        return list;
    }
}
```