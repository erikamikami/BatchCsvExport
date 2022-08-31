package com.example.config;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * ファイルパスのプロパティを持つクラス
 * @author mikami
 *
 */

@Component
@PropertySource("classpath:property/sample.properties")
@Getter
@ToString
@Slf4j
public class SampleProperty {
	
	@Value("${file.name}")
	private String fileName;
	
	@Value("${file.output.directory}")
	private String fileOutputDirectory;
	
	/**
	 * ファイルパスの生成
	 * @return
	 */
	public String outputPath() {
		String outputPath = fileOutputDirectory + File.separator + fileName;
		log.debug("outputPath={}", outputPath);
		return outputPath;
	}

}
