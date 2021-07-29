package com.atguigu.scw.user.vo.req;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class UserRegistVo implements Serializable{
	
	@ApiModelProperty("手机号")
	private String loginacct;
	@ApiModelProperty("密码")
	private String userpswd;
	@ApiModelProperty("邮箱")
	private String email;
	@ApiModelProperty("用户类型：0 - 个人，1 - 企业")
	private String usertype;
	@ApiModelProperty("验证码")
	private String code;
}
