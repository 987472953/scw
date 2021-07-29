package com.atguigu.scw.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.atguigu.scw.service.exp.handler.TOrderServiceFeignExceptionHandler;
import com.atguigu.scw.vo.resp.AppResponse;
import com.atguigu.scw.webui.vo.resp.OrderInfoSubmitVo;
import com.atguigu.scw.webui.vo.resp.TOrder;

@FeignClient(value="SCW-ORDER", fallback = TOrderServiceFeignExceptionHandler.class)
public interface TOrderServiceFeign {

	/**
	 * ①简单 @RequestParam @RequestVariable
	 * ②复杂 @RequestBody
	 * @param orderInfoSubmitVo
	 * @return
	 */
	@RequestMapping("/order/saveOrder")
	AppResponse<TOrder> saveOrder(@RequestBody OrderInfoSubmitVo orderInfoSubmitVo);

}
