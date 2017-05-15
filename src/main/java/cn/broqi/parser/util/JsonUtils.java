package cn.broqi.parser.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class JsonUtils {
	protected static Logger logger = LoggerFactory.getLogger(JsonUtils.class);
	private static ObjectMapper mapper = new ObjectMapper();

	public static String toJson(Object object) {
		try {
			return mapper.writeValueAsString(object);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T toObject(String json, Class<T> clazz) {
		try {
			return mapper.readValue(json, clazz);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("deprecation")
	public static <T> T toObject(String json, Class<T> arg1, Class<?> arg2) {
		TypeFactory t = TypeFactory.defaultInstance();
		T obj = null;
		try {
			obj = mapper.readValue(json, t.constructParametricType(arg1, arg2));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return obj;
	}

	public static <T> List<T> toObjectList(String json, Class<T> clazz) {
		try {
			return mapper.readValue(json, new TypeReference<List<T>>() {
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String getProperty(String json, String nodeName) {
		try {
			JsonNode node = mapper.readTree(json);
			return node.get(nodeName).textValue();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Long getPropertyLongValue(String json, String nodeName) {
		try {
			JsonNode node = mapper.readTree(json);
			return node.get(nodeName).longValue();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T toMap(Object object) {
		if (object == null) {
			return (T) new HashMap<String, Object>();
		}
		try {
			T t = null;
			if (Collection.class.isInstance(object)) {
				List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
				List<Object> list = (List<Object>) object;
				for (Object obj : list) {
					maps.add(noArrayToMap(obj));
				}
				t = (T) maps;
			} else if (object.getClass().isArray()) {
				Object[] objects = (Object[]) object;
				Map<String, Object>[] maps = new Map[objects.length];
				for (int i = 0; i < objects.length; i++) {
					maps[i] = noArrayToMap(objects[i]);
				}
				t = (T) objects;
			} else {
				t = (T) noArrayToMap(object);
			}
			return t;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static Map<String, Object> noArrayToMap(Object obj) {
		if (obj == null) {
			return new HashMap<String, Object>();
		}
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor property : propertyDescriptors) {
				String key = property.getName();
				// 过滤class属性
				if (!key.equals("class")) {
					// 得到property对应的getter方法
					Method getter = property.getReadMethod();
					Object value = getter.invoke(obj);
					if (Date.class.isInstance(value)) {
						Date date = (Date) value;
						value = date.getTime();
					}
					map.put(key, value);
				}
			}
			return map;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
