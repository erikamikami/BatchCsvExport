package com.example.csv;

import java.io.IOException;
import java.io.Writer;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileFooterCallback;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * CSVファイルのフッターに書き込むことを設定するクラス
 * @author mikami
 *
 */
@Component
@StepScope
public class CsvFooterCallback implements FlatFileFooterCallback{
	
	@Value("#{stepExecution}")
	private StepExecution stepExecution;
	
	/**
	 * 何件のデータを書き込んだのかをフッターに書き込む
	 */
	@Override
	public void writeFooter(Writer writer) throws IOException {
		writer.write("合計＝" + stepExecution.getWriteCount() + "件");
		
	}

}
