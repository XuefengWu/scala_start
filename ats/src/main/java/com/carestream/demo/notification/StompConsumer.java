package com.carestream.demo.notification;

import pk.aamir.stompj.Connection;
import pk.aamir.stompj.Message;
import pk.aamir.stompj.MessageHandler;
import pk.aamir.stompj.StompJException;

public class StompConsumer
{
    public static void main(String... args)
    {
        try {
            Connection con = new Connection("10.122.160.198", 61613);
            con.connect();
           
            con.subscribe("/topic/topic.patientCreated",true);
            con.addMessageHandler("/topic/topic.patientCreated", new MessageHandler(){

                @Override
                public void onMessage(Message msg) {
                    System.out.println(msg.getContentAsString());
                }
                
            });
            Thread.sleep(400000);
            con.disconnect();

        } catch (StompJException e1) {
            // Even with this try-catch, the disconnect call will print the stack trace
           e1.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
}
 
