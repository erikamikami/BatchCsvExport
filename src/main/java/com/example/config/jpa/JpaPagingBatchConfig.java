package com.example.config.jpa;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.database.orm.JpaNativeQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.config.BaseConfig;
import com.example.domain.Employee;

@Configuration
public class JpaPagingBatchConfig extends BaseConfig{
	
	@Autowired
	private EntityManagerFactory entityManagerFactory;
	
	@Bean
	@StepScope
	public JpaPagingItemReader<Employee> jpaPagingReader(){
		// SQL
		String sql = "SELECT id, name, age, gender FROM employee WHERE gender = :gender";
		
		// クエリの設定
		JpaNativeQueryProvider<Employee> queryProvider = new JpaNativeQueryProvider<>();
		queryProvider.setEntityClass(Employee.class);
		queryProvider.setSqlQuery(sql);
		
		// パラメーター
		Map<String, Object> parameterValues = new HashMap<>();
		parameterValues.put("gender", 1);
		
		return new JpaPagingItemReaderBuilder<Employee>()
				.entityManagerFactory(entityManagerFactory)
				.name("JdbcPagingItemReader")
				.queryProvider(queryProvider)
				.parameterValues(parameterValues)
				.pageSize(5)
				.build();
	}
	
	@Bean
	public Step exportJpaPagingStep() {
		return stepBuilderFactory.get("ExportJpaPagingStep")
				.<Employee, Employee>chunk(10)
				.reader(jpaPagingReader()).listener(readListener)
				.processor(genderConvertProcessor)
				.writer(csvWriter()).listener(writeListener)
				.build();
	}
	
	@Bean("JpaPagingJob")
	public Job exportJpaPagingJob() throws Exception{
		return jobBuilderFactory.get("ExportJpaPagingJob")
				.incrementer(new RunIdIncrementer())
				.start(exportJpaPagingStep())
				.build();
	}


}
