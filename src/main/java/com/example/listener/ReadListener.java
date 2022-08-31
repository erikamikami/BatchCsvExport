package com.example.listener;

import org.springframework.batch.core.ItemReadListener;
import org.springframework.stereotype.Component;

import com.example.domain.Employee;

import lombok.extern.slf4j.Slf4j;

@Component("ReadListener")
@Slf4j
public class ReadListener implements ItemReadListener<Employee>{
	
	@Override
	public void beforeRead() {
		log.debug("beforeReadです");
		
	}

	@Override
	public void afterRead(Employee item) {
		log.debug("afterReadです:{}", item);
		
	}

	@Override
	public void onReadError(Exception ex) {
		log.error("Readでエラーです:{}", ex.getMessage());
		
	}

}
