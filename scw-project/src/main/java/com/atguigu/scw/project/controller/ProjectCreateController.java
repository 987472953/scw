package com.atguigu.scw.project.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.atguigu.scw.project.bean.TReturn;
import com.atguigu.scw.project.component.OssTemplate;
import com.atguigu.scw.project.contants.ProjectConstant;
import com.atguigu.scw.project.service.TProjectService;
import com.atguigu.scw.project.vo.req.BaseVo;
import com.atguigu.scw.project.vo.req.ProjectBaseInfoVo;
import com.atguigu.scw.project.vo.req.ProjectRedisStorageVo;
import com.atguigu.scw.project.vo.req.ProjectReturnVo;
import com.atguigu.scw.vo.resp.AppResponse;
import com.atguigu.user.scw.enums.ProjectStatusEnume;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Api(tags = "项目发起模块")
@RequestMapping("/project/create")
@RestController
public class ProjectCreateController {

	@Autowired
	OssTemplate ossTemplate;

	@Autowired
	StringRedisTemplate stringRedisTemplate;

	@Autowired
	TProjectService projectService;

	@ApiOperation(value = "1-项目初始创建")
	@PostMapping("/init")
	public AppResponse<Object> init(BaseVo vo) {
		log.debug("项目初始创建====================");
		try {
			// 1.验证用户是否登录
			String accessToken = vo.getAccessToken();

			if (StringUtils.isEmpty(accessToken)) {
				AppResponse<Object> resp = AppResponse.fail(null);
				resp.setMsg("请求必须提供accessToken值");
				log.debug("accessToken错误={}", accessToken);
				return resp;
			}

			String memberId = stringRedisTemplate.opsForValue().get(accessToken);

			if (StringUtils.isEmpty(memberId)) {
				AppResponse<Object> resp = AppResponse.fail(null);
				resp.setMsg("请登录系统，再发布项目");
				log.debug("memberId错误={}", memberId);
				return resp;
			}

			// 2.初始化bigVO
			ProjectRedisStorageVo bigVO = new ProjectRedisStorageVo();
			BeanUtils.copyProperties(vo, bigVO);// 将vo中数据copy到bigVO

			String projectToken = UUID.randomUUID().toString().replaceAll("-", "");
			bigVO.setProjectToken(projectToken);

			// 3.数据存储
			String bigStr = JSON.toJSONString(bigVO);// 将bigVO转换为json（fastjosn）

			log.debug("bigVO数据={}", bigVO);

			stringRedisTemplate.opsForValue().set(ProjectConstant.TEMP_PROJECT_PREFIX + projectToken, bigStr);

			return AppResponse.ok(bigVO); // jackson组件
		} catch (BeansException e) {
			e.printStackTrace();
			return AppResponse.fail(null); // jackson组件
		}
	}

	@ApiOperation(value = "2-项目基本信息")
	@PostMapping("/baseinfo")
	public AppResponse<Object> baseInfo(ProjectBaseInfoVo baseInfoVo) {
		// 1.验证用户是否登录
		try {
			String accessToken = baseInfoVo.getAccessToken();
			if (StringUtils.isEmpty(accessToken)) { // 非空判断
				AppResponse<Object> resp = AppResponse.fail(null);
				resp.setMsg("请求必须提供accessToken值");
				log.debug("accessToken错误={}", accessToken);
				return resp;
			}

			String memberId = stringRedisTemplate.opsForValue().get(accessToken);

			if (StringUtils.isEmpty(memberId)) { // 验证Redis
				AppResponse<Object> resp = AppResponse.fail(null);
				resp.setMsg("请登录系统，再发布项目");
				log.debug("memberId错误={}", memberId);
				return resp;
			}

			// 2.从redis中获取bigVO数据，将小Vo封装到bigVO中
			String projectToken = baseInfoVo.getProjectToken();
			String bigStr = stringRedisTemplate.opsForValue().get(ProjectConstant.TEMP_PROJECT_PREFIX + projectToken);
			ProjectRedisStorageVo bigVo = JSON.parseObject(bigStr, ProjectRedisStorageVo.class);

			BeanUtils.copyProperties(baseInfoVo, bigVo);

			stringRedisTemplate.opsForValue().set(ProjectConstant.TEMP_PROJECT_PREFIX + projectToken,
					JSON.toJSONString(bigVo));

			return AppResponse.ok(bigVo);
		} catch (BeansException e) {
			e.printStackTrace();
			log.debug("表单处理失败={}", e.getMessage());
			return AppResponse.fail(null);
		}
	}

