package com.example.config.jdbc;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;

import com.example.config.BaseConfig;
import com.example.domain.Employee;

@Configuration
public class JdbcPagingBatchConfig extends BaseConfig{
	
	@Autowired  //（JDBCで必要になる）
	private DataSource dataSource;
	
	/**
	 * クエリの作成
	 * @return
	 */
	@Bean
	public SqlPagingQueryProviderFactoryBean queryProvider() {
		
		SqlPagingQueryProviderFactoryBean provider = new SqlPagingQueryProviderFactoryBean();
		
		provider.setDataSource(dataSource);
		provider.setSelectClause("SELECT id, name, age, gender");
		provider.setFromClause("FROM employee");
		provider.setWhereClause("WHERE gender = :genderParam");
		provider.setSortKey("id");
		
		return provider;
	}
	
	/**
	 * JdbcPagingItemReader
	 * @return
	 * @throws Exception
	 */
	@Bean
	@StepScope
	public JdbcPagingItemReader<Employee> jdbcPagingReader() throws Exception{
		
		// クエリに渡すパラメーター
		Map<String, Object> parameterValues = new HashMap<>();
		parameterValues.put("genderParam", 1);
		
		// RowMapper
		RowMapper<Employee> rowMapper = new BeanPropertyRowMapper<>(Employee.class);
		
		return new JdbcPagingItemReaderBuilder<Employee>()
				.name("JdbcPagingItemReader")
				.dataSource(dataSource)
				.queryProvider(queryProvider().getObject()) // 上記で作成したクエリ
				.parameterValues(parameterValues)
				.rowMapper(rowMapper)
				.pageSize(5) // 1度に読み取る件数
				.build();
		
	}
	
	/**
	 * Stepの作成
	 * @return
	 * @throws Exception
	 */
	@Bean
	public Step exportJdbcPagingStep() throws Exception {
		return stepBuilderFactory.get("ExportJdbcPagingStep")
									.<Employee, Employee>chunk(10)
									.reader(jdbcPagingReader()).listener(readListener)
									.processor(genderConvertProcessor)
									.writer(csvWriter()).listener(writeListener)
									.build();
	}
	
	/**
	 * Jobの作成
	 * @return
	 * @throws Exception
	 */
	@Bean
	public Job exportJdbcPagingJob() throws Exception {
		return jobBuilderFactory.get("ExportJdbcPagingJob")
									.incrementer(new RunIdIncrementer())
									.start(exportJdbcPagingStep())
									.build();
				
	}
	

}
