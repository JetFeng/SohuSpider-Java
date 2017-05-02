# 搜狐新闻爬虫(Java版)

2017.5.2

采用知乎上某位大牛的框架进行改写
没有使用任何其他框架
可以实现海量数据新闻去重，多线程
序列化url队列，暂停之后依然可以去重
本地测试已爬取40w+新闻

工程中的中的一些结构说明:
    SohuSpider
	    --main.java  主程序入口函数
	SohuSpider.count  数据库条目数量查询，单独main函数
	SohuSpider.filter  bloomFilter算法实现
	SohuSpider.miniSpider ip代理采集爬虫
	SohuSpider.service 爬虫主体部分
	SohuSpider.util   一些json解析，请求网页等工具类
