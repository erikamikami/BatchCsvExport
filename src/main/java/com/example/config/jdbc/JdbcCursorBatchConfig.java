package com.example.config.jdbc;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
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
		
		return new JdbcCursorItemReaderBuilder<Employee>() // Builderの取得
									.dataSource(this.dataSource) // DataSourceのセット
									.name("JdbcCursorItemReader") // 名前
									.sql(SELECT_EMPLOYEE_SQL) // SQLのセット
									.queryArguments(params) // パラメーター
									.rowMapper(rowMapper) // rowMapperのセット
									.build(); // readerの生成
		
	}
	
	/**
	 * Stepの作成
	 * @return
	 */
	@Bean
	public Step exportJdbcCursorStep() {
		return stepBuilderFactory.get("ExportJdbcCursorStep")
								.<Employee, Employee>chunk(10)
								.reader(jdbcCursorItemReader()).listener(this.readListener)
								.processor(this.genderConvertProcessor)
								.writer(csvWriter()).listener(writeListener)
								.build();
	}
	
	@Bean
	public Job exportJdbcCursorJob() {
		return jobBuilderFactory.get("ExportJdbcCursorJob")
								.incrementer(new RunIdIncrementer())
								.start(exportJdbcCursorStep())
								.build();
	}
	
}
