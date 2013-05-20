package controllers

import java.util.Properties
import javax.mail.internet.MimeMessage
import java.sql.Date
import javax.mail.internet.InternetAddress
import javax.mail.Message
import specs2.html
import javax.activation.FileDataSource
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMultipart
import javax.activation.DataHandler

object MailApp extends App {

  println("start send")

  val smtpHost = "cs-mta.carestreamhealth.com"
  val props = new Properties()
  props.setProperty("mail.transport.protocol", "smtp")
  props.setProperty("mail.host", smtpHost)
  val mailSession = javax.mail.Session.getDefaultInstance(props, null)

  val message = new MimeMessage(mailSession)

  val subject = new java.lang.StringBuffer()
  subject.append("UT Report: " + new Date(System.currentTimeMillis()));

  message.setSubject(subject.toString());

  message.setFrom(new InternetAddress("xuefeng.wu@carestream.com"))

  message.addRecipient(Message.RecipientType.TO, new InternetAddress("xuefeng.wu@carestream.com"))

  val multipart = new MimeMultipart("related")
  val messageBodyPart = new MimeBodyPart()

  val report = "helllooo"
  messageBodyPart.setContent(report.toString, "text/html")
  multipart.addBodyPart(messageBodyPart)

  //image part
  val messageBodyPart2 = new MimeBodyPart()
  val fds2 = new FileDataSource("public/images/reports/testcases.png")
  messageBodyPart2.setFileName("testcases.png")
  messageBodyPart2.setDataHandler(new DataHandler(fds2))
  multipart.addBodyPart(messageBodyPart2)

  val messageBodyPart3 = new MimeBodyPart()
  val fds3 = new FileDataSource("public/images/reports/test_fails.png")
  messageBodyPart3.setDataHandler(new DataHandler(fds3))
  //messageBodyPart3.setHeader("Content-ID", "<test_fails>")
  //messageBodyPart3.setDescription("test_fails")
  messageBodyPart3.setFileName("test_fails.png")
  //messageBodyPart3.setText("test_fails.text")
  multipart.addBodyPart(messageBodyPart3)

  // put everything together
  message.setContent(multipart)

  val transport = mailSession.getTransport()
  transport.connect()
  transport.sendMessage(message,
    message.getRecipients(Message.RecipientType.TO))
  transport.close()

  println("sended finished.")

}
