package gtapi;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;

import com.alibaba.fastjson.JSONObject;

import common.Util;
import gtcrack.GTConfig;
import gtcrack.GTUtil;
import gtcrack.PhotoCompare;
import gtcrack.PictureUtil;
import gtcrack.gtmouse.GT_Hacker;
import gtcrack.gtmouse.GT_MouseMaker;
import gtcrack.gtmouse.GT_ParamMask;
import gtcrack.webp.WebpReader;

public class GT_Crack {

	public static Logger logger = Util.getLogger(GT_Crack.class);

	private static String getRegister(final String registerContent) throws Exception {

		return GTUtil.getChallengeString(JSONObject.parseObject(registerContent));
	}

	private static int getMove(final byte[] fullbgByte, final byte[] bgByte) throws Exception {
		return PhotoCompare.getImageDefferencePoint(
				PictureUtil.repairImage(WebpReader.readByte(fullbgByte), PictureUtil.getPicList()),
				PictureUtil.repairImage(WebpReader.readByte(bgByte), PictureUtil.getPicList()));
	}

	private static String getURL(final int move, final String challenge, final String challengeString)
			throws Exception {

		final String userresponse = GT_ParamMask.get(move + "", challenge);
		final Map<Integer, List<int[]>> map = GT_MouseMaker.getMouseMove(move);
		final int passtime = map.keySet().iterator().next();
		final List<int[]> list = map.values().iterator().next();
		final String a = GT_Hacker.getA1(list);

		return "http://api.geetest.com/ajax.php?imgload=" + (50 + (int) (Math.random() * 15)) + "&callback=geetest_"
				+ System.currentTimeMillis() + "&" + challengeString + "&userresponse=" + userresponse + "&passtime="
				+ passtime + "&a=" + a;
	}

	private static String getGT(final CloseableHttpClient httpClient, final HttpGet httpGet, final String url,
			final String refererURL) throws Exception {
		httpGet.setURI(new URI(url));

		httpGet.setHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36(KHTML,like Gecko) Chrome/49.0.2623.75 Safari/537.36");
		httpGet.setHeader("Referer", refererURL);

		final String gtString = GTUtil.startString(httpClient, httpGet);

		return gtString;
	}

	public static String crackGT(final CloseableHttpClient httpClient, final HttpGet httpGet, final int offset,
			final String registerContent, final String refererURL) throws Exception {

		final String challengeString = getRegister(registerContent);
		final String challenge1 = Util.parseRegex(challengeString, "challenge=([^&]+)");

		httpGet.setURI(new URI(GTConfig.getGetURL(challengeString)));
		final String picString = "{" + Util.parseRegex(GTUtil.startString(httpClient, httpGet), "\\(\\{(.+)\\}\\)")
				+ "}";
		final JSONObject picData = JSONObject.parseObject(picString);

		final String fullbgURL = "http://static.geetest.com/" + picData.getString("fullbg");
		final String bgURL = "http://static.geetest.com/" + picData.getString("bg");

		final byte[] fullbgByte = GTUtil.loadUrlContent(fullbgURL);
		final byte[] bgByte = GTUtil.loadUrlContent(bgURL);

		// Util.saveAsFile(new File("d://full.webp"), fullbgByte);
		// Util.saveAsFile(new File("d://no.webp"), bgByte);

		final int move = getMove(fullbgByte, bgByte) - offset;
		final String challenge = picData.getString("challenge");

		final String url = getURL(move, challenge, challengeString);

		final String gtString = getGT(httpClient, httpGet, url, refererURL);

		logger.info(gtString);
		if (!gtString.contains("validate")) {
			return null;
		}

		GTUtil.close(httpClient);
		final String validate = Util.parseRegex(gtString, "validate\": \"([^\"]+)");
		return "geetest_challenge=" + challenge1 + "&geetest_validate=" + validate + "&geetest_seccode=" + validate
				+ "|jordan";

	}

	public static String crackGTAlong(final CloseableHttpClient httpClient, final HttpGet httpGet, final int offset,
			final String registerURL, final String refererURL) throws Exception {

		httpGet.setURI(new URI(registerURL + System.currentTimeMillis()));
		final String registerContent = getRegister(GTUtil.startString(httpClient, httpGet));

		return crackGT(httpClient, httpGet, offset, registerContent, refererURL);
	}

	public static void main(final String[] args) throws Exception {
	}

}
