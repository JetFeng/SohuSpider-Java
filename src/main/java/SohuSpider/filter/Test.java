package main.java.SohuSpider.filter;

public class Test {
	private BloomFilter filter = new BloomFilter();
	
	private String[] URLs = {
			"www.baidu.com",
			"www.sohu.com",
			"www.sina.com",
			"www.google.com",
			"www.facebook.com",
			"www.wangyi.com",
			"www.sina.com",
			"www.163.com",
			"www.baidu.com"
	};
	
	public void testBloomFilter(){
		for(String url : URLs){
			if(filter.contains(url)){
				System.err.println('"' + url + '"' + " already exists in bits!");
			}
			else{
				filter.add(url);
				System.out.println("add " + '"' + url + '"' + " into bits. ");
			}
		}
	}
	
	public static void main(String[] args){
		Test test = new Test();
		test.testBloomFilter();
	}
}
