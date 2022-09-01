package com.example.repository;

import org.apache.ibatis.annotations.Mapper;

import com.example.domain.Employee;

@Mapper
public interface EmployeeMapper {
	
	/**	Cursor **/
	public Employee findByGender(Integer gender);
	
	/**	Paging **/
	public Employee findByGenderPaging(Integer gender);
	
}
