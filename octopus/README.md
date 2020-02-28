# 八爪鱼

八爪鱼 (Octopus), 数据采集.

### 构想
 
数据来源于多种终端多种类型多种方式, 如:
 
 * 网站
   - [x] [Web Magic](http://webmagic.io/)
 
 * 日志
   - [ ] [Apache Flume](https://flume.apache.org/)
   - [ ] [Fluentd](http://docs.fluentd.org/articles/quickstart)
   - [ ] [Logstash](https://github.com/elastic/logstash)
   - [ ] [Splunk Forwarder](http://www.splunk.com/)
 
 * 数据库
   - [ ] [Apache Sqoop](https://sqoop.apache.org/)
  
### 架构组成

 * Sprint Boot (2.2.4.RELEASE)
 
### Document Link
 
 * [Apache Flume](https://flume.apache.org/)
 
   > Flume 是Apache旗下的一款开源、高可靠、高扩展、容易管理、支持客户扩展的数据采集系统。 Flume使用JRuby来构建，所以依赖Java运行环境。  
   > Flume最初是由Cloudera的工程师设计用于合并日志数据的系统，后来逐渐发展用于处理流数据事件。
  
 * [Logstash](https://github.com/elastic/logstash)
 
   > Logstash是著名的开源数据栈ELK (ElasticSearch, Logstash, Kibana)中的那个L。

 * [Splunk Forwarder](http://www.splunk.com/)
   > 在商业化的大数据平台产品中，Splunk提供完整的数据采金，数据存储，数据分析和处理，以及数据展现的能力。

 * [Apache Sqoop](https://sqoop.apache.org/)
   > SQL-to-Hadoop,用于在关系型数据库（RDBMS）和HDFS之间互相传输数据。