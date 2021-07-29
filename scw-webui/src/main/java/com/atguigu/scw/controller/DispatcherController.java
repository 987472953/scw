package com.atguigu.scw.controller;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.atguigu.scw.service.TMemberServiceFeign;
import com.atguigu.scw.service.TProjectServiceFeign;
import com.atguigu.scw.vo.resp.AppResponse;
import com.atguigu.scw.webui.vo.resp.ProjectVo;
import com.atguigu.scw.webui.vo.resp.UserRespVo;

@Controller
public class DispatcherController {

	@Autowired
	TMemberServiceFeign memberServiceFeign;
	
	@Autowired
	TProjectServiceFeign projectServiceFeign;
	
	@Autowired
	RedisTemplate redisTemplate;
	
	//如果Controller只跳转页面，可以配置如下标签
	//<mvc:view-controller path="/index" view-name="index"/>
	@RequestMapping("/index")
	public String index(Model model) {
		
		List<ProjectVo> data = (List<ProjectVo>) redisTemplate.opsForValue().get("projectInfo");
		if(data==null) {
			AppResponse<List<ProjectVo>> resp = projectServiceFeign.all();
			
			data = resp.getData();
			
			redisTemplate.opsForValue().set("projectInfo", data, 1, TimeUnit.HOURS);
		}
		
		model.addAttribute("projectVoList", data);
		
		return "index";
	}
	
	@RequestMapping("/login")
	public String login() {
		return "login";
	}
	
	@RequestMapping("/doLogin")
	public String doLogin(String loginacct, String userpswd, HttpSession session) {
		
		AppResponse<UserRespVo> resp = memberServiceFeign.login(loginacct, userpswd);
		
		UserRespVo user = resp.getData();
		
		if(user == null) {
			return "login";
		}
		session.setAttribute("loginMember", user);
		
		String preUrl = (String) session.getAttribute("preUrl");
		if(StringUtils.isEmpty(preUrl)) {
			return "redirect:/index";
		}else {
			return "redirect:" + preUrl;
		}
		
		
	}
	
	@RequestMapping("/loginout")
	public String login(HttpSession session) {
		if(session!=null) {
			session.removeAttribute("loginMember");
			session.invalidate();
		}
		return "redirect:/index";
	}
	
//	@RequestMapping("/index")
//	public String index(Model model) {
//		
//		model.addAttribute("hello", "MyName中文");
//		
//		model.addAttribute("unames", Arrays.asList("zhangsan","lisi","wangwu"));
//		
//		return "index";
//	}
}
