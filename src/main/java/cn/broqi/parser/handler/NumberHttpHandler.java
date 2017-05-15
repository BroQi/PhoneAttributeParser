package cn.broqi.parser.handler;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.broqi.parser.entity.NumAttribution;
import cn.broqi.parser.util.HttpTookit;
import cn.broqi.parser.util.JsonUtils;

@Component
public class NumberHttpHandler {

	private Logger log = LoggerFactory.getLogger(NumberHttpHandler.class);

	@Value("${mobile.number.attribution.url}")
	private String numberAttrbutionUrl;
	@Value("${mobile.number.attribution.apikey}")
	private String numberAttrbutionApiKey;

	@SuppressWarnings("unchecked")
	public NumAttribution getFrominternet(String number) {
		Map<String, String> params = new HashMap<>();
		params.put("phone", number);
		params.put("dtype", "json");
		params.put("key", numberAttrbutionApiKey);
		log.debug("查询参数:" + params);
		NumAttribution numberAttribution = null;
		try {
			String result = HttpTookit.doGet(numberAttrbutionUrl, params);
			log.debug("反馈结果" + result);
			Map<String, Object> resMap = JsonUtils.toObject(result, Map.class);
			if (resMap != null && !resMap.isEmpty()) {
				if ("200".equalsIgnoreCase(String.valueOf(resMap.get("resultcode")))) {
					Map<String, Object> resResult = (Map<String, Object>) resMap.get("result");
					numberAttribution = new NumAttribution();
					numberAttribution.setNum(number.substring(0, 7));
					numberAttribution.setProvince(String.valueOf(resResult.get("province")));
					numberAttribution.setCity(String.valueOf(resResult.get("city")));
					numberAttribution.setAreacode(String.valueOf(resResult.get("areacode")));
					numberAttribution.setRuncomp(String.valueOf(resResult.get("card")));
				} else {
					log.debug("手机号：" + number + "查询出错，错误代码：" + String.valueOf(
							resMap.get("error_code") + "， 错误原因： " + String.valueOf(resMap.get("reason"))));
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return numberAttribution;
	}

}
