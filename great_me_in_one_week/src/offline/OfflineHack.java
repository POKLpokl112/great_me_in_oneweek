package offline;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import common.Util;
import gtcrack.gtmouse.GT_ParamMask;

public class OfflineHack {

	private static Invocable invokeEngine;

	static {
		final ScriptEngineManager manager = new ScriptEngineManager();
		final ScriptEngine engine = manager.getEngineByMimeType("text/javascript");
		try {
			engine.eval(Util.toString(GT_ParamMask.class.getClass().getResourceAsStream("/off")));
		} catch (final ScriptException e) {
			throw new IllegalArgumentException(e);
		}
		invokeEngine = (Invocable) engine;
	}

	public static String md5(final String gt) throws Exception {
		return (String) invokeEngine.invokeFunction("md5", gt);
	}

	public static String getH(final String d, final String e) {
		final String x = d.substring(0, 9);
		final String y = e.substring(10, 19);

		final StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 9; i++) {
			sb.append(i % 2 == 0 ? x.charAt(i) + "" : y.charAt(i) + "");
		}

		return sb.toString();
	}

	public static String getMove(final String h) throws Exception {
		return ((Double) invokeEngine.invokeFunction("getX", h.substring(4, 9))).intValue() + "";
	}

	public static String getValidate(final String l, final String challenge) throws Exception {
		return GT_ParamMask.get(l, challenge);
	}

	public static String getValidate(final String l, final String d, final String e, final String challenge)
			throws Exception {
		return GT_ParamMask.get(l, challenge) + "_" + GT_ParamMask.get(d, challenge) + "_"
				+ GT_ParamMask.get(e, challenge);
	}

	public static String getValidate(final String d, final String e, final String challenge) throws Exception {
		final String l = getMove(getH(md5(d), md5(e)));

		return getValidate(l, d, e, challenge);

	}

	public static void main(final String[] args) throws Exception {
		// System.out.println(md5("34"));// c81e728d9d4c2f636f067f89cc14862c//2
		// e369853df766fa44e1ed0ff613f563bd//34

		// System.out.println(getMove(getH("c81e728d9d4c2f636f067f89cc14862c",
		// "e369853df766fa44e1ed0ff613f563bd")));//40

		System.out.println(getValidate("38", "2", "34", "a5bfc9e07964f8dddeb95fc584cd965d03"));
	}

}
