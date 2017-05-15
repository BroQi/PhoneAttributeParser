package cn.broqi.parser.controller;

import java.beans.PropertyEditorSupport;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class BaseController extends MultiActionController {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	protected ModelMap getSuccessMsg(Object data, String message) {
		ModelMap modelMap = new ModelMap();
		modelMap.put("data", data);
		modelMap.put("message", message);
		modelMap.put("code", 200);
		return modelMap;
	}

	protected ModelMap getSuccessMsg(Object data) {
		return getSuccessMsg(data, "操作成功");
	}

	protected ModelMap getSuccessMsg(String message) {
		return getSuccessMsg(null, message);
	}

	protected ModelMap getSuccessMsg() {
		return getSuccessMsg(null);
	}

	protected ModelMap getFailMsg(String message) {
		ModelMap modelMap = new ModelMap();
		modelMap.put("statusCode", 300);
		modelMap.put("message", message);
		return modelMap;
	}

	protected ModelMap getFailMsg() {
		return getFailMsg("操作出现错误");
	}

	/**
	 * ����Ҫͨ��spring IOC ע��һ��date����ʱ��Ҫ��date���ͽ���ת��
	 * 
	 * @param binder
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		// ������Ҫת��ΪDate���͵����ԣ�ʹ��DateEditor���д���
		binder.registerCustomEditor(Date.class, new DateEditor());
	}

	/**
	 * ����Ҫͨ��spring IOC ע��һ��date����ʱ��Ҫ��date���ͽ���ת��
	 * 
	 * @param binder
	 */
	@Override
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		// ������Ҫת��ΪDate���͵����ԣ�ʹ��DateEditor���д���
		binder.registerCustomEditor(Date.class, new DateEditor());
	}
}

class DateEditor extends PropertyEditorSupport {

	private static final DateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private static final DateFormat TIMEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	private DateFormat dateFormat;
	private boolean allowEmpty = true;

	public DateEditor() {
	}

	public DateEditor(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	public DateEditor(DateFormat dateFormat, boolean allowEmpty) {
		this.dateFormat = dateFormat;
		this.allowEmpty = allowEmpty;
	}

	/**
	 * Parse the Date from the given text, using the specified DateFormat.
	 */
	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (this.allowEmpty && !StringUtils.hasText(text)) {
			// Treat empty String as null value.
			setValue(null);
		} else {
			try {
				if (this.dateFormat != null)
					setValue(this.dateFormat.parse(text));
				else {
					if (text.contains(":"))
						setValue(TIMEFORMAT.parse(text));
					else
						setValue(DATEFORMAT.parse(text));
				}
			} catch (ParseException ex) {
				throw new IllegalArgumentException("Could not parse date: " + ex.getMessage(), ex);
			}
		}
	}

	/**
	 * Format the Date as String, using the specified DateFormat.
	 */
	@Override
	public String getAsText() {
		Date value = (Date) getValue();
		DateFormat dateFormat = this.dateFormat;
		if (dateFormat == null)
			dateFormat = TIMEFORMAT;
		return (value != null ? dateFormat.format(value) : "");
	}
}