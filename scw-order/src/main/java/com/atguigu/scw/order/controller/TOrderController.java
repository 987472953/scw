package com.atguigu.scw.order.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atguigu.scw.order.bean.TOrder;
import com.atguigu.scw.order.service.TOrderService;
import com.atguigu.scw.order.vo.req.OrderInfoSubmitVo;
import com.atguigu.scw.vo.resp.AppResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class TOrderController {

	
	@Autowired
	TOrderService orderService;
	
	@RequestMapping("/order/saveOrder")
	public AppResponse<TOrder> saveOrder(@RequestBody OrderInfoSubmitVo orderInfoSubmitVo){
		
		try {
			
			TOrder order = orderService.saveOrder(orderInfoSubmitVo);
			
			log.debug("Controller层Order={}", order);
			return AppResponse.ok(order);
			
		} catch (Exception e) {
			AppResponse resp =AppResponse.fail(null);
			resp.setMsg("保存订单出错");
			log.error("保存订单出错");
			return resp;
		}
	}

}
