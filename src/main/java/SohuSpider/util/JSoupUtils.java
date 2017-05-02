package main.java.SohuSpider.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JSoupUtils {
	
	static String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 UBrowser/6.1.2107.204 Safari/537.36";
	/**
	 * 通过地址得到document对象
	 */
	
	public static Document getDocument(String url){
		try{
			
			Document document = Jsoup.connect(url)
					.userAgent(userAgent)
					.timeout(3000)
					.get();
			if(document == null || document.toString().trim().equals("")){ // 表示Ip被拦截或其他情况
				System.out.println("出现ip被拦截或者其他情况");
				HttpUtils.setProxyIp(); //重新设置代理ip
				getDocument(url);
			}
			return document;
		}catch(Exception e){ //链接超时等其他情况
			System.out.println("出现链接超时等其他情况");
			HttpUtils.setProxyIp(); // 换代理ip
			getDocument(url);
		}
		return getDocument(url);
		
	}
	
	/*
	public static void main(String[] args){
		String url = "https://m.sohu.com/n/557070587/";
		Document doc = getDocument(url);
		Elements paras = doc.body().select("article p");
		if(paras.isEmpty() == false ){
			for (Element p : paras) {
				System.out.println(p.text());
			}
		}
		
		//System.out.println(doc);
	}
	*/
}
