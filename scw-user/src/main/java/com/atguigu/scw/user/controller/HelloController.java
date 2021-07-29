package com.atguigu.scw.user.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atguigu.scw.user.bean.User;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j //相当于下面logger
@Api(tags = "测试Swagger工具的HelloWord")
@RestController
public class HelloController {

	//Logger log = LoggerFactory.getLogger(HelloController.class);
	
	@ApiImplicitParams(value = { @ApiImplicitParam(value = "姓名", name = "name", required = true),
			@ApiImplicitParam(value = "年龄", name = "age", required = true) })
	@ApiOperation(value = "演示接口调用")
	@GetMapping("/hello")
	public String hello(String name, Integer age) {

		return "OK" + name + " - " + age;
	}
	
	@ApiOperation("测试方法Hello2")
	@ApiImplicitParams(value= {
			@ApiImplicitParam(name="name",value="姓名",required=true),
			@ApiImplicitParam(name="email",value="电子邮件")
	})
	@PostMapping("/user")
	public User user(String name ,String email) {
		User user = new User();
		user.setUname(name);
		user.setEmail(email);
		return user;
	}
}