	@ApiOperation(value = "3-添加项目回报档位")
	@PostMapping("/return")
	public AppResponse<Object> returnDetail(@RequestBody List<ProjectReturnVo> pros) {

		try {
			String accessToken = pros.get(0).getAccessToken();
			if (StringUtils.isEmpty(accessToken)) { // 非空判断
				AppResponse<Object> resp = AppResponse.fail(null);
				resp.setMsg("请求必须提供accessToken值");
				log.debug("accessToken错误={}", accessToken);
				return resp;
			}

			String memberId = stringRedisTemplate.opsForValue().get(accessToken);

			if (StringUtils.isEmpty(memberId)) { // 验证Redis
				AppResponse<Object> resp = AppResponse.fail(null);
				resp.setMsg("请登录系统，再发布项目");
				log.debug("memberId错误={}", memberId);
				return resp;
			}

			String projectToken = pros.get(0).getProjectToken();

			String bigStr = stringRedisTemplate.opsForValue().get(ProjectConstant.TEMP_PROJECT_PREFIX + projectToken);
			ProjectRedisStorageVo bigVo = JSON.parseObject(bigStr, ProjectRedisStorageVo.class);
			for (ProjectReturnVo projectReturnVo : pros) {
				// projectReturnVo转换为TReturn，并添加到ProjectRedisStorageVo中
				TReturn tReturn = new TReturn();
				BeanUtils.copyProperties(projectReturnVo, tReturn);
				bigVo.getProjectReturns().add(tReturn);
			}

			stringRedisTemplate.opsForValue().set(ProjectConstant.TEMP_PROJECT_PREFIX + projectToken,
					JSON.toJSONString(bigVo));
			return AppResponse.ok(bigVo);
		} catch (BeansException e) {
			e.printStackTrace();
			log.debug("表单处理失败={}", e.getMessage());
			return AppResponse.fail(null);
		}
	}

	/**
	 * 文件上传要求： 1、method="post" 2、enctype="multipart/form-data" 3、type="file"
	 * 
	 * springMVC框架集成commons-fileupload和commons-io组件，完成上传操作 springMVC提供文件上传解析器
	 * Controller处理文件上传时，通过MultipartFile接受文件
	 */

	@ApiOperation(value = "上传图片")

	@PostMapping("/upload")
	public AppResponse<Object> upload(@RequestParam("uploadfile") MultipartFile[] files) {
		log.debug("上传文件==============================");
		List<String> filepathList = new ArrayList<String>();

		try {
			for (MultipartFile multipartFile : files) {
				String filename = multipartFile.getOriginalFilename();
				filename = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 4) + "_" + filename;

				String filepath = ossTemplate.uploadPicture(filename, multipartFile.getInputStream());
				filepathList.add(filepath);
				log.debug("上传文件名：{}", filename);
				log.debug("上传文件路径：{}", filepath);
			}
			log.debug("上传文件路径List：{}", filepathList);
			return AppResponse.ok(filepathList);
		} catch (IOException e) {

			e.printStackTrace();
			return AppResponse.fail(null);
		}
	}

	@ApiOperation(value = "4-项目提交审核申请")
	@PostMapping("/submit")
	public AppResponse<Object> submit(String accessToken, String projectToken, String ops) {

		try {
			if (StringUtils.isEmpty(accessToken)) { // 非空判断
				AppResponse<Object> resp = AppResponse.fail(null);
				resp.setMsg("请求必须提供accessToken值");
				log.debug("accessToken错误={}", accessToken);
				return resp;
			}

			String memberId = stringRedisTemplate.opsForValue().get(accessToken);

			if (StringUtils.isEmpty(memberId)) { // 验证Redis
				AppResponse<Object> resp = AppResponse.fail(null);
				resp.setMsg("请登录系统，再发布项目");
				log.debug("memberId错误={}", memberId);
				return resp;
			}

			if ("0".equals(ops)) { // 草稿

				projectService.saveProject(accessToken, projectToken, ProjectStatusEnume.DRAFT.getCode());
				return AppResponse.ok("保存草稿成功");
			} else if ("1".equals(ops)) { // 提交

				projectService.saveProject(accessToken, projectToken, ProjectStatusEnume.SUBMIT_AUTH.getCode());
				return AppResponse.ok("保存成功");
			} else {
				log.error("提交ops错误ops={}", ops);
				return AppResponse.fail("请求方式不支持");
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.debug("表单处理失败={}", e.getMessage());
			return AppResponse.fail(null);
		}
	}

	@ApiOperation(value = "确认项目法人信息")
	@PostMapping("/confirm/legal")
	public AppResponse<Object> legal() {
		return AppResponse.ok("ok");
	}

	@ApiOperation(value = "项目草稿保存")
	@PostMapping("/tempsave")
	public AppResponse<Object> tempsave() {
		return AppResponse.ok("ok");
	}

}
