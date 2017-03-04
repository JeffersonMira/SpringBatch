package jeff.learning.batch.processer.composite.domain;


import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class CustomerRowMapper implements RowMapper<Customer>{

	@Override
	public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
		return new Customer(rs.getLong("id"), 
							rs.getString("firstName"), 
							rs.getString("lastName"), 
							rs.getString("birthdate"));
	}

}
