package com.carestream.demo.notification;

import org.apache.activemq.broker.BrokerService;

public class AmqBrokerServer {

    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {

        BrokerService broker= new BrokerService();
        broker.addConnector("tcp://localhost:61616");
        broker.addConnector("stomp://0.0.0.0:61613");
        
        broker.start();
        broker.waitUntilStarted();
        System.out.println("broker started");
        
        Thread.sleep(400000);
    }

}
