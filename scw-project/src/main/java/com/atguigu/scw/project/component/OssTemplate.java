package com.atguigu.scw.project.component;

import java.io.InputStream;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

//@Component
@Slf4j
@Data
@ToString
public class OssTemplate {

	// @Value("${oss.endpoint}")
	String endpoint;
	String accessKeyId; // <yourAccessKeyId>
	String accessKeySecret; // <yourAccessKeySecret>
	String bucketName;

	public String uploadPicture(String fileName, InputStream inputStream) {
		log.debug("endpoint:{}",endpoint);
		System.out.println(":::" + endpoint + " " + accessKeyId + " " + accessKeySecret + " " + bucketName);
		try {

			// 创建OSSClient实例。
			OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

			// 上传文件流
			// <yourBucketName> <yourObjectName>
			ossClient.putObject(bucketName, "picture/" + fileName, inputStream);
			String filepath = "https://" + bucketName + "." + endpoint + "/picture/" + fileName;
			log.debug("文件上传成功：{}", filepath);

			// 关闭OSSClient。
			ossClient.shutdown();

			return filepath;

		} catch (OSSException e) {

			e.printStackTrace();
			log.debug("文件上传失败：{}", fileName);
			return null;
		}

	}
}
