package com.atguigu.scw.user.vo.exception;

import com.atguigu.scw.user.vo.enums.UserExceptionEnum;

public class UserException extends RuntimeException {//RuntimeException异常会事务自动回滚
	//为什么会继承RuntimeException（回滚策略）
	
	public UserException() {
		// TODO Auto-generated constructor stub
	}
	
	public UserException(UserExceptionEnum enums) {
		super(enums.getMessage());
	}
}
