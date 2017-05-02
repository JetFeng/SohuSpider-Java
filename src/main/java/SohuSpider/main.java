package main.java.SohuSpider;

import main.java.SohuSpider.service.SpiderService;

public class main{
	/**
	 * 搜狐爬虫入口
	 * 
	 * @param 
	 * @throws InterruptedException 
	 * 
	 */
	public static void main(String[] args) throws InterruptedException{
		SpiderService spider = new SpiderService();
		spider.start();
	}
}
