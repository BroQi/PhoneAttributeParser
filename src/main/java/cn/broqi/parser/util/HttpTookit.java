package cn.broqi.parser.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HTTP 请求工具类
 * 
 * @author LiuQi
 *
 */
public class HttpTookit {

	private static final CloseableHttpClient httpClient;
	public static final String CHARSET = "UTF-8"; // 默认编码格式
	private static Logger log = LoggerFactory.getLogger(HttpTookit.class);

	static {
		RequestConfig config = RequestConfig.custom().setConnectTimeout(5000).setSocketTimeout(15000).build();
		httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
	}

	public static String doGet(String url, Map<String, String> params) {
		return doGet(url, params, CHARSET);
	}

	public static String doGet(String url, Map<String, String> params, Map<String, String> headers) {
		return doGet(url, params, headers, CHARSET);
	}

	public static String doPost(String url, Map<String, String> params) {
		return doPost(url, params, null, CHARSET);
	}

	public static String doPost(String url, Map<String, String> params, Map<String, String> headers) {
		return doPost(url, params, headers, CHARSET);
	}

	/**
	 * HTTP Get 获取内容
	 * 
	 * @param url
	 *            请求的url地址 ?之前的地址
	 * @param params
	 *            请求的参数
	 * @param charset
	 *            编码格式
	 * @return 页面内容
	 */
	public static String doGet(String url, Map<String, String> params, String charset) {
		if (StringUtils.isEmpty(url))
			return null;

		try {
			if (params != null && !params.isEmpty()) {
				List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
				for (Map.Entry<String, String> entry : params.entrySet()) {
					String value = entry.getValue();
					if (value != null) {
						pairs.add(new BasicNameValuePair(entry.getKey(), value));
					}
				}
				if (url.lastIndexOf("?") >= 0)
					url += "&" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, charset));
				else
					url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, charset));
			}

			log.debug("The get method request url is : " + url);
			HttpGet httpGet = new HttpGet(url);
			CloseableHttpResponse response = httpClient.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				httpGet.abort();
				throw new RuntimeException("HttpClient, error status code :" + statusCode);
			}
			HttpEntity entity = response.getEntity();
			String result = null;
			if (entity != null) {
				result = EntityUtils.toString(entity, "utf-8");
			}
			EntityUtils.consume(entity);
			response.close();
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * HTTP Get 获取内容
	 * 
	 * @param url
	 *            请求的url地址 ?之前的地址
	 * @param params
	 *            请求的参数
	 * @param charset
	 *            编码格式
	 * @return 页面内容
	 */
	public static String doGet(String url, Map<String, String> params, Map<String, String> headers, String charset) {
		if (StringUtils.isEmpty(url))
			return null;

		try {
			if (params != null && !params.isEmpty()) {
				List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
				for (Map.Entry<String, String> entry : params.entrySet()) {
					String value = entry.getValue();
					if (value != null) {
						pairs.add(new BasicNameValuePair(entry.getKey(), value));
					}
				}
				if (url.lastIndexOf("?") >= 0)
					url += "&" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, charset));
				else
					url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, charset));
			}

			log.debug("The get method request url is : " + url);
			HttpGet httpGet = new HttpGet(url);
			if (headers != null && !headers.isEmpty()) {
				for (Entry<String, String> entry : headers.entrySet()) {
					httpGet.addHeader(entry.getKey(), entry.getValue());
				}
			}
			CloseableHttpResponse response = httpClient.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				httpGet.abort();
				throw new RuntimeException("HttpClient, error status code :" + statusCode);
			}
			HttpEntity entity = response.getEntity();
			String result = null;
			if (entity != null) {
				result = EntityUtils.toString(entity, "utf-8");
			}
			EntityUtils.consume(entity);
			response.close();
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * HTTP Post 获取内容
	 * 
	 * @param url
	 *            请求的url地址 ?之前的地址
	 * @param params
	 *            请求的参数
	 * @param charset
	 *            编码格式
	 * @return 页面内容
	 */
	public static String doPost(String url, Map<String, String> params, Map<String, String> headers, String charset) {
		if (StringUtils.isEmpty(url))
			return null;
		try {
			List<NameValuePair> pairs = null;
			if (params != null && !params.isEmpty()) {
				pairs = new ArrayList<NameValuePair>(params.size());
				for (Map.Entry<String, String> entry : params.entrySet()) {
					String value = entry.getValue();
					if (value != null) {
						pairs.add(new BasicNameValuePair(entry.getKey(), value));
					}
				}
			}
			HttpPost httpPost = new HttpPost(url);
			// 添加头信息
			if (headers != null && !headers.isEmpty()) {
				for (Entry<String, String> entry : headers.entrySet()) {
					httpPost.addHeader(entry.getKey(), entry.getValue());
				}
			}
			if (pairs != null && pairs.size() > 0) {
				httpPost.setEntity(new UrlEncodedFormEntity(pairs, CHARSET));
			}
			CloseableHttpResponse response = httpClient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				httpPost.abort();
				throw new RuntimeException("HttpClient,error status code :" + statusCode);
			}
			HttpEntity entity = response.getEntity();
			String result = null;
			if (entity != null) {
				result = EntityUtils.toString(entity, "utf-8");
			}
			EntityUtils.consume(entity);
			response.close();
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * @param url
	 * @param data{xx:xxx}
	 * @return
	 */
	public static String doPostRequestBody(String url, String data) {
		if (StringUtils.isEmpty(url))
			return null;
		try {
			// List<NameValuePair> pairs = null;
			/*
			 * if (params != null && !params.isEmpty()) { pairs = new
			 * ArrayList<NameValuePair>(params.size()); for (Map.Entry<String,
			 * String> entry : params.entrySet()) { String value =
			 * entry.getValue(); if (value != null) { pairs.add(new
			 * BasicNameValuePair(entry.getKey(), value)); } } }
			 */
			HttpPost httpPost = new HttpPost(url);
			/*
			 * if (pairs != null && pairs.size() > 0) { httpPost.setEntity(new
			 * UrlEncodedFormEntity(pairs, CHARSET)); }
			 */
			if (StringUtils.isNotEmpty(data)) {
				httpPost.setEntity(new StringEntity(data, CHARSET));

			}
			CloseableHttpResponse response = httpClient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				httpPost.abort();
				throw new RuntimeException("HttpClient,error status code :" + statusCode);
			}
			HttpEntity entity = response.getEntity();
			String result = null;
			if (entity != null) {
				result = EntityUtils.toString(entity, "utf-8");
			}
			EntityUtils.consume(entity);
			response.close();
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 将Map转换成get请求参数拼接
	 * 
	 * @param params
	 * @return
	 */
	public static String mapToUrlParams(Map<String, String> params) {
		if (params == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			sb.append(entry.getKey() + "=" + entry.getValue());
			sb.append("&");
		}
		String s = sb.toString();
		if (s.endsWith("&")) {
			s = org.apache.commons.lang.StringUtils.substringBeforeLast(s, "&");
		}
		return s;
	}

	/**
	 * 将Map转换成get请求参数拼接
	 * 
	 * @param params
	 * @return
	 */
	public static String mapToUrlParams(Map<String, String> params, String charset) {
		String urlParams = "";
		if (params != null && !params.isEmpty()) {
			List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
			for (Map.Entry<String, String> entry : params.entrySet()) {
				String value = entry.getValue();
				if (value != null) {
					pairs.add(new BasicNameValuePair(entry.getKey(), value));
				}
			}
			try {
				urlParams = "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, charset));
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return urlParams;
	}
}