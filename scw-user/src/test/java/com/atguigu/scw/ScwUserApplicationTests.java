package com.atguigu.scw;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;


@SpringBootTest
class ScwUserApplicationTests {
	
	@Autowired
	DataSource dataSource;
	
	@Autowired
	StringRedisTemplate stringRedisTemplate; //可以将对象整成json字符串对象

	@Test
	public void contextLoads() throws SQLException {
		
		Connection conn = dataSource.getConnection(); 
		
		System.out.println(conn); //代理对象

		conn.close(); //不是销毁对象，而是归还到连接池
	}

	@Test
	public void redisTest01() {
		stringRedisTemplate.opsForValue().set("key111", "value111");
	}
	@Test
	public void redisTest02() {
		String key = stringRedisTemplate.opsForValue().get("key111");
		System.out.println(key);
	}
	
	@Test
	public void test() {

	}
}
