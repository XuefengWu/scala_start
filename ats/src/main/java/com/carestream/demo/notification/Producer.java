package com.carestream.demo.notification;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
public class Producer {
 
    //private static String url = "stomp://10.112.37.57:61613";
    private static String url = "tcp://localhost:61616";
    private static String subject = "FOO.BAR.2";
    public static void main(String[] args) throws JMSException {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        Connection connection = connectionFactory.createConnection();
        connection.start();
        
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        //Destination destination = session.createQueue(subject);
        Destination destination = session.createTopic(subject);

        MessageProducer producer = session.createProducer(destination);

        TextMessage message = session.createTextMessage("Hello Consumer!");
        message.setJMSType("car");
        producer.send(message);
        
        System.out.println("Sent message '" + message.getText() + "'");
        connection.close();
    }

}
