package com.example.config.jdbc;

import javax.activation.DataSource;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;

import com.example.config.BaseConfig;
import com.example.domain.Employee;

@Configuration
public class JdbcCursorBatchConfig extends BaseConfig{

	@Autowired
	private DataSource dataSource;
	
	/** SELECT用 SQL **/
	private static final String SELECT_EMPLOYEE_SQL = 
			"SELECT id, name, age, gender FROM employee WHERE gender = ?";
	
	@Bean
	@StepScope
	public JdbcCursorItemReader<Employee> jdbcCursorItemReader(){
		// クエリに渡すパラメータ
		Object[] params = new Object[] {1};
		
		// RowMapper
		RowMapper<Employee> rowMapper = new BeanPropertyRowMapper<>(Employee.class);
		
		
	}
	
}
