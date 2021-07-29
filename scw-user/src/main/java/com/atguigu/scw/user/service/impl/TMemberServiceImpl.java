package com.atguigu.scw.user.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.atguigu.scw.user.bean.TMember;
import com.atguigu.scw.user.bean.TMemberAddress;
import com.atguigu.scw.user.bean.TMemberAddressExample;
import com.atguigu.scw.user.bean.TMemberExample;
import com.atguigu.scw.user.mapper.TMemberAddressMapper;
import com.atguigu.scw.user.mapper.TMemberMapper;
import com.atguigu.scw.user.service.TMemberService;
import com.atguigu.scw.user.vo.enums.UserExceptionEnum;
import com.atguigu.scw.user.vo.exception.UserException;
import com.atguigu.scw.user.vo.req.UserRegistVo;
import com.atguigu.scw.user.vo.resp.UserAddressVo;
import com.atguigu.scw.user.vo.resp.UserRespVo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
public class TMemberServiceImpl implements TMemberService {

	@Autowired
	TMemberMapper memberMapper;

	@Autowired
	TMemberAddressMapper memberAdressMapper;
	
	@Autowired
	StringRedisTemplate stringRedisTemplate;

	// @Transactional(propagation = Propagation.REQUIRED, isolation =
	// Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
	@Override
	public int saveTMember(UserRegistVo vo) {

		try {
			// 将vo属性对拷到do对象中
			TMember member = new TMember();
			BeanUtils.copyProperties(vo, member);
			member.setUsername(vo.getLoginacct());

			String userpswd = vo.getUserpswd();
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			member.setUserpswd(encoder.encode(userpswd));// 加密密码

			int result = memberMapper.insertSelective(member);
			log.debug("保存用户成功：{}", member);
			return result;
		} catch (Exception e) {
			// throw new RuntimeException("保存会员业务失败！");
			log.error("保存用户失败：{}", e.getMessage());
			throw new UserException(UserExceptionEnum.USER_SAVE_ERROR);
		}
	}

	@Override
	public UserRespVo getUserByLogin(String loginacct, String password) {

		log.debug("开始验证用户==================================================");
		UserRespVo vo = new UserRespVo();

		TMemberExample example = new TMemberExample();
		example.createCriteria().andLoginacctEqualTo(loginacct);
		List<TMember> list = memberMapper.selectByExample(example);
		log.debug("memberList = {}", list);
		if (list == null || list.size() == 0) {
			log.debug("查无用户==================================================");
			throw new UserException(UserExceptionEnum.USER_UNEXISTS);
		}
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		TMember member = list.get(0);
		log.debug("member = {}", member);
		if (!encoder.matches(password, member.getUserpswd())) {
			log.debug("密码错误==================================================");
			throw new UserException(UserExceptionEnum.USER_PASSWORD_ERROR);
		}
		BeanUtils.copyProperties(member, vo);

		String accessToken = UUID.randomUUID().toString().replaceAll("-", "");

		stringRedisTemplate.opsForValue().set(accessToken, member.getId().toString());// 放入Reids缓冲区
		vo.setAccessToken(accessToken);
		
		return vo;

	}

	@Override
	public TMember getTMemberById(Integer id) {

		TMember member = memberMapper.selectByPrimaryKey(id);
		member.setUserpswd(null);
		return member;
	}

	@Override
	public List<TMemberAddress> getAdressByid(String accessToken) {
		
		Integer memberId = Integer.parseInt(accessToken);
		
		TMemberAddressExample example = new TMemberAddressExample();
		example.createCriteria().andMemberidEqualTo(memberId);
		
		List<TMemberAddress> selectByExample = memberAdressMapper.selectByExample(example);
		
		
		
		return selectByExample;
	}
}
