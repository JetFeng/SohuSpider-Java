package main.java.SohuSpider.service;

import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Date;

import org.json.JSONException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import main.java.SohuSpider.bean.NewsBean;
import main.java.SohuSpider.filter.BloomFilter;
import main.java.SohuSpider.util.DBStatement;
import static main.java.SohuSpider.util.XmlUtils.getAllChannels;
import static main.java.SohuSpider.util.JSoupUtils.getDocument;
import static main.java.SohuSpider.util.JsonUtils.parseRestContent;
import static main.java.SohuSpider.util.XmlUtils.writeEntryUrls;
import static main.java.SohuSpider.util.XmlUtils.loadEntryUrls;

public class SpiderService implements Serializable {
	
	//使用BloomFilter算法去重
	static BloomFilter filter = new BloomFilter();
	
	 //url阻塞队列
	BlockingQueue<String> urlQueue = null;
	
	//数据库连接
	static Connection con = DBStatement.getCon();
	
	static Statement stmt = DBStatement.getInstance();
	
	static PreparedStatement ps = null;
	
	//线程池
	static Executor executor = Executors.newFixedThreadPool(20);
	
	static String urlHost = "http://m.sohu.com";
	
	//导航页面url
	static String urlNavigation = "https://m.sohu.com/c/395/?_once_=000025_zhitongche_daohang_v3";
	
	//爬取深度
	static int DEFAULT_DEPTH = 10;
	
	static int DEFAULT_THREAD_NUM = 10;
	
