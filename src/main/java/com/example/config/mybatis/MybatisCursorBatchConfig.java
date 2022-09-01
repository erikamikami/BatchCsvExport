package com.example.config.mybatis;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.mybatis.spring.batch.builder.MyBatisCursorItemReaderBuilder;
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
public class MybatisCursorBatchConfig extends BaseConfig{
	
	@Autowired // Mybatisで必要
	private SqlSessionFactory sqlSessionFactory;
	
	/**
	 * MyBatisCursorItemReaderの作成
	 * @return
	 */
	@Bean
	@StepScope
	public MyBatisCursorItemReader<Employee> mybatisCursorReader(){
		// クエリに渡すパラメーター
		Map<String, Object> parameterValue = new HashMap<>();
		parameterValue.put("gender", 1);
		
		return new MyBatisCursorItemReaderBuilder<Employee>()
											.sqlSessionFactory(sqlSessionFactory)
											.queryId("com.example.repository.EmployeeMapper.findByGender") // Mapperのインターフェイス指定
											.parameterValues(parameterValue)
											.build();
											
	}
	
	/**
	 * Stepの作成
	 * @return
	 * @throws Exception
	 */
	@Bean
	public Step exportMybatisCursorStep() throws Exception{
		return stepBuilderFactory.get("ExportMybatisCursorStep")
									.<Employee, Employee>chunk(10)
									.reader(mybatisCursorReader()).listener(readListener)
									.processor(genderConvertProcessor)
									.writer(csvWriter()).listener(writeListener)
									.build();
	}
	
	@Bean
	public Job exportMybatisCursorJob() throws Exception {
		return jobBuilderFactory.get("ExportMybatisCursorJob")
									.incrementer(new RunIdIncrementer())
									.start(exportMybatisCursorStep())
									.build();
	}


}
