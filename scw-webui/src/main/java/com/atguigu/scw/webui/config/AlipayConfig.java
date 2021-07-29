package com.atguigu.scw.webui.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;



/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *修改日期：2017-04-05
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */

@Configuration
public class AlipayConfig implements InitializingBean{
	
//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

	// 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
	public static String app_id;
	
	// 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key;
	
	// 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key;

	// 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	public static String notify_url;

	// 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	public static String return_url;

	// 签名方式
	public static String sign_type;
	
	// 字符编码格式
	public static String charset;
	
	// 支付宝网关
	// https://openapi.alipaydev.com/gateway.do 沙箱测试使用的
	// https://openapi.alipay.com/gateway.do  上线后正式使用的
	public static String gatewayUrl;
	
	// 支付宝网关
	public static String log_path = "C:\\";
	

	@Value("${alipay.app_id}")
	public String appid;
	@Value("${alipay.merchant_private_key}")
	public String merchantprivatekey;
	@Value("${alipay.alipay_public_key}")
	public String alipaypublickey;
	@Value("${alipay.notify_url}")
	public String notifyurl;
	@Value("${alipay.return_url}")
	public String returnurl;
	@Value("${alipay.sign_type}")
	public String signtype;
	@Value("${alipay.charset}")
	public String charset2;
	@Value("${alipay.gatewayUrl}")
	public String gatewayUrl2;

	// Spring给这个bean的所有属性设置好值以后
	@Override
	public void afterPropertiesSet() throws Exception {
		// 为所有的静态属性赋值为Spring获取到的值
		app_id = appid;
		merchant_private_key = merchantprivatekey;
		alipay_public_key = alipaypublickey;
		notify_url = notifyurl;
		return_url = returnurl;
		sign_type = signtype;
		charset = charset2;
		gatewayUrl = gatewayUrl2;
	}
	
	
}

