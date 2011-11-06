package stompprobe;

import java.util.StringTokenizer;

/**
 * 1.0 frames
 * 
 * 1. CONNECT
 * 2. CONNECTED
 * 3. SEND
 * 4. SUBSCRIBE
 * 5. UNSUBSCRIBE
 * 6. BEGIN
 * 7. COMMIT
 * 8. ACK
 * 9. ABORT
 * 10. DISCONNECT
 * 11. MESSAGE
 * 12. RECEIPT
 * 13. ERROR
 */
public class StompFrameFactoryV10 implements StompFrameFactory
{

   public ClientStompFrame createFrame(String data)
   {
      //split the string at "\n\n"
      String[] dataFields = data.split("\n\n");
      
      StringTokenizer tokenizer = new StringTokenizer(dataFields[0], "\n");
      
      String command = tokenizer.nextToken();
      ClientStompFrame frame = new ClientStompFrameV10(command);
      
      while (tokenizer.hasMoreTokens())
      {
         String header = tokenizer.nextToken();
         String[] fields = header.split(":");
         frame.addHeader(fields[0], fields[1]);
      }
      
      //body (without null byte)
      if (dataFields.length == 2)
      {
         frame.setBody(dataFields[1]);      
      }
      return frame;
   }

   @Override
   public ClientStompFrame newFrame(String command)
   {
      return new ClientStompFrameV10(command);
   }

}
