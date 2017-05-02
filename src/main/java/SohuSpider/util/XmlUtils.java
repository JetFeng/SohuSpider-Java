package main.java.SohuSpider.util;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;


public class XmlUtils {
	
	//项目根目录
	static String rootPath = System.getProperty("user.dir");
	
	public static List<String> getAllChannels() {
		return loadEntryUrls();
	}
	
	public static void writeEntryUrls(List<String> urlArray){
		SAXReader sr = new SAXReader();
		try{
			Document doc = sr.read(new File(rootPath +"/src/main/resources/entry-config.xml"));
			
			Element root = doc.getRootElement();
			
			if (root.elements().isEmpty() == false) {
				return ;
			}
			
			for (String url : urlArray) {
				root.addElement("url").addText(url);
			}
			
	        FileOutputStream out =new FileOutputStream(new File(rootPath + "/src/main/resources/entry-config.xml"));
	        // 指定文本的写出的格式：
	        OutputFormat format=OutputFormat.createPrettyPrint();   //漂亮格式：有空格换行
	        format.setEncoding("UTF-8");
	        //创建写出对象
	        XMLWriter writer=new XMLWriter(out,format);
	        //写出Document对象
	        writer.write(doc);
	        //关闭流
	        writer.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static List<String> loadEntryUrls(){
		
		List<String> urlArray = new ArrayList<String>();
		
		//读入xml文件中的用户信息
		SAXReader sr = new SAXReader();
		try{
			Document doc = sr.read(new File(rootPath + "/src/main/resources/entry-config.xml"));
			Element root = doc.getRootElement();
			
			//System.out.println(root.getText());
			//查找所有url结点
			List urls = root.selectNodes("//url");
			for (Iterator it = urls.iterator(); it.hasNext();) {
				String url = ((Element)it.next()).getTextTrim();
				System.out.println(url);
				urlArray.add(url);
			}
			
			}catch(DocumentException e){
					e.printStackTrace();
			}
		
		return urlArray;
	}
}
