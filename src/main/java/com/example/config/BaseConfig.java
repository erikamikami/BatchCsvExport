package com.example.config;

import java.nio.charset.StandardCharsets;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.example.csv.CsvFooterCallback;
import com.example.csv.CsvHeaderCallback;
import com.example.domain.Employee;
import com.example.listener.ReadListener;
import com.example.listener.WriteListener;
import com.example.processor.GenderConvertProcessor;

@EnableBatchProcessing
public abstract class BaseConfig {
	
	@Autowired
	protected JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	protected StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	protected ReadListener readListener;
	
	@Autowired
	protected WriteListener writeListener;
	
	@Autowired
	protected CsvFooterCallback csvFooterCallback;
	
	@Autowired
	protected CsvHeaderCallback csvHeaderCallback;
	
	@Autowired
	protected SampleProperty sampleProperty;
	
	@Autowired
	@Qualifier("GenderConvertProcessor")
	protected GenderConvertProcessor genderConvertProcessor;
	
	/**
	 * csv出力のWriter
	 * @return
	 */
	@Bean
	@StepScope
	public FlatFileItemWriter<Employee> csvWriter(){
		// ファイルの出力先を設定
		String filePath = sampleProperty.outputPath();
		Resource outputResource = new FileSystemResource(filePath);
		
		// 区切り文字の設定
		DelimitedLineAggregator<Employee> aggregator = new DelimitedLineAggregator<Employee>();
		aggregator.setDelimiter(DelimitedLineTokenizer.DELIMITER_COMMA);
		
		// 出力フィールドの設定
		BeanWrapperFieldExtractor<Employee> extractor = new BeanWrapperFieldExtractor<>();
		extractor.setNames(new String[] {"id","name","age","gender"});
		aggregator.setFieldExtractor(extractor);
		
		return new FlatFileItemWriterBuilder<Employee>()
												.name("employeeCsvWriter") // 名前
												.resource(outputResource) // ファイル出力先
												.append(false) // 追記設定
												.lineAggregator(aggregator) // 区切り文字
												.headerCallback(csvHeaderCallback) // ヘッダー
												.footerCallback(csvFooterCallback)// フッター
												.encoding(StandardCharsets.UTF_8.name()) // 文字コード
												.build();
												

	}

}
