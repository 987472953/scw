package com.atguigu.scw.service.exp.handler;

import org.springframework.stereotype.Component;

import com.atguigu.scw.service.TOrderServiceFeign;
import com.atguigu.scw.vo.resp.AppResponse;
import com.atguigu.scw.webui.vo.resp.OrderInfoSubmitVo;
import com.atguigu.scw.webui.vo.resp.TOrder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TOrderServiceFeignExceptionHandler implements TOrderServiceFeign{

	@Override
	public AppResponse<TOrder> saveOrder(OrderInfoSubmitVo orderInfoSubmitVo) {
		AppResponse resp = AppResponse.fail(null);
		resp.setMsg("调用远程服务【保存订单】失败");
		
		log.error("调用远程服务【保存订单】失败");
		return resp;
	}

}
