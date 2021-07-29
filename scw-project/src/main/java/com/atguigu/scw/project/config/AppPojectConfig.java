package com.atguigu.scw.project.config;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;


import com.atguigu.scw.project.component.OssTemplate;

@SpringBootConfiguration
public class AppPojectConfig {

	@ConfigurationProperties(prefix = "oss") //将oss开头的application中的注入OssTemplate中
	@Bean
	public OssTemplate ossTemplate() {
		
		return new OssTemplate();
	}
}
