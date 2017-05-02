package main.java.SohuSpider.util;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JsonUtils {
	public static String parseRestContent (String json) throws JSONException {
		JSONTokener jsonTok = new JSONTokener(json);
		JSONObject jsonObj = new JSONObject(jsonTok);
		
		String restContent = jsonObj.getString("rest_content");
		return restContent;
	}
}
