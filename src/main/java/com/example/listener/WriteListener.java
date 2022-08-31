package com.example.listener;

import java.util.List;

import org.springframework.batch.core.ItemWriteListener;
import org.springframework.stereotype.Component;

import com.example.domain.Employee;

import lombok.extern.slf4j.Slf4j;

@Component("WriteListener")
@Slf4j
public class WriteListener implements ItemWriteListener<Employee>{
	
	@Override
	public void beforeWrite(List<? extends Employee> items) {
		log.debug("beforeWriteです");
		
	}

	@Override
	public void afterWrite(List<? extends Employee> items) {
		log.debug("afterWriteです。件数:{}", items.size());
		
	}

	@Override
	public void onWriteError(Exception exception, List<? extends Employee> items) {
		log.error("Writeでエラーです:{}", exception.getMessage());
		
	}

}
