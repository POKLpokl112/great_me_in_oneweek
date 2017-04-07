package gtapi;

import org.slf4j.Logger;

import com.alibaba.fastjson.JSONObject;

import common.Util;
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
	}

}
