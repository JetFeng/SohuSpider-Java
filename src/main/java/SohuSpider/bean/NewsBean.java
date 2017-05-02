package main.java.SohuSpider.bean;

public class NewsBean {
	String url; //新闻url
	
	String category; //新闻类别
	
	String sourceFrom; //新闻源
	
	String title; //新闻标题
	
	String content; //新闻内容
	
	String date; //发布时间
	
	String editor; //新闻作者
	
	public String getUrl(){
		return url;
	}
	
	public String getCategory(){
		return category;
	}
	
	public String getSourceFrom(){
		return sourceFrom;
	}
	
	public String getTitle(){
		return title;
	}
	
	public String getContent(){
		return content;
	}
	
	public String getDate(){
		return date;
	}
	
	public String getEditor(){
		return editor;
	}
	
	public void setUrl(String url){
		this.url = url;
	}
	
	public void setCategory(String category){
		this.category = category;
	}
	
	public void setSourceFrom(String sourceFrom){
		this.sourceFrom = sourceFrom;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	
	public void setContent(String content){
		this.content = content;
	}
	
	public void setDate(String date){
		this.date = date;
	}
	
	public void setEditor(String editor){
		this.editor = editor;
	}
	
	@Override
	public String toString(){
		return "NewsBean:{ \n" +
	           "           title:" + title + "\n" +
	           "           url:" + url + "\n" +
	           "           date:" + date + "\n" +
	           "           category:" + category + "\n" +
	           "           sourceFrom:" + sourceFrom + "\n" +
	           "           editor:" + editor + "\n" +
	           "           content:" + content + "\n" +
	           "        }"
			;	
	}
	
	
}
