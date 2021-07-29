package com.atguigu.scw.order.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.atguigu.scw.order.service.exp.handle.TProjectServiceFeignExceptionHandle;
import com.atguigu.scw.order.vo.req.TReturn;
import com.atguigu.scw.vo.resp.AppResponse;

@FeignClient(value="SCW-PROJECT", fallback = TProjectServiceFeignExceptionHandle.class)
public interface TProjectServiceFeign {

	@GetMapping("/project/details/returns/info/{returnId}")
	public AppResponse<TReturn> returnsInfo(@PathVariable("returnId") Integer returnId);
	
}
