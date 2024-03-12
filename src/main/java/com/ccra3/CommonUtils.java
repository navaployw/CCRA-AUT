package com.ccra3;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.gson.Gson;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**W
 *
 * @author niwatroongroj
 */
public class CommonUtils {

	private static final Logger LOG = LogManager.getLogger(CommonUtils.class);

	public static final String STATUS = "status";
	public static final String MESSAGE = "message";
	public static final String DATA = "data";
	public static final String SUCCESS = "success";
	public static final String EXCEPTION = "exception";
	public static final String ENTRIES = "entries";
	
	private static final String CLASSPATH = "language/messages";

	public static Map<String, Object> response(HttpServletRequest req, HttpServletResponse res, Object entries,
			String message, Map<String, Object> addOn) {

		Map<String, Object> response = new HashMap<String, Object>();
		response.put(STATUS, 200);
		response.put(MESSAGE, message);
		response.put(ENTRIES, entries);
		if (null != addOn) {
			for (Map.Entry<String, Object> entry : addOn.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				response.put(key, value);
			}
		}
		return response;
	}

	public static Map<String, Object> responseError(HttpServletRequest request, HttpServletResponse res,
			String message) {

		Map<String, Object> response = new HashMap<String, Object>();
		response.put(STATUS, 400);
		response.put(MESSAGE, message);

		return response;
	}

	public static Map<String, Object> responseByStatus(HttpServletRequest request, HttpServletResponse res, int status,
			String message) {

		Map<String, Object> response = new HashMap<String, Object>();
		response.put(STATUS, status);
		response.put(MESSAGE, message);

		return response;
	}

	public static <T> T convertDataToObject(Object data, Class<T> clazz) throws NullPointerException {
		return new Gson().fromJson(new Gson().toJson(data), clazz);
	}

	public static <T> java.util.stream.Collector<T, ?, T> toFirstObject() {
		return Collectors.collectingAndThen(Collectors.toList(), list -> {
			if (null != list && !list.isEmpty()) {
				return list.get(0);
			}
			return null;
		});
	}
	
	public static void copyObject(Object src, Object dest) {
		
        Field[] srcFields = src.getClass().getDeclaredFields();

        for (Field srcField : srcFields) {
            try {
                Field destField = dest.getClass().getDeclaredField(srcField.getName());
                srcField.setAccessible(true);
                destField.setAccessible(true);
                destField.set(dest, srcField.get(src));
            } catch (Exception e) {
				// Handle exception
            }
        }
        
	}
	
	public static String getProperty(String key) {
		try {
			
			ResourceBundle resourceBundle = ResourceBundle.getBundle(CLASSPATH);
			return resourceBundle.getString(key);
			
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return "";
	}
	
	public static String concatLikeParam(String param, boolean start, boolean end) {

		if (StringUtils.isBlank(param)) return "";
		
		StringBuilder result = new StringBuilder();
		if (start) result.append("%");
		result.append(param);
		if (end) result.append("%");

		return result.toString();
	}
	
	public static String convertDateSqlDate(Date date, boolean isStart, boolean isEnd) {
		SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat formatDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		if (date == null) return null;
		if (isStart) {
			return formatDate.format(date) + " 00:00";
		} else if (isEnd) {
			return formatDate.format(date) + " 23:59";
		} else {
			return formatDateTime.format(date);
		}
	}
	
	public static Date convertDateForBetween(Date date, boolean isStart) throws ParseException {
		SimpleDateFormat formatDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		if (null == date) return null;
		if (isStart) {
			String sqlDate = convertDateSqlDate(date, true, false);
			if (StringUtils.isBlank(sqlDate)) return null;
			return formatDateTime.parse(sqlDate);
		} else {
			String sqlDate = convertDateSqlDate(date, false, true);
			if (StringUtils.isBlank(sqlDate)) return null;
			return formatDateTime.parse(sqlDate);
		}
	}
	
    public static Object[] joinParam(Object[] objs, Object... otherParam) {
        List<Object> params = new ArrayList<>();
        if(objs!=null){
            for(Object obj : objs) params.add(obj);
        }
        if(otherParam!=null && otherParam.length>0){
            for (Object obj : otherParam){
                params.add(obj);
            }
        }
        return params.toArray();
    }

    public static Object[] joinParam(List<Object> objs, List<Object> obj2) {
        List<Object> params = new ArrayList<>();
        if(objs!=null){
            for(Object obj : objs) params.add(obj);
        }
        if(obj2!=null){
            for (Object obj : obj2){
                params.add(obj);
            }
        }
        return params.toArray();
    }
	
}
