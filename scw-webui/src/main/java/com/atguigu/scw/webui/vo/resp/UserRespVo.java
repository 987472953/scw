package com.atguigu.scw.webui.vo.resp;


import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class UserRespVo implements Serializable{
	//令牌，登录后会分配给当前用户一个临时令牌，以后对系统的任何访问必须携带该指令、否则拒绝访问
	@ApiModelProperty("访问令牌，请妥善保管，以后每次请求都要带上")
	private String accessToken;
	
	private String loginacct;

	private String userpswd;

	private String username;

	private String email;

	private String authstatus;

	private String usertype;

	private String realname;

	private String cardnum;

	private String accttype;
}
