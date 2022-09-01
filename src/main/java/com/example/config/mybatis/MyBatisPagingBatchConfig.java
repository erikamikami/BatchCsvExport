package com.example.config.mybatis;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.mybatis.spring.batch.builder.MyBatisPagingItemReaderBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.config.BaseConfig;
import com.example.domain.Employee;

@Configuration
public class MyBatisPagingBatchConfig extends BaseConfig{
	
	// (Mybatisで必要)
	@Autowired 
	private SqlSessionFactory sqlSessionFactory;
	
	/**
	 * MyBatisPagingItemReader
	 * @return
	 */
	@Bean
	@StepScope
	public MyBatisPagingItemReader<Employee> mybatisPagingReader(){
		// クエリに渡すパラメーター
		Map<String, Object> parameterValues = new HashMap<>();
		parameterValues.put("gender", 1);
		
		return new MyBatisPagingItemReaderBuilder<Employee>()
										.sqlSessionFactory(sqlSessionFactory)
										.queryId("com.example.repository.EmployeeMapper.findByGenderPaging")
										.parameterValues(parameterValues)
										.pageSize(10)
										.build();
	}
	
	/**
	 * Stepの作成
	 * @return
	 */
	@Bean
	public Step exportMybatisPagingStep() {
		return stepBuilderFactory.get("ExportMybatisPagingStep")
									.<Employee, Employee>chunk(10)
									.reader(mybatisPagingReader()).listener(readListener)
									.processor(genderConvertProcessor)
									.writer(csvWriter()).listener(writeListener)
									.build();
	}
	
	/**
	 * Jobの作成
	 * @return
	 */
	@Bean
	public Job exportMybatisPagingJob() {
		return jobBuilderFactory.get("ExportMybatisPagingJob")
									.incrementer(new RunIdIncrementer())
									.start(exportMybatisPagingStep())
									.build();
	}


}
