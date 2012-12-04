package actors

import java.io.InputStream
import akka.actor.Actor

sealed trait CrawlerMessage
case class Url(url: String) extends CrawlerMessage
case class Loaded(url: String) extends CrawlerMessage
case class Data(url: String, in: InputStream) extends CrawlerMessage
case class Contents(url: String, contents: String) extends CrawlerMessage
case class Urls(links: Seq[String]) extends CrawlerMessage
case class Restart extends CrawlerMessage

case class LogStart(actor: Actor) extends CrawlerMessage
case class LogFinished(obj: Object, spend: Long) extends CrawlerMessage