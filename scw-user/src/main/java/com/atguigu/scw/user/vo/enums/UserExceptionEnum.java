package com.atguigu.scw.user.vo.enums;

public enum UserExceptionEnum {

	USER_EXISTS(1,"用户已经存在"),
	EMAIL_EXISTS(2,"邮箱地址已存在"),
	USER_LOCKED(3,"用户已锁定"),
	USER_SAVE_ERROR(4,"用户保存失败"),
	USER_UNEXISTS(5,"用户不存在"),
	USER_PASSWORD_ERROR(6,"用户密码错误");
	
	
	
	private int code;
	private String message;
	
	private UserExceptionEnum(int code, String message) {
		this.code = code;
		this.message = message;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
}

