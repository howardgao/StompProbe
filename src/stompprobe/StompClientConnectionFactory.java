package org.hornetq.test.stomp.client;

import java.io.IOException;

public class StompClientConnectionFactory
{
   //create a raw connection to the host.
   public static StompClientConnection createClientConnection(String version, String host, int port) throws IOException
   {
      if ("1.0".equals(version))
      {
         return new StompClientConnectionV10(host, port);
      }
      if ("1.1".equals(version))
      {
         return new StompClientConnectionV11(host, port);
      }
      return null;
   }

   public static void main(String[] args) throws Exception
   {
      StompClientConnection connV11 = StompClientConnectionFactory.createClientConnection("1.1", "127.0.0.1", 61613);
      ClientStompFrame frame = connV11.createFrame("CONNECT");
      frame.addHeader("host", "127.0.0.1");
      frame.addHeader("login", "guest");
      frame.addHeader("passcode", "guest");
      frame.addHeader("heart-beat", "4000,0");
      frame.addHeader("accept-version", "1.0,1.1");
      
      ClientStompFrame reply = connV11.sendFrame(frame);
            
      System.out.println("========== start pinger!");
      
      connV11.startPinger(4000);
      
      Thread.sleep(10000);
      
      //now check the frame size
      int size = connV11.getFrameQueueSize();
      
      System.out.println("ping received: " + size);
      
      connV11.disconnect();
   }
}
