package main.java.SohuSpider.miniSpider;

import static main.java.SohuSpider.util.JSoupUtils.getDocument;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/*
 * 获取可用代理ip
 */
public class IpSpider {
	
	//代理ip网址
	static String proxyHost = "http://www.xicidaili.com/nn/1";
	
	static String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 UBrowser/6.1.2107.204 Safari/537.36";
	
	//测试Ip代理的网址
	static String testUrl = "http://ip.chinaz.com/getip.aspx";
	
	static void getProxyIp() {
		try {
			//得到项目根目录
			String rootPath = System.getProperty("user.dir"); 
			//System.out.println(rootPath);
			BufferedWriter proxyIpWriter = new BufferedWriter(new FileWriter(rootPath + "/src/main/resources/proxyip.txt"));
			Document doc = getDocument(proxyHost);
			Elements ips = doc.select("#ip_list tr"); 
			//System.out.println(ips.size());
			
			for (Element e : ips) {
				Elements ip = e.select("td");
				if (ip.size() > 2){
					String ipAddr = ip.get(1).text();
					int port = Integer.parseInt(ip.get(2).text());
					if (testIp(ipAddr,port)) {
						System.out.println(ipAddr + ":" + port + "  可用");
						proxyIpWriter.write(ipAddr + ":" + port);
						proxyIpWriter.newLine();
					}
				}
			}
			proxyIpWriter.flush();
			proxyIpWriter.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	static boolean testIp(String ip, int port) {
		try{
			
			//如果3s内没有响应，则该ip不可用
			Document doc = Jsoup.connect(testUrl)
					            .userAgent(userAgent)
					            .proxy(ip, port)
					            .timeout(3000)
					            .get();
			return true;
		} catch(Exception e) {
			System.out.println("访问超时");
			return false;
		}
	}
	
	public static void main(String[] args) {
		/*
		//添加程序监听结束
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable(){
			public void run() {
				System.out.println("程序结束了!");
				
			}
			
		}));
		*/
		getProxyIp();
		System.out.println("成功获取可用代理Ip!");
	}
	
}
