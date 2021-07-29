package com.atguigu.scw.project.vo.req;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BaseVo implements Serializable{

	private String accessToken;
}