	public void start() throws InterruptedException{
		
		File urlsSer = new File("urlQueue.ser"); 
		if (urlsSer.exists()){
		
			try{
			   //对象反序列化
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(urlsSer));
				urlQueue = (BlockingQueue<String>) ois.readObject();
				
				ois.close();
			} catch (Exception e) {
				e.printStackTrace();
			}  
		}
		else{
			//创建阻塞队列
			urlQueue = new LinkedBlockingQueue<String>();
			
			//获取入口Url
			List<String> urlChannels = genEntryChannel(urlNavigation);
			
			for (String url : urlChannels) {
				urlQueue.add(url);
				System.out.println(url);
			}
		}
		
		
		//添加程序监听结束,程序结束时候应序列化两个重要对象--urlQueue和filter
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable(){

			public void run() {
				System.out.println(urlQueue.isEmpty());
				try{
					if (urlQueue.isEmpty() == false) {
						//序列化urlQueue
						ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("urlQueue.ser"));
						os.writeObject(urlQueue);
						os.close();
							
					 }
						
					//序列化bits
					ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("bits.ser"));
					os.writeObject(filter.getBitset());
					os.close();
				} catch(Exception e) {
					e.printStackTrace();
				}
				
			}
		}));
				
		for(int i = 0; i < DEFAULT_THREAD_NUM; i++){
			Thread a = new Thread(new Runnable() {

				public void run() {
				     while (true) {
	                        String url = getAUrl();
	                        if (!filter.contains(url)) {
	                            filter.add(url);
	                            System.out.println(Thread.currentThread().getName()+"正在爬取url:" + url);
	                            if (url != null) {
										crawler(url);
	                            }
	                        }else {
	                            System.out.println("此url存在，不爬了." + url);
	                        }
	                    }
					
				}
				
			});
			executor.execute(a);
		}
		
		//线程池监视线程
		new Thread(new Runnable(){  
			public void run() {
				while(true) {
					try{
						if (((ThreadPoolExecutor)executor).getActiveCount() < 10) {
							Thread a = new Thread(new Runnable() {
								public void run() {
									while (true) {
										String url = getAUrl();
										if (!filter.contains(url)) {
											filter.add(url);
											System.out.println(Thread.currentThread().getName()+"正在爬取url:" + url);
											if (url != null) {
												crawler(url);
											}
										}else {
											System.out.println("此url存在， 不爬了." + url);
										}
									}
								}
							});
							executor.execute(a);
							if (urlQueue.size() == 0) {
								System.out.println("队列为0了！！！！！！！");
							}
						}
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
			}
			
		}).start();
		
	}
	
	/* 从导航页解析入口新闻url */
	public static List<String> genEntryChannel (String startUrl) {
		
		List<String> urlArray = new ArrayList<String>();
		//小说类别的url不需要，其url特征是含有单词read
		String pattern = "^/c.*";
		
		Document doc = getDocument(startUrl);
		Elements Urls = doc.select("a.h3Sub");
		for (Element url : Urls) {
			String link = url.attr("href");
			if (Pattern.matches(pattern, link) == true) {
				urlArray.add(urlHost + link);
			}
		}
		
		writeEntryUrls(urlArray);
		return urlArray;
	}

	
	/* 爬取新闻网页 */
	public void crawler(String url) {
		
		Document doc = getDocument(url); //返回的Document对象一定是正确的
		
		String pattern = ".*/n/[0-9]+/.*";
		//System.out.println(Pattern.matches(pattern, url));
		if (Pattern.matches(pattern, url)){
			
			String title = "";
			String category = null;
			String sourceFrom = null;
			String date = null;
			String content = "";
			String editor = null;
			
			NewsBean news = new NewsBean();
			
			news.setUrl(url);
			
			try{
				/**
				 * 新闻标题格式 题目-类别-手机搜狐
				 * 但是有些题目中本身就含有 "-" 
				 */
				String[] temp = doc.title().trim().split("-");
				category = temp[temp.length - 2].substring(0, 2);
				for (int i = 0; i < temp.length - 2; i++){
					title += temp[i];
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				//e.printStackTrace();
				return ; 
			}
			
			news.setCategory(category);
			news.setTitle(title);
			
			Elements articleInfo = doc.body().select("div.article-info");
			if ( articleInfo.isEmpty() == false) {
				try{
					String[] temp = articleInfo.first().text().split(" ");
					sourceFrom = temp[0];
					date = temp[1];
				} catch (ArrayIndexOutOfBoundsException e) {
					e.printStackTrace();
					return ;
				}
			}
			news.setSourceFrom(sourceFrom);
			news.setDate(date);
			
			Elements paras = doc.body().select("article p");
			if ( paras.isEmpty() == false) {
				for (Element e : paras) {
					content += e.text();
					content += "\n";
				}
			}
			
			news.setContent(content);
			
			if (content.length() > 8000) {
				return ;
			}
			
			
			Elements divEditor = doc.body().select("div.editor");
			if (divEditor.isEmpty() == false) {
				editor = divEditor.first().text();
			}
			news.setEditor(editor);
			
			//打印用户信息
	        System.out.println("爬取成功：" + news);
	
	        String sql = "insert into news_info " +
	                "(title,url,cate,date,srcFrom,content,editor) " +
	                "values (?,?,?,?,?,?,?)";
	        try {
	            ps = con.prepareStatement(sql, Statement.SUCCESS_NO_INFO);
	            ps.setString(1, news.getTitle());
	            ps.setString(2, news.getUrl());
	            ps.setString(3, news.getCategory());
	            ps.setString(4, news.getDate());
	            ps.setString(5, news.getSourceFrom());
	            ps.setString(6, news.getContent());
	            ps.setString(7, news.getEditor());
	            //存储news
	            ps.executeUpdate();
	        }catch (Exception e){
	            e.printStackTrace();
	        }
		}
		
		//新闻正文url的特征  https://m.sohu.com/n/488483157/
		Elements urlCandidates = doc.body().select("a[href~=(.*/n/[0-9]+/)|(.*/c.*)]");
		for (Element e : urlCandidates){
			url = urlHost + e.attr("href");
			try {
				urlQueue.put(url);
			} catch (InterruptedException e1) {
				
				e1.printStackTrace();
			}
		}
		
	}
	
	
	public String getAUrl() {
		String tmpAUrl;
        try {
            tmpAUrl= urlQueue.take();
            return tmpAUrl;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
	}
	

}

