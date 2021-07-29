package com.atguigu.scw.project.service.exc.handle;

import org.springframework.stereotype.Component;

import com.atguigu.scw.project.service.TUserServiceFeign;
import com.atguigu.scw.project.vo.resp.TMember;
import com.atguigu.scw.vo.resp.AppResponse;

@Component
public class TUserServiceFeignExceptionHandle implements TUserServiceFeign{

	@Override
	public AppResponse<TMember> getTMemberById(Integer id) {
		AppResponse<TMember> resp = AppResponse.fail(null);
		resp.setMsg("远程调用【查询用户信息】失败");
		return resp;
	}
}
