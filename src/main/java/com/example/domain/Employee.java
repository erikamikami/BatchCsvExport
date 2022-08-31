package com.example.domain;

import lombok.Data;

@Data
public class Employee {
	
	private Integer id;
	private String name;
	private Integer age;
	private Integer gender;
	private String genderString;
	
	/**
	 * 性別の数字を文字列に変換
	 * 
	 */
	public void convertGenderIntToString() {
		if(this.gender == 1) {
			this.genderString = "男性";
		}else if(this.gender == 2) {
			this.genderString = "女性";
		}else {
			String errorMessage = "Gender is invalid:" + this.gender;
			throw new IllegalStateException(errorMessage);

		}
	}

}
