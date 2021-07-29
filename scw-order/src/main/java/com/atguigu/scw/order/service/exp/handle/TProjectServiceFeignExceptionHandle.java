package com.atguigu.scw.order.service.exp.handle;

import org.springframework.stereotype.Component;

import com.atguigu.scw.order.service.TProjectServiceFeign;
import com.atguigu.scw.order.vo.req.TReturn;
import com.atguigu.scw.vo.resp.AppResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TProjectServiceFeignExceptionHandle implements TProjectServiceFeign{

	@Override
	public AppResponse<TReturn> returnsInfo(Integer returnId) {
		AppResponse<TReturn> resp = AppResponse.fail(null);
		resp.setMsg("调用项目远程服务【查询回报信息】失败");
		log.debug("调用项目远程服务【查询回报信息】失败");
		return resp;
	}

	
}
