package com.example.config.jpa;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.orm.JpaNativeQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.config.BaseConfig;
import com.example.domain.Employee;

@Configuration
public class JpaCursorBatchConfig extends BaseConfig{
	
	// JPAで必要
	@Autowired
	private EntityManagerFactory entityManagerFactory;
	
	@Bean
	@StepScope
	public JpaCursorItemReader<Employee> JpaCursorReader(){
		// SQL
		String sql = "SELECT id, name, age, gender FROM employee WHERE gender = :gender";
		
		// クエリの設定
		JpaNativeQueryProvider<Employee> queryProvider = new JpaNativeQueryProvider<>();
		queryProvider.setSqlQuery(sql);
		queryProvider.setEntityClass(Employee.class);
		
		// クエリに流すパラメーター
		Map<String, Object> parameterValues = new HashMap<>();
		parameterValues.put("gender", 1);
		
		return new JpaCursorItemReaderBuilder<Employee>()
									.entityManagerFactory(entityManagerFactory)
									.queryProvider(queryProvider)
									.name("JpaCursorItemReader")
									.parameterValues(parameterValues)
									.build();
	}
	
	@Bean
	public Step exportJpaCursorStep() {
		return stepBuilderFactory.get("ExportJpaCursorStep")
									.<Employee, Employee>chunk(10)
									.reader(JpaCursorReader()).listener(readListener)
									.processor(genderConvertProcessor)
									.writer(csvWriter()).listener(writeListener)
									.build();
	}
	
	@Bean
	public Job exportJpaCursorJob() {
		return jobBuilderFactory.get("ExportJpaCursorJob")
									.incrementer(new RunIdIncrementer())
									.start(exportJpaCursorStep())
									.build();
	}

}
