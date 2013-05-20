package com.carestream.demo.notification;


import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Consumer {
 
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
        MessageConsumer consumer = session.createConsumer(destination);

        consumer.getMessageSelector();

        System.out.println("start receive");
        Message message = consumer.receive();

        System.out.println("finish receive");

        if(message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage)message;
            System.out.println("Received message '"+textMessage.getText() + "'");
        }
        connection.close();
    }

}
