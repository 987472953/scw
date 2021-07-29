package com.atguigu.scw.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TMemberController {

	@RequestMapping("/member/minecrowdfunding")
	public ModelAndView myOrderList() {
		
		return new ModelAndView("member/minecrowdfunding");
	}
}
