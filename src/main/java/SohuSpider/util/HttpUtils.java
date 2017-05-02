package main.java.SohuSpider.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HttpUtils {
	
	static String rootPath = System.getProperty("user.dir");
	/**
	 *  设置代理Ip
	 *  
	 */
	public static void setProxyIp(){
		try{
			List<String> ipList = new ArrayList<String>();
			BufferedReader proxyIpReader = new BufferedReader(new FileReader(rootPath + "/src/main/resources/proxyip.txt"));
		    
			String ip = "";
			while((ip = proxyIpReader.readLine())!= null){
				ipList.add(ip);
			}
			
			Random random = new Random();
			int randomInt = random.nextInt(ipList.size());
			String ipport = ipList.get(randomInt);
			String proxyIp = ipport.substring(0, ipport.lastIndexOf(":"));
			String proxyPort = ipport.substring(ipport.lastIndexOf(":") + 1, ipport.length());
			
			System.setProperty("http.maxRedirects", "50");
			System.getProperties().setProperty("proxySet", "true");
			System.getProperties().setProperty("http.proxyHost", proxyIp);
			System.getProperties().setProperty("http.proxyPort", proxyPort);
			
			System.out.println("设置代理ip为: " + proxyIp + "端口号为: " + proxyPort);
		}catch(Exception e){
			System.err.println("重新设置代理ip");
			setProxyIp();
		}
	}
}
