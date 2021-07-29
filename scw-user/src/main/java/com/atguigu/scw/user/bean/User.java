package com.atguigu.scw.user.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode //提供equals和hashCode方法
@NoArgsConstructor //无参构造器
@AllArgsConstructor	//有参构造器
@ToString
@Data //lombok自动生成get，set方法
@ApiModel
public class User {

	@ApiModelProperty(value = "用户姓名")
	private String uname;
	
	@ApiModelProperty("用户邮箱")
	private String email;

	

}
