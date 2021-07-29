package com.atguigu.scw.project.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.atguigu.scw.project.bean.TProject;
import com.atguigu.scw.project.bean.TProjectImages;
import com.atguigu.scw.project.bean.TReturn;
import com.atguigu.scw.project.bean.TTag;
import com.atguigu.scw.project.bean.TType;
import com.atguigu.scw.project.component.OssTemplate;
import com.atguigu.scw.project.service.ProjectInfoService;
import com.atguigu.scw.project.service.TUserServiceFeign;
import com.atguigu.scw.project.vo.resp.ProjectDetailVo;
import com.atguigu.scw.project.vo.resp.ProjectVo;
import com.atguigu.scw.project.vo.resp.ReturnPayConfirmVo;
import com.atguigu.scw.project.vo.resp.TMember;
import com.atguigu.scw.vo.resp.AppResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Api(tags = "项目基本功能模块（文件上传、项目信息获取等）")
@Slf4j
@RequestMapping("/project")
@RestController
public class ProjectInfoController {

	@Autowired
	OssTemplate ossTemplate;

	@Autowired
	ProjectInfoService projectInfoService;
	
	@Autowired
	TUserServiceFeign userServiceFeign;

	@ApiOperation("[+]获取项目信息详情")
	@GetMapping("/details/info/{projectId}")
	public AppResponse<ProjectDetailVo> detailsInfo(@PathVariable("projectId") Integer projectId) {
		TProject p = projectInfoService.getProjectInfo(projectId);
		ProjectDetailVo projectDetailVo = new ProjectDetailVo();

		// 1、查出这个项目的所有图片
		List<TProjectImages> projectImages = projectInfoService.getProjectImages(p.getId());
		for (TProjectImages tProjectImages : projectImages) {
			if (tProjectImages.getImgtype() == 0) {
				projectDetailVo.setHeaderImage(tProjectImages.getImgurl());
			} else {
				List<String> detailsImage = projectDetailVo.getDetailsImage();
				detailsImage.add(tProjectImages.getImgurl());
			}
		}

		// 2、项目的所有支持档位；
		List<TReturn> returns = projectInfoService.getProjectReturns(p.getId());
		projectDetailVo.setProjectReturns(returns);

		BeanUtils.copyProperties(p, projectDetailVo);
		return AppResponse.ok(projectDetailVo);
	}

	@ApiOperation("[+]获取项目回报列表")
	@GetMapping("/details/returns/{projectId}")
	public AppResponse<List<TReturn>> detailsReturn(@PathVariable("projectId") Integer projectId) {

		List<TReturn> returns = projectInfoService.getProjectReturns(projectId);
		return AppResponse.ok(returns);
	}

	@ApiOperation("[+]获取项目某个回报档位信息")
	@GetMapping("/details/returns/info/{returnId}")
	public AppResponse<TReturn> returnsInfo(@PathVariable("returnId") Integer returnId) {
		TReturn tReturn = projectInfoService.getProjectReturnById(returnId);
		return AppResponse.ok(tReturn);
	}

	@ApiOperation("[+]获取系统所有的项目分类")
	@GetMapping("/types")
	public AppResponse<List<TType>> types() {

		List<TType> types = projectInfoService.getProjectTypes();
		return AppResponse.ok(types);
	}

	@ApiOperation("[+]获取系统所有的项目标签")
	@GetMapping("/tags")
	public AppResponse<List<TTag>> tags() {
		List<TTag> tags = projectInfoService.getAllProjectTags();
		return AppResponse.ok(tags);
	}
	
	@ApiOperation("[+]确认回报信息")
	@GetMapping("/confim/returns/{projectId}/{returnId}")
	public AppResponse<ReturnPayConfirmVo> returnInfo(@PathVariable("projectId") Integer projectId, @PathVariable("returnId") Integer returnId) {

		ReturnPayConfirmVo returnPayConfirmVo = new ReturnPayConfirmVo();
		
		//回报数据
		TReturn tReturn = projectInfoService.getProjectReturnById(returnId);
		returnPayConfirmVo.setReturnId(tReturn.getId());
		returnPayConfirmVo.setReturnContent(tReturn.getContent());
		returnPayConfirmVo.setNum(1);//默认支持数量
		returnPayConfirmVo.setPrice(tReturn.getSupportmoney());
		returnPayConfirmVo.setFreight(tReturn.getFreight());
		returnPayConfirmVo.setSignalpurchase(tReturn.getSignalpurchase());
		returnPayConfirmVo.setPurchase(tReturn.getPurchase());
		
		//有优惠时需要单独业务
		returnPayConfirmVo.setTotalPrice(new BigDecimal(returnPayConfirmVo.getNum() * returnPayConfirmVo.getPrice() + returnPayConfirmVo.getFreight()));
		
		//项目数据
		TProject project = projectInfoService.getProjectInfo(projectId);
		Integer memberid = project.getMemberid();
		returnPayConfirmVo.setProjectId(project.getId());
		returnPayConfirmVo.setProjectName(project.getName());
		returnPayConfirmVo.setProjectRemark(project.getRemark());
		
		//发起人数据
		//调用远程feign接口
		AppResponse<TMember> respMember = userServiceFeign.getTMemberById(memberid);
		TMember member = respMember.getData();
		BeanUtils.copyProperties(member, returnPayConfirmVo);
		returnPayConfirmVo.setMemberId(member.getId());
		returnPayConfirmVo.setMemberName(member.getLoginacct());
		
		
		return AppResponse.ok(returnPayConfirmVo);
	}

	@ApiOperation("[+]获取系统所有的项目")
	@GetMapping("/all")
	public AppResponse<List<ProjectVo>> all() {

		// 1、分步查询，先查出所有项目
		// 2、再查询这些项目图片
		List<ProjectVo> prosVo = new ArrayList<>();

		// 1、连接查询，所有的项目left join 图片表，查出所有的图片
		// left join：笛卡尔积 A*B 1000万*6 = 6000万
		// 大表禁止连接查询；
		List<TProject> pros = projectInfoService.getAllProjects();

		for (TProject tProject : pros) {
			Integer id = tProject.getId();
			List<TProjectImages> images = projectInfoService.getProjectImages(id);
			ProjectVo projectVo = new ProjectVo();
			BeanUtils.copyProperties(tProject, projectVo);

			for (TProjectImages tProjectImages : images) {
				if (tProjectImages.getImgtype() == 0) {
					projectVo.setHeaderImage(tProjectImages.getImgurl());
				}
			}
			prosVo.add(projectVo);

		}

		return AppResponse.ok(prosVo);
	}

	/**
	 * <input type='file' name='file'/>
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 * 
	 *                     /resources/bootstrap/xxxxx /static/css/xxx
	 */
	@ApiOperation("文件上传功能")
	@PostMapping("/upload")
	public AppResponse<Map<String, Object>> upload(@RequestParam("file") MultipartFile[] file) throws IOException {
		Map<String, Object> map = new HashMap<>();
		List<String> list = new ArrayList<>();
		if (file != null && file.length > 0) {
			for (MultipartFile item : file) {
				if (!item.isEmpty()) {
					String upload = ossTemplate.uploadPicture(item.getOriginalFilename(), item.getInputStream());
					list.add(upload);
				}
			}
		}
		map.put("urls", list);
		log.debug("ossTemplate信息：{},文件上传成功访问路径：{}", ossTemplate, list);

		// 文件上传
		return AppResponse.ok(map);
	}

	// 查热门推荐、分类推荐

}
