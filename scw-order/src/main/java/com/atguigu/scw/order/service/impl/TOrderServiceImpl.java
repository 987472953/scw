package com.atguigu.scw.order.service.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.atguigu.scw.order.bean.TOrder;
import com.atguigu.scw.order.mapper.TOrderMapper;
import com.atguigu.scw.order.service.TOrderService;
import com.atguigu.scw.order.service.TProjectServiceFeign;
import com.atguigu.scw.order.vo.req.OrderInfoSubmitVo;
import com.atguigu.scw.order.vo.req.TReturn;
import com.atguigu.scw.util.AppDateUtils;
import com.atguigu.scw.vo.resp.AppResponse;
import com.atguigu.user.scw.enums.OrderStatusEnumes;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TOrderServiceImpl implements TOrderService {

	@Autowired
	StringRedisTemplate stringRedisTemplate;
	
	@Autowired
	TOrderMapper orderMapper;
	
	@Autowired
	TProjectServiceFeign projectServiceFeign;
	
	@Override
	public TOrder saveOrder(OrderInfoSubmitVo orderInfoSubmitVo) {
		
		log.debug("===开始保存Order订单=====");
		TOrder order = new TOrder();
		
		String accessToken = orderInfoSubmitVo.getAccessToken();
		String memberid = stringRedisTemplate.opsForValue().get(accessToken);
		order.setMemberid(Integer.parseInt(memberid));
		
		order.setReturnid(orderInfoSubmitVo.getReturnid());
		order.setProjectid(orderInfoSubmitVo.getProjectid());
		
		String orderNum = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
		order.setOrdernum(orderNum);
		
		order.setCreatedate(AppDateUtils.getFormatTime());
		
		AppResponse<TReturn> resp = projectServiceFeign.returnsInfo(orderInfoSubmitVo.getReturnid());
		TReturn retObj = resp.getData();
		
		Integer titleMoney = orderInfoSubmitVo.getRtncount() * retObj.getSupportmoney()  + retObj.getFreight();
		order.setMoney(titleMoney);
		
		order.setStatus(OrderStatusEnumes.UNPAY.getCode()+"");
		
		order.setInvoice(orderInfoSubmitVo.getInvoice().toString());
		order.setAddress(orderInfoSubmitVo.getAddress());
		order.setRtncount(orderInfoSubmitVo.getRtncount());
		order.setInvoictitle(orderInfoSubmitVo.getInvoictitle());
		order.setRemark(orderInfoSubmitVo.getRemark());
		
		orderMapper.insertSelective(order);
		log.debug("Service层Order={}", order);
		return order;
	}

}
