package gtcrack;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;

import common.Util;

public class GTUtil {

	public static String loadUrlStringContent(final String url) throws UnsupportedEncodingException {
		return new String(loadUrlContent(url, (HttpHost) null), "utf-8");
	}

	public static byte[] loadUrlContent(final String url) {
		return loadUrlContent(url, (HttpHost) null);
	}

	public static byte[] loadUrlContent(final String url, final String proxy) {
		HttpHost proxyHttpHost;
		try {
			proxyHttpHost = new HttpHost(InetAddress.getByName(proxy.split(":")[0]),
					Integer.valueOf(proxy.split(":")[1]));
		} catch (final Exception e) {
			throw new IllegalStateException(e);
		}

		return loadUrlContent(url, proxyHttpHost);
	}

	public static byte[] loadUrlContent(final String url, final HttpHost proxy) {

		final CloseableHttpClient httpClient = getHttpClient();
		final HttpGet httpGet = getHttpGet(url, proxy);

		try {
			return start(httpClient, httpGet);
		} catch (final Exception e) {
			throw new IllegalStateException(e);
		} finally {
			close(httpClient);
		}

	}

	public static String startString(final CloseableHttpClient httpClient, final HttpGet httpGet) throws Exception {
		return new String(start(httpClient, httpGet), "utf-8");
	}

	public static byte[] start(final CloseableHttpClient httpClient, final HttpGet httpGet) {
		return execute(httpClient, httpGet);

	}

	public static String startString(final CloseableHttpClient httpClient, final HttpPost httpPost) throws Exception {
		return new String(start(httpClient, httpPost), "utf-8");
	}

	public static byte[] start(final CloseableHttpClient httpClient, final HttpPost httpPost) {
		return execute(httpClient, httpPost);
	}

	public static byte[] execute(final CloseableHttpClient httpClient, final HttpUriRequest request) {
		CloseableHttpResponse response = null;
		try {
			response = httpClient.execute(request);
			for (final Header header : response.getHeaders("Set-Cookie")) {
				if (header.toString().contains("expires")) {
					request.setHeader("Cookie", Util.parseRegex(header.toString(), "Set-Cookie: ([^;]+)"));
				}
			}
			final HttpEntity entity = response.getEntity();
			return EntityUtils.toByteArray(entity);
		} catch (final Exception e) {
			throw new IllegalStateException(e);
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (final IOException e) {
				}
			}
		}

	}

	public static CloseableHttpClient getHttpClient() {
		return HttpClients.createDefault();
	}

	public static HttpGet getHttpGet(final String url, final HttpHost proxy) {
		final RequestConfig.Builder requestConfigBuilder = getBuilder(proxy);
		final HttpGet httpGet = new HttpGet(url);
		httpGet.setConfig(requestConfigBuilder.build());
		return httpGet;
	}

	public static HttpPost getHttpPost(final String url, final HttpHost proxy, final Map<String, String> map) {
		final RequestConfig.Builder requestConfigBuilder = getBuilder(proxy);
		final HttpPost httpPost = new HttpPost(url);
		httpPost.setConfig(requestConfigBuilder.build());

		final List<NameValuePair> list = new ArrayList<NameValuePair>();
		final Iterator<Entry<String, String>> iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			final Entry<String, String> elem = iterator.next();
			list.add(new BasicNameValuePair(elem.getKey(), elem.getValue()));
		}
		if (list.size() > 0) {
			try {
				final UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "utf-8");
				httpPost.setEntity(entity);
			} catch (final Exception e) {
				throw new IllegalArgumentException(e);
			}
		}

		return httpPost;
	}

	private static RequestConfig.Builder getBuilder(final HttpHost proxy) {
		final RequestConfig.Builder requestConfigBuilder = RequestConfig.custom().setConnectionRequestTimeout(30000)
				.setSocketTimeout(30000).setConnectTimeout(30000).setCookieSpec(CookieSpecs.DEFAULT);
		if (proxy != null) {
			requestConfigBuilder.setProxy(proxy);
		}

		return requestConfigBuilder;

	}

	public static void close(final CloseableHttpClient httpClient) {
		if (httpClient != null) {
			try {
				httpClient.close();
			} catch (final IOException e) {
			}
		}
	}

	public static String getChallengeString(final JSONObject json) {
		return "gt=" + json.getString("gt") + "&challenge=" + json.getString("challenge");
	}

	public static void main(final String[] args) {
	}

}
