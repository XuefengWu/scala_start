package com.carestream.demo.notification;

import pk.aamir.stompj.Connection;
import pk.aamir.stompj.StompJException;

public class StompProducer
{
    public static void main(String... args)
    {
        try {
            Connection con = new Connection("localhost", 61613);
            con.connect();
            for (int i = 0; i < 10; i++)
                con.send("Test Message " + i, "/topic/hellos");
            con.disconnect();

        } catch (StompJException e1) {
            // Even with this try-catch, the disconnect call will print the stack trace
           e1.printStackTrace();
        }
        
    }
}