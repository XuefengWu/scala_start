package util

import org.apache.activemq.ActiveMQConnectionFactory
import javax.jms._
import conf.Conf

/**
 * Created by IntelliJ IDEA.
 * User: 19002850
 * Date: 12-3-29
 * Time: 下午4:21
 * To change this template use File | Settings | File Templates.
 */

object  ActiveMQ {
  def receive(subject:String): String = {
     val msg = receiveAll(subject,1).head
     println(subject+"="+msg)
     msg
  }
  
  def receiveAll(subject:String,num:Int): Seq[String] = {
    val connectionFactory: ConnectionFactory = new ActiveMQConnectionFactory(Conf.notifyUrl)
    val connection: Connection = connectionFactory.createConnection
    connection.start
    val session: Session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
    val destination: Destination = session.createTopic(subject)
    val consumer: MessageConsumer = session.createConsumer(destination)
    val msgs = (1 to num).map{ i =>
      val message: Message = consumer.receive
        val msg:String = message match {
          case t:TextMessage => t.getText
          case _ => ""
        }
      msg
    }
    println("receive message completed.")
    connection.close
    println("close message connection")
    msgs
  }
    
}
