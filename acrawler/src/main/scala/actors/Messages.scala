package actors

sealed trait CrawlerMessage
case class Url(url:String) extends CrawlerMessage
case class Loaded(url:String) extends CrawlerMessage
case class Data(url:String,bytes: Array[Byte]) extends CrawlerMessage 
case class Contents(url:String,contents:String) extends CrawlerMessage 
case class Urls(links:Seq[String]) extends CrawlerMessage 