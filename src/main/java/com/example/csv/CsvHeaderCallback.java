package com.example.csv;

import java.io.IOException;
import java.io.Writer;

import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * CSVファイルのヘッダー文字を出力するクラス
 * @author mikami
 *
 */

@Component
@Slf4j
public class CsvHeaderCallback implements FlatFileHeaderCallback{
	
	@Override
	public void writeHeader(Writer writer) throws IOException {
		writer.write("ID, 名前, 年齢, 性別");
		
	}

}
