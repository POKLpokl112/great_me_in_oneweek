package gtapi;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;

import com.alibaba.fastjson.JSONObject;

import common.Util;
import gtcrack.GTUtil;
import offline.OfflineHack;

public class GT_Crack_Offline {

	static Logger logger = Util.getLogger(GT_Crack_Offline.class);

	public static String getValidate(final String challenge) throws Exception {
		final String d = (int) (6 * Math.random()) + "";
		final String e = (int) (300 * Math.random()) + "";

		return OfflineHack.getValidate(d, e, challenge);
	}

	public static String crack(final String registerContent) throws Exception {

		final String challenge = JSONObject.parseObject(registerContent).getString("challenge");

		final String validate = getValidate(challenge);
		logger.info("validate : " + validate);

		return "geetest_challenge=" + challenge + "&geetest_validate=" + validate + "&geetest_seccode=" + validate
				+ "|jordan";
	}

	public static void main(final String[] args) throws Exception {

		final CloseableHttpClient httpClient = GTUtil.getHttpClient();
		final HttpGet httpGet = GTUtil
				.getHttpGet("http://localhost:8080/pc-geetest/register?t=" + System.currentTimeMillis(), null);

		final String challenge = JSONObject.parseObject(GTUtil.startString(httpClient, httpGet)).getString("challenge");

		final String validate = getValidate(challenge);

		final Map<String, String> map = new HashMap<>();
		map.put("username", "极验验证");
		map.put("password", "123456");
		map.put("geetest_challenge", challenge);
		map.put("geetest_validate", validate);
		map.put("geetest_seccode", validate + "|jordan");
		final HttpPost httpPost = GTUtil.getHttpPost("http://localhost:8080/pc-geetest/validate", null, map);
		System.out.println(GTUtil.startString(httpClient, httpPost));

	}

}
