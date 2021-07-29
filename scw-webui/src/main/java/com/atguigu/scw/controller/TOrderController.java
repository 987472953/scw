package com.atguigu.scw.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.atguigu.scw.service.TOrderServiceFeign;
import com.atguigu.scw.vo.resp.AppResponse;
import com.atguigu.scw.webui.config.AlipayConfig;
import com.atguigu.scw.webui.vo.req.OrderFormInfoSubmitVo;
import com.atguigu.scw.webui.vo.resp.OrderInfoSubmitVo;
import com.atguigu.scw.webui.vo.resp.ReturnPayConfirmVo;
import com.atguigu.scw.webui.vo.resp.TOrder;
import com.atguigu.scw.webui.vo.resp.UserRespVo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class TOrderController {

	@Autowired
	TOrderServiceFeign orderServiceFeign;

	@ResponseBody
	@PostMapping("/order/payOrder")
	public ModelAndView orderPay(OrderFormInfoSubmitVo vo/* 接收表单数据 */, HttpSession session) {

		log.debug("OrderFormInfoSubmitVo={}", vo);

		OrderInfoSubmitVo orderInfoSubmitVo = new OrderInfoSubmitVo();// feign接口远程VO
		BeanUtils.copyProperties(vo, orderInfoSubmitVo);

		UserRespVo userRespVo = (UserRespVo) session.getAttribute("loginMember");
		if (userRespVo == null) {
			return new ModelAndView("redirect:/login");
		}
		orderInfoSubmitVo.setAccessToken(userRespVo.getAccessToken());

		ReturnPayConfirmVo returnInfoVo = (ReturnPayConfirmVo) session.getAttribute("returnInfoSession");
		if (returnInfoVo == null) {
			return new ModelAndView("redirect:/login");
		}
		orderInfoSubmitVo.setProjectid(returnInfoVo.getProjectId());
		orderInfoSubmitVo.setReturnid(returnInfoVo.getReturnId());
		orderInfoSubmitVo.setRtncount(returnInfoVo.getNum());

		AppResponse<TOrder> resp = orderServiceFeign.saveOrder(orderInfoSubmitVo);
		TOrder order = resp.getData();
		log.debug("Order={}", order.toString());

		// 支付模块
		String orderName = returnInfoVo.getProjectName();

		ModelAndView result = payOrder(order.getOrdernum(), order.getMoney(), orderName, orderInfoSubmitVo.getRemark());

		//return "redirect:/member/minecrowdfunding";
		return result;//这是一个表单，返回给浏览器并立即提交表单，显示出支付二维码页面
	}

	private ModelAndView payOrder(String ordernum, Integer money, String orderName, String remark) {
		// 1、创建支付宝客户端
		AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.app_id,
				AlipayConfig.merchant_private_key, "json", AlipayConfig.charset, AlipayConfig.alipay_public_key,
				AlipayConfig.sign_type);

		// 2、创建一次支付请求
		AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
		alipayRequest.setReturnUrl(AlipayConfig.return_url);
		alipayRequest.setNotifyUrl(AlipayConfig.notify_url);
		log.debug("AliPayConfig={}", AlipayConfig.notify_url);
		log.debug("AliPayConfig={}", AlipayConfig.return_url);
		log.debug("AliPayConfigapp_id={}", AlipayConfig.app_id);
		log.debug("AliPayConfiggatewayUrl={}", AlipayConfig.gatewayUrl);
		// 3、构造支付请求数据
		alipayRequest.setBizContent("{\"out_trade_no\":\"" + ordernum + "\"," + "\"total_amount\":\"" + money + "\","
				+ "\"subject\":\"" + orderName + "\"," + "\"body\":\"" + remark + "\","
				+ "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

		String result = "";
		try {
			// 4、请求
			result = alipayClient.pageExecute(alipayRequest).getBody();
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}

		return new ModelAndView(result);// 支付跳转页的代码，一个form表单，来到扫码页
	}
	
	
	//临时写在客户端，应写在订单服务系统。需要内网穿透
	@ResponseBody
	@RequestMapping("/order/updateOrderStatus")
	public ModelAndView updateOrderStatus() {
		
		log.debug("支付宝完成支付后，异步显示通知结果");
		return new ModelAndView("success");//业务完成，必须返回success给支付宝服务器表示业务完成
	} 
	
}
