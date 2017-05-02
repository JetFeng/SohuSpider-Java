搜狐新闻爬虫(Java版)
=================

2017.5.2
------------

采用知乎上某位大牛的框架进行改写<br>
没有使用任何其他框架<br>
可以实现海量数据新闻去重，多线程<br>
序列化url队列，暂停之后依然可以去重<br>
本地测试已爬取40w+新闻<br>

工程中的中的一些结构说明:<br>
    SohuSpider<br>
	    --main.java  主程序入口函数<br>
	SohuSpider.count  数据库条目数量查询，单独main函数<br>
	SohuSpider.filter  bloomFilter算法实现<br>
	SohuSpider.miniSpider ip代理采集爬虫<br>
	SohuSpider.service 爬虫主体部分<br>
	SohuSpider.util   一些json解析，请求网页等工具类<br>
