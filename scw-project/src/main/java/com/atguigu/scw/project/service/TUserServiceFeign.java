package com.atguigu.scw.project.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.atguigu.scw.project.service.exc.handle.TUserServiceFeignExceptionHandle;
import com.atguigu.scw.project.vo.resp.TMember;
import com.atguigu.scw.vo.resp.AppResponse;

@FeignClient(value = "SCW-USER", fallback = TUserServiceFeignExceptionHandle.class)
public interface TUserServiceFeign {
	
	@GetMapping("/user/info/getTMemberById")
	public AppResponse<TMember> getTMemberById(@RequestParam("id") Integer id);
	
}
