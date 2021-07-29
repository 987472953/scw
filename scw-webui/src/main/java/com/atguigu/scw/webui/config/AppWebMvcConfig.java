package com.atguigu.scw.webui.config;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootConfiguration
public class AppWebMvcConfig implements WebMvcConfigurer{

//	@Override
//	public void addViewControllers(ViewControllerRegistry registry) {
//		//<mvc:view-controller path="/index" view-name="index"/>
//		registry.addViewController("/index").setViewName("index");
//	}
	
//	@Override
//	public void addInterceptors(InterceptorRegistry registry) {
//		//注册拦截器
//	}
	
}
