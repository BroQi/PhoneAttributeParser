package cn.broqi.parser.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

	/**
	 * 编码自增
	 * 
	 * @param prefix
	 *            前缀
	 * @param code
	 *            当前最大code
	 * @return 默认code长度为10
	 */
	public static String generateCode(String prefix, String code) {
		int prefixLength = prefix.length();
		String suffix = "1";
		int conut = 10;
		if (StringUtils.isNotEmpty(code)) {
			conut = code.length();
			if (code.indexOf(prefix) == 0) {
				suffix = code.replaceAll(prefix, "");
				Long l = Long.valueOf(suffix);
				l++;
				suffix = l.toString();
			}
		}
		StringBuilder builder = new StringBuilder(prefix);
		for (int i = 0; i < conut - prefixLength - suffix.length(); i++) {
			builder.append("0");
		}
		builder.append(suffix);
		return builder.toString();
	}

	public static String generateCode(String prefix, long maxId) {
		int prefixLength = prefix.length();
		String suffix = String.valueOf(maxId);
		int conut = 15;
		StringBuilder builder = new StringBuilder(prefix);
		for (int i = 0; i < conut - prefixLength - suffix.length(); i++) {
			builder.append("0");
		}
		builder.append(suffix);
		return builder.toString();
	}

	/**
	 * 将List转化为指定字符串分割的长字符串
	 * 
	 * @param rs
	 * @param split
	 * @return
	 */
	public static String list2String(List<?> rs, String split) {
		if (rs == null || rs.size() <= 0) {
			return null;
		}
		StringBuffer temp = new StringBuffer();
		for (int i = 0; i < rs.size(); i++) {
			temp.append(rs.get(i));
			temp.append(split);
		}
		StringBuffer result = new StringBuffer(temp.substring(0, temp.length() - split.length()));
		return result.toString();
	}

	/**
	 * 将NULL转为""
	 * 
	 * @param obj
	 * @return
	 */
	public static Object getStrValue(Object obj) {
		if (obj != null) {
			return obj;
		} else {
			return "";
		}

	}

	/**
	 * @param xml
	 * @return
	 */
	public static String replaceXmlStr(String str) {
		Pattern p = Pattern.compile("\\s*|\t|\r|\n");
		Matcher m = p.matcher(str);
		String afterXml = m.replaceAll("");
		return afterXml;
	}

	/**
	 * 方法描述：把Object转化为字符串
	 * 
	 * @param
	 * @param str
	 * @param
	 * @return
	 * @return String
	 */
	public static String objToString(Object str) {
		if (null == str) {
			return "";
		} else {
			return String.valueOf(str);
		}
	}

	/**
	 * 字符串为空
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isEmpty(String s) {
		return !isNotEmpty(s);
	}

	/**
	 * 字符串不为空
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isNotEmpty(String s) {
		return s != null && s.length() > 0;
	}

	/**
	 * 改变编码
	 * 
	 * @param s
	 * @param charsetName
	 * @return
	 */
	public static String changeEncoding(String s, String charsetName) {
		try {
			s = new String(s.getBytes("iso-8859-1"), charsetName);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return s;
	}

	/**
	 * 将Date类型转成String串型
	 * 
	 * @param date
	 *            日期类型,pattern 指日志类型样式
	 * @return
	 */
	public static String dateToString(Date date, String pattern) {

		if (date == null) {
			return "";
		}
		try {
			SimpleDateFormat sfDate = new SimpleDateFormat(pattern);
			sfDate.setLenient(false);
			return sfDate.format(date);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 将带有逗号","的字符串转化为integer数组
	 * 
	 * @param str
	 * @return
	 */
	public static Integer[] getIntegerArrayByStr(String str) {
		String[] strs = str.split(",");
		Integer[] ints = new Integer[strs.length];
		for (int i = 0; i < strs.length; i++) {
			ints[i] = Integer.parseInt(strs[i]);
		}
		return ints;
	}

	/**
	 * 将数组转换逗号分割的字符串 Description: ModifyHistory:
	 */
	public static String getStrByObjectArray(Object[] ids) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < ids.length; i++) {
			if (i == (ids.length - 1))
				sb.append(ids[0].toString());
			else {
				sb.append(ids[0].toString() + ",");
			}
		}
		return sb.toString();
	}

	/**
	 * 将带有逗号","的字符串转化为StringList
	 * 
	 * @param str
	 * @return
	 */
	public static List<String> splitStrToList(String str) {
		String[] strs = str.split(",");
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < strs.length; i++) {
			list.add(strs[i]);
		}
		return list;
	}

	/**
	 * 将字符流转换为字符串
	 * 
	 * @param is
	 * @return
	 */
	public static String getStrByStream(InputStream is) {
		String encoding = "UTF-8";
		StringBuffer str = new StringBuffer();
		try {
			BufferedInputStream in = new BufferedInputStream(is);
			byte[] buffer = new byte[1024];
			int i = 0;
			do {
				i = in.read(buffer);
				if (i > 0)
					str.append(new String(buffer, 0, i, encoding));
			} while (i == 1024);
			return str.toString();
		} catch (Exception e) {
			throw new RuntimeException("字符串转换错误", e);
		}
	}

	/**
	 * 将一个字符串的首字母改为大写或者小写
	 * 
	 * @param srcString
	 *            源字符串
	 * @param flag
	 *            大小写标识，ture小写，false大些
	 * @return 改写后的新字符串
	 */
	public static String toLowerCaseInitial(String srcString, boolean flag) {
		StringBuilder sb = new StringBuilder();
		if (flag) {
			sb.append(Character.toLowerCase(srcString.charAt(0)));
		} else {
			sb.append(Character.toUpperCase(srcString.charAt(0)));
		}
		sb.append(srcString.substring(1));
		return sb.toString();
	}

	/**
	 * 格式化文件路径，将其中不规范的分隔转换为标准的分隔符,并且去掉末尾的"/"符号。
	 * 
	 * @param path
	 *            文件路径
	 * @return 格式化后的文件路径
	 */
	public static String formatPath(String path) {
		String reg0 = "\\\\＋";
		String reg = "\\\\＋|/＋";
		String temp = path.trim().replaceAll(reg0, "/");
		temp = temp.replaceAll(reg, "/");
		if (temp.endsWith("/")) {
			temp = temp.substring(0, temp.length() - 1);
		}
		if (System.getProperty("file.separator").equals("\\")) {
			temp = temp.replace('/', '\\');
		}
		return temp;
	}

	/**
	 * 格式化文件路径，将其中不规范的分隔转换为标准的分隔符,并且去掉末尾的"/"符号(适用于FTP远程文件路径或者Web资源的相对路径)。
	 * 
	 * @param path
	 *            文件路径
	 * @return 格式化后的文件路径
	 */
	public static String formatPath4Ftp(String path) {
		String reg0 = "\\\\＋";
		String reg = "\\\\＋|/＋";
		String temp = path.trim().replaceAll(reg0, "/");
		temp = temp.replaceAll(reg, "/");
		if (temp.endsWith("/")) {
			temp = temp.substring(0, temp.length() - 1);
		}
		return temp;
	}

	/**
	 * 获取文件父路径
	 * 
	 * @param path
	 *            文件路径
	 * @return 文件父路径
	 */
	public static String getParentPath(String path) {
		return new File(path).getParent();
	}

	/**
	 * 获取相对路径
	 * 
	 * @param fullPath
	 *            全路径
	 * @param rootPath
	 *            根路径
	 * @return 相对根路径的相对路径
	 */
	public static String getRelativeRootPath(String fullPath, String rootPath) {
		String relativeRootPath = null;
		String _fullPath = formatPath(fullPath);
		String _rootPath = formatPath(rootPath);

		if (_fullPath.startsWith(_rootPath)) {
			relativeRootPath = fullPath.substring(_rootPath.length());
		} else {
			throw new RuntimeException("要处理的两个字符串没有包含关系，处理失败！");
		}
		if (relativeRootPath == null)
			return null;
		else
			return formatPath(relativeRootPath);
	}

	/**
	 * 获取当前系统换行符
	 * 
	 * @return 系统换行符
	 */
	public static String getSystemLineSeparator() {
		return System.getProperty("line.separator");
	}

	/**
	 * 将字符串的首字母转为小写
	 * 
	 * @param resStr
	 *            源字符串
	 * @return 首字母转为小写后的字符串
	 */
	public static String firstToLowerCase(String resStr) {
		if (resStr == null) {
			return null;
		} else if ("".equals(resStr.trim())) {
			return "";
		} else {
			StringBuffer sb = new StringBuffer();
			Character c = resStr.charAt(0);
			if (Character.isLetter(c)) {
				if (Character.isUpperCase(c))
					c = Character.toLowerCase(c);
				sb.append(resStr);
				sb.setCharAt(0, c);
				return sb.toString();
			}
		}
		return resStr;
	}

	/**
	 * 将字符串的首字母转为大写
	 * 
	 * @param resStr
	 *            源字符串
	 * @return 首字母转为大写后的字符串
	 */
	public static String firstToUpperCase(String resStr) {
		if (resStr == null) {
			return null;
		} else if ("".equals(resStr.trim())) {
			return "";
		} else {
			StringBuffer sb = new StringBuffer();
			Character c = resStr.charAt(0);
			if (Character.isLetter(c)) {
				if (Character.isLowerCase(c))
					c = Character.toUpperCase(c);
				sb.append(resStr);
				sb.setCharAt(0, c);
				return sb.toString();
			}
		}
		return resStr;
	}

	/**
	 * 比较两个版本号的大小
	 * 
	 * @param version1
	 *            版本号1
	 * @param version2
	 *            版本号2
	 * @return version1 > version2 : 1 version1 = version2 :0 version1 <
	 *         version2 :-1
	 */
	public static int compareVersion(String version1, String version2) {
		int index = 0;
		if (isEmpty(version1) && !isEmpty(version2)) {
			index = -1;
		} else if (isEmpty(version2) && !isEmpty(version1)) {
			index = 1;
		} else if (!isEmpty(version2) && !isEmpty(version1)) {
			if (version1.length() - version2.length() >= 0) {
				for (int i = 0; i < version1.length(); i++) {
					if (i < version2.length()) {
						int tempInt = version1.charAt(i) - version2.charAt(i);
						if (tempInt > 0) {
							index = 1;
							break;
						} else if (tempInt < 0) {
							index = -1;
							break;
						}
					}
				}
			}
		}
		return index;
	}

	public static String split(String params, int potion) {

		if (params != null && params.indexOf(".") != -1) {
			params = params.substring(0, params.indexOf(".") + potion);
		}

		return params;
	}

	/**
	 * in条件语句
	 * 
	 * @param
	 * @return
	 */
	public static String getInWhere(String inwhere) {
		StringBuffer sb = new StringBuffer();
		if (null != inwhere) {
			sb.append("(");
			String[] inValue = inwhere.split(",");
			for (int i = 0; i < inValue.length; i++) {
				if (i != inValue.length - 1) {
					sb.append("'" + inValue[i] + "',");
				} else {
					sb.append("'" + inValue[i] + "'");
				}
			}
		}
		sb.append(")");
		return sb.toString();
	}

	/**
	 * in条件语句(List)
	 * 
	 * @param
	 * @return
	 */
	public static String getListInWhere(List<String> inwhere) {
		StringBuffer sb = new StringBuffer();
		if (null != inwhere) {
			sb.append("(");
			if (null != inwhere && inwhere.size() > 0) {
				for (int i = 0; i < inwhere.size(); i++) {
					if (i != inwhere.size() - 1) {
						sb.append("'" + inwhere.get(i) + "',");
					} else {
						sb.append("'" + inwhere.get(i) + "'");
					}
				}
			} else {
				sb.append("''");
			}

		}
		sb.append(")");
		return sb.toString();
	}

	/**
	 * 将html转换为特殊字符
	 * 
	 * @param str
	 * @return
	 */
	public static String htmlTOSpecialChars(String str) {
		if (StringUtils.isNotEmpty(str)) {
			str = str.replaceAll("&", "&amp;");
			str = str.replaceAll("\\$", "&yen;");
			str = str.replaceAll("<", "&lt;");
			str = str.replaceAll(">", "&gt;");
		}
		return str;
	}

	/**
	 * 将特殊字符转换为html
	 * 
	 * @param str
	 * @return
	 */
	public static String specialCharsToHtml(String str, boolean isOutJsp) {
		if (StringUtils.isNotEmpty(str)) {
			str = str.replaceAll("&lt;", "<");
			str = str.replaceAll("&gt;", ">");
			if (isOutJsp) {
				str = str.replaceAll("&yen;", "\\$");
				str = str.replaceAll("￥", "\\$");
				Pattern p = Pattern.compile("\\t*|\r|\n");
				Matcher m = p.matcher(str);
				str = m.replaceAll("");
				str = str.replaceAll("\\\r\\\n", "");
			}
			str = str.replaceAll("&amp;", "&");
		}
		return str;
	}

	/**
	 * 读取
	 * 
	 * @param httpUrl
	 *            http连接地址 比如"http://www.baidu.com/#wd=java"或者
	 *            "http://www.baidu.com/?wd=java"
	 * @return
	 */
	public static String htmlByUrl(String httpUrl, String mathod) {
		String data = "";
		try {
			URL url = new URL(httpUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod(mathod);
			connection.setDoOutput(true);
			connection.setRequestProperty("User-agent", "Mozilla/4.0");
			InputStream is = connection.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(is, "utf-8"));
			String str = null;
			while ((str = in.readLine()) != null) {
				data += str;
			}
			connection.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * 比较两个值的 b比a多 或b比a少
	 * 
	 * @param b
	 * @param a
	 * @return
	 */
	public static List<String> more(String[] b, String[] a) {
		List<String> temp = new ArrayList<String>();
		int t = 0;
		String tempString = "";
		boolean exist = false;
		for (int a1 = 0; a1 < a.length; a1++) {
			for (int b1 = 0; b1 < b.length; b1++) {
				if (a[a1].equals(b[b1])) {
					exist = true;
					break;
				} else {
					exist = false;
					tempString = a[a1];
				}
			}
			if (!exist) {
				temp.add(t, tempString);
				t++;
			}
		}
		return temp;
	}

	/**
	 * 获取制定字符串内容
	 * 
	 * @param str：要获取的字符串
	 * @param startNum
	 * @param endNum
	 * @return
	 */
	public static String getBrithday(String str, int startNum, int endNum) {
		if (!isEmpty(str)) {
			return str.substring(startNum, endNum).substring(0, 4) + "-"
					+ str.substring(startNum, endNum).substring(4, 6) + "-"
					+ str.substring(startNum, endNum).substring(6, str.substring(startNum, endNum).length());
		} else {
			return null;
		}
	}

	/**
	 * @Title: getGender @Description: 获取性别数字 @param @param str @param @param
	 *         startNum @param @param endNum @param @return： @return
	 *         String： @throws
	 */
	public static String getGender(String str, int startNum, int endNum) {
		if (!isEmpty(str)) {
			return str.substring(startNum, endNum);
		} else {
			return null;
		}
	}

	/**
	 * @Title: checkNum @Description: @param @return： @return boolean： @throws
	 */
	public static Map<String, Boolean> checkString(String str) {
		boolean isDigit = false;// 定义一个boolean值，用来表示是否包含数字:isDigit == true 包含数字
		boolean isLetter = false;// 定义一个boolean值，用来表示是否包含字母：isLetter == true
									// 包含字母
		// String str = "aaasssfff"; // 假设有一个字符串
		for (int i = 0; i < str.length(); i++) { // 循环遍历字符串
			if (Character.isDigit(str.charAt(i))) { // 用char包装类中的判断数字的方法判断每一个字符
				isDigit = true;
			}
			if (Character.isLetter(str.charAt(i))) { // 用char包装类中的判断字母的方法判断每一个字符
				isLetter = true;
			}
		}
		Map<String, Boolean> checkMap = new HashMap<String, Boolean>();
		checkMap.put("isDigit", isDigit);
		checkMap.put("isLetter", isLetter);
		return checkMap;
	}

	/**
	 * 检查问卷格式
	 * 
	 * @param questin
	 * @return
	 */
	public static boolean checkQuestion(String question) {

		String[] qArray = question.split("\\|");
		for (int i = 0; i < qArray.length; i++) {
			if (qArray[i].indexOf("[") < 0 || qArray[i].indexOf("]") < 0
					|| qArray[i].indexOf("[") > qArray[i].indexOf("]")) {
				return false;
			}

			String[] str = qArray[i].split("[\\[\\]]");

			if (str.length > 2) {
				for (int j = 2; j < str.length; j++) {
					if (StringUtils.isNotEmpty(str[j].trim())) {
						return false;
					}
				}
			}
			if (str.length < 2) {
				return false;
			}

			if (StringUtils.isEmpty(str[1])) {
				return false;
			}
		}

		return true;

	}

	/**
	 * 返回一个非null的字符串
	 * 
	 * @param res
	 * @return
	 */
	public static String getValue(String res) {
		if (res == null) {
			res = "";
		}
		return res;
	}

	public static void main(String[] args) {
		String s = "1.咋说的空间和 [asd] | asdas [asdasd]";
		System.out.println(StringUtils.checkQuestion(s));
	}
}
