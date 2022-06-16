package com.qidian.demo.util;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

/**
 * httpClient工具包
 *
 * @author Jun
 */
public class HttpClientUtil {

	private static CloseableHttpClient client;

	public HttpClientUtil() throws Exception {
		// 采用绕过验证的方式处理https请求
		SSLContext sslcontext = createIgnoreVerifySsl();
		SSLConnectionSocketFactory ssL = new SSLConnectionSocketFactory(sslcontext,
				NoopHostnameVerifier.INSTANCE);
		// 设置协议http和https对应的处理socket链接工厂的对象
		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("http", PlainConnectionSocketFactory.INSTANCE).register("https", ssL).build();
		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(
				socketFactoryRegistry);
		HttpClients.custom().setConnectionManager(connManager);
		// 创建自定义的httpclient对象
		client = HttpClients.custom().setConnectionManager(connManager).setSSLSocketFactory(ssL).build();
	}


	/**
	 * 发送https请求
	 *
	 * @param url    请求地址
	 * @param param  参数
	 * @param method 请求的方法类型
	 * @return 响应信息
	 * @throws Exception 异常抛出外层处理
	 */
	public String sendHttpsRequest(String url, String param, String method) throws Exception {
		method = method.toUpperCase();
		switch (method) {
			case "GET":
				HttpGet get = new HttpGet(url);
				return execute(get);
			case "POST":
				HttpPost post = new HttpPost(url);
				// 判断是否有参数
				if (null != param) {
					// 构建消息实体
					StringEntity message = new StringEntity(param, StandardCharsets.UTF_8);
					message.setContentEncoding("UTF-8");
					// 发送Json格式的数据请求
					message.setContentType("application/json");
					post.setEntity(message);
				}
				return execute(post);
			case "PUT":
				HttpPut put = new HttpPut(url);
				// 判断是否有参数
				if (null != param) {
					// 构建消息实体
					StringEntity message = new StringEntity(param, StandardCharsets.UTF_8);
					message.setContentEncoding("UTF-8");
					// 发送Json格式的数据请求
					message.setContentType("application/json");
					put.setEntity(message);
				}
				return execute(put);
			case "PATCH":
				HttpPatch patch = new HttpPatch(url);
				// 判断是否有参数
				if (null != param) {
					// 构建消息实体
					StringEntity message = new StringEntity(param, StandardCharsets.UTF_8);
					message.setContentEncoding("UTF-8");
					// 发送Json格式的数据请求
					message.setContentType("application/json");
					patch.setEntity(message);
				}
				return execute(patch);
			case "DELETE":
				HttpDelete delete = new HttpDelete(url);
				return execute(delete);
			default:
				return "";
		}
	}

	/**
	 * 发送请求
	 *
	 * @param request http请求类型
	 * @return 结果集
	 * @throws Exception 异常抛出外层处理
	 */
	private String execute(HttpUriRequest request) throws Exception {
		CloseableHttpResponse response = null;
		HttpEntity entity = null;
		String result = "";
		// 指定报文头Content-type、Authorization
		request.setHeader("Content-type", "application/json");
		response = client.execute(request);
		// 获取结果实体
		if (response != null) {
			entity = response.getEntity();
			result = EntityUtils.toString(entity, "UTF-8");
			response.close();
		}
		return result;
	}

	/**
	 * 绕过验证
	 *
	 * @return 获取SSLContext
	 * @throws NoSuchAlgorithmException 异常抛出
	 * @throws KeyManagementException   异常抛出
	 */
	private static SSLContext createIgnoreVerifySsl() throws NoSuchAlgorithmException, KeyManagementException {
		SSLContext sc = SSLContext.getInstance("SSLv3");

		// 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
		X509TrustManager trustManager = new X509TrustManager() {
			@Override
			public void checkClientTrusted(java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
			                               String paramString) throws CertificateException {
			}

			@Override
			public void checkServerTrusted(java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
			                               String paramString) throws CertificateException {
			}

			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};
		sc.init(null, new TrustManager[]{trustManager}, null);
		return sc;
	}

}