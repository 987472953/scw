package com.atguigu.scw.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.atguigu.scw.service.TMemberServiceFeign;
import com.atguigu.scw.service.TProjectServiceFeign;
import com.atguigu.scw.vo.resp.AppResponse;
import com.atguigu.scw.webui.vo.resp.ProjectDetailVo;
import com.atguigu.scw.webui.vo.resp.ReturnPayConfirmVo;
import com.atguigu.scw.webui.vo.resp.UserAddressVo;
import com.atguigu.scw.webui.vo.resp.UserRespVo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class TProjectController {

	@Autowired
	TProjectServiceFeign projectServiceFeign;

	@Autowired
	TMemberServiceFeign memberServiceFeign;

	@Autowired
	RedisTemplate redisTemplate;

	@RequestMapping("/project/projectInfo") // 详情页
	public ModelAndView index(Integer id, Model model) {

		ProjectDetailVo data = (ProjectDetailVo) redisTemplate.opsForValue().get("detailsInfo" + id);

		if (data == null) {
			AppResponse<ProjectDetailVo> resp = projectServiceFeign.detailsInfo(id);
			data = resp.getData();
			log.debug("--------------------------------------");
			log.debug("detailsInfo={}", data);
			redisTemplate.opsForValue().set("detailsInfo" + id, data, 1, TimeUnit.HOURS);
		}
		model.addAttribute("detailsInfo", data);
		return new ModelAndView("project/index");
	}
	@RequestMapping("/project/returnInfo/{projectId}/{returnId}") // 回报信息
	public ModelAndView returnInfo(@PathVariable("projectId") Integer projectId, @PathVariable("returnId") Integer returnId,
			Model model, HttpSession session) {
		log.debug("-=======================-");
		ReturnPayConfirmVo data = (ReturnPayConfirmVo) redisTemplate.opsForValue().get("returnInfo" + returnId);
		log.debug("Redis中的ReturnPayConfirmVo={}", data);
		if (data == null) {
			AppResponse<ReturnPayConfirmVo> returnInfo = projectServiceFeign.returnInfo(projectId, returnId);
			data = returnInfo.getData();
			log.debug("ReturnPayConfirmVo={}", data);
			redisTemplate.opsForValue().set("returnInfo" + returnId, data, 1, TimeUnit.HOURS);
		}

		model.addAttribute("returnInfo", data);
		session.setAttribute("returnInfoSession", data);
		return new ModelAndView("project/pay-step-1");
	}

	@RequestMapping("/project/confirm/order/{num}")
	public ModelAndView confirmOrder(@PathVariable("num") Integer num, HttpSession session, Model model) {

		UserRespVo userRespVo = (UserRespVo) session.getAttribute("loginMember");
		if (userRespVo == null) {

			session.setAttribute("preUrl", "/project/confirm/order/" + num);

			return new ModelAndView("redirect:/login");
		}
		log.debug("userRespVo={}", userRespVo.toString());
		String accessToken = userRespVo.getAccessToken();
		log.debug("session accessToken={}", accessToken);
		AppResponse<List<UserAddressVo>> resp = memberServiceFeign.address(accessToken);
		List<UserAddressVo> address = resp.getData();

		model.addAttribute("MemberAddressList", address);

		// 共享的回报信息
		ReturnPayConfirmVo vo = (ReturnPayConfirmVo) session.getAttribute("returnInfoSession");
		vo.setNum(num);
		vo.setTotalPrice(new BigDecimal(num * vo.getPrice() + vo.getFreight()));
		session.setAttribute("returnInfoSession", vo);
		return new ModelAndView("project/pay-step-2");
	}

}
